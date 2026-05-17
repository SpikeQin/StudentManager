package servlet;

import util.SecurityUtil;
import dao.ScoreD;
import util.LogUtil;
import vo.Teacher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * 更新成绩 Servlet — 教师功能
 *
 * <p>修改学生四科成绩，含服务端分数验证（0-100 范围、必须为数字）。
 * <br>仅教师可操作。
 */
@WebServlet("/update_score")
public class update_score extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(update_score.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        Object info = session.getAttribute("info");
        if (!(info instanceof Teacher)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // CSRF Token 校验
        String csrfParam = request.getParameter("csrf_token");
        if (!SecurityUtil.validateCsrfToken(session, csrfParam)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF 验证失败");
            return;
        }

        ScoreD scoreD = new ScoreD();
        String id = request.getParameter("stuno");
        String dataStructure = request.getParameter("dataStructure");
        String operatingSystem = request.getParameter("operatingSystem");
        String computerNetwork = request.getParameter("computerNetwork");
        String computerOrganization = request.getParameter("computerOrganization");

        if (id == null || id.trim().isEmpty()) {
            out.print("<script>alert(\"没有接收到数据！\");window.location.href='"
                    + request.getContextPath() + "/one_page_score';</script>");
            return;
        }

        try {
            // 服务端分数验证
            String errorMsg = validateScore(dataStructure, "数据结构")
                    + validateScore(operatingSystem, "操作系统")
                    + validateScore(computerNetwork, "计算机网络")
                    + validateScore(computerOrganization, "计算机组成原理");
            if (!errorMsg.isEmpty()) {
                out.print("<script>alert(\"" + errorMsg + "\");window.location.href='"
                        + request.getContextPath() + "/one_page_score';</script>");
                return;
            }

            scoreD.updateScoreInfo(id, dataStructure, operatingSystem, computerNetwork, computerOrganization);

            String operator = ((Teacher) info).getId();
            LogUtil.log(operator, "teacher", "修改成绩", "学号:" + id);
            response.sendRedirect(request.getContextPath() + "/one_page_score");
        } catch (Exception e) {
            LOG.severe("更新成绩失败: " + e.getMessage());
            out.print("<script>alert(\"保存失败，请稍后重试\");window.location.href='"
                    + request.getContextPath() + "/one_page_score';</script>");
        }
    }

    /**
     * 验证单个成绩字段
     * @param score       成绩字符串
     * @param subjectName 科目名称（用于错误提示）
     * @return 通过返回空串，失败返回错误提示
     */
    private String validateScore(String score, String subjectName) {
        if (score == null || score.trim().isEmpty()) return "";
        try {
            int s = Integer.parseInt(score.trim());
            if (s < 0 || s > 100) return subjectName + "成绩必须在0-100之间！";
        } catch (NumberFormatException e) {
            return subjectName + "成绩必须为数字！";
        }
        return "";
    }
}
