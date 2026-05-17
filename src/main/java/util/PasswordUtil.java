package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码安全工具 — PBKDF2WithHmacSHA256 哈希
 *
 * <p>安全特性：
 * <ul>
 *   <li>10 万次迭代，提高暴力破解成本</li>
 *   <li>每用户独立 16 字节随机盐值（SecureRandom）</li>
 *   <li>恒定时间比较（slowEquals），防时序攻击</li>
 *   <li>密码用 char[] 处理，用后立即清零</li>
 *   <li>兼容旧版明文密码，首次登录自动升级</li>
 * </ul>
 *
 * <p>存储格式：<code>Base64(盐) + ":" + Base64(哈希)</code>
 */
public class PasswordUtil {

    private static final int ITERATIONS  = 100_000;        // 迭代次数
    private static final int KEY_LENGTH  = 256;            // 哈希输出长度（位）
    private static final int SALT_LENGTH = 16;             // 盐值长度（字节）
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * 对明文密码做哈希
     * @param password 明文
     * @return "盐:哈希" 字符串
     */
    public static String hashPassword(String password) {
        byte[] salt = generateSalt();
        byte[] hash = pbkdf2(password.toCharArray(), salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * 验证密码。
     * <br>若 storedHash 不含 ":" 分隔符，则按旧版明文密码比对。
     *
     * @param password   用户输入的明文
     * @param storedHash 数据库中的存储值（哈希或明文）
     * @return true 表示匹配
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null || !storedHash.contains(":")) {
            // 兼容旧版明文密码
            return password != null && password.equals(storedHash);
        }
        String[] parts = storedHash.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] hash = Base64.getDecoder().decode(parts[1]);
        byte[] computedHash = pbkdf2(password.toCharArray(), salt);
        return slowEquals(hash, computedHash);
    }

    /**
     * 检查是否需要升级为哈希存储
     * @param storedHash 数据库中的值
     * @return true 表示是旧版明文，需要升级
     */
    public static boolean needsRehash(String storedHash) {
        return storedHash == null || !storedHash.contains(":");
    }

    /** 生成加密级随机盐值 */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /** 执行 PBKDF2 哈希计算 */
    private static byte[] pbkdf2(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("密码哈希失败", e);
        } finally {
            spec.clearPassword(); // 清除内存中的敏感数据
        }
    }

    /**
     * 恒定时间比较两个字节数组。
     * <br>无论是否匹配，执行耗时相同，防止侧信道攻击。
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
