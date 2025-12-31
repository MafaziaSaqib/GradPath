import java.sql.*;
import java.util.ArrayList;
public class ApplicationDAO {
    private Connection conn;
    private ActivityLogDAO logDAO;
    public ApplicationDAO() {
        conn = DBConnection.getConnection();
        logDAO = new ActivityLogDAO();
    }
    //CREATE APPLICATION
    public int addApplication(Application a, int performingUserId) {
        String sql = "INSERT INTO Applications (StudentID, InternshipID, Status, ApplyDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getStudentId());
            ps.setInt(2, a.getInternshipId());
            ps.setString(3, a.getStatus());
            ps.setDate(4, a.getApplyDate());
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    a.setApplicationId(id);
                    logDAO.logAction(performingUserId, "Added application ID " + id + " for student ID " + a.getStudentId());
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println("Add application failed: " + e.getMessage());
            return -1;
        }
    }
    //UPDATE
    public boolean updateStatus(int applicationId, String newStatus, int performingUserId) {
        String sql = "UPDATE Applications SET Status=? WHERE ApplicationID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, applicationId);
            boolean success = ps.executeUpdate() > 0;
            if (success)
                logDAO.logAction(performingUserId, "Updated application ID " + applicationId + " status to " + newStatus);
            return success;
        } catch (SQLException e) {
            System.out.println("Update application failed: " + e.getMessage());
            return false;
        }
    }
    //GET ALL APPLICATIONS
    public ArrayList<Application> getAllApplications() {
        ArrayList<Application> list = new ArrayList<>();
        String sql = "SELECT a.ApplicationID, a.StudentID, s.StudentName, a.InternshipID, i.Role, a.Status " +
                "FROM Applications a " +
                "JOIN Students s ON a.StudentID = s.StudentID " +
                "JOIN Internships i ON a.InternshipID = i.InternshipID";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Application app = new Application();
                app.setApplicationId(rs.getInt("ApplicationID"));
                app.setStudentId(rs.getInt("StudentID"));
                app.setStudentName(rs.getString("StudentName"));
                app.setInternshipId(rs.getInt("InternshipID"));
                app.setInternshipRole(rs.getString("Role"));
                app.setStatus(rs.getString("Status"));
                list.add(app);
            }
        } catch (SQLException e) {
            System.out.println("Fetch applications failed: " + e.getMessage());
        }
        return list;
    }
    //GET APPLICATIONS FOR STUDENT
    public ArrayList<Application> getApplicationsForStudent(int studentId) {
        ArrayList<Application> list = new ArrayList<>();
        String sql = "SELECT a.ApplicationID, s.StudentName, i.Role, a.Status, a.InternshipID " +
                "FROM Applications a " +
                "JOIN Students s ON a.StudentID = s.StudentID " +
                "JOIN Internships i ON a.InternshipID = i.InternshipID " +
                "WHERE a.StudentID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Application a = new Application();
                    a.setApplicationId(rs.getInt("ApplicationID"));
                    a.setStudentId(studentId);
                    a.setStudentName(rs.getString("StudentName"));
                    a.setInternshipId(rs.getInt("InternshipID"));
                    a.setInternshipRole(rs.getString("Role"));
                    a.setStatus(rs.getString("Status"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            System.out.println("Fetch student applications failed: " + e.getMessage());
        }
        return list;
    }
    //GET APPLICATIONS FOR COMPANY
    public ArrayList<Application> getApplicationsForCompany(int companyId) {
        ArrayList<Application> list = new ArrayList<>();
        String sql = "SELECT a.ApplicationID, s.StudentName, i.Role, a.Status, a.StudentID " +
                "FROM Applications a " +
                "JOIN Students s ON a.StudentID = s.StudentID " +
                "JOIN Internships i ON a.InternshipID = i.InternshipID " +
                "WHERE i.CompanyID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Application a = new Application();
                    a.setApplicationId(rs.getInt("ApplicationID"));
                    a.setStudentId(rs.getInt("StudentID"));
                    a.setStudentName(rs.getString("StudentName"));
                    a.setInternshipRole(rs.getString("Role"));
                    a.setStatus(rs.getString("Status"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            System.out.println("Fetch company applications failed: " + e.getMessage());
        }
        return list;
    }
    //GET STUDENT ID BY APPLICATION ID
    public int getStudentId(int applicationId) {
        String sql = "SELECT StudentID FROM Applications WHERE ApplicationID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("StudentID");
            }
        } catch (SQLException e) {
            System.out.println("Get student ID failed: " + e.getMessage());
        }
        return -1;
    }
}
