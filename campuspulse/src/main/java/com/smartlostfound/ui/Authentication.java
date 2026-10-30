package com.smartlostfound.ui;

import com.smartlostfound.dao.UserDao;
import com.smartlostfound.model.Role;
import com.smartlostfound.model.User;
import java.sql.SQLException;
import java.util.Scanner;

/** Handles the startup menu: registration, login, and routing to role-based menus. */
public class Authentication {
    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n==================================");
            System.out.println(" SMART HIGH SCHOOL LOST AND FOUND");
            System.out.println("==================================");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> registerAccount();
                case "2" -> running = !loginAccount();
                case "3" -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }
        System.out.println("Thank you for using the system!");
    }

    private void registerAccount() {
        try {
            System.out.println("\n--- REGISTER ACCOUNT ---");
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            if (username.isBlank() || password.isBlank()) {
                System.out.println("Username and password are required.");
                return;
            }
            if (userDao.usernameExists(username)) {
                System.out.println("Username already exists.");
                return;
            }

            Role role = selectRole();
            if (role == null) {
                System.out.println("Invalid role selection.");
                return;
            }

            User user = new User(0, username, password, role.name(), "PENDING");
            if (userDao.register(user)) {
                System.out.println("Registration successful!");
                System.out.println("Account status: PENDING");
                System.out.println("Wait for account approval.");
            }
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private Role selectRole() {
        Role[] roles = Role.values();
        System.out.println("Select role:");
        for (int i = 0; i < roles.length; i++) {
            System.out.println((i + 1) + ". " + roles[i]);
        }
        System.out.print("Enter choice: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return (choice >= 1 && choice <= roles.length) ? roles[choice - 1] : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** @return true if the user chose to exit the application from inside their menu. */
    private boolean loginAccount() {
        try {
            System.out.println("\n--- LOGIN ---");
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = userDao.login(username, password);
            if (user == null) {
                System.out.println("Invalid username or password.");
                return false;
            }
            if (!"APPROVED".equals(user.status())) {
                System.out.println("Your account is " + user.status() + ".");
                System.out.println("Please wait for approval.");
                return false;
            }

            System.out.println("\nLogin successful!");
            System.out.println("Welcome, " + user.username());
            System.out.println("Role: " + user.roles());
            return routeToMenu(user);
        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
            return false;
        }
    }

    private boolean routeToMenu(User user) {
        Role role;
        try {
            role = Role.valueOf(user.roles());
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown role: " + user.roles());
            return false;
        }

        RoleMenu menu = switch (role) {
            case ADMIN -> new AdminMenu(user, userDao, scanner);
            case TEACHER -> new TeacherMenu(user, userDao, scanner);
            case STUDENT -> new StudentMenu(user, userDao, scanner);
            case STAFF -> new StaffMenu(user, userDao, scanner);
            case SECURITY -> new SecurityMenu(user, userDao, scanner);
        };
        return menu.show();
    }
}
