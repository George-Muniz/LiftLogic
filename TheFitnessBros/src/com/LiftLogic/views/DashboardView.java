package com.LiftLogic.views;

import com.LiftLogic.App;

import javax.swing.*;
import java.awt.*;

public class DashboardView {
    private JPanel panel;

    public DashboardView(App app) {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create cards for different features
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 15, 15));

        // Workout Log Card
        JPanel workoutCard = createFeatureCard(
                "Workout Logger",
                "Track your exercises and sets",
                "🏋️",
                e -> app.showWorkoutLogView()
        );

        // Nutrition Tracker Card
        JPanel nutritionCard = createFeatureCard(
                "Nutrition Tracker",
                "Log your daily macros",
                "🍎",
                e -> app.showNutritionView()
        );

        // Profile Card
        JPanel profileCard = createFeatureCard(
                "My Profile",
                "View and edit your profile",
                "👤",
                e -> app.showProfileView()
        );

        // Progress Card
        JPanel progressCard = createFeatureCard(
                "My Progress",
                "View your fitness journey",
                "📈",
                e -> JOptionPane.showMessageDialog(panel, "Progress view coming soon!")
        );

        cardsPanel.add(workoutCard);
        cardsPanel.add(nutritionCard);
        cardsPanel.add(profileCard);
        cardsPanel.add(progressCard);

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome to LiftLogic!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        panel.add(welcomeLabel, BorderLayout.NORTH);
        panel.add(cardsPanel, BorderLayout.CENTER);
    }

    private JPanel createFeatureCard(String title, String description, String emoji, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JButton button = new JButton(emoji);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        button.setPreferredSize(new Dimension(60, 60));
        button.addActionListener(action);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JTextArea descArea = new JTextArea(description);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(card.getBackground());

        JPanel textPanel = new JPanel(new BorderLayout(5, 5));
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descArea, BorderLayout.CENTER);

        card.add(button, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    public JPanel getPanel() {
        return panel;
    }
}