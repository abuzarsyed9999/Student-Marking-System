package com.college.sms.ui;

import com.college.sms.dao.FacultyDAO;
import com.college.sms.model.Faculty;

import javax.swing.*;
import java.awt.*;

public class FacultyLoginUI extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private FacultyDAO facultyDAO;

    public FacultyLoginUI() {
        facultyDAO = new FacultyDAO();
        initUI();
    }

    private void initUI() {
        setTitle("🏫 Faculty Login");
        setSize(400, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(new Color(240, 242, 245));

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(0, 70));
        JLabel title = new JLabel("🎓 Faculty Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(4, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));
        form.setBackground(Color.WHITE);

        form.add(new JLabel("📧 Email:"));
        txtEmail = new JTextField("abuzar@gmail.com");
        form.add(txtEmail);

        form.add(new JLabel("🔒 Password:"));
        txtPassword = new JPasswordField("1234");
        form.add(txtPassword);

        JButton btnLogin = new JButton("🔓 LOGIN");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(0, 45));
        form.add(new JLabel());
        form.add(btnLogin);

        add(form, BorderLayout.CENTER);

        // Login action
        btnLogin.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter both email and password", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Faculty faculty = facultyDAO.authenticateFaculty(email, password);
            
            if (faculty != null) {
                JOptionPane.showMessageDialog(this,
                    "✅ Login successful!\nWelcome, " + faculty.getName() + "!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                
                // ⚠️ FIX THIS LINE to match your actual dashboard class name!
                try {
                    new FacultyDashboard(faculty.getFacultyId()).setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error opening dashboard: " + ex.getMessage() + 
                        "\n\nMake sure FacultyDashboard class exists and accepts int constructor",
                        "Dashboard Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Invalid credentials!\n\nEmail: abuzar@gmail.com\nPassword: 1234",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
                txtPassword.selectAll();
                txtPassword.requestFocus();
            }
        });

        txtPassword.addActionListener(e -> btnLogin.doClick());
        txtEmail.requestFocus();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new FacultyLoginUI();
        });
    }
}