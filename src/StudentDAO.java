import java.sql.*;
import java.util.ArrayList;
public class StudentDAO {
    private Connection con;
    private ActivityLogDAO logDAO;
    public StudentDAO() {
        con = DBConnection.getConnection();
        logDAO = new ActivityLogDAO();
    }
    // CREATE
    public int addStudent(Student s, int performingUserId) {
        String sql = "INSERT INTO Students (StudentName, Email, Phone, DeptID, CGPA, Batch) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getStudentName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.setInt(4, s.getDeptId());
            ps.setDouble(5, s.getCgpa());
            ps.setString(6, s.getBatch());
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    s.setStudentId(id);
                    logDAO.logAction(performingUserId, "Added student: " + s.getStudentName());
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println("Add student failed: " + e.getMessage());
            return -1;
        }
    }
    // READ single
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM Students WHERE StudentID = ?";
        Student st = null;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    st = new Student(
                            rs.getInt("StudentID"),
                            rs.getString("StudentName"),
                            rs.getString("Email"),
                            rs.getString("Phone"),
                            rs.getInt("DeptID"),
                            rs.getDouble("CGPA"),
                            rs.getString("Batch")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Fetch student failed: " + e.getMessage());
        }
        return st;
    }
    // READ ALL
    public ArrayList<Student> getAllStudents() {
        ArrayList<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM Students";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Student st = new Student(
                        rs.getInt("StudentID"),
                        rs.getString("StudentName"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getInt("DeptID"),
                        rs.getDouble("CGPA"),
                        rs.getString("Batch")
                );
                list.add(st);
            }
        } catch (SQLException e) {
            System.out.println("Fetch all students failed: " + e.getMessage());
        }
        return list;
    }
    // UPDATE
    public boolean updateStudent(Student s, int performingUserId) {
        String sql = "UPDATE Students SET StudentName=?, Email=?, Phone=?, DeptID=?, CGPA=?, Batch=? WHERE StudentID=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getStudentName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.setInt(4, s.getDeptId());
            ps.setDouble(5, s.getCgpa());
            ps.setString(6, s.getBatch());
            ps.setInt(7, s.getStudentId());
            boolean success = ps.executeUpdate() > 0;
            if (success) logDAO.logAction(performingUserId, "Updated student: " + s.getStudentName());
            return success;
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }
    // DELETE
    public boolean deleteStudent(int id, int performingUserId) {
        Student st = getStudentById(id);
        String sql = "DELETE FROM Students WHERE StudentID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean success = ps.executeUpdate() > 0;
            if (success && st != null) logDAO.logAction(performingUserId, "Deleted student: " + st.getStudentName());
            return success;
        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
            return false;
        }
    }
}
