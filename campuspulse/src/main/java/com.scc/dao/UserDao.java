package com.scc.dao;

import com.scc.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {
    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = """
            SELECT user_id, username, role, first_name, last_name, email, status
            FROM user
            WHERE LOWER(username) = LOWER(?)
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = """
            SELECT user_id, username, role, first_name, last_name, email, status
            FROM user
            WHERE LOWER(email) = LOWER(?)
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<String> findPasswordHashByUsername(String username) throws SQLException {
        String sql = "SELECT password_hash FROM user WHERE LOWER(username) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("password_hash"));
                }
            }
        }
        return Optional.empty();
    }

    public User create(String username, String passwordHash, String role,
                       String firstName, String lastName, String email, String status) throws SQLException {
        String sql = """
            INSERT INTO user (username, password_hash, role, first_name, last_name, email, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            stmt.setString(3, role);
            stmt.setString(4, firstName);
            stmt.setString(5, lastName);
            stmt.setString(6, email);
            stmt.setString(7, status);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new User(keys.getInt(1), username, role, firstName, lastName, email, status);
                }
            }
        }
        return findByUsername(username)
                .orElseThrow(() -> new SQLException("User created but could not be retrieved."));
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = """
            SELECT user_id, username, role, first_name, last_name, email, status
            FROM user
            ORDER BY user_id ASC
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        }
        return users;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("role"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("status")
        );
    }
}