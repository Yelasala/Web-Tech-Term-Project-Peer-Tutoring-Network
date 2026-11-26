<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.peertutor.model.User, com.peertutor.model.Course, com.peertutor.model.TutorProfile, com.peertutor.model.Booking" %>
<%
    User user = (User) session.getAttribute("user");
    List<Course> courses = (List<Course>) request.getAttribute("courses");
    List<TutorProfile> tutors = (List<TutorProfile>) request.getAttribute("tutors");
    List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
    Integer selectedCourseId = (Integer) request.getAttribute("selectedCourseId");
    String keyword = (String) request.getAttribute("keyword");
    if (keyword == null) keyword = "";

    String msg = request.getParameter("msg");
    String err = request.getParameter("err");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Student Dashboard - PeerTutorNetwork</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<div class="navbar">
    <div class="brand">PeerTutorNetwork — Student Portal</div>
    <div class="nav-links">
        <span>Welcome, <%= user.getFullName() %></span>
        <a href="<%= request.getContextPath() %>/student">Dashboard</a>
        <a href="<%= request.getContextPath() %>/auth?action=logout">Logout</a>
    </div>
</div>

<div class="container">
    <% if ("booked".equals(msg)) { %>
        <div class="alert alert-success">Tutoring session request submitted! Waiting for tutor confirmation.</div>
    <% } else if ("cancelled".equals(msg)) { %>
        <div class="alert alert-success">Booking cancelled.</div>
    <% } else if ("rated".equals(msg)) { %>
        <div class="alert alert-success">Thank you! Your session rating has been saved.</div>
    <% } %>

    <% if ("conflict".equals(err)) { %>
        <div class="alert alert-error">That tutor already has a pending or confirmed booking on that date and time slot.</div>
    <% } else if ("missing_slot".equals(err) || "bad_date".equals(err)) { %>
        <div class="alert alert-error">Please select a valid date and time slot before booking.</div>
    <% } else if ("rating".equals(err)) { %>
        <div class="alert alert-error">Please choose a rating between 1 and 5.</div>
    <% } %>

    <div class="card">
        <h3>Find Approved Peer Tutors</h3>
        <form action="<%= request.getContextPath() %>/student" method="get" class="filter-row">
            <div class="form-group">
                <label for="courseId">Course</label>
                <select id="courseId" name="courseId" class="form-control">
                    <option value="">All Courses</option>
                    <% if (courses != null) {
                        for (Course c : courses) {
                            boolean sel = (selectedCourseId != null && selectedCourseId == c.getCourseId());
                    %>
                        <option value="<%= c.getCourseId() %>" <%= sel ? "selected" : "" %>>
                            <%= c.getCourseCode() %> - <%= c.getCourseName() %>
                        </option>
                    <%  }
                       } %>
                </select>
            </div>
            <div class="form-group">
                <label for="keyword">Keyword (Tutor, Course, or Bio)</label>
                <input type="text" id="keyword" name="keyword" class="form-control" value="<%= keyword %>" placeholder="e.g., Data Structures, SQL, Tariq">
            </div>
            <div>
                <button type="submit" class="btn btn-primary">Search</button>
                <a href="<%= request.getContextPath() %>/student" class="btn btn-warning">Reset</a>
            </div>
        </form>
    </div>

    <div class="card">
        <h3>Available Tutors</h3>
        <% if (tutors == null || tutors.isEmpty()) { %>
            <p class="muted">No approved tutors match your current filter.</p>
        <% } else { %>
            <table>
                <thead>
                <tr>
                    <th>Tutor</th>
                    <th>Course</th>
                    <th>Bio &amp; Availability</th>
                    <th>Rating</th>
                    <th>Book Session</th>
                </tr>
                </thead>
                <tbody>
                <% for (TutorProfile t : tutors) { %>
                    <tr>
                        <td>
                            <strong><%= t.getTutorName() %></strong><br>
                            <span class="muted"><%= t.getUniversityId() %></span>
                        </td>
                        <td><strong><%= t.getCourseCode() %></strong><br><span class="muted"><%= t.getCourseName() %></span></td>
                        <td>
                            <div><%= t.getBio() %></div>
                            <div class="muted"><strong>Availability:</strong> <%= t.getAvailability() %></div>
                        </td>
                        <td>
                            <% if (t.getRatingCount() > 0) { %>
                                <strong><%= String.format("%.1f", t.getAvgRating()) %> / 5</strong> (<%= t.getRatingCount() %>)
                            <% } else { %>
                                <span class="muted">No ratings yet</span>
                            <% } %>
                        </td>
                        <td>
                            <form action="<%= request.getContextPath() %>/student" method="post" class="inline-form">
                                <input type="hidden" name="action" value="book">
                                <input type="hidden" name="tutorId" value="<%= t.getTutorId() %>">
                                <input type="hidden" name="courseId" value="<%= t.getCourseId() %>">
                                <input type="date" name="sessionDate" class="form-control" style="width: 145px;" required>
                                <select name="timeSlot" class="form-control" style="width: 165px;" required>
                                    <option value="">Select slot</option>
                                    <% for (String slot : t.getTimeSlots()) { %>
                                        <option value="<%= slot %>"><%= slot %></option>
                                    <% } %>
                                </select>
                                <button type="submit" class="btn btn-success btn-sm">Book</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        <% } %>
    </div>

    <div class="card">
        <h3>My Tutoring Bookings</h3>
        <% if (bookings == null || bookings.isEmpty()) { %>
            <p class="muted">You have not booked any tutoring sessions yet.</p>
        <% } else { %>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Course</th>
                    <th>Tutor</th>
                    <th>Date &amp; Slot</th>
                    <th>Status</th>
                    <th>Rating / Action</th>
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
                        <td><%= b.getCourseCode() %> — <%= b.getCourseName() %></td>
                        <td><%= b.getTutorName() %></td>
                        <td><%= b.getSessionDate() %> (<%= b.getTimeSlot() %>)</td>
                        <td><span class="badge <%= badgeClass %>"><%= st %></span></td>
                        <td>
                            <% if ("PENDING".equals(st) || "CONFIRMED".equals(st)) { %>
                                <form action="<%= request.getContextPath() %>/student" method="post" class="inline-form">
                                    <input type="hidden" name="action" value="cancel">
                                    <input type="hidden" name="bookingId" value="<%= b.getBookingId() %>">
                                    <button type="submit" class="btn btn-danger btn-sm">Cancel</button>
                                </form>
                            <% } else if ("COMPLETED".equals(st)) { %>
                                <% if (b.getRating() != null) { %>
                                    <strong>Rated: <%= b.getRating() %> / 5</strong>
                                <% } else { %>
                                    <form action="<%= request.getContextPath() %>/student" method="post" class="inline-form">
                                        <input type="hidden" name="action" value="rate">
                                        <input type="hidden" name="bookingId" value="<%= b.getBookingId() %>">
                                        <select name="rating" class="form-control" style="width: 80px;" required>
                                            <option value="5">5</option>
                                            <option value="4">4</option>
                                            <option value="3">3</option>
                                            <option value="2">2</option>
                                            <option value="1">1</option>
                                        </select>
                                        <button type="submit" class="btn btn-primary btn-sm">Rate</button>
                                    </form>
                                <% } %>
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