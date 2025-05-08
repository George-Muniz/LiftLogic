package com.LiftLogic;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ChartUtils {

    public static JPanel createDynamicWorkoutProgressCharts(int userId) {
        JTabbedPane workoutTabPane = new JTabbedPane();
        String sql = "SELECT DISTINCT workout_name FROM workouts WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String workoutName = rs.getString("workout_name");
                JPanel chartPanel = createWorkoutProgressChart(userId, workoutName);
                workoutTabPane.addTab(workoutName, chartPanel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(workoutTabPane, BorderLayout.CENTER);
        return panel;
    }

    public static JPanel createWorkoutProgressChart(int userId, String workoutName) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = "SELECT weight, workout_date, reps, sets FROM workouts WHERE user_id = ? AND workout_name = ? ORDER BY workout_date ASC";

        double maxWeight = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, workoutName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double weight = rs.getDouble("weight");
                int reps = rs.getInt("reps");
                int sets = rs.getInt("sets");
                Date workoutDate = rs.getDate("workout_date");

                double volume = weight * reps * sets;
                dataset.addValue(volume, "Volume", workoutDate.toString());
                dataset.addValue(weight, "Weight", workoutDate.toString());

                if (weight > maxWeight) {
                    maxWeight = weight;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createLineChart(
                workoutName + " Progress",
                "Date",
                "Weight / Volume",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true, true, false
        );

        // Adjust Y-Axis to start from 0 and end at max weight + 20
        NumberAxis rangeAxis = (NumberAxis) chart.getCategoryPlot().getRangeAxis();
        rangeAxis.setLowerBound(0);
        rangeAxis.setUpperBound(maxWeight + 20);

        return new ChartPanel(chart);
    }

    public static JPanel createBodyWeightChart(int userId) {
        TimeSeries series = new TimeSeries("Body Weight");
        double maxWeight = 0;

        String sql = "SELECT body_weight, recorded_date FROM body_weight_history WHERE user_id = ? ORDER BY recorded_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double weight = rs.getDouble("body_weight");
                Date date = rs.getDate("recorded_date"); // Use getDate for Date object
                series.add(new Day(date), weight);

                if (weight > maxWeight) {
                    maxWeight = weight;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Body Weight Progress",
                "Date",
                "Weight (lbs/kg)",
                dataset,
                true, true, false
        );

        // Adjust Y-Axis to start from 50 and end at (max weight + 40)
        NumberAxis rangeAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
        rangeAxis.setLowerBound(50);
        rangeAxis.setUpperBound(maxWeight + 40);

        return new ChartPanel(chart);
    }

    public static JPanel createNutritionChart(int userId) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = "SELECT DATE(meal_date) AS meal_day, SUM(calories) AS calories, SUM(protein) AS protein, SUM(carbs) AS carbs, SUM(fats) AS fats FROM meals WHERE user_id = ? GROUP BY meal_day ORDER BY meal_day ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date mealDate = rs.getDate("meal_day");
                dataset.addValue(rs.getDouble("calories"), "Calories", mealDate.toString());
                dataset.addValue(rs.getDouble("protein"), "Protein", mealDate.toString());
                dataset.addValue(rs.getDouble("carbs"), "Carbs", mealDate.toString());
                dataset.addValue(rs.getDouble("fats"), "Fats", mealDate.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createStackedBarChart("Nutrition Progress","Date","Amount (g/kcal)",dataset,org.jfree.chart.plot.PlotOrientation.VERTICAL,true,true,false);
        return new ChartPanel(chart);
    }
}
