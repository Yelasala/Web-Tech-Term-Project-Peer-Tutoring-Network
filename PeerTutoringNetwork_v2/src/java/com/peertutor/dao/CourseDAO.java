package com.peertutor.dao;

import com.peertutor.model.Course;
import com.peertutor.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT course_id, course_code, course_name FROM COURSES ORDER BY course_code";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("course_id"));
                c.setCourseCode(rs.getString("course_code"));
                c.setCourseName(rs.getString("course_name"));
                list.add(c);
            }
        }
        return list;
    }

    public boolean addCourse(String courseCode, String courseName) throws SQLException {
        String sql = "INSERT INTO COURSES (course_code, course_name) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim().toUpperCase());
            ps.setString(2, courseName.trim());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean courseCodeExists(String courseCode) throws SQLException {
        String sql = "SELECT 1 FROM COURSES WHERE UPPER(course_code) = UPPER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}