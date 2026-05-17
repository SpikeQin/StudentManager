package util;

import dao.LogD;

/**
 * 操作日志工具 — 各层统一调用的日志门面
 *
 * <p>日志写入失败不抛异常，仅打印错误信息到 stderr，保证主流程不被中断。
 */
public class LogUtil {

    /**
     * 记录一条操作日志
     * @param userId   操作人 ID
     * @param userType admin | teacher | student
     * @param action   操作名称，如「添加学生」
     * @param detail   操作详情
     */
    public static void log(String userId, String userType, String action, String detail) {
        try {
            LogD logD = new LogD();
            logD.insertLog(userId, userType, action, detail);
        } catch (Exception e) {
            // 日志记录失败不应影响主流程
            System.err.println("操作日志记录失败: " + e.getMessage());
        }
    }
}
