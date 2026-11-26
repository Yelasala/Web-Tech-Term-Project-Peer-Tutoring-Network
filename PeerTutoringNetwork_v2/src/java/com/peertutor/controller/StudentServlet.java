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
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "StudentServlet", urlPatterns = {"/student"})
public class StudentServlet extends HttpServlet {

    private final CourseDAO courseDAO = new CourseDAO();
    private final TutorDAO tutorDAO = new TutorDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    private User requireStudent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return null;
        }
        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
            return null;
        }
        return user;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User student = requireStudent(request, response);
        if (student == null) {
            return;
        }

        String courseParam = request.getParameter("courseId");
        String keyword = request.getParameter("keyword");
        Integer courseIdFilter = null;
        if (courseParam != null && !courseParam.trim().isEmpty()) {
            try {
                courseIdFilter = Integer.parseInt(courseParam.trim());
            } catch (NumberFormatException ignored) {
                courseIdFilter = null;
            }
        }

        try {
            List<Course> courses = courseDAO.getAllCourses();
            List<TutorProfile> tutors = tutorDAO.getApprovedTutors(courseIdFilter, keyword);
            List<Booking> bookings = bookingDAO.getBookingsByStudent(student.getUserId());

            request.setAttribute("courses", courses);
            request.setAttribute("tutors", tutors);
            request.setAttribute("bookings", bookings);
            request.setAttribute("selectedCourseId", courseIdFilter);
            request.setAttribute("keyword", keyword != null ? keyword : "");

            request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error loading student dashboard data.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User student = requireStudent(request, response);
        if (student == null) {
            return;
        }
        String action = request.getParameter("action");

        try {
            if ("book".equalsIgnoreCase(action)) {
                handleBookSession(request, response, student);
            } else if ("cancel".equalsIgnoreCase(action)) {
                int bookingId = Integer.parseInt(request.getParameter("bookingId"));
                bookingDAO.cancelBookingForStudent(bookingId, student.getUserId());
                response.sendRedirect(request.getContextPath() + "/student?msg=cancelled");
            } else if ("rate".equalsIgnoreCase(action)) {
                int bookingId = Integer.parseInt(request.getParameter("bookingId"));
                int rating = Integer.parseInt(request.getParameter("rating"));
                if (rating >= 1 && rating <= 5) {
                    bookingDAO.rateBooking(bookingId, student.getUserId(), rating);
                    response.sendRedirect(request.getContextPath() + "/student?msg=rated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/student?err=rating");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/student");
            }
        } catch (SQLException | NumberFormatException e) {
            throw new ServletException("Error processing student action.", e);
        }
    }

    private void handleBookSession(HttpServletRequest request, HttpServletResponse response, User student)
            throws ServletException, IOException, SQLException {
        int tutorId = Integer.parseInt(request.getParameter("tutorId"));
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String dateStr = request.getParameter("sessionDate");
        String timeSlot = request.getParameter("timeSlot");

        if (dateStr == null || dateStr.trim().isEmpty() || timeSlot == null || timeSlot.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/student?err=missing_slot");
            return;
        }

        Date sessionDate;
        try {
            sessionDate = Date.valueOf(dateStr.trim());
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/student?err=bad_date");
            return;
        }

        if (bookingDAO.hasConflict(tutorId, sessionDate, timeSlot)) {
            response.sendRedirect(request.getContextPath() + "/student?err=conflict");
            return;
        }

        Booking b = new Booking();
        b.setStudentId(student.getUserId());
        b.setTutorId(tutorId);
        b.setCourseId(courseId);
        b.setSessionDate(sessionDate);
        b.setTimeSlot(timeSlot);

        bookingDAO.createBooking(b);
        response.sendRedirect(request.getContextPath() + "/student?msg=booked");
    }
}