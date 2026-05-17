package servlet;

import util.SecurityUtil;
import dao.AnnouncementD;
import util.LogUtil;
import vo.Teacher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * 公告管理 Servlet — 教师 / 管理员功能
 *
 * <p>POST 处理发布（action=add）和删除（action=delete）。
 * <br>GET 重定向到公告列表页，不再处理任何写操作。
 */
@WebServlet("/announcement_manage")
public class announcement_manage extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(announcement_manage.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        String action = req.getParameter("action");

        // 身份验证
        String operator = getOperator(req);
        if (operator == null) {
            out.print("<script>alert(\"请先登录\");location.href='" + req.getContextPath() + "/login.jsp';</script>");
            return;
        }

        // CSRF Token 校验
        String csrfParam = req.getParameter("csrf_token");
        if (!SecurityUtil.validateCsrfToken(req.getSession(), csrfParam)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF 验证失败");
            return;
        }

        try {
            AnnouncementD ad = new AnnouncementD();
            if ("add".equals(action)) {
                String title = req.getParameter("title");
                String content = req.getParameter("content");
                if (title != null && !title.trim().isEmpty() && content != null && !content.trim().isEmpty()) {
                    ad.insertAnnouncement(title.trim(), content.trim(), operator);
                    LogUtil.log(operator, "teacher", "发布公告", title);
                }
                resp.sendRedirect(req.getContextPath() + "/teacher/announcements.jsp");
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                ad.deleteAnnouncement(id);
                resp.sendRedirect(req.getContextPath() + "/teacher/announcements.jsp");
            } else {
                resp.sendRedirect(req.getContextPath() + "/teacher/announcements.jsp");
            }
        } catch (Exception e) {
            LOG.severe("公告操作失败: " + e.getMessage());
            out.print("<script>alert(\"操作失败\");location.href='" + req.getContextPath() + "/teacher/announcements.jsp';</script>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/teacher/announcements.jsp");
    }

    /** 从 Session 获取操作人姓名 */
    private String getOperator(HttpServletRequest req) {
        Object info = req.getSession().getAttribute("info");
        if (info instanceof Teacher) return ((Teacher) info).getName();
        return null;
    }
}
