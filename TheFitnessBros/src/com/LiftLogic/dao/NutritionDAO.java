package com.LiftLogic.dao;

import com.LiftLogic.model.NutritionLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NutritionDAO {
    public boolean saveNutritionLog(NutritionLog log) {
        String sql = "INSERT OR REPLACE INTO nutrition_logs(user_id, date, calories, protein, carbs, fats) "
                + "VALUES(?,?,?,?,?,?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, log.getUserId());
            pstmt.setString(2, log.getDate().toString());
            pstmt.setInt(3, log.getCalories());
            pstmt.setDouble(4, log.getProtein());
            pstmt.setDouble(5, log.getCarbs());
            pstmt.setDouble(6, log.getFats());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving nutrition log: " + e.getMessage());
            return false;
        }
    }

    public List<NutritionLog> getNutritionLogs(int userId) {
        String sql = "SELECT * FROM nutrition_logs WHERE user_id = ? ORDER BY date DESC";
        List<NutritionLog> logs = new ArrayList<>();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                NutritionLog log = new NutritionLog();
                log.setId(rs.getInt("id"));
                log.setUserId(userId);
                log.setDate(LocalDate.parse(rs.getString("date")));
                log.setCalories(rs.getInt("calories"));
                log.setProtein(rs.getDouble("protein"));
                log.setCarbs(rs.getDouble("carbs"));
                log.setFats(rs.getDouble("fats"));

                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching nutrition logs: " + e.getMessage());
        }

        return logs;
    }

    public NutritionLog getNutritionLog(int userId, LocalDate date) {
        String sql = "SELECT * FROM nutrition_logs WHERE user_id = ? AND date = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, date.toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                NutritionLog log = new NutritionLog();
                log.setId(rs.getInt("id"));
                log.setUserId(userId);
                log.setDate(date);
                log.setCalories(rs.getInt("calories"));
                log.setProtein(rs.getDouble("protein"));
                log.setCarbs(rs.getDouble("carbs"));
                log.setFats(rs.getDouble("fats"));
                return log;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching nutrition log: " + e.getMessage());
        }

        return null;
    }

    public boolean deleteNutritionLog(int userId, LocalDate date) {
        String sql = "DELETE FROM nutrition_logs WHERE user_id = ? AND date = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, date.toString());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting nutrition log: " + e.getMessage());
            return false;
        }
    }
}