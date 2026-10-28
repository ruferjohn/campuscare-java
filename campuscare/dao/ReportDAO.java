
package campuscare.dao;

import campuscare.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import campuscare.model.User;

public class ReportDAO {

    public List<Object[]> getAllReports() throws SQLException {
        String sql =
            "SELECT r.report_id, r.title, c.category_name, " +
            "CONCAT(b.building_name, ' - ', rm.room_name) AS location, " +
            "r.priority, r.status, " +
            "DATE_FORMAT(r.created_at, '%Y-%m-%d %H:%i') AS created " +
            "FROM reports r " +
            "JOIN rooms rm ON r.room_id = rm.room_id " +
            "JOIN buildings b ON rm.building_id = b.building_id " +
            "JOIN issue_categories c ON r.category_id = c.category_id " +
            "ORDER BY r.created_at DESC, r.report_id DESC";

        List<Object[]> reports = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reports.add(new Object[] {
                    rs.getInt("report_id"),
                    rs.getString("title"),
                    rs.getString("category_name"),
                    rs.getString("location"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getString("created")
                });
            }
        }

        return reports;
    }

public boolean updateStatus(
        int reportId, String status, User currentUser)
        throws SQLException {

    if (currentUser == null) {
        throw new SecurityException("Login required.");
    }

    String role = currentUser.getRole();

    if (!"ADMIN".equalsIgnoreCase(role)
            && !"MAINTENANCE".equalsIgnoreCase(role)) {
        throw new SecurityException(
            "You are not allowed to update report statuses."
        );
    }

    if (reportId <= 0 || status == null
            || !List.of(
                "SUBMITTED", "ASSIGNED", "IN_PROGRESS",
                "RESOLVED", "CLOSED"
            ).contains(status)) {
        throw new IllegalArgumentException(
            "Invalid report ID or status."
        );
    }

    String sql =
        "UPDATE reports SET status = ?, " +
        "resolved_at = CASE " +
        "WHEN ? IN ('RESOLVED', 'CLOSED') " +
        "THEN COALESCE(resolved_at, NOW()) " +
        "WHEN ? IN ('SUBMITTED', 'ASSIGNED', 'IN_PROGRESS') " +
        "THEN NULL ELSE resolved_at END " +
        "WHERE report_id = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, status);
        stmt.setString(2, status);
        stmt.setString(3, status);
        stmt.setInt(4, reportId);

        return stmt.executeUpdate() > 0;
    }
}


public boolean deleteReport(
        int reportId, User currentUser)
        throws SQLException {

    if (currentUser == null) {
        throw new SecurityException("Login required.");
    }

    String role = currentUser.getRole();
    int userId = currentUser.getUserId();

    boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
    boolean isReporter = "REPORTER".equalsIgnoreCase(role);

    if (!isAdmin && !isReporter) {
        throw new SecurityException(
            "You are not allowed to delete reports."
        );
    }

    if (reportId <= 0) {
        throw new IllegalArgumentException("Invalid report ID.");
    }

    String deleteUpdates =
        "DELETE FROM report_updates WHERE report_id = ? " +
        "AND EXISTS (SELECT 1 FROM reports " +
        "WHERE report_id = ? " +
        "AND (? = TRUE OR reporter_id = ?))";

    String deleteReport =
        "DELETE FROM reports WHERE report_id = ? " +
        "AND (? = TRUE OR reporter_id = ?)";

    try (Connection conn = DBConnection.getConnection()) {
        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            try (PreparedStatement stmt =
                     conn.prepareStatement(deleteUpdates)) {
                stmt.setInt(1, reportId);
                stmt.setInt(2, reportId);
                stmt.setBoolean(3, isAdmin);
                stmt.setInt(4, userId);
                stmt.executeUpdate();
            }

            int deleted;

            try (PreparedStatement stmt =
                     conn.prepareStatement(deleteReport)) {
                stmt.setInt(1, reportId);
                stmt.setBoolean(2, isAdmin);
                stmt.setInt(3, userId);
                deleted = stmt.executeUpdate();
            }

            if (deleted == 0) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException | RuntimeException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }
}

    
public List<Object[]> getBuildings() throws SQLException {
    String sql =
        "SELECT building_id, building_name " +
        "FROM buildings ORDER BY building_name";

    List<Object[]> items = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            items.add(new Object[] {
                rs.getInt("building_id"),
                rs.getString("building_name")
            });
        }
    }

    return items;
}

public List<Object[]> getRoomsByBuilding(int buildingId)
        throws SQLException {

    String sql =
        "SELECT room_id, room_name FROM rooms " +
        "WHERE building_id = ? ORDER BY room_name";

    List<Object[]> items = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, buildingId);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(new Object[] {
                    rs.getInt("room_id"),
                    rs.getString("room_name")
                });
            }
        }
    }

    return items;
}

public List<Object[]> getCategories() throws SQLException {
    String sql =
        "SELECT category_id, category_name " +
        "FROM issue_categories ORDER BY category_name";

    List<Object[]> items = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            items.add(new Object[] {
                rs.getInt("category_id"),
                rs.getString("category_name")
            });
        }
    }

    return items;
}

public boolean createReport(
        int reporterId,
        int roomId,
        int categoryId,
        String title,
        String description,
        String priority) throws SQLException {

    String sql =
        "INSERT INTO reports " +
        "(reporter_id, room_id, category_id, title, " +
        "description, priority, status) " +
        "VALUES (?, ?, ?, ?, ?, ?, 'SUBMITTED')";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, reporterId);
        stmt.setInt(2, roomId);
        stmt.setInt(3, categoryId);
        stmt.setString(4, title);
        stmt.setString(5, description);
        stmt.setString(6, priority);

        return stmt.executeUpdate() == 1;
    }
}

public List<Object[]> getReportsByReporterId(int reporterId)
        throws SQLException {

    String sql =
        "SELECT r.report_id, r.title, c.category_name, " +
        "CONCAT(b.building_name, ' - ', rm.room_name) AS location, " +
        "r.priority, r.status, " +
        "DATE_FORMAT(r.created_at, '%Y-%m-%d %H:%i') AS created " +
        "FROM reports r " +
        "JOIN rooms rm ON r.room_id = rm.room_id " +
        "JOIN buildings b ON rm.building_id = b.building_id " +
        "JOIN issue_categories c ON r.category_id = c.category_id " +
        "WHERE r.reporter_id = ? " +
        "ORDER BY r.created_at DESC";

    List<Object[]> reports = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, reporterId);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reports.add(new Object[] {
                    rs.getInt("report_id"),
                    rs.getString("title"),
                    rs.getString("category_name"),
                    rs.getString("location"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getString("created")
                });
            }
        }
    }

    return reports;
}
public boolean deleteOwnReport(int reportId, int reporterId)
        throws SQLException {

    String sql = """
        DELETE FROM reports
        WHERE report_id = ?
          AND reporter_id = ?
        """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, reportId);
        stmt.setInt(2, reporterId);

        return stmt.executeUpdate() > 0;
    }
}
}
