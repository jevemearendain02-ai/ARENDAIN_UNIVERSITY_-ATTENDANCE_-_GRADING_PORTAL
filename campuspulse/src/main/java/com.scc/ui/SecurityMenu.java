package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.SecurityUtil;
import com.scc.model.ActivityLog;
import com.scc.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class SecurityMenu {
    private final Scanner scanner;
    private final ActivityLogViewer logViewer;
    private final ActivityLogDao logDao;

    public SecurityMenu(Scanner scanner, ActivityLogViewer logViewer, ActivityLogDao logDao) {
        this.scanner = scanner;
        this.logViewer = logViewer;
        this.logDao = logDao;
    }

    public void display(User user) {
        while (true) {
            System.out.println("=== SECURITY MENU (" + user.username() + ") ===");
            System.out.println("1. Log Campus Security Incident / Patrol");
            System.out.println("2. View All Activity Logs");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Enter incident/patrol note: ");
                        String note = SecurityUtil.sanitizeLogText(scanner.nextLine());
                        if (!note.isBlank()) {
                            logDao.save(new ActivityLog(0, user.userId(), "SECURITY_LOG",
                                    note, LocalDateTime.now(), "127.0.0.1"));
                            System.out.println("Security log saved.\n");
                        } else {
                            System.out.println("Note cannot be empty.\n");
                        }
                    }
                    case "2" -> logViewer.showAllLogs();
                    case "3" -> {
                        return;
                    }
                    default -> System.out.println("Invalid option.\n");
                }
            } catch (SQLException e) {
                System.err.println("Error in Security Menu: " + e.getMessage());
            }
        }
    }
}