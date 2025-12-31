import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginUI extends JFrame {

    private JTextField usernameField = new JTextField(22);
    private JPasswordField passwordField = new JPasswordField(22);
    private JLabel message = new JLabel(" ");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI());
    }

    public LoginUI() {
        setTitle("GradPath — Login");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new GradientPanel();
        root.setLayout(new BorderLayout());
        add(root);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,12,12,12);

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(720,420));
        card.setBackground(Color.WHITE);
        card.setBorder(UITheme.cardBorder());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(10,10,10,10);
        c2.gridx = 0; c2.gridy = 0; c2.gridwidth = 2;

        JLabel title = new JLabel("GradPath");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.ACCENT);
        card.add(title, c2);

        c2.gridy++;
        JLabel sub = new JLabel("Login to your account");
        sub.setFont(UITheme.NORMAL);
        sub.setForeground(Color.DARK_GRAY);
        card.add(sub, c2);

        c2.gridwidth = 1; c2.gridy++; c2.gridx = 0;
        card.add(new JLabel("Username"), c2);
        c2.gridx = 1; card.add(usernameField, c2);

        c2.gridy++; c2.gridx = 0;
        card.add(new JLabel("Password"), c2);
        c2.gridx = 1; card.add(passwordField, c2);

        c2.gridy++; c2.gridx = 0; c2.gridwidth = 2;
        message.setForeground(Color.RED);
        card.add(message, c2);

        c2.gridy++; c2.gridx = 0; c2.gridwidth = 2;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);

        JButton signup = new JButton("Signup");
        JButton login = new JButton("Login");
        UITheme.styleButton(login);
        UITheme.styleButton(signup);

        btns.add(signup); btns.add(login);
        card.add(btns, c2);

        center.add(card, gbc);
        root.add(center, BorderLayout.CENTER);

        login.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        signup.addActionListener(e -> signupDialog());

        setVisible(true);
    }

    private void doLogin() {
        message.setText(" ");
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            message.setText("Enter username and password.");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User user = dao.login(u, p);
            if (user == null) {
                message.setText("Invalid credentials.");
                return;
            }

            SwingUtilities.invokeLater(() -> {
                switch (user.getRole().toLowerCase()) {
                    case "admin": new AdminDashboard(user); break;
                    case "student": new StudentDashboard(user); break;
                    case "company": new CompanyDashboard(user); break;
                    default: JOptionPane.showMessageDialog(this, "Unknown role: " + user.getRole());
                }
            });
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            message.setText("Login error: " + ex.getMessage());
        }
    }

    private void signupDialog() {
        Object[] options = {"Student", "Company"};
        int n = JOptionPane.showOptionDialog(this, "Register as:", "Signup",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (n == JOptionPane.YES_OPTION) new StudentSignup(this);
        else if (n == JOptionPane.NO_OPTION) new CompanySignup(this);
    }

    class StudentSignup extends JDialog {
        StudentSignup(Frame parent) {
            super(parent, "Student Signup", true);
            setSize(900, 600);
            setLocationRelativeTo(parent);

            JPanel p = new JPanel(new GridBagLayout());
            p.setBorder(UITheme.padding());
            p.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8,8,8,8);
            gbc.anchor = GridBagConstraints.WEST;

            JTextField name = new JTextField(28);
            JTextField email = new JTextField(28);
            JTextField phone = new JTextField(20);
            JComboBox<String> dept = new JComboBox<>(new String[]{"Select Dept","CS","AI","DS","CY"});
            JTextField cgpa = new JTextField(6);
            JTextField batch = new JTextField(8);
            JTextField uname = new JTextField(20);
            JPasswordField pass = new JPasswordField(20);

            int r = 0;
            gbc.gridx=0; gbc.gridy=0; p.add(new JLabel("Full Name"), gbc);
            gbc.gridx=1; p.add(name, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("Email"), gbc);
            gbc.gridx=1; p.add(email, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("Phone"), gbc);
            gbc.gridx=1; p.add(phone, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("DeptID"), gbc);
            gbc.gridx=1; p.add(dept, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("CGPA"), gbc);
            gbc.gridx=1; p.add(cgpa, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("Batch"), gbc);
            gbc.gridx=1; p.add(batch, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("Username"), gbc);
            gbc.gridx=1; p.add(uname, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("Password"), gbc);
            gbc.gridx=1; p.add(pass, gbc);

            gbc.gridx=1; gbc.gridy++; gbc.anchor = GridBagConstraints.EAST;
            JButton reg = new JButton("Register");
            UITheme.styleButton(reg);
            p.add(reg, gbc);

            reg.addActionListener(e -> {
                String nme = name.getText().trim(), em = email.getText().trim(), ph = phone.getText().trim();
                int deptId = dept.getSelectedIndex();
                double cg = 0; try { cg = Double.parseDouble(cgpa.getText().trim()); } catch (Exception ex){}
                String bt = batch.getText().trim(), un = uname.getText().trim(), pw = new String(pass.getPassword());
                if (nme.isEmpty() || em.isEmpty() || ph.isEmpty() || deptId==0 || un.isEmpty() || pw.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fill required fields");
                    return;
                }
                try {
                    Student s = new Student(nme, em, ph, deptId, cg, bt);
                    UserDAO udao = new UserDAO();
                    boolean ok = udao.registerStudentWithUser(s, un, pw);
                    if (ok) { JOptionPane.showMessageDialog(this, "Registered successfully!"); dispose(); }
                    else JOptionPane.showMessageDialog(this, "Registration failed");
                } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            });

            add(p);
            setVisible(true);
        }
    }

    class CompanySignup extends JDialog {
        CompanySignup(Frame parent) {
            super(parent, "Company Signup", true);
            setSize(800,500);
            setLocationRelativeTo(parent);

            JPanel p = new JPanel(new GridBagLayout());
            p.setBorder(UITheme.padding());
            p.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8,8,8,8);
            gbc.anchor = GridBagConstraints.WEST;

            JTextField name = new JTextField(28);
            JTextField industry = new JTextField(20);
            JTextField location = new JTextField(20);
            JTextField hr = new JTextField(20);
            JTextField uname = new JTextField(20);
            JPasswordField pass = new JPasswordField(20);

            gbc.gridx=0; gbc.gridy=0; p.add(new JLabel("Company Name"), gbc);
            gbc.gridx=1; p.add(name, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("Industry"), gbc);
            gbc.gridx=1; p.add(industry, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("Location"), gbc);
            gbc.gridx=1; p.add(location, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("HR Contact"), gbc);
            gbc.gridx=1; p.add(hr, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("Username"), gbc);
            gbc.gridx=1; p.add(uname, gbc);
            gbc.gridx=0; gbc.gridy++; p.add(new JLabel("Password"), gbc);
            gbc.gridx=1; p.add(pass, gbc);

            gbc.gridx=1; gbc.gridy++; gbc.anchor = GridBagConstraints.EAST;
            JButton reg = new JButton("Register");
            UITheme.styleButton(reg);
            p.add(reg, gbc);

            reg.addActionListener(e -> {
                String nme = name.getText().trim(), ind = industry.getText().trim(), loc = location.getText().trim(), hrc = hr.getText().trim();
                String un = uname.getText().trim(), pw = new String(pass.getPassword());
                if (nme.isEmpty() || ind.isEmpty() || loc.isEmpty() || hrc.isEmpty() || un.isEmpty() || pw.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fill required fields"); return;
                }
                try {
                    Company c = new Company(nme, ind, loc, hrc);
                    UserDAO udao = new UserDAO();
                    boolean ok = udao.registerCompanyWithUser(c, un, pw);
                    if (ok) { JOptionPane.showMessageDialog(this, "Registered successfully!"); dispose(); }
                    else JOptionPane.showMessageDialog(this, "Registration failed");
                } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            });

            add(p);
            setVisible(true);
        }
    }
}
