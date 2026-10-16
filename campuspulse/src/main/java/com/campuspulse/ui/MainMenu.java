package com.campuspulse.ui;

import com.campuspulse.dao.UserDao;
import com.campuspulse.model.User;

import java.util.Optional;
import java.util.Scanner;

/** Entry menu: Register / Login / Exit. */
public class MainMenu {

    private final Scanner in = new Scanner(System.in);
    private final UserDao userDao = new UserDao();

    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== University Attendance & Grading Portal ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            switch (in.nextLine().trim()) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- Register Account ---");
        String username = prompt("Username: ");
        String password = prompt("Password: ");
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password are required.");
            return;
        }
        String role = null;
        while (role == null) {
            System.out.print("Role (1 = TEACHER, 2 = STUDENT): ");
            switch (in.nextLine().trim()) {
                case "1" -> role = "TEACHER";
                case "2" -> role = "STUDENT";
                default -> System.out.println("Please enter 1 or 2.");
            }
        }
        if (userDao.register(username, password, role)) {
            System.out.println("Account created with status PENDING. Await admin approval.");
        } else {
            System.out.println("Username already taken.");
        }
    }

    private void login() {
        System.out.println("\n--- Login ---");
        String username = prompt("Username: ");
        String password = prompt("Password: ");
        Optional<User> user = userDao.login(username, password);
        if (user.isPresent()) {
            new UserMenu(in, user.get()).run();
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private String prompt(String label) {
        System.out.print(label);
        return in.nextLine().trim();
    }
}
