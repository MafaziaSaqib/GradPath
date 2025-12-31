import java.sql.*;
import java.util.ArrayList;
public class InternshipDAO {
    private Connection con;
    private ActivityLogDAO logDAO;
    public InternshipDAO() {
        con = DBConnection.getConnection();
        logDAO = new ActivityLogDAO();
    }
    // CREATE
    public int addInternship(Internship i, int performingUserId) {
        String sql = "INSERT INTO Internships (CompanyID, Role, Description, Stipend, Duration) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, i.getCompanyId());
            ps.setString(2, i.getRole());
            ps.setString(3, i.getDescription());
            ps.setDouble(4, i.getStipend());
            ps.setString(5, i.getDuration());
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    i.setInternshipId(id);
                    logDAO.logAction(performingUserId, "Added internship: " + i.getRole() + " at company ID " + i.getCompanyId());
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println("Add internship failed: " + e.getMessage());
            return -1;
        }
    }
    // READ ALL
    public ArrayList<Internship> getAllInternships() {
        ArrayList<Internship> list = new ArrayList<>();
        String sql = "SELECT * FROM Internships";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Internship i = new Internship(
                        rs.getInt("InternshipID"),
                        rs.getInt("CompanyID"),
                        rs.getString("Role"),
                        rs.getString("Description"),
                        rs.getDouble("Stipend"),
                        rs.getString("Duration")
                );
                list.add(i);
            }
        } catch (SQLException e) {
            System.out.println("Fetch internships failed: " + e.getMessage());
        }
        return list;
    }
    // UPDATE
    public boolean updateInternship(Internship i, int performingUserId) {
        String sql = "UPDATE Internships SET Role=?, Description=?, Stipend=?, Duration=? WHERE InternshipID=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, i.getRole());
            ps.setString(2, i.getDescription());
            ps.setDouble(3, i.getStipend());
            ps.setString(4, i.getDuration());
            ps.setInt(5, i.getInternshipId());
            boolean success = ps.executeUpdate() > 0;
            if (success) logDAO.logAction(performingUserId, "Updated internship: " + i.getRole() + " (ID " + i.getInternshipId() + ")");
            return success;
        } catch (SQLException e) {
            System.out.println("Update internship failed: " + e.getMessage());
            return false;
        }
    }
    // DELETE
    public boolean deleteInternship(int id, int performingUserId) {
        Internship i = null;
        for (Internship it : getAllInternships()) {
            if (it.getInternshipId() == id) { i = it; break; }
        }
        String sql = "DELETE FROM Internships WHERE InternshipID=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean success = ps.executeUpdate() > 0;
            if (success && i != null) logDAO.logAction(performingUserId, "Deleted internship: " + i.getRole() + " (ID " + id + ")");
            return success;
        } catch (SQLException e) {
            System.out.println("Delete internship failed: " + e.getMessage());
            return false;
        }
    }
}
