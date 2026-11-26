CREATE TABLE USERS (
    user_id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    full_name VARCHAR(100) NOT NULL,
    university_id VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (user_id)
);

CREATE TABLE COURSES (
    course_id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(120) NOT NULL,
    PRIMARY KEY (course_id)
);

CREATE TABLE TUTOR_PROFILES (
    tutor_id INT NOT NULL,
    course_id INT NOT NULL,
    bio VARCHAR(500) NOT NULL,
    availability VARCHAR(200) NOT NULL,
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (tutor_id),
    CONSTRAINT fk_tutor_user FOREIGN KEY (tutor_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_tutor_course FOREIGN KEY (course_id) REFERENCES COURSES(course_id)
);

CREATE TABLE BOOKINGS (
    booking_id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    student_id INT NOT NULL,
    tutor_id INT NOT NULL,
    course_id INT NOT NULL,
    session_date DATE NOT NULL,
    time_slot VARCHAR(40) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    rating INT,
    PRIMARY KEY (booking_id),
    CONSTRAINT fk_booking_student FOREIGN KEY (student_id) REFERENCES USERS(user_id),
    CONSTRAINT fk_booking_tutor FOREIGN KEY (tutor_id) REFERENCES USERS(user_id),
    CONSTRAINT fk_booking_course FOREIGN KEY (course_id) REFERENCES COURSES(course_id)
);