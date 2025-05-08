package com.LiftLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import org.jfree.chart.ChartPanel;

public class WorkoutEntryForm extends JFrame {
    private static  Color PLUM = new Color(142, 69, 133);
    private static  Color TEAL = new Color(33, 188, 255);
    private static  Color BG_DARK = new Color(0, 0, 0);
    private static  Color CARD_BG_DARK = new Color(110, 110, 110, 255);
    private static  Color TEXT_LIGHT = new Color(255, 255, 255);

    private JTextField workoutNameField;
    private JTextField weightField;
    private JTextField durationField;
    private JTextField repsField;
    private JTextField setsField;
    private JTextField caloriesField;
    private JButton submitButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton backButton;
    private JTable workoutHistoryTable;
    private JTabbedPane chartTabPane;
    private int userId;
    private int selectedWorkoutId = -1;

    public WorkoutEntryForm(int userId) {
        this.userId = userId;
        setTitle("Workout Log - LiftLogic");
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);

        // Input Panel
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Chart Panel
        chartTabPane = new JTabbedPane();
        add(chartTabPane, BorderLayout.SOUTH);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Initial data load
        refreshWorkoutHistory();
        updateExerciseCharts();

        setSize(900, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Workout Details"));
        panel.setBackground(CARD_BG_DARK);

        workoutNameField = new JTextField(20);
        weightField = new JTextField(10);
        durationField = new JTextField(10);
        repsField = new JTextField(10);
        setsField = new JTextField(10);
        caloriesField = new JTextField(10);

        panel.add(new JLabel("Workout Name:"));
        panel.add(workoutNameField);
        panel.add(new JLabel("Weight (lbs/kg):"));
        panel.add(weightField);
        panel.add(new JLabel("Duration (min, optional):"));
        panel.add(durationField);
        panel.add(new JLabel("Reps (optional):"));
        panel.add(repsField);
        panel.add(new JLabel("Sets (optional):"));
        panel.add(setsField);
        panel.add(new JLabel("Calories Burned:"));
        panel.add(caloriesField);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Workout History"));
        panel.setBackground(CARD_BG_DARK);

        workoutHistoryTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(workoutHistoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(CARD_BG_DARK);

        submitButton = new JButton("Log Workout");
        submitButton.setBackground(PLUM);
        submitButton.setForeground(TEXT_LIGHT);
        submitButton.setPreferredSize(new Dimension(200, 40));

        editButton = new JButton("Edit Selected");
        editButton.setBackground(PLUM);
        editButton.setForeground(TEXT_LIGHT);
        editButton.setPreferredSize(new Dimension(200, 40));

        deleteButton = new JButton("Delete Selected");
        deleteButton.setBackground(PLUM);
        deleteButton.setForeground(TEXT_LIGHT);
        deleteButton.setPreferredSize(new Dimension(200, 40));

        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(PLUM);
        backButton.setForeground(TEXT_LIGHT);
        backButton.setPreferredSize(new Dimension(200, 40));

        panel.add(submitButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(backButton);

        // Action Listeners
        submitButton.addActionListener(e -> handleWorkoutSubmission());
        editButton.addActionListener(e -> handleEditWorkout());
        deleteButton.addActionListener(e -> handleDeleteWorkout());
        backButton.addActionListener(e -> {
            new Dashboard(userId); // Pass userId to Dashboard
            dispose(); // Close WorkoutEntryForm
        });

        return panel;
    }

    private void handleWorkoutSubmission() {
        try {
            String workoutName = workoutNameField.getText().trim();
            double weight = Double.parseDouble(weightField.getText().trim());
            double calories = Double.parseDouble(caloriesField.getText().trim());

            // Optional fields
            Double duration = durationField.getText().trim().isEmpty() ? null :
                    Double.parseDouble(durationField.getText().trim());
            Integer reps = repsField.getText().trim().isEmpty() ? null :
                    Integer.parseInt(repsField.getText().trim());
            Integer sets = setsField.getText().trim().isEmpty() ? null :
                    Integer.parseInt(setsField.getText().trim());

            // Validate at least one of duration or reps/sets is provided
            if (duration == null && (reps == null || sets == null)) {
                JOptionPane.showMessageDialog(this,
                        "Please provide either duration or both reps and sets",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedWorkoutId == -1) {
                if (logWorkout(workoutName, weight, duration, reps, sets, calories)) {
                    showSuccessMessage("Workout logged successfully!");
                }
            } else {
                if (updateWorkout(selectedWorkoutId, workoutName, weight, duration, reps, sets, calories)) {
                    showSuccessMessage("Workout updated successfully!");
                }
            }

            clearForm();
            refreshWorkoutHistory();
            updateExerciseCharts();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for all required fields",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean logWorkout(String workoutName, double weight, Double duration,
                               Integer reps, Integer sets, double calories) {
        String sql = "INSERT INTO workouts (workout_id, user_id, workout_name, weight, " +
                "duration, reps, sets, calories_burned) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int nextId = getNextWorkoutId();
            stmt.setInt(1, nextId);
            stmt.setInt(2, userId);
            stmt.setString(3, workoutName);
            stmt.setDouble(4, weight);

            if (duration != null) stmt.setDouble(5, duration);
            else stmt.setNull(5, Types.DOUBLE);

            if (reps != null) stmt.setInt(6, reps);
            else stmt.setNull(6, Types.INTEGER);

            if (sets != null) stmt.setInt(7, sets);
            else stmt.setNull(7, Types.INTEGER);

            stmt.setDouble(8, calories);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error saving workout to database");
            return false;
        }
    }

    private int getNextWorkoutId() throws SQLException {
        String sql = "SELECT MAX(workout_id) FROM workouts WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }

    private void handleEditWorkout() {
        int selectedRow = workoutHistoryTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Please select a workout to edit");
            return;
        }

        selectedWorkoutId = (int) workoutHistoryTable.getValueAt(selectedRow, 0);
        workoutNameField.setText(workoutHistoryTable.getValueAt(selectedRow, 1).toString());
        weightField.setText(workoutHistoryTable.getValueAt(selectedRow, 2).toString());

        Object durationValue = workoutHistoryTable.getValueAt(selectedRow, 3);
        durationField.setText(durationValue != null ? durationValue.toString() : "");

        Object repsValue = workoutHistoryTable.getValueAt(selectedRow, 4);
        repsField.setText(repsValue != null ? repsValue.toString() : "");

        Object setsValue = workoutHistoryTable.getValueAt(selectedRow, 5);
        setsField.setText(setsValue != null ? setsValue.toString() : "");

        caloriesField.setText(workoutHistoryTable.getValueAt(selectedRow, 6).toString());

        submitButton.setText("Update Workout");
    }

    private boolean updateWorkout(int workoutId, String workoutName, double weight,
                                  Double duration, Integer reps, Integer sets, double calories) {
        String sql = "UPDATE workouts SET workout_name = ?, weight = ?, duration = ?, " +
                "reps = ?, sets = ?, calories_burned = ? " +
                "WHERE workout_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, workoutName);
            stmt.setDouble(2, weight);

            if (duration != null) stmt.setDouble(3, duration);
            else stmt.setNull(3, Types.DOUBLE);

            if (reps != null) stmt.setInt(4, reps);
            else stmt.setNull(4, Types.INTEGER);

            if (sets != null) stmt.setInt(5, sets);
            else stmt.setNull(5, Types.INTEGER);

            stmt.setDouble(6, calories);
            stmt.setInt(7, workoutId);
            stmt.setInt(8, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error updating workout");
            return false;
        }
    }

    private void handleDeleteWorkout() {
        int selectedRow = workoutHistoryTable.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Please select a workout to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this workout?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int workoutId = (int) workoutHistoryTable.getValueAt(selectedRow, 0);
            if (deleteWorkout(workoutId)) {
                showSuccessMessage("Workout deleted successfully!");
                refreshWorkoutHistory();
                updateExerciseCharts();
            }
        }
    }

    private boolean deleteWorkout(int workoutId) {
        String sql = "DELETE FROM workouts WHERE workout_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, workoutId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error deleting workout");
            return false;
        }
    }

    private void refreshWorkoutHistory() {
        String sql = "SELECT workout_id, workout_name, weight, duration, reps, sets, " +
                "calories_burned, workout_date " +
                "FROM workouts WHERE user_id = ? ORDER BY workout_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            ArrayList<Object[]> data = new ArrayList<>();
            while (rs.next()) {
                data.add(new Object[]{
                        rs.getInt("workout_id"),
                        rs.getString("workout_name"),
                        rs.getDouble("weight"),
                        rs.getObject("duration"),
                        rs.getObject("reps"),
                        rs.getObject("sets"),
                        rs.getDouble("calories_burned"),
                        rs.getTimestamp("workout_date")
                });
            }

            String[] columns = {"ID", "Workout", "Weight", "Duration", "Reps", "Sets", "Calories", "Date"};
            DefaultTableModel model = new DefaultTableModel(data.toArray(new Object[0][]), columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            workoutHistoryTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error loading workout history");
        }
    }

    private void updateExerciseCharts() {
        chartTabPane.removeAll();

        String sql = "SELECT DISTINCT workout_name FROM workouts WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String workoutName = rs.getString("workout_name");
                ChartPanel chartPanel = (ChartPanel) ChartUtils.createWorkoutProgressChart(userId, workoutName);
                chartTabPane.addTab(workoutName, chartPanel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error loading exercise charts");
        }
    }

    private void clearForm() {
        workoutNameField.setText("");
        weightField.setText("");
        durationField.setText("");
        repsField.setText("");
        setsField.setText("");
        caloriesField.setText("");
        selectedWorkoutId = -1;
        submitButton.setText("Log Workout");
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WorkoutEntryForm(1)); // Example with userId = 1
    }
}
