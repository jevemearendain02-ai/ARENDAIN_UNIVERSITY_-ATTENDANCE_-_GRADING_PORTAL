package com.smartlostfound;

import com.smartlostfound.dao.DatabaseInitializer;
import com.smartlostfound.dao.UserDao;
import com.smartlostfound.ui.Authentication;
import java.sql.SQLException;
import java.util.Scanner;

/** Startup entry point only: initialize DB -> create shared instances -> hand off to Authentication. */
public class 1Main {
    public static void main(String[] args) {
        try {
            DatabaseInitializer.initialize();
            System.out.println("Database initialized.");
        } catch (SQLException e) {
            System.err.println("Unable to initialize database: " + e.getMessage());
            return;
        }

        UserDao userDao = new UserDao();
        try (Scanner scanner = new Scanner(System.in)) {
            new Authentication(userDao, scanner).start();
        }
    }
}
