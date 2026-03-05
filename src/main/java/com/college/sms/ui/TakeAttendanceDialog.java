package com.college.sms.ui;

import com.college.sms.dao.SessionAttendanceDAO;
import com.college.sms.dao.RemedialSessionDAO;
import com.college.sms.model.SessionAttendance;
import com.college.sms.model.RemedialSession;
//import com.college.sms.model.AttendanceStats;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

public class TakeAttendanceDialog extends JDialog {
    
    private int sessionId;
    private SessionAttendanceDAO attendanceDAO;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JLabel lblSessionInfo;

    public TakeAttendanceDialog(JFrame parent, int sessionId) {
        super(parent, "📝 Take Attendance - Session #" + sessionId, true);
        this.sessionId = sessionId;
        attendanceDAO = new SessionAttendanceDAO();
        
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 247, 250));
        
        initComponents();
        loadStudents();
        setVisible(true);
    }

    private void initComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        lblSessionInfo = new JLabel("Loading session info...");
        lblSessionInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSessionInfo.setForeground(Color.WHITE);
        headerPanel.add(lblSessionInfo, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Attendance Table
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columns = {"Student ID", "Roll No", "Name", "Status", "Check-In", "Check-Out", "Rating", "Feedback"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return c >= 3; }
        };

        attendanceTable = new JTable(tableModel);
        styleTable(attendanceTable);

        JScrollPane tableScroll = new JScrollPane(attendanceTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnSave = new JButton("💾 Save Attendance");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(160, 36));
        btnSave.addActionListener(e -> saveAttendance());
        buttonPanel.add(btnSave);

        JButton btnMarkAllPresent = new JButton("✅ Mark All Present");
        btnMarkAllPresent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMarkAllPresent.setBackground(new Color(52, 152, 219));
        btnMarkAllPresent.setForeground(Color.WHITE);
        btnMarkAllPresent.setPreferredSize(new Dimension(160, 36));
        btnMarkAllPresent.addActionListener(e -> markAllPresent());
        buttonPanel.add(btnMarkAllPresent);

        JButton btnCancel = new JButton("❌ Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setBackground(new Color(149, 165, 166));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(120, 36));
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(235, 235, 235));

        // Status column renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                
                if (column == 3) { // Status column
                    String status = value.toString();
                    if ("Present".equals(status)) {
                        c.setForeground(new Color(27, 94, 32));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if ("Absent".equals(status)) {
                        c.setForeground(new Color(183, 28, 28));
                    } else if ("Late".equals(status)) {
                        c.setForeground(new Color(137, 104, 0));
                    }
                }
                return c;
            }
        });
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        
        // Load students from remedial record (implement based on your schema)
        // This is example data - replace with actual database query
        Object[] sampleStudent = {1, "A001", "John Doe", "Present", 
            LocalTime.now().toString(), "", "Good", ""};
        tableModel.addRow(sampleStudent);
        
        lblSessionInfo.setText("📅 Session #" + sessionId + " | Students: 1");
    }

    private void markAllPresent() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt("Present", i, 3);
            tableModel.setValueAt(LocalTime.now().toString(), i, 4);
        }
    }

    private void saveAttendance() {
        int savedCount = 0;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                SessionAttendance attendance = new SessionAttendance();
                attendance.setSessionId(sessionId);
                attendance.setStudentId(Integer.parseInt(tableModel.getValueAt(i, 0).toString()));
                attendance.setAttendanceStatus(tableModel.getValueAt(i, 3).toString());
                attendance.setCheckInTime(Time.valueOf(tableModel.getValueAt(i, 4).toString()));
                attendance.setPerformanceRating(tableModel.getValueAt(i, 6).toString());
                attendance.setFacultyFeedback(tableModel.getValueAt(i, 7).toString());
                
                if (attendanceDAO.markAttendance(attendance)) {
                    savedCount++;
                }
            } catch (Exception e) {
                System.err.println("Error saving attendance for row " + i + ": " + e.getMessage());
            }
        }
        
        JOptionPane.showMessageDialog(this, 
            "✅ Attendance saved for " + savedCount + " students!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}