package LiftLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProfilePage extends JFrame {
    private JTextField nameField;
    private JTextField weightField;
    private JTextField physiqueFocusField;
    private JTextField liftingSportField;
    private JLabel emailLabel;
    private JLabel usernameLabel;
    private JLabel memberSinceLabel;
    private JButton backButton;

    public ProfilePage() {
        setTitle("Profile Page");
        setLayout(new FlowLayout());

        // Create and initialize components
        emailLabel = new JLabel("Email: ");
        usernameLabel = new JLabel("Username: ");
        memberSinceLabel = new JLabel("Member Since: ");
        nameField = new JTextField(20);
        weightField = new JTextField(10);
        physiqueFocusField = new JTextField(15);
        liftingSportField = new JTextField(15);
        backButton = new JButton("Back");

        // Disable editing for non-editable fields
        emailLabel.setEnabled(false);
        usernameLabel.setEnabled(false);
        memberSinceLabel.setEnabled(false);

        // Add components to the form
        add(new JLabel("Email:"));
        add(emailLabel);
        add(new JLabel("Username:"));
        add(usernameLabel);
        add(new JLabel("Member Since:"));
        add(memberSinceLabel);
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Weight:"));
        add(weightField);
        add(new JLabel("Physique Focus:"));
        add(physiqueFocusField);
        add(new JLabel("Lifting Sport (Optional):"));
        add(liftingSportField);
        add(backButton);

        // Action listener for back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Dashboard();  // Go back to the Dashboard
                dispose(); // Close the current page (ProfilePage)
            }
        });

        // Load initial profile data from the database
        loadProfileData(1);  // Replace with actual logged-in user ID

        // Action listeners to make fields editable when clicked
        nameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                enableEditing("name");
            }
        });

        weightField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                enableEditing("weight");
            }
        });

        physiqueFocusField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                enableEditing("physique_focus");
            }
        });

        liftingSportField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                enableEditing("lifting_sport");
            }
        });

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the frame
        setVisible(true);
    }

    // Method to load user profile data from the Users table (no Goals table reference)
    private void loadProfileData(int userId) {
        String sql = "SELECT u.email, u.username, u.created_at, u.full_name, u.weight, u.physique_focus, u.lifting_sport " +
                "FROM Users u " +
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to enable editing of fields and save changes
    private void enableEditing(String field) {
        // Use a dialog to confirm the update action for the respective field
        String newValue = JOptionPane.showInputDialog("Enter new value for " + field + ":");
        if (newValue != null && !newValue.trim().isEmpty()) {
            // Update the specific field based on which one was clicked
            if ("name".equals(field)) {
                nameField.setText(newValue);
                updateProfile("full_name", newValue);
            } else if ("weight".equals(field)) {
                weightField.setText(newValue);
                updateProfile("weight", newValue);
            } else if ("physique_focus".equals(field)) {
                physiqueFocusField.setText(newValue);
                updateProfile("physique_focus", newValue);
            } else if ("lifting_sport".equals(field)) {
                liftingSportField.setText(newValue);
                updateProfile("lifting_sport", newValue);
            }
        }
    }

    // Method to update profile data in the database
    private void updateProfile(String fieldName, String newValue) {
        String sql = "UPDATE Users SET " + fieldName + " = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newValue);
            stmt.setInt(2, 1);  // Replace with actual user ID
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(ProfilePage.this, fieldName + " updated successfully.");
            } else {
                JOptionPane.showMessageDialog(ProfilePage.this, "Error updating " + fieldName + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ProfilePage();
    }
}
