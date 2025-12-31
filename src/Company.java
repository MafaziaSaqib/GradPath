public class Company {
    private int companyId;
    private String companyName;
    private String industry;
    private String location;
    private String hrContact;

    public Company(int companyId, String companyName, String industry, String location, String hrContact) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.industry = industry;
        this.location = location;
        this.hrContact = hrContact;
    }

    public Company(String companyName, String industry, String location, String hrContact) {
        this(0, companyName, industry, location, hrContact);
    }

    public int getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public String getIndustry() { return industry; }
    public String getLocation() { return location; }
    public String getHrContact() { return hrContact; }
    public void setCompanyId(int id) { this.companyId = id; }
}
