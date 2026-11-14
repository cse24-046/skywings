<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SkyWings - Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center justify-content-center vh-100">
<div class="card shadow-sm border-0 p-4" style="max-width: 400px; width: 100%;">
    <div class="text-center mb-4">
        <h3 class="fw-bold text-primary">SkyWings</h3>
        <p class="text-muted">Sign in to your account</p>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger py-2" role="alert">${errorMessage}</div>
    </c:if>
    <c:if test="${param.registered eq 'true'}">
        <div class="alert alert-success py-2" role="alert">Registration successful! Please log in.</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="mb-3">
            <label class="form-label">Email Address</label>
            <input type="email" name="email" class="form-control" required placeholder="name@example.com">
        </div>
        <div class="mb-3">
            <label class="form-label">Password</label>
            <input type="password" name="password" class="form-control" required placeholder="Enter password">
        </div>
        <div class="d-grid mb-3">
            <button type="submit" class="btn btn-primary">Login</button>
        </div>
    </form>

    <div class="text-center">
        <p class="text-muted small mb-0">Don't have an account? <a href="${pageContext.request.contextPath}/register">Register here</a></p>
    </div>
</div>
</body>
</html>