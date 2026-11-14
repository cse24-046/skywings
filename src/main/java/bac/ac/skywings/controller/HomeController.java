package bac.ac.skywings.controller;

import bac.ac.skywings.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("")
public class HomeController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
        } else if ("ADMIN".equalsIgnoreCase(user.getRole()) || "AIRLINE_AGENT".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/agent/flights");
        } else {
            response.sendRedirect(request.getContextPath() + "/flights");
        }
    }
}