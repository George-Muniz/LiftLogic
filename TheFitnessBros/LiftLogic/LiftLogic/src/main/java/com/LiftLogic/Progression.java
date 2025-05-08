package com.LiftLogic;

import org.jfree.chart.ChartPanel;
import javax.swing.*;
import java.awt.*;

public class Progression extends JFrame {
    private static final Color PLUM = new Color(142, 69, 133);
    private static final Color TEAL = new Color(64, 224, 208);
    private static final Color BG_DARK = new Color(46, 46, 46);
    private static final Color CARD_BG_DARK = new Color(60, 60, 60, 230);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);

    private int userId;
    private JTabbedPane tabbedPane;

    public Progression(int userId) {
        this.userId = userId;
        setTitle("Progression - LiftLogic");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // Title Panel
        JLabel title = new JLabel("📈 Your Progression Charts", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        title.setForeground(PLUM);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Tabbed Pane for Charts
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(CARD_BG_DARK);
        tabbedPane.setForeground(TEXT_LIGHT);
        loadCharts();
        add(tabbedPane, BorderLayout.CENTER);

        // Buttons
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBackground(PLUM);
        backButton.setForeground(TEXT_LIGHT);
        backButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        backButton.setPreferredSize(new Dimension(220, 50));
        backButton.addActionListener(e -> {
            new Dashboard(userId);
            dispose();
        });

        JButton refreshButton = new JButton("Refresh Charts");
        refreshButton.setBackground(TEAL);
        refreshButton.setForeground(TEXT_LIGHT);
        refreshButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        refreshButton.setPreferredSize(new Dimension(220, 50));
        refreshButton.addActionListener(e -> {
            tabbedPane.removeAll();
            loadCharts();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_DARK);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadCharts() {
        tabbedPane.addTab("Workout Progress", ChartUtils.createDynamicWorkoutProgressCharts(userId));
        tabbedPane.addTab("Body Weight", ChartUtils.createBodyWeightChart(userId));
        tabbedPane.addTab("Nutrition", ChartUtils.createNutritionChart(userId));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Progression(1)); // Example userId = 1
    }
}
