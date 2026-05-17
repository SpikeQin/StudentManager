package dao;

import util.DBUtil;
import vo.OperationLog;
import java.sql.*;
import java.util.ArrayList;

/**
 * 操作日志数据访问层 — 操作 operation_log 表
 *
 * <p>职责：日志的写入与分页查询（可按用户类型筛选）。
 * <br>全部使用 PreparedStatement 参数化查询，防 SQL 注入。
 */
public class LogD {

    /**
     * 插入一条操作日志
     * @param userId   操作人 ID
     * @param userType admin | teacher | student
     * @param action   操作名称，如「添加学生」
     * @param detail   操作详情
     */
    public void insertLog(String userId, String userType, String action, String detail) throws Exception {
        String sql = "INSERT INTO operation_log(user_id, user_type, action, detail) VALUES(?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, userType);
            ps.setString(3, action);
            ps.setString(4, detail);
            ps.executeUpdate();
        }
    }

    /**
     * 分页查询日志，按时间倒序
     * @param page 页码（从 1 开始）
     * @param size 每页条数
     * @return 日志列表
     */
    public ArrayList<OperationLog> getOnePage(int page, int size) throws Exception {
        ArrayList<OperationLog> list = new ArrayList<>();
        String sql = "SELECT * FROM operation_log ORDER BY created_at DESC LIMIT ?,?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * size);
            ps.setInt(2, size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(buildLog(rs));
            }
        }
        return list;
    }

    /**
     * 统计日志总数
     * @return 总记录数
     */
    public int getLogCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM operation_log";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /**
     * 按用户类型筛选日志（分页）
     * @param userType admin | teacher | student
     * @param page     页码
     * @param size     每页条数
     * @return 筛选后的日志列表
     */
    public ArrayList<OperationLog> getLogsByType(String userType, int page, int size) throws Exception {
        ArrayList<OperationLog> list = new ArrayList<>();
        String sql = "SELECT * FROM operation_log WHERE user_type=? ORDER BY created_at DESC LIMIT ?,?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userType);
            ps.setInt(2, (page - 1) * size);
            ps.setInt(3, size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(buildLog(rs));
            }
        }
        return list;
    }

    /** 从 ResultSet 构建 OperationLog 对象 */
    private OperationLog buildLog(ResultSet rs) throws SQLException {
        OperationLog log = new OperationLog();
        log.setId(rs.getInt("id"));
        log.setUserId(rs.getString("user_id"));
        log.setUserType(rs.getString("user_type"));
        log.setAction(rs.getString("action"));
        log.setDetail(rs.getString("detail"));
        log.setCreatedAt(rs.getString("created_at"));
        return log;
    }
}
