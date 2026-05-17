package vo;

/**
 * 公告实体 — 映射数据库 announcement 表
 *
 * <p>字段说明：
 * <ul>
 *   <li>id        — 公告 ID（自增主键）
 *   <li>title     — 标题
 *   <li>content   — 正文
 *   <li>publisher — 发布人姓名
 *   <li>createdAt — 发布时间（数据库自动填充）
 * </ul>
 */
public class Announcement {

    private int id;
    private String title;
    private String content;
    private String publisher;
    private String createdAt;

    /* ---- Getter / Setter ---- */

    public int getId()             { return id; }
    public void setId(int id)      { this.id = id; }

    public String getTitle()       { return title; }
    public void setTitle(String t) { this.title = t; }

    public String getContent()       { return content; }
    public void setContent(String c) { this.content = c; }

    public String getPublisher()       { return publisher; }
    public void setPublisher(String p) { this.publisher = p; }

    public String getCreatedAt()        { return createdAt; }
    public void setCreatedAt(String ca) { this.createdAt = ca; }
}
