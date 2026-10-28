
package campuscare.ui;

import campuscare.config.DBConnection;
import campuscare.service.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;

public class TestPassword {
    public static void main(String[] args) {
        JPasswordField field = new JPasswordField();

        int result = JOptionPane.showConfirmDialog(
                null, field, "Test RJ admin password",
                JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        char[] password = field.getPassword();
        field.setText("");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT password_hash FROM users WHERE username = ?")) {

            stmt.setString(1, "RJ");

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(
                            null, "Username RJ was not found.");
                    return;
                }

                boolean valid = PasswordUtil.verifyPassword(
                        password, rs.getString("password_hash"));

                JOptionPane.showMessageDialog(
                        null,
                        valid
                                ? "PASSWORD MATCHES the saved hash."
                                : "PASSWORD DOES NOT MATCH the saved hash.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null, "Test error: " + ex.getMessage());
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
