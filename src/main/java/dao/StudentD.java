package dao;

import util.DBUtil;
import util.PasswordUtil;
import vo.Student;
import java.sql.*;
import java.util.ArrayList;

/**
 * 学生数据访问层 — 操作 student 表
 *
 * <p>职责：登录验证（含自动密码升级）、学生增删改查、分页、模糊搜索。
 * <br>全部使用 PreparedStatement 参数化查询，防 SQL 注入。
 */
public class StudentD {

    /**
     * 验证学生登录。
     * <br>密码匹配成功后，若为旧版明文密码则自动升级为 PBKDF2 哈希。
     *
     * @param user     学号
     * @param password 明文密码
     * @return 验证通过返回 Student 对象，失败返回 null
     */
    public Student checkAccount(String user, String password) throws Exception {
        String sql = "select * from student where id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                Student stu = getStudent(rs);
                if (stu != null && PasswordUtil.verifyPassword(password, stu.getPassword())) {
                    // 旧版明文密码 → 安全哈希
                    if (PasswordUtil.needsRehash(stu.getPassword())) {
                        updateStudentPasswordInternal(conn, user, PasswordUtil.hashPassword(password));
                        stu.setPassword(PasswordUtil.hashPassword(password));
                    }
                    return stu;
                }
                return null;
            }
        }
    }

    /**
     * 按学号精确查找
     * @param id 学号
     * @return Student 对象，未找到返回 null
     */
    public Student findWithId(String id) throws Exception {
        String sql = "select * from student where id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return getStudent(rs);
            }
        }
    }

    /**
     * 按姓名模糊搜索
     * @param name 姓名关键词
     * @return 匹配的学生列表
     */
    public ArrayList<Student> findWithName(String name) throws Exception {
        ArrayList<Student> al = new ArrayList<>();
        String sql = "select * from student where name like ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                getMoreStudent(al, rs);
            }
        }
        return al;
    }

    /**
     * 添加学生（密码自动哈希）
     * @return true 表示添加成功
     */
    public boolean insertStudent(String id, String password, String name, String sex,
                                  String school_date, String major, String email) throws Exception {
        String sql = "insert into student(id, password, name, sex, school_date, major, email) values(?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, PasswordUtil.hashPassword(password));
            ps.setString(3, name);
            ps.setString(4, sex);
            ps.setString(5, school_date);
            ps.setString(6, major);
            ps.setString(7, email);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * 删除学生
     * @param id 学号
     * @return true 表示删除成功
     */
    public boolean deleteStudent(String id) throws Exception {
        String sql = "delete from student where id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * 分页查询学生列表（不含密码字段）
     * @param page 页码（从 1 开始）
     * @param size 每页条数
     * @return 学生列表
     */
    public ArrayList<Student> getOnePage(int page, int size) throws Exception {
        ArrayList<Student> al = new ArrayList<>();
        String sql = "SELECT * FROM student limit ?, ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * size);
            ps.setInt(2, size);
            try (ResultSet rs = ps.executeQuery()) {
                getMoreStudent(al, rs);
            }
        }
        return al;
    }

    /**
     * 统计学生总数
     * @return 记录数
     */
    public int getStudentCount() throws Exception {
        String sql = "select count(*) from student";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /**
     * 获取全部学生（谨慎用于大数据量场景）
     * @return 全部学生列表
     */
    public ArrayList<Student> getAllStudents() throws Exception {
        ArrayList<Student> al = new ArrayList<>();
        String sql = "SELECT * FROM student";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            getMoreStudent(al, rs);
        }
        return al;
    }

    /**
     * 更新学生基本信息（姓名、性别、专业）
     */
    public void updateStudentInfo(String id, String name, String sex, String major) throws Exception {
        String sql = "update student set name=?, sex=?, major=? where id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, sex);
            ps.setString(3, major);
            ps.setString(4, id);
            ps.executeUpdate();
        }
    }

    /**
     * 更新密码（自动哈希）
     */
    public void updateStudentPassword(String id, String password) throws Exception {
        String sql = "update student set password=? where id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hashPassword(password));
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    /**
     * 更新邮箱
     */
    public void updateStudentEmail(String id, String email) throws Exception {
        String sql = "update student set email=? where id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    /** @see #updateStudentEmail(String, String) */
    public void updateEmail(String id, String email) throws Exception {
        updateStudentEmail(id, email);
    }

    /** @see #updateStudentPassword(String, String) */
    public void updatePassword(String id, String password) throws Exception {
        updateStudentPassword(id, password);
    }

    /** 内部方法：在已有连接中更新密码（用于自动升级旧密码时复用外部连接） */
    private void updateStudentPasswordInternal(Connection conn, String id, String hashedPassword) throws SQLException {
        String sql = "update student set password=? where id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    /** 从 ResultSet 构建 Student（含密码，仅用于登录验证） */
    private Student getStudent(ResultSet rs) throws SQLException {
        Student stu = null;
        if (rs.next()) {
            stu = new Student();
            stu.setId(rs.getString("id"));
            stu.setPassword(rs.getString("password"));
            stu.setName(rs.getString("name"));
            stu.setSex(rs.getString("sex"));
            stu.setSchool_date(rs.getString("school_date"));
            stu.setMajor(rs.getString("major"));
            stu.setEmail(rs.getString("email"));
        }
        return stu;
    }

    /** 从 ResultSet 批量构建 Student 列表（不含密码，减少敏感数据暴露） */
    private void getMoreStudent(ArrayList<Student> al, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Student stu = new Student();
            stu.setId(rs.getString("id"));
            stu.setName(rs.getString("name"));
            stu.setSex(rs.getString("sex"));
            stu.setSchool_date(rs.getString("school_date"));
            stu.setMajor(rs.getString("major"));
            stu.setEmail(rs.getString("email"));
            al.add(stu);
        }
    }
}
