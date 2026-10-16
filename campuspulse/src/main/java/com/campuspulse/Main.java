package com.campuspulse;

import com.campuspulse.dao.DatabaseConnection;
import com.campuspulse.ui.MainMenu;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.initialize();
        new MainMenu().run();
    }
}
