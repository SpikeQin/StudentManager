package servlet;

import dao.StudentD;
import vo.Student;
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
 * 学生分页查询 Servlet — 教师功能
 *
 * <p>无关键词 → 分页展示（每页 10 条）
 * <br>有关键词 → 先模糊搜索姓名，找不到再精确查学号
 * <br>仅教师可操作。
 */
@WebServlet("/one_page_student")
public class one_page_student extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(one_page_student.class.getName());

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

        String key = request.getParameter("key");

        try {
            if (key == null || key.trim().isEmpty()) {
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

                StudentD sdao = new StudentD();
                ArrayList<Student> stus = sdao.getOnePage(currentIndex, size);
                int count = sdao.getStudentCount();
                int sumIndex = count % size == 0 ? count / size : count / size + 1;

                session.setAttribute("onePageStudent", stus);
                session.setAttribute("sumIndex", sumIndex);
                session.setAttribute("singleStudentView", false);
                response.sendRedirect(request.getContextPath() + "/teacher/main.jsp");
            } else {
                // 模糊搜索 + 精确查找
                StudentD studentD = new StudentD();
                ArrayList<Student> students = studentD.findWithName(key);
                if (students.isEmpty()) {
                    Student student = studentD.findWithId(key);
                    if (student != null) students.add(student);
                }
                session.setAttribute("onePageStudent", students);
                session.setAttribute("sumIndex", 1);
                session.setAttribute("singleStudentView", students.size() == 1);
                response.sendRedirect(request.getContextPath() + "/teacher/main.jsp");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "学生查询失败", e);
            out.print("<script>alert(\"查询失败，请重试\");window.location.href='"
                    + request.getContextPath() + "/teacher/main.jsp';</script>");
        }
    }
}
