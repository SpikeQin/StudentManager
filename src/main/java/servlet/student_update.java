package servlet;

import util.SecurityUtil;
import dao.StudentD;
import vo.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * 学生个人信息更新 Servlet — 学生功能
 *
 * <p>修改本人的邮箱和密码（两者均为可选更新）。
 * <br>从 session 获取当前用户 ID，防止越权修改他人。
 * <br>仅学生可操作。
 */
@WebServlet("/student_update")
public class student_update extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object info = session.getAttribute("info");

        // CSRF Token 校验
        String csrfParam = request.getParameter("csrf_token");
        if (!SecurityUtil.validateCsrfToken(session, csrfParam)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF 验证失败");
            return;
        }

        // 权限校验
        if (!(info instanceof Student)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Student student = (Student) info;
        String id = student.getId();
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            StudentD sd = new StudentD();

            if (email != null && !email.trim().isEmpty()) {
                sd.updateEmail(id, email);
                student.setEmail(email);
            }
            if (password != null && !password.trim().isEmpty()) {
                sd.updatePassword(id, password);
            }

            session.setAttribute("info", student);
            response.sendRedirect(request.getContextPath() + "/student/personal.jsp");
        } catch (Exception e) {
            Logger.getLogger(student_update.class.getName()).severe("更新失败: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/student/personal.jsp");
        }
    }
}
