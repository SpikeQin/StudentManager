<%--
  成绩 Excel 导出 — 教师端

  联表查询全部成绩，以 .xls 格式流式输出触发浏览器下载。
--%>
<%@ page import="dao.ScoreD" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page contentType="application/msexcel" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>成绩导出</title>
</head>
<body>
<%-- 身份校验：未登录或非教师角色拒绝导出 --%>
<%
    Object info = session.getAttribute("info");
    if (!(info instanceof vo.Teacher)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "需要教师权限");
        return;
    }
%>
<%-- 设置响应头，使浏览器以附件形式下载 Excel 文件 --%>
<%
    out.clearBuffer();
    response.setHeader("Content-Disposition", "attachment;filename=excel.xls");

    /** 导出数据量上限常量 */
    int EXPORT_LIMIT = 10000;
%>
<%-- 成绩数据表格：以 HTML 表格形式导出为 xls，Excel 可识别 --%>
<table align="center" border="1">
    <tr>
        <th height="35">学号</th>
        <th>姓名</th>
        <th>专业</th>
        <th>数据结构</th>
        <th>操作系统</th>
        <th>计算机网络</th>
        <th>计算机组成原理</th>
    </tr>
    <%-- 查询所有学生成绩数据（最多10000条），遍历输出到表格 --%>
    <%
        try {
            ScoreD scoD = new ScoreD();
            ArrayList<ScoreD.ScoreWithStudent> stus = scoD.getOnePageWithStudent(1, EXPORT_LIMIT);
            for (ScoreD.ScoreWithStudent stu : stus) {
                String name = stu.getStudentName();
                String major = stu.getStudentMajor();
    %>
    <tr>
            <td align="center"><%=util.SecurityUtil.escapeHtml(stu.getId())%></td>
            <td align="center"><%=util.SecurityUtil.escapeHtml(name)%></td>
            <td align="center"><%=util.SecurityUtil.escapeHtml(major)%></td>
            <td align="center"><%=util.SecurityUtil.escapeHtml(stu.getDataStructure() != null ? stu.getDataStructure() : "")%></td>
            <td align="center"><%=util.SecurityUtil.escapeHtml(stu.getOperatingSystem() != null ? stu.getOperatingSystem() : "")%></td>
            <td align="center"><%=util.SecurityUtil.escapeHtml(stu.getComputerNetwork() != null ? stu.getComputerNetwork() : "")%></td>
            <td align="center"><%=util.SecurityUtil.escapeHtml(stu.getComputerOrganization() != null ? stu.getComputerOrganization() : "")%></td>
    </tr>
    <%
            }
        } catch (Exception e) {
            Logger.getLogger("score_excel").log(Level.WARNING, "Excel export failed", e);
        }
    %>
</table>
</body>
</html>
