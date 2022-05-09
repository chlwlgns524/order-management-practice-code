<%--
  Created by IntelliJ IDEA.
  User: chlwl
  Date: 2022-04-21
  Time: 오전 10:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
</head>
<body class="container-fluid">

    <img src="<c:url value="/resources/nature.jpg"/>" class="img-fluid"/>
    <h1>KDT Spring App</h1>
    <p1>The time on server is <%= request.getAttribute("serverTime")%></p1>
    
    <h2>Customer Table</h2>
    <table class="table table-hover">
        <thead>
        <tr>
            <th scope="col">customerId</th>
            <th scope="col">name</th>
            <th scope="col">email</th>
            <th scope="col">lastLoginAt</th>
            <th scope="col">createdAt</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="customer" items="${allCustomers}">
            <tr>
                <td>${customer.customerId}</td>
                <td>${customer.name}</td>
                <td>${customer.email}</td>
                <td>${customer.lastLoginAt}</td>
                <td>${customer.createdAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</body>
</html>
