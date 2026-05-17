package servlet;

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
 * 删除学生 Servlet — 教师功能
 *
 * <p>删除学生及其成绩记录（级联删除），仅处理 POST。
 * <br>仅教师可操作。
 */
@WebServlet("/delete_student")
public class delete_student extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(delete_student.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        // 权限校验
        HttpSession session = request.getSession();
        Object info = session.getAttribute("info");
        if (!(info instanceof Teacher)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String id = request.getParameter("id");
        if (id == null || id.trim().isEmpty()) {
            out.print("<script>alert(\"学号不能为空！\");window.location.href='"
                    + request.getContextPath() + "/one_page_student';</script>");
            return;
        }

        try {
            StudentD studentD = new StudentD();
            ScoreD scoreD = new ScoreD();
            studentD.deleteStudent(id);
            scoreD.deleteScore(id);

            String operator = ((Teacher) info).getId();
            LogUtil.log(operator, "teacher", "删除学生", "学号:" + id);
            response.sendRedirect(request.getContextPath() + "/one_page_student");
        } catch (Exception e) {
            LOG.severe("删除学生失败: " + e.getMessage());
            out.print("<script>alert(\"删除失败！\");window.location.href='"
                    + request.getContextPath() + "/one_page_student';</script>");
        }
    }
}
