package com.campuspulse.ui;

import com.campuspulse.model.User;

import java.util.Scanner;

/** Menu shown after a successful login. Logout returns to the main menu. */
public class UserMenu {

    private final Scanner in;
    private final User user;

    public UserMenu(Scanner in, User user) {
        this.in = in;
        this.user = user;
    }

    public void run() {
        System.out.println("\nWelcome, " + user.username() + " [" + user.roles() + "]");
        if ("PENDING".equals(user.status())) {
            System.out.println("Note: your account is still PENDING approval.");
        }
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n1. View Profile");
            System.out.println("2. Logout");
            System.out.print("Choose: ");
            switch (in.nextLine().trim()) {
                case "1" -> System.out.printf("ID: %d | Username: %s | Role: %s | Status: %s%n",
                        user.id(), user.username(), user.roles(), user.status());
                case "2" -> {
                    System.out.println("Logged out successfully.");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
