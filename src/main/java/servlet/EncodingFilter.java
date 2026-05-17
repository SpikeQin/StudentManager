package servlet;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 全局编码过滤器 — 拦截所有请求 /*
 *
 * <p>统一设置请求/响应字符编码为 UTF-8，解决中文乱码问题。
 * <br>Servlet 中不再需要手动设置编码（已有的可逐步移除）。
 *
 * <p><b>注意：</b>不设置 Content-Type，避免覆盖 CSS/JS 等静态资源的正确 MIME 类型。
 */
@WebFilter("/*")
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
