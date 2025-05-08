package com.LiftLogic.views;

import com.LiftLogic.App;
import com.LiftLogic.model.UserProfile;

import javax.swing.*;
import java.awt.*;

public class ProfileView {
    private JPanel panel;
    private JTextField nameField;
    private JSpinner ageSpinner;
    private JComboBox<String> genderCombo;
    private JSpinner heightSpinner;
    private JSpinner weightSpinner;
    private JButton saveButton;

    public ProfileView(App app) {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        // Name field
        formPanel.add(new JLabel("Full Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        // Age field
        formPanel.add(new JLabel("Age:"));
        ageSpinner = new JSpinner(new SpinnerNumberModel(25, 13, 120, 1));
        formPanel.add(ageSpinner);

        // Gender field
        formPanel.add(new JLabel("Gender:"));
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(genderCombo);

        // Height field (in cm)
        formPanel.add(new JLabel("Height (cm):"));
        heightSpinner = new JSpinner(new SpinnerNumberModel(170, 100, 250, 1));
        formPanel.add(heightSpinner);

        // Weight field (in kg)
        formPanel.add(new JLabel("Weight (kg):"));
        weightSpinner = new JSpinner(new SpinnerNumberModel(70, 30, 300, 0.5));
        formPanel.add(weightSpinner);

        // Save button
        saveButton = new JButton("Save Profile");
        saveButton.addActionListener(e -> saveProfile(app));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);

        // Add components to main panel
        panel.add(new JLabel("My Profile", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load existing profile data if available
        loadProfileData(app);
    }

    private void loadProfileData(App app) {
        // In a real app, this would load from database
        // For now we'll use sample data
        nameField.setText("John Doe");
        ageSpinner.setValue(30);
        genderCombo.setSelectedItem("Male");
        heightSpinner.setValue(175);
        weightSpinner.setValue(75.5);
    }

    private void saveProfile(App app) {
        UserProfile profile = new UserProfile();
        profile.setName(nameField.getText());
        profile.setAge((Integer) ageSpinner.getValue());
        profile.setGender((String) genderCombo.getSelectedItem());
        profile.setHeight((Integer) heightSpinner.getValue());
        profile.setWeight((Double) weightSpinner.getValue());

        // Save to database
        // app.getProfileController().saveProfile(profile);

        JOptionPane.showMessageDialog(panel,
                "Profile saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public JPanel getPanel() {
        return panel;
    }
}
