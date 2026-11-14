package bac.ac.skywings.controller;

import bac.ac.skywings.dao.BookingDAO;
import bac.ac.skywings.model.Booking;
import bac.ac.skywings.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/book", "/my-bookings"})
public class BookingServlet extends HttpServlet {
    private BookingDAO bookingDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Booking> bookings = bookingDAO.getBookingsByUser(user.getId());
        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("/WEB-INF/views/my-bookings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("cancel".equals(action)) {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            boolean success = bookingDAO.cancelBooking(bookingId, user.getId());
            if (success) {
                response.sendRedirect(request.getContextPath() + "/my-bookings?cancelled=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/my-bookings?error=cancellation_failed");
            }
        } else {
            try {
                int flightId = Integer.parseInt(request.getParameter("flightId"));
                int seats = Integer.parseInt(request.getParameter("seats"));

                boolean success = bookingDAO.bookFlight(user.getId(), flightId, seats);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/my-bookings?booked=true");
                } else {
                    response.sendRedirect(request.getContextPath() + "/flights?error=booking_failed");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/flights?error=invalid_input");
            }
        }
    }
}