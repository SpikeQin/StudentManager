<%--
  验证码生成 — 输出 JPEG 图片流

  生成 4 位随机数字 + 干扰点，验证码存入 Session（键名 randStr）。
--%>
<%@ page import="java.awt.image.BufferedImage,java.awt.*,java.security.SecureRandom,javax.imageio.ImageIO" %>
<%@ page contentType="image/jpeg" pageEncoding="UTF-8" language="java" %>
<%
    // 禁止浏览器缓存验证码图片，确保每次都能获取最新验证码
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // 创建 80x30 像素的验证码图片
    int width = 80, height = 30;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();

    // 填充浅灰色背景
    g.setColor(new Color(245, 245, 245));
    g.fillRect(0, 0, width, height);

    // 生成 1000~9999 之间的随机4位数字验证码
    SecureRandom rnd = new SecureRandom();
    int randNum = rnd.nextInt(8999) + 1000;
    String randStr = String.valueOf(randNum);
    // 将验证码存入 Session，登录时用于比对用户输入
    session.setAttribute("randStr", randStr);

    // 在图片上绘制深色验证码文字
    g.setColor(new Color(50, 50, 50));
    g.setFont(new Font("Arial", Font.BOLD, 22));
    g.drawString(randStr, 12, 24);

    // 绘制随机干扰点，增加机器识别难度
    for (int i = 0; i < 80; i++) {
        g.setColor(new Color(rnd.nextInt(200), rnd.nextInt(200), rnd.nextInt(200)));
        int x = rnd.nextInt(width);
        int y = rnd.nextInt(height);
        g.drawOval(x, y, 2, 2);
    }

    // 将图片以 JPEG 格式写入响应输出流
    ImageIO.write(image, "jpeg", response.getOutputStream());
%>