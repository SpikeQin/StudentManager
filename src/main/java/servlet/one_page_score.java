package servlet;

import dao.ScoreD;
import dao.ScoreD.ScoreWithStudent;
import vo.Teacher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 成绩分页查询 Servlet — 教师功能
 *
 * <p>无 id 参数 → 分页展示全部（联表查询姓名和专业）
 * <br>有 id 参数 → 按学号精确查找
 * <br>仅教师可操作。
 */
@WebServlet("/one_page_score")
public class one_page_score extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(one_page_score.class.getName());

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

        // 权限校验
        Object info = session.getAttribute("info");
        if (!(info instanceof Teacher)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String key = request.getParameter("id");

        if (key == null) {
            // 分页查询全部
            int size = 10;
            String index = request.getParameter("index");
            if (index == null) index = "1";
            int currentIndex;
            try {
                currentIndex = Integer.parseInt(index);
            } catch (NumberFormatException e) {
                currentIndex = 1;
            }
            try {
                ScoreD scoD = new ScoreD();
                int count = scoD.getScoreCount();
                ArrayList<ScoreWithStudent> stus = scoD.getOnePageWithStudent(currentIndex, size);
                int sumIndex = count % size == 0 ? count / size : count / size + 1;

                session.setAttribute("onePageScore", stus);
                session.setAttribute("sumScoreIndex", sumIndex);
                session.setAttribute("singleStudentView", false);
                response.sendRedirect(request.getContextPath() + "/teacher/score.jsp");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "成绩查询失败", e);
                out.print("<script>alert(\"查询失败，请重试\");window.location.href='"
                        + request.getContextPath() + "/teacher/main.jsp';</script>");
            }
        } else {
            // 按学号精确查找
            ScoreD scoreD = new ScoreD();
            try {
                ScoreWithStudent score = scoreD.findWithIdWithStudent(key);
                ArrayList<ScoreWithStudent> scores = new ArrayList<>();
                if (score != null) scores.add(score);
                session.setAttribute("onePageScore", scores);
                session.setAttribute("sumScoreIndex", 1);
                session.setAttribute("singleStudentView", true);
                response.sendRedirect(request.getContextPath() + "/teacher/score.jsp");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "成绩查询失败", e);
                out.print("<script>alert(\"查询失败\");window.location.href='"
                        + request.getContextPath() + "/teacher/main.jsp';</script>");
            }
        }
    }
}
