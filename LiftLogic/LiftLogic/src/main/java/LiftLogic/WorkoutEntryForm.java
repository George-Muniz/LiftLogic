package LiftLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class WorkoutEntryForm extends JFrame {
    private JTextField workoutNameField;
    private JTextField weightField;
    private JTextField durationField;  // Optional field
    private JTextField repsField;      // Optional field
    private JTextField setsField;      // Optional field
    private JTextField caloriesField;
    private JButton submitButton;
    private JButton deleteButton; // Delete button
    private JButton editButton;   // Edit button
    private JButton backButton;
    private JTable workoutHistoryTable;

    private int selectedWorkoutId = -1;  // Track selected workout for editing or deletion

    public WorkoutEntryForm() {
        setTitle("Workout Entry");
        setLayout(new FlowLayout());

        // Components
        workoutNameField = new JTextField(20);
        weightField = new JTextField(10);
        durationField = new JTextField(10);  // Optional field for duration
        repsField = new JTextField(10);      // Optional field for reps
        setsField = new JTextField(10);      // Optional field for sets
        caloriesField = new JTextField(10);
        submitButton = new JButton("Log Workout");
        backButton = new JButton("Back");
        deleteButton = new JButton("Delete Workout"); // Delete button
        editButton = new JButton("Edit Workout");     // Edit button

        // Add components to the form
        add(new JLabel("Workout Name:"));
        add(workoutNameField);
        add(new JLabel("Weight:"));
        add(weightField);
        add(new JLabel("Duration (min, optional):"));
        add(durationField);
        add(new JLabel("Reps (optional):"));
        add(repsField);
        add(new JLabel("Sets (optional):"));
        add(setsField);
        add(new JLabel("Calories Burned:"));
        add(caloriesField);
        add(submitButton);
        add(backButton);
        add(editButton);     // Add edit button
        add(deleteButton);   // Add delete button

        // Display workout history in a table
        workoutHistoryTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(workoutHistoryTable);
        add(scrollPane);

        // Submit button action listener
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String workoutName = workoutNameField.getText();
                double weight = Double.parseDouble(weightField.getText());
                String durationText = durationField.getText();
                String repsText = repsField.getText();
                String setsText = setsField.getText();
                double calories = Double.parseDouble(caloriesField.getText());

                // Default values for optional fields
                Double duration = (durationText.isEmpty()) ? null : Double.parseDouble(durationText);
                Integer reps = (repsText.isEmpty()) ? null : Integer.parseInt(repsText);
                Integer sets = (setsText.isEmpty()) ? null : Integer.parseInt(setsText);

                // Check the condition: if duration is empty, reps and sets must be filled
                if (duration == null && (reps == null || sets == null)) {
                    JOptionPane.showMessageDialog(WorkoutEntryForm.this, "If duration is empty, reps and sets must be filled!");
                    return; // Stop further execution if the condition is not met
                }

                int userId = 1; // Hardcoded for now, replace with logged-in user's ID

                if (selectedWorkoutId == -1) { // If it's a new workout
                    if (logWorkout(userId, workoutName, weight, duration, reps, sets, calories)) {
                        JOptionPane.showMessageDialog(WorkoutEntryForm.this, "Workout logged successfully!");
                    } else {
                        JOptionPane.showMessageDialog(WorkoutEntryForm.this, "Error logging workout.");
                    }
                } else { // If it's an update to an existing workout
                    if (updateWorkout(selectedWorkoutId, workoutName, weight, duration, reps, sets, calories)) {
                        JOptionPane.showMessageDialog(WorkoutEntryForm.this, "Workout updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(WorkoutEntryForm.this, "Error updating workout.");
                    }
                }

                refreshWorkoutHistory(userId);  // Refresh workout history table
                selectedWorkoutId = -1; // Reset selected workout ID to -1 after add or update
            }
        });

        // Edit button action listener
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = workoutHistoryTable.getSelectedRow();
                if (row != -1) {
                    selectedWorkoutId = (int) workoutHistoryTable.getValueAt(row, 0); // Get workout ID
                    workoutNameField.setText((String) workoutHistoryTable.getValueAt(row, 1));
                    weightField.setText(workoutHistoryTable.getValueAt(row, 2).toString());
                    durationField.setText(workoutHistoryTable.getValueAt(row, 3) != null ? workoutHistoryTable.getValueAt(row, 3).toString() : "");
                    repsField.setText(workoutHistoryTable.getValueAt(row, 4) != null ? workoutHistoryTable.getValueAt(row, 4).toString() : "");
                    setsField.setText(workoutHistoryTable.getValueAt(row, 5) != null ? workoutHistoryTable.getValueAt(row, 5).toString() : "");
                    caloriesField.setText(workoutHistoryTable.getValueAt(row, 6).toString());
                } else {
                    JOptionPane.showMessageDialog(WorkoutEntryForm.this, "Please select a workout to edit.");
                }
            }
        });

        // Delete button action listener
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = workoutHistoryTable.getSelectedRow();
                if (row != -1) {
                    int workoutId = (int) workoutHistoryTable.getValueAt(row, 0); // Get workout ID

                    int confirm = JOptionPane.showConfirmDialog(WorkoutEntryForm.this,
                            "Are you sure you want to delete this workout?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (deleteWorkout(workoutId)) {
                            JOptionPane.showMessageDialog(WorkoutEntryForm.this, "Workout deleted successfully!");
                            refreshWorkoutHistory(1);  // Refresh the workout history table
                        } else {
                            JOptionPane.showMessageDialog(WorkoutEntryForm.this, "Error deleting workout.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(WorkoutEntryForm.this, "Please select a workout to delete.");
                }
            }
        });
        // Action listener for the Back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Dashboard();  // Go back to the Dashboard
                dispose(); // Close the current page (WorkoutEntryForm)
            }
        });

        // Initial setup
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Load initial workout history for the user
        refreshWorkoutHistory(1);  // Replace with actual user ID
    }

    // Method to log a workout in the database
    // Method to log a workout in the database with manual control of workout_id
    // Method to log a workout in the database with manual control of workout_id
    private boolean logWorkout(int userId, String workoutName, double weight, Double duration, Integer reps, Integer sets, double calories) {
        // Fetch the highest current workout_id and increment it
        String sqlGetMaxId = "SELECT MAX(workout_id) FROM Workouts";
        String sqlInsert = "INSERT INTO Workouts (workout_id, user_id, workout_name, weight, duration, reps, sets, calories_burned) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtMaxId = conn.prepareStatement(sqlGetMaxId);
             PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {

            // Get the current highest workout_id
            ResultSet rs = stmtMaxId.executeQuery();
            int nextWorkoutId = 1; // Default to 1 if no workouts exist
            if (rs.next()) {
                nextWorkoutId = rs.getInt(1) + 1; // Increment the max workout_id by 1
            }

            // Insert new workout with manually set workout_id
            stmtInsert.setInt(1, nextWorkoutId);  // Set the manually controlled workout_id
            stmtInsert.setInt(2, userId);
            stmtInsert.setString(3, workoutName);
            stmtInsert.setDouble(4, weight);
            if (duration != null) stmtInsert.setDouble(5, duration);
            else stmtInsert.setNull(5, java.sql.Types.DOUBLE);
            if (reps != null) stmtInsert.setInt(6, reps);
            else stmtInsert.setNull(6, java.sql.Types.INTEGER);
            if (sets != null) stmtInsert.setInt(7, sets);
            else stmtInsert.setNull(7, java.sql.Types.INTEGER);
            stmtInsert.setDouble(8, calories);

            int rowsAffected = stmtInsert.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to update an existing workout in the database
    private boolean updateWorkout(int workoutId, String workoutName, double weight, Double duration, Integer reps, Integer sets, double calories) {
        String sql = "UPDATE Workouts SET workout_name = ?, weight = ?, duration = ?, reps = ?, sets = ?, calories_burned = ? WHERE workout_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, workoutName);
            stmt.setDouble(2, weight);
            if (duration != null) stmt.setDouble(3, duration);
            else stmt.setNull(3, java.sql.Types.DOUBLE);

            if (reps != null) stmt.setInt(4, reps);
            else stmt.setNull(4, java.sql.Types.INTEGER);

            if (sets != null) stmt.setInt(5, sets);
            else stmt.setNull(5, java.sql.Types.INTEGER);

            stmt.setDouble(6, calories);
            stmt.setInt(7, workoutId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to delete a workout from the database
    // Method to delete a workout and re-sequence the workout IDs manually
    private boolean deleteWorkout(int workoutId) {
        String sqlDelete = "DELETE FROM Workouts WHERE workout_id = ?";
        String sqlReSequence = "UPDATE Workouts SET workout_id = workout_id - 1 WHERE workout_id > ? ORDER BY workout_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
             PreparedStatement stmtReSequence = conn.prepareStatement(sqlReSequence)) {

            // Delete the selected workout
            stmtDelete.setInt(1, workoutId);
            int rowsDeleted = stmtDelete.executeUpdate();

            if (rowsDeleted > 0) {
                // Re-sequence the remaining workout IDs
                stmtReSequence.setInt(1, workoutId);
                stmtReSequence.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to refresh workout history table
    private void refreshWorkoutHistory(int userId) {
        String sql = "SELECT workout_id, workout_name, weight, duration, reps, sets, calories_burned, workout_date FROM Workouts WHERE user_id = ? ORDER BY workout_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            ArrayList<Object[]> data = new ArrayList<>();
            while (rs.next()) {
                data.add(new Object[]{
                        rs.getInt("workout_id"), // Workout ID (for editing/deleting)
                        rs.getString("workout_name"),
                        rs.getDouble("weight"),
                        rs.getDouble("duration"),
                        rs.getInt("reps"),
                        rs.getInt("sets"),
                        rs.getDouble("calories_burned"),
                        rs.getTimestamp("workout_date")
                });
            }

            // Create table model and set it
            String[] columns = {"Workout ID", "Workout Name", "Weight", "Duration", "Reps", "Sets", "Calories Burned", "Workout Date"};
            Object[][] tableData = data.toArray(new Object[0][]);
            workoutHistoryTable.setModel(new DefaultTableModel(tableData, columns));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new WorkoutEntryForm();
    }
}
