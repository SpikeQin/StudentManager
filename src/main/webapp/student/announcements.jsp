<%--
  公告列表 — 学生端

  卡片形式展示全部公告，按时间倒序排列。
--%>
<%@ page import="vo.Student, vo.Announcement, dao.AnnouncementD, java.util.ArrayList, util.SecurityUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>公告</title>
    <link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet"/>
</head>
<body>
<%-- 权限校验：验证学生登录状态，未登录则重定向到登录页 --%>
<%
    Object info = session.getAttribute("info");
    if (!(info instanceof Student)) { 
        response.sendRedirect("../login.jsp"); 
        return; 
    }
    Student s = (Student) info;
    AnnouncementD ad = new AnnouncementD();
    ArrayList<Announcement> list = ad.getAll();
%>
<div id="page" class="container">
    <div id="header">
        <!-- 学生端顶部信息与导航菜单 -->
        <div id="logo"><img src="${pageContext.request.contextPath}/image?id=<%=util.SecurityUtil.escapeHtml(s.getId())%>"/><h1><%=util.SecurityUtil.escapeHtml(s.getName())%></h1></div>
        <div id="menu">
            <ul>
                <li><a href="${pageContext.request.contextPath}/student/personal.jsp">个人信息</a></li>
                <li><a href="${pageContext.request.contextPath}/student/main.jsp">成绩信息</a></li>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/student/announcements.jsp">公告</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出登录</a></li>
            </ul>
        </div>
    </div>
    <div id="main">
        <div class="top"><h2>系统公告</h2></div>
        <!-- 公告列表：以卡片形式逐条渲染 -->
<%  for (Announcement a : list) { %>
        <div style="border:1px solid #e2e6ea;border-radius:6px;padding:16px 20px;margin-bottom:12px;">
            <h3 style="font-size:15px;color:#1e3a5f;margin:0 0 6px 0;"><%=util.SecurityUtil.escapeHtml(a.getTitle())%></h3>
            <div style="font-size:12px;color:#7b8c9d;margin-bottom:8px;">
                <%=util.SecurityUtil.escapeHtml(a.getPublisher())%> 发布于 <%=a.getCreatedAt() != null ? util.SecurityUtil.escapeHtml(a.getCreatedAt().substring(0, 16)) : ""%>
            </div>
            <p style="margin:0;color:#3d4f5f;font-size:13px;line-height:1.6;white-space:pre-wrap;"><%=util.SecurityUtil.escapeHtml(a.getContent())%></p>
        </div>
<%  } %>
        <!-- 无公告时的空状态提示 -->
        <% if (list.isEmpty()) { %><p style="color:#7b8c9d;text-align:center;padding:40px;">暂无公告</p><% } %>
    </div>
</div>
</body>
</html>
