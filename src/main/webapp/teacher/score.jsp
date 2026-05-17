<%--
  成绩管理 — 教师端

  联表分页展示 + 行内编辑四科成绩 + Excel 导出入口。
--%>
<%@ page import="vo.Teacher" %>
<%@ page import="dao.ScoreD" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>成绩管理</title>
    <link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet"/>
    <script src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.js"></script>
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
    ArrayList<ScoreD.ScoreWithStudent> stus = (ArrayList<ScoreD.ScoreWithStudent>) session.getAttribute("onePageScore");
    int sumIndex = (int) session.getAttribute("sumScoreIndex");
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
                <li><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/one_page_score">成绩管理</a></li>
                <li><a href="${pageContext.request.contextPath}/score_statistics">统计看板</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/announcements.jsp">公告管理</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出</a></li>
        <%-- 普通教师菜单 --%>
        <% } else { %>
                <li><a href="${pageContext.request.contextPath}/teacher/personal.jsp">个人信息</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/one_page_score">成绩管理</a></li>
                <li><a href="${pageContext.request.contextPath}/score_statistics">统计看板</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/announcements.jsp">公告管理</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出</a></li>
        <% } %>
            </ul>
        </div>
    </div>
    <div id="main">
        <div class="top">
            <h2>学生成绩管理</h2>
            <hr/>
        </div>
        <%
            Boolean singleStudentView = (Boolean) session.getAttribute("singleStudentView");
            if (singleStudentView == null || !singleStudentView) {
        %>
        <%-- 导出Excel按钮：仅在查看全部学生时显示 --%>
        <input type="button" class="btn-add" onclick="location.href='score_excel.jsp';" value="导出EXCEL">
        <%
            }
        %>
        <%-- 成绩表格：展示学号、姓名、专业及四门课程成绩 --%>
        <div class="table">
            <table id="table">
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
                    for (ScoreD.ScoreWithStudent score : stus) {
                        String formId = "form_score_" + score.getId();
                %>
                        <tr>
                            <td><%=util.SecurityUtil.escapeHtml(score.getId())%></td>
                            <td><%=util.SecurityUtil.escapeHtml(score.getStudentName() != null ? score.getStudentName() : "")%></td>
                            <td><%=util.SecurityUtil.escapeHtml(score.getStudentMajor() != null ? score.getStudentMajor() : "")%></td>
                            <td>
                                <span class="view-text"><%=util.SecurityUtil.escapeHtml(score.getDataStructure() != null ? score.getDataStructure() : "")%></span>
                                <input value="<%=util.SecurityUtil.escapeHtml(score.getDataStructure() != null ? score.getDataStructure() : "")%>" name="dataStructure" form="<%=util.SecurityUtil.escapeHtml(formId)%>" class="table-input edit-input" style="display:none">
                            </td>
                            <td>
                                <span class="view-text"><%=util.SecurityUtil.escapeHtml(score.getOperatingSystem() != null ? score.getOperatingSystem() : "")%></span>
                                <input value="<%=util.SecurityUtil.escapeHtml(score.getOperatingSystem() != null ? score.getOperatingSystem() : "")%>" name="operatingSystem" form="<%=util.SecurityUtil.escapeHtml(formId)%>" class="table-input edit-input" style="display:none">
                            </td>
                            <td>
                                <span class="view-text"><%=util.SecurityUtil.escapeHtml(score.getComputerNetwork() != null ? score.getComputerNetwork() : "")%></span>
                                <input value="<%=util.SecurityUtil.escapeHtml(score.getComputerNetwork() != null ? score.getComputerNetwork() : "")%>" name="computerNetwork" form="<%=util.SecurityUtil.escapeHtml(formId)%>" class="table-input edit-input" style="display:none">
                            </td>
                            <td>
                                <span class="view-text"><%=util.SecurityUtil.escapeHtml(score.getComputerOrganization() != null ? score.getComputerOrganization() : "")%></span>
                                <input value="<%=util.SecurityUtil.escapeHtml(score.getComputerOrganization() != null ? score.getComputerOrganization() : "")%>" name="computerOrganization" form="<%=util.SecurityUtil.escapeHtml(formId)%>" class="table-input edit-input" style="display:none">
                            </td>
                            <td class="action-cell">
                                <form id="<%=util.SecurityUtil.escapeHtml(formId)%>" method="post" action="${pageContext.request.contextPath}/update_score" style="display:none">
                                    <input type="hidden" name="csrf_token" value="<%=(String)session.getAttribute("csrf_token")%>">
                                    <input value="<%=util.SecurityUtil.escapeHtml(score.getId())%>" name="stuno" type="hidden">
                                </form>
                                <button type="button" class="edit-toggle-btn">编辑</button>
                                <input type="submit" class="save-btn" value="保存" form="<%=util.SecurityUtil.escapeHtml(formId)%>" style="display:none">
                                <button type="button" class="cancel-btn" style="display:none">取消</button>
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
            <a href="${pageContext.request.contextPath}/one_page_score?index=1">首页</a>
            <% } %>
            <%
                for (int i = 1; i <= sumIndex; i++) {
            %>
            <a href="${pageContext.request.contextPath}/one_page_score?index=<%=i%>">第<%=i%>页</a>
            <%
                }
            %>
            <% if (sumIndex > 1) { %>
            <a href="${pageContext.request.contextPath}/one_page_score?index=<%=sumIndex%>">末页</a>
            <% } %>
        </div>
    </div>
</div>
<script>
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
</body>
</html>
