# 教务管理系统 StudentManager

基于 Java Web 的教务管理平台，支持**管理员**、**教师**、**学生**三种角色，涵盖学生管理、成绩管理、公告发布、操作审计、成绩统计、PDF/Excel 导出等功能。

> 仓库地址：[https://github.com/SpikeQin/StudentManager](https://github.com/SpikeQin/StudentManager)

---

## 目录

- [快速开始](#快速开始)
- [技术栈](#技术栈)
- [系统架构](#系统架构)
- [功能概览](#功能概览)
- [数据库设计](#数据库设计)
- [安全机制](#安全机制)
- [默认账号](#默认账号)
- [常见问题](#常见问题)

---

## 快速开始

### 环境要求

| 组件 | 版本 |
|------|------|
| JDK | 21 或更高 |
| MySQL | 8.0 或更高 |
| Tomcat | 10.1 或更高（Jakarta EE 10） |
| Maven | 3.6 或更高 |

### 部署步骤

```bash
# 1. 克隆项目
git clone https://github.com/SpikeQin/StudentManager.git
cd StudentManager

# 2. 初始化数据库
mysql -u root -p < student_manager.sql

# 3. 配置数据库连接
# 创建 src/main/resources/db.properties，内容如下：
#   db.driver=com.mysql.cj.jdbc.Driver
#   db.url=jdbc:mysql://localhost:3306/student_manager?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
#   db.username=root
#   db.password=你的密码
# 注：db.properties 已加入 .gitignore，不会提交到仓库

# 4. 构建 WAR 包
mvn clean package

# 5. 部署到 Tomcat 的 webapps/ 目录，启动后访问：
# http://localhost:8080/student-manager/login.jsp
```

### 头像持久化

头像图片存储在 `{用户目录}/StudentManager/userImg/` 下，以 `{ID}.jpeg` 命名。该目录独立于项目源码，重新构建或部署不会丢失头像文件。

---

## 技术栈

| 层次 | 技术选型 |
|------|---------|
| 表现层 | JSP / HTML5 / CSS3 / jQuery / Bootstrap 4 / Chart.js |
| 控制层 | Jakarta Servlet 6.0（`@WebServlet` 注解映射） |
| 业务层 | DAO 模式，面向接口编程 |
| 数据层 | MySQL 8.0+（utf8mb4 编码） |
| 构建工具 | Maven |
| PDF 生成 | iTextPDF 5.5.13.4（微软雅黑内嵌字体） |
| 密码安全 | PBKDF2WithHmacSHA256（10 万次迭代 / 随机盐值） |
| 容器 | Tomcat 10.1+ |

---

## 系统架构

```
┌─────────────────────────────────────────────────┐
│                   前端 (JSP)                     │
│   login / admin / teacher / student             │
└──────────────────┬──────────────────────────────┘
                   │ HTTP Request
┌──────────────────▼──────────────────────────────┐
│              控制层 (Servlet)                     │
│   EncodingFilter → 身份校验 → 参数验证 → 业务调用  │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│              数据访问层 (DAO)                     │
│   PreparedStatement 参数化查询 / LEFT JOIN 联表   │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│              MySQL 数据库                         │
│   6 张表：student / teacher / score / course     │
│           announcement / operation_log           │
└─────────────────────────────────────────────────┘
```

**目录结构：**

```
StudentManager/
├── pom.xml                         # Maven 项目配置
├── student_manager.sql             # 建库建表 + 初始数据
├── README.md
└── src/main/
    ├── java/
    │   ├── vo/                     # 实体类（VO）
    │   │   ├── Student.java
    │   │   ├── Teacher.java
    │   │   ├── Score.java
    │   │   ├── Announcement.java
    │   │   └── OperationLog.java
    │   ├── dao/                    # 数据访问层（DAO）
    │   │   ├── StudentD.java
    │   │   ├── TeacherD.java
    │   │   ├── ScoreD.java          # 含 ScoreWithStudent 内部类（联表结果）
    │   │   ├── AnnouncementD.java
    │   │   └── LogD.java
    │   ├── util/                   # 工具类
    │   │   ├── DBUtil.java          # 数据库连接（db.properties 驱动）
    │   │   ├── PasswordUtil.java    # PBKDF2 密码哈希
    │   │   ├── SecurityUtil.java    # XSS 防护 + CSRF Token
    │   │   └── LogUtil.java         # 操作日志门面
    │   └── servlet/                 # 控制器（17 个 Servlet + 1 个 Filter）
    │       ├── EncodingFilter.java         # 全局 UTF-8 编码过滤器
    │       ├── check_login.java            # 统一登录验证
    │       ├── exit.java                   # 退出登录
    │       ├── admin_dashboard.java        # 管理员后台
    │       ├── add_student.java            # 添加学生
    │       ├── delete_student.java         # 删除学生（级联成绩）
    │       ├── update_student.java         # 修改学生基本信息
    │       ├── update_score.java           # 修改成绩（含 0-100 校验）
    │       ├── student_update.java         # 学生个人资料更新
    │       ├── teacher_update.java         # 教师个人资料更新
    │       ├── update_teacher.java         # 教师信息整表更新
    │       ├── one_page_student.java       # 学生分页 + 模糊搜索
    │       ├── one_page_score.java         # 成绩分页 + 精确查找（联表）
    │       ├── score_statistics.java       # 成绩统计分析
    │       ├── announcement_manage.java    # 公告发布 / 删除
    │       ├── UploadImageServlet.java     # 通用头像上传
    │       └── ImageServlet.java           # 头像读取（持久化存储）
    ├── webapp/
    │   ├── login.jsp               # 登录页（含验证码）
    │   ├── index.jsp               # 首页（Cookie 自动登录）
    │   ├── code.jsp                # 图形验证码生成
    │   ├── forget.jsp              # 忘记密码引导页
    │   ├── admin/                  # 管理员端
    │   │   ├── logs.jsp            # 操作日志列表
    │   │   └── teachers.jsp        # 教师管理（CRUD + 弹窗）
    │   ├── teacher/                # 教师端
    │   │   ├── main.jsp            # 学生管理（分页 + 行内编辑）
    │   │   ├── score.jsp           # 成绩管理（联表 + 行内编辑）
    │   │   ├── statistics.jsp      # 成绩统计（Chart.js 柱状图）
    │   │   ├── score_excel.jsp     # Excel 成绩导出
    │   │   ├── announcements.jsp   # 公告管理（发布 / 删除）
    │   │   └── personal.jsp        # 教师个人信息（头像 / 邮箱 / 密码）
    │   ├── student/                # 学生端
    │   │   ├── main.jsp            # 成绩信息（含 PDF 导出链接）
    │   │   ├── pdf.jsp             # 成绩单 PDF 生成
    │   │   ├── announcements.jsp   # 公告查看
    │   │   └── personal.jsp        # 学生个人信息（头像 / 邮箱 / 密码）
    │   ├── resources/              # 静态资源
    │   │   ├── css/
    │   │   ├── js/                 # jQuery / Bootstrap / Chart.js（均为第三方库）
    │   │   ├── font/msyh.ttf       # 微软雅黑（PDF 中文渲染）
    │   │   └── img/                # 登录页图标
    │   └── WEB-INF/web.xml         # Web 配置
    └── resources/
        └── db.properties           # 数据库配置（不入库）
```

---

## 功能概览

### 公共功能

| 功能 | 说明 |
|------|------|
| 统一登录 | 教师 / 学生 / 管理员账号自动识别；记住我（Cookie，7 天有效） |
| 验证码 | 登录页随机 4 位数字验证码，点击刷新 |
| 找回密码 | 联系管理员重置 |
| 退出登录 | 清除 Session 和 Cookie |
| 密码安全 | PBKDF2 哈希存储；旧版明文密码首次登录自动升级 |
| 全局编码 | `EncodingFilter` 统一 UTF-8，告别乱码 |
| XSS 防护 | `SecurityUtil.escapeHtml()` 对所有输出到页面的数据进行 HTML 转义 |
| CSRF 防护 | 表单提交需携带一次性 CSRF Token |

### 管理员端

| 功能 | 说明 |
|------|------|
| 操作日志 | 分页查看所有用户操作记录（增删改查、登录等） |
| 教师管理 | 查看 / 添加 / 编辑 / 删除教师账号 |

### 教师端

| 功能 | 说明 |
|------|------|
| 学生管理 | 分页列表 / 按学号精确搜索 / 按姓名模糊搜索 / 行内编辑 / 删除（级联成绩） |
| 成绩管理 | 分页联表展示 / 行内编辑四科成绩 / 0-100 服务端校验 |
| 成绩统计 | 四科最高分 / 最低分 / 平均分 / 及格率 / 优秀率 / 五段分布柱状图 |
| 公告管理 | 发布公告 / 删除公告（POST） |
| Excel 导出 | 全部学生成绩一键导出 |
| 个人中心 | 头像上传 / 修改邮箱 / 修改密码 |

### 学生端

| 功能 | 说明 |
|------|------|
| 成绩查询 | 查看四科成绩 |
| PDF 导出 | 生成格式化成单 PDF（微软雅黑字体） |
| 公告查看 | 查看教师发布的所有公告 |
| 个人中心 | 头像上传 / 修改邮箱 / 修改密码 |

---

## 数据库设计

**数据库名：** `student_manager` | **字符集：** `utf8mb4`

### 表结构

#### student — 学生信息

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | varchar(50) | 主键 | 学号 |
| password | varchar(255) | - | PBKDF2 哈希 |
| name | varchar(50) | - | 姓名 |
| sex | varchar(50) | - | 性别 |
| school_date | varchar(50) | - | 入学日期 |
| major | varchar(50) | - | 专业 |
| email | varchar(50) | - | 邮箱 |

#### teacher — 教师信息

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | varchar(50) | 主键 | 教师号 |
| password | varchar(255) | - | PBKDF2 哈希 |
| name | varchar(50) | - | 姓名 |
| sex | varchar(50) | - | 性别 |
| email | varchar(50) | - | 邮箱 |
| role | varchar(20) | - | admin / teacher |

#### score — 成绩

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | varchar(50) | 主键，外键→student.id | 学号（级联删除） |
| data_structure | varchar(50) | - | 数据结构 |
| operating_system | varchar(50) | - | 操作系统 |
| computer_network | varchar(50) | - | 计算机网络 |
| computer_organization | varchar(50) | - | 计算机组成原理 |

#### course — 课程

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | int | 主键，自增 | 课程 ID |
| name | varchar(100) | - | 课程名称 |

#### announcement — 公告

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | int | 主键，自增 | 公告 ID |
| title | varchar(200) | - | 标题 |
| content | text | - | 正文 |
| publisher | varchar(50) | - | 发布人 |
| created_at | timestamp | 默认 CURRENT_TIMESTAMP | 发布时间 |

#### operation_log — 操作日志

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | int | 主键，自增 | 日志 ID |
| user_id | varchar(50) | - | 操作人 ID |
| user_type | varchar(20) | - | admin / teacher / student |
| action | varchar(100) | - | 操作名称 |
| detail | text | - | 操作详情 |
| created_at | timestamp | 默认 CURRENT_TIMESTAMP | 操作时间 |

---

## 安全机制

| 层面 | 措施 |
|------|------|
| SQL 注入 | 全部使用 `PreparedStatement` 参数化查询，杜绝字符串拼接 |
| 密码存储 | PBKDF2WithHmacSHA256，10 万次迭代，每用户独立 16 字节随机盐值 |
| 密码升级 | 旧版明文密码首次登录自动升级为哈希，无需用户干预 |
| 时序攻击 | `slowEquals()` 恒定时间比较，防止密码比对被侧信道利用 |
| XSS | `SecurityUtil.escapeHtml()` 对所有输出到页面的用户数据做 HTML 转义 |
| CSRF | 所有状态变更操作需携带一次性 CSRF Token，用后即销毁 |
| Session 固定 | 登录成功后 `changeSessionId()` 防止 Session Fixation |
| 越权防护 | 头像上传校验 session 用户 ID 与请求参数一致性（IDOR 防护） |
| 文件上传 | Magic bytes 校验，防止伪装 Content-Type 上传恶意文件 |
| 连接泄露 | 全部使用 `try-with-resources` 自动释放数据库连接 |
| 密码不入库 | `db.properties` 已加入 `.gitignore` |
| 敏感数据 | 批量查询列表不返回密码字段 |

---

## 默认账号

### 管理员

| 账号 | 密码 | 角色 |
|------|------|------|
| ztw | ztw | admin |

### 教师

| 账号 | 密码 |
|------|------|
| tea001 | tea001 |
| tea002 | tea002 |

### 学生

| 学号 | 密码 | 专业 |
|------|------|------|
| 202412211214 | 202412211214 | 软件工程 |
| 202412211219 | 202412211219 | 软件工程 |
| 202412211220 | 202412211220 | 软件工程 |
| 202412211221 | 202412211221 | 软件工程 |
| 202412211222 | 202412211222 | 软件工程 |
| 202412211223 | 202412211223 | 软件工程 |
| 202412211225 | 202412211225 | 软件工程 |

> 首次登录后系统会自动将密码升级为 PBKDF2 哈希，建议及时修改默认密码。

---

## 常见问题

**Q: 头像上传后不显示？**
确认 `{用户目录}/StudentManager/userImg/` 目录存在且可写，ImageServlet（`/image`）负责读取该目录。重启 Tomcat 后头像不会丢失。

**Q: 验证码不刷新？**
检查浏览器是否缓存了 `code.jsp`，点击验证码图片或附加随机参数可强制刷新。

**Q: 数据库密码怎么配置？**
创建 `src/main/resources/db.properties`，参考 [部署步骤](#部署步骤) 中的配置模板。该文件已加入 `.gitignore`，不会被提交到 GitHub。

**Q: 如何添加新课程？**
在 `course` 表中插入新记录；在 `score` 表中添加对应字段；修改 `Score.java` 实体类、`ScoreD.java` DAO、及前端 JSP 页面。

**Q: PDF 中文乱码？**
项目内置了微软雅黑字体（`resources/font/msyh.ttf`），`pdf.jsp` 通过 `BaseFont.createFont()` 加载，无需系统安装。
