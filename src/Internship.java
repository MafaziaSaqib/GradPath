public class Internship {
    private int internshipId;
    private int companyId;
    private String role;
    private String description;
    private double stipend;
    private String duration;

    public Internship(int internshipId, int companyId, String role, String description, double stipend, String duration) {
        this.internshipId = internshipId;
        this.companyId = companyId;
        this.role = role;
        this.description = description;
        this.stipend = stipend;
        this.duration = duration;
    }
    public Internship(int companyId, String role, String description, double stipend, String duration) {
        this(0, companyId, role, description, stipend, duration);
    }
    public int getInternshipId(){ return internshipId; }
    public int getCompanyId(){ return companyId; }
    public String getRole(){ return role; }
    public String getDescription(){ return description; }
    public double getStipend(){ return stipend; }
    public String getDuration(){ return duration; }
    public void setInternshipId(int id){ this.internshipId = id; }
}
