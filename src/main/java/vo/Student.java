package vo;

/**
 * 学生实体 — 映射数据库 student 表
 *
 * <p>字段说明：
 * <ul>
 *   <li>id          — 学号（主键）
 *   <li>password    — 登录密码（PBKDF2 哈希存储）
 *   <li>name        — 姓名
 *   <li>sex         — 性别
 *   <li>school_date — 入学日期，格式如 2024-9
 *   <li>major       — 专业
 *   <li>email       — 邮箱
 * </ul>
 */
public class Student {

    private String id;           // 学号
    private String password;     // 登录密码
    private String name;         // 姓名
    private String sex;          // 性别
    private String school_date;  // 入学日期
    private String major;        // 专业
    private String email;        // 邮箱

    /* ---- Getter ---- */

    public String getId()          { return id; }
    public String getPassword()    { return password; }
    public String getName()        { return name; }
    public String getSex()         { return sex; }
    public String getSchool_date() { return school_date; }
    public String getMajor()       { return major; }
    public String getEmail()       { return email; }

    /* ---- Setter ---- */

    public void setId(String id)                   { this.id = id; }
    public void setPassword(String password)       { this.password = password; }
    public void setName(String name)               { this.name = name; }
    public void setSex(String sex)                 { this.sex = sex; }
    public void setSchool_date(String school_date) { this.school_date = school_date; }
    public void setMajor(String major)             { this.major = major; }
    public void setEmail(String email)             { this.email = email; }
}
