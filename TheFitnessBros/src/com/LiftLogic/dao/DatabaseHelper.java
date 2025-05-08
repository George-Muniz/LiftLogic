// DatabaseHelper.java
package com.LiftLogic.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:liftlogic.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Create users table
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "email TEXT UNIQUE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(sql);

            // Create user_profiles table
            sql = "CREATE TABLE IF NOT EXISTS user_profiles (" +
                    "user_id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "age INTEGER," +
                    "gender TEXT," +
                    "height REAL," +
                    "weight REAL," +
                    "FOREIGN KEY(user_id) REFERENCES users(id))";
            stmt.execute(sql);

            // Create workouts table
            sql = "CREATE TABLE IF NOT EXISTS workouts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "date TEXT," +
                    "workout_name TEXT," +
                    "duration INTEGER," +
                    "notes TEXT," +
                    "FOREIGN KEY(user_id) REFERENCES users(id))";
            stmt.execute(sql);

            // Create exercises table
            sql = "CREATE TABLE IF NOT EXISTS exercises (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "workout_id INTEGER," +
                    "exercise_name TEXT," +
                    "sets INTEGER," +
                    "reps INTEGER," +
                    "weight REAL," +
                    "FOREIGN KEY(workout_id) REFERENCES workouts(id))";
            stmt.execute(sql);

            // Create nutrition_logs table
            sql = "CREATE TABLE IF NOT EXISTS nutrition_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "date TEXT," +
                    "calories INTEGER," +
                    "protein REAL," +
                    "carbs REAL," +
                    "fats REAL," +
                    "FOREIGN KEY(user_id) REFERENCES users(id))";
            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
