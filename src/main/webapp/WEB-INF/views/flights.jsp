<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SkyWings - Flight Catalog</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-primary mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold" href="#">SkyWings Airlines</a>
        <div class="d-flex align-items-center text-white">
            <span class="me-3">${sessionScope.currentUser.fullName} (${sessionScope.currentUser.role})</span>
            <c:if test="${sessionScope.currentUser.role eq 'PASSENGER'}">
                <a href="${pageContext.request.contextPath}/my-bookings" class="btn btn-outline-light btn-sm me-3">My Bookings</a>
            </c:if>
            <c:if test="${sessionScope.currentUser.role eq 'AIRLINE_AGENT' || sessionScope.currentUser.role eq 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/agent/flights" class="btn btn-warning btn-sm me-3">Agent Portal</a>
            </c:if>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm">Logout</a>
        </div>
    </div>
</nav>

<div class="container">
    <h2 class="fw-bold mb-3">Available Flights</h2>

    <form action="${pageContext.request.contextPath}/flights" method="get" class="row g-3 mb-4">
        <div class="col-md-4"><input type="text" name="origin" class="form-control" placeholder="Origin city..." value="${param.origin}"></div>
        <div class="col-md-4"><input type="text" name="destination" class="form-control" placeholder="Destination city..." value="${param.destination}"></div>
        <div class="col-md-4"><button type="submit" class="btn btn-primary w-100">Search Flights</button></div>
    </form>

    <div class="card shadow-sm border-0">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-dark">
            <tr>
                <th>Flight Code</th>
                <th>Route</th>
                <th>Departure</th>
                <th>Available Seats</th>
                <th>Price</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="f" items="${flights}">
                <tr>
                    <td class="fw-semibold">${f.flightCode}</td>
                    <td>${f.origin} &rarr; ${f.destination}</td>
                    <td>${f.departureDatetime}</td>
                    <td><span class="badge bg-success">${f.availableSeats} seats</span></td>
                    <td><fmt:formatNumber value="${f.ticketPrice}" type="currency" currencySymbol="P "/></td>
                    <td>
                        <form action="${pageContext.request.contextPath}/book" method="post" class="d-flex gap-2 m-0">
                            <input type="hidden" name="flightId" value="${f.id}">
                            <input type="number" name="seats" value="1" min="1" max="${f.availableSeats}" class="form-control form-control-sm w-25" required>
                            <button type="submit" class="btn btn-sm btn-success">Book</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>