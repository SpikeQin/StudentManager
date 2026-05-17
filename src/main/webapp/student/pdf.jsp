<%--
  成绩单 PDF 生成 — 学生端

  接收 URL 参数生成 PDF 并触发下载。不渲染可见 HTML。
  使用 iTextPDF + 微软雅黑字体（内嵌）确保中文正常显示。
--%>
<%@ page import="com.itextpdf.text.Document" %>
<%@ page import="com.itextpdf.text.pdf.PdfWriter" %>
<%@ page import="com.itextpdf.text.Paragraph" %>
<%@ page import="com.itextpdf.text.pdf.BaseFont" %>
<%@ page import="com.itextpdf.text.Font" %>
<%@ page import="com.itextpdf.text.PageSize" %>
<%@ page import="com.itextpdf.text.Element" %>
<%@ page import="com.itextpdf.text.pdf.PdfPTable" %>
<%@ page import="com.itextpdf.text.pdf.PdfPCell" %>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>成绩PDF</title>
</head>
<body>
<%-- PDF 生成与输出：设置下载响应头，获取参数，生成 PDF 表格 --%>
<%
    // 设置响应头，触发浏览器下载 PDF 文件
    response.setContentType("application/pdf");
    response.addHeader("Content-Disposition", "attachment;filename=report.pdf");

    // 从 URL 请求参数中获取成绩数据
    String id = request.getParameter("id");
    String name = request.getParameter("name");
    String major = request.getParameter("major");
    String dataStructure = request.getParameter("dataStructure");
    String operatingSystem = request.getParameter("operatingSystem");
    String computerNetwork = request.getParameter("computerNetwork");
    String computerOrganization = request.getParameter("computerOrganization");

    // 使用 iTextPDF 库生成 PDF 文档
    try {
        Document doc = new Document(PageSize.A4);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, buffer);

        // 加载微软雅黑中文字体，确保 PDF 中文字正常显示
        BaseFont bf = BaseFont.createFont(request.getSession().getServletContext().getRealPath("/")+"resources/font/msyh.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font font = new Font(bf, 15, Font.NORMAL);

        doc.open();

        // PDF 标题：成绩单
        Paragraph paragraph = new Paragraph("\u6210\u7ee9\u5355", font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        doc.add(paragraph);
        doc.add(new Paragraph(" "));

        // 构建成绩表格（7列：学号、姓名、专业、数据结构、操作系统、计算机网络、计算机组成原理）
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell;
        // 学号列（跨2行合并）
        cell = new PdfPCell(new Paragraph("\u5b66\u53f7", font));
        cell.setRowspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // 姓名列（跨2行合并）
        cell = new PdfPCell(new Paragraph("\u59d3\u540d", font));
        cell.setRowspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // 专业列（跨2行合并）
        cell = new PdfPCell(new Paragraph("\u4e13\u4e1a", font));
        cell.setRowspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // 成绩表头（跨4列）
        cell = new PdfPCell(new Paragraph("\u6210\u7ee9", font));
        cell.setColspan(4);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // 四门课程名称子表头
        table.addCell(new Paragraph("\u6570\u636e\u7ed3\u6784", font));
        table.addCell(new Paragraph("\u64cd\u4f5c\u7cfb\u7edf", font));
        table.addCell(new Paragraph("\u8ba1\u7b97\u673a\u7f51\u7edc", font));
        table.addCell(new Paragraph("\u8ba1\u7b97\u673a\u7ec4\u6210\u539f\u7406", font));

        // 填充学生成绩数据
        table.addCell(id);
        table.addCell(new Paragraph(name, font));
        table.addCell(new Paragraph(major, font));
        table.addCell(dataStructure);
        table.addCell(operatingSystem);
        table.addCell(computerNetwork);
        table.addCell(computerOrganization);

        doc.add(table);
        doc.close();

        // 将生成的 PDF 字节流写入 HTTP 响应输出流，触发浏览器下载
        byte[] bytes = buffer.toByteArray();
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }
    catch (Exception e) {
        Logger.getLogger("pdf").log(Level.WARNING, "PDF generation failed", e);
    }
%>
</body>
</html>

