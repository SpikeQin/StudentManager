<%--
  成绩统计看板 — 教师 / 管理员

  Chart.js 柱状图展示四科统计指标（最高/最低/平均分、及格率、优秀率、五段分布）。
--%>
<%@ page import="vo.Teacher, servlet.score_statistics.SubjectStat, java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- 身份校验：未登录或非教师角色重定向到登录页；若无统计数据则请求数据 --%>
<%
    Object info = session.getAttribute("info");
    if (!(info instanceof Teacher)) {
        response.sendRedirect("${pageContext.request.contextPath}/login.jsp");
        return;
    }
    Teacher t = (Teacher) info;
    ArrayList<SubjectStat> statList = (ArrayList<SubjectStat>) request.getAttribute("statList");
    Integer totalStudents = (Integer) request.getAttribute("totalStudents");
    if (statList == null) {
        response.sendRedirect("${pageContext.request.contextPath}/score_statistics");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>成绩统计看板</title>
    <link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet"/>
    <%-- 统计看板样式：网格布局、卡片、分布柱状图、图例 --%>
    <style>
        .stat-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:20px;margin-top:20px}
        .stat-card{border:1px solid #e2e6ea;border-radius:6px;padding:20px;background:#fff}
        .stat-card h3{margin:0 0 12px 0;font-size:15px;color:#1e3a5f;border-bottom:1px solid #eef1f5;padding-bottom:8px}
        .stat-row{display:flex;justify-content:space-between;padding:4px 0;font-size:13px}
        .stat-row .label{color:#7b8c9d}
        .stat-row .value{color:#2c3e50;font-weight:500}
        .bar-container{height:16px;background:#eef1f5;border-radius:8px;overflow:hidden;margin:3px 0;display:flex}
        .bar-seg{height:100%;min-width:2px;transition:width 0.3s}
        .bar-90{background:#27ae60}.bar-80{background:#2ecc71}.bar-70{background:#f39c12}.bar-60{background:#e67e22}.bar-fail{background:#e74c3c}
        .legend{display:flex;flex-wrap:wrap;gap:10px;margin-top:8px;font-size:12px;color:#5a6c7d}
        .legend-item{display:flex;align-items:center;gap:4px}
        .legend-dot{width:10px;height:10px;border-radius:2px;display:inline-block}
        .legend-dot.green{background:#27ae60}.legend-dot.light-green{background:#2ecc71}
        .legend-dot.orange{background:#f39c12}.legend-dot.dark-orange{background:#e67e22}.legend-dot.red{background:#e74c3c}
    </style>
</head>
<body>

<div id="page" class="container">
    <div id="header">
        <div id="logo"><img src="${pageContext.request.contextPath}/image?id=<%=util.SecurityUtil.escapeHtml(t.getId())%>"/><h1><%=util.SecurityUtil.escapeHtml(t.getName())%></h1></div>
        <%-- 导航菜单 --%>
        <div id="menu">
            <ul>
            <%-- 管理员菜单 --%>
            <% if ("admin".equals(t.getRole())) { %>
                <li><a href="${pageContext.request.contextPath}/admin_dashboard?tab=logs">操作日志</a></li>
                <li><a href="${pageContext.request.contextPath}/admin_dashboard?tab=teachers">教师管理</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_score">成绩管理</a></li>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/score_statistics">统计看板</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/announcements.jsp">公告管理</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出</a></li>
            <%-- 普通教师菜单 --%>
            <% } else { %>
                <li><a href="${pageContext.request.contextPath}/teacher/personal.jsp">个人信息</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_student">学生管理</a></li>
                <li><a href="${pageContext.request.contextPath}/one_page_score">成绩管理</a></li>
                <li class="current_page_item"><a href="${pageContext.request.contextPath}/score_statistics">统计看板</a></li>
                <li><a href="${pageContext.request.contextPath}/teacher/announcements.jsp">公告管理</a></li>
                <li><a onclick="return confirm('确认退出?');" href="${pageContext.request.contextPath}/exit">退出</a></li>
            <% } %>
            </ul>
        </div>
    </div>
    <div id="main">
        <div class="top"><h2>成绩统计看板</h2></div>
        <%-- 统计数据展示区域 --%>
        <% if (statList != null) { %>
            <p style="color:#7b8c9d;margin:8px 0;">学生总数：<%=totalStudents != null ? totalStudents : 0%> 人</p>
            <div class="stat-grid">
            <%-- 遍历各科目统计数据，每个科目渲染为一个卡片 --%>
            <% for (SubjectStat s : statList) { %>
                <div class="stat-card">
                    <h3><%=util.SecurityUtil.escapeHtml(s.name)%></h3>
                    <div class="stat-row"><span class="label">参考人数</span><span class="value"><%=s.count%></span></div>
                    <div class="stat-row"><span class="label">平均分</span><span class="value"><%=s.avg%></span></div>
                    <div class="stat-row"><span class="label">最高分</span><span class="value"><%=s.max%></span></div>
                    <div class="stat-row"><span class="label">最低分</span><span class="value"><%=s.min == Integer.MAX_VALUE ? "-" : s.min%></span></div>
                    <div class="stat-row"><span class="label">及格率</span><span class="value"><%=s.count > 0 ? Math.round(s.passCount * 100.0 / s.count) : 0%>%</span></div>
                    <div class="stat-row"><span class="label">优秀率(>=90)</span><span class="value"><%=s.count > 0 ? Math.round(s.excellentCount * 100.0 / s.count) : 0%>%</span></div>
                    <div style="margin-top:12px;font-size:12px;color:#7b8c9d;">分数段分布</div>
                    <%-- 分数段分布柱状图：计算最大分段值以等比缩放各段宽度 --%>
                    <% int maxSeg = 1; for (int v : s.distribution) if (v > maxSeg) maxSeg = v; %>
                    <div class="bar-container">
                        <div class="bar-seg bar-fail" style="width:<%=s.distribution[0] * 100.0 / maxSeg%>%"></div>
                        <div class="bar-seg bar-60" style="width:<%=s.distribution[1] * 100.0 / maxSeg%>%"></div>
                        <div class="bar-seg bar-70" style="width:<%=s.distribution[2] * 100.0 / maxSeg%>%"></div>
                        <div class="bar-seg bar-80" style="width:<%=s.distribution[3] * 100.0 / maxSeg%>%"></div>
                        <div class="bar-seg bar-90" style="width:<%=s.distribution[4] * 100.0 / maxSeg%>%"></div>
                    </div>
                    <%-- 图例：各分数段的颜色标识和人数 --%>
                    <div class="legend">
                        <span class="legend-item"><span class="legend-dot red"></span>&lt;60: <%=s.distribution[0]%></span>
                        <span class="legend-item"><span class="legend-dot dark-orange"></span>60-69: <%=s.distribution[1]%></span>
                        <span class="legend-item"><span class="legend-dot orange"></span>70-79: <%=s.distribution[2]%></span>
                        <span class="legend-item"><span class="legend-dot light-green"></span>80-89: <%=s.distribution[3]%></span>
                        <span class="legend-item"><span class="legend-dot green"></span>90-100: <%=s.distribution[4]%></span>
                    </div>
                </div>
            <% } %>
            </div>
        <% } %>
    </div>
</div>
</body>
</html>
