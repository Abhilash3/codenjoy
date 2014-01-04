<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<head>
    <meta http-equiv="Content-Type" content="text/html;">
    <title>Codenjoy</title>
    <link href="${ctx}/resources/css/bootstrap.css" rel="stylesheet">
</head>
<body>
    <div class="page-header">
        <h1>Hi ${(user!=null)?user:ip}, please:</h1>
    </div>
    <ol>
        <li><a href="${ctx}/help">How to start</a></li>
        <c:if test="${!registered}">
        <li><a href="${ctx}/register">Register</a></li>
        </c:if>
        <c:if test="${registered}">
            <li><a href="${ctx}/register?remove_me&code=${code}">Unregister</a></li>
        </c:if>
        <li>Check game board</li>
        <c:forEach items="${gameNames}" var="gameName">
            - <a href="${ctx}/board?gameName=${gameName}">${gameName}</a></br>
        </c:forEach>
    </ol>
<body>
</html>