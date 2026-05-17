<%--
  公告管理 — 教师 / 管理员

  列表 + 弹窗发布 + POST 删除。
--%>
<%@ page import="vo.Teacher, vo.Announcement, dao.AnnouncementD, java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>公告管理</title>
    <link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery-ui-1.10.4.custom.min.css">
    <script src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/jquery-ui-1.10.4.custom.min.js"></script>
</head>
<body>
<%-- 身份校验：未登录或非教师角色重定向到登录页 --%>
<%
    Object info = session.getAttribute("info");
    if (!(info instanceof Teacher)) {
        response.sendRedirect("../login.jsp");
        return;
    }
    Teacher t = (Teacher) info;
    AnnouncementD ad = new AnnouncementD();
    ArrayList<Announcement> list = ad.getAll();
    if (session.getAttribute("csrf_token") == null) {
        session.setAttribute("csrf_token", util.SecurityUtil.generateCsrfToken());
    }
%>
<div id="page" class="container">
    <div id="header">
        <div id="logo"><img src="${pageContext.request.contextPath}/image?id=<%=util.SecurityUtil.escapeHtml(t.getId())%>"/><h1><%=util.SecurityUtil.escapeHtml(t.getName())%></h1></div>
        <%-- 导航菜单：根据角色显示不同的菜单项 --%>
        <div id="menu">
            <ul>
        <%-- 管理员菜单 --%>
        <% if ("admin".equals(t.getRole())) { %>
            <li><a href="${pageContext.request.contextPath}/admin_dashboard?tab=logs">操作日志</a></li>
            <li><a href="${pageContext.request.contextPath}/admin_dashboard?tab=teachers">教师管理</a></li>
            <li><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
            <li><a href="${pageContext.request.contextPath}/one_page_score">成绩管理</a></li>
            <li><a href="${pageContext.request.contextPath}/score_statistics">统计看板</a></li>
            <li class="current_page_item"><a href="${pageContext.request.contextPath}/teacher/announcements.jsp">公告管理</a></li>
            <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出</a></li>
        <%-- 普通教师菜单 --%>
        <% } else { %>
            <li><a href="${pageContext.request.contextPath}/teacher/personal.jsp">个人信息</a></li>
            <li><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
            <li><a href="${pageContext.request.contextPath}/one_page_score">成绩管理</a></li>
            <li><a href="${pageContext.request.contextPath}/score_statistics">统计看板</a></li>
            <li class="current_page_item"><a href="${pageContext.request.contextPath}/teacher/announcements.jsp">公告管理</a></li>
            <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出</a></li>
        <% } %>
            </ul>
        </div>
    </div>
    <div id="main">
        <div class="top"><h2>公告管理</h2></div>
        <button class="btn-add" onclick="$('#addDialog').dialog('open')">添加公告</button>
        <%-- 公告列表区域：遍历展示所有公告 --%>
        <div style="margin-top:20px;clear:both;">
<%  for (Announcement a : list) { %>
        <div style="border:1px solid #e2e6ea;border-radius:6px;padding:16px 20px;margin-bottom:12px;">
            <div style="display:flex;justify-content:space-between;align-items:center;">
                <h3 style="font-size:15px;color:#1e3a5f;margin:0;"><%=util.SecurityUtil.escapeHtml(a.getTitle())%></h3>
                <div style="font-size:12px;color:#7b8c9d;">
                    <%=util.SecurityUtil.escapeHtml(a.getPublisher())%> 发布于 <%=a.getCreatedAt() != null ? a.getCreatedAt().substring(0, 16) : ""%>
                    <form method="post" action="${pageContext.request.contextPath}/announcement_manage" style="display:inline;">
                        <input type="hidden" name="csrf_token" value="<%=(String)session.getAttribute("csrf_token")%>">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="<%=util.SecurityUtil.escapeHtml(String.valueOf(a.getId()))%>">
                        <button type="submit" class="btn-delete" style="margin-left:10px;" onclick="return confirm('确定删除此公告?')">删除</button>
                    </form>
                </div>
            </div>
            <p style="margin:10px 0 0 0;color:#3d4f5f;font-size:13px;line-height:1.6;white-space:pre-wrap;"><%=util.SecurityUtil.escapeHtml(a.getContent())%></p>
        </div>
<%  } %>
        <%-- 空状态提示：无公告时显示 --%>
        <% if (list.isEmpty()) { %><p style="color:#7b8c9d;text-align:center;padding:40px;">暂无公告</p><% } %>
        </div>
    </div>
</div>

<%-- 添加公告弹窗：通过 jQuery UI Dialog 实现模态表单 --%>
<div id="addDialog" title="添加公告">
    <form action="${pageContext.request.contextPath}/announcement_manage" method="post">
        <input type="hidden" name="csrf_token" value="<%=(String)session.getAttribute("csrf_token")%>">
        <input type="hidden" name="action" value="add">
        标题: <input type="text" name="title" required style="width:100%"><br>
        内容: <textarea name="content" rows="6" required style="width:100%;margin:8px 0;padding:8px;border:1px solid #dde2e8;border-radius:3px;font-size:13px;resize:vertical;"></textarea>
        <hr>
        <input type="button" value="取消" onclick="$('#addDialog').dialog('close')" style="float:right">
        <input type="submit" value="发布" style="float:right;margin-right:10px">
    </form>
</div>
<script>
    // 初始化添加公告弹窗：设置宽度、禁用拖拽、模态、不可调整大小
    $('#addDialog').dialog({width:420,autoOpen:false,draggable:false,modal:true,resizable:false});
</script>
<%-- 隐藏弹窗关闭按钮 --%>
<style>.ui-dialog-titlebar-close{display:none}</style>
</body>
</html>
