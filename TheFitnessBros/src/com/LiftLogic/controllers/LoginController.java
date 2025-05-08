// LoginController.java
package com.LiftLogic.controllers;

import com.LiftLogic.dao.UserDAO;
import com.LiftLogic.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label statusLabel;

    private UserDAO userDao = new UserDAO();

    @FXML
    private void initialize() {
        loginButton.setOnAction(e -> handleLogin());
        registerButton.setOnAction(e -> handleRegister());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }

        if (userDao.validateUser(username, password)) {
            statusLabel.setText("Login successful!");
            // Proceed to main application
            // App.setCurrentUser(userDao.getUserByUsername(username));
            // App.showMainView();
        } else {
            statusLabel.setText("Invalid credentials");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }

        if (userDao.getUserByUsername(username) != null) {
            statusLabel.setText("Username already exists");
            return;
        }

        User newUser = new User(username, password, "");
        if (userDao.createUser(newUser)) {
            statusLabel.setText("Registration successful! Please login.");
        } else {
            statusLabel.setText("Registration failed");
        }
    }
}