
package campuscare.dao;

import campuscare.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueCategoryDAO {

    public List<Object[]> getAllCategories() throws SQLException {
        List<Object[]> categories = new ArrayList<>();

        String sql = """
            SELECT category_id, category_name, description
            FROM issue_categories
            ORDER BY category_id
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(new Object[]{
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getString("description")
                });
            }
        }

        return categories;
    }

    public void addCategory(String name, String description)
            throws SQLException {

        String sql = """
            INSERT INTO issue_categories (category_name, description)
            VALUES (?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.executeUpdate();
        }
    }

    public void updateCategory(int id, String name, String description)
            throws SQLException {

        String sql = """
            UPDATE issue_categories
            SET category_name = ?, description = ?
            WHERE category_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void deleteCategory(int id) throws SQLException {
        String sql = """
            DELETE FROM issue_categories
            WHERE category_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

