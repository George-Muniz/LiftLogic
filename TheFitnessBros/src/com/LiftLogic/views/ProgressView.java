package com.LiftLogic.views;

import com.LiftLogic.App;
import com.LiftLogic.controllers.ProgressController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

public class ProgressView {
    private JPanel panel;
    private ProgressController progressController;

    public ProgressView(App app) {
        progressController = new ProgressController();
        initializeUI(app);
    }

    private void initializeUI(App app) {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane tabbedPane = new JTabbedPane();

        // Weight Progress Tab
        tabbedPane.addTab("Weight Trend", createWeightProgressPanel(app));

        // Workout Progress Tab
        tabbedPane.addTab("Workout Progress", createWorkoutProgressPanel(app));

        // Nutrition Progress Tab
        tabbedPane.addTab("Nutrition Trends", createNutritionProgressPanel(app));

        panel.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createWeightProgressPanel(App app) {
        JPanel weightPanel = new JPanel(new BorderLayout());

        // Get weight data
        Map<LocalDate, Double> weightData = progressController.getWeightProgress(
                app.getAuthController().getCurrentUser().getId());

        // Create dataset
        TimeSeries series = new TimeSeries("Weight (kg)");
        weightData.forEach((date, weight) ->
                series.add(new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear()), weight));

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);

        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Weight Progress",
                "Date",
                "Weight (kg)",
                dataset,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));

        weightPanel.add(chartPanel, BorderLayout.CENTER);

        // Add stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        double currentWeight = weightData.values().stream().reduce((first, second) -> second).orElse(0.0);
        double startingWeight = weightData.values().stream().findFirst().orElse(0.0);
        double weightChange = currentWeight - startingWeight;

        statsPanel.add(createStatCard("Current Weight", String.format("%.1f kg", currentWeight)));
        statsPanel.add(createStatCard("Starting Weight", String.format("%.1f kg", startingWeight)));
        statsPanel.add(createStatCard("Total Change",
                String.format("%.1f kg (%s)", Math.abs(weightChange),
                        weightChange >= 0 ? "gain" : "loss")));

        weightPanel.add(statsPanel, BorderLayout.SOUTH);

        return weightPanel;
    }

    private JPanel createWorkoutProgressPanel(App app) {
        JPanel workoutPanel = new JPanel(new BorderLayout());

        // Get workout data
        Map<String, Integer> workoutFrequency = progressController.getWorkoutFrequency(
                app.getAuthController().getCurrentUser().getId());
        Map<LocalDate, Integer> workoutVolume = progressController.getWorkoutVolume(
                app.getAuthController().getCurrentUser().getId());

        // Create frequency chart
        DefaultCategoryDataset frequencyDataset = new DefaultCategoryDataset();
        workoutFrequency.forEach((type, count) ->
                frequencyDataset.addValue(count, "Workouts", type));

        JFreeChart frequencyChart = ChartFactory.createBarChart(
                "Workout Frequency by Type",
                "Workout Type",
                "Count",
                frequencyDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Create volume chart
        TimeSeries volumeSeries = new TimeSeries("Total Exercises");
        workoutVolume.forEach((date, count) ->
                volumeSeries.add(new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear()), count));

        TimeSeriesCollection volumeDataset = new TimeSeriesCollection(volumeSeries);

        JFreeChart volumeChart = ChartFactory.createTimeSeriesChart(
                "Workout Volume Over Time",
                "Date",
                "Exercise Count",
                volumeDataset,
                true,
                true,
                false
        );

        // Create tabbed pane for workout charts
        JTabbedPane workoutCharts = new JTabbedPane();
        workoutCharts.addTab("Frequency", new ChartPanel(frequencyChart));
        workoutCharts.addTab("Volume", new ChartPanel(volumeChart));

        workoutPanel.add(workoutCharts, BorderLayout.CENTER);

        return workoutPanel;
    }

    private JPanel createNutritionProgressPanel(App app) {
        JPanel nutritionPanel = new JPanel(new BorderLayout());

        // Get nutrition data
        Map<LocalDate, Double> calorieData = progressController.getCalorieTrends(
                app.getAuthController().getCurrentUser().getId());
        Map<String, Double> macroAverages = progressController.getMacroAverages(
                app.getAuthController().getCurrentUser().getId());

        // Create calorie chart
        TimeSeries calorieSeries = new TimeSeries("Calories");
        calorieData.forEach((date, calories) ->
                calorieSeries.add(new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear()), calories));

        TimeSeriesCollection calorieDataset = new TimeSeriesCollection(calorieSeries);

        JFreeChart calorieChart = ChartFactory.createTimeSeriesChart(
                "Daily Calorie Intake",
                "Date",
                "Calories",
                calorieDataset,
                true,
                true,
                false
        );

        // Create macro chart
        DefaultCategoryDataset macroDataset = new DefaultCategoryDataset();
        macroAverages.forEach((macro, value) ->
                macroDataset.addValue(value, "Average", macro));

        JFreeChart macroChart = ChartFactory.createBarChart(
                "Average Macronutrient Intake",
                "Macronutrient",
                "Grams",
                macroDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Create tabbed pane for nutrition charts
        JTabbedPane nutritionCharts = new JTabbedPane();
        nutritionCharts.addTab("Calories", new ChartPanel(calorieChart));
        nutritionCharts.addTab("Macronutrients", new ChartPanel(macroChart));

        nutritionPanel.add(nutritionCharts, BorderLayout.CENTER);

        return nutritionPanel;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public JPanel getPanel() {
        return panel;
    }
}
