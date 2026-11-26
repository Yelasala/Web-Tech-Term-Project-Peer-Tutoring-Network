package com.peertutor.controller;

import com.peertutor.dao.BookingDAO;
import com.peertutor.dao.CourseDAO;
import com.peertutor.dao.TutorDAO;
import com.peertutor.dao.UserDAO;
import com.peertutor.model.Booking;
import com.peertutor.model.Course;
import com.peertutor.model.TutorProfile;
import com.peertutor.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final TutorDAO tutorDAO = new TutorDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireAdmin(request, response)) {
            return;
        }

        try {
            List<User> users = userDAO.getAllUsers();
            List<Course> courses = courseDAO.getAllCourses();
            List<TutorProfile> tutorProfiles = tutorDAO.getAllTutorProfiles();
            List<Booking> bookings = bookingDAO.getAllBookings();

            request.setAttribute("users", users);
            request.setAttribute("courses", courses);
            request.setAttribute("tutorProfiles", tutorProfiles);
            request.setAttribute("bookings", bookings);

            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error loading admin dashboard.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!requireAdmin(request, response)) {
            return;
        }
        String action = request.getParameter("action");

        try {
            if ("approveTutor".equalsIgnoreCase(action)) {
                int tutorId = Integer.parseInt(request.getParameter("tutorId"));
                String status = request.getParameter("status");
                if ("APPROVED".equals(status) || "REJECTED".equals(status) || "PENDING".equals(status)) {
                    tutorDAO.updateApprovalStatus(tutorId, status);
                    response.sendRedirect(request.getContextPath() + "/admin?msg=tutor_updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin?err=bad_status");
                }
            } else if ("addCourse".equalsIgnoreCase(action)) {
                String code = request.getParameter("courseCode");
                String name = request.getParameter("courseName");
                if (code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/admin?err=course_empty");
                } else if (courseDAO.courseCodeExists(code)) {
                    response.sendRedirect(request.getContextPath() + "/admin?err=course_exists");
                } else {
                    courseDAO.addCourse(code, name);
                    response.sendRedirect(request.getContextPath() + "/admin?msg=course_added");
                }
            } else if ("toggleUser".equalsIgnoreCase(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                boolean active = Boolean.parseBoolean(request.getParameter("active"));
                userDAO.setUserActive(userId, active);
                response.sendRedirect(request.getContextPath() + "/admin?msg=user_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin");
            }
        } catch (SQLException | NumberFormatException e) {
            throw new ServletException("Error processing admin action.", e);
        }
    }
}