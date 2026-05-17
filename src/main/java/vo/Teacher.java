package vo;

/**
 * 教师实体 — 映射数据库 teacher 表
 *
 * <p>role 字段区分管理员（admin）和普通教师（teacher），
 * <br>管理员有额外权限：操作日志查看、教师账号管理。
 */
public class Teacher {

    private String id;       // 教师号（主键）
    private String password; // 登录密码（PBKDF2 哈希）
    private String email;    // 邮箱
    private String name;     // 姓名
    private String sex;      // 性别
    private String role;     // admin | teacher

    /* ---- Getter ---- */
    public String getId()       { return id; }
    public String getPassword() { return password; }
    public String getEmail()    { return email; }
    public String getName()     { return name; }
    public String getSex()      { return sex; }
    public String getRole()     { return role; }

    /* ---- Setter ---- */
    public void setId(String id)             { this.id = id; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email)       { this.email = email; }
    public void setName(String name)         { this.name = name; }
    public void setSex(String sex)           { this.sex = sex; }
    public void setRole(String role)         { this.role = role; }
}
