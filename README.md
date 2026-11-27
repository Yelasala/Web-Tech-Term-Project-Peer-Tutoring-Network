# PeerTutorNetwork_v2

A Java web application for a University Peer-to-Peer Tutoring Network built with Servlets, JSP, JDBC, Apache Derby, and GlassFish 7.

## What You Need Before Starting

- **JDK 17** (for example, Zulu 17 or Eclipse Temurin JDK 17)
- **Apache NetBeans** with **JDK 17** selected under **Tools > Java Platforms**
- **Eclipse GlassFish 7** added to NetBeans and set to run with **JDK 17**
- **Apache Derby** database server (`jdbc:derby://localhost:1527/peertutordb`)
- **Apache Derby JAR files** (`derbyclient.jar` and `derby.jar`) added to the project's **Libraries**

## How to Set Up and Run the Project

### 1. Open the Project in NetBeans
1. Download or clone the folder from GitHub.
2. Open **Apache NetBeans**, click **File > Open Project**, and select the project folder.

### 2. Check Project Settings in NetBeans
1. Right-click the project in the **Projects** list and click **Properties**.
2. Click **Sources** and make sure **Source/Binary Format** is set to **JDK 17**.
3. Click **Run** and make sure your **GlassFish Server** is selected.
4. Click **Libraries** and make sure the GlassFish / Jakarta EE libraries and Apache Derby JAR files are listed.

### 3. Create and Fill the Database (`peertutordb`)
1. Open the **Services** tab in NetBeans (**Window > Services**), expand **Databases**, start the **Java DB / Apache Derby** server if it is stopped, and connect to `peertutordb` (or create a new database named `peertutordb` on `localhost:1527`).
2. Right-click `peertutordb` and click **Execute Command...**.
3. Copy and run the SQL code from `sql/schema.sql` to create the tables (`USERS`, `COURSES`, `TUTOR_PROFILES`, and `BOOKINGS`).
4. Copy and run the SQL code from `sql/data.sql` to add the sample users, courses, tutor profile, and booking.

> **Note:** Depending on your Apache Derby version, the `active` column in `USERS` may use `BOOLEAN` (`TRUE`/`FALSE`) or `SMALLINT` (`1`/`0`). Make sure your table column and `UserDAO.java` match.

### 4. Build and Run
1. Right-click the project and click **Clean and Build**.
2. Click **Run** to start the app in your browser.
3. Any new user created on the **Register** page (`register.jsp`) will automatically have their password saved with a random salt and hash.
