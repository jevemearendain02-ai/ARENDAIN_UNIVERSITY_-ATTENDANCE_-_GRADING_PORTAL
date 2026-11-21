package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.SecurityUtil;
import com.scc.model.ActivityLog;
import com.scc.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class TeacherMenu {
    private final Scanner scanner;
    private final ActivityLogViewer logViewer;
    private final ActivityLogDao logDao;

    public TeacherMenu(Scanner scanner, ActivityLogViewer logViewer, ActivityLogDao logDao) {
        this.scanner = scanner;
        this.logViewer = logViewer;
        this.logDao = logDao;
    }

    public void display(User user) {
        while (true) {
            System.out.println("=== TEACHER MENU (" + user.username() + ") ===");
            System.out.println("1. Log Class / Teaching Activity");
            System.out.println("2. View My Activity Logs");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Enter teaching activity: ");
                        String activity = SecurityUtil.sanitizeLogText(scanner.nextLine());
                        if (!activity.isBlank()) {
                            logDao.save(new ActivityLog(0, user.userId(), "TEACHER_ACTIVITY",
                                    activity, LocalDateTime.now(), "127.0.0.1"));
                            System.out.println("Teaching activity logged.\n");
                        } else {
                            System.out.println("Activity cannot be empty.\n");
                        }
                    }
                    case "2" -> logViewer.showUserLogs(user.userId());
                    case "3" -> {
                        return;
                    }
                    default -> System.out.println("Invalid option.\n");
                }
            } catch (SQLException e) {
                System.err.println("Error in Teacher Menu: " + e.getMessage());
            }
        }
    }
}