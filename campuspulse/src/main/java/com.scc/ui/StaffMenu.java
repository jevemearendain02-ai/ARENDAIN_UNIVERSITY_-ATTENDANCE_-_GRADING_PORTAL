package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.SecurityUtil;
import com.scc.model.ActivityLog;
import com.scc.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class StaffMenu {
    private final Scanner scanner;
    private final ActivityLogViewer logViewer;
    private final ActivityLogDao logDao;

    public StaffMenu(Scanner scanner, ActivityLogViewer logViewer, ActivityLogDao logDao) {
        this.scanner = scanner;
        this.logViewer = logViewer;
        this.logDao = logDao;
    }

    public void display(User user) {
        while (true) {
            System.out.println("=== STAFF MENU (" + user.username() + ") ===");
            System.out.println("1. Log Staff Task");
            System.out.println("2. View My Activity Logs");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Enter task description: ");
                        String task = SecurityUtil.sanitizeLogText(scanner.nextLine());
                        if (!task.isBlank()) {
                            logDao.save(new ActivityLog(0, user.userId(), "STAFF_TASK",
                                    task, LocalDateTime.now(), "127.0.0.1"));
                            System.out.println("Task logged.\n");
                        } else {
                            System.out.println("Task cannot be empty.\n");
                        }
                    }
                    case "2" -> logViewer.showUserLogs(user.userId());
                    case "3" -> {
                        return;
                    }
                    default -> System.out.println("Invalid option.\n");
                }
            } catch (SQLException e) {
                System.err.println("Error in Staff Menu: " + e.getMessage());
            }
        }
    }
}