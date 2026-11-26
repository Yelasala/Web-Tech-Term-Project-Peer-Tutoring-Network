package com.peertutor.filter;

import com.peertutor.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/student", "/student/*", "/tutor", "/tutor/*", "/admin", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String path = req.getRequestURI().substring(req.getContextPath().length());
        String role = user.getRole();

        boolean isStudentArea = path.equals("/student") || path.startsWith("/student/");
        boolean isTutorArea = path.equals("/tutor") || path.startsWith("/tutor/");
        boolean isAdminArea = path.equals("/admin") || path.startsWith("/admin/");

        if (isStudentArea && !"STUDENT".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }
        if (isTutorArea && !"TUTOR".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }
        if (isAdminArea && !"ADMIN".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}