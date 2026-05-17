<%--
  成绩信息 — 学生端

  展示四科成绩 + PDF 导出链接。
--%>
<%@ page import="vo.Student" %>
<%@ page import="dao.ScoreD" %>
<%@ page import="vo.Score" %>
<%@ page import="util.SecurityUtil" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>成绩信息</title>
    <link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet"/>
</head>
<body>
<%-- 权限校验：验证学生登录状态，未登录则重定向到登录页 --%>
<%
    Object info = session.getAttribute("info");
    if (!(info instanceof Student)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    Student student = (Student) info;
%>
<div id="page" class="container">
    <div id="header">
        <!-- 学生端顶部信息与导航菜单 -->
        <div id="logo">
            <img src="${pageContext.request.contextPath}/image?id=<%=util.SecurityUtil.escapeHtml(student.getId())%>"/>
            <h1><%=util.SecurityUtil.escapeHtml(student.getName())%></h1>
        </div>
        <div id="menu">
            <ul>
                <li><a href="${pageContext.request.contextPath}/student/personal.jsp">个人信息</a></li>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/student/main.jsp">成绩信息</a></li>
                <li><a href="${pageContext.request.contextPath}/student/announcements.jsp">公告</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出登录</a></li>
            </ul>
        </div>
    </div>
    <div id="main">
        <div class="top">
            <h2>成绩信息</h2>
            <hr/>
        </div>
        <!-- 学生成绩表格：包含基本信息及四门课程成绩 -->
        <div class="table">
            <table frame="box" align="center">
                <tr>
                    <th>学号</th>
                    <th>姓名</th>
                    <th>专业</th>
                    <th>数据结构</th>
                    <th>操作系统</th>
                    <th>计算机网络</th>
                    <th>计算机组成原理</th>
                    <th>操作</th>
                </tr>
                <%
                    try {
                        ScoreD scoD = new ScoreD();
                        Score stu = scoD.findWithId(student.getId());
                        String name = student.getName();
                        String major = student.getMajor();
                        if (stu == null) { // 防止无成绩记录时 NPE
                %>
                <tr>
                    <td colspan="8" style="text-align:center;color:#999;">暂无成绩记录</td>
                </tr>
                <%
                        } else {
                %>
                <tr>
                    <td><%=util.SecurityUtil.escapeHtml(stu.getId())%></td>
                    <td><%=util.SecurityUtil.escapeHtml(name)%></td>
                    <td><%=util.SecurityUtil.escapeHtml(major)%></td>
                    <td><%=util.SecurityUtil.escapeHtml(stu.getDataStructure())%></td>
                    <td><%=util.SecurityUtil.escapeHtml(stu.getOperatingSystem())%></td>
                    <td><%=util.SecurityUtil.escapeHtml(stu.getComputerNetwork())%></td>
                    <td><%=util.SecurityUtil.escapeHtml(stu.getComputerOrganization())%></td>
                    <!-- PDF 导出链接：将成绩数据通过 URL 参数传递到 pdf.jsp -->
                    <td><a href="pdf.jsp?id=<%=util.SecurityUtil.escapeHtml(stu.getId())%>&name=<%=util.SecurityUtil.escapeHtml(name)%>&major=<%=util.SecurityUtil.escapeHtml(major)%>&dataStructure=<%=util.SecurityUtil.escapeHtml(stu.getDataStructure())%>&operatingSystem=<%=util.SecurityUtil.escapeHtml(stu.getOperatingSystem())%>&computerNetwork=<%=util.SecurityUtil.escapeHtml(stu.getComputerNetwork())%>&computerOrganization=<%=util.SecurityUtil.escapeHtml(stu.getComputerOrganization())%>">PDF</a></td>
                </tr>
                <%
                        }
                    } catch (Exception e) {
                        Logger.getLogger("student_main").log(Level.WARNING, "Failed to load score", e);
                    }
                %>
            </table>
        </div>
    </div>
</div>
</body>
</html>
