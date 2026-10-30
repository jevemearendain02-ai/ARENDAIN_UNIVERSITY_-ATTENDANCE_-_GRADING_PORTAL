package com.smartlostfound.ui;

import com.smartlostfound.dao.UserDao;
import com.smartlostfound.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu extends RoleMenu {
    public AdminMenu(User user, UserDao userDao, Scanner scanner) {
        super(user, userDao, scanner);
    }

    @Override
    protected String title() {
        return "ADMIN MENU";
    }

    @Override
    protected String[] options() {
        return new String[]{
                "View All System Users",
                "View Pending Registrations",
                "Approve User Account"
        };
    }

    @Override
    protected void handleOption(int index) {
        try {
            switch (index) {
                case 1 -> viewAllUsers();
                case 2 -> viewPendingUsers();
                case 3 -> approveUserAccount();
                default -> System.out.println("Invalid choice.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void viewAllUsers() throws SQLException {
        System.out.println("\n--- ALL SYSTEM USERS ---");
        printUserTable(userDao.getAllUsers());
    }

    private void viewPendingUsers() throws SQLException {
        System.out.println("\n--- PENDING REGISTRATIONS ---");
        printUserTable(userDao.getPendingUsers());
    }

    private void approveUserAccount() throws SQLException {
        System.out.println("\n--- APPROVE USER ACCOUNT ---");
        List<User> pending = userDao.getPendingUsers();
        printUserTable(pending);
        if (pending.isEmpty()) {
            return;
        }

        while (true) {
            System.out.print("Enter Target User ID to approve (0 to cancel): ");
            String input = scanner.nextLine().trim();

            int id;
            try {
                id = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (id == 0) {
                System.out.println("Approval cancelled.");
                return;
            }

            User target = pending.stream().filter(u -> u.id() == id).findFirst().orElse(null);
            if (target == null) {
                System.out.println("User ID " + id + " is not in the pending list.");
                continue;
            }

            if (userDao.approveUser(id)) {
                System.out.println("Success! " + target.username() + " (" + target.roles()
                        + ") has been APPROVED.");
            } else {
                System.out.println("Approval failed. Please try again.");
            }
            return;
        }
    }

    /** Prints any list of users as an aligned, bordered table. */
    private void printUserTable(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users to display.");
            return;
        }
        String border = "+-" + "-".repeat(5) + "-+-" + "-".repeat(15) + "-+-"
                + "-".repeat(12) + "-+-" + "-".repeat(10) + "-+";
        String row = "| %-5s | %-15s | %-12s | %-10s |%n";

        System.out.println(border);
        System.out.printf(row, "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(border);
        for (User u : users) {
            System.out.printf(row, u.id(), u.username(), u.roles(), u.status());
        }
        System.out.println(border);
        System.out.println("Total: " + users.size());
    }
}
