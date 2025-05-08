package com.LiftLogic.dao;

import com.LiftLogic.model.Exercise;
import com.LiftLogic.model.Workout;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDAO {
    public boolean saveWorkout(Workout workout) {
        String sql = "INSERT INTO workouts(user_id, date, workout_name, duration, notes) "
                + "VALUES(?,?,?,?,?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, workout.getUserId());
            pstmt.setString(2, workout.getDate().toString());
            pstmt.setString(3, workout.getWorkoutName());
            pstmt.setInt(4, workout.getDuration());
            pstmt.setString(5, workout.getNotes());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        workout.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error saving workout: " + e.getMessage());
        }
        return false;
    }

    public List<Workout> getWorkoutsByUser(int userId) {
        String sql = "SELECT * FROM workouts WHERE user_id = ? ORDER BY date DESC";
        List<Workout> workouts = new ArrayList<>();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Workout workout = new Workout();
                workout.setId(rs.getInt("id"));
                workout.setUserId(userId);
                workout.setDate(LocalDate.parse(rs.getString("date")));
                workout.setWorkoutName(rs.getString("workout_name"));
                workout.setDuration(rs.getInt("duration"));
                workout.setNotes(rs.getString("notes"));

                workouts.add(workout);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching workouts: " + e.getMessage());
        }

        return workouts;
    }

    public int getExerciseCount(int workoutId) {
        String sql = "SELECT COUNT(*) FROM exercises WHERE workout_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, workoutId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting exercises: " + e.getMessage());
        }

        return 0;
    }
    public boolean saveExercise(Exercise exercise) {
        String sql = "INSERT INTO exercises(workout_id, exercise_name, sets, reps, weight) "
                + "VALUES(?,?,?,?,?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, exercise.getWorkoutId());
            pstmt.setString(2, exercise.getExerciseName());
            pstmt.setInt(3, exercise.getSets());
            pstmt.setInt(4, exercise.getReps());
            pstmt.setDouble(5, exercise.getWeight());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving exercise: " + e.getMessage());
            return false;
        }
    }

    public List<Exercise> getExercisesForWorkout(int workoutId) {
        String sql = "SELECT * FROM exercises WHERE workout_id = ? ORDER BY id";
        List<Exercise> exercises = new ArrayList<>();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, workoutId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Exercise exercise = new Exercise();
                exercise.setId(rs.getInt("id"));
                exercise.setWorkoutId(workoutId);
                exercise.setExerciseName(rs.getString("exercise_name"));
                exercise.setSets(rs.getInt("sets"));
                exercise.setReps(rs.getInt("reps"));
                exercise.setWeight(rs.getDouble("weight"));

                exercises.add(exercise);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching exercises: " + e.getMessage());
        }

        return exercises;
    }

    public Workout getWorkout(int userId, LocalDate date, String workoutName) {
        String sql = "SELECT * FROM workouts WHERE user_id = ? AND date = ? AND workout_name = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, date.toString());
            pstmt.setString(3, workoutName);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Workout workout = new Workout();
                workout.setId(rs.getInt("id"));
                workout.setUserId(userId);
                workout.setDate(date);
                workout.setWorkoutName(workoutName);
                workout.setDuration(rs.getInt("duration"));
                workout.setNotes(rs.getString("notes"));
                return workout;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching workout: " + e.getMessage());
        }

        return null;
    }

    public boolean deleteWorkout(int workoutId) {
        // First delete all exercises for this workout
        String deleteExercisesSql = "DELETE FROM exercises WHERE workout_id = ?";
        String deleteWorkoutSql = "DELETE FROM workouts WHERE id = ?";

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(deleteExercisesSql)) {
                pstmt.setInt(1, workoutId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(deleteWorkoutSql)) {
                pstmt.setInt(1, workoutId);
                int affectedRows = pstmt.executeUpdate();
                conn.commit();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting workout: " + e.getMessage());
            return false;
        }
    }
}
