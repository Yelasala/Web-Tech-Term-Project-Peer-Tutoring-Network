INSERT INTO USERS (full_name, university_id, email, password, role, active)
VALUES ('Yazan Student', '202410001', 'yazan.student@ajmanuni.ac.ae',
        'lMgXbx7/OUyqwZy0xljyLA==:fZX6BgU+F9C/gJ1TczlKAAGha/xpKmpzR5GBPNr0Aqk=', 'STUDENT', TRUE);

INSERT INTO USERS (full_name, university_id, email, password, role, active)
VALUES ('Omar Tutor', '202310045', 'omar.tutor@ajmanuni.ac.ae',
        '/ITHvx3pjgo6Q+d/3legiw==:mgTpde8Cv97eUgq1yqk9TFYDLt82xOz0IGi/SsssIQ4=', 'TUTOR', TRUE);

INSERT INTO USERS (full_name, university_id, email, password, role, active)
VALUES ('Platform Admin', 'ADMIN001', 'admin@ajmanuni.ac.ae',
        'rXl5h8fjEJaUdHuYwou5qg==:BjjrljDFjCYOOP5o4shrX/M2O4g1/wFAxNGwCzbwyAI=', 'ADMIN', TRUE);

INSERT INTO COURSES (course_code, course_name)
VALUES ('INT209', 'Data Structures');

INSERT INTO COURSES (course_code, course_name)
VALUES ('INT206', 'Fundamentals of Web Systems');

INSERT INTO COURSES (course_code, course_name)
VALUES ('INT322', 'Web Technologies');

INSERT INTO TUTOR_PROFILES (tutor_id, course_id, bio, availability, approval_status)
VALUES (2, 1, 'Experienced peer tutor for Data Structures, recursion, trees, and exam prep.', 'Mon & Wed 10:00 - 16:00', 'APPROVED');

INSERT INTO BOOKINGS (student_id, tutor_id, course_id, session_date, time_slot, status, rating)
VALUES (1, 2, 1, DATE('2026-11-26'), '10:00 - 11:00', 'CONFIRMED', NULL);