package LiftLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList; // Import ArrayList
import java.util.List;      // Import List

public class DietEntryForm extends JFrame {
    private JTextField mealNameField;
    private JTextField caloriesField;
    private JTextField proteinField;
    private JTextField carbsField;
    private JTextField fatsField;
    private JButton submitButton;
    private JButton backButton;
    private JButton editButton;
    private JButton deleteButton;
    private JTable mealHistoryTable;
    private int selectedMealId = -1; // Variable to track selected meal for editing or deleting

    public DietEntryForm() {
        setTitle("Meal Entry");
        setLayout(new FlowLayout());

        // Components
        mealNameField = new JTextField(20);
        caloriesField = new JTextField(10);
        proteinField = new JTextField(10);
        carbsField = new JTextField(10);
        fatsField = new JTextField(10);
        backButton = new JButton("Back");
        submitButton = new JButton("Log Meal");
        editButton = new JButton("Edit Meal");
        deleteButton = new JButton("Delete Meal");

        // Add components to the form
        add(new JLabel("Meal Name:"));
        add(mealNameField);
        add(new JLabel("Calories:"));
        add(caloriesField);
        add(new JLabel("Protein (g):"));
        add(proteinField);
        add(new JLabel("Carbs (g):"));
        add(carbsField);
        add(new JLabel("Fats (g):"));
        add(fatsField);
        add(submitButton);
        add(editButton);
        add(deleteButton);
        add(backButton);

        // Display meal history in a table
        mealHistoryTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(mealHistoryTable);
        add(scrollPane);

        // Submit button action listener (Log Meal or Update Meal)
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mealName = mealNameField.getText();
                double calories = Double.parseDouble(caloriesField.getText());
                double protein = Double.parseDouble(proteinField.getText());
                double carbs = Double.parseDouble(carbsField.getText());
                double fats = Double.parseDouble(fatsField.getText());

                int userId = 1; // Hardcoded for now, replace with logged-in user's ID

                if (selectedMealId == -1) {
                    // Log new meal
                    if (logMeal(userId, mealName, calories, protein, carbs, fats)) {
                        JOptionPane.showMessageDialog(DietEntryForm.this, "Meal logged successfully!");
                    } else {
                        JOptionPane.showMessageDialog(DietEntryForm.this, "Error logging meal.");
                    }
                } else {
                    // Update existing meal
                    if (updateMeal(selectedMealId, mealName, calories, protein, carbs, fats)) {
                        JOptionPane.showMessageDialog(DietEntryForm.this, "Meal updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(DietEntryForm.this, "Error updating meal.");
                    }
                }

                // Refresh meal history table after operation
                refreshMealHistory(userId);
                selectedMealId = -1; // Reset selected meal ID after the operation
            }
        });

        // Edit button action listener
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = mealHistoryTable.getSelectedRow();
                if (row != -1) {
                    selectedMealId = (int) mealHistoryTable.getValueAt(row, 0); // Get meal ID
                    mealNameField.setText((String) mealHistoryTable.getValueAt(row, 1));
                    caloriesField.setText(mealHistoryTable.getValueAt(row, 2).toString());
                    proteinField.setText(mealHistoryTable.getValueAt(row, 3).toString());
                    carbsField.setText(mealHistoryTable.getValueAt(row, 4).toString());
                    fatsField.setText(mealHistoryTable.getValueAt(row, 5).toString());
                } else {
                    JOptionPane.showMessageDialog(DietEntryForm.this, "Please select a meal to edit.");
                }
            }
        });

        // Delete button action listener
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = mealHistoryTable.getSelectedRow();
                if (row != -1) {
                    int mealId = (int) mealHistoryTable.getValueAt(row, 0); // Get meal ID

                    int confirm = JOptionPane.showConfirmDialog(DietEntryForm.this,
                            "Are you sure you want to delete this meal?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (deleteMeal(mealId)) {
                            JOptionPane.showMessageDialog(DietEntryForm.this, "Meal deleted successfully!");
                            refreshMealHistory(1);  // Refresh the meal history table
                        } else {
                            JOptionPane.showMessageDialog(DietEntryForm.this, "Error deleting meal.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(DietEntryForm.this, "Please select a meal to delete.");
                }
            }
        });

        // Action listener to the Back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Dashboard();  // Go back to the Dashboard
                dispose(); // Close the current page (DietEntryForm)
            }
        });

        // Initial setup
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Load initial meal history for the user
        refreshMealHistory(1);  // Replace with actual user ID
    }

    // Method to log a meal in the database
    private boolean logMeal(int userId, String mealName, double calories, double protein, double carbs, double fats) {
        int nextMealId = getNextMealId(userId); // Get the next available meal_id
        String sql = "INSERT INTO Meals (meal_id, user_id, meal_name, calories, protein, carbs, fats) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nextMealId);  // Use manually calculated meal_id
            stmt.setInt(2, userId);
            stmt.setString(3, mealName);
            stmt.setDouble(4, calories);
            stmt.setDouble(5, protein);
            stmt.setDouble(6, carbs);
            stmt.setDouble(7, fats);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to get the next available meal_id based on the current number of meals
    private int getNextMealId(int userId) {
        String sql = "SELECT COUNT(*) FROM Meals WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) + 1; // meal_id is based on the number of rows in the table (1-based index)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Default to 1 if no meals exist
    }

    // Method to refresh meal history table
    private void refreshMealHistory(int userId) {
        String sql = "SELECT meal_id, meal_name, calories, protein, carbs, fats, meal_date FROM Meals WHERE user_id = ? ORDER BY meal_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Object[]> data = new ArrayList<>();
            while (rs.next()) {
                data.add(new Object[]{
                        rs.getInt("meal_id"),
                        rs.getString("meal_name"),
                        rs.getDouble("calories"),
                        rs.getDouble("protein"),
                        rs.getDouble("carbs"),
                        rs.getDouble("fats"),
                        rs.getTimestamp("meal_date")
                });
            }

            String[] columns = {"Meal ID", "Meal Name", "Calories", "Protein (g)", "Carbs (g)", "Fats (g)", "Meal Date"};
            Object[][] tableData = data.toArray(new Object[0][]);
            DefaultTableModel model = new DefaultTableModel(tableData, columns);
            mealHistoryTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update a meal in the database
    private boolean updateMeal(int mealId, String mealName, double calories, double protein, double carbs, double fats) {
        String sql = "UPDATE Meals SET meal_name = ?, calories = ?, protein = ?, carbs = ?, fats = ? WHERE meal_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mealName);
            stmt.setDouble(2, calories);
            stmt.setDouble(3, protein);
            stmt.setDouble(4, carbs);
            stmt.setDouble(5, fats);
            stmt.setInt(6, mealId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to delete a meal from the database and re-sequence meal_ids
    private boolean deleteMeal(int mealId) {
        String sqlDelete = "DELETE FROM Meals WHERE meal_id = ?";
        String sqlReSequence = "UPDATE Meals SET meal_id = meal_id - 1 WHERE meal_id > ? ORDER BY meal_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
             PreparedStatement stmtReSequence = conn.prepareStatement(sqlReSequence)) {

            // Delete the selected meal
            stmtDelete.setInt(1, mealId);
            int rowsDeleted = stmtDelete.executeUpdate();

            if (rowsDeleted > 0) {
                // Re-sequence the remaining meal_ids after deletion
                stmtReSequence.setInt(1, mealId);  // Only update rows with meal_id > the deleted meal_id
                stmtReSequence.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        new DietEntryForm();
    }
}
