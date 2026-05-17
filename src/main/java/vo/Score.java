package vo;

/**
 * 成绩实体 — 映射数据库 score 表
 *
 * <p>字段说明：
 * <ul>
 *   <li>id                   — 学号（主键，外键关联 student.id，级联删除）
 *   <li>dataStructure        — 数据结构成绩
 *   <li>operatingSystem      — 操作系统成绩
 *   <li>computerNetwork      — 计算机网络成绩
 *   <li>computerOrganization — 计算机组成原理成绩
 * </ul>
 */
public class Score {

    private String id;                    // 学号
    private String dataStructure;         // 数据结构
    private String operatingSystem;       // 操作系统
    private String computerNetwork;       // 计算机网络
    private String computerOrganization;  // 计算机组成原理

    /* ---- Getter ---- */
    public String getId()                   { return id; }
    public String getDataStructure()        { return dataStructure; }
    public String getOperatingSystem()      { return operatingSystem; }
    public String getComputerNetwork()      { return computerNetwork; }
    public String getComputerOrganization() { return computerOrganization; }

    /* ---- Setter ---- */
    public void setId(String id)                                     { this.id = id; }
    public void setDataStructure(String dataStructure)               { this.dataStructure = dataStructure; }
    public void setOperatingSystem(String operatingSystem)           { this.operatingSystem = operatingSystem; }
    public void setComputerNetwork(String computerNetwork)           { this.computerNetwork = computerNetwork; }
    public void setComputerOrganization(String computerOrganization) { this.computerOrganization = computerOrganization; }
}
