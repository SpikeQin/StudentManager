package servlet;

import util.SecurityUtil;
import dao.TeacherD;
import vo.Teacher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * 教师个人信息更新 Servlet — 教师功能
 *
 * <p>修改本人的邮箱和密码（两者均为可选更新）。
 * <br>从 session 获取当前用户 ID，防止越权修改他人。
 * <br>仅教师可操作。
 */
@WebServlet("/teacher_update")
public class teacher_update extends HttpServlet {

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
        if (!(info instanceof Teacher)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Teacher teacher = (Teacher) info;
        String id = teacher.getId();
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            TeacherD td = new TeacherD();

            if (email != null && !email.trim().isEmpty()) {
                td.updateEmail(id, email);
                teacher.setEmail(email);
            }
            if (password != null && !password.trim().isEmpty()) {
                td.updatePassword(id, password);
            }

            session.setAttribute("info", teacher);
            response.sendRedirect(request.getContextPath() + "/teacher/personal.jsp");
        } catch (Exception e) {
            Logger.getLogger(teacher_update.class.getName()).severe("更新失败: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/teacher/personal.jsp");
        }
    }
}
