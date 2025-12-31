public class Student {
    private int studentId; private String studentName;
    private String email; private String phone;
    private int deptId; private double cgpa;
    private String batch;
    public Student(int studentId, String studentName, String email, String phone, int deptId, double cgpa, String batch) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.phone = phone;
        this.deptId = deptId;
        this.cgpa = cgpa;
        this.batch = batch;
    }
    public Student(String studentName, String email, String phone, int deptId, double cgpa, String batch) {
        this(0, studentName, email, phone, deptId, cgpa, batch);
    }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public int getDeptId() { return deptId; }
    public double getCgpa() { return cgpa; }
    public String getBatch() { return batch; }
    public void setStudentId(int id) { this.studentId = id; }
}
