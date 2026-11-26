package com.peertutor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TutorProfile {

    private int tutorId;
    private String tutorName;
    private String universityId;
    private String email;
    private int courseId;
    private String courseCode;
    private String courseName;
    private String bio;
    private String availability;
    private String approvalStatus;
    private double avgRating;
    private int ratingCount;

    public int getTutorId() {
        return tutorId;
    }

    public void setTutorId(int tutorId) {
        this.tutorId = tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getUniversityId() {
        return universityId;
    }

    public void setUniversityId(String universityId) {
        this.universityId = universityId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public List<String> getTimeSlots() {
        List<String> slots = new ArrayList<>();
        if (availability != null && !availability.trim().isEmpty()) {
            Matcher m = Pattern
                    .compile("(\\d{1,2}:\\d{2})\\s*-\\s*(\\d{1,2}:\\d{2})")
                    .matcher(availability);
            while (m.find()) {
                int startHour = Integer.parseInt(m.group(1).split(":")[0]);
                int endHour = Integer.parseInt(m.group(2).split(":")[0]);
                if (endHour > startHour) {
                    for (int h = startHour; h < endHour; h++) {
                        String s = String.format("%02d:00 - %02d:00", h, h + 1);
                        if (!slots.contains(s)) {
                            slots.add(s);
                        }
                    }
                } else {
                    String exact = m.group(1) + " - " + m.group(2);
                    if (!slots.contains(exact)) {
                        slots.add(exact);
                    }
                }
            }
        }
        if (slots.isEmpty() && availability != null && !availability.trim().isEmpty()) {
            slots.add(availability.trim());
        }
        return slots;
    }
}