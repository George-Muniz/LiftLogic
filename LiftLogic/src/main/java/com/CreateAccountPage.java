package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

public class CreateAccountPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField emailField;  // New email field
    private JButton submitButton;

    public CreateAccountPage() {
        setTitle("Create Account");
        setLayout(new FlowLayout());

        // Components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        fullNameField = new JTextField(20);
        emailField = new JTextField(20);  // New email input field
        submitButton = new JButton("Create Account");

        // Add components
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel("Full Name:"));
        add(fullNameField);
        add(new JLabel("Email:"));  // Label for the email field
        add(emailField);  // Add email field to the form
        add(submitButton);

        // Submit button action listener
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String fullName = fullNameField.getText();
                String email = emailField.getText();  // Get email from the field

                if (createAccount(username, password, fullName, email)) {
                    JOptionPane.showMessageDialog(CreateAccountPage.this, "Account created successfully!");
                    new LoginPage(); // Open the login page after account creation
                    setVisible(false); // Close create account page
                } else {
                    JOptionPane.showMessageDialog(CreateAccountPage.this, "Error creating account.");
                }
            }
        });

        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Method to create a new account and store in the database
    private boolean createAccount(String username, String password, String fullName, String email) {
        // Hash the password using BCrypt
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        // SQL to insert the new user into the Users table
        String sql = "INSERT INTO Users (username, password_hash, full_name, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the values from the user input
            stmt.setString(1, username);     // Username
            stmt.setString(2, passwordHash); // Hashed password
            stmt.setString(3, fullName);     // Full name
            stmt.setString(4, email);        // Email

            // Execute the query to insert the new user into the Users table
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Returns true if the account creation was successful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        new CreateAccountPage();
    }
}

