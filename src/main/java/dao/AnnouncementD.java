package dao;

import util.DBUtil;
import vo.Announcement;
import java.sql.*;
import java.util.ArrayList;

/**
 * 公告数据访问层 — 操作 announcement 表
 *
 * <p>职责：公告的增删查（分页 / 全量）
 * <br>全部使用 PreparedStatement 参数化查询，防 SQL 注入。
 */
public class AnnouncementD {

    /**
     * 发布公告
     * @param title     标题
     * @param content   正文
     * @param publisher 发布人姓名
     * @return true 表示插入成功
     */
    public boolean insertAnnouncement(String title, String content, String publisher) throws Exception {
        String sql = "INSERT INTO announcement(title, content, publisher) VALUES(?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, publisher);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * 分页查询公告，按发布时间倒序
     * @param page 页码（从 1 开始）
     * @param size 每页条数
     * @return 公告列表
     */
    public ArrayList<Announcement> getOnePage(int page, int size) throws Exception {
        ArrayList<Announcement> list = new ArrayList<>();
        String sql = "SELECT * FROM announcement ORDER BY created_at DESC LIMIT ?,?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * size);
            ps.setInt(2, size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(buildAnnouncement(rs));
            }
        }
        return list;
    }

    /**
     * 查询全部公告，按发布时间倒序
     * @return 所有公告列表
     */
    public ArrayList<Announcement> getAll() throws Exception {
        ArrayList<Announcement> list = new ArrayList<>();
        String sql = "SELECT * FROM announcement ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(buildAnnouncement(rs));
        }
        return list;
    }

    /**
     * 统计公告总数
     * @return 总记录数
     */
    public int getCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM announcement";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /**
     * 按 ID 删除公告
     * @param id 公告 ID
     * @return true 表示删除成功
     */
    public boolean deleteAnnouncement(int id) throws Exception {
        String sql = "DELETE FROM announcement WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    /** 从 ResultSet 构建 Announcement 对象 */
    private Announcement buildAnnouncement(ResultSet rs) throws SQLException {
        Announcement a = new Announcement();
        a.setId(rs.getInt("id"));
        a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content"));
        a.setPublisher(rs.getString("publisher"));
        a.setCreatedAt(rs.getString("created_at"));
        return a;
    }
}
