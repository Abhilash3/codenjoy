<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<head>
    <meta http-equiv="Content-Type" content="text/html;">
    <title>Coding dojo</title>
    <link href="${ctx}/resources/css/bootstrap.css" rel="stylesheet">
    <link href="${ctx}/resources/css/dojo.css" rel="stylesheet">
    <script src="${ctx}/resources/js/jquery-1.7.2.js"></script>
    <script src="${ctx}/resources/js/jquery.validate.js"></script>
    <script src="${ctx}/resources/js/registration.js"></script>
    <script src="${ctx}/resources/js/validation.js"></script>
</head>
<body>
    <div class="page-header">
        <h1 id="title">Registration</h1>
    </div>
    <form:form commandName="player" action="register" method="POST">
        <table>
            <tr>
                <td>Player name<form:errors path="name"/></td>
            </tr>
            <tr>
                <td><form:input path="name"/><c:if test="${bad_pass}">Already registered</c:if></td>
            </tr>
            <tr>
                <td>Password<form:errors path="password"/></td>
            </tr>
            <tr>
                <td><form:password path="password"/><c:if test="${bad_pass}">Bad password</c:if></td>
            </tr>
            <tr>
                <td>Your game</td>
            </tr>
            <tr>
                <td><form:select items="${gameNames}" path="gameName"/></td>
            </tr>
            <tr>
                <td colspan="3">
                    <input type="submit" id="submit" value="Register"/>
                </td>
            </tr>
        </table>
    </form:form>
</body>
</html>