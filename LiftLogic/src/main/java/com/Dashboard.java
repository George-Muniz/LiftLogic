package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {
    private JButton workoutButton;
    private JButton dietButton;
    private JButton profileButton;
    private JButton logoutButton;

    public Dashboard() {
        // Set up the main dashboard frame
        setTitle("Dashboard");
        setLayout(new FlowLayout());

        // Create buttons for the different sections
        workoutButton = new JButton("Workout Logging");
        dietButton = new JButton("Diet Tracking");
        profileButton = new JButton("Profile");
        logoutButton = new JButton("Logout");

        // Add action listeners to the buttons
        workoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Workout Entry form
                new WorkoutEntryForm();  // This will open the workout logging form
                dispose(); // Close the dashboard window
            }
        });

        dietButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Diet Entry form
                new DietEntryForm();  // This will open the diet tracking form
                dispose(); // Close the dashboard window
            }
        });

        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Profile form
                new ProfilePage();  // This will open the profile page form
                dispose(); // Close the dashboard window
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Log out and redirect to the login page
                new LoginPage();  // Open the login page again
                dispose(); // Close the dashboard window
            }
        });

        // Add the buttons to the dashboard
        add(workoutButton);
        add(dietButton);
        add(profileButton);
        add(logoutButton);

        // Final frame settings
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the frame on the screen
        setVisible(true);
    }

    public static void main(String[] args) {
        new Dashboard(); // Open the dashboard when the program starts
    }
}
