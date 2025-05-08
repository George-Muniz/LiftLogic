package com.LiftLogic.model;

public class Exercise {
    private int id;
    private int workoutId;
    private String exerciseName;
    private int sets;
    private int reps;
    private double weight; // in kg

    // Constructors, getters, setters
    public Exercise() {}

    public Exercise(int id, int workoutId, String exerciseName, int sets, int reps, double weight) {
        this.id = id;
        this.workoutId = workoutId;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getWorkoutId() {
        return workoutId;
    }
    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }
    public String getExerciseName() {
        return exerciseName;
    }
    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
    public int getSets() {
        return sets;
    }
    public void setSets(int sets) {
        this.sets = sets;
    }
    public int getReps() {
        return reps;
    }
    public void setReps(int reps) {
        this.reps = reps;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
}

