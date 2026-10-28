
package campuscare.ui;

import campuscare.dao.UserDAO;
import campuscare.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    private final Color NAVY = new Color(23, 48, 83);
    private final Color ACCENT = CampusCareTheme.ACCENT;
    private final Color BACKGROUND = CampusCareTheme.BACKGROUND;
    private final Color SURFACE = CampusCareTheme.SURFACE;
    private final Color TEXT = CampusCareTheme.TEXT;
    private final Color MUTED = CampusCareTheme.MUTED_TEXT;
    private final Color BORDER = CampusCareTheme.BORDER;

    private final Font BODY_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    public LoginFrame() {
        setTitle("CampusCare | Login");
        setSize(900, 560);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(createBrandPanel());
        mainPanel.add(createLoginPanel());

        setContentPane(mainPanel);
    }



private JPanel createBrandPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(NAVY);

    JPanel brandContent = new JPanel();
    brandContent.setOpaque(false);
    brandContent.setLayout(
            new BoxLayout(brandContent, BoxLayout.Y_AXIS)
    );


ImageIcon originalIcon = new ImageIcon(
        "src/campuscare/ui/campus-logo.png"
);

Image scaledImage = originalIcon.getImage()
        .getScaledInstance(100, 90, Image.SCALE_SMOOTH);

JLabel icon = new JLabel(new ImageIcon(scaledImage));
icon.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel title = new JLabel("CampusCare");
    title.setFont(new Font("Segoe UI", Font.BOLD, 34));
    title.setForeground(Color.WHITE);
    title.setAlignmentX(Component.CENTER_ALIGNMENT);

    JPanel accentBar = new JPanel();
    accentBar.setBackground(ACCENT);
    accentBar.setPreferredSize(new Dimension(55, 4));
    accentBar.setMaximumSize(new Dimension(55, 4));
    accentBar.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel subtitle = new JLabel(
            "<html><div style='text-align:center;'>"
            + "FACILITY ISSUE REPORTING<br>"
            + "AND MAINTENANCE MANAGEMENT"
            + "</div></html>"
    );
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    subtitle.setForeground(new Color(220, 230, 245));
    subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

    brandContent.add(icon);
    brandContent.add(Box.createVerticalStrut(12));
    brandContent.add(title);
    brandContent.add(Box.createVerticalStrut(16));
    brandContent.add(accentBar);
    brandContent.add(Box.createVerticalStrut(20));
    brandContent.add(subtitle);

    panel.add(brandContent);

    return panel;
}


    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(45, 45, 35, 45));

        JLabel heading = new JLabel("Welcome Back!");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 29));
        heading.setForeground(TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel(
                "Sign in to your CampusCare account"
        );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(MUTED);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        styleField(usernameField);
        styleField(passwordField);

        JButton loginButton = new JButton("SIGN IN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(ACCENT);
        loginButton.setOpaque(true);
        loginButton.setContentAreaFilled(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setRolloverEnabled(true);
        loginButton.setBorder(
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        );
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 46)
        );
        loginButton.setPreferredSize(new Dimension(350, 46));
        loginButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        loginButton.getModel().addChangeListener(e -> {
            ButtonModel model = loginButton.getModel();

            if (model.isPressed()) {
                loginButton.setBackground(NAVY);
            } else if (model.isRollover()) {
                loginButton.setBackground(
                        ACCENT.brighter()
                );
            } else {
                loginButton.setBackground(ACCENT);
            }
        });

        loginButton.addActionListener(e -> login());

        JLabel footer = new JLabel(
                "CampusCare Facility Management System"
        );
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(MUTED);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(Box.createVerticalGlue());

        panel.add(heading);
        panel.add(Box.createVerticalStrut(8));
        panel.add(description);
        panel.add(Box.createVerticalStrut(34));

        addLabel(panel, "Username");
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(21));

        addLabel(panel, "Password");
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(28));

        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(24));
        panel.add(footer);

        panel.add(Box.createVerticalGlue());

        getRootPane().setDefaultButton(loginButton);

        return panel;
    }

    private void addLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(9));
    }

    private void styleField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setForeground(TEXT);
        field.setBackground(SURFACE);
        field.setCaretColor(ACCENT);
        field.setSelectionColor(ACCENT);
        field.setSelectedTextColor(Color.WHITE);
        field.setOpaque(true);

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44)
        );
        field.setPreferredSize(new Dimension(350, 44));
    }

    private void login() {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();

        if (username.isEmpty() || passwordChars.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your username and password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );

            Arrays.fill(passwordChars, '\0');
            return;
        }

        String password = new String(passwordChars);
        Arrays.fill(passwordChars, '\0');

        try {
            UserDAO dao = new UserDAO();
            User user = dao.authenticate(username, password);

            if (user == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );

                passwordField.setText("");
                passwordField.requestFocusInWindow();
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Login successful!\nRole: " + user.getRole(),
                    "Welcome to CampusCare",
                    JOptionPane.INFORMATION_MESSAGE
            );

            new DashboardFrame(user).setVisible(true);
            dispose();

        } catch (java.sql.SQLException
                | java.security.GeneralSecurityException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to authenticate with the database.\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();

        } finally {
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new LoginFrame().setVisible(true)
        );
    }
}

