package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.SecurityUtil;
import com.scc.dao.UserDao;
import com.scc.model.ActivityLog;
import com.scc.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final Scanner scanner;
    private final ActivityLogViewer logViewer;
    private final ActivityLogDao logDao;
    private final UserDao userDao;

    public AdminMenu(Scanner scanner, ActivityLogViewer logViewer, ActivityLogDao logDao, UserDao userDao) {
        this.scanner = scanner;
        this.logViewer = logViewer;
        this.logDao = logDao;
        this.userDao = userDao;
    }

    public void display(User user) {
        while (true) {
            System.out.println("=== ADMIN MENU (" + user.username() + ") ===");
            System.out.println("1. View All Users");
            System.out.println("2. View All Activity Logs");
            System.out.println("3. Record Admin Action");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        List<User> users = userDao.findAll();
                        System.out.println("\n=== Registered Users ===");
                        for (User u : users) {
                            System.out.printf("ID: %d | %s (%s %s) | Role: %s | Email: %s | Status: %s%n",
                                    u.userId(), u.username(), u.firstName(), u.lastName(),
                                    u.role(), u.email(), u.status());
                        }
                        System.out.println();
                        logDao.save(new ActivityLog(0, user.userId(), "VIEW_USERS",
                                "Viewed all users", LocalDateTime.now(), "127.0.0.1"));
                    }
                    case "2" -> {
                        logViewer.showAllLogs();
                        logDao.save(new ActivityLog(0, user.userId(), "VIEW_LOGS",
                                "Viewed all activity logs", LocalDateTime.now(), "127.0.0.1"));
                    }
                    case "3" -> {
                        System.out.print("Enter action description: ");
                        String action = SecurityUtil.sanitizeLogText(scanner.nextLine());
                        if (!action.isBlank()) {
                            logDao.save(new ActivityLog(0, user.userId(), "ADMIN_ACTION",
                                    action, LocalDateTime.now(), "127.0.0.1"));
                            System.out.println("Action recorded.\n");
                        } else {
                            System.out.println("Action cannot be empty.\n");
                        }
                    }
                    case "4" -> {
                        return;
                    }
                    default -> System.out.println("Invalid option.\n");
                }
            } catch (SQLException e) {
                System.err.println("Error in Admin Menu: " + e.getMessage());
            }
        }
    }
}