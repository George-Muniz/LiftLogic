package com;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;

    public LoginPage() {
        setTitle("Login");
        setLayout(new FlowLayout());

        // Components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        createAccountButton = new JButton("Create Account");

        // Add components
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginButton);
        add(createAccountButton);

        // Login button action listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (checkLogin(username, password)) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Login successful!");
                    new Dashboard();  // Open Dashboard on successful login
                    setVisible(false); // Close login page
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this, "Invalid credentials!");
                }
            }
        });

        // Create account button action listener
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateAccountPage().setVisible(true); // Open Create Account page
                setVisible(false); // Hide login page
            }
        });

        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Method to check login against the database
    private boolean checkLogin(String username, String password) {
        String sql = "SELECT password_hash FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return BCrypt.checkpw(password, storedHash); // Check password hash
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
