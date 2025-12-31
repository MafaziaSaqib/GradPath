import java.sql.*;
import java.util.ArrayList;
public class NotificationDAO {
    private Connection conn;
    public NotificationDAO() throws SQLException {
        conn = DBConnection.getConnection();
    }
    // Check if user exists
    public boolean userExists(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE UserID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
            return false;
        }
    }
    public boolean sendNotification(int userId, String message) {
        try {
            if (!userExists(userId)) {
                System.out.println("Cannot send notification: UserID " + userId + " does not exist!");
                return false; // prevent foreign key error
            }
            String sql = "INSERT INTO notifications(UserID, Message, CreatedAt) VALUES (?, ?, NOW())";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setString(2, message);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Get notifications for a user
    public ArrayList<String> getUserNotifications(int userId) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String sql = "SELECT Message FROM notifications WHERE UserID=? ORDER BY CreatedAt DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) list.add(rs.getString("Message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
