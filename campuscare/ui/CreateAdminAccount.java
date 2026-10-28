
package campuscare.ui;

import campuscare.config.DBConnection;
import campuscare.service.PasswordUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

public class CreateAdminAccount {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                CreateAdminAccount::createAccount);
    }

    private static void createAccount() {
        String name = JOptionPane.showInputDialog(
                null, "Administrator full name:");
        if (name == null) return;

        String username = JOptionPane.showInputDialog(
                null, "Choose an admin username:");
        if (username == null) return;

        String email = JOptionPane.showInputDialog(
                null, "Administrator email:");
        if (email == null) return;

        JPasswordField passwordField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(
                null, passwordField, "Create admin password",
                JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        char[] password = passwordField.getPassword();

        try {
            if (name.isBlank() || username.isBlank()
                    || email.isBlank() || password.length < 8) {
                JOptionPane.showMessageDialog(null,
                        "Complete all fields. Password must be at least 8 characters.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                try (PreparedStatement check = conn.prepareStatement(
                        "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'");
                     ResultSet rs = check.executeQuery()) {

                    rs.next();
                    if (rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null,
                                "An administrator already exists. Setup cancelled.");
                        return;
                    }
                }

                String sql = """
                    INSERT INTO users
                        (full_name, username, email, password_hash, role)
                    VALUES (?, ?, ?, ?, 'ADMIN')
                """;

                String hash = PasswordUtil.hashPassword(password);

                try (PreparedStatement stmt =
                             conn.prepareStatement(sql)) {
                    stmt.setString(1, name.trim());
                    stmt.setString(2, username.trim());
                    stmt.setString(3, email.trim());
                    stmt.setString(4, hash);
                    stmt.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(null,
                    "Administrator account created successfully!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Could not create account:\n" + ex.getMessage(),
                    "Setup Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
            passwordField.setText("");
        }
    }
}

