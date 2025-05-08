package com.LiftLogic.controllers;

import com.LiftLogic.App;
import com.LiftLogic.dao.UserDAO;
import com.LiftLogic.model.User;

import javax.swing.*;

public class AuthController {
    private UserDAO userDao;
    private User currentUser;

    public AuthController() {
        userDao = new UserDAO();
    }

    public boolean login(String username, String password) {
        currentUser = userDao.getUserByUsername(username);
        if (currentUser != null && userDao.validateUser(username, password)) {
            App.getInstance().showDashboardView();
            return true;
        }
        JOptionPane.showMessageDialog(null, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
