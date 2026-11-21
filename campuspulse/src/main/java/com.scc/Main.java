package com.scc;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.DatabaseConnection;
import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.model.ActivityLog;
import com.scc.model.User;
import com.scc.ui.ActivityLogViewer;
import com.scc.ui.AdminMenu;
import com.scc.ui.Authentication;
import com.scc.ui.SecurityMenu;
import com.scc.ui.StaffMenu;
import com.scc.ui.StudentMenu;
import com.scc.ui.TeacherMenu;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
             Scanner scanner = new Scanner(System.in)) {

            new DatabaseInitializer(conn).initialize();

            UserDao userDao = new UserDao(conn);
            ActivityLogDao logDao = new ActivityLogDao(conn);
            ActivityLogViewer logViewer = new ActivityLogViewer(logDao);
            Authentication auth = new Authentication(userDao, logDao, scanner);

            while (true) {
                User currentUser = auth.start();
                if (currentUser == null) {
                    System.out.println("Exiting application. Goodbye!");
                    break;
                }

                logDao.save(new ActivityLog(0, currentUser.id(), "Logged into system", LocalDateTime.now()));

                openMenuForUser(currentUser, scanner, logViewer, logDao, userDao);

                logDao.save(new ActivityLog(0, currentUser.id(), "Logged out of system", LocalDateTime.now()));
                System.out.println("You have been logged out.\n");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void openMenuForUser(User user, Scanner scanner, ActivityLogViewer logViewer,
                                        ActivityLogDao logDao, UserDao userDao) {
        String role = user.role().toUpperCase();
        switch (role) {
            case "ADMIN" -> new AdminMenu(scanner, logViewer, logDao, userDao).display(user);
            case "SECURITY" -> new SecurityMenu(scanner, logViewer, logDao).display(user);
            case "STAFF" -> new StaffMenu(scanner, logViewer, logDao).display(user);
            case "STUDENT" -> new StudentMenu(scanner, logViewer, logDao).display(user);
            case "TEACHER" -> new TeacherMenu(scanner, logViewer, logDao).display(user);
            default -> {
                System.out.println("Unknown role '" + user.role() + "'. Defaulting to Student Menu.");
                new StudentMenu(scanner, logViewer, logDao).display(user);
            }
        }
    }
}