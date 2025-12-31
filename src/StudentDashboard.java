import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentDashboard extends JFrame {

    private User currentUser;
    private JTabbedPane tabs;
    private JLabel availableInternshipsLabel, myApplicationsLabel, placementsLabel;
    private DefaultTableModel myApplicationsTableModel;

    public StudentDashboard(User user) {
        this.currentUser = user;
        setTitle("GradPath — Student");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        setVisible(true);
    }

    private void initUI() {

        JPanel root = new GradientPanel();
        root.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 70));
        header.setBorder(UITheme.cardBorder());

        JLabel title = new JLabel("GradPath — Student");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.ACCENT);
        title.setBorder(new EmptyBorder(8,16,8,8));
        header.add(title, BorderLayout.WEST);

        String rightLabelText = currentUser.getUsername() + " | Student";
        Integer linkedStudentId = currentUser.getLinkedStudentId();
        if (linkedStudentId != null) {
            Student s = new StudentDAO().getStudentById(linkedStudentId);
            if (s != null && s.getStudentName() != null && !s.getStudentName().isEmpty()) {
                rightLabelText = s.getStudentName() + " | Student";
            }
        }

        JButton logout = new JButton("Logout");
        UITheme.styleButton(logout);
        logout.addActionListener(e -> { dispose(); new LoginUI(); });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(new JLabel(rightLabelText));
        right.add(logout);
        header.add(right, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(UITheme.NORMAL);
        tabs.setPreferredSize(new Dimension(230, 0));

        tabs.add("Dashboard", buildDashboard());
        tabs.add("Internships", buildInternshipsPanel());
        tabs.add("My Applications", buildMyApplicationsPanel());
        tabs.add("Notifications", buildNotificationsPanel());
        tabs.add("Profile", buildProfilePanel());

        root.add(tabs, BorderLayout.CENTER);
        add(root);
    }

    private JPanel buildDashboard() {

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel top = new JPanel(new GridLayout(1,3,16,16));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(20,20,20,20));

        availableInternshipsLabel =
                new JLabel(String.valueOf(new InternshipDAO().getAllInternships().size()));

        int myAppsCount = 0;
        if(currentUser.getLinkedStudentId() != null) {
            myAppsCount = new ApplicationDAO()
                    .getApplicationsForStudent(currentUser.getLinkedStudentId()).size();
        }
        myApplicationsLabel = new JLabel(String.valueOf(myAppsCount));
        placementsLabel = new JLabel(String.valueOf(new PlacementDAO().getPlacements().size()));

        top.add(card("Available Internships", availableInternshipsLabel));
        top.add(card("My Applications", myApplicationsLabel));
        top.add(card("Placements", placementsLabel));

        p.add(top, BorderLayout.NORTH);
        return p;
    }

    private JPanel card(String title, JLabel valueLabel) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Color.WHITE);
        c.setBorder(UITheme.cardBorder());

        JLabel t = new JLabel(title);
        t.setFont(UITheme.NORMAL);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(UITheme.ACCENT);

        c.add(t, BorderLayout.NORTH);
        c.add(valueLabel, BorderLayout.CENTER);
        return c;
    }

    private JPanel buildInternshipsPanel() {

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] cols = {"InternshipID","Role","Company","Stipend","Duration"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        loadInternships(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton applyBtn = new JButton("Apply Selected Internship");
        UITheme.styleButton(applyBtn);

        applyBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1){
                JOptionPane.showMessageDialog(this,"Select internship first!");
                return;
            }

            int internshipId = Integer.parseInt(model.getValueAt(row,0).toString());
            Integer studentId = currentUser.getLinkedStudentId();
            if(studentId == null){
                JOptionPane.showMessageDialog(this,"Not linked to student profile.");
                return;
            }

            Application a = new Application();
            a.setStudentId(studentId);
            a.setInternshipId(internshipId);
            a.setStatus("Pending");
            a.setApplyDate(new Date(System.currentTimeMillis()));

            int id = new ApplicationDAO().addApplication(a, currentUser.getUserId());
            if(id>0){
                JOptionPane.showMessageDialog(this,"Applied successfully!");
                loadInternships(model);
                loadMyApplications(myApplicationsTableModel);
                myApplicationsLabel.setText(String.valueOf(countMyApplications()));
            } else {
                JOptionPane.showMessageDialog(this,"Application failed");
            }
        });

        JButton refreshBtn = new JButton("Refresh");
        UITheme.styleButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadInternships(model));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(refreshBtn);
        bottom.add(applyBtn);

        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void loadInternships(DefaultTableModel model){
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
                    it.getDuration()
            });
        }
    }

    private JPanel buildMyApplicationsPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] cols = {"AppID","Student","Internship","Status"};
        myApplicationsTableModel = new DefaultTableModel(cols,0);
        JTable table = new JTable(myApplicationsTableModel);
        UITheme.styleTable(table);

        loadMyApplications(myApplicationsTableModel);

        JButton refresh = new JButton("Refresh");
        UITheme.styleButton(refresh);
        refresh.addActionListener(e -> loadMyApplications(myApplicationsTableModel));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(refresh);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void loadMyApplications(DefaultTableModel model){
        model.setRowCount(0);
        int studentId = currentUser.getLinkedStudentId();
        ArrayList<Application> rows =
                new ApplicationDAO().getApplicationsForStudent(studentId);

        for(Application a: rows){
            model.addRow(new Object[]{
                    a.getApplicationId(),
                    a.getStudentName(),
                    a.getInternshipRole(),
                    a.getStatus()
            });
        }
    }

    private int countMyApplications(){
        int studentId = currentUser.getLinkedStudentId();
        return new ApplicationDAO()
                .getApplicationsForStudent(studentId).size();
    }

    private JPanel buildNotificationsPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        DefaultListModel<String> lm = new DefaultListModel<>();
        try {
            new NotificationDAO()
                    .getUserNotifications(currentUser.getUserId())
                    .forEach(lm::addElement);
        } catch (SQLException e) {
            e.printStackTrace();
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

        Integer sid = currentUser.getLinkedStudentId();
        if (sid != null) {
            Student s = new StudentDAO().getStudentById(sid);
            if (s != null) {
                gbc.gridx=0; gbc.gridy++;
                p.add(new JLabel("Full Name:"), gbc);
                gbc.gridx=1;
                p.add(new JLabel(s.getStudentName()), gbc);

                gbc.gridx=0; gbc.gridy++;
                p.add(new JLabel("Email:"), gbc);
                gbc.gridx=1;
                p.add(new JLabel(s.getEmail()), gbc);

                gbc.gridx=0; gbc.gridy++;
                p.add(new JLabel("Phone:"), gbc);
                gbc.gridx=1;
                p.add(new JLabel(s.getPhone()), gbc);
            }
        }
        return p;
    }
}