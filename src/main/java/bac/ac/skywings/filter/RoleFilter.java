package bac.ac.skywings.filter;

import bac.ac.skywings.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/flights", "/book", "/my-bookings", "/agent/*", "/admin/*"})
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        String uri = req.getRequestURI();

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login?error=unauthorized");
            return;
        }

        if (uri.contains("/agent/") && !"AIRLINE_AGENT".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            res.sendRedirect(req.getContextPath() + "/flights?error=forbidden");
            return;
        }

        if (uri.contains("/admin/") && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            res.sendRedirect(req.getContextPath() + "/flights?error=forbidden");
            return;
        }

        chain.doFilter(request, response);
    }
}