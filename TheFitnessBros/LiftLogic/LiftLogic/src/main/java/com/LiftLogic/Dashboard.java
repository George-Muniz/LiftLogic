package com.LiftLogic;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class Dashboard extends JFrame {
    private static Color PLUM = new Color(142, 69, 133);
    private static Color TEAL = new Color(64, 224, 208);
    private static Color BG_DARK = new Color(46, 46, 46);
    private static Color CARD_BG_DARK = new Color(60, 60, 60, 230);
    private static Color TEXT_LIGHT = new Color(230, 230, 230);

    private int userId;

    public Dashboard(int userId) {
        this.userId = userId;

        setTitle("LiftLogic Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));

        JLabel titleLabel = new JLabel("LiftLogic", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        titleLabel.setForeground(PLUM);
        header.add(titleLabel, BorderLayout.NORTH);

        JLabel subtitle = new JLabel("Your Personal Fitness Companion", SwingConstants.CENTER);
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 18));
        subtitle.setForeground(TEXT_LIGHT);
        header.add(subtitle, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridLayout(2, 3, 30, 30));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(30, 60, 80, 60));

        card.add(createCardButton("🏋️ Workout Logging", userId));
        card.add(createCardButton("🍎 Diet Tracking", userId));
        card.add(createCardButton("📊 Progression", userId));
        card.add(createCardButton("👤 Profile", userId));
        card.add(createCardButton("🚪 Logout", userId));

        JPanel overlay = new JPanel(new BorderLayout());
        overlay.setBackground(CARD_BG_DARK);
        overlay.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        overlay.add(card, BorderLayout.CENTER);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(overlay, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createCardButton(String text, int userId) {
        RoundedButton btn = new RoundedButton(text, PLUM, TEAL);
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        btn.setPreferredSize(new Dimension(250, 90));
        btn.addActionListener(e -> {
            String action = text.replaceAll("[^A-Za-z ]", "").trim();
            handleAction(action, userId);
        });
        return btn;
    }

    private void handleAction(String action, int userId) {
        switch (action) {
            case "Workout Logging": new WorkoutEntryForm(userId); break;
            case "Diet Tracking": new DietEntryForm(userId); break;
            case "Progression": new Progression(userId); break;
            case "Profile": new ProfilePage(userId); break;
            case "Logout": new LoginPage(); break;
        }
        dispose();
    }

    private static class RoundedButton extends JButton {
        private final Color normal, hover;
        RoundedButton(String text, Color bg, Color hoverColor) {
            super(text);
            normal = bg;
            hover = hoverColor;
            setForeground(Color.WHITE);
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setUI(new BasicButtonUI());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? hover : normal);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard(1));
    }
}
