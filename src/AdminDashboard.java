import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {

    private User currentUser;
    private JTabbedPane tabs;

    public AdminDashboard(User user){
        this.currentUser = user;
        setTitle("GradPath — Admin");
        setSize(1400,900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        setVisible(true);
    }

    private void initUI(){

        JPanel root = new GradientPanel();
        root.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(),70));
        header.setBorder(UITheme.cardBorder());

        JLabel title = new JLabel("GradPath — Admin");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.ACCENT);
        title.setBorder(new EmptyBorder(8,16,8,8));
        header.add(title, BorderLayout.WEST);

        JButton logout = new JButton("Logout");
        UITheme.styleButton(logout);
        logout.addActionListener(e -> { dispose(); new LoginUI(); });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(new JLabel(currentUser.getUsername()+" | Admin"));
        right.add(logout);
        header.add(right, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(UITheme.NORMAL);
        tabs.setPreferredSize(new Dimension(230,0));

        tabs.add("Dashboard", buildDashboardPanel());
        tabs.add("Students", buildStudentsPanel());
        tabs.add("Companies", buildCompaniesPanel());
        tabs.add("Internships", buildInternshipsPanel());
        tabs.add("Applications", buildApplicationsPanel());
        tabs.add("Placements", buildPlacementsPanel());
        tabs.add("Activity Logs", buildLogsPanel());
        tabs.add("Notifications", buildNotificationsPanel());
        tabs.add("Profile", buildProfilePanel());

        root.add(tabs, BorderLayout.CENTER);
        add(root);
    }

    private JPanel buildDashboardPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel top = new JPanel(new GridLayout(1,3,16,16));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(20,20,20,20));

        top.add(card("Students",
                String.valueOf(new StudentDAO().getAllStudents().size())));
        top.add(card("Companies",
                String.valueOf(new CompanyDAO().getAllCompanies().size())));
        top.add(card("Internships",
                String.valueOf(new InternshipDAO().getAllInternships().size())));

        p.add(top, BorderLayout.NORTH);
        return p;
    }

    private JPanel card(String title,String val){
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Color.WHITE);
        c.setBorder(UITheme.cardBorder());

        JLabel t = new JLabel(title);
        t.setFont(UITheme.NORMAL);

        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI",Font.BOLD,28));
        v.setForeground(UITheme.ACCENT);

        c.add(t, BorderLayout.NORTH);
        c.add(v, BorderLayout.CENTER);
        return c;
    }

    private JPanel buildStudentsPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] cols = {"StudentID","Name","Email","Dept","CGPA","Batch","Action"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        loadStudentsModel(model);

        JButton refresh = new JButton("Refresh");
        UITheme.styleButton(refresh);
        refresh.addActionListener(e -> loadStudentsModel(model));

        JButton delete = new JButton("Delete Selected");
        UITheme.styleButton(delete);
        delete.addActionListener(e -> {
            int r = table.getSelectedRow(); if(r==-1) return;
            int id = (int) model.getValueAt(r,0);
            if(new StudentDAO().deleteStudent(id, currentUser.getUserId())){
                loadStudentsModel(model);
                JOptionPane.showMessageDialog(this,"Deleted student");
            } else JOptionPane.showMessageDialog(this,"Delete failed");
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(refresh); bottom.add(delete);

        p.add(bottom, BorderLayout.SOUTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void loadStudentsModel(DefaultTableModel model){
        model.setRowCount(0);
        ArrayList<Student> list = new StudentDAO().getAllStudents();
        for(Student s:list)
            model.addRow(new Object[]{
                    s.getStudentId(),
                    s.getStudentName(),
                    s.getEmail(),
                    s.getDeptId(),
                    s.getCgpa(),
                    s.getBatch(),
                    "Delete"
            });
    }

    private JPanel buildCompaniesPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] cols = {"CompanyID","Name","Industry","Location","HR","Action"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        loadCompaniesModel(model);

        JButton refresh = new JButton("Refresh");
        UITheme.styleButton(refresh);
        refresh.addActionListener(e -> loadCompaniesModel(model));

        JButton delete = new JButton("Delete Selected");
        UITheme.styleButton(delete);
        delete.addActionListener(e -> {
            int r = table.getSelectedRow(); if(r==-1) return;
            int id = (int) model.getValueAt(r,0);
            if(new CompanyDAO().deleteCompany(id, currentUser.getUserId())){
                loadCompaniesModel(model);
                JOptionPane.showMessageDialog(this,"Deleted company");
            } else JOptionPane.showMessageDialog(this,"Delete failed");
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(refresh); bottom.add(delete);

        p.add(bottom, BorderLayout.SOUTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void loadCompaniesModel(DefaultTableModel model){
        model.setRowCount(0);
        ArrayList<Company> list = new CompanyDAO().getAllCompanies();
        for(Company c:list)
            model.addRow(new Object[]{
                    c.getCompanyId(),
                    c.getCompanyName(),
                    c.getIndustry(),
                    c.getLocation(),
                    c.getHrContact(),
                    "Delete"
            });
    }

    private JPanel buildInternshipsPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] cols = {"InternshipID","Role","Company","Stipend","Duration","Action"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        loadInternshipsModel(model);

        JButton refresh = new JButton("Refresh");
        UITheme.styleButton(refresh);
        refresh.addActionListener(e -> loadInternshipsModel(model));

        JButton delete = new JButton("Delete Selected");
        UITheme.styleButton(delete);
        delete.addActionListener(e -> {
            int r = table.getSelectedRow(); if(r==-1) return;
            int id = (int) model.getValueAt(r,0);
            if(new InternshipDAO().deleteInternship(id, currentUser.getUserId())){
                loadInternshipsModel(model);
                JOptionPane.showMessageDialog(this,"Deleted internship");
            } else JOptionPane.showMessageDialog(this,"Delete failed");
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(refresh); bottom.add(delete);

        p.add(bottom, BorderLayout.SOUTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void loadInternshipsModel(DefaultTableModel model){
        model.setRowCount(0);
        InternshipDAO idao = new InternshipDAO();
        CompanyDAO cdao = new CompanyDAO();
        ArrayList<Internship> list = idao.getAllInternships();

        for(Internship it:list){
            Company comp = cdao.getCompanyById(it.getCompanyId());
            String cname = comp!=null ? comp.getCompanyName() : "Unknown";
            model.addRow(new Object[]{
                    it.getInternshipId(),
                    it.getRole(),
                    cname,
                    it.getStipend(),
                    it.getDuration(),
                    "Delete"
            });
        }
    }

    private JPanel buildApplicationsPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] cols = {"ApplicationID","Student","Internship","Status","Action"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        loadApplications(model);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if(col==4){
                    int appId = (int) model.getValueAt(row,0);
                    String[] options = {"Accept","Reject"};
                    int choice = JOptionPane.showOptionDialog(
                            AdminDashboard.this,
                            "Choose action for application "+appId,
                            "Application Action",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    String newStatus = null;
                    if(choice==0) newStatus="Accepted";
                    else if(choice==1) newStatus="Rejected";

                    if(newStatus != null){
                        try{
                            boolean updated =
                                    new ApplicationDAO()
                                            .updateStatus(appId,newStatus,currentUser.getUserId());
                            if(updated){
                                JOptionPane.showMessageDialog(
                                        AdminDashboard.this,
                                        "Application "+newStatus);
                                loadApplications(model);
                            } else JOptionPane.showMessageDialog(
                                    AdminDashboard.this,
                                    "Update failed");
                        } catch(Exception ex){ ex.printStackTrace(); }
                    }
                }
            }
        });

        JButton refresh = new JButton("Refresh");
        UITheme.styleButton(refresh);
        refresh.addActionListener(e -> loadApplications(model));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(refresh);

        p.add(bottom, BorderLayout.SOUTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void loadApplications(DefaultTableModel model){
        model.setRowCount(0);
        ArrayList<Application> list = new ApplicationDAO().getAllApplications();
        for(Application a:list)
            model.addRow(new Object[]{
                    a.getApplicationId(),
                    a.getStudentName(),
                    a.getInternshipRole(),
                    a.getStatus(),
                    "Action"
            });
    }

    private JPanel buildPlacementsPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] cols = {"PlacementID","StudentID","CompanyID","JobID","OfferDate","Package"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        ArrayList<Placement> list = new PlacementDAO().getPlacements();
        for(Placement pl:list)
            model.addRow(new Object[]{
                    pl.getPlacementId(),
                    pl.getStudentId(),
                    pl.getCompanyId(),
                    pl.getJobId(),
                    pl.getOfferDate(),
                    pl.getPackageAmt()
            });

        JButton refresh = new JButton("Refresh");
        UITheme.styleButton(refresh);
        refresh.addActionListener(e -> table.repaint());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(refresh);

        p.add(bottom, BorderLayout.SOUTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildLogsPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] cols = {"LogID","UserID","Action","Timestamp"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        loadLogsModel(model);

        JButton refresh = new JButton("Refresh");
        UITheme.styleButton(refresh);
        refresh.addActionListener(e -> loadLogsModel(model));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(refresh);

        p.add(bottom, BorderLayout.SOUTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void loadLogsModel(DefaultTableModel model){
        model.setRowCount(0);
        ArrayList<ActivityLog> logs = new ActivityLogDAO().getAllLogs();
        for(ActivityLog log : logs){
            model.addRow(new Object[]{
                    log.getLogId(),
                    log.getUserId(),
                    log.getAction(),
                    log.getTimestamp()
            });
        }
    }

    private JPanel buildNotificationsPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        DefaultListModel<String> lm = new DefaultListModel<>();
        try{
            new NotificationDAO()
                    .getUserNotifications(currentUser.getUserId())
                    .forEach(lm::addElement);
        } catch(Exception e){
            lm.addElement("Welcome Admin");
        }

        p.add(new JScrollPane(new JList<>(lm)), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildProfilePanel(){

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets=new Insets(8,8,8,8);
        gbc.anchor=GridBagConstraints.WEST;

        gbc.gridx=0; gbc.gridy=0;
        p.add(new JLabel("Username:"),gbc);
        gbc.gridx=1;
        p.add(new JLabel(currentUser.getUsername()),gbc);

        gbc.gridx=0; gbc.gridy++;
        p.add(new JLabel("Role:"),gbc);
        gbc.gridx=1;
        p.add(new JLabel(currentUser.getRole()),gbc);

        return p;
    }
}
