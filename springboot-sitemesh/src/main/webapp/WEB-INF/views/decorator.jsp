<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><sitemesh:write property='title'/></title>
    <sitemesh:write property='head'/>
</head>
<body>
<p>头部装饰器</p>

<div style="border: 1px solid red;">
    <sitemesh:write property='body'/>
</div>

<p>底部装饰器</p>
</body>
</html>
