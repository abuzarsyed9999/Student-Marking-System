package com.college.sms.ui;

import com.college.sms.dao.RemedialSessionDAO;
import com.college.sms.model.RemedialSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;

public class CreateSessionDialog extends JDialog {
    
    private int facultyId;
    private int classId;
    private int subjectId;
    private RemedialSessionDAO sessionDAO;
    private JComboBox<Integer> comboRemedial;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private JTextField txtTopic, txtLocation, txtDuration;
    private JTextArea txtNotes;

    public CreateSessionDialog(JFrame parent, int facultyId, int classId, int subjectId) {
        super(parent, "➕ Create Remedial Session", true);
        this.facultyId = facultyId;
        this.classId = classId;
        this.subjectId = subjectId;
        sessionDAO = new RemedialSessionDAO();
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 247, 250));
        
        initComponents();
        loadRemedialStudents();
        setVisible(true);
    }

    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("📅 Schedule New Remedial Session");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Remedial Student
        formPanel.add(new JLabel("🔧 Remedial Student:"));
        comboRemedial = new JComboBox<>();
        formPanel.add(comboRemedial);

        // Date
        formPanel.add(new JLabel("📅 Session Date:"));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        formPanel.add(dateSpinner);

        // Time
        formPanel.add(new JLabel("🕐 Session Time:"));
        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        formPanel.add(timeSpinner);

        // Duration
        formPanel.add(new JLabel("⏱️ Duration (minutes):"));
        txtDuration = new JTextField("60");
        formPanel.add(txtDuration);

        // Topic
        formPanel.add(new JLabel("📚 Topic Covered:"));
        txtTopic = new JTextField();
        formPanel.add(txtTopic);

        // Location
        formPanel.add(new JLabel("📍 Location:"));
        txtLocation = new JTextField();
        formPanel.add(txtLocation);

        // Notes
        formPanel.add(new JLabel("📝 Notes:"));
        txtNotes = new JTextArea(3, 20);
        txtNotes.setLineWrap(true);
        formPanel.add(new JScrollPane(txtNotes));

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnSave = new JButton("💾 Save Session");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(140, 36));
        btnSave.addActionListener(e -> saveSession());
        buttonPanel.add(btnSave);

        JButton btnCancel = new JButton("❌ Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setBackground(new Color(231, 76, 60));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(120, 36));
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadRemedialStudents() {
        try {
            // Load students from remedial_tracking table
            // This is simplified - implement based on your DAO
            comboRemedial.addItem(1); // Example student ID
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSession() {
        try {
            RemedialSession session = new RemedialSession();
            session.setRemedialId((Integer) comboRemedial.getSelectedItem());
            session.setSessionDate(new Date(((java.util.Date) dateSpinner.getValue()).getTime()));
            session.setSessionTime(new Time(((java.util.Date) timeSpinner.getValue()).getTime()));
            session.setDurationMinutes(Integer.parseInt(txtDuration.getText()));
            session.setTopicCovered(txtTopic.getText());
            session.setLocation(txtLocation.getText());
            session.setFacultyId(facultyId);
            session.setNotes(txtNotes.getText());
            session.setSessionStatus("Scheduled");

            int sessionId = sessionDAO.createSession(session);
            
            if (sessionId > 0) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Session created successfully!\nSession ID: " + sessionId,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Failed to create session",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}