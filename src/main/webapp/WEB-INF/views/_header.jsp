<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CampusMarket &mdash; student resale marketplace</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${ctx}/css/style.css?v=8" rel="stylesheet">
</head>
<body>
<nav class="app-nav">
    <div class="container">
        <a class="brand" href="${ctx}/browse">
            <img src="${ctx}/img/logo.svg" alt="logo">
            <span class="brand-name">Campus<span>Market</span></span>
        </a>
        <div class="nav-links d-none d-md-flex">
            <a href="${ctx}/browse">Browse</a>
            <c:if test="${not empty currentStudent}">
                <a href="${ctx}/sell">Sell</a>
                <a href="${ctx}/my-listings">My Listings</a>
                <a href="${ctx}/history">History</a>
                <a href="${ctx}/notifications">Alerts<c:if test="${notificationCount > 0}"> (${notificationCount})</c:if></a>
                <c:if test="${currentStudent.admin}"><a href="${ctx}/admin">Admin</a></c:if>
            </c:if>
        </div>
        <div class="nav-right">
            <a class="cart-link" href="${ctx}/cart">
                <img src="${ctx}/img/ic-cart.svg" width="18" height="18" alt="">
                <span class="d-none d-sm-inline">Cart</span>
                <c:if test="${cartCount > 0}"><span class="cart-badge">${cartCount}</span></c:if>
            </a>
            <c:choose>
                <c:when test="${not empty currentStudent}">
                    <a class="wallet-chip" href="${ctx}/profile" title="Open dashboard">
                        <c:out value="${currentStudent.name}"/>
                    </a>
                    <form method="post" action="${ctx}/logout" class="d-inline">
<input type="hidden" name="csrfToken" value="${csrfToken}"><button class="btn btn-ghost btn-sm" type="submit">Logout</button></form>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-ghost btn-sm" href="${ctx}/login">Login</a>
                    <a class="btn btn-primary btn-sm" href="${ctx}/register">Sign up</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>
<main class="container py-4">
    <c:if test="${not empty flash}">
        <div class="alert alert-success"><c:out value="${flash}"/></div>
    </c:if>
