package com.LiftLogic;

import com.LiftLogic.controllers.AuthController;
import com.LiftLogic.dao.DatabaseHelper;
import com.LiftLogic.views.DashboardView;
import com.LiftLogic.views.LoginView;
import com.LiftLogic.views.NutritionView;
import com.LiftLogic.views.ProfileView;

import javax.swing.*;

public class App {
    private static App instance;
    private JFrame mainFrame;
    private AuthController authController;

    // Application views
    private LoginView loginView;
    private RegisterView registerView;
    private DashboardView dashboardView;
    private WorkoutLogView workoutLogView;
    private NutritionView nutritionView;
    private ProfileView profileView;

    public App() {
        // Initialize database
        DatabaseHelper.initializeDatabase();

        // Initialize controllers
        authController = new AuthController();

        // Initialize views
        loginView = new LoginView(this);
        registerView = new RegisterView(this);
        dashboardView = new DashboardView(this);
        workoutLogView = new WorkoutLogView(this);
        nutritionView = new NutritionView(this);
        profileView = new ProfileView(this);

        // Configure main frame
        mainFrame = new JFrame("LiftLogic - Fitness Tracker");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 650);
        mainFrame.setLocationRelativeTo(null); // Center on screen
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            instance = new App();
            instance.showLoginView();
        });
    }

    public static App getInstance() {
        return instance;
    }

    public AuthController getAuthController() {
        return authController;
    }

    // View navigation methods
    public void showLoginView() {
        mainFrame.setContentPane(loginView.getPanel());
        mainFrame.setJMenuBar(null);
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }

    public void showRegisterView() {
        mainFrame.setContentPane(registerView.getPanel());
        mainFrame.setJMenuBar(null);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void showDashboardView() {
        mainFrame.setContentPane(dashboardView.getPanel());
        mainFrame.setJMenuBar(createMenuBar());
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void showWorkoutLogView() {
        mainFrame.setContentPane(workoutLogView.getPanel());
        mainFrame.setJMenuBar(createMenuBar());
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void showNutritionView() {
        mainFrame.setContentPane(nutritionView.getPanel());
        mainFrame.setJMenuBar(createMenuBar());
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void showProfileView() {
        mainFrame.setContentPane(profileView.getPanel());
        mainFrame.setJMenuBar(createMenuBar());
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            authController.logout();
            showLoginView();
        });
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Navigation Menu
        JMenu navMenu = new JMenu("Navigation");
        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        dashboardItem.addActionListener(e -> showDashboardView());
        JMenuItem workoutItem = new JMenuItem("Workout Log");
        workoutItem.addActionListener(e -> showWorkoutLogView());
        JMenuItem nutritionItem = new JMenuItem("Nutrition Tracker");
        nutritionItem.addActionListener(e -> showNutritionView());
        JMenuItem profileItem = new JMenuItem("My Profile");
        profileItem.addActionListener(e -> showProfileView());

        navMenu.add(dashboardItem);
        navMenu.add(workoutItem);
        navMenu.add(nutritionItem);
        navMenu.add(profileItem);

        menuBar.add(fileMenu);
        menuBar.add(navMenu);

        return menuBar;
    }
}