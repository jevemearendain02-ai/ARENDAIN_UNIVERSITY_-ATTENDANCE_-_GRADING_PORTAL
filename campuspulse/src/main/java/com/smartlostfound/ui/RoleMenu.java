package com.smartlostfound.ui;

import com.smartlostfound.dao.UserDao;
import com.smartlostfound.model.User;
import java.util.Scanner;

/**
 * Base class for every role-based menu. Subclasses supply the title and any
 * role-specific options; logout / exit handling is shared here.
 */
public abstract class RoleMenu {
    protected final User user;
    protected final UserDao userDao;
    protected final Scanner scanner;

    protected RoleMenu(User user, UserDao userDao, Scanner scanner) {
        this.user = user;
        this.userDao = userDao;
        this.scanner = scanner;
    }

    protected abstract String title();

    /** Extra role-specific options, printed before Logout / Exit. Numbered from 1. */
    protected String[] options() {
        return new String[0];
    }

    /** Handles a role-specific option (1-based index). */
    protected void handleOption(int index) {
        System.out.println(options()[index - 1] + " is not available yet.");
    }

    /** @return true if the user chose to exit the whole application, false on logout. */
    public boolean show() {
        String[] extra = options();
        int logoutNo = extra.length + 1;
        int exitNo = extra.length + 2;

        while (true) {
            System.out.println("\n--- " + title() + " ---");
            System.out.println("Logged in as: " + user.username());
            for (int i = 0; i < extra.length; i++) {
                System.out.println((i + 1) + ". " + extra[i]);
            }
            System.out.println(logoutNo + ". Logout");
            System.out.println(exitNo + ". Exit Application");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice.");
                continue;
            }

            if (choice >= 1 && choice <= extra.length) {
                handleOption(choice);
            } else if (choice == logoutNo) {
                System.out.println("You have logged out.");
                return false;
            } else if (choice == exitNo) {
                return true;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
