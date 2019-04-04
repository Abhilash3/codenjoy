<%--
  #%L
  Codenjoy - it's a dojo-like platform from developers to developers.
  %%
  Copyright (C) 2018 Codenjoy
  %%
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public
  License along with this program.  If not, see
  <http://www.gnu.org/licenses/gpl-3.0.html>.
  #L%
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<head>
    <link href="${ctx}/resources/favicon.ico" rel="icon">
    <link href="${ctx}/resources/css/custom.css" rel="stylesheet">

    <c:choose>
        <c:when test="${debug}" >
            <script src="${ctx}/resources/js/all.js"></script>
        </c:when>
        <c:otherwise>
            <script src="${ctx}/resources/js/all.min.js"></script>
        </c:otherwise>
    </c:choose>
</head>
<sec:authorize access="isAuthenticated()">
<body>
    <div class="user-controls-container">
        <div class="user-control float-left">
            <form class="user-control" action="${ctx}/process_logout">
                <input type="submit" value="Logout" class="btn btn-link">
            </form>
        </div>
        <div class="user-control">
            <a href="${ctx}/" >Games</a>
        </div>
    </div>
</body>
</sec:authorize>
