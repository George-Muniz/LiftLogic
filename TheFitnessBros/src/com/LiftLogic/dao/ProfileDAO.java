package com.LiftLogic.dao;

import com.LiftLogic.model.UserProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileDAO {
    public boolean saveProfile(UserProfile profile) {
        String sql = "INSERT OR REPLACE INTO user_profiles(user_id, name, age, gender, height, weight) "
                + "VALUES(?,?,?,?,?,?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, profile.getUserId());
            pstmt.setString(2, profile.getName());
            pstmt.setInt(3, profile.getAge());
            pstmt.setString(4, profile.getGender());
            pstmt.setDouble(5, profile.getHeight());
            pstmt.setDouble(6, profile.getWeight());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving profile: " + e.getMessage());
            return false;
        }
    }

    public UserProfile getProfile(int userId) {
        String sql = "SELECT * FROM user_profiles WHERE user_id = ?";
        UserProfile profile = null;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                profile = new UserProfile();
                profile.setUserId(userId);
                profile.setName(rs.getString("name"));
                profile.setAge(rs.getInt("age"));
                profile.setGender(rs.getString("gender"));
                profile.setHeight(rs.getDouble("height"));
                profile.setWeight(rs.getDouble("weight"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching profile: " + e.getMessage());
        }

        return profile;
    }
}
