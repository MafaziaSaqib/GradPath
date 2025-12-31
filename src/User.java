public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String role;
    private Integer linkedStudentId;
    private Integer linkedCompanyId;

    public User(int userId, String username, String passwordHash, String role, Integer linkedStudentId, Integer linkedCompanyId) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.linkedStudentId = linkedStudentId;
        this.linkedCompanyId = linkedCompanyId;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public Integer getLinkedStudentId() { return linkedStudentId; }
    public Integer getLinkedCompanyId() { return linkedCompanyId; }
    public void setLinkedStudentId(Integer id) { this.linkedStudentId = id; }
    public void setLinkedCompanyId(Integer id) { this.linkedCompanyId = id; }
}
