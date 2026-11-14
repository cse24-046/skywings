<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SkyWings - My Bookings</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-primary mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/flights">SkyWings Airlines</a>
        <div class="d-flex align-items-center text-white">
            <a href="${pageContext.request.contextPath}/flights" class="btn btn-outline-light btn-sm me-3">Flight Catalog</a>
            <span class="me-3">${sessionScope.currentUser.fullName}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm">Logout</a>
        </div>
    </div>
</nav>

<div class="container">
    <h2 class="fw-bold mb-3">Your Booked Flight Tickets</h2>

    <c:if test="${param.success eq 'true'}">
        <div class="alert alert-success">Flight booked successfully!</div>
    </c:if>
    <c:if test="${param.cancelled eq 'true'}">
        <div class="alert alert-info">Booking cancelled successfully. Seats have been restored.</div>
    </c:if>
    <c:if test="${param.error eq 'cancellation_failed'}">
        <div class="alert alert-danger">Cancellation failed. Bookings can only be cancelled at least 24 hours prior to departure.</div>
    </c:if>

    <div class="card shadow-sm border-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                <tr>
                    <th>Booking ID</th>
                    <th>Flight Code</th>
                    <th>Route</th>
                    <th>Departure Time</th>
                    <th>Seats</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="bk" items="${bookings}">
                    <tr>
                        <td class="fw-semibold">#${bk.id}</td>
                        <td>${bk.flightCode}</td>
                        <td>${bk.origin} &rarr; ${bk.destination}</td>
                        <td>${bk.departureDatetime}</td>
                        <td><span class="badge bg-secondary">${bk.seatsBooked}</span></td>
                        <td><fmt:formatNumber value="${bk.totalAmount}" type="currency" currencySymbol="P "/></td>
                        <td>
                            <c:choose>
                                <c:when test="${bk.bookingStatus eq 'CONFIRMED'}">
                                    <span class="badge bg-success">CONFIRMED</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger">CANCELLED</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${bk.bookingStatus eq 'CONFIRMED'}">
                                <form action="${pageContext.request.contextPath}/book" method="post" class="m-0">
                                    <input type="hidden" name="action" value="cancel">
                                    <input type="hidden" name="bookingId" value="${bk.id}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Are you sure you want to cancel this ticket? Seats will be restored.')">Cancel</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty bookings}">
                    <tr>
                        <td colspan="8" class="text-center py-4 text-muted">You haven't booked any flights yet.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>