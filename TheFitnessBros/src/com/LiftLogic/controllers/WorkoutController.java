package com.liftlogic.controllers;

import com.LiftLogic.dao.WorkoutDAO;
import com.LiftLogic.model.Exercise;
import com.LiftLogic.model.Workout;

import java.time.LocalDate;
import java.util.List;

public class WorkoutController {
    private WorkoutDAO workoutDao;

    public WorkoutController() {
        workoutDao = new WorkoutDAO();
    }

    public boolean saveWorkout(Workout workout) {
        return workoutDao.saveWorkout(workout);
    }

    public List<Workout> getUserWorkouts(int userId) {
        return workoutDao.getWorkoutsByUser(userId);
    }

    public Workout getWorkout(int userId, LocalDate date, String workoutName) {
        return workoutDao.getWorkout(userId, date, workoutName);
    }

    public int getExerciseCount(int workoutId) {
        return workoutDao.getExerciseCount(workoutId);
    }

    public boolean saveExercise(Exercise exercise) {
        return workoutDao.saveExercise(exercise);
    }

    public List<Exercise> getExercisesForWorkout(int workoutId) {
        return workoutDao.getExercisesForWorkout(workoutId);
    }

    public boolean deleteWorkout(int workoutId) {
        return workoutDao.deleteWorkout(workoutId);
    }
}