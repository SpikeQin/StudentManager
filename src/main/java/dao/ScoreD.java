package dao;

import util.DBUtil;
import vo.Score;
import java.sql.*;
import java.util.ArrayList;

/**
 * 成绩数据访问层 — 操作 score 表
 *
 * <p>核心职责：
 * <ul>
 *   <li>成绩增删改查 + 分页</li>
 *   <li><strong>联表查询</strong>（LEFT JOIN student）一次性获取姓名和专业，消除 N+1 问题</li>
 *   <li>内部类 {@link ScoreWithStudent} 承载联表结果</li>
 * </ul>
 * 全部使用 PreparedStatement 参数化查询，防 SQL 注入。
 */
public class ScoreD {

    /**
     * 为新生创建成绩记录（初始成绩为空）
     * @param id 学号
     * @return true 表示插入成功
     */
    public boolean insertScore(String id) throws Exception {
        String sql = "insert into score(id) values(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * 删除学生成绩
     * @param id 学号
     * @return true 表示删除成功
     */
    public boolean deleteScore(String id) throws Exception {
        String sql = "delete from score where id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * 更新学生四科成绩
     * @param id                   学号
     * @param dataStructure        数据结构
     * @param operatingSystem      操作系统
     * @param computerNetwork      计算机网络
     * @param computerOrganization 计算机组成原理
     */
    public void updateScoreInfo(String id, String dataStructure, String operatingSystem,
                                 String computerNetwork, String computerOrganization) throws Exception {
        String sql = "update score set data_structure=?, operating_system=?, computer_network=?, computer_organization=? where id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dataStructure);
            ps.setString(2, operatingSystem);
            ps.setString(3, computerNetwork);
            ps.setString(4, computerOrganization);
            ps.setString(5, id);
            ps.executeUpdate();
        }
    }

    /**
     * 按学号查询成绩（纯成绩表）
     * @param id 学号
     * @return Score 对象，未找到返回 null
     */
    public Score findWithId(String id) throws Exception {
        String sql = "select * from score where id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return getScore(rs);
            }
        }
    }

    /**
     * 分页查询成绩（联表），含学生姓名和专业
     * @param page 页码（从 1 开始）
     * @param size 每页条数
     * @return 联表结果列表
     */
    public ArrayList<ScoreWithStudent> getOnePageWithStudent(int page, int size) throws Exception {
        ArrayList<ScoreWithStudent> al = new ArrayList<>();
        String sql = "SELECT s.id, s.data_structure, s.operating_system, s.computer_network, s.computer_organization, " +
                     "st.name AS student_name, st.major AS student_major " +
                     "FROM score s LEFT JOIN student st ON s.id = st.id LIMIT ?, ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * size);
            ps.setInt(2, size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) al.add(buildScoreWithStudent(rs));
            }
        }
        return al;
    }

    /**
     * 查询全部成绩（联表），用于 Excel 导出
     * @return 全部联表结果列表
     */
    public ArrayList<ScoreWithStudent> getAllWithStudent() throws Exception {
        ArrayList<ScoreWithStudent> al = new ArrayList<>();
        String sql = "SELECT s.id, s.data_structure, s.operating_system, s.computer_network, s.computer_organization, " +
                     "st.name AS student_name, st.major AS student_major " +
                     "FROM score s LEFT JOIN student st ON s.id = st.id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) al.add(buildScoreWithStudent(rs));
        }
        return al;
    }

    /**
     * 按学号查询成绩（联表），返回单条结果
     * @param id 学号
     * @return 联表结果对象，未找到返回 null
     */
    public ScoreWithStudent findWithIdWithStudent(String id) throws Exception {
        String sql = "SELECT s.id, s.data_structure, s.operating_system, s.computer_network, s.computer_organization, " +
                     "st.name AS student_name, st.major AS student_major " +
                     "FROM score s LEFT JOIN student st ON s.id = st.id WHERE s.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return buildScoreWithStudent(rs);
            }
        }
        return null;
    }

    /**
     * 分页查询成绩（仅成绩表，不联表）
     * @param page 页码
     * @param size 每页条数
     * @return 成绩列表
     */
    public ArrayList<Score> getOnePage(int page, int size) throws Exception {
        ArrayList<Score> al = new ArrayList<>();
        String sql = "SELECT * FROM score limit ?, ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * size);
            ps.setInt(2, size);
            try (ResultSet rs = ps.executeQuery()) {
                getMoreScore(al, rs);
            }
        }
        return al;
    }

    /**
     * 统计成绩记录总数
     * @return 记录数
     */
    public int getScoreCount() throws Exception {
        String sql = "select count(*) from score";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /* ---- 私有辅助方法 ---- */

    /** 从 ResultSet 构建单个 Score 对象 */
    private Score getScore(ResultSet rs) throws SQLException {
        Score stu = null;
        if (rs.next()) {
            stu = new Score();
            stu.setId(rs.getString("id"));
            stu.setDataStructure(rs.getString("data_structure"));
            stu.setOperatingSystem(rs.getString("operating_system"));
            stu.setComputerNetwork(rs.getString("computer_network"));
            stu.setComputerOrganization(rs.getString("computer_organization"));
        }
        return stu;
    }

    /** 从 ResultSet 批量构建 Score 并添加到列表 */
    private void getMoreScore(ArrayList<Score> al, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Score score = new Score();
            score.setId(rs.getString("id"));
            score.setDataStructure(rs.getString("data_structure"));
            score.setOperatingSystem(rs.getString("operating_system"));
            score.setComputerNetwork(rs.getString("computer_network"));
            score.setComputerOrganization(rs.getString("computer_organization"));
            al.add(score);
        }
    }

    /** 从 ResultSet 构建联表结果 */
    private ScoreWithStudent buildScoreWithStudent(ResultSet rs) throws SQLException {
        ScoreWithStudent sws = new ScoreWithStudent();
        sws.setId(rs.getString("id"));
        sws.setDataStructure(rs.getString("data_structure"));
        sws.setOperatingSystem(rs.getString("operating_system"));
        sws.setComputerNetwork(rs.getString("computer_network"));
        sws.setComputerOrganization(rs.getString("computer_organization"));
        sws.setStudentName(rs.getString("student_name"));
        sws.setStudentMajor(rs.getString("student_major"));
        return sws;
    }

    /* ---- 内部类 ---- */

    /**
     * 成绩联表视图 — 在 Score 基础上附加学生姓名、专业
     *
     * <p>由 LEFT JOIN 联表查询构建，调用方可通过 {@code getStudentName()} /
     * {@code getStudentMajor()} 直接获取学生信息，无需二次查询。
     */
    public static class ScoreWithStudent extends Score {
        private String studentName;
        private String studentMajor;

        public String getStudentName()     { return studentName; }
        public void setStudentName(String n) { this.studentName = n; }
        public String getStudentMajor()     { return studentMajor; }
        public void setStudentMajor(String m) { this.studentMajor = m; }
    }
}
