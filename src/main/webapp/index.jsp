<%--
  首页 — Cookie 自动登录入口

  读取「记住我」Cookie，按身份跳转：管理员→后台 / 教师→学生管理 / 学生→成绩页。
  未登录跳回登录页。
--%>
<%@ page import="dao.TeacherD" %>
<%@ page import="dao.StudentD" %>
<%@ page import="vo.Teacher" %>
<%@ page import="vo.Student" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>教务管理系统</title>
</head>
<body>
<%
    // 初始化 DAO 对象，用于查询教师和学生信息
    TeacherD teacherD = new TeacherD();
    StudentD studentD = new StudentD();
    Teacher teacher = null;
    Student student = null;

    // 遍历所有 Cookie，查找名为 "name" 的登录凭据
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie c : cookies) {
            String cookieName = c.getName();
            // 找到登录 Cookie，验证用户身份
            if ("name".equals(cookieName)) {
                String user = c.getValue();
                try {
                    // 分别尝试以该值查询教师表和学生表
                    teacher = teacherD.findWithId(user);
                    student = studentD.findWithId(user);
                } catch (Exception e) {
                    Logger.getLogger("index").log(Level.WARNING, "Cookie auto-login failed", e);
                }
                // 教师身份验证通过，根据角色跳转到不同页面
                if (teacher != null) {
                    session.setAttribute("info", teacher);
                    if ("admin".equals(teacher.getRole())) {
                        // 管理员 → 跳转到后台日志页面
                        response.sendRedirect(request.getContextPath() + "/admin_dashboard?tab=logs");
                    } else {
                        // 普通教师 → 跳转到学生管理页面
                        response.sendRedirect(request.getContextPath() + "/one_page_student");
                    }
                    return;
                }
                // 学生身份验证通过，跳转到个人主页
                else if(student != null){
                    session.setAttribute("info", student);
                    response.sendRedirect(request.getContextPath() + "/student/main.jsp");
                    return;
                }
            }
        }
    }
    // 未找到有效 Cookie 或身份验证失败，重定向到登录页面
    response.sendRedirect(request.getContextPath() + "/login.jsp");
%>
</body>
</html>
