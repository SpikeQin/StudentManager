package util;

/**
 * Web 安全工具集 — XSS 防护 + CSRF 防护
 *
 * <p>所有输出到页面的用户数据均应经过 {@link #escapeHtml(String)} 转义；
 * <br>所有状态变更表单均应携带并校验 CSRF Token。
 */
public class SecurityUtil {

    /** HTML 特殊字符映射 */
    private static final String[][] HTML_ESCAPE = {
        {"&",  "&amp;"},
        {"<",  "&lt;"},
        {">",  "&gt;"},
        {"\"", "&quot;"},
        {"'",  "&#39;"}
    };

    /**
     * HTML 转义，防 XSS 注入
     * @param input 用户输入的原始字符串
     * @return 转义后的安全字符串；input 为 null 时返回空串
     */
    public static String escapeHtml(String input) {
        if (input == null) return "";
        String result = input;
        for (String[] pair : HTML_ESCAPE) {
            result = result.replace(pair[0], pair[1]);
        }
        return result;
    }

    /**
     * 生成 16 字符 hex CSRF Token（8 字节 SecureRandom）
     * @return 随机 token
     */
    public static String generateCsrfToken() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(16);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }

    /**
     * 校验 CSRF Token（一次性，用后即销毁）
     * @param session        当前会话
     * @param submittedToken 表单提交的 token
     * @return true 表示合法
     */
    public static boolean validateCsrfToken(jakarta.servlet.http.HttpSession session, String submittedToken) {
        if (submittedToken == null || submittedToken.isEmpty()) return false;
        Object expected = session.getAttribute("csrf_token");
        if (!(expected instanceof String)) return false;
        session.removeAttribute("csrf_token"); // 一次性 token
        return expected.equals(submittedToken);
    }

    /**
     * 刷新 CSRF Token 并存入 session
     * @param session 当前会话
     * @return 新生成的 token
     */
    public static String refreshCsrfToken(jakarta.servlet.http.HttpSession session) {
        String token = generateCsrfToken();
        session.setAttribute("csrf_token", token);
        return token;
    }
}
