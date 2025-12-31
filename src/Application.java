import java.sql.Date;
public class Application {
    private int applicationId;
    private int studentId;
    private int internshipId;
    private String studentName;
    private String internshipRole;
    private String companyName;
    private String status;
    private Date applyDate;
    // Getters and setters
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getInternshipId() { return internshipId; }
    public void setInternshipId(int internshipId) { this.internshipId = internshipId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getInternshipRole() { return internshipRole; }
    public void setInternshipRole(String internshipRole) { this.internshipRole = internshipRole; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getApplyDate() { return applyDate; }
    public void setApplyDate(Date applyDate) { this.applyDate = applyDate; }
}