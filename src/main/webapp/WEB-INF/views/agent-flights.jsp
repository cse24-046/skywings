<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SkyWings - Flight Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-warning mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold text-dark" href="#">SkyWings Agent Portal</a>
        <div class="d-flex align-items-center">
            <a href="${pageContext.request.contextPath}/flights" class="btn btn-dark btn-sm me-3">Back to Catalog</a>
            <span class="me-3 text-dark fw-semibold">${sessionScope.currentUser.fullName}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm">Logout</a>
        </div>
    </div>
</nav>

<div class="container">
    <div class="row mb-4">
        <div class="col-md-12">
            <div class="card shadow-sm border-0 p-4">
                <h4 class="fw-bold mb-3">Add New Flight Schedule</h4>
                <form action="${pageContext.request.contextPath}/agent/flights" method="post" class="row g-3">
                    <input type="hidden" name="action" value="create">
                    <div class="col-md-3">
                        <label class="form-label">Flight Code</label>
                        <input type="text" name="flightCode" class="form-control" placeholder="e.g. SW-404" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Origin</label>
                        <input type="text" name="origin" class="form-control" placeholder="City (Code)" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Destination</label>
                        <input type="text" name="destination" class="form-control" placeholder="City (Code)" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Departure Date & Time</label>
                        <input type="datetime-local" name="departureDatetime" class="form-control" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Total Seats</label>
                        <input type="number" name="totalSeats" class="form-control" min="10" value="100" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Ticket Price (P)</label>
                        <input type="number" step="0.01" name="ticketPrice" class="form-control" placeholder="1500.00" required>
                    </div>
                    <div class="col-md-4 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary w-100">Publish Flight</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <h3 class="fw-bold mb-3">Active Fleet Schedules</h3>
    <div class="card shadow-sm border-0">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Flight Code</th>
                <th>Route</th>
                <th>Departure</th>
                <th>Seats (Avail / Total)</th>
                <th>Price</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="f" items="${flights}">
                <tr>
                    <td class="fw-semibold">#${f.id}</td>
                    <td>${f.flightCode}</td>
                    <td>${f.origin} &rarr; ${f.destination}</td>
                    <td>${f.departureDatetime}</td>
                    <td><span class="badge bg-success">${f.availableSeats}</span> / ${f.totalSeats}</td>
                    <td><fmt:formatNumber value="${f.ticketPrice}" type="currency" currencySymbol="P "/></td>
                    <td>
                        <form action="${pageContext.request.contextPath}/agent/flights" method="post" class="m-0">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="flightId" value="${f.id}">
                            <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Are you sure you want to delete this flight?')">Delete</button>
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