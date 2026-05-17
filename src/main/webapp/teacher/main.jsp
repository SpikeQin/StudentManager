<%--
  学生管理 — 教师端

  分页列表 + 搜索（姓名/学号）+ 弹窗添加 + 行内编辑 + 删除。
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="vo.Student" %>
<%@ page import="vo.Teacher" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery-ui-1.10.4.custom.min.css">
    <script src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/jquery-ui-1.10.4.custom.min.js"></script>
    <title>学生管理</title>
    <link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet"/>
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
    ArrayList<Student> stus = (ArrayList<Student>) session.getAttribute("onePageStudent");
    int sumIndex = (int) session.getAttribute("sumIndex");
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
                <li><a href="${pageContext.request.contextPath}/admin_dashboard?tab=logs">操作日志</a></li>
                <li><a href="${pageContext.request.contextPath}/admin_dashboard?tab=teachers">教师管理</a></li>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_score">成绩管理</a></li>
                <li><a href="${pageContext.request.contextPath}/score_statistics">统计看板</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/announcements.jsp">公告管理</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出</a></li>
        <%-- 普通教师菜单 --%>
        <% } else { %>
                <li><a href="${pageContext.request.contextPath}/teacher/personal.jsp">个人信息</a></li>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
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
            <h2>学生信息管理</h2>
            <hr/>
            <%
                Boolean singleStudentView = (Boolean) session.getAttribute("singleStudentView");
                if (singleStudentView == null || !singleStudentView) {
            %>
            <%-- 添加学生按钮：仅在未查看单个学生时显示 --%>
            <button class="btn-add">添加学生</button>
            <%
                }
            %>
            <%-- 搜索表单：按学号或姓名搜索学生 --%>
            <div class="find">
                <form action="${pageContext.request.contextPath}/one_page_student" method="post">
                    <input type="hidden" name="csrf_token" value="<%=(String)session.getAttribute("csrf_token")%>">
                    <input id="find-text" type="text" name="key" placeholder="输入学号或姓名搜索">
                    <input class="find-btn" type="submit" value="搜索">
                </form>
            </div>
        </div>
        <%-- 学生信息表格 --%>
        <div class="table">
            <table id="table">
                <tr>
                    <th>学号</th>
                    <th>姓名</th>
                    <th>性别</th>
                    <th>入学日期</th>
                    <th>专业</th>
                    <th>操作</th>
                </tr>
                <%
                    for (Student stu : stus) {
                        String formId = "form_" + stu.getId();
                %>
                        <tr>
                            <td><%=util.SecurityUtil.escapeHtml(stu.getId())%></td>
                            <td>
                                <span class="view-text"><%=util.SecurityUtil.escapeHtml(stu.getName())%></span>
                                <input value="<%=util.SecurityUtil.escapeHtml(stu.getName())%>" name="stuname" form="<%=util.SecurityUtil.escapeHtml(formId)%>" class="table-input edit-input" style="display:none">
                            </td>
                            <td>
                                <span class="view-text"><%=util.SecurityUtil.escapeHtml(stu.getSex())%></span>
                                <input value="<%=util.SecurityUtil.escapeHtml(stu.getSex())%>" name="stusex" form="<%=util.SecurityUtil.escapeHtml(formId)%>" class="table-input edit-input" style="display:none">
                            </td>
                            <td><%=util.SecurityUtil.escapeHtml(stu.getSchool_date())%></td>
                            <td>
                                <span class="view-text"><%=util.SecurityUtil.escapeHtml(stu.getMajor())%></span>
                                <input value="<%=util.SecurityUtil.escapeHtml(stu.getMajor())%>" name="stumajor" form="<%=util.SecurityUtil.escapeHtml(formId)%>" class="table-input edit-input" style="display:none">
                            </td>
                            <td class="action-cell">
                                <form id="<%=util.SecurityUtil.escapeHtml(formId)%>" method="post" action="${pageContext.request.contextPath}/update_student" style="display:none">
                                    <input type="hidden" name="csrf_token" value="<%=(String)session.getAttribute("csrf_token")%>">
                                    <input value="<%=util.SecurityUtil.escapeHtml(stu.getId())%>" name="stuno" type="hidden">
                                </form>
                                <button type="button" class="edit-toggle-btn">编辑</button>
                                <input type="submit" class="save-btn" value="保存" form="<%=util.SecurityUtil.escapeHtml(formId)%>" style="display:none">
                                <button type="button" class="cancel-btn" style="display:none">取消</button>
                                <form method="post" action="${pageContext.request.contextPath}/delete_student" style="display:inline;">
                                    <input type="hidden" name="id" value="<%=util.SecurityUtil.escapeHtml(stu.getId())%>">
                                    <button type="submit" class="btn-delete" onclick="return confirm('确定要删除吗?');">删除</button>
                                </form>
                                <a href="${pageContext.request.contextPath}/one_page_score?id=<%=util.SecurityUtil.escapeHtml(stu.getId())%>">查看成绩</a>
                            </td>
                        </tr>
                <%
                    }
                %>
            </table>
        </div>
        <%-- 分页控件：首页、页码、末页 --%>
        <div class="page">
            <% if (sumIndex > 1) { %>
            <a href="${pageContext.request.contextPath}/one_page_student?index=1">首页</a>
            <% } %>
            <%
                for (int i = 1; i <= sumIndex; i++) {
            %>
            <a href="${pageContext.request.contextPath}/one_page_student?index=<%=i%>">第<%=i%>页</a>
            <%
                }
            %>
            <% if (sumIndex > 1) { %>
            <a href="${pageContext.request.contextPath}/one_page_student?index=<%=sumIndex%>">末页</a>
            <% } %>
        </div>
        <%-- 添加学生弹窗表单 --%>
        <div id="addDialog" title="添加学生">
            <form action="${pageContext.request.contextPath}/add_student" method="post">
                <input type="hidden" name="csrf_token" value="<%=(String)session.getAttribute("csrf_token")%>">
                学号: <input type="text" name="id" required><br>
                姓名: <input type="text" name="name" required><br>
                性别: <input type="text" name="sex"><br>
                专业: <input type="text" name="major"><br>
                入学日期: <input type="month" name="school_date"><br>
                <hr>
                <input type="button" value="取消" onclick="$('#addDialog').dialog('close')" style="float:right">
                <input type="submit" value="添加" style="float:right;margin-right:10px">
            </form>
        </div>
    </div>
</div>
<script>
    // 点击添加按钮打开添加学生弹窗
    $('.btn-add').click(function(){ $('#addDialog').dialog('open'); });
    // 初始化添加学生弹窗
    $('#addDialog').dialog({width:340,autoOpen:false,draggable:false,modal:true,resizable:false});
    // 点击编辑按钮：隐藏只读文本，显示编辑输入框和保存/取消按钮
    $('.edit-toggle-btn').click(function(){
        $(this).closest('tr').find('.view-text').hide();
        $(this).closest('tr').find('.edit-input').show();
        $(this).hide();
        $(this).closest('tr').find('.save-btn').show();
        $(this).closest('tr').find('.cancel-btn').show();
    });
    // 点击取消按钮：恢复只读显示，隐藏编辑输入框
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
