package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 头像读取 Servlet — 从持久化目录返回图片
 *
 * <p>调用方式：<code>/image?id={用户ID}</code>
 * <br>头像存储路径：<code>{user.home}/StudentManager/userImg/{id}.jpeg</code>
 * <br>文件不存在时返回 404。
 */
@WebServlet("/image")
public class ImageServlet extends HttpServlet {

    /**
     * 获取头像持久化目录，若不存在则创建
     * @return {user.home}/StudentManager/userImg/
     */
    private File getUploadDir() {
        String home = System.getProperty("user.home");
        File dir = new File(home, "StudentManager/userImg");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id == null || id.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少 id 参数");
            return;
        }

        File imgFile = new File(getUploadDir(), id.trim() + ".jpeg");
        if (!imgFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("image/jpeg");
        response.setContentLength((int) imgFile.length());

        try (FileInputStream fis = new FileInputStream(imgFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
    }
}
