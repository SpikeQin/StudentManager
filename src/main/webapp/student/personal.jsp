<%--
  个人信息 — 学生端

  查看基本信息 + 修改邮箱 / 密码 / 上传头像。
--%>
<%@ page import="vo.Student, util.SecurityUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>个人信息</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery-ui-1.10.4.custom.min.css">
    <script src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/jquery-ui-1.10.4.custom.min.js"></script>
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
    if (session.getAttribute("csrf_token") == null) {
        session.setAttribute("csrf_token", util.SecurityUtil.generateCsrfToken());
    }
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
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/student/personal.jsp">个人信息</a></li>
                <li><a href="${pageContext.request.contextPath}/student/main.jsp">成绩信息</a></li>
                <li><a href="${pageContext.request.contextPath}/student/announcements.jsp">公告</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出登录</a></li>
            </ul>
        </div>
    </div>
    <div id="main">
        <div class="top">
            <h2>个人信息</h2>
            <hr/>
        </div>
        <div class="info">
            <!-- 头像展示与上传区域 -->
            <img src="${pageContext.request.contextPath}/image?id=<%=util.SecurityUtil.escapeHtml(student.getId())%>" class="personalImg"><br>
            <!-- 头像上传表单：选择文件后自动提交 -->
            <form id="uploadForm" action="${pageContext.request.contextPath}/upload_image" method="post" enctype="multipart/form-data">
                <input type="hidden" name="csrf_token" value="<%=session.getAttribute("csrf_token")%>">
                <input type="hidden" name="type" value="student">
                <input type="hidden" name="id" value="<%=util.SecurityUtil.escapeHtml(student.getId())%>">
                <input type="file" id="fileInput" name="img" accept="image/*" onchange="document.getElementById('uploadForm').submit()">
                <label for="fileInput" class="upload-btn">更换头像</label>
            </form>
            <!-- 个人信息展示表格 -->
            <table frame="box" align="center">
                <tr>
                    <td>学号</td>
                    <td><%=util.SecurityUtil.escapeHtml(student.getId())%></td>
                </tr>
                <tr>
                    <td>姓名</td>
                    <td><%=util.SecurityUtil.escapeHtml(student.getName())%></td>
                </tr>
                <tr>
                    <td>性别</td>
                    <td><%=util.SecurityUtil.escapeHtml(student.getSex())%></td>
                </tr>
                <tr>
                    <td>专业</td>
                    <td><%=util.SecurityUtil.escapeHtml(student.getMajor())%></td>
                </tr>
                <tr>
                    <td>邮箱</td>
                    <td>
                        <!-- 邮箱：查看模式显示文本，编辑模式显示输入框 -->
                        <span class="view-text"><%=util.SecurityUtil.escapeHtml(student.getEmail() != null ? student.getEmail() : "")%></span>
                        <input value="<%=util.SecurityUtil.escapeHtml(student.getEmail() != null ? student.getEmail() : "")%>" name="email" form="updateForm" class="table-input edit-input" style="display:none">
                    </td>
                </tr>
            </table>
            <!-- 个人信息更新表单（隐藏）：通过编辑/保存按钮切换显示 -->
            <form id="updateForm" method="post" action="${pageContext.request.contextPath}/student_update" style="display:none">
                <input type="hidden" name="csrf_token" value="<%=session.getAttribute("csrf_token")%>">
                <input type="hidden" name="id" value="<%=util.SecurityUtil.escapeHtml(student.getId())%>">
                <input type="password" name="password" placeholder="新密码（不填则不修改）" class="edit-input" style="display:none">
            </form>
            <!-- 编辑模式控制按钮 -->
            <button type="button" class="edit-toggle-btn" style="margin-top:15px;">编辑信息</button>
            <input type="submit" class="save-btn" value="保存" form="updateForm" style="display:none;margin-top:15px;">
            <button type="button" class="cancel-btn" style="display:none;margin-top:15px;">取消</button>
        </div>
    </div>
</div>
<script>
    // 点击"编辑信息"按钮：隐藏查看模式文本，显示编辑输入框和保存/取消按钮
    $('.edit-toggle-btn').click(function(){
        $('.view-text').hide();
        $('.edit-input').show();
        $('.edit-toggle-btn').hide();
        $('.save-btn').show();
        $('.cancel-btn').show();
    });
    // 点击"取消"按钮：恢复查看模式，隐藏编辑输入框和保存/取消按钮
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
