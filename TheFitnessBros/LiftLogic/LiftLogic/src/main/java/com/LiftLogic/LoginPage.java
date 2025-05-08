package com.LiftLogic;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginPage extends JFrame {
    private static final Color PLUM = new Color(142, 69, 133);
    private static  Color TEAL = new Color(64, 224, 208);
    private static  Color BG_DARK = new Color(46, 46, 46);
    private static  Color PANEL_BG = new Color(60, 60, 60, 230);
    private static  Color TEXT_LIGHT = new Color(220, 220, 220);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private RoundedButton loginButton;
    private RoundedButton createAccountButton;

    public LoginPage() {
        // Frame settings
        setTitle("LiftLogic Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        // Username field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20)); userLabel.setForeground(TEXT_LIGHT);
        form.add(userLabel, gbc);
        usernameField = new JTextField();
        usernameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        usernameField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20)); passLabel.setForeground(TEXT_LIGHT);
        form.add(passLabel, gbc);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        passwordField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 0;
        gbc.insets = new Insets(20, 20, 60, 20);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        btnPanel.setOpaque(false);
        loginButton = new RoundedButton("Login", PLUM, TEAL);
        loginButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        loginButton.setPreferredSize(new Dimension(300, 90));
        btnPanel.add(loginButton);
        form.add(btnPanel, gbc);

        // Create Account button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 0;
        JPanel createAccountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        createAccountPanel.setOpaque(false);
        createAccountButton = new RoundedButton("Create Account", PLUM, TEAL);
        createAccountButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        createAccountButton.setPreferredSize(new Dimension(300, 90));
        createAccountPanel.add(createAccountButton);
        form.add(createAccountPanel, gbc);

        add(form, BorderLayout.CENTER);

        // Add the ActionListener to the loginButton
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin(); // Call onLogin method when button is clicked
            }
        });

        // Add the ActionListener to the createAccountButton
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreateAccount(); // Call onCreateAccount method when button is clicked
            }
        });

        // Pack to fit
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        int userId = validateLogin(username, password); // Get userId if login is successful
        if (userId != -1) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            new Dashboard(userId); // Pass the userId to the Dashboard
            dispose(); // Close LoginPage
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }

    private void onCreateAccount() {
        new CreateAccountPage(); // Open the Create Account page
        dispose(); // Close LoginPage
    }

    private int validateLogin(String username, String password) {
        String sql = "SELECT user_id, password_hash FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPasswordHash = rs.getString("password_hash");
                if (BCrypt.checkpw(password, storedPasswordHash)) {
                    return rs.getInt("user_id"); // Return the userId if authentication is successful
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if login fails
    }

    // Custom rounded button
    private static class RoundedButton extends JButton {
        private final Color normal, hover;
        RoundedButton(String text, Color bg, Color hoverColor) {
            super(text); normal = bg; hover = hoverColor;
            setForeground(Color.WHITE);
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setUI(new BasicButtonUI());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { repaint(); }
                public void mouseExited(MouseEvent e) { repaint(); }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? hover : normal);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g2);
            g2.dispose();
        }

        public boolean contains(int x, int y) {
            return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30).contains(x, y);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(LoginPage::new);
    }
}
