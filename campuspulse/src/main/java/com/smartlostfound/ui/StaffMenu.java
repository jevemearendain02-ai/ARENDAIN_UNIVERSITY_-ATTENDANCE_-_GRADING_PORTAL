package com.smartlostfound.ui;

import com.smartlostfound.dao.UserDao;
import com.smartlostfound.model.User;
import java.util.Scanner;

public class StaffMenu extends RoleMenu {
    public StaffMenu(User user, UserDao userDao, Scanner scanner) {
        super(user, userDao, scanner);
    }

    @Override
    protected String title() {
        return "STAFF MENU";
    }
}
