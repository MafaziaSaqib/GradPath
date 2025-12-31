import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class CompanyDashboard extends JFrame {

    private User currentUser;
    private JTabbedPane tabs;

    public CompanyDashboard(User user){
        this.currentUser = user;
        setTitle("GradPath — Company");
        setSize(1400, 900);
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

        JLabel title = new JLabel("GradPath — Company");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.ACCENT);
        title.setBorder(new EmptyBorder(8,16,8,8));
        header.add(title, BorderLayout.WEST);

        JButton logout = new JButton("Logout");
        UITheme.styleButton(logout);
        logout.addActionListener(e -> { dispose(); new LoginUI(); });

        String rightLabelText = currentUser.getUsername() + " | Company";
        Integer linkedCompanyId = currentUser.getLinkedCompanyId();
        if (linkedCompanyId != null) {
            Company comp = new CompanyDAO().getCompanyById(linkedCompanyId);
            if (comp != null && comp.getCompanyName() != null && !comp.getCompanyName().isEmpty()) {
                rightLabelText = comp.getCompanyName() + " | Company";
            }
        }

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(new JLabel(rightLabelText));
        right.add(logout);
        header.add(right, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(UITheme.NORMAL);
        tabs.setPreferredSize(new Dimension(230,0));

        tabs.add("Dashboard", buildDashboard());
        tabs.add("My Internships", buildMyInternships());
        tabs.add("Applicants", buildApplicantsPanel());
        tabs.add("Notifications", buildNotifications());
        tabs.add("Profile", buildProfile());

        root.add(tabs, BorderLayout.CENTER);
        add(root);
    }

    private JPanel buildDashboard(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel top = new JPanel(new GridLayout(1,3,16,16));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(20,20,20,20));

        top.add(card("My Internships", String.valueOf(countMyInternships())));

        int applicantsCount =
                new ApplicationDAO()
                        .getApplicationsForCompany(currentUser.getLinkedCompanyId())
                        .size();
        top.add(card("Applicants", String.valueOf(applicantsCount)));

        int notificationsCount = 0;
        try {
            notificationsCount =
                    new NotificationDAO()
                            .getUserNotifications(currentUser.getUserId())
                            .size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        top.add(card("Notifications", String.valueOf(notificationsCount)));

        p.add(top, BorderLayout.NORTH);
        return p;
    }

    private int countMyInternships(){
        int cid = currentUser.getLinkedCompanyId()!=null
                ? currentUser.getLinkedCompanyId()
                : -1;
        if(cid==-1) return 0;

        int count=0;
        for(Internship i : new InternshipDAO().getAllInternships())
            if(i.getCompanyId()==cid) count++;
        return count;
    }

    private JPanel card(String t, String v){
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Color.WHITE);
        c.setBorder(UITheme.cardBorder());

        JLabel title = new JLabel(t);
        title.setFont(UITheme.NORMAL);

        JLabel val = new JLabel(v);
        val.setFont(new Font("Segoe UI",Font.BOLD,28));
        val.setForeground(UITheme.ACCENT);

        c.add(title, BorderLayout.NORTH);
        c.add(val, BorderLayout.CENTER);
        return c;
    }

    private JPanel buildMyInternships(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] cols = {"InternshipID","Role","Stipend","Duration","Action"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        loadMyInternships(model);

        JButton post = new JButton("Post Internship");
        UITheme.styleButton(post);

        post.addActionListener(e -> {
            JTextField role = new JTextField(30);
            JTextField desc = new JTextField(30);
            JTextField stipend = new JTextField(10);
            JTextField dur = new JTextField(12);

            JPanel form = new JPanel(new GridLayout(0,1));
            form.add(new JLabel("Role")); form.add(role);
            form.add(new JLabel("Description")); form.add(desc);
            form.add(new JLabel("Stipend")); form.add(stipend);
            form.add(new JLabel("Duration")); form.add(dur);

            int r = JOptionPane.showConfirmDialog(
                    this,form,"Post Internship",JOptionPane.OK_CANCEL_OPTION);

            if(r==JOptionPane.OK_OPTION){
                double stp = 0;
                try{ stp = Double.parseDouble(stipend.getText().trim()); }
                catch(Exception ex){}

                int compId =
                        currentUser.getLinkedCompanyId()!=null
                                ? currentUser.getLinkedCompanyId()
                                : 0;

                Internship i = new Internship(
                        compId,
                        role.getText().trim(),
                        desc.getText().trim(),
                        stp,
                        dur.getText().trim()
                );

                int id = new InternshipDAO()
                        .addInternship(i, currentUser.getUserId());

                if(id>0){
                    JOptionPane.showMessageDialog(this,"Posted successfully");
                    loadMyInternships(model);
                } else {
                    JOptionPane.showMessageDialog(this,"Post failed");
                }
            }
        });

        JButton refresh = new JButton("Refresh");
        UITheme.styleButton(refresh);
        refresh.addActionListener(e -> loadMyInternships(model));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(refresh);
        bottom.add(post);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void loadMyInternships(DefaultTableModel model){
        model.setRowCount(0);

        int cid = currentUser.getLinkedCompanyId()!=null
                ? currentUser.getLinkedCompanyId()
                : -1;
        if(cid==-1) return;

        InternshipDAO idao = new InternshipDAO();
        ArrayList<Internship> list = idao.getAllInternships();

        for(Internship it:list)
            if(it.getCompanyId()==cid)
                model.addRow(new Object[]{
                        it.getInternshipId(),
                        it.getRole(),
                        it.getStipend(),
                        it.getDuration(),
                        "Applicants"
                });
    }

    private JPanel buildApplicantsPanel(){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String[] cols = {"ApplicationID","Student","Internship","Status","Action"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        loadApplicants(model);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if(col==4){
                    int appId =
                            Integer.parseInt(model.getValueAt(row,0).toString());

                    String[] options = {"Accept","Reject"};
                    int choice = JOptionPane.showOptionDialog(
                            CompanyDashboard.this,
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

                    if(newStatus!=null){
                        boolean updated =
                                new ApplicationDAO()
                                        .updateStatus(appId,newStatus,currentUser.getUserId());

                        if(updated){
                            int studentId =
                                    new ApplicationDAO().getStudentId(appId);
                            Integer uid =
                                    new UserDAO().getUserIdByStudentId(studentId);

                            if(uid != null){
                                try{
                                    new NotificationDAO()
                                            .sendNotification(
                                                    uid,
                                                    "Your application "+appId+" was "+newStatus+"!");
                                } catch (SQLException ex){
                                    ex.printStackTrace();
                                }
                            }

                            loadApplicants(model);
                            JOptionPane.showMessageDialog(
                                    CompanyDashboard.this,
                                    "Application "+newStatus);
                        } else {
                            JOptionPane.showMessageDialog(
                                    CompanyDashboard.this,
                                    "Update failed");
                        }
                    }
                }
            }
        });

        JButton refresh = new JButton("Refresh");
        UITheme.styleButton(refresh);
        refresh.addActionListener(e -> loadApplicants(model));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(refresh);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void loadApplicants(DefaultTableModel model){
        model.setRowCount(0);

        int cid = currentUser.getLinkedCompanyId()!=null
                ? currentUser.getLinkedCompanyId()
                : -1;
        if(cid==-1) return;

        ArrayList<Application> list =
                new ApplicationDAO().getApplicationsForCompany(cid);

        for(Application a:list)
            model.addRow(new Object[]{
                    a.getApplicationId(),
                    a.getStudentName(),
                    a.getInternshipRole(),
                    a.getStatus(),
                    "Action"
            });
    }

    private JPanel buildNotifications(){

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

    private JPanel buildProfile(){

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor=GridBagConstraints.WEST;

        gbc.gridx=0; gbc.gridy=0;
        p.add(new JLabel("Username:"),gbc);
        gbc.gridx=1;
        p.add(new JLabel(currentUser.getUsername()),gbc);

        gbc.gridx=0; gbc.gridy++;
        p.add(new JLabel("Role:"),gbc);
        gbc.gridx=1;
        p.add(new JLabel(currentUser.getRole()),gbc);

        Integer cid = currentUser.getLinkedCompanyId();
        if (cid != null) {
            Company c = new CompanyDAO().getCompanyById(cid);
            if (c != null) {
                gbc.gridx=0; gbc.gridy++;
                p.add(new JLabel("Company Name:"), gbc);
                gbc.gridx=1;
                p.add(new JLabel(c.getCompanyName()), gbc);

                gbc.gridx=0; gbc.gridy++;
                p.add(new JLabel("Industry:"), gbc);
                gbc.gridx=1;
                p.add(new JLabel(c.getIndustry()), gbc);

                gbc.gridx=0; gbc.gridy++;
                p.add(new JLabel("Location:"), gbc);
                gbc.gridx=1;
                p.add(new JLabel(c.getLocation()), gbc);

                gbc.gridx=0; gbc.gridy++;
                p.add(new JLabel("HR Contact:"), gbc);
                gbc.gridx=1;
                p.add(new JLabel(c.getHrContact()), gbc);
            }
        }
        return p;
    }
}
