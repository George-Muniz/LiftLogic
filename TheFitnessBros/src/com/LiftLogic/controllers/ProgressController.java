package com.LiftLogic.controllers;

import com.LiftLogic.dao.NutritionDAO;
import com.LiftLogic.dao.ProfileDAO;
import com.LiftLogic.dao.WorkoutDAO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProgressController {
    private ProfileDAO profileDao;
    private WorkoutDAO workoutDao;
    private NutritionDAO nutritionDao;

    public ProgressController() {
        profileDao = new ProfileDAO();
        workoutDao = new WorkoutDAO();
        nutritionDao = new NutritionDAO();
    }

    public Map<LocalDate, Double> getWeightProgress(int userId) {
        // In a real app, this would query a dedicated weight tracking table
        // For now, we'll use the profile updates as weight data points
        return Collections.emptyMap(); // Implement with actual data
    }

    public Map<String, Integer> getWorkoutFrequency(int userId) {
        return workoutDao.getWorkoutsByUser(userId).stream()
                .collect(Collectors.groupingBy(
                        Workout::getWorkoutName,
                        Collectors.summingInt(w -> 1)
                ));
    }

    public Map<LocalDate, Integer> getWorkoutVolume(int userId) {
        return workoutDao.getWorkoutsByUser(userId).stream()
                .collect(Collectors.toMap(
                        Workout::getDate,
                        w -> workoutDao.getExerciseCount(w.getId())
                ));
    }

    public Map<LocalDate, Double> getCalorieTrends(int userId) {
        return nutritionDao.getNutritionLogs(userId).stream()
                .collect(Collectors.toMap(
                        NutritionLog::getDate,
                        NutritionLog::getCalories
                ));
    }

    public Map<String, Double> getMacroAverages(int userId) {
        List<NutritionLog> logs = nutritionDao.getNutritionLogs(userId);
        if (logs.isEmpty()) return Map.of();

        double avgProtein = logs.stream().mapToDouble(NutritionLog::getProtein).average().orElse(0);
        double avgCarbs = logs.stream().mapToDouble(NutritionLog::getCarbs).average().orElse(0);
        double avgFats = logs.stream().mapToDouble(NutritionLog::getFats).average().orElse(0);

        return Map.of(
                "Protein", avgProtein,
                "Carbs", avgCarbs,
                "Fats", avgFats
        );
    }
}
