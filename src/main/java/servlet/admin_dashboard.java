package servlet;

import util.SecurityUtil;
import dao.TeacherD;
import dao.LogD;
import util.LogUtil;
import vo.OperationLog;
import vo.Teacher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * 管理员后台 Servlet
 *
 * <p>GET 根据 tab 参数分发页面：
 * <ul>
 *   <li>tab=logs      → 操作日志（分页，每页 20 条）</li>
 *   <li>tab=teachers  → 教师列表</li>
 * </ul>
 * POST 处理教师增删改操作。
 * <br>仅 role=admin 的教师可访问。
 */
@WebServlet("/admin_dashboard")
public class admin_dashboard extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(admin_dashboard.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 管理员身份验证
        HttpSession session = req.getSession();
        Object info = session.getAttribute("info");
        if (!(info instanceof Teacher) || !"admin".equals(((Teacher) info).getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String tab = req.getParameter("tab");
        if ("logs".equals(tab)) {
            showLogs(req, resp);
        } else if ("teachers".equals(tab)) {
            showTeachers(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin_dashboard?tab=logs");
        }
    }

    /** 展示操作日志（分页） */
    private void showLogs(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            LogD ld = new LogD();
            int page;
            try {
                String index = req.getParameter("index");
                page = (index == null) ? 1 : Integer.parseInt(index);
            } catch (NumberFormatException e) {
                page = 1;
            }
            int size = 20;
            ArrayList<OperationLog> logs = ld.getOnePage(page, size);
            int count = ld.getLogCount();
            int sumIndex = count % size == 0 ? count / size : count / size + 1;
            req.setAttribute("logList", logs);
            req.setAttribute("logSumIndex", sumIndex);
            req.getRequestDispatcher("/admin/logs.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/admin_dashboard?tab=logs");
        }
    }

    /** 展示教师列表 */
    private void showTeachers(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            TeacherD td = new TeacherD();
            ArrayList<Teacher> teachers = new ArrayList<>(td.findAll());
            req.setAttribute("teachers", teachers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        req.getRequestDispatcher("/admin/teachers.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 管理员身份验证
        HttpSession session = req.getSession();
        Object info = session.getAttribute("info");
        if (!(info instanceof Teacher) || !"admin".equals(((Teacher) info).getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // CSRF Token 校验
        String csrfParam = req.getParameter("csrf_token");
        if (!SecurityUtil.validateCsrfToken(session, csrfParam)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF 验证失败");
            return;
        }

        String action = req.getParameter("action");
        try {
            TeacherD td = new TeacherD();
            if ("addTeacher".equals(action)) {
                String id = req.getParameter("id");
                String name = req.getParameter("name");
                String sex = req.getParameter("sex");
                String password = req.getParameter("password");
                String email = req.getParameter("email");
                if (id != null && password != null) {
                    td.insertTeacher(id, password, name, sex, email);
                    LogUtil.log(((Teacher) info).getId(), "admin", "添加教师", id + "/" + name);
                }
                resp.sendRedirect(req.getContextPath() + "/admin_dashboard?tab=teachers");
            } else if ("editTeacher".equals(action)) {
                String id = req.getParameter("id");
                String name = req.getParameter("name");
                String sex = req.getParameter("sex");
                String email = req.getParameter("email");
                if (id != null) {
                    td.updateTeacher(id, name, sex, email, null);
                    LogUtil.log(((Teacher) info).getId(), "admin", "编辑教师", id);
                }
                resp.sendRedirect(req.getContextPath() + "/admin_dashboard?tab=teachers");
            } else if ("deleteTeacher".equals(action)) {
                String id = req.getParameter("id");
                if (id != null && !id.isEmpty()) {
                    td.deleteTeacher(id);
                    LogUtil.log(((Teacher) info).getId(), "admin", "删除教师", id);
                }
                resp.sendRedirect(req.getContextPath() + "/admin_dashboard?tab=teachers");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin_dashboard?tab=logs");
            }
        } catch (Exception e) {
            LOG.severe("管理员操作失败: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin_dashboard?tab=logs");
        }
    }
}
