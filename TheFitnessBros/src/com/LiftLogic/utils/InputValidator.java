package com.LiftLogic.utils;

import javax.swing.*;
import java.time.LocalDate;

public class InputValidator {
    public static boolean validateDate(JSpinner dateSpinner) {
        try {
            String dateStr = ((JSpinner.DateEditor)dateSpinner.getEditor()).getTextField().getText();
            LocalDate.parse(dateStr);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Invalid date format. Please use YYYY-MM-DD",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static boolean validatePositiveNumber(JSpinner spinner, String fieldName) {
        if (((Number)spinner.getValue()).doubleValue() <= 0) {
            JOptionPane.showMessageDialog(null,
                    fieldName + " must be a positive number",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static boolean validateTextField(JTextField field, String fieldName) {
        if (field.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    fieldName + " cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
