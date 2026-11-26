package com.peertutor.controller;

import com.peertutor.dao.BookingDAO;
import com.peertutor.dao.CourseDAO;
import com.peertutor.dao.TutorDAO;
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

@WebServlet(name = "TutorServlet", urlPatterns = {"/tutor"})
public class TutorServlet extends HttpServlet {

    private final CourseDAO courseDAO = new CourseDAO();
    private final TutorDAO tutorDAO = new TutorDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    private User requireTutor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return null;
        }
        if (!"TUTOR".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
            return null;
        }
        return user;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User tutor = requireTutor(request, response);
        if (tutor == null) {
            return;
        }

        try {
            List<Course> courses = courseDAO.getAllCourses();
            TutorProfile profile = tutorDAO.getProfileByTutorId(tutor.getUserId());
            List<Booking> bookings = bookingDAO.getBookingsByTutor(tutor.getUserId());

            request.setAttribute("courses", courses);
            request.setAttribute("profile", profile);
            request.setAttribute("bookings", bookings);

            request.getRequestDispatcher("/tutor/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error loading tutor dashboard.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User tutor = requireTutor(request, response);
        if (tutor == null) {
            return;
        }
        String action = request.getParameter("action");

        try {
            if ("saveProfile".equalsIgnoreCase(action)) {
                int courseId = Integer.parseInt(request.getParameter("courseId"));
                String bio = request.getParameter("bio");
                String availability = request.getParameter("availability");

                if (bio == null || bio.trim().isEmpty() || availability == null || availability.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/tutor?err=empty_profile");
                    return;
                }

                tutorDAO.upsertTutorProfile(tutor.getUserId(), courseId, bio, availability);
                response.sendRedirect(request.getContextPath() + "/tutor?msg=profile_saved");
            } else if ("updateStatus".equalsIgnoreCase(action)) {
                int bookingId = Integer.parseInt(request.getParameter("bookingId"));
                String status = request.getParameter("status");

                if ("CONFIRMED".equals(status) || "DECLINED".equals(status) || "COMPLETED".equals(status)) {
                    bookingDAO.updateStatusForTutor(bookingId, tutor.getUserId(), status);
                    response.sendRedirect(request.getContextPath() + "/tutor?msg=status_updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/tutor?err=bad_status");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/tutor");
            }
        } catch (SQLException | NumberFormatException e) {
            throw new ServletException("Error processing tutor action.", e);
        }
    }
}