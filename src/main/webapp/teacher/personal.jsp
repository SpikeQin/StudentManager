<%--
  个人信息 — 教师端

  查看基本信息 + 修改邮箱 / 密码 / 上传头像。
--%>
<%@ page import="vo.Teacher" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>个人信息</title>
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
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    Teacher teacher = (Teacher) info;
    if (session.getAttribute("csrf_token") == null) {
        session.setAttribute("csrf_token", util.SecurityUtil.generateCsrfToken());
    }
%>
<div id="page" class="container">
    <div id="header">
        <div id="logo">
            <img src="${pageContext.request.contextPath}/image?id=<%=util.SecurityUtil.escapeHtml(teacher.getId())%>"/>
            <h1><%=util.SecurityUtil.escapeHtml(teacher.getName())%></h1>
        </div>
        <%-- 导航菜单：根据角色显示不同的菜单项 --%>
        <div id="menu">
            <ul>
        <%-- 管理员菜单 --%>
        <% if ("admin".equals(teacher.getRole())) { %>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/admin_dashboard?tab=logs">操作日志</a></li>
                <li><a href="${pageContext.request.contextPath}/admin_dashboard?tab=teachers">教师管理</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_score">成绩管理</a></li>
                <li><a href="${pageContext.request.contextPath}/score_statistics">统计看板</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/announcements.jsp">公告管理</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出</a></li>
        <%-- 普通教师菜单 --%>
        <% } else { %>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/teacher/personal.jsp">个人信息</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_score">成绩管理</a></li>
                <li><a href="${pageContext.request.contextPath}/score_statistics">统计看板</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/announcements.jsp">公告管理</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出</a></li>
        <% } %>
            </ul>
        </div>
    </div>
    <div id="main">
        <div class="top">
            <h2>个人信息</h2>
            <hr/>
        </div>
        <%-- 个人信息展示区域 --%>
        <div class="info">
            <%-- 头像显示与上传表单 --%>
            <img src="${pageContext.request.contextPath}/image?id=<%=util.SecurityUtil.escapeHtml(teacher.getId())%>" class="personalImg"><br>
            <form id="uploadForm" action="${pageContext.request.contextPath}/upload_image" method="post" enctype="multipart/form-data">
                <input type="hidden" name="csrf_token" value="<%=(String)session.getAttribute("csrf_token")%>">
                <input type="hidden" name="type" value="teacher">
                <input type="hidden" name="id" value="<%=util.SecurityUtil.escapeHtml(teacher.getId())%>">
                <input type="file" id="fileInput" name="img" accept="image/*" onchange="document.getElementById('uploadForm').submit()">
                <label for="fileInput" class="upload-btn">更换头像</label>
            </form>
            <%-- 基本信息表格：展示教师基本信息 --%>
            <table>
                <tr>
                    <td>教师号</td>
                    <td><%=util.SecurityUtil.escapeHtml(teacher.getId())%></td>
                </tr>
                <tr>
                    <td>姓名</td>
                    <td><%=util.SecurityUtil.escapeHtml(teacher.getName())%></td>
                </tr>
                <tr>
                    <td>性别</td>
                    <td><%=util.SecurityUtil.escapeHtml(teacher.getSex())%></td>
                </tr>
                <tr>
                    <td>邮箱</td>
                    <td>
                        <span class="view-text"><%=util.SecurityUtil.escapeHtml(teacher.getEmail() != null ? teacher.getEmail() : "")%></span>
                        <input value="<%=util.SecurityUtil.escapeHtml(teacher.getEmail() != null ? teacher.getEmail() : "")%>" name="email" form="updateForm" class="table-input edit-input" style="display:none">
                    </td>
                </tr>
            </table>
            <%-- 隐藏的更新表单：用于提交教师信息修改 --%>
            <form id="updateForm" method="post" action="${pageContext.request.contextPath}/teacher_update" style="display:none">
                <input type="hidden" name="csrf_token" value="<%=(String)session.getAttribute("csrf_token")%>">
                <input type="hidden" name="id" value="<%=util.SecurityUtil.escapeHtml(teacher.getId())%>">
                <input type="hidden" name="name" value="<%=util.SecurityUtil.escapeHtml(teacher.getName())%>">
                <input type="hidden" name="sex" value="<%=util.SecurityUtil.escapeHtml(teacher.getSex())%>">
                <input type="password" name="password" placeholder="新密码（不填则不修改）" form="updateForm" class="edit-input" style="display:none">
            </form>
            <button type="button" class="edit-toggle-btn" style="margin-top:15px;">编辑信息</button>
            <input type="submit" class="save-btn" value="保存" form="updateForm" style="display:none;margin-top:15px;">
            <button type="button" class="cancel-btn" style="display:none;margin-top:15px;">取消</button>
        </div>
    </div>
</div>
<script>
    // 点击编辑按钮：隐藏只读文本，显示编辑输入框和保存/取消按钮
    $('.edit-toggle-btn').click(function(){
        $('.view-text').hide();
        $('.edit-input').show();
        $('.edit-toggle-btn').hide();
        $('.save-btn').show();
        $('.cancel-btn').show();
    });
    // 点击取消按钮：恢复只读显示，隐藏编辑输入框
    $('.cancel-btn').click(function(){
        $('.view-text').show();
        $('.edit-input').hide();
        $('.save-btn').hide();
        $('.cancel-btn').hide();
        $('.edit-toggle-btn').show();
    });
</script>
</body>
</html>
