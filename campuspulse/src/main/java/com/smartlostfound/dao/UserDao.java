package com.smartlostfound.dao;

import com.smartlostfound.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, roles, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.username());
            ps.setString(2, user.password());
            ps.setString(3, user.roles());
            ps.setString(4, user.status());
            return ps.executeUpdate() == 1;
        }
    }

    public User login(String username, String password) throws SQLException {
        String sql = """
                SELECT id, username, password, roles, status
                FROM users
                WHERE username = ? AND password = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"),
                            rs.getString("password"), rs.getString("roles"),
                            rs.getString("status"));
                }
            }
        }
        return null;
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** All registered accounts, regardless of role or status. */
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT id, username, password, roles, status FROM users ORDER BY id";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    /** All accounts awaiting approval, across every role. */
    public List<User> getPendingUsers() throws SQLException {
        String sql = "SELECT id, username, password, roles, status FROM users WHERE status = ? ORDER BY id";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "PENDING");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        }
        return users;
    }

    /** Sets the given user's status to APPROVED. @return true if a row was updated. */
    public boolean approveUser(int userId) throws SQLException {
        String sql = "UPDATE users SET status = 'APPROVED' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() == 1;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("username"),
                rs.getString("password"), rs.getString("roles"),
                rs.getString("status"));
    }
}
