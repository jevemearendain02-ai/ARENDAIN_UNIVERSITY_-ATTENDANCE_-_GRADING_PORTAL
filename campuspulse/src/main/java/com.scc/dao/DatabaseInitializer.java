package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class DatabaseInitializer {
    private final Connection connection;

    public DatabaseInitializer(Connection connection) {
        this.connection = connection;
    }

    public void initialize() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS user (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username VARCHAR(100) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    role VARCHAR(50) NOT NULL,
                    first_name VARCHAR(100) NOT NULL,
                    last_name VARCHAR(100) NOT NULL,
                    email VARCHAR(150) NOT NULL UNIQUE,
                    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE'
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Courses (
                    course_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_code VARCHAR(30) NOT NULL UNIQUE,
                    course_name VARCHAR(150) NOT NULL,
                    credits INTEGER NOT NULL,
                    instructor_id INTEGER NOT NULL,
                    FOREIGN KEY (instructor_id) REFERENCES user(user_id)
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Enrollments (
                    enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER NOT NULL,
                    course_id INTEGER NOT NULL,
                    enrollment_date VARCHAR(30) NOT NULL,
                    status VARCHAR(30) NOT NULL DEFAULT 'ENROLLED',
                    FOREIGN KEY (student_id) REFERENCES user(user_id),
                    FOREIGN KEY (course_id) REFERENCES Courses(course_id),
                    UNIQUE (student_id, course_id)
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Attendance_Records (
                    attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    enrollment_id INTEGER NOT NULL,
                    date VARCHAR(30) NOT NULL,
                    status VARCHAR(20) NOT NULL CHECK (status IN ('Present', 'Absent', 'Late')),
                    remarks VARCHAR(255),
                    FOREIGN KEY (enrollment_id) REFERENCES Enrollments(enrollment_id)
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Grades (
                    grade_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    enrollment_id INTEGER NOT NULL,
                    assessment_name VARCHAR(120) NOT NULL,
                    score REAL NOT NULL,
                    max_score REAL NOT NULL,
                    weight REAL NOT NULL,
                    FOREIGN KEY (enrollment_id) REFERENCES Enrollments(enrollment_id)
                )
                """);

            ensureActivityLogsSchema(stmt);
        }

        String seedSql = """
            INSERT OR IGNORE INTO user (username, password_hash, role, first_name, last_name, email, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(seedSql)) {
            pstmt.setString(1, "admin");
            pstmt.setString(2, SecurityUtil.hashPassword("admin123"));
            pstmt.setString(3, "ADMIN");
            pstmt.setString(4, "System");
            pstmt.setString(5, "Administrator");
            pstmt.setString(6, "admin@scc.edu");
            pstmt.setString(7, "ACTIVE");
            pstmt.executeUpdate();
        }
    }

    private void ensureActivityLogsSchema(Statement stmt) throws SQLException {
        Set<String> columns = new HashSet<>();
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(Activity_Logs)")) {
            while (rs.next()) {
                columns.add(rs.getString("name").toLowerCase());
            }
        }

        boolean needsRecreate = !columns.isEmpty()
                && (!columns.contains("action_type") || !columns.contains("description") || !columns.contains("ip_address"));

        if (needsRecreate) {
            stmt.execute("ALTER TABLE Activity_Logs RENAME TO Activity_Logs_old");
        }

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS Activity_Logs (
                log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                action_type VARCHAR(50) NOT NULL,
                description VARCHAR(255) NOT NULL,
                timestamp TIMESTAMP NOT NULL,
                ip_address VARCHAR(64),
                FOREIGN KEY (user_id) REFERENCES user(user_id)
            )
            """);

        if (needsRecreate) {
            boolean hasLegacyAction = columns.contains("action");
            if (hasLegacyAction) {
                stmt.execute("""
                    INSERT INTO Activity_Logs (user_id, action_type, description, timestamp, ip_address)
                    SELECT user_id, 'LEGACY', action, timestamp, '127.0.0.1'
                    FROM Activity_Logs_old
                    """);
            }
            stmt.execute("DROP TABLE Activity_Logs_old");
        }
    }
}