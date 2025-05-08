package com.LiftLogic.model;

import java.time.LocalDate;

public class Workout {
    private int id;
    private int userId;
    private LocalDate date;
    private String workoutName;
    private int duration; // in minutes
    private String notes;

    // Constructors, getters, setters
    public Workout() {}

    public Workout(int id, int userId, LocalDate date, String workoutName, int duration, String notes) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.workoutName = workoutName;
        this.duration = duration;
        this.notes = notes;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getWorkoutName() {
        return workoutName;
    }
    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}

