package com.LiftLogic.views;

import com.LiftLogic.App;

import javax.swing.*;
import java.awt.*;

public class LoginView {
    private JPanel panel;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginView(App app) {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> app.getAuthController().login(
                usernameField.getText(),
                new String(passwordField.getPassword())
        ));

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> app.showRegisterView());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public JPanel getPanel() {
        return panel;
    }
}