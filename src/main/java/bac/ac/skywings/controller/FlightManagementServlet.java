package bac.ac.skywings.controller;

import bac.ac.skywings.dao.FlightDAO;
import bac.ac.skywings.model.Flight;
import bac.ac.skywings.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/agent/flights")
public class FlightManagementServlet extends HttpServlet {
    private FlightDAO flightDAO;

    @Override
    public void init() {
        flightDAO = new FlightDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null || (!"AIRLINE_AGENT".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole()))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        List<Flight> flights = flightDAO.searchFlights(null, null);
        request.setAttribute("flights", flights);
        request.getRequestDispatcher("/WEB-INF/views/agent-flights.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            String flightCode = request.getParameter("flightCode");
            String origin = request.getParameter("origin");
            String destination = request.getParameter("destination");
            String departureStr = request.getParameter("departureDatetime").replace("T", " ") + ":00";
            int totalSeats = Integer.parseInt(request.getParameter("totalSeats"));
            BigDecimal ticketPrice = new BigDecimal(request.getParameter("ticketPrice"));

            Flight flight = new Flight();
            flight.setFlightCode(flightCode);
            flight.setOrigin(origin);
            flight.setDestination(destination);
            flight.setDepartureDatetime(Timestamp.valueOf(departureStr));
            flight.setTotalSeats(totalSeats);
            flight.setTicketPrice(ticketPrice);

            flightDAO.createFlight(flight);
        } else if ("delete".equals(action)) {
            int flightId = Integer.parseInt(request.getParameter("flightId"));
            flightDAO.deleteFlight(flightId);
        }

        response.sendRedirect(request.getContextPath() + "/agent/flights");
    }
}