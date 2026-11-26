package com.peertutor.dao;

import com.peertutor.model.Booking;
import com.peertutor.util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public boolean hasConflict(int tutorId, Date sessionDate, String timeSlot) throws SQLException {
        String sql = "SELECT 1 FROM BOOKINGS WHERE tutor_id = ? AND session_date = ? " +
                     "AND time_slot = ? AND status IN ('PENDING', 'CONFIRMED')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tutorId);
            ps.setDate(2, sessionDate);
            ps.setString(3, timeSlot.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean createBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO BOOKINGS (student_id, tutor_id, course_id, session_date, time_slot, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, booking.getStudentId());
            ps.setInt(2, booking.getTutorId());
            ps.setInt(3, booking.getCourseId());
            ps.setDate(4, booking.getSessionDate());
            ps.setString(5, booking.getTimeSlot().trim());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Booking> getBookingsByStudent(int studentId) throws SQLException {
        String sql = baseSelect() + "WHERE b.student_id = ? ORDER BY b.session_date DESC, b.booking_id DESC";
        return queryBookings(sql, studentId);
    }

    public List<Booking> getBookingsByTutor(int tutorId) throws SQLException {
        String sql = baseSelect() + "WHERE b.tutor_id = ? ORDER BY b.session_date DESC, b.booking_id DESC";
        return queryBookings(sql, tutorId);
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = baseSelect() + "ORDER BY b.session_date DESC, b.booking_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapBooking(rs));
            }
        }
        return list;
    }

    public boolean updateStatusForTutor(int bookingId, int tutorId, String newStatus) throws SQLException {
        String sql = "UPDATE BOOKINGS SET status = ? WHERE booking_id = ? AND tutor_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, bookingId);
            ps.setInt(3, tutorId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean cancelBookingForStudent(int bookingId, int studentId) throws SQLException {
        String sql = "UPDATE BOOKINGS SET status = 'CANCELLED' " +
                     "WHERE booking_id = ? AND student_id = ? AND status IN ('PENDING', 'CONFIRMED')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean rateBooking(int bookingId, int studentId, int rating) throws SQLException {
        String sql = "UPDATE BOOKINGS SET rating = ? " +
                     "WHERE booking_id = ? AND student_id = ? AND status = 'COMPLETED'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setInt(2, bookingId);
            ps.setInt(3, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    private String baseSelect() {
        return "SELECT b.booking_id, b.student_id, su.full_name AS student_name, " +
               "b.tutor_id, tu.full_name AS tutor_name, b.course_id, c.course_code, c.course_name, " +
               "b.session_date, b.time_slot, b.status, b.rating " +
               "FROM BOOKINGS b " +
               "JOIN USERS su ON b.student_id = su.user_id " +
               "JOIN USERS tu ON b.tutor_id = tu.user_id " +
               "JOIN COURSES c ON b.course_id = c.course_id ";
    }

    private List<Booking> queryBookings(String sql, int idParam) throws SQLException {
        List<Booking> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idParam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBooking(rs));
                }
            }
        }
        return list;
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setStudentId(rs.getInt("student_id"));
        b.setStudentName(rs.getString("student_name"));
        b.setTutorId(rs.getInt("tutor_id"));
        b.setTutorName(rs.getString("tutor_name"));
        b.setCourseId(rs.getInt("course_id"));
        b.setCourseCode(rs.getString("course_code"));
        b.setCourseName(rs.getString("course_name"));
        b.setSessionDate(rs.getDate("session_date"));
        b.setTimeSlot(rs.getString("time_slot"));
        b.setStatus(rs.getString("status"));
        int r = rs.getInt("rating");
        b.setRating(rs.wasNull() ? null : r);
        return b;
    }
}