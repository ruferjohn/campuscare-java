
package campuscare.dao;

import campuscare.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public int[] getReportCounts() throws SQLException {
        String sql = """
            SELECT
                COUNT(*) AS total,
                COALESCE(SUM(
                    CASE WHEN status IN ('SUBMITTED', 'ASSIGNED')
                    THEN 1 ELSE 0 END
                ), 0) AS pending,
                COALESCE(SUM(
                    CASE WHEN status = 'IN_PROGRESS'
                    THEN 1 ELSE 0 END
                ), 0) AS in_progress,
                COALESCE(SUM(
                    CASE WHEN status IN ('RESOLVED', 'CLOSED')
                    THEN 1 ELSE 0 END
                ), 0) AS resolved
            FROM reports
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new int[] {
                    rs.getInt("total"),
                    rs.getInt("pending"),
                    rs.getInt("in_progress"),
                    rs.getInt("resolved")
                };
            }
        }

        return new int[] {0, 0, 0, 0};
    }

    public List<Object[]> getRecentReports() throws SQLException {
        String sql = """
            SELECT
                r.report_id,
                r.title,
                c.category_name,
                CONCAT(b.building_name, ' - ', rm.room_name)
                    AS location,
                r.status
            FROM reports r
            JOIN rooms rm ON r.room_id = rm.room_id
            JOIN buildings b ON rm.building_id = b.building_id
            JOIN issue_categories c ON r.category_id = c.category_id
            ORDER BY r.created_at DESC, r.report_id DESC
            LIMIT 10
            """;

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
                    rs.getString("status")
                });
            }
        }

        return reports;
    }
}
