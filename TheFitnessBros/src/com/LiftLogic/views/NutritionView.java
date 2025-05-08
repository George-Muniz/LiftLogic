package com.LiftLogic.views;

import com.LiftLogic.App;
import com.LiftLogic.controllers.NutritionController;
import com.LiftLogic.model.NutritionLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class NutritionView {
    private JPanel panel;
    private JTable nutritionTable;
    private JButton addButton;
    private JButton viewButton;
    private JButton deleteButton;
    private JSpinner dateSpinner;
    private JLabel summaryLabel;
    private NutritionController nutritionController;

    public NutritionView(App app) {
        nutritionController = new NutritionController();
        initializeUI(app);
        loadNutritionLogs(app.getAuthController().getCurrentUser().getId());
        updateSummary();
    }

    private void initializeUI(App app) {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Toolbar
        JPanel toolbar = createToolbar(app);

        // Nutrition table
        nutritionTable = createNutritionTable();
        JScrollPane scrollPane = new JScrollPane(nutritionTable);

        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryLabel = new JLabel();
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(summaryLabel);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);
    }

    private JPanel createToolbar(App app) {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // Date selector
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(java.util.Calendar.getInstance().getTime());

        // Buttons
        addButton = new JButton("Add Entry");
        viewButton = new JButton("View Details");
        deleteButton = new JButton("Delete");

        // Button actions
        addButton.addActionListener(e -> showAddNutritionDialog(app));
        viewButton.addActionListener(e -> showNutritionDetails(app));
        deleteButton.addActionListener(e -> deleteSelectedNutritionLog(app));

        // Disable buttons until selection
        setNutritionActionButtonsEnabled(false);
        nutritionTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = nutritionTable.getSelectedRow() >= 0;
            setNutritionActionButtonsEnabled(rowSelected);
        });

        toolbar.add(new JLabel("Date:"));
        toolbar.add(dateSpinner);
        toolbar.add(addButton);
        toolbar.add(viewButton);
        toolbar.add(deleteButton);

        return toolbar;
    }

    private JTable createNutritionTable() {
        String[] columnNames = {"Date", "Calories", "Protein (g)", "Carbs (g)", "Fats (g)"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? String.class : Number.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);

        // Custom renderer for numbers
        table.setDefaultRenderer(Number.class, new RightAlignRenderer());

        return table;
    }

    private void setNutritionActionButtonsEnabled(boolean enabled) {
        viewButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    private void loadNutritionLogs(int userId) {
        DefaultTableModel model = (DefaultTableModel) nutritionTable.getModel();
        model.setRowCount(0);

        List<NutritionLog> logs = nutritionController.getNutritionLogs(userId);
        for (NutritionLog log : logs) {
            model.addRow(new Object[]{
                    log.getDate().toString(),
                    log.getCalories(),
                    log.getProtein(),
                    log.getCarbs(),
                    log.getFats()
            });
        }
    }

    private void updateSummary() {
        DefaultTableModel model = (DefaultTableModel) nutritionTable.getModel();
        int rowCount = model.getRowCount();

        if (rowCount == 0) {
            summaryLabel.setText("No nutrition data available");
            return;
        }

        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFats = 0;

        for (int i = 0; i < rowCount; i++) {
            totalCalories += ((Number)model.getValueAt(i, 1)).doubleValue();
            totalProtein += ((Number)model.getValueAt(i, 2)).doubleValue();
            totalCarbs += ((Number)model.getValueAt(i, 3)).doubleValue();
            totalFats += ((Number)model.getValueAt(i, 4)).doubleValue();
        }

        double avgCalories = totalCalories / rowCount;
        double avgProtein = totalProtein / rowCount;
        double avgCarbs = totalCarbs / rowCount;
        double avgFats = totalFats / rowCount;

        summaryLabel.setText(String.format(
                "Daily Averages: %.0f kcal | Protein: %.1fg | Carbs: %.1fg | Fats: %.1fg",
                avgCalories, avgProtein, avgCarbs, avgFats
        ));
    }

    private void showAddNutritionDialog(App app) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add Nutrition Entry");
        dialog.setModal(true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(panel);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Date field
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(new JSpinner(), "yyyy-MM-dd");
        JSpinner dateField = new JSpinner();
        dateField.setEditor(dateEditor);
        dateField.setValue(dateSpinner.getValue());

        // Nutrition fields
        JSpinner caloriesField = new JSpinner(new SpinnerNumberModel(2000, 0, 10000, 50));
        JSpinner proteinField = new JSpinner(new SpinnerNumberModel(150, 0, 500, 5));
        JSpinner carbsField = new JSpinner(new SpinnerNumberModel(200, 0, 500, 5));
        JSpinner fatsField = new JSpinner(new SpinnerNumberModel(70, 0, 200, 5));

        formPanel.add(new JLabel("Date:"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Calories:"));
        formPanel.add(caloriesField);
        formPanel.add(new JLabel("Protein (g):"));
        formPanel.add(proteinField);
        formPanel.add(new JLabel("Carbs (g):"));
        formPanel.add(carbsField);
        formPanel.add(new JLabel("Fats (g):"));
        formPanel.add(fatsField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            NutritionLog log = new NutritionLog();
            log.setUserId(app.getAuthController().getCurrentUser().getId());
            log.setDate(LocalDate.parse(
                    ((JSpinner.DateEditor)dateField.getEditor()).getTextField().getText()
            ));
            log.setCalories((Integer) caloriesField.getValue());
            log.setProtein((Double) proteinField.getValue());
            log.setCarbs((Double) carbsField.getValue());
            log.setFats((Double) fatsField.getValue());

            if (nutritionController.saveNutritionLog(log)) {
                loadNutritionLogs(log.getUserId());
                updateSummary();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to save nutrition log",
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

    private void showNutritionDetails(App app) {
        int selectedRow = nutritionTable.getSelectedRow();
        if (selectedRow < 0) return;

        String date = (String) nutritionTable.getValueAt(selectedRow, 0);
        int userId = app.getAuthController().getCurrentUser().getId();
        NutritionLog log = nutritionController.getNutritionLog(userId, LocalDate.parse(date));

        if (log == null) {
            JOptionPane.showMessageDialog(panel,
                    "Nutrition log not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("Nutrition Details - " + date);
        dialog.setModal(true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(panel);

        JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        detailsPanel.add(new JLabel("Date:"));
        detailsPanel.add(new JLabel(log.getDate().toString()));
        detailsPanel.add(new JLabel("Calories:"));
        detailsPanel.add(new JLabel(String.valueOf(log.getCalories())));
        detailsPanel.add(new JLabel("Protein:"));
        detailsPanel.add(new JLabel(log.getProtein() + " g"));
        detailsPanel.add(new JLabel("Carbohydrates:"));
        detailsPanel.add(new JLabel(log.getCarbs() + " g"));
        detailsPanel.add(new JLabel("Fats:"));
        detailsPanel.add(new JLabel(log.getFats() + " g"));

        // Calculate macronutrient ratios
        double proteinCal = log.getProtein() * 4;
        double carbsCal = log.getCarbs() * 4;
        double fatsCal = log.getFats() * 9;
        double totalCal = proteinCal + carbsCal + fatsCal;

        if (totalCal > 0) {
            detailsPanel.add(new JLabel("Macro Ratios:"));
            detailsPanel.add(new JLabel(String.format(
                    "P: %.1f%% C: %.1f%% F: %.1f%%",
                    (proteinCal / totalCal * 100),
                    (carbsCal / totalCal * 100),
                    (fatsCal / totalCal * 100)
            )));
        }

        dialog.add(detailsPanel);
        dialog.setVisible(true);
    }

    private void deleteSelectedNutritionLog(App app) {
        int selectedRow = nutritionTable.getSelectedRow();
        if (selectedRow < 0) return;

        String date = (String) nutritionTable.getValueAt(selectedRow, 0);
        int userId = app.getAuthController().getCurrentUser().getId();

        int confirm = JOptionPane.showConfirmDialog(panel,
                "Delete nutrition log for " + date + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (nutritionController.deleteNutritionLog(userId, LocalDate.parse(date))) {
                loadNutritionLogs(userId);
                updateSummary();
            } else {
                JOptionPane.showMessageDialog(panel,
                        "Failed to delete nutrition log",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    // Custom renderer for right-aligning numbers
    private static class RightAlignRenderer extends DefaultTableCellRenderer {
        public RightAlignRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }
    }
}