package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.model.ActivityLog;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ActivityLogViewer {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ActivityLogDao logDao;

    public ActivityLogViewer(ActivityLogDao logDao) {
        this.logDao = logDao;
    }

    public void showAllLogs() {
        try {
            List<ActivityLog> logs = logDao.findAll();
            printLogs(logs, "=== All Activity Logs ===");
        } catch (SQLException e) {
            System.err.println("Failed to load activity logs: " + e.getMessage());
        }
    }

    public void showUserLogs(int userId) {
        try {
            List<ActivityLog> logs = logDao.findByUserId(userId);
            printLogs(logs, "=== Your Activity Logs ===");
        } catch (SQLException e) {
            System.err.println("Failed to load user activity logs: " + e.getMessage());
        }
    }

    private void printLogs(List<ActivityLog> logs, String header) {
        System.out.println("\n" + header);
        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
        } else {
            for (ActivityLog log : logs) {
                System.out.printf("[%s] Log #%d | User #%d | %s | %s (%s)%n",
                        log.getTimestamp().format(FORMATTER),
                        log.getLogId(),
                        log.getUserId(),
                        log.getActionType(),
                        log.getDescription(),
                        log.getIpAddress());
            }
        }
        System.out.println();
    }
}