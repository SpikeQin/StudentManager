package servlet;

import util.SecurityUtil;
import dao.ScoreD;
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
 * 添加学生 Servlet — 教师功能
 *
 * <p>添加学生并同步创建空成绩记录，初始密码为 6 位随机数（非学号，防弱密码猜测）。
 * <br>仅教师可操作。
 */
@WebServlet("/add_student")
public class add_student extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(add_student.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        // 权限校验
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

        StudentD studentD = new StudentD();
        ScoreD scoreD = new ScoreD();

        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String sex = request.getParameter("sex");
        String major = request.getParameter("major");
        String school_date = request.getParameter("school_date");

        // 参数验证
        if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            out.print("<script>alert(\"学号和姓名不能为空！\");window.location.href='" + request.getContextPath() + "/one_page_student';</script>");
            return;
        }

        try {
            // 生成 6 位随机初始密码
            String defaultPassword = String.format("%06d", new java.security.SecureRandom().nextInt(1000000));
            studentD.insertStudent(id, defaultPassword, name, sex, school_date, major, "");
            scoreD.insertScore(id);

            String operator = ((Teacher) info).getId();
            LogUtil.log(operator, "teacher", "添加学生", "学号:" + id + " 姓名:" + name);
            response.sendRedirect(request.getContextPath() + "/one_page_student");
        } catch (Exception e) {
            LOG.severe("添加学生失败: " + e.getMessage());
            out.print("<script>alert(\"添加失败，学号可能已存在！\");window.location.href='" + request.getContextPath() + "/one_page_student';</script>");
        }
    }
}
