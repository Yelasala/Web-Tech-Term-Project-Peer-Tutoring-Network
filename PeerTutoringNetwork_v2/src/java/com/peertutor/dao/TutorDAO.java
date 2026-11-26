package com.peertutor.dao;

import com.peertutor.model.TutorProfile;
import com.peertutor.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TutorDAO {

    public boolean upsertTutorProfile(int tutorId, int courseId, String bio, String availability) throws SQLException {
        String checkSql = "SELECT tutor_id FROM TUTOR_PROFILES WHERE tutor_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setInt(1, tutorId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    String updateSql = "UPDATE TUTOR_PROFILES SET course_id = ?, bio = ?, availability = ?, " +
                                       "approval_status = 'PENDING' WHERE tutor_id = ?";
                    try (PreparedStatement upPs = con.prepareStatement(updateSql)) {
                        upPs.setInt(1, courseId);
                        upPs.setString(2, bio.trim());
                        upPs.setString(3, availability.trim());
                        upPs.setInt(4, tutorId);
                        return upPs.executeUpdate() > 0;
                    }
                } else {
                    String insertSql = "INSERT INTO TUTOR_PROFILES (tutor_id, course_id, bio, availability, approval_status) " +
                                       "VALUES (?, ?, ?, ?, 'PENDING')";
                    try (PreparedStatement inPs = con.prepareStatement(insertSql)) {
                        inPs.setInt(1, tutorId);
                        inPs.setInt(2, courseId);
                        inPs.setString(3, bio.trim());
                        inPs.setString(4, availability.trim());
                        return inPs.executeUpdate() > 0;
                    }
                }
            }
        }
    }

    public TutorProfile getProfileByTutorId(int tutorId) throws SQLException {
        String sql = "SELECT tp.tutor_id, u.full_name, u.university_id, u.email, tp.course_id, " +
                     "c.course_code, c.course_name, tp.bio, tp.availability, tp.approval_status, " +
                     "COALESCE((SELECT AVG(CAST(b.rating AS DOUBLE)) FROM BOOKINGS b " +
                     "          WHERE b.tutor_id = tp.tutor_id AND b.rating IS NOT NULL), 0.0) AS avg_rating, " +
                     "COALESCE((SELECT COUNT(*) FROM BOOKINGS b " +
                     "          WHERE b.tutor_id = tp.tutor_id AND b.rating IS NOT NULL), 0) AS rating_count " +
                     "FROM TUTOR_PROFILES tp " +
                     "JOIN USERS u ON tp.tutor_id = u.user_id " +
                     "JOIN COURSES c ON tp.course_id = c.course_id " +
                     "WHERE tp.tutor_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tutorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTutor(rs);
                }
            }
        }
        return null;
    }

    public List<TutorProfile> getApprovedTutors(Integer courseIdFilter, String keyword) throws SQLException {
        List<TutorProfile> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder(
            "SELECT tp.tutor_id, u.full_name, u.university_id, u.email, tp.course_id, " +
            "c.course_code, c.course_name, tp.bio, tp.availability, tp.approval_status, " +
            "COALESCE((SELECT AVG(CAST(b.rating AS DOUBLE)) FROM BOOKINGS b " +
            "          WHERE b.tutor_id = tp.tutor_id AND b.rating IS NOT NULL), 0.0) AS avg_rating, " +
            "COALESCE((SELECT COUNT(*) FROM BOOKINGS b " +
            "          WHERE b.tutor_id = tp.tutor_id AND b.rating IS NOT NULL), 0) AS rating_count " +
            "FROM TUTOR_PROFILES tp " +
            "JOIN USERS u ON tp.tutor_id = u.user_id " +
            "JOIN COURSES c ON tp.course_id = c.course_id " +
            "WHERE tp.approval_status = 'APPROVED' AND u.active = TRUE "
        );
        List<Object> params = new ArrayList<>();
        if (courseIdFilter != null && courseIdFilter > 0) {
            sb.append("AND tp.course_id = ? ");
            params.add(courseIdFilter);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            sb.append("AND (LOWER(u.full_name) LIKE ? OR LOWER(c.course_code) LIKE ? " +
                      "OR LOWER(c.course_name) LIKE ? OR LOWER(tp.bio) LIKE ?) ");
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        sb.append("ORDER BY u.full_name");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTutor(rs));
                }
            }
        }
        return list;
    }

    public List<TutorProfile> getAllTutorProfiles() throws SQLException {
        List<TutorProfile> list = new ArrayList<>();
        String sql = "SELECT tp.tutor_id, u.full_name, u.university_id, u.email, tp.course_id, " +
                     "c.course_code, c.course_name, tp.bio, tp.availability, tp.approval_status, " +
                     "COALESCE((SELECT AVG(CAST(b.rating AS DOUBLE)) FROM BOOKINGS b " +
                     "          WHERE b.tutor_id = tp.tutor_id AND b.rating IS NOT NULL), 0.0) AS avg_rating, " +
                     "COALESCE((SELECT COUNT(*) FROM BOOKINGS b " +
                     "          WHERE b.tutor_id = tp.tutor_id AND b.rating IS NOT NULL), 0) AS rating_count " +
                     "FROM TUTOR_PROFILES tp " +
                     "JOIN USERS u ON tp.tutor_id = u.user_id " +
                     "JOIN COURSES c ON tp.course_id = c.course_id " +
                     "ORDER BY tp.approval_status DESC, u.full_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapTutor(rs));
            }
        }
        return list;
    }

    public boolean updateApprovalStatus(int tutorId, String status) throws SQLException {
        String sql = "UPDATE TUTOR_PROFILES SET approval_status = ? WHERE tutor_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, tutorId);
            return ps.executeUpdate() > 0;
        }
    }

    private TutorProfile mapTutor(ResultSet rs) throws SQLException {
        TutorProfile tp = new TutorProfile();
        tp.setTutorId(rs.getInt("tutor_id"));
        tp.setTutorName(rs.getString("full_name"));
        tp.setUniversityId(rs.getString("university_id"));
        tp.setEmail(rs.getString("email"));
        tp.setCourseId(rs.getInt("course_id"));
        tp.setCourseCode(rs.getString("course_code"));
        tp.setCourseName(rs.getString("course_name"));
        tp.setBio(rs.getString("bio"));
        tp.setAvailability(rs.getString("availability"));
        tp.setApprovalStatus(rs.getString("approval_status"));
        tp.setAvgRating(rs.getDouble("avg_rating"));
        tp.setRatingCount(rs.getInt("rating_count"));
        return tp;
    }
}