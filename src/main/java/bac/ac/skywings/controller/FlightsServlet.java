package bac.ac.skywings.controller;

import bac.ac.skywings.dao.FlightDAO;
import bac.ac.skywings.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/flights")
public class FlightsServlet extends HttpServlet {
    private FlightDAO flightDAO;

    @Override
    public void init() {
        flightDAO = new FlightDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String origin = request.getParameter("origin");
        String destination = request.getParameter("destination");

        request.setAttribute("flights", flightDAO.searchFlights(origin, destination));
        request.getRequestDispatcher("/WEB-INF/views/flights.jsp").forward(request, response);
    }
}
