<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.peertutor.model.User, com.peertutor.model.Course, com.peertutor.model.TutorProfile, com.peertutor.model.Booking" %>
<%
    User user = (User) session.getAttribute("user");
    List<Course> courses = (List<Course>) request.getAttribute("courses");
    TutorProfile profile = (TutorProfile) request.getAttribute("profile");
    List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");

    String msg = request.getParameter("msg");
    String err = request.getParameter("err");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Tutor Dashboard - PeerTutorNetwork</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<div class="navbar">
    <div class="brand">PeerTutorNetwork — Tutor Portal</div>
    <div class="nav-links">
        <span>Welcome, <%= user.getFullName() %></span>
        <a href="<%= request.getContextPath() %>/tutor">Dashboard</a>
        <a href="<%= request.getContextPath() %>/auth?action=logout">Logout</a>
    </div>
</div>

<div class="container">
    <% if ("profile_saved".equals(msg)) { %>
        <div class="alert alert-success">Tutor profile saved! Status is set to PENDING for admin review.</div>
    <% } else if ("status_updated".equals(msg)) { %>
        <div class="alert alert-success">Booking status updated.</div>
    <% } %>

    <% if ("empty_profile".equals(err)) { %>
        <div class="alert alert-error">Please fill in both your bio and availability before saving.</div>
    <% } else if ("bad_status".equals(err)) { %>
        <div class="alert alert-error">Invalid booking status update.</div>
    <% } %>

    <div class="card">
        <h3>My Tutor Profile</h3>
        <% if (profile != null) {
            String appSt = profile.getApprovalStatus();
            String appBadge = "badge-pending";
            if ("APPROVED".equals(appSt)) appBadge = "badge-approved";
            else if ("REJECTED".equals(appSt)) appBadge = "badge-rejected";
        %>
            <p style="margin-bottom: 14px;">
                <strong>Current Approval Status:</strong> <span class="badge <%= appBadge %>"><%= appSt %></span>
                <% if (profile.getRatingCount() > 0) { %>
                    &nbsp;|&nbsp; <strong>Average Rating:</strong> <%= String.format("%.1f", profile.getAvgRating()) %> / 5 (<%= profile.getRatingCount() %> ratings)
                <% } %>
            </p>
        <% } else { %>
            <p class="muted" style="margin-bottom: 14px;">You have not created a tutor profile yet. Submit one below so an admin can approve you.</p>
        <% } %>

        <form action="<%= request.getContextPath() %>/tutor" method="post">
            <input type="hidden" name="action" value="saveProfile">
            <div class="form-group">
                <label for="courseId">Primary Course to Tutor</label>
                <select id="courseId" name="courseId" class="form-control" required>
                    <% if (courses != null) {
                        for (Course c : courses) {
                            boolean selected = (profile != null && profile.getCourseId() == c.getCourseId());
                    %>
                        <option value="<%= c.getCourseId() %>" <%= selected ? "selected" : "" %>>
                            <%= c.getCourseCode() %> - <%= c.getCourseName() %>
                        </option>
                    <%  }
                       } %>
                </select>
            </div>
            <div class="form-group">
                <label for="bio">Tutoring Bio &amp; Strengths</label>
                <textarea id="bio" name="bio" rows="3" class="form-control" required placeholder="Describe topics you help students with..."><%= (profile != null && profile.getBio() != null) ? profile.getBio() : "" %></textarea>
            </div>
            <div class="form-group">
                <label for="availability">Availability Schedule</label>
                <input type="text" id="availability" name="availability" class="form-control" required
                       value="<%= (profile != null && profile.getAvailability() != null) ? profile.getAvailability() : "" %>"
                       placeholder="e.g., Mon &amp; Wed 10:00 - 16:00">
            </div>
            <button type="submit" class="btn btn-primary">Save Tutor Profile</button>
        </form>
    </div>

    <div class="card">
        <h3>Session Requests &amp; Bookings</h3>
        <% if (bookings == null || bookings.isEmpty()) { %>
            <p class="muted">No booking requests for your profile yet.</p>
        <% } else { %>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Student</th>
                    <th>Course</th>
                    <th>Date &amp; Slot</th>
                    <th>Status</th>
                    <th>Rating</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <% for (Booking b : bookings) {
                    String st = b.getStatus();
                    String badgeClass = "badge-pending";
                    if ("CONFIRMED".equals(st)) badgeClass = "badge-confirmed";
                    else if ("COMPLETED".equals(st)) badgeClass = "badge-completed";
                    else if ("DECLINED".equals(st) || "CANCELLED".equals(st)) badgeClass = "badge-cancelled";
                %>
                    <tr>
                        <td>#<%= b.getBookingId() %></td>
                        <td><%= b.getStudentName() %></td>
                        <td><%= b.getCourseCode() %></td>
                        <td><%= b.getSessionDate() %> (<%= b.getTimeSlot() %>)</td>
                        <td><span class="badge <%= badgeClass %>"><%= st %></span></td>
                        <td><%= (b.getRating() != null) ? (b.getRating() + " / 5") : "<span class='muted'>—</span>" %></td>
                        <td>
                            <% if ("PENDING".equals(st)) { %>
                                <form action="<%= request.getContextPath() %>/tutor" method="post" class="inline-form">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="bookingId" value="<%= b.getBookingId() %>">
                                    <button type="submit" name="status" value="CONFIRMED" class="btn btn-success btn-sm">Confirm</button>
                                    <button type="submit" name="status" value="DECLINED" class="btn btn-danger btn-sm">Decline</button>
                                </form>
                            <% } else if ("CONFIRMED".equals(st)) { %>
                                <form action="<%= request.getContextPath() %>/tutor" method="post" class="inline-form">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="bookingId" value="<%= b.getBookingId() %>">
                                    <button type="submit" name="status" value="COMPLETED" class="btn btn-primary btn-sm">Mark Completed</button>
                                </form>
                            <% } else { %>
                                <span class="muted">—</span>
                            <% } %>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        <% } %>
    </div>
</div>
</body>
</html>