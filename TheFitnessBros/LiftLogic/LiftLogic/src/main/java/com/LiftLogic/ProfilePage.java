package com.LiftLogic;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class ProfilePage extends JFrame {
    private static  Color PLUM = new Color(142, 69, 133);
    private static  Color TEAL = new Color(64, 224, 208);
    private static  Color BG_DARK = new Color(46, 46, 46);
    private static  Color PANEL_BG = new Color(60, 60, 60, 230);
    private static  Color TEXT_LIGHT = new Color(230, 230, 230);

    private int userId;
    private JTextField nameField, weightField, physiqueFocusField, liftingSportField, goalTypeField, goalValueField, targetDateField;
    private JLabel emailLabel, usernameLabel, memberSinceLabel;

    public ProfilePage(int userId) {
        this.userId = userId; // Store the userId passed in
        setTitle("LiftLogic Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // Form panel for profile data
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        // Email Label (non-editable)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel emailLabelHeader = new JLabel("Email:");
        emailLabelHeader.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        emailLabelHeader.setForeground(TEXT_LIGHT);
        form.add(emailLabelHeader, gbc);
        emailLabel = new JLabel();
        emailLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        emailLabel.setForeground(TEXT_LIGHT);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(emailLabel, gbc);

        // Username Label (non-editable)
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel usernameLabelHeader = new JLabel("Username:");
        usernameLabelHeader.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        usernameLabelHeader.setForeground(TEXT_LIGHT);
        form.add(usernameLabelHeader, gbc);
        usernameLabel = new JLabel();
        usernameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        usernameLabel.setForeground(TEXT_LIGHT);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(usernameLabel, gbc);

        // Member Since Label (non-editable)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel memberSinceLabelHeader = new JLabel("Member Since:");
        memberSinceLabelHeader.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        memberSinceLabelHeader.setForeground(TEXT_LIGHT);
        form.add(memberSinceLabelHeader, gbc);
        memberSinceLabel = new JLabel();
        memberSinceLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        memberSinceLabel.setForeground(TEXT_LIGHT);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(memberSinceLabel, gbc);

        // Editable fields for profile info (Name, Weight, Physique Focus, Lifting Sport, etc.)
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        nameLabel.setForeground(TEXT_LIGHT);
        form.add(nameLabel, gbc);
        nameField = new JTextField();
        nameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        nameField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(nameField, gbc);

        // Weight
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel weightLabel = new JLabel("Weight (kg):");
        weightLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        weightLabel.setForeground(TEXT_LIGHT);
        form.add(weightLabel, gbc);
        weightField = new JTextField();
        weightField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        weightField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(weightField, gbc);

        // Physique Focus
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel physiqueFocusLabel = new JLabel("Physique Focus:");
        physiqueFocusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        physiqueFocusLabel.setForeground(TEXT_LIGHT);
        form.add(physiqueFocusLabel, gbc);
        physiqueFocusField = new JTextField();
        physiqueFocusField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        physiqueFocusField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(physiqueFocusField, gbc);

        // Lifting Sport (Optional)
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel liftingSportLabel = new JLabel("Lifting Sport (Optional):");
        liftingSportLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        liftingSportLabel.setForeground(TEXT_LIGHT);
        form.add(liftingSportLabel, gbc);
        liftingSportField = new JTextField();
        liftingSportField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        liftingSportField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(liftingSportField, gbc);

        // Goal Type
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel goalTypeLabel = new JLabel("Goal Type:");
        goalTypeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        goalTypeLabel.setForeground(TEXT_LIGHT);
        form.add(goalTypeLabel, gbc);
        goalTypeField = new JTextField();
        goalTypeField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        goalTypeField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(goalTypeField, gbc);

        // Goal Value
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel goalValueLabel = new JLabel("Goal Value:");
        goalValueLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        goalValueLabel.setForeground(TEXT_LIGHT);
        form.add(goalValueLabel, gbc);
        goalValueField = new JTextField();
        goalValueField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        goalValueField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(goalValueField, gbc);

        // Target Date
        gbc.gridx = 0; gbc.gridy = 9;
        JLabel targetDateLabel = new JLabel("Target Date:");
        targetDateLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        targetDateLabel.setForeground(TEXT_LIGHT);
        form.add(targetDateLabel, gbc);
        targetDateField = new JTextField();
        targetDateField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        targetDateField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(targetDateField, gbc);

        // Save button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        btnPanel.setOpaque(false);
        JButton saveButton = new RoundedButton("Save Changes", PLUM, TEAL);
        saveButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        saveButton.setPreferredSize(new Dimension(300, 90));
        btnPanel.add(saveButton);
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2; gbc.weightx = 0;
        form.add(btnPanel, gbc);

        // Back Button
        JPanel backBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        backBtnPanel.setOpaque(false);
        JButton backButton = new RoundedButton("Back", PLUM, TEAL);
        backButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        backButton.setPreferredSize(new Dimension(300, 90));
        backBtnPanel.add(backButton);
        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2; gbc.weightx = 0;
        form.add(backBtnPanel, gbc);

        // Action listener for Save Button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveProfileChanges(); // Method to save changes in profile
            }
        });

        // Action listener for Back Button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Dashboard(userId); // Send userId back to Dashboard
                dispose(); // Close ProfilePage
            }
        });

        add(form, BorderLayout.CENTER);

        // Load existing profile data
        loadProfileData(userId);

        // Pack to fit
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadProfileData(int userId) {
        String sql = "SELECT u.email, u.username, u.created_at, u.full_name, u.weight, u.physique_focus, u.lifting_sport, " +
                "g.goal_type, g.goal_value, g.target_date " +
                "FROM Users u " +
                "LEFT JOIN Goals g ON u.user_id = g.user_id " +
                "WHERE u.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Load data into labels and fields
                emailLabel.setText(rs.getString("email"));
                usernameLabel.setText(rs.getString("username"));
                memberSinceLabel.setText(rs.getString("created_at").substring(0, 10).replace("-", "/"));
                nameField.setText(rs.getString("full_name"));
                weightField.setText(rs.getString("weight"));
                physiqueFocusField.setText(rs.getString("physique_focus"));
                liftingSportField.setText(rs.getString("lifting_sport"));
                goalTypeField.setText(rs.getString("goal_type"));
                goalValueField.setText(rs.getString("goal_value"));
                targetDateField.setText(rs.getString("target_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onSaveProfileChanges() {
        String name = nameField.getText();
        String weight = weightField.getText();
        String physiqueFocus = physiqueFocusField.getText();
        String liftingSport = liftingSportField.getText();
        String goalType = goalTypeField.getText();
        String goalValue = goalValueField.getText();
        String targetDate = targetDateField.getText();

        String sql = "UPDATE Users SET full_name = ?, weight = ?, physique_focus = ?, lifting_sport = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, weight);
            stmt.setString(3, physiqueFocus);
            stmt.setString(4, liftingSport);
            stmt.setInt(5, userId);
            stmt.executeUpdate();

            // Update the goal information in the Goals table
            String goalSql = "UPDATE Goals SET goal_type = ?, goal_value = ?, target_date = ? WHERE user_id = ?";
            try (PreparedStatement goalStmt = conn.prepareStatement(goalSql)) {
                goalStmt.setString(1, goalType);
                goalStmt.setString(2, goalValue);
                goalStmt.setString(3, targetDate);
                goalStmt.setInt(4, userId);
                goalStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating profile.");
        }
    }
    private void saveWeight() {
        double weight = Double.parseDouble(weightField.getText().trim());

        // Update user weight
        String updateSql = "UPDATE users SET weight = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setDouble(1, weight);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            // Log weight in body_weight_history table if different from last logged weight
            logWeightInHistory(weight);

            JOptionPane.showMessageDialog(this, "Weight updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating weight.");
        }
    }
    private void logWeightInHistory(double weight) {
        String insertSql = "INSERT INTO body_weight_history (user_id, body_weight) " +
                "SELECT ?, ? WHERE NOT EXISTS (" +
                "SELECT * FROM body_weight_history " +
                "WHERE user_id = ? AND body_weight = ? AND DATE(recorded_date) = CURDATE())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            stmt.setInt(1, userId);
            stmt.setDouble(2, weight);
            stmt.setInt(3, userId);
            stmt.setDouble(4, weight);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Custom rounded button
    private static class RoundedButton extends JButton {
        private final Color normal, hover;
        RoundedButton(String text, Color bg, Color hoverColor) {
            super(text); normal = bg; hover = hoverColor;
            setForeground(Color.WHITE);
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setUI(new BasicButtonUI());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { repaint(); }
                public void mouseExited(MouseEvent e) { repaint(); }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? hover : normal);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g2);
            g2.dispose();
        }

        public boolean contains(int x, int y) {
            return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30).contains(x, y);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new ProfilePage(1)); // Pass userId here (example 1)
    }
}
