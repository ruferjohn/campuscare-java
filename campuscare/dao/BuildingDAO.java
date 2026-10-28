
package campuscare.dao;

import campuscare.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuildingDAO {

    public List<Object[]> getAllBuildings() throws SQLException {
        String sql = """
            SELECT building_id, building_name, description
            FROM buildings
            ORDER BY building_id DESC
            """;

        List<Object[]> buildings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                buildings.add(new Object[] {
                    rs.getInt("building_id"),
                    rs.getString("building_name"),
                    rs.getString("description")
                });
            }
        }

        return buildings;
    }

    public boolean addBuilding(
            String name, String description) throws SQLException {

        String sql = """
            INSERT INTO buildings (building_name, description)
            VALUES (?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2,
                description.isBlank() ? null : description);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateBuilding(
            int id, String name, String description)
            throws SQLException {

        String sql = """
            UPDATE buildings
            SET building_name = ?, description = ?
            WHERE building_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2,
                description.isBlank() ? null : description);
            stmt.setInt(3, id);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteBuilding(int id) throws SQLException {
        String sql = "DELETE FROM buildings WHERE building_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
