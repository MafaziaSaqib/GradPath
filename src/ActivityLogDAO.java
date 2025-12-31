import java.sql.*;
import java.util.ArrayList;
public class ActivityLogDAO {
    Connection con;
    public ActivityLogDAO() { con = DBConnection.getConnection(); }
    public int logAction(int userId, String action) {
        String sql = "INSERT INTO ActivityLogs (UserID, Action) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            System.out.println("Log insert failed: " + e.getMessage());
            return -1;
        }
    }
    public ArrayList<ActivityLog> getAllLogs() {
        ArrayList<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT LogID, UserID, Action, Timestamp FROM ActivityLogs ORDER BY Timestamp DESC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ActivityLog log = new ActivityLog(
                        rs.getInt("LogID"),
                        rs.getInt("UserID"),
                        rs.getString("Action"),
                        rs.getTimestamp("Timestamp")
                );
                list.add(log);
            }
        } catch (SQLException e) {
            System.out.println("Fetching logs failed: " + e.getMessage());
        }
        return list;
    }
}
