package com.college.sms.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class FacultyDashboard extends JFrame {

    private int facultyId;

    public FacultyDashboard(int facultyId) {
        this.facultyId = facultyId;

        setTitle("Faculty Dashboard - ID: " + facultyId);
        setSize(450, 650);  // ✅ Increased height for 9 buttons
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(76, 175, 80));
        titlePanel.setPreferredSize(new Dimension(getWidth(), 80));
        
        JLabel lblTitle = new JLabel("Faculty Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        titlePanel.add(lblTitle);
        
        // Buttons panel - 9 rows for 9 buttons (including BOTH remedial options)
        JPanel panel = new JPanel(new GridLayout(9, 1, 15, 15));  // ✅ Changed from 8 to 9
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JButton[] buttons = {
            createButton("Manage Classes", "class"),
            createButton("Manage Subjects", "subject"),
            createButton("Manage Exams", "exam"),
            createButton("Manage Students", "student"),
            createButton("Manage Marks", "marks"),
            createButton("View Results", "results"),
            createButton("🔧 Remedial Tracking", "remedial"),        // ✅ Button 1: Add failed students
            createButton("📅 Session Management", "sessions"),       // ✅ Button 2: Schedule sessions
            createButton("Logout", "logout")
        };
        
        for (JButton btn : buttons) {
            panel.add(btn);
        }

        // Layout
        setLayout(new BorderLayout(10, 10));
        add(titlePanel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    // ✅ Getter method for facultyId
    public int getFacultyId() {
        return this.facultyId;
    }

    private JButton createButton(String text, String type) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Professional color scheme based on type
        switch(type) {
            case "class": btn.setBackground(new Color(52, 152, 219)); break;    // Blue
            case "subject": btn.setBackground(new Color(142, 68, 173)); break;  // Purple
            case "exam": btn.setBackground(new Color(243, 156, 18)); break;     // Orange
            case "student": btn.setBackground(new Color(46, 204, 113)); break;  // Green
            case "marks": btn.setBackground(new Color(231, 76, 60)); break;     // Red
            case "results": btn.setBackground(new Color(41, 128, 185)); break;  // Light Blue
            case "remedial": btn.setBackground(new Color(155, 89, 182)); break; // Purple (remedial tracking)
            case "sessions": btn.setBackground(new Color(108, 92, 231)); break; // Indigo (session management)
            case "logout": btn.setBackground(new Color(149, 165, 166)); break;  // Gray
        }
        
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Hover effect for better UX
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(btn.getBackground().darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                switch(type) {
                    case "class": btn.setBackground(new Color(52, 152, 219)); break;
                    case "subject": btn.setBackground(new Color(142, 68, 173)); break;
                    case "exam": btn.setBackground(new Color(243, 156, 18)); break;
                    case "student": btn.setBackground(new Color(46, 204, 113)); break;
                    case "marks": btn.setBackground(new Color(231, 76, 60)); break;
                    case "results": btn.setBackground(new Color(41, 128, 185)); break;
                    case "remedial": btn.setBackground(new Color(155, 89, 182)); break;
                    case "sessions": btn.setBackground(new Color(108, 92, 231)); break;
                    case "logout": btn.setBackground(new Color(149, 165, 166)); break;
                }
            }
        });
        
        // Action listeners
        if (type.equals("class")) {
            btn.addActionListener(e -> {
                setVisible(false);
                new ClassManagementUI(facultyId).setVisible(true);
            });
        } else if (type.equals("subject")) {
            btn.addActionListener(e -> {
                setVisible(false);
                try {
                    new SubjectManagementUI(facultyId, this).setVisible(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Cannot open Subject Management", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        } else if (type.equals("exam")) {
            btn.addActionListener(e -> {
                setVisible(false);
                try {
                    new ExamManagementUI(facultyId, this).setVisible(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Unable to open Exam Management", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        } else if (type.equals("student")) {
            btn.addActionListener(e -> {
                setVisible(false);
                new StudentManagementUI(facultyId, this).setVisible(true);
            });
        } else if (type.equals("marks")) {
            btn.addActionListener(e -> {
                setVisible(false);
                try {
                    new MarksManagementUI(this).setVisible(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Unable to open Marks Management", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        } else if (type.equals("results")) {
            btn.addActionListener(e -> {
                setVisible(false);
                new ResultsUI(facultyId, this).setVisible(true);  // ✅ Pass 'this' for back navigation
            });
        } 
        // ✅ Remedial Tracking button - Opens RemedialManagementUI (add failed students)
        else if (type.equals("remedial")) {
            btn.addActionListener(e -> {
                setVisible(false);
                new RemedialManagementUI(facultyId, this).setVisible(true);  // ✅ Pass 'this' for back navigation
            });
        } 
        // ✅ Session Management button - Opens RemedialSessionManagementUI (schedule sessions)
        else if (type.equals("sessions")) {
            btn.addActionListener(e -> {
                setVisible(false);
                new RemedialSessionManagementUI(facultyId, this).setVisible(true);  // ✅ Pass 'this' for back navigation
            });
        } 
        else if (type.equals("logout")) {
            btn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    // new LoginUI().setVisible(true); // Uncomment when login exists
                }
            });
        }
        
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new FacultyDashboard(1);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Application failed to start!\nError: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}