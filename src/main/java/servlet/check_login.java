package servlet;

import dao.StudentD;
import dao.TeacherD;
import util.LogUtil;
import vo.Student;
import vo.Teacher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 统一登录 Servlet
 *
 * <p>处理教师 / 学生 / 管理员的登录请求：
 * <ol>
 *   <li>验证验证码（一次性）</li>
 *   <li>先匹配教师表 → 再匹配学生表</li>
 *   <li>管理员（role=admin）跳转后台，普通教师跳转学生管理</li>
 *   <li>支持「记住我」Cookie（7 天，HttpOnly）</li>
 *   <li>登录成功执行 changeSessionId（防 Session Fixation）</li>
 * </ol>
 */
@WebServlet("/check_login")
public class check_login extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(check_login.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        String user = request.getParameter("user");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");
        String code = request.getParameter("code");

        // 1. 参数校验
        if (user == null || user.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=empty");
            return;
        }

        // 2. 验证码校验
        String randStr = (String) session.getAttribute("randStr");
        if (code == null || code.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=code_empty");
            return;
        }
        if (randStr == null || !code.equalsIgnoreCase(randStr)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=code_wrong");
            return;
        }
        session.removeAttribute("randStr"); // 一次性使用

        TeacherD teacherD = new TeacherD();
        StudentD studentD = new StudentD();

        try {
            // 3. 先尝试教师身份
            Teacher teacher = teacherD.checkAccount(user, password);
            if (teacher != null) {
                request.changeSessionId(); // 防 Session Fixation
                session = request.getSession();
                session.setAttribute("info", teacher);
                if (remember != null) setRememberCookie(response, user);
                LogUtil.log(user, "teacher", "登录系统", "教师登录");
                if ("admin".equals(teacher.getRole())) {
                    response.sendRedirect(request.getContextPath() + "/admin_dashboard?tab=logs");
                } else {
                    response.sendRedirect(request.getContextPath() + "/one_page_student");
                }
                return;
            }

            // 4. 再尝试学生身份
            Student student = studentD.checkAccount(user, password);
            if (student != null) {
                request.changeSessionId();
                session = request.getSession();
                session.setAttribute("info", student);
                if (remember != null) setRememberCookie(response, user);
                LogUtil.log(user, "student", "登录系统", "学生登录");
                response.sendRedirect(request.getContextPath() + "/student/main.jsp");
                return;
            }

            // 5. 验证失败
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=credential");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "登录失败", e);
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=system");
        }
    }

    /**
     * 设置「记住我」Cookie（7 天有效，HttpOnly）
     */
    private void setRememberCookie(HttpServletResponse response, String user) {
        Cookie c = new Cookie("name", user);
        c.setMaxAge(60 * 60 * 24 * 7);
        c.setPath("/");
        c.setHttpOnly(true);
        response.addCookie(c);
    }
}
