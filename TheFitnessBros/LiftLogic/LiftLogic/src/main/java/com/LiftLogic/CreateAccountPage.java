package com.LiftLogic;

import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CreateAccountPage extends JFrame {
    private static  Color PLUM = new Color(142, 69, 133);
    private static  Color TEAL = new Color(64, 224, 208);
    private static  Color BG_DARK = new Color(46, 46, 46);
    private static  Color PANEL_BG = new Color(60, 60, 60, 230);
    private static  Color TEXT_LIGHT = new Color(230, 230, 230);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private RoundedButton submitButton;

    public CreateAccountPage() {
        // Frame settings
        setTitle("LiftLogic Create Account");
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
        JLabel userLabel = new JLabel("Create Username:");
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20)); userLabel.setForeground(TEXT_LIGHT);
        form.add(userLabel, gbc);
        usernameField = new JTextField();
        usernameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        usernameField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel passLabel = new JLabel("Create Password:");
        passLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20)); passLabel.setForeground(TEXT_LIGHT);
        form.add(passLabel, gbc);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        passwordField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(passwordField, gbc);

        // Full Name field
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20)); nameLabel.setForeground(TEXT_LIGHT);
        form.add(nameLabel, gbc);
        fullNameField = new JTextField();
        fullNameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        fullNameField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(fullNameField, gbc);

        // Email field
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20)); emailLabel.setForeground(TEXT_LIGHT);
        form.add(emailLabel, gbc);
        emailField = new JTextField();
        emailField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        emailField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(emailField, gbc);

        // Submit button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 0;
        gbc.insets = new Insets(20, 20, 60, 20);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        btnPanel.setOpaque(false);
        submitButton = new RoundedButton("Create Account", PLUM, TEAL);
        submitButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        submitButton.setPreferredSize(new Dimension(300, 90));
        btnPanel.add(submitButton);
        form.add(btnPanel, gbc);

        add(form, BorderLayout.CENTER);

        // Add the ActionListener to the submitButton
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSubmit(); // Call onSubmit method when button is clicked
            }
        });

        // Pack to fit
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onSubmit() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        if (createAccount(username, password, fullName, email)) {
            JOptionPane.showMessageDialog(this, "Account created successfully!");
            new LoginPage(); // Redirect to LoginPage
            dispose(); // Close CreateAccountPage
        } else {
            JOptionPane.showMessageDialog(this, "Error creating account.");
        }
    }

    private boolean createAccount(String username, String password, String fullName, String email) {
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO Users (username, password_hash, full_name, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            stmt.setString(3, fullName);
            stmt.setString(4, email);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        SwingUtilities.invokeLater(CreateAccountPage::new);
    }
}
