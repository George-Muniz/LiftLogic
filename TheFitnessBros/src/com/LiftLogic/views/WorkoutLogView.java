package com.liftlogic.views;

import com.LiftLogic.App;
import com.LiftLogic.controllers.*;
import com.LiftLogic.model.Exercise;
import com.LiftLogic.model.Workout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class WorkoutLogView {
    private JPanel panel;
    private JTable workoutTable;
    private JButton addButton;
    private JButton viewButton;
    private JButton deleteButton;
    private JButton addExerciseButton;
    private JSpinner dateSpinner;
    private WorkoutController workoutController;

    public WorkoutLogView(App app) {
        workoutController = new WorkoutController();
        initializeUI(app);
        loadWorkouts(app.getAuthController().getCurrentUser().getId());
    }

    private void initializeUI(App app) {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Toolbar
        JPanel toolbar = createToolbar(app);

        // Workout table
        workoutTable = createWorkoutTable();
        JScrollPane scrollPane = new JScrollPane(workoutTable);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createToolbar(App app) {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // Date selector
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(java.util.Calendar.getInstance().getTime());

        // Buttons
        addButton = new JButton("Add Workout");
        viewButton = new JButton("View Details");
        deleteButton = new JButton("Delete");
        addExerciseButton = new JButton("Add Exercise");

        // Button styling
        for (JButton button : new JButton[]{addButton, viewButton, deleteButton, addExerciseButton}) {
            button.setMargin(new Insets(5, 10, 5, 10));
        }

        // Button actions
        addButton.addActionListener(e -> showAddWorkoutDialog(app));
        viewButton.addActionListener(e -> showWorkoutDetails(app));
        deleteButton.addActionListener(e -> deleteSelectedWorkout(app));
        addExerciseButton.addActionListener(e -> showAddExerciseDialog(app));

        // Disable buttons until a workout is selected
        setWorkoutActionButtonsEnabled(false);

        toolbar.add(new JLabel("Date:"));
        toolbar.add(dateSpinner);
        toolbar.add(addButton);
        toolbar.add(viewButton);
        toolbar.add(deleteButton);
        toolbar.add(addExerciseButton);

        return toolbar;
    }

    private JTable createWorkoutTable() {
        String[] columnNames = {"Date", "Workout Type", "Duration", "Exercises"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = table.getSelectedRow() >= 0;
            setWorkoutActionButtonsEnabled(rowSelected);
        });

        // Custom row height
        table.setRowHeight(30);

        return table;
    }

    private void setWorkoutActionButtonsEnabled(boolean enabled) {
        viewButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        addExerciseButton.setEnabled(enabled);
    }

    private void loadWorkouts(int userId) {
        DefaultTableModel model = (DefaultTableModel) workoutTable.getModel();
        model.setRowCount(0); // Clear existing data

        List<Workout> workouts = workoutController.getUserWorkouts(userId);
        for (Workout workout : workouts) {
            int exerciseCount = workoutController.getExerciseCount(workout.getId());
            model.addRow(new Object[]{
                    workout.getDate().toString(),
                    workout.getWorkoutName(),
                    workout.getDuration() + " mins",
                    exerciseCount + " exercises"
            });
        }
    }

    private void showAddWorkoutDialog(App app) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Workout");
        dialog.setModal(true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(panel);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Date field
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(new JSpinner(), "yyyy-MM-dd");
        JSpinner dateField = new JSpinner();
        dateField.setEditor(dateEditor);
        dateField.setValue(dateSpinner.getValue());

        // Workout type
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
                "Upper Body", "Lower Body", "Full Body", "Cardio", "HIIT", "Yoga", "Custom"
        });

        // Duration
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 180, 5));

        // Notes
        JTextField notesField = new JTextField();

        formPanel.add(new JLabel("Date:"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Workout Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Duration (mins):"));
        formPanel.add(durationSpinner);
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(notesField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            Workout workout = new Workout();
            workout.setUserId(app.getAuthController().getCurrentUser().getId());
            workout.setDate(LocalDate.parse(
                    ((JSpinner.DateEditor)dateField.getEditor()).getTextField().getText()
            ));
            workout.setWorkoutName((String) typeCombo.getSelectedItem());
            workout.setDuration((Integer) durationSpinner.getValue());
            workout.setNotes(notesField.getText());

            if (workoutController.saveWorkout(workout)) {
                loadWorkouts(workout.getUserId());
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to save workout",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showWorkoutDetails(App app) {
        int selectedRow = workoutTable.getSelectedRow();
        if (selectedRow < 0) return;

        String date = (String) workoutTable.getValueAt(selectedRow, 0);
        String workoutName = (String) workoutTable.getValueAt(selectedRow, 1);

        // Get workout ID from database
        int userId = app.getAuthController().getCurrentUser().getId();
        Workout workout = workoutController.getWorkout(userId, LocalDate.parse(date), workoutName);

        if (workout == null) {
            JOptionPane.showMessageDialog(panel,
                    "Workout not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create details dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("Workout Details - " + workoutName);
        dialog.setModal(true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(panel);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        infoPanel.add(new JLabel("Date:"));
        infoPanel.add(new JLabel(workout.getDate().toString()));
        infoPanel.add(new JLabel("Type:"));
        infoPanel.add(new JLabel(workout.getWorkoutName()));
        infoPanel.add(new JLabel("Duration:"));
        infoPanel.add(new JLabel(workout.getDuration() + " minutes"));
        infoPanel.add(new JLabel("Notes:"));
        infoPanel.add(new JLabel(workout.getNotes() != null ? workout.getNotes() : ""));

        // Exercises table
        String[] exerciseColumns = {"Exercise", "Sets", "Reps", "Weight (kg)"};
        DefaultTableModel exerciseModel = new DefaultTableModel(exerciseColumns, 0);
        JTable exerciseTable = new JTable(exerciseModel);

        List<Exercise> exercises = workoutController.getExercisesForWorkout(workout.getId());
        for (Exercise exercise : exercises) {
            exerciseModel.addRow(new Object[]{
                    exercise.getExerciseName(),
                    exercise.getSets(),
                    exercise.getReps(),
                    exercise.getWeight()
            });
        }

        JScrollPane exerciseScrollPane = new JScrollPane(exerciseTable);

        // Add components to main panel
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(new JLabel("Exercises:", JLabel.LEFT), BorderLayout.CENTER);
        mainPanel.add(exerciseScrollPane, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showAddExerciseDialog(App app) {
        int selectedRow = workoutTable.getSelectedRow();
        if (selectedRow < 0) return;

        String date = (String) workoutTable.getValueAt(selectedRow, 0);
        String workoutName = (String) workoutTable.getValueAt(selectedRow, 1);

        int userId = app.getAuthController().getCurrentUser().getId();
        Workout workout = workoutController.getWorkout(userId, LocalDate.parse(date), workoutName);

        if (workout == null) {
            JOptionPane.showMessageDialog(panel,
                    "Workout not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("Add Exercise to " + workoutName);
        dialog.setModal(true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(panel);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Exercise name
        JComboBox<String> exerciseCombo = new JComboBox<>(new String[]{
                "Bench Press", "Squat", "Deadlift", "Overhead Press", "Pull-up",
                "Barbell Row", "Dumbbell Curl", "Triceps Extension", "Leg Press", "Custom"
        });
        exerciseCombo.setEditable(true);

        // Sets, reps, weight
        JSpinner setsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        JSpinner repsSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
        JSpinner weightSpinner = new JSpinner(new SpinnerNumberModel(20.0, 0.0, 500.0, 2.5));

        formPanel.add(new JLabel("Exercise:"));
        formPanel.add(exerciseCombo);
        formPanel.add(new JLabel("Sets:"));
        formPanel.add(setsSpinner);
        formPanel.add(new JLabel("Reps:"));
        formPanel.add(repsSpinner);
        formPanel.add(new JLabel("Weight (kg):"));
        formPanel.add(weightSpinner);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            Exercise exercise = new Exercise();
            exercise.setWorkoutId(workout.getId());
            exercise.setExerciseName((String) exerciseCombo.getSelectedItem());
            exercise.setSets((Integer) setsSpinner.getValue());
            exercise.setReps((Integer) repsSpinner.getValue());
            exercise.setWeight((Double) weightSpinner.getValue());

            if (workoutController.saveExercise(exercise)) {
                loadWorkouts(userId);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to save exercise",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedWorkout(App app) {
        int selectedRow = workoutTable.getSelectedRow();
        if (selectedRow < 0) return;

        String date = (String) workoutTable.getValueAt(selectedRow, 0);
        String workoutName = (String) workoutTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(panel,
                "Delete workout '" + workoutName + "' on " + date + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int userId = app.getAuthController().getCurrentUser().getId();
            Workout workout = workoutController.getWorkout(userId, LocalDate.parse(date), workoutName);

            if (workout != null && workoutController.deleteWorkout(workout.getId())) {
                loadWorkouts(userId);
            } else {
                JOptionPane.showMessageDialog(panel,
                        "Failed to delete workout",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public JPanel getPanel() {
        return panel;
    }
}