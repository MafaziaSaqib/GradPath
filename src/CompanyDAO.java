import java.sql.*;
import java.util.ArrayList;
public class CompanyDAO {
    private Connection con;
    private ActivityLogDAO logDAO;
    public CompanyDAO() {
        con = DBConnection.getConnection();
        logDAO = new ActivityLogDAO();
    }
    // CREATE
    public int addCompany(Company c, int performingUserId) {
        String sql = "INSERT INTO Companies (CompanyName, Industry, Location, HRContact) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCompanyName());
            ps.setString(2, c.getIndustry());
            ps.setString(3, c.getLocation());
            ps.setString(4, c.getHrContact());
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    c.setCompanyId(id);
                    logDAO.logAction(performingUserId, "Added company: " + c.getCompanyName());
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println("Add company failed: " + e.getMessage());
            return -1;
        }
    }
    // GET by id
    public Company getCompanyById(int id) {
        String sql = "SELECT * FROM Companies WHERE CompanyID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Company(
                            rs.getInt("CompanyID"),
                            rs.getString("CompanyName"),
                            rs.getString("Industry"),
                            rs.getString("Location"),
                            rs.getString("HRContact")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Fetch company failed: " + e.getMessage());
        }
        return null;
    }
    // GET all
    public ArrayList<Company> getAllCompanies() {
        ArrayList<Company> list = new ArrayList<>();
        String sql = "SELECT * FROM Companies";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Company c = new Company(
                        rs.getInt("CompanyID"),
                        rs.getString("CompanyName"),
                        rs.getString("Industry"),
                        rs.getString("Location"),
                        rs.getString("HRContact")
                );
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Fetch all companies failed: " + e.getMessage());
        }
        return list;
    }
    // UPDATE
    public boolean updateCompany(Company c, int performingUserId) {
        String sql = "UPDATE Companies SET CompanyName=?, Industry=?, Location=?, HRContact=? WHERE CompanyID=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCompanyName());
            ps.setString(2, c.getIndustry());
            ps.setString(3, c.getLocation());
            ps.setString(4, c.getHrContact());
            ps.setInt(5, c.getCompanyId());
            boolean success = ps.executeUpdate() > 0;
            if (success) logDAO.logAction(performingUserId, "Updated company: " + c.getCompanyName());
            return success;
        } catch (SQLException e) {
            System.out.println("Update company failed: " + e.getMessage());
            return false;
        }
    }
    // DELETE
    public boolean deleteCompany(int id, int performingUserId) {
        Company c = getCompanyById(id);
        String sql = "DELETE FROM Companies WHERE CompanyID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean success = ps.executeUpdate() > 0;
            if (success && c != null) logDAO.logAction(performingUserId, "Deleted company: " + c.getCompanyName());
            return success;
        } catch (SQLException e) {
            System.out.println("Delete company failed: " + e.getMessage());
            return false;
        }
    }
}
