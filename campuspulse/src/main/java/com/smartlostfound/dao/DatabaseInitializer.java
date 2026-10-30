package com.smartlostfound.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Makes sure every required table exists before the CLI starts. */
public class DatabaseInitializer {
    private static final String CREATE_USERS_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                roles TEXT NOT NULL CHECK (roles IN ('ADMIN', 'TEACHER', 'STUDENT', 'STAFF', 'SECURITY')),
                status TEXT NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
            )
            """;

    private static final String CREATE_USERS_NEW_TABLE = """
            CREATE TABLE users_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                roles TEXT NOT NULL CHECK (roles IN ('ADMIN', 'TEACHER', 'STUDENT', 'STAFF', 'SECURITY')),
                status TEXT NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
            )
            """;

    private static final String SEED_ADMIN = """
            INSERT INTO users (username, password, roles, status)
            SELECT ?, ?, 'ADMIN', 'APPROVED'
            WHERE NOT EXISTS (SELECT 1 FROM users WHERE roles = 'ADMIN')
            """;

    private DatabaseInitializer() {
    }

    public static void initialize() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            if (usersTableIsOutdated(stmt)) {
                migrateUsersTable(stmt);
            }
            stmt.execute(CREATE_USERS_TABLE);
            seedDefaultAdmin(conn);
        }
    }

    /** An older database may have a users table whose CHECK only allows TEACHER and STUDENT. */
    private static boolean usersTableIsOutdated(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery(
                "SELECT sql FROM sqlite_master WHERE type = 'table' AND name = 'users'")) {
            if (!rs.next()) {
                return false;
            }
            String ddl = rs.getString(1);
            return !(ddl.contains("'ADMIN'") && ddl.contains("'STAFF'") && ddl.contains("'SECURITY'"));
        }
    }

    private static void migrateUsersTable(Statement stmt) throws SQLException {
        stmt.execute("DROP TABLE IF EXISTS users_new");
        stmt.execute(CREATE_USERS_NEW_TABLE);
        stmt.execute("INSERT INTO users_new (id, username, password, roles, status) SELECT id, username, password, roles, status FROM users");
        stmt.execute("DROP TABLE users");
        stmt.execute("ALTER TABLE users_new RENAME TO users");
    }

    /** Ensures at least one approved ADMIN exists so pending accounts can be approved. */
    private static void seedDefaultAdmin(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SEED_ADMIN)) {
            ps.setString(1, "admin");
            ps.setString(2, "admin123");
            ps.executeUpdate();
        }
    }
}
