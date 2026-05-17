package dao;

import util.DBUtil;
import util.PasswordUtil;
import vo.Teacher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 教师数据访问层 — 操作 teacher 表
 *
 * <p>职责：登录验证（含自动密码升级）、教师增删改查、角色管理。
 * <br>全部使用 PreparedStatement 参数化查询，防 SQL 注入。
 * <br>列表查询不返回密码字段，减少敏感数据暴露。
 */
public class TeacherD {
    private static final Logger LOG = Logger.getLogger(TeacherD.class.getName());

    /**
     * 验证教师登录。
     * <br>密码匹配成功后，若为旧版明文密码则自动升级为 PBKDF2 哈希。
     *
     * @param id       教师号
     * @param password 明文密码
     * @return 验证通过返回 Teacher 对象，失败返回 null
     */
    public Teacher checkAccount(String id, String password) throws Exception {
        String sql = "select * from teacher where id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                Teacher tea = getTeacher(rs);
                if (tea != null && PasswordUtil.verifyPassword(password, tea.getPassword())) {
                    if (PasswordUtil.needsRehash(tea.getPassword())) {
                        updateTeacherPasswordInternal(conn, id, PasswordUtil.hashPassword(password));
                        tea.setPassword(PasswordUtil.hashPassword(password));
                    }
                    return tea;
                }
                return null;
            }
        }
    }

    /**
     * 按教师号精确查找
     * @param id 教师号
     * @return Teacher 对象，未找到返回 null
     */
    public Teacher findWithId(String id) throws Exception {
        String sql = "select * from teacher where id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return getTeacher(rs);
            }
        }
    }

    /**
     * 添加教师（密码自动哈希，邮箱为空时默认设为 {id}@example.com）
     * @return 新增的 Teacher 对象
     */
    public Teacher insertTeacher(String id, String password, String name, String sex, String email) throws Exception {
        String sql = "insert into teacher(id, password, name, sex, email) values(?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, PasswordUtil.hashPassword(password));
            ps.setString(3, name != null ? name : "");
            ps.setString(4, sex != null ? sex : "");
            ps.setString(5, email != null ? email : id + "@example.com");
            ps.executeUpdate();
        }
        return findWithId(id);
    }

    /**
     * 更新教师信息。密码为空时不修改密码字段。
     * @return 更新后的 Teacher 对象
     */
    public Teacher updateTeacher(String id, String name, String sex, String email, String password) throws Exception {
        if (password != null && !password.trim().isEmpty()) {
            String sql = "update teacher set name=?, sex=?, email=?, password=? where id=?";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, sex);
                ps.setString(3, email);
                ps.setString(4, PasswordUtil.hashPassword(password));
                ps.setString(5, id);
                ps.executeUpdate();
            }
        } else {
            String sql = "update teacher set name=?, sex=?, email=? where id=?";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, sex);
                ps.setString(3, email);
                ps.setString(4, id);
                ps.executeUpdate();
            }
        }
        return findWithId(id);
    }

    /**
     * 更新教师密码（自动哈希）
     */
    public void updateTeacherPassword(String id, String password) throws Exception {
        String sql = "update teacher set password=? where id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hashPassword(password));
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    /**
     * 更新教师邮箱
     */
    public void updateTeacherEmail(String id, String email) throws Exception {
        String sql = "update teacher set email=? where id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    /** @see #updateTeacherEmail(String, String) */
    public void updateEmail(String id, String email) throws Exception {
        updateTeacherEmail(id, email);
    }

    /** @see #updateTeacherPassword(String, String) */
    public void updatePassword(String id, String password) throws Exception {
        updateTeacherPassword(id, password);
    }

    /**
     * 删除教师
     */
    public void deleteTeacher(String id) throws Exception {
        String sql = "delete from teacher where id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * 查询全部教师（不含密码），按教师号排序
     * @return 教师列表
     */
    public List<Teacher> findAll() throws Exception {
        String sql = "select id, name, sex, email, role from teacher order by id";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Teacher tea = new Teacher();
                tea.setId(rs.getString("id"));
                tea.setName(rs.getString("name"));
                tea.setSex(rs.getString("sex"));
                tea.setEmail(rs.getString("email"));
                try {
                    String role = rs.getString("role");
                    if (role != null) tea.setRole(role);
                } catch (SQLException e) {
                    LOG.log(Level.FINE, "role column not available", e);
                }
                teachers.add(tea);
            }
        }
        return teachers;
    }

    /** 内部方法：在已有连接中更新密码（用于自动升级旧密码时复用外部连接） */
    private void updateTeacherPasswordInternal(Connection conn, String id, String hashedPassword) throws SQLException {
        String sql = "update teacher set password=? where id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    /** 从 ResultSet 构建 Teacher（含密码，仅用于登录验证） */
    private Teacher getTeacher(ResultSet rs) throws SQLException {
        Teacher tea = null;
        if (rs.next()) {
            tea = new Teacher();
            tea.setId(rs.getString("id"));
            tea.setPassword(rs.getString("password"));
            tea.setName(rs.getString("name"));
            tea.setEmail(rs.getString("email"));
            tea.setSex(rs.getString("sex"));
            try {
                String role = rs.getString("role");
                if (role != null) tea.setRole(role);
            } catch (SQLException e) {
                LOG.log(Level.FINE, "role column not available", e);
            }
        }
        return tea;
    }
}
