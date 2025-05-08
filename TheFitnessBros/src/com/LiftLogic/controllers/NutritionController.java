package com.LiftLogic.controllers;

import com.LiftLogic.dao.NutritionDAO;
import com.LiftLogic.model.NutritionLog;

import java.time.LocalDate;
import java.util.List;

public class NutritionController {
    private NutritionDAO nutritionDao;

    public NutritionController() {
        nutritionDao = new NutritionDAO();
    }

    public boolean saveNutritionLog(NutritionLog log) {
        return nutritionDao.saveNutritionLog(log);
    }

    public List<NutritionLog> getNutritionLogs(int userId) {
        return nutritionDao.getNutritionLogs(userId);
    }

    public NutritionLog getNutritionLog(int userId, LocalDate date) {
        return nutritionDao.getNutritionLog(userId, date);
    }

    public boolean deleteNutritionLog(int userId, LocalDate date) {
        return nutritionDao.deleteNutritionLog(userId, date);
    }
}
