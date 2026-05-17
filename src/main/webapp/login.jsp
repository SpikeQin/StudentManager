<%--
  登录页面 — 所有角色共用

  提交到 /check_login 做身份验证，支持验证码刷新、「记住我」和忘记密码跳转。
--%>
<%@ page import="util.SecurityUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>教务管理系统</title>
    <link href="resources/css/login.css" type="text/css" rel="stylesheet" />
</head>
<body>
<script>
    /**
     * 登录表单前端校验
     * 检查账号、密码、验证码是否为空
     * @param form - 登录表单对象
     * @returns {boolean} - 校验通过返回 true，否则返回 false 并弹出提示
     */
    function check(form){
        if (form.user.value === "") {
            alert("请输入账号！");
            return false;
        }
        if (form.password.value === "") {
            alert("请输入密码！");
            return false;
        }
        if (form.code.value === "") {
            alert("请输入验证码！");
            return false;
        }
        return true;
    }

    /**
     * 刷新验证码图片
     * 通过在 URL 后追加随机参数绕过浏览器缓存，强制重新加载验证码
     */
    function refreshCode() {
        document.getElementById("codeImg").src = "code.jsp?" + Math.random();
    }
</script>
<!-- 登录页面容器 -->
<div class="login-container">
    <!-- 左侧横幅区域：展示系统名称和功能介绍 -->
    <div class="login-banner">
        <div class="banner-content">
            <div class="banner-icon"></div>
            <h1>教务管理系统</h1>
            <p>高效、便捷的教学管理平台<br>为师生提供优质的服务体验</p>
            <!-- 系统功能列表 -->
            <div class="banner-features">
                <div class="feature-item">学生信息管理</div>
                <div class="feature-item">教师信息管理</div>
                <div class="feature-item">成绩录入查询</div>
            </div>
        </div>
    </div>
    <!-- 右侧登录表单区域 -->
    <div class="login-box">
        <h2>账号登录</h2>
        <p class="subtitle">欢迎回来，请登录您的账号</p>
        <!-- 登录表单：提交到 check_login Servlet，提交前调用 check() 进行前端校验 -->
        <form action="${pageContext.request.contextPath}/check_login" method="post" onsubmit="return check(this)">
            <input type="hidden" name="csrf_token" value="<%= SecurityUtil.refreshCsrfToken(session) %>" />
            <!-- 账号输入 -->
            <div class="input-group">
                <span class="input-icon"></span>
                <input type="text" name="user" placeholder="请输入账号">
            </div>
            <!-- 密码输入 -->
            <div class="input-group">
                <span class="input-icon"></span>
                <input type="password" name="password" placeholder="请输入密码">
            </div>
            <!-- 验证码输入及图片（点击图片可刷新验证码） -->
            <div class="input-group code-group">
                <span class="input-icon"></span>
                <input type="text" name="code" placeholder="请输入验证码" maxlength="4">
                <img id="codeImg" src="code.jsp" onclick="refreshCode()" alt="验证码" title="点击刷新验证码">
            </div>
            <!-- 记住我复选框和忘记密码链接 -->
            <div class="extra">
                <label>
                    <input type="checkbox" name="remember" value="true">记住我
                </label>
                <a href="forget.jsp">忘记密码</a>
            </div>
            <!-- 登录提交按钮 -->
            <input type="submit" value="登录" class="login-btn" />
        </form>
    </div>
</div>
</body>
</html>
