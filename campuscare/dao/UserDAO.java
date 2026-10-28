
package campuscare.dao;

import campuscare.config.DBConnection;
import campuscare.model.*;
import campuscare.service.PasswordUtil;

import java.sql.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDAO {

    // EXISTING LOGIN AUTHENTICATION
    public User authenticate(String username, String password)
            throws SQLException, GeneralSecurityException {

        String sql = """
            SELECT user_id, full_name, username, email,
                   role, password_hash
            FROM users
            WHERE username = ?
            """;

        char[] passwordChars = password.toCharArray();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                String storedHash = rs.getString("password_hash");

                boolean passwordMatches =
                        PasswordUtil.verifyPassword(
                                passwordChars, storedHash);

                if (!passwordMatches) return null;

                int id = rs.getInt("user_id");
                String name = rs.getString("full_name");
                String email = rs.getString("email");
                String role = rs.getString("role");

                switch (role) {
                    case "ADMIN":
                        return new Admin(id, name, username, email);
                    case "MAINTENANCE":
                        return new MaintenanceStaff(
                                id, name, username, email);
                    case "REPORTER":
                        return new Reporter(id, name, username, email);
                    default:
                        return null;
                }
            }
        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    // GET ALL USERS — NEVER RETRIEVE PASSWORD HASHES FOR THE TABLE
    public List<Object[]> getAllUsers() throws SQLException {
        List<Object[]> users = new ArrayList<>();

        String sql = """
            SELECT user_id, full_name, username, email, role, created_at
            FROM users
            ORDER BY user_id
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("full_name"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at")
                });
            }
        }

        return users;
    }

    // CREATE USER
    public void addUser(String fullName, String username, String email,
                        char[] password, String role)
            throws SQLException, GeneralSecurityException {

        String sql = """
            INSERT INTO users
                (full_name, username, email, password_hash, role)
            VALUES (?, ?, ?, ?, ?)
            """;

        try {
            String passwordHash = PasswordUtil.hashPassword(password);

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, fullName);
                stmt.setString(2, username);
                stmt.setString(3, email);
                stmt.setString(4, passwordHash);
                stmt.setString(5, role);

                stmt.executeUpdate();
            }
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    // UPDATE USER DETAILS (DOES NOT CHANGE PASSWORD)
    public void updateUser(int userId, String fullName, String username,
                           String email, String role)
            throws SQLException {

        String sql = """
            UPDATE users
            SET full_name = ?, username = ?, email = ?, role = ?
            WHERE user_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setString(2, username);
            stmt.setString(3, email);
            stmt.setString(4, role);
            stmt.setInt(5, userId);

            stmt.executeUpdate();
        }
    }

    // RESET USER PASSWORD
    public void resetPassword(int userId, char[] newPassword)
            throws SQLException, GeneralSecurityException {

        String sql = """
            UPDATE users
            SET password_hash = ?
            WHERE user_id = ?
            """;

        try {
            String passwordHash =
                    PasswordUtil.hashPassword(newPassword);

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, passwordHash);
                stmt.setInt(2, userId);

                int updated = stmt.executeUpdate();

                if (updated == 0) {
                    throw new SQLException("User not found.");
                }
            }
        } finally {
            Arrays.fill(newPassword, '\0');
        }
    }

    // DELETE USER
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            int deleted = stmt.executeUpdate();

            if (deleted == 0) {
                throw new SQLException("User not found.");
            }
        }
    }
}
