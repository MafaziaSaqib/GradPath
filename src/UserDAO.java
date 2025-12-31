import java.sql.*;
import java.security.MessageDigest;
public class UserDAO {
    private Connection con;
    private ActivityLogDAO logDAO;

    public UserDAO() {
        con = DBConnection.getConnection();
        logDAO = new ActivityLogDAO();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hashing error");
        }
    }

    private int insertUserRow(String username, String passwordHash, String role,
                              Integer linkedStudentId, Integer linkedCompanyId) throws SQLException {
        String sql = "INSERT INTO Users (Username, PasswordHash, Role, LinkedStudentID, LinkedCompanyID) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role);
            if (linkedStudentId == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, linkedStudentId);
            if (linkedCompanyId == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, linkedCompanyId);
            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("User insert failed");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                else throw new SQLException("No UserID returned");
            }
        }
    }
    public boolean registerStudentWithUser(Student s, String username, String plainPassword) {
        try {
            // Insert student
            String studentSql = "INSERT INTO Students (StudentName, Email, Phone, DeptID, CGPA, Batch) VALUES (?, ?, ?, ?, ?, ?)";
            int studentId;
            try (PreparedStatement ps = con.prepareStatement(studentSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, s.getStudentName());
                ps.setString(2, s.getEmail());
                ps.setString(3, s.getPhone());
                ps.setInt(4, s.getDeptId());
                ps.setDouble(5, s.getCgpa());
                ps.setString(6, s.getBatch());
                int affected = ps.executeUpdate();
                if (affected == 0) throw new SQLException("Student insert failed");
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) studentId = keys.getInt(1);
                    else throw new SQLException("No StudentID returned");
                }
            }
            // Insert user
            String hash = hashPassword(plainPassword);
            int userId = insertUserRow(username, hash, "Student", studentId, null);
            // Log registration
            logDAO.logAction(userId, "Student registered with user account");
            return true;
        } catch (SQLException e) {
            System.out.println("registerStudentWithUser failed: " + e.getMessage());
            return false;
        }
    }
    public boolean registerCompanyWithUser(Company c, String username, String plainPassword) {
        try {
            // Insert company
            String companySql = "INSERT INTO Companies (CompanyName, Industry, Location, HRContact) VALUES (?, ?, ?, ?)";
            int companyId;
            try (PreparedStatement ps = con.prepareStatement(companySql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, c.getCompanyName());
                ps.setString(2, c.getIndustry());
                ps.setString(3, c.getLocation());
                ps.setString(4, c.getHrContact());
                int affected = ps.executeUpdate();
                if (affected == 0) throw new SQLException("Company insert failed");
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) companyId = keys.getInt(1);
                    else throw new SQLException("No CompanyID returned");
                }
            }
            // Insert user
            String hash = hashPassword(plainPassword);
            int userId = insertUserRow(username, hash, "Company", null, companyId);
            // Log registration
            logDAO.logAction(userId, "Company registered with user account");
            return true;
        } catch (SQLException e) {
            System.out.println("registerCompanyWithUser failed: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String plainPassword) {
        String sql = "SELECT UserID, Username, PasswordHash, Role, LinkedStudentID, LinkedCompanyID FROM Users WHERE Username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String storedHash = rs.getString("PasswordHash");
                String givenHash = hashPassword(plainPassword);
                if (!storedHash.equals(givenHash)) return null;
                int userId = rs.getInt("UserID");
                String role = rs.getString("Role");
                Integer linkedStudentId = rs.getObject("LinkedStudentID") != null ? rs.getInt("LinkedStudentID") : null;
                Integer linkedCompanyId = rs.getObject("LinkedCompanyID") != null ? rs.getInt("LinkedCompanyID") : null;
                logDAO.logAction(userId, "User logged in");
                return new User(userId, username, storedHash, role, linkedStudentId, linkedCompanyId);
            }
        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
            return null;
        }
    }
    public Integer getUserIdByStudentId(int studentId) {
        String sql = "SELECT UserID FROM Users WHERE LinkedStudentID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("UserID");
            }
        } catch (SQLException e) {
            System.out.println("getUserIdByStudentId error: " + e.getMessage());
        }
        return null;
    }
}
