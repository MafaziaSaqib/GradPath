import java.sql.*;
import java.util.ArrayList;
public class PlacementDAO {
    Connection con;
    public PlacementDAO() { con = DBConnection.getConnection(); }
    public ArrayList<Placement> getPlacements() {
        ArrayList<Placement> list = new ArrayList<>();
        String sql = "SELECT * FROM Placements";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Placement p = new Placement(
                        rs.getInt("PlacementID"),
                        rs.getInt("StudentID"),
                        rs.getInt("CompanyID"),
                        rs.getInt("JobID"),
                        rs.getDate("OfferDate"),
                        rs.getDouble("Package")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Fetch placements failed: " + e.getMessage());
        }
        return list;
    }
}
