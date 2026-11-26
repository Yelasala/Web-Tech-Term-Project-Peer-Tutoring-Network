<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - University Peer-to-Peer Tutoring Network</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<div class="navbar">
    <div class="brand">PeerTutorNetwork</div>
    <div class="nav-links">
        <a href="<%= request.getContextPath() %>/login.jsp">Login</a>
        <a href="<%= request.getContextPath() %>/register.jsp">Register</a>
    </div>
</div>

<div class="auth-wrapper">
    <div class="card">
        <h2>Create Account</h2>
        <p class="muted" style="margin-bottom: 16px;">Join as a Student or Peer Tutor</p>

        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-error"><%= error %></div>
        <% } %>

        <form action="<%= request.getContextPath() %>/auth" method="post">
            <input type="hidden" name="action" value="register">
            <div class="form-group">
                <label for="fullName">Full Name</label>
                <input type="text" id="fullName" name="fullName" class="form-control" required placeholder="e.g., Layla Hassan">
            </div>
            <div class="form-group">
                <label for="universityId">University ID</label>
                <input type="text" id="universityId" name="universityId" class="form-control" required placeholder="e.g., 202410999">
            </div>
            <div class="form-group">
                <label for="email">University Email</label>
                <input type="email" id="email" name="email" class="form-control" required placeholder="name@ajmanuni.ac.ae">
            </div>
            <div class="form-group">
                <label for="password">Password (min 4 chars)</label>
                <input type="password" id="password" name="password" class="form-control" minlength="4" required>
            </div>
            <div class="form-group">
                <label for="role">Role</label>
                <select id="role" name="role" class="form-control">
                    <option value="STUDENT">Student (Book Tutoring Sessions)</option>
                    <option value="TUTOR">Peer Tutor (Offer Course Tutoring)</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%;">Register</button>
        </form>

        <p class="muted" style="margin-top: 16px; text-align: center;">
            Already have an account? <a href="<%= request.getContextPath() %>/login.jsp">Sign in</a>
        </p>
    </div>
</div>
</body>
</html>