<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.peertutor.model.User, com.peertutor.model.Course, com.peertutor.model.TutorProfile, com.peertutor.model.Booking" %>
<%
    User user = (User) session.getAttribute("user");
    List<User> users = (List<User>) request.getAttribute("users");
    List<Course> courses = (List<Course>) request.getAttribute("courses");
    List<TutorProfile> tutorProfiles = (List<TutorProfile>) request.getAttribute("tutorProfiles");
    List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");

    String msg = request.getParameter("msg");
    String err = request.getParameter("err");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - PeerTutorNetwork</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<div class="navbar">
    <div class="brand">PeerTutorNetwork — Admin Portal</div>
    <div class="nav-links">
        <span>Admin: <%= user.getFullName() %></span>
        <a href="<%= request.getContextPath() %>/admin">Dashboard</a>
        <a href="<%= request.getContextPath() %>/auth?action=logout">Logout</a>
    </div>
</div>

<div class="container">
    <% if ("tutor_updated".equals(msg)) { %>
        <div class="alert alert-success">Tutor approval status updated.</div>
    <% } else if ("course_added".equals(msg)) { %>
        <div class="alert alert-success">New course added to catalog.</div>
    <% } else if ("user_updated".equals(msg)) { %>
        <div class="alert alert-success">User account status updated.</div>
    <% } %>

    <% if ("course_empty".equals(err)) { %>
        <div class="alert alert-error">Course code and course name are required.</div>
    <% } else if ("course_exists".equals(err)) { %>
        <div class="alert alert-error">That course code already exists.</div>
    <% } else if ("bad_status".equals(err)) { %>
        <div class="alert alert-error">Invalid approval status selected.</div>
    <% } %>

    <div class="card">
        <h3>Tutor Profile Approvals</h3>
        <% if (tutorProfiles == null || tutorProfiles.isEmpty()) { %>
            <p class="muted">No tutor profiles submitted yet.</p>
        <% } else { %>
            <table>
                <thead>
                <tr>
                    <th>Tutor</th>
                    <th>Course</th>
                    <th>Availability</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <% for (TutorProfile tp : tutorProfiles) {
                    String st = tp.getApprovalStatus();
                    String badgeClass = "badge-pending";
                    if ("APPROVED".equals(st)) badgeClass = "badge-approved";
                    else if ("REJECTED".equals(st)) badgeClass = "badge-rejected";
                %>
                    <tr>
                        <td><strong><%= tp.getTutorName() %></strong> <span class="muted">(<%= tp.getUniversityId() %>)</span></td>
                        <td><%= tp.getCourseCode() %> — <%= tp.getCourseName() %></td>
                        <td><%= tp.getAvailability() %></td>
                        <td><span class="badge <%= badgeClass %>"><%= st %></span></td>
                        <td>
                            <form action="<%= request.getContextPath() %>/admin" method="post" class="inline-form">
                                <input type="hidden" name="action" value="approveTutor">
                                <input type="hidden" name="tutorId" value="<%= tp.getTutorId() %>">
                                <button type="submit" name="status" value="APPROVED" class="btn btn-success btn-sm">Approve</button>
                                <button type="submit" name="status" value="REJECTED" class="btn btn-danger btn-sm">Reject</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        <% } %>
    </div>

    <div class="card">
        <h3>Course Catalog Management</h3>
        <form action="<%= request.getContextPath() %>/admin" method="post" class="filter-row" style="margin-bottom: 16px;">
            <input type="hidden" name="action" value="addCourse">
            <div class="form-group">
                <label for="courseCode">Course Code</label>
                <input type="text" id="courseCode" name="courseCode" class="form-control" required placeholder="e.g., INT301">
            </div>
            <div class="form-group">
                <label for="courseName">Course Name</label>
                <input type="text" id="courseName" name="courseName" class="form-control" required placeholder="e.g., Object-Oriented Programming">
            </div>
            <div>
                <button type="submit" class="btn btn-primary">Add Course</button>
            </div>
        </form>

        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Course Code</th>
                <th>Course Name</th>
            </tr>
            </thead>
            <tbody>
            <% if (courses != null) {
                for (Course c : courses) { %>
                <tr>
                    <td>#<%= c.getCourseId() %></td>
                    <td><strong><%= c.getCourseCode() %></strong></td>
                    <td><%= c.getCourseName() %></td>
                </tr>
            <%  }
               } %>
            </tbody>
        </table>
    </div>

    <div class="card">
        <h3>User Accounts</h3>
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Full Name</th>
                <th>University ID</th>
                <th>Email</th>
                <th>Role</th>
                <th>Active</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <% if (users != null) {
                for (User u : users) { %>
                <tr>
                    <td>#<%= u.getUserId() %></td>
                    <td><%= u.getFullName() %></td>
                    <td><%= u.getUniversityId() %></td>
                    <td><%= u.getEmail() %></td>
                    <td><strong><%= u.getRole() %></strong></td>
                    <td><%= u.isActive() ? "Active" : "Disabled" %></td>
                    <td>
                        <% if (!"ADMIN".equalsIgnoreCase(u.getRole())) { %>
                            <form action="<%= request.getContextPath() %>/admin" method="post" class="inline-form">
                                <input type="hidden" name="action" value="toggleUser">
                                <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                                <input type="hidden" name="active" value="<%= !u.isActive() %>">
                                <button type="submit" class="btn <%= u.isActive() ? "btn-warning" : "btn-success" %> btn-sm">
                                    <%= u.isActive() ? "Disable" : "Enable" %>
                                </button>
                            </form>
                        <% } else { %>
                            <span class="muted">Protected</span>
                        <% } %>
                    </td>
                </tr>
            <%  }
               } %>
            </tbody>
        </table>
    </div>

    <div class="card">
        <h3>All Platform Bookings (<%= (bookings != null) ? bookings.size() : 0 %>)</h3>
        <% if (bookings == null || bookings.isEmpty()) { %>
            <p class="muted">No bookings recorded yet.</p>
        <% } else { %>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Student</th>
                    <th>Tutor</th>
                    <th>Course</th>
                    <th>Date &amp; Slot</th>
                    <th>Status</th>
                    <th>Rating</th>
                </tr>
                </thead>
                <tbody>
                <% for (Booking b : bookings) { %>
                    <tr>
                        <td>#<%= b.getBookingId() %></td>
                        <td><%= b.getStudentName() %></td>
                        <td><%= b.getTutorName() %></td>
                        <td><%= b.getCourseCode() %></td>
                        <td><%= b.getSessionDate() %> (<%= b.getTimeSlot() %>)</td>
                        <td><%= b.getStatus() %></td>
                        <td><%= (b.getRating() != null) ? (b.getRating() + " / 5") : "—" %></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        <% } %>
    </div>
</div>
</body>
</html>