package com.smartlostfound.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:smart_lost_found.db";

    private DatabaseConnection() {
    }

    /** Opens a connection with SQLite foreign key enforcement turned on. */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            conn.close();
            throw e;
        }
        return conn;
    }
}
