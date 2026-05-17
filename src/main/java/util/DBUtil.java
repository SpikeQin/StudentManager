package util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 数据库连接工具 — 从 db.properties 加载配置并提供连接
 *
 * <p>静态初始化块中完成配置加载与驱动注册，失败时抛出异常阻止应用启动。
 * <br>调用方需使用 try-with-resources 确保连接正确关闭。
 */
public class DBUtil {

    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static {
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            props.load(is);
            driver   = props.getProperty("db.driver");
            url      = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");
            Class.forName(driver);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("数据库配置加载失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据库连接。每次调用创建新连接。
     * @return Connection 对象
     * @throws SQLException 连接失败时抛出
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
