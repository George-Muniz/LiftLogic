package com.LiftLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class DietEntryForm extends JFrame {
    private static  Color PLUM = new Color(142, 69, 133);
    private static  Color TEAL = new Color(142, 69, 133);
    private static  Color BG_DARK = new Color(0, 0, 0);
    private static  Color CARD_BG_DARK = new Color(135, 135, 135, 255);
    private static  Color TEXT_LIGHT = new Color(255, 255, 255);

    private JTextField mealNameField;
    private JTextField caloriesField;
    private JTextField proteinField;
    private JTextField carbsField;
    private JTextField fatsField;
    private JButton submitButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton backButton;
    private JTable mealHistoryTable;
    private int userId;
    private int selectedMealId = -1;

    public DietEntryForm(int userId) {
        this.userId = userId;
        setTitle("Meal Log - LiftLogic");
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);

        // Input Panel
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Initial data load
        refreshMealHistory();

        setSize(900, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Meal Details"));
        panel.setBackground(CARD_BG_DARK);

        mealNameField = new JTextField(20);
        caloriesField = new JTextField(10);
        proteinField = new JTextField(10);
        carbsField = new JTextField(10);
        fatsField = new JTextField(10);

        panel.add(new JLabel("Meal Name:"));
        panel.add(mealNameField);
        panel.add(new JLabel("Calories:"));
        panel.add(caloriesField);
        panel.add(new JLabel("Protein (g):"));
        panel.add(proteinField);
        panel.add(new JLabel("Carbs (g):"));
        panel.add(carbsField);
        panel.add(new JLabel("Fats (g):"));
        panel.add(fatsField);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Meal History"));
        panel.setBackground(CARD_BG_DARK);

        mealHistoryTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(mealHistoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(CARD_BG_DARK);

        submitButton = new JButton("Log Meal");
        submitButton.setBackground(PLUM);
        submitButton.setForeground(TEXT_LIGHT);
        submitButton.setPreferredSize(new Dimension(200, 40));

        editButton = new JButton("Edit Selected");
        editButton.setBackground(TEAL);
        editButton.setForeground(TEXT_LIGHT);
        editButton.setPreferredSize(new Dimension(200, 40));

        deleteButton = new JButton("Delete Selected");
        deleteButton.setBackground(PLUM);
        deleteButton.setForeground(TEXT_LIGHT);
        deleteButton.setPreferredSize(new Dimension(200, 40));

        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(TEAL);
        backButton.setForeground(TEXT_LIGHT);
        backButton.setPreferredSize(new Dimension(200, 40));

        panel.add(submitButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(backButton);

        // Action Listeners
        submitButton.addActionListener(e -> handleMealSubmission());
        editButton.addActionListener(e -> handleEditMeal());
        deleteButton.addActionListener(e -> handleDeleteMeal());
        backButton.addActionListener(e -> {
            new Dashboard(userId); // Pass userId to Dashboard
            dispose(); // Close DietEntryForm
        });

        return panel;
    }


    private void clearForm() {
        // Clear the text fields
        mealNameField.setText("");
        caloriesField.setText("");
        proteinField.setText("");
        carbsField.setText("");
        fatsField.setText("");

        // Reset the selected meal ID
        selectedMealId = -1;

        // Reset the submit button text back to "Log Meal"
        submitButton.setText("Log Meal");
    }

    private void handleMealSubmission() {
        try {
            String mealName = mealNameField.getText().trim();
            double calories = Double.parseDouble(caloriesField.getText().trim());
            double protein = Double.parseDouble(proteinField.getText().trim());
            double carbs = Double.parseDouble(carbsField.getText().trim());
            double fats = Double.parseDouble(fatsField.getText().trim());

            if (selectedMealId == -1) {
                if (logMeal(mealName, calories, protein, carbs, fats)) {
                    showSuccessMessage("Meal logged successfully!");
                }
            } else {
                if (updateMeal(selectedMealId, mealName, calories, protein, carbs, fats)) {
                    showSuccessMessage("Meal updated successfully!");
                }
            }

            clearForm();
            refreshMealHistory();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for all required fields",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean logMeal(String mealName, double calories, double protein, double carbs, double fats) {
        String sql = "INSERT INTO meals (meal_id, user_id, meal_name, calories, protein, carbs, fats) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int nextId = getNextMealId();
            stmt.setInt(1, nextId);
            stmt.setInt(2, userId);
            stmt.setString(3, mealName);
            stmt.setDouble(4, calories);
            stmt.setDouble(5, protein);
            stmt.setDouble(6, carbs);
            stmt.setDouble(7, fats);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error saving meal to database");
            return false;
        }
    }

    private int getNextMealId() throws SQLException {
        String sql = "SELECT MAX(meal_id) FROM meals WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }

    private void handleEditMeal() {
        int selectedRow = mealHistoryTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Please select a meal to edit");
            return;
        }

        selectedMealId = (int) mealHistoryTable.getValueAt(selectedRow, 0);
        mealNameField.setText(mealHistoryTable.getValueAt(selectedRow, 1).toString());
        caloriesField.setText(mealHistoryTable.getValueAt(selectedRow, 2).toString());
        proteinField.setText(mealHistoryTable.getValueAt(selectedRow, 3).toString());
        carbsField.setText(mealHistoryTable.getValueAt(selectedRow, 4).toString());
        fatsField.setText(mealHistoryTable.getValueAt(selectedRow, 5).toString());

        submitButton.setText("Update Meal");
    }

    private boolean updateMeal(int mealId, String mealName, double calories, double protein, double carbs, double fats) {
        String sql = "UPDATE meals SET meal_name = ?, calories = ?, protein = ?, carbs = ?, fats = ? " +
                "WHERE meal_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mealName);
            stmt.setDouble(2, calories);
            stmt.setDouble(3, protein);
            stmt.setDouble(4, carbs);
            stmt.setDouble(5, fats);
            stmt.setInt(6, mealId);
            stmt.setInt(7, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error updating meal");
            return false;
        }
    }

    private void handleDeleteMeal() {
        int selectedRow = mealHistoryTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Please select a meal to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this meal?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int mealId = (int) mealHistoryTable.getValueAt(selectedRow, 0);
            if (deleteMeal(mealId)) {
                showSuccessMessage("Meal deleted successfully!");
                refreshMealHistory();
            }
        }
    }

    private boolean deleteMeal(int mealId) {
        String sql = "DELETE FROM meals WHERE meal_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mealId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error deleting meal");
            return false;
        }
    }

    private void refreshMealHistory() {
        String sql = "SELECT meal_id, meal_name, calories, protein, carbs, fats " +
                "FROM meals WHERE user_id = ? ORDER BY meal_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            ArrayList<Object[]> data = new ArrayList<>();
            while (rs.next()) {
                data.add(new Object[] {
                        rs.getInt("meal_id"),
                        rs.getString("meal_name"),
                        rs.getDouble("calories"),
                        rs.getDouble("protein"),
                        rs.getDouble("carbs"),
                        rs.getDouble("fats")
                });
            }

            String[] columns = {"ID", "Meal Name", "Calories", "Protein", "Carbs", "Fats"};
            DefaultTableModel model = new DefaultTableModel(data.toArray(new Object[0][]), columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            mealHistoryTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error loading meal history");
        }
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DietEntryForm(1)); // Example with userId = 1
    }
}
