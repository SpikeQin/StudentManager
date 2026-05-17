<%--
  教师管理 — 管理员端

  列表 + 行内编辑 + 弹窗添加。jQuery UI Dialog 实现添加对话框。
--%>
<%@ page import="vo.Teacher, java.util.ArrayList, java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>教师管理</title>
    <link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery-ui-1.10.4.custom.min.css">
    <script src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/jquery-ui-1.10.4.custom.min.js"></script>
</head>
<body>
<%-- 权限校验与数据获取：验证管理员登录状态，获取教师列表 --%>
<%
    Object info = session.getAttribute("info");
    if (!(info instanceof Teacher)) {
        response.sendRedirect("../login.jsp");
        return;
    }
    Teacher t = (Teacher) info;
    @SuppressWarnings("unchecked")
    List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
    if (teachers == null) {
        teachers = new ArrayList<>();
    }
    if (session.getAttribute("csrf_token") == null) {
        session.setAttribute("csrf_token", util.SecurityUtil.generateCsrfToken());
    }
%>
<div id="page" class="container">
    <div id="header">
        <!-- 管理员顶部信息与导航菜单 -->
        <div id="logo">
            <img src="${pageContext.request.contextPath}/image?id=<%=util.SecurityUtil.escapeHtml(t.getId())%>"/>
            <h1><%=util.SecurityUtil.escapeHtml(t.getName())%></h1>
        </div>
        <div id="menu">
            <ul>
                <li><a href="${pageContext.request.contextPath}/admin_dashboard?tab=logs">操作日志</a></li>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/admin_dashboard?tab=teachers">教师管理</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_score">成绩管理</a></li>
                <li><a href="${pageContext.request.contextPath}/score_statistics">统计看板</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/announcements.jsp">公告管理</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出</a></li>
            </ul>
        </div>
    </div>
    <div id="main">
        <div class="top">
            <h2>教师管理</h2>
            <hr/>
            <!-- 添加教师按钮，点击触发 jQuery UI Dialog -->
            <button class="btn-add">添加教师</button>
        </div>
        <!-- 教师数据表格，支持行内编辑模式 -->
        <div class="table">
            <table id="table">
                <tr><th>教师号</th><th>姓名</th><th>性别</th><th>邮箱</th><th>角色</th><th>操作</th></tr>
                <% for (Teacher teacher : teachers) {
                    String formId = "form_edit_" + teacher.getId();
                %>
                <tr>
                    <td><%=util.SecurityUtil.escapeHtml(teacher.getId())%></td>
                    <td>
                        <!-- 查看模式：显示文本；编辑模式：显示输入框 -->
                        <span class="view-text"><%=util.SecurityUtil.escapeHtml(teacher.getName() != null ? teacher.getName() : "")%></span>
                        <input value="<%=util.SecurityUtil.escapeHtml(teacher.getName() != null ? teacher.getName() : "")%>" name="name" form="<%=util.SecurityUtil.escapeHtml(formId)%>" class="table-input edit-input" style="display:none">
                    </td>
                    <td>
                        <span class="view-text"><%=util.SecurityUtil.escapeHtml(teacher.getSex() != null ? teacher.getSex() : "")%></span>
                        <input value="<%=util.SecurityUtil.escapeHtml(teacher.getSex() != null ? teacher.getSex() : "")%>" name="sex" form="<%=util.SecurityUtil.escapeHtml(formId)%>" class="table-input edit-input" style="display:none">
                    </td>
                    <td>
                        <span class="view-text"><%=util.SecurityUtil.escapeHtml(teacher.getEmail() != null ? teacher.getEmail() : "")%></span>
                        <input value="<%=util.SecurityUtil.escapeHtml(teacher.getEmail() != null ? teacher.getEmail() : "")%>" name="email" form="<%=util.SecurityUtil.escapeHtml(formId)%>" class="table-input edit-input" style="display:none">
                    </td>
                    <td><%="admin".equals(teacher.getRole()) ? "管理员" : "教师"%></td>
                    <td class="action-cell">
                        <!-- 编辑表单（隐藏），通过编辑/保存/取消按钮切换显示 -->
                        <form id="<%=util.SecurityUtil.escapeHtml(formId)%>" method="post" action="${pageContext.request.contextPath}/admin_dashboard" style="display:none">
                            <input type="hidden" name="csrf_token" value="<%=(String)session.getAttribute("csrf_token")%>">
                            <input type="hidden" name="action" value="editTeacher">
                            <input type="hidden" name="id" value="<%=util.SecurityUtil.escapeHtml(teacher.getId())%>">
                        </form>
                        <button type="button" class="edit-toggle-btn">编辑</button>
                        <input type="submit" class="save-btn" value="保存" form="<%=util.SecurityUtil.escapeHtml(formId)%>" style="display:none">
                        <button type="button" class="cancel-btn" style="display:none">取消</button>
                        <!-- 删除教师表单 -->
                        <form action="${pageContext.request.contextPath}/admin_dashboard" method="post" style="display:inline;">
                            <input type="hidden" name="csrf_token" value="<%=(String)session.getAttribute("csrf_token")%>">
                            <input type="hidden" name="action" value="deleteTeacher">
                            <input type="hidden" name="id" value="<%=util.SecurityUtil.escapeHtml(teacher.getId())%>">
                            <input type="submit" class="btn-delete" value="删除" onclick="return confirm('确定删除教师 <%=util.SecurityUtil.escapeHtml(teacher.getId())%> 吗?');">
                        </form>
                    </td>
                </tr>
                <% } %>
            </table>
            <!-- 无数据时的提示 -->
            <% if (teachers.isEmpty()) { %>
            <div style="color:#7b8c9d;font-size:13px;text-align:center;margin-top:20px;">暂无教师数据</div>
            <% } %>
        </div>
    </div>
</div>

<!-- 添加教师对话框（jQuery UI Dialog） -->
<div id="addDialog" title="添加教师">
    <form action="${pageContext.request.contextPath}/admin_dashboard" method="post">
        <input type="hidden" name="csrf_token" value="<%=(String)session.getAttribute("csrf_token")%>">
        <input type="hidden" name="action" value="addTeacher">
        教师号: <input type="text" name="id" required><br>
        姓名: <input type="text" name="name"><br>
        性别: <input type="text" name="sex"><br>
        密码: <input type="password" name="password" required><br>
        邮箱: <input type="email" name="email"><br>
        <hr>
        <input type="button" value="取消" onclick="$('#addDialog').dialog('close')" style="float:right">
        <input type="submit" value="添加" style="float:right;margin-right:10px">
    </form>
</div>
<script>
    // 点击"添加教师"按钮，打开 jQuery UI Dialog 对话框
    $('.btn-add').click(function(){ $('#addDialog').dialog('open'); });
    // 初始化添加对话框（默认关闭，模态）
    $('#addDialog').dialog({width:340,autoOpen:false,draggable:false,modal:true,resizable:false});
    // 点击"编辑"按钮：隐藏查看文本，显示编辑输入框和保存/取消按钮
    $('.edit-toggle-btn').click(function(){
        $(this).closest('tr').find('.view-text').hide();
        $(this).closest('tr').find('.edit-input').show();
        $(this).hide();
        $(this).closest('tr').find('.save-btn').show();
        $(this).closest('tr').find('.cancel-btn').show();
    });
    // 点击"取消"按钮：恢复查看模式，隐藏编辑输入框
    $('.cancel-btn').click(function(){
        $(this).closest('tr').find('.view-text').show();
        $(this).closest('tr').find('.edit-input').hide();
        $(this).hide();
        $(this).closest('tr').find('.save-btn').hide();
        $(this).closest('tr').find('.edit-toggle-btn').show();
    });
</script>
<style>.ui-dialog-titlebar-close{display:none}</style>
</body>
</html>
