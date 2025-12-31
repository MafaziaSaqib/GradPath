import java.sql.Date;
public class Placement {
    private int placementId;
    private int studentId;
    private int companyId;
    private int jobId;
    private Date offerDate;
    private double packageAmt;
    public Placement(int placementId, int studentId, int companyId, int jobId, Date offerDate, double packageAmt) {
        this.placementId = placementId;
        this.studentId = studentId;
        this.companyId = companyId;
        this.jobId = jobId;
        this.offerDate = offerDate;
        this.packageAmt = packageAmt;
    }
    public Placement(int studentId, int companyId, int jobId, Date offerDate, double packageAmt) {
        this(0, studentId, companyId, jobId, offerDate, packageAmt);
    }
    public int getPlacementId(){ return placementId; }
    public int getStudentId(){ return studentId; }
    public int getCompanyId(){ return companyId; }
    public int getJobId(){ return jobId; }
    public Date getOfferDate(){ return offerDate; }
    public double getPackageAmt(){ return packageAmt; }
    public void setPlacementId(int id){ this.placementId = id; }
}
