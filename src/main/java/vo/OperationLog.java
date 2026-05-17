package vo;

/**
 * 操作日志实体 — 映射数据库 operation_log 表
 *
 * <p>记录所有用户的关键操作，用于审计追踪。
 *
 * <p>字段说明：
 * <ul>
 *   <li>id        — 日志 ID（自增主键）
 *   <li>userId    — 操作人 ID
 *   <li>userType  — 操作人类型（admin | teacher | student）
 *   <li>action    — 操作名称，如「添加学生」「修改成绩」
 *   <li>detail    — 操作详情
 *   <li>createdAt — 操作时间（数据库自动填充）
 * </ul>
 */
public class OperationLog {

    private int id;
    private String userId;    // 操作人 ID
    private String userType;  // admin | teacher | student
    private String action;    // 操作名称
    private String detail;    // 操作详情
    private String createdAt; // 操作时间

    /* ---- Getter / Setter ---- */

    public int getId()             { return id; }
    public void setId(int id)      { this.id = id; }

    public String getUserId()        { return userId; }
    public void setUserId(String u)  { this.userId = u; }

    public String getUserType()        { return userType; }
    public void setUserType(String u)  { this.userType = u; }

    public String getAction()        { return action; }
    public void setAction(String a)  { this.action = a; }

    public String getDetail()        { return detail; }
    public void setDetail(String d)  { this.detail = d; }

    public String getCreatedAt()         { return createdAt; }
    public void setCreatedAt(String ca)  { this.createdAt = ca; }
}
