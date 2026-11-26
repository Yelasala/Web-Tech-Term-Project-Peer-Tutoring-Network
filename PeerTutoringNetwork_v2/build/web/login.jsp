<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login - University Peer-to-Peer Tutoring Network</title>
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
        <h2>Sign In</h2>
        <p class="muted" style="margin-bottom: 16px;">University Peer-to-Peer Tutoring Network</p>

        <%
            String error = (String) request.getAttribute("error");
            String errParam = request.getParameter("error");
            String registered = request.getParameter("registered");
            String loggedOut = request.getParameter("loggedOut");
        %>
        <% if (error != null) { %>
            <div class="alert alert-error"><%= error %></div>
        <% } else if ("unauthorized".equals(errParam)) { %>
            <div class="alert alert-error">You are not authorized to access that role area.</div>
        <% } %>

        <% if ("1".equals(registered)) { %>
            <div class="alert alert-success">Registration successful. Please sign in with your credentials.</div>
        <% } else if ("1".equals(loggedOut)) { %>
            <div class="alert alert-success">You have been logged out.</div>
        <% } %>

        <form action="<%= request.getContextPath() %>/auth" method="post">
            <input type="hidden" name="action" value="login">
            <div class="form-group">
                <label for="email">University Email</label>
                <input type="email" id="email" name="email" class="form-control" required placeholder="name@ajmanuni.ac.ae">
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control" required placeholder="Enter password">
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%;">Login</button>
        </form>

        <div class="demo-box">
            <div class="demo-title">Sample Test Accounts (from <code>data.sql</code>)</div>
            <div class="demo-item">
                <strong>Student:</strong> <code>yazan.student@ajmanuni.ac.ae</code> / <code>student123</code>
            </div>
            <div class="demo-item">
                <strong>Tutor:</strong> <code>omar.tutor@ajmanuni.ac.ae</code> / <code>tutor123</code>
            </div>
            <div class="demo-item">
                <strong>Admin:</strong> <code>admin@ajmanuni.ac.ae</code> / <code>admin123</code>
            </div>
        </div>

        <p class="muted" style="margin-top: 16px; text-align: center;">
            New to the platform? <a href="<%= request.getContextPath() %>/register.jsp">Create an account</a>
        </p>
    </div>
</div>
</body>
</html>