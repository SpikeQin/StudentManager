package servlet;

import util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.logging.Logger;

/**
 * 通用头像上传 Servlet — 学生 / 教师共用
 *
 * <p>调用方式：
 * <pre>
 *   POST /upload_image?type=student&id={学号}
 *   POST /upload_image?type=teacher&id={教师号}
 * </pre>
 *
 * <p>安全措施：
 * <ul>
 *   <li>CSRF Token 校验</li>
 *   <li>IDOR 防护（校验 session 用户 ID 与请求参数一致）</li>
 *   <li>文件 magic bytes 校验（防伪装 Content-Type）</li>
 *   <li>最大 10MB</li>
 *   <li>仅允许 JPEG / PNG / GIF</li>
 * </ul>
 *
 * <p>存储路径：<code>{user.home}/StudentManager/userImg/{id}.jpeg</code>
 * <br>读取路径：<code>/image?id={id}</code>（由 ImageServlet 处理）
 */
@WebServlet("/upload_image")
@MultipartConfig(maxFileSize = 10_485_760) // 10MB
public class UploadImageServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(UploadImageServlet.class.getName());

    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif"};
    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC  = {(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final byte[] GIF_MAGIC  = {0x47, 0x49, 0x46};

    /**
     * 获取头像持久化目录
     * @return {user.home}/StudentManager/userImg/
     */
    private File getUploadDir() {
        String home = System.getProperty("user.home");
        File dir = new File(home, "StudentManager/userImg");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 1. 登录校验
        Object uploadInfo = request.getSession().getAttribute("info");
        if (uploadInfo == null) {
            out.print("<script>alert('请先登录！');window.location.href='" + request.getContextPath() + "/login.jsp';</script>");
            return;
        }

        // 2. CSRF Token 校验
        HttpSession uploadSession = request.getSession();
        String csrfParam = request.getParameter("csrf_token");
        if (!SecurityUtil.validateCsrfToken(uploadSession, csrfParam)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF 验证失败");
            return;
        }

        String type = request.getParameter("type");
        String id   = request.getParameter("id");

        // 3. IDOR 防护：确保操作对象为当前登录用户本人
        String sessionUserId = null;
        if ("student".equals(type) && uploadInfo instanceof vo.Student) {
            sessionUserId = ((vo.Student) uploadInfo).getId();
        } else if ("teacher".equals(type) && uploadInfo instanceof vo.Teacher) {
            sessionUserId = ((vo.Teacher) uploadInfo).getId();
        }
        if (sessionUserId == null || !sessionUserId.equals(id)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权操作此账号");
            return;
        }

        Part filePart = request.getPart("img");

        // 4. 参数验证
        if (type == null || (!"student".equals(type) && !"teacher".equals(type))) {
            out.print("<script>alert('参数错误');window.location.href='" + request.getContextPath() + "/login.jsp';</script>");
            return;
        }
        if (id == null || id.trim().isEmpty()) {
            out.print("<script>alert('用户ID不能为空');window.location.href='" + getRedirectPath(request, type) + "';</script>");
            return;
        }
        if (filePart == null || filePart.getSize() <= 0) {
            out.print("<script>alert('请选择要上传的图片');window.location.href='" + getRedirectPath(request, type) + "';</script>");
            return;
        }

        // 5. MIME 类型校验
        String contentType = filePart.getContentType();
        if (!isAllowedImageType(contentType)) {
            out.print("<script>alert('仅支持 JPG/PNG/GIF 格式的图片！');window.location.href='" + getRedirectPath(request, type) + "';</script>");
            return;
        }

        // 6. 读取文件内容
        byte[] fileBytes;
        try (InputStream is = filePart.getInputStream()) {
            fileBytes = is.readAllBytes();
        }

        // 7. Magic bytes 校验
        if (fileBytes.length < 4) {
            out.print("<script>alert('上传的文件不是有效的图片！');window.location.href='" + getRedirectPath(request, type) + "';</script>");
            return;
        }
        if (!isValidImageContent(fileBytes)) {
            out.print("<script>alert('文件内容与声明的图片格式不匹配！');window.location.href='" + getRedirectPath(request, type) + "';</script>");
            return;
        }

        // 8. 保存到持久化目录
        String fileName = id + ".jpeg";
        File uploadDir = getUploadDir();
        File targetFile = new File(uploadDir, fileName);
        try (OutputStream os = new FileOutputStream(targetFile)) {
            os.write(fileBytes);
        }
        targetFile.setReadable(true);

        out.print("<script>alert('头像上传成功！');window.location.href='" + getRedirectPath(request, type) + "';</script>");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /** 根据用户类型返回跳转路径 */
    private String getRedirectPath(HttpServletRequest request, String type) {
        if ("teacher".equals(type)) {
            return request.getContextPath() + "/teacher/personal.jsp";
        }
        return request.getContextPath() + "/student/personal.jsp";
    }

    /** 校验 MIME 类型是否在允许列表中 */
    private boolean isAllowedImageType(String contentType) {
        if (contentType == null) return false;
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equals(contentType)) return true;
        }
        return false;
    }

    /**
     * 通过文件头 magic bytes 校验文件是否为真实图片。
     * <br>防止攻击者伪装 Content-Type 上传恶意文件。
     */
    private boolean isValidImageContent(byte[] fileBytes) {
        if (fileBytes.length < 4) return false;
        if (fileBytes[0] == JPEG_MAGIC[0] && fileBytes[1] == JPEG_MAGIC[1] && fileBytes[2] == JPEG_MAGIC[2]) return true;
        if (fileBytes[0] == PNG_MAGIC[0]  && fileBytes[1] == PNG_MAGIC[1]  && fileBytes[2] == PNG_MAGIC[2]  && fileBytes[3] == PNG_MAGIC[3])  return true;
        if (fileBytes[0] == GIF_MAGIC[0]  && fileBytes[1] == GIF_MAGIC[1]  && fileBytes[2] == GIF_MAGIC[2])  return true;
        return false;
    }
}
