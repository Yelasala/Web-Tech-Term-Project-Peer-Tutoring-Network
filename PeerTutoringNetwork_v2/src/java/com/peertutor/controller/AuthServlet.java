package com.peertutor.controller;

import com.peertutor.dao.UserDAO;
import com.peertutor.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("logout".equalsIgnoreCase(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login.jsp?loggedOut=1");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("register".equalsIgnoreCase(action)) {
            handleRegister(request, response);
        } else {
            handleLogin(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Please enter both email and password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = userDAO.authenticate(email, password);
            if (user == null) {
                request.setAttribute("error", "Invalid credentials or inactive account.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else if ("TUTOR".equalsIgnoreCase(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/tutor");
            } else {
                response.sendRedirect(request.getContextPath() + "/student");
            }
        } catch (SQLException e) {
            throw new ServletException("Database error during login.", e);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String universityId = request.getParameter("universityId");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        if (fullName == null || fullName.trim().isEmpty()
                || universityId == null || universityId.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.length() < 4) {
            request.setAttribute("error", "All fields are required (password at least 4 characters).");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!"STUDENT".equalsIgnoreCase(role) && !"TUTOR".equalsIgnoreCase(role)) {
            role = "STUDENT";
        }

        try {
            if (userDAO.emailExists(email)) {
                request.setAttribute("error", "An account with that email already exists.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }
            if (userDAO.universityIdExists(universityId)) {
                request.setAttribute("error", "That University ID is already registered.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            User u = new User();
            u.setFullName(fullName);
            u.setUniversityId(universityId);
            u.setEmail(email);
            u.setPassword(password);
            u.setRole(role.toUpperCase());

            if (userDAO.registerUser(u)) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?registered=1");
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error during registration.", e);
        }
    }
}