
package campuscare.dao;

import campuscare.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Object[]> getAllRooms() throws SQLException {
        String sql = """
            SELECT r.room_id, r.room_name, b.building_name,
                   r.floor_number, r.description
            FROM rooms r
            JOIN buildings b ON r.building_id = b.building_id
            ORDER BY r.room_id DESC
            """;

        List<Object[]> rooms = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rooms.add(new Object[]{
                    rs.getInt("room_id"),
                    rs.getString("room_name"),
                    rs.getString("building_name"),
                    rs.getInt("floor_number"),
                    rs.getString("description")
                });
            }
        }

        return rooms;
    }

    public List<Object[]> getBuildings() throws SQLException {
        String sql = """
            SELECT building_id, building_name
            FROM buildings
            ORDER BY building_name
            """;

        List<Object[]> buildings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                buildings.add(new Object[]{
                    rs.getInt("building_id"),
                    rs.getString("building_name")
                });
            }
        }

        return buildings;
    }

    public boolean addRoom(
            int buildingId, String roomName,
            int floorNumber, String description) throws SQLException {

        String sql = """
            INSERT INTO rooms
                (building_id, room_name, floor_number, description)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, buildingId);
            stmt.setString(2, roomName);
            stmt.setInt(3, floorNumber);
            stmt.setString(4,
                    description.isBlank() ? null : description);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateRoom(
            int roomId, int buildingId, String roomName,
            int floorNumber, String description) throws SQLException {

        String sql = """
            UPDATE rooms
            SET building_id = ?, room_name = ?,
                floor_number = ?, description = ?
            WHERE room_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, buildingId);
            stmt.setString(2, roomName);
            stmt.setInt(3, floorNumber);
            stmt.setString(4,
                    description.isBlank() ? null : description);
            stmt.setInt(5, roomId);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteRoom(int roomId) throws SQLException {
        String sql = "DELETE FROM rooms WHERE room_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            return stmt.executeUpdate() > 0;
        }
    }
}
