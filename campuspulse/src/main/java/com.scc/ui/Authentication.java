package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.SecurityUtil;
import com.scc.dao.UserDao;
import com.scc.model.ActivityLog;
import com.scc.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Authentication {
    private static final List<String> AVAILABLE_ROLES = List.of(
            "ADMIN", "SECURITY", "STAFF", "STUDENT", "TEACHER"
    );

    private final UserDao userDao;
    private final ActivityLogDao logDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, ActivityLogDao logDao, Scanner scanner) {
        this.userDao = userDao;
        this.logDao = logDao;
        this.scanner = scanner;
    }

    public User start() throws SQLException {
        while (true) {
            System.out.println("=== SCC Authentication ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    User loggedIn = login();
                    if (loggedIn != null) {
                        return loggedIn;
                    }
                }
                case "2" -> {
                    User registered = register();
                    if (registered != null) {
                        return registered;
                    }
                }
                case "3" -> {
                    return null;
                }
                default -> System.out.println("Invalid option. Please select 1, 2, or 3.\n");
            }
        }
    }

    public User login() throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        if (username.isBlank()) {
            System.out.println("Username cannot be empty.\n");
            return null;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Optional<User> userOpt = userDao.findByUsername(username);
        Optional<String> hashOpt = userDao.findPasswordHashByUsername(username);

        if (userOpt.isEmpty() || hashOpt.isEmpty() || !SecurityUtil.verifyPassword(password, hashOpt.get())) {
            System.out.println("Invalid username or password.\n");
            return null;
        }

        User user = userOpt.get();
        if (!"ACTIVE".equalsIgnoreCase(user.status())) {
            System.out.println("Account status is '" + user.status() + "'. Login denied.\n");
            return null;
        }

        System.out.println("Welcome back, " + user.firstName() + " " + user.lastName() + " (" + user.role() + ")!");
        return user;
    }

    public User register() throws SQLException {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine().trim();
        if (!SecurityUtil.isValidUsername(username)) {
            System.out.println("Invalid username format.\n");
            return null;
        }

        if (userDao.findByUsername(username).isPresent()) {
            System.out.println("Username '" + username + "' is already taken.\n");
            return null;
        }

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine().trim();
        if (firstName.isBlank()) {
            System.out.println("First name cannot be blank.\n");
            return null;
        }

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine().trim();
        if (lastName.isBlank()) {
            System.out.println("Last name cannot be blank.\n");
            return null;
        }

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        if (!SecurityUtil.isValidEmail(email)) {
            System.out.println("Invalid email format.\n");
            return null;
        }

        if (userDao.findByEmail(email).isPresent()) {
            System.out.println("Email '" + email + "' is already registered.\n");
            return null;
        }

        System.out.print("Enter password (min 6 characters): ");
        String password = scanner.nextLine();
        if (!SecurityUtil.isValidPassword(password)) {
            System.out.println("Password must be at least 6 characters.\n");
            return null;
        }

        String selectedRole = promptRole();
        if (selectedRole == null) {
            System.out.println("Registration cancelled.\n");
            return null;
        }

        String passwordHash = SecurityUtil.hashPassword(password);
        User created = userDao.create(username, passwordHash, selectedRole, firstName, lastName, email, "ACTIVE");
        logDao.save(new ActivityLog(
                0,
                created.userId(),
                "REGISTER",
                "Registered new account as " + created.role(),
                LocalDateTime.now(),
                "127.0.0.1"
        ));
        System.out.println("Account created for " + created.username() + " with role " + created.role() + "!\n");
        return created;
    }

    private String promptRole() {
        while (true) {
            System.out.println("Select a role:");
            for (int i = 0; i < AVAILABLE_ROLES.size(); i++) {
                System.out.println((i + 1) + ". " + AVAILABLE_ROLES.get(i));
            }
            System.out.println((AVAILABLE_ROLES.size() + 1) + ". Cancel");
            System.out.print("Choice: ");

            String input = scanner.nextLine().trim();
            try {
                int num = Integer.parseInt(input);
                if (num >= 1 && num <= AVAILABLE_ROLES.size()) {
                    return AVAILABLE_ROLES.get(num - 1);
                }
                if (num == AVAILABLE_ROLES.size() + 1) {
                    return null;
                }
            } catch (NumberFormatException ignored) {
                // Fall through to invalid message
            }
            System.out.println("Invalid role selection. Try again.");
        }
    }
}