<%--
  操作日志列表 — 管理员端

  分页展示所有用户操作记录，数据由 admin_dashboard Servlet 注入。
--%>
<%@ page import="vo.Teacher, vo.OperationLog, java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>操作日志</title>
    <link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet"/>
</head>
<body>
<%-- 权限校验：检查 session 中是否包含教师对象，未登录则重定向到登录页 --%>
<%
    Object info = session.getAttribute("info");
    if (!(info instanceof Teacher)) {
        response.sendRedirect("../login.jsp");
        return;
    }
    Teacher t = (Teacher) info;
    ArrayList<OperationLog> logs = (ArrayList<OperationLog>) request.getAttribute("logList");
    Integer sumIndex = (Integer) request.getAttribute("logSumIndex");
    if (logs == null) {
        response.sendRedirect(request.getContextPath() + "/admin_dashboard?tab=logs");
        return;
    }
%>
<div id="page" class="container">
    <div id="header">
        <!-- 管理员顶部信息与导航菜单 -->
        <div id="logo"><img src="${pageContext.request.contextPath}/image?id=<%=util.SecurityUtil.escapeHtml(t.getId())%>"/><h1><%=util.SecurityUtil.escapeHtml(t.getName())%></h1></div>
        <div id="menu">
            <ul>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/admin_dashboard?tab=logs">操作日志</a></li>
                <li><a href="${pageContext.request.contextPath}/admin_dashboard?tab=teachers">教师管理</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_score">成绩管理</a></li>
                <li><a href="${pageContext.request.contextPath}/score_statistics">统计看板</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/announcements.jsp">公告管理</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出</a></li>
            </ul>
        </div>
    </div>
    <div id="main">
        <div class="top"><h2>操作日志</h2></div>
        <!-- 操作日志数据表格 -->
        <div class="table">
            <table id="table">
                <tr><th>ID</th><th>用户</th><th>类型</th><th>操作</th><th>详情</th><th>时间</th></tr>
<%  for (OperationLog log : logs) { %>
                <tr>
                    <td><%=util.SecurityUtil.escapeHtml(String.valueOf(log.getId()))%></td>
                    <td><%=util.SecurityUtil.escapeHtml(log.getUserId())%></td>
                    <td><%=util.SecurityUtil.escapeHtml(log.getUserType())%></td>
                    <td><%=util.SecurityUtil.escapeHtml(log.getAction())%></td>
                    <td style="max-width:300px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;"><%=util.SecurityUtil.escapeHtml(log.getDetail() != null ? log.getDetail() : "")%></td>
                    <td style="font-size:12px;"><%=log.getCreatedAt() != null ? log.getCreatedAt().substring(0, 19) : ""%></td>
                </tr>
<%  } %>
            </table>
        </div>
        <% if (sumIndex != null && sumIndex > 1) { %>
        <!-- 分页导航：首页 / 分页链接 / 尾页 -->
        <div id="index">
            <a href="${pageContext.request.contextPath}/admin_dashboard?tab=logs&index=1">首页</a>
            <% for (int i=1; i<=sumIndex; i++) { %>
            <a href="${pageContext.request.contextPath}/admin_dashboard?tab=logs&index=<%=i%>">第<%=i%>页</a>
            <% } %>
            <a href="${pageContext.request.contextPath}/admin_dashboard?tab=logs&index=<%=sumIndex%>">尾页</a>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>
