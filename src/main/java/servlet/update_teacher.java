package servlet;

import dao.TeacherD;
import vo.Teacher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 教师信息更新 Servlet — 个人信息页整表提交版本
 *
 * <p>一次性更新教师姓名、性别、邮箱和密码（密码可选），
 * <br>含姓名非空验证和邮箱格式验证。
 */
@WebServlet("/update_teacher")
public class update_teacher extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(update_teacher.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        request.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        TeacherD teacherD = new TeacherD();

        String uid = request.getParameter("uid");
        String name = request.getParameter("name");
        String sex = request.getParameter("sex");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // 姓名非空验证
        if (name == null || name.trim().isEmpty()) {
            out.print("<script>alert(\"姓名不能为空！\");location.href='"
                    + request.getContextPath() + "/teacher/personal.jsp';</script>");
            return;
        }
        // 邮箱格式验证
        if (email != null && !email.trim().isEmpty()
                && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            out.print("<script>alert(\"邮箱格式无效！\");location.href='"
                    + request.getContextPath() + "/teacher/personal.jsp';</script>");
            return;
        }

        try {
            Teacher teacher = teacherD.updateTeacher(uid, name, sex, email, password);
            session.setAttribute("info", teacher);
            out.print("<script>alert(\"保存成功！\");location.href='"
                    + request.getContextPath() + "/teacher/personal.jsp';</script>");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "更新教师失败", e);
            out.print("<script>alert(\"保存失败！\");location.href='"
                    + request.getContextPath() + "/teacher/personal.jsp';</script>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {}
}
