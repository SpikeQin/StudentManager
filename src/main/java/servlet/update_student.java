package servlet;

import util.SecurityUtil;
import dao.StudentD;
import util.LogUtil;
import vo.Teacher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * 更新学生信息 Servlet — 教师功能
 *
 * <p>修改学生姓名、性别、专业（行内编辑）。
 * <br>仅教师可操作。
 */
@WebServlet("/update_student")
public class update_student extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(update_student.class.getName());

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

        String stuno = request.getParameter("stuno");
        String stuname = request.getParameter("stuname");
        String stusex = request.getParameter("stusex");
        String stumajor = request.getParameter("stumajor");

        if (stuno == null || stuno.trim().isEmpty()) {
            out.print("<script>alert(\"学号不能为空！\");window.location.href='"
                    + request.getContextPath() + "/one_page_student';</script>");
            return;
        }

        try {
            StudentD studentD = new StudentD();
            studentD.updateStudentInfo(stuno, stuname, stusex, stumajor);

            String operator = ((Teacher) info).getId();
            LogUtil.log(operator, "teacher", "修改学生信息", "学号:" + stuno + " 姓名:" + stuname);
            response.sendRedirect(request.getContextPath() + "/one_page_student");
        } catch (Exception e) {
            LOG.severe("更新学生信息失败: " + e.getMessage());
            out.print("<script>alert(\"保存失败！\");window.location.href='"
                    + request.getContextPath() + "/one_page_student';</script>");
        }
    }
}
