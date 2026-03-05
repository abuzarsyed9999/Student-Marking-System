//package com.college.sms.ui;
//
//import com.college.sms.dao.*;
//import com.college.sms.model.*;
//
//import javax.swing.*;
//import javax.swing.border.*;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.JTableHeader;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.sql.*;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public class RemedialSessionManagementUI extends JFrame {
//
//    private int facultyId;
//    private JFrame previousUI;
//
//    // Filters
//    private JComboBox<String> comboClass;
//    private JComboBox<String> comboSubject;
//    private JComboBox<String> comboExam;
//
//    // Left Panel: Remedial Students Table
//    private JTable remedialStudentsTable;
//    private DefaultTableModel remedialStudentsModel;
//
//    // Right Panel: Sessions Table
//    private JTable sessionsTable;
//    private DefaultTableModel sessionsModel;
//
//    // Buttons
//    private JButton btnBack, btnCreateSession, btnLoadSessions, btnRefresh, btnTakeAttendance, btnViewProgress;
//
//    // DAOs (instance fields - accessible to non-static inner classes)
//    private ClassDAO classDAO = new ClassDAO();
//    private SubjectDAO subjectDAO = new SubjectDAO();
//    private ExamDAO examDAO = new ExamDAO();
//    private RemedialDAO remedialDAO = new RemedialDAO();
//    private RemedialSessionDAO sessionDAO = new RemedialSessionDAO();
//    private SessionAttendanceDAO attendanceDAO = new SessionAttendanceDAO();
//    private StudentDAO studentDAO = new StudentDAO();
//
//    // State
//    private int currentClassId = -1;
//    private int currentSubjectId = -1;
//    private int currentExamId = -1;
//    private int currentMaxMarks = 0;
//    private int currentPassMarks = 0;
//    private int selectedRemedialId = -1;
//    private int selectedStudentId = -1; // For marks entry
//
//    // Colors
//    private static final Color PRIMARY_COLOR = new Color(52, 73, 94);
//    private static final Color PRIMARY_LIGHT = new Color(41, 128, 185);
//    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
//    private static final Color WARNING_COLOR = new Color(243, 156, 18);
//    private static final Color DANGER_COLOR = new Color(231, 76, 60);
//    private static final Color INFO_COLOR = new Color(52, 152, 219);
//    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
//    private static final Color CARD_COLOR = Color.WHITE;
//
//    public RemedialSessionManagementUI(int facultyId, JFrame previousUI) {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        initComponents();
//        loadClasses();
//        setVisible(true);
//    }
//
//    private void initComponents() {
//        setTitle("📅 Remedial Session Management | Faculty ID: " + facultyId);
//        setSize(1350, 820);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(new BorderLayout(10, 10));
//        getContentPane().setBackground(BACKGROUND_COLOR);
//
//        // ===== HEADER =====
//        JPanel headerPanel = new JPanel(new BorderLayout());
//        headerPanel.setBackground(PRIMARY_COLOR);
//        headerPanel.setPreferredSize(new Dimension(0, 60));
//        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
//
//        JLabel titleLabel = new JLabel("📅 Remedial Session Management");
//        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
//        titleLabel.setForeground(Color.WHITE);
//        headerPanel.add(titleLabel, BorderLayout.WEST);
//
//        btnBack = createButton("⇦ Back to Dashboard", PRIMARY_LIGHT, Color.WHITE);
//        btnBack.setPreferredSize(new Dimension(180, 38));
//        btnBack.addActionListener(e -> navigateBack());
//        headerPanel.add(btnBack, BorderLayout.EAST);
//
//        // ===== FILTER PANEL =====
//        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
//        filterPanel.setBackground(CARD_COLOR);
//        filterPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
//
//        filterPanel.add(new JLabel("🏫 Class:"));
//        comboClass = createComboBox();
//        comboClass.addActionListener(e -> {
//            if (comboClass.getSelectedItem() != null && !comboClass.getSelectedItem().toString().contains("--")) {
//                currentClassId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//                loadSubjects();
//            }
//        });
//        filterPanel.add(comboClass);
//
//        filterPanel.add(new JLabel("📚 Subject:"));
//        comboSubject = createComboBox();
//        comboSubject.addActionListener(e -> {
//            if (comboSubject.getSelectedItem() != null && !comboSubject.getSelectedItem().toString().contains("--")) {
//                currentSubjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//                loadExams();
//            }
//        });
//        filterPanel.add(comboSubject);
//
//        filterPanel.add(new JLabel("📝 Exam:"));
//        comboExam = createComboBox();
//        comboExam.addActionListener(e -> {
//            if (comboExam.getSelectedItem() != null && !comboExam.getSelectedItem().toString().contains("--")) {
//                currentExamId = Integer.parseInt(comboExam.getSelectedItem().toString().split(":")[0].trim());
//                try {
//                    Exam exam = examDAO.getExamById(currentExamId);
//                    currentMaxMarks = exam.getMaxMarks();
//                    currentPassMarks = exam.getPassMarks();
//                } catch (Exception ex) { System.err.println("Error: " + ex.getMessage()); }
//                loadRemedialStudents();
//            }
//        });
//        filterPanel.add(comboExam);
//
//        btnRefresh = createButton("🔄 Refresh", new Color(149, 165, 166), Color.WHITE);
//        btnRefresh.setPreferredSize(new Dimension(100, 34));
//        btnRefresh.addActionListener(e -> { loadRemedialStudents(); });
//        filterPanel.add(btnRefresh);
//
//        // ===== MAIN SPLIT PANE =====
//        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//        mainSplitPane.setDividerLocation(550);
//        mainSplitPane.setResizeWeight(0.4);
//        mainSplitPane.setBorder(new EmptyBorder(10, 10, 10, 10));
//
//        // ===== LEFT PANEL: Remedial Students =====
//        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
//        leftPanel.setBackground(CARD_COLOR);
//        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
//
//        JLabel leftTitle = new JLabel("👥 Students in Remedial Tracking");
//        leftTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        leftTitle.setForeground(PRIMARY_COLOR);
//        leftPanel.add(leftTitle, BorderLayout.NORTH);
//
//        String[] remedialCols = {"Remedial ID", "Student", "Roll No", "Failed Marks", "Status", "Attended", "Actions"};
//        remedialStudentsModel = new DefaultTableModel(remedialCols, 0) {
//            public boolean isCellEditable(int r, int c) { return false; }
//        };
//
//        remedialStudentsTable = new JTable(remedialStudentsModel);
//        styleTable(remedialStudentsTable);
//        remedialStudentsTable.getSelectionModel().addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) {
//                int row = remedialStudentsTable.getSelectedRow();
//                if (row >= 0) {
//                    selectedRemedialId = Integer.parseInt(remedialStudentsModel.getValueAt(row, 0).toString());
//                    selectedStudentId = getStudentIdFromRemedial(selectedRemedialId);
//                    btnCreateSession.setEnabled(true);
//                    btnLoadSessions.setEnabled(true);
//                }
//            }
//        });
//
//        JScrollPane leftScroll = new JScrollPane(remedialStudentsTable);
//        leftPanel.add(leftScroll, BorderLayout.CENTER);
//        mainSplitPane.setLeftComponent(leftPanel);
//
//        // ===== RIGHT PANEL: Sessions =====
//        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
//        rightPanel.setBackground(CARD_COLOR);
//        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
//
//        JLabel rightTitle = new JLabel("📋 Remedial Sessions");
//        rightTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        rightTitle.setForeground(PRIMARY_COLOR);
//        rightPanel.add(rightTitle, BorderLayout.NORTH);
//
//        String[] sessionCols = {"Session ID", "Date", "Time", "Topic", "Location", "Status", "Marks Entry", "Attendance", "Actions"};
//        sessionsModel = new DefaultTableModel(sessionCols, 0) {
//            public boolean isCellEditable(int r, int c) { return c == 8; }
//        };
//
//        sessionsTable = new JTable(sessionsModel);
//        styleTable(sessionsTable);
//        sessionsTable.getSelectionModel().addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) {
//                int row = sessionsTable.getSelectedRow();
//                btnTakeAttendance.setEnabled(row >= 0);
//                btnViewProgress.setEnabled(row >= 0);
//            }
//        });
//
//        JScrollPane rightScroll = new JScrollPane(sessionsTable);
//        rightPanel.add(rightScroll, BorderLayout.CENTER);
//
//        // Right Panel Actions - CRUD + Load
//        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
//        rightActions.setBackground(CARD_COLOR);
//
//        btnCreateSession = createButton("➕ Create", SUCCESS_COLOR, Color.WHITE);
//        btnCreateSession.setPreferredSize(new Dimension(90, 32));
//        btnCreateSession.setEnabled(false);
//        btnCreateSession.addActionListener(e -> createNewSession());
//        rightActions.add(btnCreateSession);
//
//        btnLoadSessions = createButton("🔄 Load", INFO_COLOR, Color.WHITE);
//        btnLoadSessions.setPreferredSize(new Dimension(80, 32));
//        btnLoadSessions.setEnabled(false);
//        btnLoadSessions.addActionListener(e -> loadSessions());
//        rightActions.add(btnLoadSessions);
//
//        btnTakeAttendance = createButton("📝 Attendance", WARNING_COLOR, Color.WHITE);
//        btnTakeAttendance.setPreferredSize(new Dimension(110, 32));
//        btnTakeAttendance.setEnabled(false);
//        btnTakeAttendance.addActionListener(e -> takeAttendance());
//        rightActions.add(btnTakeAttendance);
//
//        btnViewProgress = createButton("📊 Progress", DANGER_COLOR, Color.WHITE);
//        btnViewProgress.setPreferredSize(new Dimension(100, 32));
//        btnViewProgress.setEnabled(false);
//        btnViewProgress.addActionListener(e -> viewProgress());
//        rightActions.add(btnViewProgress);
//
//        rightPanel.add(rightActions, BorderLayout.SOUTH);
//        mainSplitPane.setRightComponent(rightPanel);
//
//        // ===== ASSEMBLE UI =====
//        JPanel topContainer = new JPanel();
//        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
//        topContainer.add(headerPanel);
//        topContainer.add(filterPanel);
//        add(topContainer, BorderLayout.NORTH);
//        add(mainSplitPane, BorderLayout.CENTER);
//
//        // Status Bar
//        JLabel statusLabel = new JLabel("Ready. Select Class → Subject → Exam to view remedial students.");
//        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
//        statusLabel.setOpaque(true);
//        statusLabel.setBackground(new Color(236, 240, 241));
//        add(statusLabel, BorderLayout.SOUTH);
//    }
//
//    // ===== BACK NAVIGATION =====
//    private void navigateBack() {
//        dispose();
//        if (previousUI != null) {
//            SwingUtilities.invokeLater(() -> {
//                previousUI.setVisible(true);
//                previousUI.toFront();
//                previousUI.requestFocus();
//            });
//        } else {
//            new FacultyDashboard(facultyId).setVisible(true);
//        }
//    }
//
//    // ===== HELPER METHODS =====
//    private JButton createButton(String text, Color bg, Color fg) {
//        JButton btn = new JButton(text);
//        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btn.setForeground(fg);
//        btn.setBackground(bg);
//        btn.setFocusPainted(false);
//        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
//        btn.addMouseListener(new MouseAdapter() {
//            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
//            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
//        });
//        return btn;
//    }
//
//    private JComboBox<String> createComboBox() {
//        JComboBox<String> combo = new JComboBox<>();
//        combo.setPreferredSize(new Dimension(190, 32));
//        combo.setBackground(Color.WHITE);
//        return combo;
//    }
//
//    private void styleTable(JTable table) {
//        table.setRowHeight(30);
//        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
//        table.setGridColor(new Color(235, 235, 235));
//        table.setSelectionBackground(INFO_COLOR);
//        table.setSelectionForeground(Color.WHITE);
//        JTableHeader header = table.getTableHeader();
//        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
//        header.setBackground(PRIMARY_COLOR);
//        header.setForeground(Color.WHITE);
//
//        // Custom renderer for marks comparison column
//        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
//                if (!isSelected && column == 6) { // Marks Entry column
//                    String text = value.toString();
//                    if (text.contains("🟢")) {
//                        c.setForeground(new Color(27, 94, 32));
//                        c.setFont(c.getFont().deriveFont(Font.BOLD));
//                    } else if (text.contains("🔴")) {
//                        c.setForeground(new Color(183, 28, 28));
//                    }
//                }
//                return c;
//            }
//        });
//    }
//
//    // Get student_id from remedial_tracking table
//    private int getStudentIdFromRemedial(int remedialId) {
//        try {
//            Connection conn = com.college.sms.util.DBConnection.getConnection();
//            PreparedStatement pstmt = conn.prepareStatement("SELECT student_id FROM remedial_tracking WHERE remedial_id = ?");
//            pstmt.setInt(1, remedialId);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                int studentId = rs.getInt("student_id");
//                rs.close();
//                pstmt.close();
//                conn.close();
//                return studentId;
//            }
//            rs.close();
//            pstmt.close();
//            conn.close();
//        } catch (Exception e) { e.printStackTrace(); }
//        return -1;
//    }
//
//    // Get total sessions count for a student
//    private int getTotalSessionsCount(int studentId) {
//        try {
//            Connection conn = com.college.sms.util.DBConnection.getConnection();
//            PreparedStatement pstmt = conn.prepareStatement(
//                "SELECT COUNT(*) FROM remedial_sessions rs JOIN remedial_tracking r ON rs.remedial_id = r.remedial_id WHERE r.student_id = ?"
//            );
//            pstmt.setInt(1, studentId);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                int count = rs.getInt(1);
//                rs.close();
//                pstmt.close();
//                conn.close();
//                return count;
//            }
//            rs.close();
//            pstmt.close();
//            conn.close();
//        } catch (Exception e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    // Get attended sessions count for a student
//    private int getAttendedSessionsCount(int studentId) {
//        try {
//            Connection conn = com.college.sms.util.DBConnection.getConnection();
//            PreparedStatement pstmt = conn.prepareStatement(
//                "SELECT COUNT(DISTINCT sa.session_id) FROM session_attendance sa " +
//                "JOIN remedial_sessions rs ON sa.session_id = rs.session_id " +
//                "JOIN remedial_tracking r ON rs.remedial_id = r.remedial_id " +
//                "WHERE r.student_id = ? AND sa.attendance_status = 'Present'"
//            );
//            pstmt.setInt(1, studentId);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                int count = rs.getInt(1);
//                rs.close();
//                pstmt.close();
//                conn.close();
//                return count;
//            }
//            rs.close();
//            pstmt.close();
//            conn.close();
//        } catch (Exception e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    // ===== DATA LOADING =====
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        comboSubject.removeAllItems();
//        comboExam.removeAllItems();
//        remedialStudentsModel.setRowCount(0);
//        sessionsModel.setRowCount(0);
//        try {
//            List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
//            for (String[] c : classes) comboClass.addItem(c[0] + " - " + c[1]);
//        } catch (Exception e) { comboClass.addItem("-- Error --"); }
//    }
//
//    private void loadSubjects() {
//        comboSubject.removeAllItems();
//        comboExam.removeAllItems();
//        remedialStudentsModel.setRowCount(0);
//        sessionsModel.setRowCount(0);
//        try {
//            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(currentClassId, facultyId);
//            for (Subject s : subjects) comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
//        } catch (Exception e) { comboSubject.addItem("-- Error --"); }
//    }
//
//    private void loadExams() {
//        comboExam.removeAllItems();
//        remedialStudentsModel.setRowCount(0);
//        sessionsModel.setRowCount(0);
//        try {
//            List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, currentClassId, currentSubjectId);
//            for (Exam ex : exams)
//                comboExam.addItem(ex.getExamId() + ":" + ex.getExamName() + " (Max: " + ex.getMaxMarks() + ", Pass: " + ex.getPassMarks() + ")");
//        } catch (Exception e) { comboExam.addItem("-- Error --"); }
//    }
//
//    // Load students added to remedial tracking for selected exam
//    private void loadRemedialStudents() {
//        remedialStudentsModel.setRowCount(0);
//        sessionsModel.setRowCount(0);
//        btnCreateSession.setEnabled(false);
//        btnLoadSessions.setEnabled(false);
//        if (currentExamId <= 0) return;
//
//        try {
//            Connection conn = com.college.sms.util.DBConnection.getConnection();
//            PreparedStatement pstmt = conn.prepareStatement(
//                "SELECT r.remedial_id, r.student_id, s.name, s.roll_no, r.failed_marks, r.max_marks, r.remedial_status " +
//                "FROM remedial_tracking r JOIN student s ON r.student_id = s.student_id " +
//                "WHERE r.subject_id = ? AND r.failed_exam_id = ?"
//            );
//            pstmt.setInt(1, currentSubjectId);
//            pstmt.setInt(2, currentExamId);
//            ResultSet rs = pstmt.executeQuery();
//            
//            while (rs.next()) {
//                int remedialId = rs.getInt("remedial_id");
//                int studentId = rs.getInt("student_id");
//                String studentName = rs.getString("name");
//                String rollNo = rs.getString("roll_no");
//                int failedMarks = rs.getInt("failed_marks");
//                int maxMarks = rs.getInt("max_marks");
//                String status = rs.getString("remedial_status");
//                
//                // ✅ Calculate attendance summary: "X/Y sessions"
//                int totalSessions = getTotalSessionsCount(studentId);
//                int attendedSessions = getAttendedSessionsCount(studentId);
//                String attendedSummary = attendedSessions + "/" + totalSessions + " sessions";
//                
//                Object[] row = {
//                    remedialId,
//                    studentName + " (" + rollNo + ")",
//                    rollNo,
//                    failedMarks + "/" + maxMarks,
//                    status,
//                    attendedSummary,
//                    "📅 Create | 📊 View"
//                };
//                remedialStudentsModel.addRow(row);
//            }
//            rs.close();
//            pstmt.close();
//            conn.close();
//        } catch (Exception e) { e.printStackTrace(); }
//    }
//
//    // Load sessions for selected remedial student (with marks comparison & attendance)
//    private void loadSessions() {
//        sessionsModel.setRowCount(0);
//        btnTakeAttendance.setEnabled(false);
//        btnViewProgress.setEnabled(false);
//        if (selectedRemedialId <= 0 || selectedStudentId <= 0) return;
//
//        try {
//            Connection conn = com.college.sms.util.DBConnection.getConnection();
//            PreparedStatement pstmt = conn.prepareStatement(
//                "SELECT rs.*, sa.attendance_status, sa.performance_rating, rm.new_marks " +
//                "FROM remedial_sessions rs " +
//                "LEFT JOIN session_attendance sa ON rs.session_id = sa.session_id AND sa.student_id = ? " +
//                "LEFT JOIN remedial_marks rm ON rs.session_id = rm.session_id AND rm.student_id = ? " +
//                "WHERE rs.remedial_id = ? " +
//                "ORDER BY rs.session_date DESC, rs.session_time DESC"
//            );
//            pstmt.setInt(1, selectedStudentId);
//            pstmt.setInt(2, selectedStudentId);
//            pstmt.setInt(3, selectedRemedialId);
//            ResultSet rs = pstmt.executeQuery();
//            
//            while (rs.next()) {
//                int sessionId = rs.getInt("session_id");
//                Date sessionDate = rs.getDate("session_date");
//                Time sessionTime = rs.getTime("session_time");
//                String topic = rs.getString("topic_covered");
//                String location = rs.getString("location");
//                String sessionStatus = rs.getString("session_status");
//                String attendanceStatus = rs.getString("attendance_status");
//                String rating = rs.getString("performance_rating");
//                Integer newMarks = rs.getObject("new_marks", Integer.class);
//                
//                // ✅ Marks comparison: compare with failed_marks from remedial_tracking
//                String marksDisplay = "-";
//                if (newMarks != null) {
//                    int failedMarks = getFailedMarksForStudent(selectedStudentId);
//                    if (newMarks > failedMarks) {
//                        marksDisplay = "🟢 " + newMarks + " (Improved)";
//                    } else if (newMarks == failedMarks) {
//                        marksDisplay = "🟡 " + newMarks + " (Same)";
//                    } else {
//                        marksDisplay = "🔴 " + newMarks + " (Not Improved)";
//                    }
//                } else {
//                    marksDisplay = "⚪ Enter Marks";
//                }
//                
//                // ✅ Attendance display
//                String attendanceDisplay = attendanceStatus != null ? attendanceStatus : "⏳ Not Marked";
//                
//                Object[] row = {
//                    sessionId, sessionDate, sessionTime,
//                    topic, location, sessionStatus,
//                    marksDisplay, attendanceDisplay,
//                    "✏️ Edit | 🗑️ Delete"
//                };
//                sessionsModel.addRow(row);
//            }
//            rs.close();
//            pstmt.close();
//            conn.close();
//        } catch (Exception e) { e.printStackTrace(); }
//    }
//
//    // Get failed marks for a student from remedial_tracking
//    private int getFailedMarksForStudent(int studentId) {
//        try {
//            Connection conn = com.college.sms.util.DBConnection.getConnection();
//            PreparedStatement pstmt = conn.prepareStatement("SELECT failed_marks FROM remedial_tracking WHERE student_id = ? AND subject_id = ?");
//            pstmt.setInt(1, studentId);
//            pstmt.setInt(2, currentSubjectId);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                int marks = rs.getInt("failed_marks");
//                rs.close();
//                pstmt.close();
//                conn.close();
//                return marks;
//            }
//            rs.close();
//            pstmt.close();
//            conn.close();
//        } catch (Exception e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    // ===== ACTIONS =====
//    private void createNewSession() {
//        if (selectedRemedialId <= 0 || selectedStudentId <= 0) {
//            JOptionPane.showMessageDialog(this, "Please select a student first!", "Selection Required", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        new CreateSessionDialog(facultyId, selectedRemedialId, selectedStudentId, currentMaxMarks);
//        loadSessions(); // Refresh after creation
//    }
//
//    private void takeAttendance() {
//        int row = sessionsTable.getSelectedRow();
//        if (row < 0) return;
//        int sessionId = Integer.parseInt(sessionsModel.getValueAt(row, 0).toString());
//        new TakeAttendanceDialog(sessionId, selectedRemedialId, selectedStudentId);
//        loadSessions(); // Refresh after attendance
//        loadRemedialStudents(); // Refresh attendance summary in left panel
//    }
//
//    private void viewProgress() {
//        int row = sessionsTable.getSelectedRow();
//        if (row < 0) return;
//        int sessionId = Integer.parseInt(sessionsModel.getValueAt(row, 0).toString());
//        new ProgressReportDialog(selectedRemedialId, sessionId, selectedStudentId, currentMaxMarks);
//    }
//
//    // ===== DIALOGS (Non-static inner classes) =====
//
//    // Create/Edit Session Dialog with Marks Entry
//    private class CreateSessionDialog extends JDialog {
//        private int facultyId, remedialId, studentId, maxMarks;
//        private boolean isEdit = false;
//        private int sessionIdToEdit = -1;
//        private JSpinner dateSpinner, timeSpinner;
//        private JTextField txtTopic, txtLocation, txtDuration, txtNewMarks;
//        private JTextArea txtNotes;
//        private JComboBox<String> comboStatus;
//
//        public CreateSessionDialog(int facultyId, int remedialId, int studentId, int maxMarks) {
//            super(RemedialSessionManagementUI.this, "➕ Create Remedial Session", true);
//            this.facultyId = facultyId;
//            this.remedialId = remedialId;
//            this.studentId = studentId;
//            this.maxMarks = maxMarks;
//            initComponents(false, -1);
//            setVisible(true);
//        }
//
//        // Constructor for editing existing session
//        public CreateSessionDialog(int facultyId, int remedialId, int studentId, int maxMarks, int sessionId) {
//            super(RemedialSessionManagementUI.this, "✏️ Edit Remedial Session", true);
//            this.facultyId = facultyId;
//            this.remedialId = remedialId;
//            this.studentId = studentId;
//            this.maxMarks = maxMarks;
//            this.isEdit = true;
//            this.sessionIdToEdit = sessionId;
//            initComponents(true, sessionId);
//            setVisible(true);
//        }
//
//        private void initComponents(boolean isEdit, int sessionId) {
//            setSize(500, 600);
//            setLocationRelativeTo(RemedialSessionManagementUI.this);
//            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//            setLayout(new BorderLayout(15, 15));
//            getContentPane().setBackground(new Color(245, 247, 250));
//
//            JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
//            form.setBorder(new EmptyBorder(15, 20, 15, 20));
//            form.setBackground(Color.WHITE);
//
//            form.add(new JLabel("📅 Date:"));
//            SpinnerDateModel dateModel = new SpinnerDateModel();
//            dateSpinner = new JSpinner(dateModel);
//            dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
//            form.add(dateSpinner);
//
//            form.add(new JLabel("🕐 Time:"));
//            SpinnerDateModel timeModel = new SpinnerDateModel();
//            timeSpinner = new JSpinner(timeModel);
//            timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
//            form.add(timeSpinner);
//
//            form.add(new JLabel("⏱️ Duration (min):"));
//            txtDuration = new JTextField("60");
//            form.add(txtDuration);
//
//            form.add(new JLabel("📚 Topic:"));
//            txtTopic = new JTextField();
//            form.add(txtTopic);
//
//            form.add(new JLabel("📍 Location:"));
//            txtLocation = new JTextField();
//            form.add(txtLocation);
//
//            form.add(new JLabel("🎯 New Marks (Optional):"));
//            txtNewMarks = new JTextField();
//            txtNewMarks.setToolTipText("Enter new marks to compare with failed marks");
//            form.add(txtNewMarks);
//
//            form.add(new JLabel("📊 Session Status:"));
//            comboStatus = new JComboBox<>(new String[]{"Scheduled", "Completed", "Cancelled"});
//            comboStatus.setSelectedItem("Scheduled");
//            form.add(comboStatus);
//
//            form.add(new JLabel("📝 Notes:"));
//            txtNotes = new JTextArea(3, 20);
//            txtNotes.setLineWrap(true);
//            form.add(new JScrollPane(txtNotes));
//
//            add(form, BorderLayout.CENTER);
//
//            // Load existing data if editing
//            if (isEdit && sessionId > 0) {
//                loadSessionData(sessionId);
//            }
//
//            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
//            JButton btnSave = new JButton(isEdit ? "💾 Update" : "💾 Save");
//            btnSave.setBackground(SUCCESS_COLOR);
//            btnSave.setForeground(Color.WHITE);
//            btnSave.addActionListener(e -> saveSession());
//            buttons.add(btnSave);
//
//            JButton btnCancel = new JButton("❌ Cancel");
//            btnCancel.setBackground(DANGER_COLOR);
//            btnCancel.setForeground(Color.WHITE);
//            btnCancel.addActionListener(e -> dispose());
//            buttons.add(btnCancel);
//
//            // ✅ DELETE button for edit mode
//            if (isEdit) {
//                JButton btnDelete = new JButton("🗑️ Delete");
//                btnDelete.setBackground(DANGER_COLOR);
//                btnDelete.setForeground(Color.WHITE);
//                btnDelete.addActionListener(e -> deleteSession());
//                buttons.add(btnDelete);
//            }
//
//            add(buttons, BorderLayout.SOUTH);
//        }
//
//        private void loadSessionData(int sessionId) {
//            try {
//                Connection conn = com.college.sms.util.DBConnection.getConnection();
//                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM remedial_sessions WHERE session_id = ?");
//                pstmt.setInt(1, sessionId);
//                ResultSet rs = pstmt.executeQuery();
//                if (rs.next()) {
//                    dateSpinner.setValue(rs.getDate("session_date"));
//                    timeSpinner.setValue(rs.getTime("session_time"));
//                    txtDuration.setText(String.valueOf(rs.getInt("duration_minutes")));
//                    txtTopic.setText(rs.getString("topic_covered"));
//                    txtLocation.setText(rs.getString("location"));
//                    comboStatus.setSelectedItem(rs.getString("session_status"));
//                    txtNotes.setText(rs.getString("notes"));
//                    
//                    // Load marks if exists
//                    PreparedStatement marksPstmt = conn.prepareStatement("SELECT new_marks FROM remedial_marks WHERE session_id = ? AND student_id = ?");
//                    marksPstmt.setInt(1, sessionId);
//                    marksPstmt.setInt(2, studentId);
//                    ResultSet marksRs = marksPstmt.executeQuery();
//                    if (marksRs.next()) {
//                        txtNewMarks.setText(String.valueOf(marksRs.getInt("new_marks")));
//                    }
//                    marksRs.close();
//                    marksPstmt.close();
//                }
//                rs.close();
//                pstmt.close();
//                conn.close();
//            } catch (Exception e) { e.printStackTrace(); }
//        }
//
//        private void saveSession() {
//            try {
//                RemedialSession session = new RemedialSession();
//                if (isEdit) session.setSessionId(sessionIdToEdit);
//                session.setRemedialId(remedialId);
//                session.setSessionDate(new Date(((java.util.Date) dateSpinner.getValue()).getTime()));
//                session.setSessionTime(new Time(((java.util.Date) timeSpinner.getValue()).getTime()));
//                session.setDurationMinutes(Integer.parseInt(txtDuration.getText()));
//                session.setTopicCovered(txtTopic.getText());
//                session.setLocation(txtLocation.getText());
//                session.setFacultyId(facultyId);
//                session.setNotes(txtNotes.getText());
//                session.setSessionStatus(comboStatus.getSelectedItem().toString());
//
//                boolean success;
//                if (isEdit) {
//                    success = sessionDAO.updateSession(session);
//                } else {
//                    int newSessionId = sessionDAO.createSession(session);
//                    success = newSessionId > 0;
//                    if (success) sessionIdToEdit = newSessionId;
//                }
//
//                // ✅ Auto-create marks entry if new marks provided
//                if (!txtNewMarks.getText().trim().isEmpty()) {
//                    try {
//                        int newMarks = Integer.parseInt(txtNewMarks.getText().trim());
//                        Connection conn = com.college.sms.util.DBConnection.getConnection();
//                        
//                        // Check if marks entry exists
//                        PreparedStatement checkPstmt = conn.prepareStatement("SELECT COUNT(*) FROM remedial_marks WHERE session_id = ? AND student_id = ?");
//                        checkPstmt.setInt(1, sessionIdToEdit);
//                        checkPstmt.setInt(2, studentId);
//                        ResultSet checkRs = checkPstmt.executeQuery();
//                        boolean exists = checkRs.next() && checkRs.getInt(1) > 0;
//                        checkRs.close();
//                        checkPstmt.close();
//                        
//                        if (exists) {
//                            // Update existing
//                            PreparedStatement updPstmt = conn.prepareStatement("UPDATE remedial_marks SET new_marks = ?, updated_at = CURRENT_TIMESTAMP WHERE session_id = ? AND student_id = ?");
//                            updPstmt.setInt(1, newMarks);
//                            updPstmt.setInt(2, sessionIdToEdit);
//                            updPstmt.setInt(3, studentId);
//                            updPstmt.executeUpdate();
//                            updPstmt.close();
//                        } else {
//                            // Insert new
//                            PreparedStatement insPstmt = conn.prepareStatement("INSERT INTO remedial_marks (session_id, student_id, new_marks) VALUES (?, ?, ?)");
//                            insPstmt.setInt(1, sessionIdToEdit);
//                            insPstmt.setInt(2, studentId);
//                            insPstmt.setInt(3, newMarks);
//                            insPstmt.executeUpdate();
//                            insPstmt.close();
//                        }
//                        conn.close();
//                    } catch (NumberFormatException ex) {
//                        JOptionPane.showMessageDialog(this, "Invalid marks format", "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                }
//
//                if (success) {
//                    JOptionPane.showMessageDialog(this, "✅ Session " + (isEdit ? "updated" : "created") + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                    dispose();
//                } else {
//                    JOptionPane.showMessageDialog(this, "❌ Failed to " + (isEdit ? "update" : "create") + " session", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//
//        private void deleteSession() {
//            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this session?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
//            if (confirm != JOptionPane.YES_OPTION) return;
//            
//            try {
//                if (sessionDAO.deleteSession(sessionIdToEdit)) {
//                    JOptionPane.showMessageDialog(this, "✅ Session deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                    dispose();
//                } else {
//                    JOptionPane.showMessageDialog(this, "❌ Failed to delete session", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }
//
//    // Take Attendance Dialog with Summary
//    private class TakeAttendanceDialog extends JDialog {
//        private int sessionId, remedialId, studentId;
//        private JTable attendanceTable;
//        private DefaultTableModel attendanceModel;
//        private JLabel lblAttendanceSummary;
//
//        public TakeAttendanceDialog(int sessionId, int remedialId, int studentId) {
//            super(RemedialSessionManagementUI.this, "📝 Take Attendance - Session #" + sessionId, true);
//            this.sessionId = sessionId;
//            this.remedialId = remedialId;
//            this.studentId = studentId;
//            setSize(700, 500);
//            setLocationRelativeTo(RemedialSessionManagementUI.this);
//            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//            setLayout(new BorderLayout(10, 10));
//            getContentPane().setBackground(new Color(245, 247, 250));
//
//            // Attendance summary label
//            lblAttendanceSummary = new JLabel();
//            lblAttendanceSummary.setFont(new Font("Segoe UI", Font.BOLD, 12));
//            lblAttendanceSummary.setForeground(INFO_COLOR);
//            lblAttendanceSummary.setBorder(new EmptyBorder(5, 15, 5, 15));
//            add(lblAttendanceSummary, BorderLayout.NORTH);
//
//            String[] cols = {"Student", "Roll No", "Status", "Check-In", "Rating", "Feedback"};
//            attendanceModel = new DefaultTableModel(cols, 0) {
//                public boolean isCellEditable(int r, int c) { return c >= 2; }
//            };
//            attendanceTable = new JTable(attendanceModel);
//            attendanceTable.setRowHeight(32);
//
//            // Load student and attendance summary
//            try {
//                Connection conn = com.college.sms.util.DBConnection.getConnection();
//                
//                // Get student info
//                PreparedStatement stuPstmt = conn.prepareStatement("SELECT name, roll_no FROM student WHERE student_id = ?");
//                stuPstmt.setInt(1, studentId);
//                ResultSet stuRs = stuPstmt.executeQuery();
//                String studentName = "", rollNo = "";
//                if (stuRs.next()) {
//                    studentName = stuRs.getString("name");
//                    rollNo = stuRs.getString("roll_no");
//                }
//                stuRs.close();
//                stuPstmt.close();
//                
//                // ✅ Get attendance summary: "X sessions attended out of Y total"
//                int totalSessions = getTotalSessionsCount(studentId);
//                int attendedSessions = getAttendedSessionsCount(studentId);
//                lblAttendanceSummary.setText("📊 Attendance Summary: " + attendedSessions + " sessions attended out of " + totalSessions + " total sessions for " + studentName);
//                
//                // Load existing attendance if any
//                PreparedStatement attPstmt = conn.prepareStatement(
//                    "SELECT attendance_status, check_in_time, performance_rating, faculty_feedback FROM session_attendance WHERE session_id = ? AND student_id = ?"
//                );
//                attPstmt.setInt(1, sessionId);
//                attPstmt.setInt(2, studentId);
//                ResultSet attRs = attPstmt.executeQuery();
//                
//                String status = "Present", checkIn = LocalTime.now().toString(), rating = "Good", feedback = "";
//                if (attRs.next()) {
//                    status = attRs.getString("attendance_status");
//                    Time t = attRs.getTime("check_in_time");
//                    if (t != null) checkIn = t.toString();
//                    rating = attRs.getString("performance_rating");
//                    feedback = attRs.getString("faculty_feedback");
//                }
//                attRs.close();
//                attPstmt.close();
//                
//                attendanceModel.addRow(new Object[]{studentName, rollNo, status, checkIn, rating, feedback});
//                conn.close();
//            } catch (Exception e) { e.printStackTrace(); }
//
//            add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
//
//            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
//            JButton btnSave = new JButton("💾 Save Attendance");
//            btnSave.setBackground(SUCCESS_COLOR);
//            btnSave.setForeground(Color.WHITE);
//            btnSave.addActionListener(e -> saveAttendance());
//            buttons.add(btnSave);
//
//            JButton btnAllPresent = new JButton("✅ Mark Present");
//            btnAllPresent.setBackground(INFO_COLOR);
//            btnAllPresent.setForeground(Color.WHITE);
//            btnAllPresent.addActionListener(e -> {
//                for (int i = 0; i < attendanceModel.getRowCount(); i++) {
//                    attendanceModel.setValueAt("Present", i, 2);
//                    attendanceModel.setValueAt(LocalTime.now().toString(), i, 3);
//                }
//            });
//            buttons.add(btnAllPresent);
//
//            JButton btnCancel = new JButton("❌ Cancel");
//            btnCancel.setBackground(DANGER_COLOR);
//            btnCancel.setForeground(Color.WHITE);
//            btnCancel.addActionListener(e -> dispose());
//            buttons.add(btnCancel);
//
//            add(buttons, BorderLayout.SOUTH);
//            setVisible(true);
//        }
//
//        private void saveAttendance() {
//            int saved = 0;
//            for (int i = 0; i < attendanceModel.getRowCount(); i++) {
//                try {
//                    SessionAttendance att = new SessionAttendance();
//                    att.setSessionId(sessionId);
//                    att.setStudentId(studentId);
//                    att.setAttendanceStatus(attendanceModel.getValueAt(i, 2).toString());
//                    att.setCheckInTime(Time.valueOf(attendanceModel.getValueAt(i, 3).toString()));
//                    att.setPerformanceRating(attendanceModel.getValueAt(i, 4).toString());
//                    att.setFacultyFeedback(attendanceModel.getValueAt(i, 5).toString());
//                    if (attendanceDAO.markAttendance(att)) saved++;
//                } catch (Exception e) { e.printStackTrace(); }
//            }
//            JOptionPane.showMessageDialog(this, "✅ Saved attendance for " + saved + " student(s)", "Success", JOptionPane.INFORMATION_MESSAGE);
//            dispose();
//        }
//    }
//
//    // Progress Report Dialog with Marks Comparison
//    private class ProgressReportDialog extends JDialog {
//        public ProgressReportDialog(int remedialId, int sessionId, int studentId, int maxMarks) {
//            super(RemedialSessionManagementUI.this, "📊 Progress Report", true);
//            setSize(650, 450);
//            setLocationRelativeTo(RemedialSessionManagementUI.this);
//            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//            setLayout(new BorderLayout(15, 15));
//            getContentPane().setBackground(new Color(245, 247, 250));
//
//            JTextArea report = new JTextArea();
//            report.setEditable(false);
//            report.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//            report.setBorder(new EmptyBorder(15, 20, 15, 20));
//
//            try {
//                Connection conn = com.college.sms.util.DBConnection.getConnection();
//                
//                // Load remedial record
//                PreparedStatement pstmt = conn.prepareStatement(
//                    "SELECT r.*, s.name, s.roll_no, e.exam_name as failed_exam " +
//                    "FROM remedial_tracking r JOIN student s ON r.student_id = s.student_id JOIN exam e ON r.failed_exam_id = e.exam_id " +
//                    "WHERE r.remedial_id = ?"
//                );
//                pstmt.setInt(1, remedialId);
//                ResultSet rs = pstmt.executeQuery();
//                
//                if (rs.next()) {
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("📋 Remedial Progress Report\n");
//                    sb.append("════════════════════════════\n\n");
//                    sb.append("Student: ").append(rs.getString("name")).append(" (").append(rs.getString("roll_no")).append(")\n");
//                    sb.append("Failed Exam: ").append(rs.getString("failed_exam")).append("\n");
//                    
//                    int failedMarks = rs.getInt("failed_marks");
//                    int maxM = rs.getInt("max_marks");
//                    sb.append("Failed Marks: ").append(failedMarks).append("/").append(maxM)
//                      .append(" (").append(rs.getDouble("failed_percentage")).append("%)\n\n");
//
//                    // ✅ Load new marks from remedial_marks table
//                    PreparedStatement marksPstmt = conn.prepareStatement("SELECT new_marks FROM remedial_marks WHERE session_id = ? AND student_id = ?");
//                    marksPstmt.setInt(1, sessionId);
//                    marksPstmt.setInt(2, studentId);
//                    ResultSet marksRs = marksPstmt.executeQuery();
//                    
//                    if (marksRs.next()) {
//                        int newMarks = marksRs.getInt("new_marks");
//                        sb.append("New Marks: ").append(newMarks).append("/").append(maxM).append("\n");
//                        
//                        // ✅ Compare and show improvement status
//                        if (newMarks > failedMarks) {
//                            double improvement = ((double)(newMarks - failedMarks) / maxM) * 100;
//                            sb.append("✅ Status: 🟢 Improved (+").append(String.format("%.1f", improvement)).append("%)\n");
//                        } else if (newMarks == failedMarks) {
//                            sb.append("⚠️ Status: 🟡 No Change (same marks)\n");
//                        } else {
//                            double decline = ((double)(failedMarks - newMarks) / maxM) * 100;
//                            sb.append("❌ Status: 🔴 Not Improved (-").append(String.format("%.1f", decline)).append("%)\n");
//                        }
//                    } else {
//                        sb.append("⏳ New Marks: Not entered yet\n");
//                        sb.append("💡 Tip: Enter marks in session edit to see comparison\n");
//                    }
//                    marksRs.close();
//                    marksPstmt.close();
//                    
//                    // Attendance summary
//                    int totalSessions = getTotalSessionsCount(studentId);
//                    int attendedSessions = getAttendedSessionsCount(studentId);
//                    sb.append("\n📊 Attendance: ").append(attendedSessions).append(" sessions attended out of ").append(totalSessions).append(" total sessions\n");
//                    
//                    String notes = rs.getString("faculty_notes");
//                    if (notes != null && !notes.trim().isEmpty()) {
//                        sb.append("\n📝 Faculty Notes: ").append(notes);
//                    }
//                    report.setText(sb.toString());
//                }
//                rs.close();
//                pstmt.close();
//                conn.close();
//            } catch (Exception e) {
//                report.setText("Error loading report: " + e.getMessage());
//            }
//
//            add(new JScrollPane(report), BorderLayout.CENTER);
//
//            JButton btnClose = new JButton("✅ Close");
//            btnClose.setBackground(SUCCESS_COLOR);
//            btnClose.setForeground(Color.WHITE);
//            btnClose.addActionListener(e -> dispose());
//            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
//            buttons.add(btnClose);
//            add(buttons, BorderLayout.SOUTH);
//
//            setVisible(true);
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame mock = new JFrame("Faculty Dashboard");
//            mock.setSize(1000, 650);
//            mock.setVisible(true);
//            new RemedialSessionManagementUI(1, mock);
//        });
//    }
//}

//
//package com.college.sms.ui;
//
//import com.college.sms.dao.*;
//import com.college.sms.model.*;
//
//import javax.swing.*;
//import javax.swing.border.*;
//import javax.swing.table.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.sql.*;
//import java.time.LocalTime;
//import java.util.List;
//
//public class RemedialSessionManagementUI extends JFrame {
//
//    private int facultyId;
//    private JFrame previousUI;
//
//    // Filters
//    private JComboBox<String> comboClass;
//    private JComboBox<String> comboSubject;
//    private JComboBox<String> comboExam;
//
//    // Left Panel: Remedial Students Table
//    private JTable remedialStudentsTable;
//    private DefaultTableModel remedialStudentsModel;
//
//    // Right Panel: Sessions Table
//    private JTable sessionsTable;
//    private DefaultTableModel sessionsModel;
//
//    // Buttons
//    private JButton btnBack, btnCreateSession, btnLoadSessions, btnRefresh;
//    private JButton btnEditSession, btnDeleteSession, btnTakeAttendance, btnEnterMarks, btnViewProgress;
//
//    // DAOs
//    private ClassDAO classDAO         = new ClassDAO();
//    private SubjectDAO subjectDAO     = new SubjectDAO();
//    private ExamDAO examDAO           = new ExamDAO();
//    private RemedialDAO remedialDAO   = new RemedialDAO();
//    private RemedialSessionDAO sessionDAO = new RemedialSessionDAO();
//    private SessionAttendanceDAO attendanceDAO = new SessionAttendanceDAO();
//    private StudentDAO studentDAO     = new StudentDAO();
//
//    // State
//    private int currentClassId   = -1;
//    private int currentSubjectId = -1;
//    private int currentExamId    = -1;
//    private int currentMaxMarks  = 0;
//    private int currentPassMarks = 0;
//    private int selectedRemedialId  = -1;
//    private int selectedStudentId   = -1;
//    private String selectedStudentName = "";
//
//    // Status bar
//    private JLabel statusLabel;
//
//    // Colors
//    private static final Color PRIMARY_COLOR    = new Color(52, 73, 94);
//    private static final Color PRIMARY_LIGHT    = new Color(41, 128, 185);
//    private static final Color SUCCESS_COLOR    = new Color(39, 174, 96);
//    private static final Color WARNING_COLOR    = new Color(243, 156, 18);
//    private static final Color DANGER_COLOR     = new Color(231, 76, 60);
//    private static final Color INFO_COLOR       = new Color(52, 152, 219);
//    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
//    private static final Color CARD_COLOR       = Color.WHITE;
//    private static final Color HEADER_BG        = new Color(44, 62, 80);
//
//    // ============================================================
//    public RemedialSessionManagementUI(int facultyId, JFrame previousUI) {
//        this.facultyId  = facultyId;
//        this.previousUI = previousUI;
//        initComponents();
//        loadClasses();
//        setVisible(true);
//    }
//
//    // ============================================================
//    // UI CONSTRUCTION
//    // ============================================================
//    private void initComponents() {
//        setTitle("📅 Remedial Session Management | Faculty ID: " + facultyId);
//        setSize(1450, 900);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(new BorderLayout(0, 0));
//        getContentPane().setBackground(BACKGROUND_COLOR);
//
//        add(buildNorthPanel(), BorderLayout.NORTH);
//        add(buildMainSplitPane(), BorderLayout.CENTER);
//        add(buildStatusBar(), BorderLayout.SOUTH);
//    }
//
//    // ---------- NORTH (header + filters) ----------
//    private JPanel buildNorthPanel() {
//        JPanel northWrapper = new JPanel(new BorderLayout());
//        northWrapper.setBackground(BACKGROUND_COLOR);
//
//        // --- Header ---
//        JPanel header = new JPanel(new BorderLayout());
//        header.setBackground(HEADER_BG);
//        header.setPreferredSize(new Dimension(0, 62));
//        header.setBorder(new EmptyBorder(0, 20, 0, 20));
//
//        JLabel title = new JLabel("📅 Remedial Session Management");
//        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
//        title.setForeground(Color.WHITE);
//        header.add(title, BorderLayout.WEST);
//
//        btnBack = createBtn("⇦ Back to Dashboard", PRIMARY_LIGHT, Color.WHITE);
//        btnBack.setPreferredSize(new Dimension(190, 38));
//        btnBack.addActionListener(e -> navigateBack());
//        header.add(btnBack, BorderLayout.EAST);
//
//        northWrapper.add(header, BorderLayout.NORTH);
//
//        // --- Filter bar ---
//        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
//        filterPanel.setBackground(CARD_COLOR);
//        filterPanel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)),
//                new EmptyBorder(4, 15, 4, 15)));
//
//        filterPanel.add(label("🏫 Class:"));
//        comboClass = createComboBox(200);
//        comboClass.addActionListener(e -> {
//            String sel = (String) comboClass.getSelectedItem();
//            if (sel != null && !sel.contains("--")) {
//                currentClassId = Integer.parseInt(sel.split(" - ")[0].trim());
//                loadSubjects();
//            }
//        });
//        filterPanel.add(comboClass);
//
//        filterPanel.add(label("📚 Subject:"));
//        comboSubject = createComboBox(200);
//        comboSubject.addActionListener(e -> {
//            String sel = (String) comboSubject.getSelectedItem();
//            if (sel != null && !sel.contains("--")) {
//                currentSubjectId = Integer.parseInt(sel.split(" - ")[0].trim());
//                loadExams();
//            }
//        });
//        filterPanel.add(comboSubject);
//
//        filterPanel.add(label("📝 Exam:"));
//        comboExam = createComboBox(260);
//        comboExam.addActionListener(e -> {
//            String sel = (String) comboExam.getSelectedItem();
//            if (sel != null && !sel.contains("--")) {
//                currentExamId = Integer.parseInt(sel.split(":")[0].trim());
//                try {
//                    Exam ex = examDAO.getExamById(currentExamId);
//                    currentMaxMarks  = ex.getMaxMarks();
//                    currentPassMarks = ex.getPassMarks();
//                } catch (Exception ex2) { ex2.printStackTrace(); }
//                loadRemedialStudents();
//            }
//        });
//        filterPanel.add(comboExam);
//
//        btnRefresh = createBtn("🔄 Refresh", new Color(127, 140, 141), Color.WHITE);
//        btnRefresh.setPreferredSize(new Dimension(100, 34));
//        btnRefresh.addActionListener(e -> { loadRemedialStudents(); loadSessions(); });
//        filterPanel.add(btnRefresh);
//
//        northWrapper.add(filterPanel, BorderLayout.CENTER);
//        return northWrapper;
//    }
//
//    // ---------- CENTER (split pane) ----------
//    private JSplitPane buildMainSplitPane() {
//        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//        split.setDividerLocation(520);
//        split.setResizeWeight(0.36);
//        split.setBorder(new EmptyBorder(8, 8, 8, 8));
//        split.setBackground(BACKGROUND_COLOR);
//
//        // ===== LEFT: Remedial Students =====
//        JPanel left = new JPanel(new BorderLayout(0, 6));
//        left.setBackground(CARD_COLOR);
//        left.setBorder(cardBorder("👥 Students in Remedial Tracking"));
//
//        // Table: ID hidden, Student, Roll No, Failed Marks, Status, Attendance
//        remedialStudentsModel = new DefaultTableModel(
//                new String[]{"Remedial ID", "Student Name", "Roll No", "Failed Marks", "Remedial Status", "Attendance"}, 0) {
//            public boolean isCellEditable(int r, int c) { return false; }
//        };
//        remedialStudentsTable = new JTable(remedialStudentsModel);
//        styleTable(remedialStudentsTable);
//        remedialStudentsTable.getColumnModel().getColumn(0).setMinWidth(0);
//        remedialStudentsTable.getColumnModel().getColumn(0).setMaxWidth(0);
//        remedialStudentsTable.getColumnModel().getColumn(0).setWidth(0);
//        remedialStudentsTable.getColumnModel().getColumn(1).setPreferredWidth(160);
//        remedialStudentsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
//        remedialStudentsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
//        remedialStudentsTable.getColumnModel().getColumn(4).setPreferredWidth(110);
//        remedialStudentsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
//
//        remedialStudentsTable.getSelectionModel().addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) {
//                int row = remedialStudentsTable.getSelectedRow();
//                if (row >= 0) {
//                    selectedRemedialId   = (int) remedialStudentsModel.getValueAt(row, 0);
//                    selectedStudentId    = getStudentIdFromRemedial(selectedRemedialId);
//                    selectedStudentName  = remedialStudentsModel.getValueAt(row, 1).toString();
//                    btnCreateSession.setEnabled(true);
//                    btnLoadSessions.setEnabled(true);
//                    sessionsModel.setRowCount(0);
//                    updateStatus("Selected: " + selectedStudentName + " | Click '🔄 Load Sessions' to view sessions.", INFO_COLOR);
//                }
//            }
//        });
//
//        left.add(new JScrollPane(remedialStudentsTable), BorderLayout.CENTER);
//
//        // Left bottom action
//        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
//        leftActions.setBackground(new Color(248, 249, 250));
//        leftActions.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
//
//        btnCreateSession = createBtn("➕ New Session", SUCCESS_COLOR, Color.WHITE);
//        btnCreateSession.setEnabled(false);
//        btnCreateSession.setToolTipText("Create a new remedial session for selected student");
//        btnCreateSession.addActionListener(e -> openCreateSessionDialog());
//        leftActions.add(btnCreateSession);
//
//        btnLoadSessions = createBtn("🔄 Load Sessions", INFO_COLOR, Color.WHITE);
//        btnLoadSessions.setEnabled(false);
//        btnLoadSessions.addActionListener(e -> loadSessions());
//        leftActions.add(btnLoadSessions);
//
//        left.add(leftActions, BorderLayout.SOUTH);
//        split.setLeftComponent(left);
//
//        // ===== RIGHT: Sessions with full CRUD =====
//        JPanel right = new JPanel(new BorderLayout(0, 6));
//        right.setBackground(CARD_COLOR);
//        right.setBorder(cardBorder("📋 Sessions — CRUD | Marks | Attendance"));
//
//        // Columns: ID hidden | Date | Time | Topic | Status | Marks (vs failed) | Attendance
//        sessionsModel = new DefaultTableModel(
//                new String[]{"Session ID", "Date", "Time", "Topic", "Location", "Status", "Marks vs Failed", "Attendance"}, 0) {
//            public boolean isCellEditable(int r, int c) { return false; }
//        };
//        sessionsTable = new JTable(sessionsModel);
//        styleSessionsTable(sessionsTable);
//        sessionsTable.getColumnModel().getColumn(0).setMinWidth(0);
//        sessionsTable.getColumnModel().getColumn(0).setMaxWidth(0);
//        sessionsTable.getColumnModel().getColumn(0).setWidth(0);
//        sessionsTable.getColumnModel().getColumn(1).setPreferredWidth(90);
//        sessionsTable.getColumnModel().getColumn(2).setPreferredWidth(70);
//        sessionsTable.getColumnModel().getColumn(3).setPreferredWidth(160);
//        sessionsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
//        sessionsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
//        sessionsTable.getColumnModel().getColumn(6).setPreferredWidth(150);
//        sessionsTable.getColumnModel().getColumn(7).setPreferredWidth(110);
//
//        sessionsTable.getSelectionModel().addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) {
//                boolean hasRow = sessionsTable.getSelectedRow() >= 0;
//                btnEditSession.setEnabled(hasRow);
//                btnDeleteSession.setEnabled(hasRow);
//                btnTakeAttendance.setEnabled(hasRow);
//                btnEnterMarks.setEnabled(hasRow);
//                btnViewProgress.setEnabled(hasRow);
//            }
//        });
//
//        right.add(new JScrollPane(sessionsTable), BorderLayout.CENTER);
//
//        // Right action bar — full CRUD + attendance + marks + progress
//        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 6));
//        rightActions.setBackground(new Color(248, 249, 250));
//        rightActions.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
//
//        btnEditSession = createBtn("✏️ Edit", new Color(41, 128, 185), Color.WHITE);
//        btnEditSession.setEnabled(false);
//        btnEditSession.setToolTipText("Edit selected session details");
//        btnEditSession.addActionListener(e -> openEditSessionDialog());
//        rightActions.add(btnEditSession);
//
//        btnDeleteSession = createBtn("🗑️ Delete", DANGER_COLOR, Color.WHITE);
//        btnDeleteSession.setEnabled(false);
//        btnDeleteSession.setToolTipText("Permanently delete selected session");
//        btnDeleteSession.addActionListener(e -> deleteSelectedSession());
//        rightActions.add(btnDeleteSession);
//
//        rightActions.add(new JSeparator(JSeparator.VERTICAL) {{ setPreferredSize(new Dimension(2, 28)); }});
//
//        btnTakeAttendance = createBtn("📝 Attendance", WARNING_COLOR, Color.WHITE);
//        btnTakeAttendance.setEnabled(false);
//        btnTakeAttendance.setToolTipText("Mark Present / Absent / Late for this session");
//        btnTakeAttendance.addActionListener(e -> openAttendanceDialog());
//        rightActions.add(btnTakeAttendance);
//
//        btnEnterMarks = createBtn("🏅 Enter Marks", new Color(142, 68, 173), Color.WHITE);
//        btnEnterMarks.setEnabled(false);
//        btnEnterMarks.setToolTipText("Enter new marks & compare with original failed marks");
//        btnEnterMarks.addActionListener(e -> openEnterMarksDialog());
//        rightActions.add(btnEnterMarks);
//
//        btnViewProgress = createBtn("📊 Progress", new Color(22, 160, 133), Color.WHITE);
//        btnViewProgress.setEnabled(false);
//        btnViewProgress.setToolTipText("View detailed progress report with marks comparison");
//        btnViewProgress.addActionListener(e -> openProgressDialog());
//        rightActions.add(btnViewProgress);
//
//        right.add(rightActions, BorderLayout.SOUTH);
//        split.setRightComponent(right);
//
//        return split;
//    }
//
//    // ---------- STATUS BAR ----------
//    private JLabel buildStatusBar() {
//        statusLabel = new JLabel("Ready. Select Class → Subject → Exam to begin.");
//        statusLabel.setBorder(new EmptyBorder(6, 15, 6, 15));
//        statusLabel.setOpaque(true);
//        statusLabel.setBackground(new Color(236, 240, 241));
//        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        statusLabel.setForeground(new Color(60, 60, 60));
//        return statusLabel;
//    }
//
//    // ============================================================
//    // NAVIGATION
//    // ============================================================
//    private void navigateBack() {
//        dispose();
//        SwingUtilities.invokeLater(() -> {
//            if (previousUI != null) {
//                previousUI.setVisible(true);
//                previousUI.toFront();
//                previousUI.requestFocus();
//            } else {
//                new FacultyDashboard(facultyId).setVisible(true);
//            }
//        });
//    }
//
//    // ============================================================
//    // DATA LOADING
//    // ============================================================
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        comboSubject.removeAllItems();
//        comboExam.removeAllItems();
//        remedialStudentsModel.setRowCount(0);
//        sessionsModel.setRowCount(0);
//        try {
//            List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
//            if (classes.isEmpty()) { comboClass.addItem("-- No classes --"); return; }
//            for (String[] c : classes) comboClass.addItem(c[0] + " - " + c[1]);
//        } catch (Exception e) { comboClass.addItem("-- Error --"); }
//    }
//
//    private void loadSubjects() {
//        comboSubject.removeAllItems();
//        comboExam.removeAllItems();
//        remedialStudentsModel.setRowCount(0);
//        sessionsModel.setRowCount(0);
//        try {
//            List<Subject> subs = subjectDAO.getSubjectsByClassAndFaculty(currentClassId, facultyId);
//            if (subs.isEmpty()) { comboSubject.addItem("-- No subjects --"); return; }
//            for (Subject s : subs) comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
//        } catch (Exception e) { comboSubject.addItem("-- Error --"); }
//    }
//
//    private void loadExams() {
//        comboExam.removeAllItems();
//        remedialStudentsModel.setRowCount(0);
//        sessionsModel.setRowCount(0);
//        try {
//            List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, currentClassId, currentSubjectId);
//            if (exams.isEmpty()) { comboExam.addItem("-- No exams --"); return; }
//            for (Exam ex : exams)
//                comboExam.addItem(ex.getExamId() + ":" + ex.getExamName() +
//                        " (Max:" + ex.getMaxMarks() + " Pass:" + ex.getPassMarks() + ")");
//        } catch (Exception e) { comboExam.addItem("-- Error --"); }
//    }
//
//    private void loadRemedialStudents() {
//        remedialStudentsModel.setRowCount(0);
//        sessionsModel.setRowCount(0);
//        selectedRemedialId = -1; selectedStudentId = -1;
//        btnCreateSession.setEnabled(false);
//        btnLoadSessions.setEnabled(false);
//        disableSessionButtons();
//        if (currentExamId <= 0) return;
//
//        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//            PreparedStatement ps = conn.prepareStatement(
//                "SELECT r.remedial_id, r.student_id, s.name, s.roll_no, r.failed_marks, r.max_marks, r.remedial_status " +
//                "FROM remedial_tracking r JOIN student s ON r.student_id = s.student_id " +
//                "WHERE r.subject_id = ? AND r.failed_exam_id = ? ORDER BY s.name");
//            ps.setInt(1, currentSubjectId);
//            ps.setInt(2, currentExamId);
//            ResultSet rs = ps.executeQuery();
//            int count = 0;
//            while (rs.next()) {
//                int remedialId  = rs.getInt("remedial_id");
//                int studentId   = rs.getInt("student_id");
//                String name     = rs.getString("name");
//                String rollNo   = rs.getString("roll_no");
//                int failedMarks = rs.getInt("failed_marks");
//                int maxM        = rs.getInt("max_marks");
//                String status   = rs.getString("remedial_status");
//
//                int total    = getTotalSessions(studentId);
//                int attended = getAttendedSessions(studentId);
//                String attSummary = attended + "/" + total;
//
//                remedialStudentsModel.addRow(new Object[]{
//                    remedialId, name, rollNo,
//                    failedMarks + "/" + maxM, status, attSummary
//                });
//                count++;
//            }
//            updateStatus("Loaded " + count + " student(s) in remedial for selected exam.", SUCCESS_COLOR);
//        } catch (Exception e) {
//            e.printStackTrace();
//            updateStatus("❌ Error loading students: " + e.getMessage(), DANGER_COLOR);
//        }
//    }
//
//    private void loadSessions() {
//        sessionsModel.setRowCount(0);
//        disableSessionButtons();
//        if (selectedRemedialId <= 0 || selectedStudentId <= 0) {
//            updateStatus("⚠️ Select a student from the left panel first.", WARNING_COLOR);
//            return;
//        }
//        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//            PreparedStatement ps = conn.prepareStatement(
//                "SELECT rs.session_id, rs.session_date, rs.session_time, rs.topic_covered, rs.location, rs.session_status, " +
//                "       sa.attendance_status, rm.new_marks " +
//                "FROM remedial_sessions rs " +
//                "LEFT JOIN session_attendance sa ON rs.session_id = sa.session_id AND sa.student_id = ? " +
//                "LEFT JOIN remedial_marks rm ON rs.session_id = rm.session_id AND rm.student_id = ? " +
//                "WHERE rs.remedial_id = ? " +
//                "ORDER BY rs.session_date DESC, rs.session_time DESC");
//            ps.setInt(1, selectedStudentId);
//            ps.setInt(2, selectedStudentId);
//            ps.setInt(3, selectedRemedialId);
//            ResultSet rs = ps.executeQuery();
//
//            int failedMarks = getFailedMarks(selectedStudentId);
//            int count = 0;
//            while (rs.next()) {
//                int    sessionId   = rs.getInt("session_id");
//                Date   date        = rs.getDate("session_date");
//                Time   time        = rs.getTime("session_time");
//                String topic       = rs.getString("topic_covered");
//                String location    = rs.getString("location");
//                String sessStatus  = rs.getString("session_status");
//                String attStatus   = rs.getString("attendance_status");
//                Integer newMarks   = rs.getObject("new_marks", Integer.class);
//
//                // --- Marks comparison column ---
//                String marksDisplay;
//                if (newMarks == null) {
//                    marksDisplay = "⏳ Not Entered";
//                } else if (newMarks > failedMarks) {
//                    int delta = newMarks - failedMarks;
//                    marksDisplay = "🟢 " + newMarks + "/" + currentMaxMarks + " (+" + delta + " improved)";
//                } else if (newMarks == failedMarks) {
//                    marksDisplay = "🟡 " + newMarks + "/" + currentMaxMarks + " (no change)";
//                } else {
//                    int delta = failedMarks - newMarks;
//                    marksDisplay = "🔴 " + newMarks + "/" + currentMaxMarks + " (-" + delta + " dropped)";
//                }
//
//                // --- Attendance column ---
//                String attDisplay = (attStatus == null) ? "⏳ Not Marked" : attStatus;
//
//                sessionsModel.addRow(new Object[]{
//                    sessionId, date, time, topic, location, sessStatus, marksDisplay, attDisplay
//                });
//                count++;
//            }
//            updateStatus("Loaded " + count + " session(s) for " + selectedStudentName, INFO_COLOR);
//        } catch (Exception e) {
//            e.printStackTrace();
//            updateStatus("❌ Error loading sessions: " + e.getMessage(), DANGER_COLOR);
//        }
//    }
//
//    // ============================================================
//    // CRUD ACTIONS
//    // ============================================================
//
//    // --- CREATE ---
//    private void openCreateSessionDialog() {
//        if (selectedRemedialId <= 0 || selectedStudentId <= 0) {
//            JOptionPane.showMessageDialog(this, "Select a student first!", "No Selection", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        new SessionDialog(this, false, -1).setVisible(true);
//        loadSessions();
//        loadRemedialStudents();
//    }
//
//    // --- EDIT ---
//    private void openEditSessionDialog() {
//        int row = sessionsTable.getSelectedRow();
//        if (row < 0) return;
//        int sessionId = (int) sessionsModel.getValueAt(row, 0);
//        new SessionDialog(this, true, sessionId).setVisible(true);
//        loadSessions();
//        loadRemedialStudents();
//    }
//
//    // --- DELETE ---
//    private void deleteSelectedSession() {
//        int row = sessionsTable.getSelectedRow();
//        if (row < 0) return;
//        int sessionId = (int) sessionsModel.getValueAt(row, 0);
//        String topic  = (String) sessionsModel.getValueAt(row, 3);
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//            "<html><b>🗑️ Delete Session?</b><br><br>" +
//            "Topic: <b>" + topic + "</b><br><br>" +
//            "⚠️ This will permanently delete:<br>" +
//            "&nbsp;&nbsp;• The session record<br>" +
//            "&nbsp;&nbsp;• All attendance records for this session<br>" +
//            "&nbsp;&nbsp;• All marks entries for this session<br><br>" +
//            "<b>This action CANNOT be undone!</b></html>",
//            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
//
//        if (confirm != JOptionPane.YES_OPTION) return;
//
//        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//            // Delete child records first (FK constraints)
//            conn.prepareStatement("DELETE FROM session_attendance WHERE session_id = " + sessionId).executeUpdate();
//            conn.prepareStatement("DELETE FROM remedial_marks WHERE session_id = " + sessionId).executeUpdate();
//            conn.prepareStatement("DELETE FROM remedial_sessions WHERE session_id = " + sessionId).executeUpdate();
//
//            updateStatus("✅ Session deleted successfully.", SUCCESS_COLOR);
//            JOptionPane.showMessageDialog(this, "✅ Session deleted!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
//            loadSessions();
//            loadRemedialStudents();
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error deleting session:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    // --- ATTENDANCE ---
//    private void openAttendanceDialog() {
//        int row = sessionsTable.getSelectedRow();
//        if (row < 0) return;
//        int sessionId = (int) sessionsModel.getValueAt(row, 0);
//        new AttendanceDialog(this, sessionId).setVisible(true);
//        loadSessions();
//        loadRemedialStudents();
//    }
//
//    // --- ENTER MARKS ---
//    private void openEnterMarksDialog() {
//        int row = sessionsTable.getSelectedRow();
//        if (row < 0) return;
//        int sessionId = (int) sessionsModel.getValueAt(row, 0);
//        new EnterMarksDialog(this, sessionId).setVisible(true);
//        loadSessions();
//    }
//
//    // --- PROGRESS ---
//    private void openProgressDialog() {
//        int row = sessionsTable.getSelectedRow();
//        if (row < 0) return;
//        int sessionId = (int) sessionsModel.getValueAt(row, 0);
//        new ProgressDialog(this, sessionId).setVisible(true);
//    }
//
//    // ============================================================
//    // INNER DIALOG: Create / Edit Session
//    // ============================================================
//    private class SessionDialog extends JDialog {
//        private final boolean isEdit;
//        private final int sessionId;
//
//        private JSpinner dateSpinner, timeSpinner;
//        private JTextField txtTopic, txtLocation, txtDuration;
//        private JTextArea txtNotes;
//        private JComboBox<String> comboStatus;
//
//        SessionDialog(JFrame owner, boolean isEdit, int sessionId) {
//            super(owner, isEdit ? "✏️ Edit Session" : "➕ Create New Session", true);
//            this.isEdit    = isEdit;
//            this.sessionId = sessionId;
//            buildUI();
//            if (isEdit) loadData();
//        }
//
//        private void buildUI() {
//            setSize(520, 520);
//            setLocationRelativeTo(getOwner());
//            setLayout(new BorderLayout(10, 10));
//            getContentPane().setBackground(new Color(248, 249, 250));
//
//            // Title strip
//            JPanel titleStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
//            titleStrip.setBackground(isEdit ? new Color(41, 128, 185) : SUCCESS_COLOR);
//            JLabel lbl = new JLabel(isEdit ? "✏️  Edit Remedial Session" : "➕  Create Remedial Session");
//            lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
//            lbl.setForeground(Color.WHITE);
//            titleStrip.add(lbl);
//            add(titleStrip, BorderLayout.NORTH);
//
//            // Form
//            JPanel form = new JPanel(new GridBagLayout());
//            form.setBackground(CARD_COLOR);
//            form.setBorder(new EmptyBorder(15, 25, 10, 25));
//            GridBagConstraints gbc = new GridBagConstraints();
//            gbc.insets = new Insets(6, 6, 6, 6);
//            gbc.fill = GridBagConstraints.HORIZONTAL;
//
//            // Date
//            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
//            form.add(formLabel("📅 Date:"), gbc);
//            gbc.gridx = 1; gbc.weightx = 0.7;
//            dateSpinner = new JSpinner(new SpinnerDateModel());
//            dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
//            dateSpinner.setPreferredSize(new Dimension(180, 32));
//            form.add(dateSpinner, gbc);
//
//            // Time
//            gbc.gridx = 0; gbc.gridy = 1;
//            form.add(formLabel("🕐 Time:"), gbc);
//            gbc.gridx = 1;
//            timeSpinner = new JSpinner(new SpinnerDateModel());
//            timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
//            form.add(timeSpinner, gbc);
//
//            // Duration
//            gbc.gridx = 0; gbc.gridy = 2;
//            form.add(formLabel("⏱️ Duration (min):"), gbc);
//            gbc.gridx = 1;
//            txtDuration = styledField("60");
//            form.add(txtDuration, gbc);
//
//            // Topic
//            gbc.gridx = 0; gbc.gridy = 3;
//            form.add(formLabel("📚 Topic:"), gbc);
//            gbc.gridx = 1;
//            txtTopic = styledField("");
//            form.add(txtTopic, gbc);
//
//            // Location
//            gbc.gridx = 0; gbc.gridy = 4;
//            form.add(formLabel("📍 Location:"), gbc);
//            gbc.gridx = 1;
//            txtLocation = styledField("");
//            form.add(txtLocation, gbc);
//
//            // Status
//            gbc.gridx = 0; gbc.gridy = 5;
//            form.add(formLabel("📊 Status:"), gbc);
//            gbc.gridx = 1;
//            comboStatus = new JComboBox<>(new String[]{"Scheduled", "Completed", "Cancelled"});
//            comboStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//            form.add(comboStatus, gbc);
//
//            // Notes
//            gbc.gridx = 0; gbc.gridy = 6; gbc.anchor = GridBagConstraints.NORTHWEST;
//            form.add(formLabel("📝 Notes:"), gbc);
//            gbc.gridx = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
//            txtNotes = new JTextArea(3, 20);
//            txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//            txtNotes.setLineWrap(true);
//            form.add(new JScrollPane(txtNotes), gbc);
//
//            add(form, BorderLayout.CENTER);
//
//            // Buttons
//            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
//            btnPanel.setBackground(new Color(248, 249, 250));
//
//            JButton btnSave = createBtn(isEdit ? "💾 Update Session" : "💾 Create Session",
//                    isEdit ? new Color(41, 128, 185) : SUCCESS_COLOR, Color.WHITE);
//            btnSave.addActionListener(e -> save());
//            btnPanel.add(btnSave);
//
//            JButton btnCancel = createBtn("❌ Cancel", new Color(149, 165, 166), Color.WHITE);
//            btnCancel.addActionListener(e -> dispose());
//            btnPanel.add(btnCancel);
//
//            add(btnPanel, BorderLayout.SOUTH);
//        }
//
//        private void loadData() {
//            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//                PreparedStatement ps = conn.prepareStatement("SELECT * FROM remedial_sessions WHERE session_id = ?");
//                ps.setInt(1, sessionId);
//                ResultSet rs = ps.executeQuery();
//                if (rs.next()) {
//                    dateSpinner.setValue(rs.getDate("session_date"));
//                    timeSpinner.setValue(rs.getTime("session_time"));
//                    txtDuration.setText(String.valueOf(rs.getInt("duration_minutes")));
//                    txtTopic.setText(rs.getString("topic_covered"));
//                    txtLocation.setText(rs.getString("location"));
//                    comboStatus.setSelectedItem(rs.getString("session_status"));
//                    String notes = rs.getString("notes");
//                    if (notes != null) txtNotes.setText(notes);
//                }
//            } catch (Exception e) { e.printStackTrace(); }
//        }
//
//        private void save() {
//            String topic    = txtTopic.getText().trim();
//            String location = txtLocation.getText().trim();
//            if (topic.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Topic is required!", "Validation", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//                if (isEdit) {
//                    PreparedStatement ps = conn.prepareStatement(
//                        "UPDATE remedial_sessions SET session_date=?, session_time=?, duration_minutes=?, " +
//                        "topic_covered=?, location=?, session_status=?, notes=? WHERE session_id=?");
//                    ps.setDate(1, new Date(((java.util.Date) dateSpinner.getValue()).getTime()));
//                    ps.setTime(2, new Time(((java.util.Date) timeSpinner.getValue()).getTime()));
//                    ps.setInt(3, Integer.parseInt(txtDuration.getText().trim().isEmpty() ? "60" : txtDuration.getText().trim()));
//                    ps.setString(4, topic);
//                    ps.setString(5, location);
//                    ps.setString(6, (String) comboStatus.getSelectedItem());
//                    ps.setString(7, txtNotes.getText().trim());
//                    ps.setInt(8, sessionId);
//                    ps.executeUpdate();
//                    updateStatus("✅ Session updated.", SUCCESS_COLOR);
//                } else {
//                    PreparedStatement ps = conn.prepareStatement(
//                        "INSERT INTO remedial_sessions (remedial_id, session_date, session_time, duration_minutes, " +
//                        "topic_covered, location, faculty_id, session_status, notes) VALUES (?,?,?,?,?,?,?,?,?)");
//                    ps.setInt(1, selectedRemedialId);
//                    ps.setDate(2, new Date(((java.util.Date) dateSpinner.getValue()).getTime()));
//                    ps.setTime(3, new Time(((java.util.Date) timeSpinner.getValue()).getTime()));
//                    ps.setInt(4, Integer.parseInt(txtDuration.getText().trim().isEmpty() ? "60" : txtDuration.getText().trim()));
//                    ps.setString(5, topic);
//                    ps.setString(6, location);
//                    ps.setInt(7, facultyId);
//                    ps.setString(8, (String) comboStatus.getSelectedItem());
//                    ps.setString(9, txtNotes.getText().trim());
//                    ps.executeUpdate();
//                    updateStatus("✅ New session created.", SUCCESS_COLOR);
//                }
//                JOptionPane.showMessageDialog(this,
//                    "✅ Session " + (isEdit ? "updated" : "created") + " successfully!",
//                    "Success", JOptionPane.INFORMATION_MESSAGE);
//                dispose();
//            } catch (Exception e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }
//
//    // ============================================================
//    // INNER DIALOG: Attendance — mark Present / Absent / Late + rating + feedback
//    // ============================================================
//    private class AttendanceDialog extends JDialog {
//        private final int sessionId;
//
//        // Per-student controls
//        private JComboBox<String> comboAttStatus;
//        private JTextField txtCheckIn;
//        private JComboBox<String> comboRating;
//        private JTextField txtFeedback;
//        private JLabel lblStudentInfo;
//
//        AttendanceDialog(JFrame owner, int sessionId) {
//            super(owner, "📝 Attendance — Session #" + sessionId, true);
//            this.sessionId = sessionId;
//            buildUI();
//        }
//
//        private void buildUI() {
//            setSize(560, 440);
//            setLocationRelativeTo(getOwner());
//            setLayout(new BorderLayout(10, 10));
//            getContentPane().setBackground(new Color(248, 249, 250));
//
//            // Header strip
//            JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
//            strip.setBackground(WARNING_COLOR);
//            JLabel hdr = new JLabel("📝  Mark Attendance for Session #" + sessionId);
//            hdr.setFont(new Font("Segoe UI", Font.BOLD, 15));
//            hdr.setForeground(Color.WHITE);
//            strip.add(hdr);
//            add(strip, BorderLayout.NORTH);
//
//            // Student info
//            lblStudentInfo = new JLabel();
//            lblStudentInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
//            lblStudentInfo.setForeground(PRIMARY_COLOR);
//            lblStudentInfo.setBorder(new EmptyBorder(8, 20, 4, 20));
//
//            // Attendance history summary
//            int total    = getTotalSessions(selectedStudentId);
//            int attended = getAttendedSessions(selectedStudentId);
//            double pct   = total > 0 ? (attended * 100.0 / total) : 0;
//            lblStudentInfo.setText("<html>Student: <b>" + selectedStudentName + "</b> &nbsp;|&nbsp; " +
//                "Attendance: <b>" + attended + "/" + total + " sessions (" +
//                String.format("%.0f", pct) + "%)</b></html>");
//            add(lblStudentInfo, BorderLayout.AFTER_LAST_LINE); // placeholder
//
//            // Form panel
//            JPanel form = new JPanel(new GridBagLayout());
//            form.setBackground(CARD_COLOR);
//            form.setBorder(new CompoundBorder(
//                new EmptyBorder(8, 15, 8, 15),
//                new CompoundBorder(
//                    BorderFactory.createLineBorder(new Color(220, 220, 220)),
//                    new EmptyBorder(12, 15, 12, 15)
//                )
//            ));
//            GridBagConstraints g = new GridBagConstraints();
//            g.insets = new Insets(7, 8, 7, 8);
//            g.fill = GridBagConstraints.HORIZONTAL;
//            g.weightx = 0.4;
//
//            // Row 0: student info label inside form
//            g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
//            JLabel infoLbl = new JLabel("<html>Student: <b>" + selectedStudentName + "</b> &nbsp;&nbsp; " +
//                "Prev Attendance: <b>" + attended + "/" + total + "</b></html>");
//            infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//            infoLbl.setForeground(new Color(52, 73, 94));
//            form.add(infoLbl, g);
//            g.gridwidth = 1;
//
//            // Status
//            g.gridx = 0; g.gridy = 1; g.weightx = 0.3;
//            form.add(formLabel("📌 Status:"), g);
//            g.gridx = 1; g.weightx = 0.7;
//            comboAttStatus = new JComboBox<>(new String[]{"Present", "Absent", "Late"});
//            comboAttStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//            styleComboColors(comboAttStatus);
//            form.add(comboAttStatus, g);
//
//            // Check-in time
//            g.gridx = 0; g.gridy = 2;
//            form.add(formLabel("🕐 Check-In Time:"), g);
//            g.gridx = 1;
//            txtCheckIn = styledField(LocalTime.now().toString().substring(0, 5));
//            form.add(txtCheckIn, g);
//
//            // Performance rating
//            g.gridx = 0; g.gridy = 3;
//            form.add(formLabel("⭐ Performance:"), g);
//            g.gridx = 1;
//            comboRating = new JComboBox<>(new String[]{"Excellent", "Good", "Average", "Poor"});
//            comboRating.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//            form.add(comboRating, g);
//
//            // Feedback
//            g.gridx = 0; g.gridy = 4;
//            form.add(formLabel("💬 Feedback:"), g);
//            g.gridx = 1;
//            txtFeedback = styledField("");
//            form.add(txtFeedback, g);
//
//            add(form, BorderLayout.CENTER);
//
//            // Load existing attendance if already marked
//            loadExistingAttendance();
//
//            // Buttons
//            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
//            btnPanel.setBackground(new Color(248, 249, 250));
//
//            JButton btnPresent = createBtn("✅ Mark Present", SUCCESS_COLOR, Color.WHITE);
//            btnPresent.addActionListener(e -> { comboAttStatus.setSelectedItem("Present"); save(); });
//            btnPanel.add(btnPresent);
//
//            JButton btnAbsent = createBtn("❌ Mark Absent", DANGER_COLOR, Color.WHITE);
//            btnAbsent.addActionListener(e -> { comboAttStatus.setSelectedItem("Absent"); save(); });
//            btnPanel.add(btnAbsent);
//
//            JButton btnLate = createBtn("⏰ Mark Late", WARNING_COLOR, Color.WHITE);
//            btnLate.addActionListener(e -> { comboAttStatus.setSelectedItem("Late"); save(); });
//            btnPanel.add(btnLate);
//
//            JButton btnSaveCustom = createBtn("💾 Save", PRIMARY_LIGHT, Color.WHITE);
//            btnSaveCustom.addActionListener(e -> save());
//            btnPanel.add(btnSaveCustom);
//
//            JButton btnCancel = createBtn("Cancel", new Color(149, 165, 166), Color.WHITE);
//            btnCancel.addActionListener(e -> dispose());
//            btnPanel.add(btnCancel);
//
//            add(btnPanel, BorderLayout.SOUTH);
//        }
//
//        private void loadExistingAttendance() {
//            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//                PreparedStatement ps = conn.prepareStatement(
//                    "SELECT attendance_status, check_in_time, performance_rating, faculty_feedback " +
//                    "FROM session_attendance WHERE session_id = ? AND student_id = ?");
//                ps.setInt(1, sessionId);
//                ps.setInt(2, selectedStudentId);
//                ResultSet rs = ps.executeQuery();
//                if (rs.next()) {
//                    comboAttStatus.setSelectedItem(rs.getString("attendance_status"));
//                    Time t = rs.getTime("check_in_time");
//                    if (t != null) txtCheckIn.setText(t.toString().substring(0, 5));
//                    String rating = rs.getString("performance_rating");
//                    if (rating != null) comboRating.setSelectedItem(rating);
//                    String fb = rs.getString("faculty_feedback");
//                    if (fb != null) txtFeedback.setText(fb);
//                }
//            } catch (Exception e) { e.printStackTrace(); }
//        }
//
//        private void save() {
//            String status   = (String) comboAttStatus.getSelectedItem();
//            String checkIn  = txtCheckIn.getText().trim();
//            String rating   = (String) comboRating.getSelectedItem();
//            String feedback = txtFeedback.getText().trim();
//
//            // Validate time format
//            if (!checkIn.matches("\\d{2}:\\d{2}(:\\d{2})?")) {
//                JOptionPane.showMessageDialog(this,
//                    "Check-in time must be in HH:mm format (e.g. 09:30)", "Validation", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            if (checkIn.length() == 5) checkIn += ":00";
//
//            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//                // Upsert: update if exists, else insert
//                PreparedStatement chk = conn.prepareStatement(
//                    "SELECT COUNT(*) FROM session_attendance WHERE session_id = ? AND student_id = ?");
//                chk.setInt(1, sessionId); chk.setInt(2, selectedStudentId);
//                ResultSet cr = chk.executeQuery();
//                boolean exists = cr.next() && cr.getInt(1) > 0;
//                cr.close(); chk.close();
//
//                if (exists) {
//                    PreparedStatement upd = conn.prepareStatement(
//                        "UPDATE session_attendance SET attendance_status=?, check_in_time=?, " +
//                        "performance_rating=?, faculty_feedback=? WHERE session_id=? AND student_id=?");
//                    upd.setString(1, status);
//                    upd.setTime(2, Time.valueOf(checkIn));
//                    upd.setString(3, rating);
//                    upd.setString(4, feedback);
//                    upd.setInt(5, sessionId);
//                    upd.setInt(6, selectedStudentId);
//                    upd.executeUpdate();
//                } else {
//                    PreparedStatement ins = conn.prepareStatement(
//                        "INSERT INTO session_attendance (session_id, student_id, attendance_status, " +
//                        "check_in_time, performance_rating, faculty_feedback) VALUES (?,?,?,?,?,?)");
//                    ins.setInt(1, sessionId);
//                    ins.setInt(2, selectedStudentId);
//                    ins.setString(3, status);
//                    ins.setTime(4, Time.valueOf(checkIn));
//                    ins.setString(5, rating);
//                    ins.setString(6, feedback);
//                    ins.executeUpdate();
//                }
//
//                String emoji = "Present".equals(status) ? "✅" : "Absent".equals(status) ? "❌" : "⏰";
//                JOptionPane.showMessageDialog(this,
//                    emoji + " Attendance saved: " + selectedStudentName + " marked <b>" + status + "</b>",
//                    "Saved", JOptionPane.INFORMATION_MESSAGE);
//                updateStatus("Attendance saved: " + selectedStudentName + " → " + status, SUCCESS_COLOR);
//                dispose();
//            } catch (Exception e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(this, "Error saving attendance:\n" + e.getMessage(),
//                    "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }
//
//    // ============================================================
//    // INNER DIALOG: Enter Marks & Comparison
//    // ============================================================
//    private class EnterMarksDialog extends JDialog {
//        private final int sessionId;
//        private JTextField txtNewMarks;
//        private JLabel lblOriginalMarks, lblComparison;
//
//        EnterMarksDialog(JFrame owner, int sessionId) {
//            super(owner, "🏅 Enter Marks — Session #" + sessionId, true);
//            this.sessionId = sessionId;
//            buildUI();
//        }
//
//        private void buildUI() {
//            setSize(480, 380);
//            setLocationRelativeTo(getOwner());
//            setLayout(new BorderLayout(10, 10));
//            getContentPane().setBackground(new Color(248, 249, 250));
//
//            // Header
//            JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
//            strip.setBackground(new Color(142, 68, 173));
//            JLabel hdr = new JLabel("🏅  Enter Marks — Compare with Original Failed Marks");
//            hdr.setFont(new Font("Segoe UI", Font.BOLD, 14));
//            hdr.setForeground(Color.WHITE);
//            strip.add(hdr);
//            add(strip, BorderLayout.NORTH);
//
//            // Info panel
//            JPanel info = new JPanel(new GridBagLayout());
//            info.setBackground(CARD_COLOR);
//            info.setBorder(new EmptyBorder(15, 25, 10, 25));
//            GridBagConstraints g = new GridBagConstraints();
//            g.insets = new Insets(8, 8, 8, 8);
//            g.fill = GridBagConstraints.HORIZONTAL;
//
//            int failedMarks = getFailedMarks(selectedStudentId);
//
//            // Student
//            g.gridx = 0; g.gridy = 0; g.weightx = 0.4;
//            info.add(formLabel("👤 Student:"), g);
//            g.gridx = 1; g.weightx = 0.6;
//            JLabel lblStu = new JLabel(selectedStudentName);
//            lblStu.setFont(new Font("Segoe UI", Font.BOLD, 13));
//            info.add(lblStu, g);
//
//            // Original failed marks
//            g.gridx = 0; g.gridy = 1;
//            info.add(formLabel("📉 Original Failed Marks:"), g);
//            g.gridx = 1;
//            lblOriginalMarks = new JLabel(failedMarks + " / " + currentMaxMarks +
//                "  (Pass: " + currentPassMarks + ")");
//            lblOriginalMarks.setFont(new Font("Segoe UI", Font.BOLD, 13));
//            lblOriginalMarks.setForeground(DANGER_COLOR);
//            info.add(lblOriginalMarks, g);
//
//            // New marks input
//            g.gridx = 0; g.gridy = 2;
//            info.add(formLabel("🏅 New Marks (0–" + currentMaxMarks + "):"), g);
//            g.gridx = 1;
//            txtNewMarks = styledField("");
//            info.add(txtNewMarks, g);
//
//            // Live comparison label
//            g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
//            lblComparison = new JLabel("  Enter marks above to see comparison.");
//            lblComparison.setFont(new Font("Segoe UI", Font.BOLD, 13));
//            lblComparison.setBorder(new CompoundBorder(
//                BorderFactory.createLineBorder(new Color(210, 210, 210)),
//                new EmptyBorder(8, 12, 8, 12)));
//            lblComparison.setOpaque(true);
//            lblComparison.setBackground(new Color(245, 245, 245));
//            info.add(lblComparison, g);
//
//            // Live update on key release
//            txtNewMarks.addKeyListener(new java.awt.event.KeyAdapter() {
//                public void keyReleased(java.awt.event.KeyEvent e) {
//                    updateComparison(failedMarks);
//                }
//            });
//
//            add(info, BorderLayout.CENTER);
//
//            // Load existing marks
//            loadExistingMarks();
//            updateComparison(failedMarks);
//
//            // Buttons
//            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
//            btnPanel.setBackground(new Color(248, 249, 250));
//
//            JButton btnSave = createBtn("💾 Save Marks", new Color(142, 68, 173), Color.WHITE);
//            btnSave.addActionListener(ev -> save(failedMarks));
//            btnPanel.add(btnSave);
//
//            JButton btnCancel = createBtn("Cancel", new Color(149, 165, 166), Color.WHITE);
//            btnCancel.addActionListener(ev -> dispose());
//            btnPanel.add(btnCancel);
//
//            add(btnPanel, BorderLayout.SOUTH);
//        }
//
//        private void loadExistingMarks() {
//            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//                PreparedStatement ps = conn.prepareStatement(
//                    "SELECT new_marks FROM remedial_marks WHERE session_id = ? AND student_id = ?");
//                ps.setInt(1, sessionId); ps.setInt(2, selectedStudentId);
//                ResultSet rs = ps.executeQuery();
//                if (rs.next()) txtNewMarks.setText(String.valueOf(rs.getInt("new_marks")));
//            } catch (Exception e) { e.printStackTrace(); }
//        }
//
//        private void updateComparison(int failedMarks) {
//            String txt = txtNewMarks.getText().trim();
//            if (txt.isEmpty()) {
//                lblComparison.setText("  Enter marks above to see comparison.");
//                lblComparison.setBackground(new Color(245, 245, 245));
//                lblComparison.setForeground(Color.DARK_GRAY);
//                return;
//            }
//            try {
//                int nm = Integer.parseInt(txt);
//                if (nm < 0 || nm > currentMaxMarks) {
//                    lblComparison.setText("  ⚠️ Marks must be between 0 and " + currentMaxMarks);
//                    lblComparison.setBackground(new Color(255, 243, 205));
//                    lblComparison.setForeground(new Color(133, 100, 0));
//                    return;
//                }
//                if (nm > failedMarks) {
//                    int delta = nm - failedMarks;
//                    double pct = (delta * 100.0) / currentMaxMarks;
//                    lblComparison.setText("  🟢 IMPROVED: " + nm + " vs " + failedMarks +
//                        " (+"+delta+" marks, +" + String.format("%.1f", pct) + "%)  " +
//                        (nm >= currentPassMarks ? "✅ PASSED!" : "⚠️ Still below pass mark"));
//                    lblComparison.setBackground(new Color(212, 237, 218));
//                    lblComparison.setForeground(new Color(21, 87, 36));
//                } else if (nm == failedMarks) {
//                    lblComparison.setText("  🟡 NO CHANGE: Still " + nm + " marks (same as original)");
//                    lblComparison.setBackground(new Color(255, 243, 205));
//                    lblComparison.setForeground(new Color(133, 100, 0));
//                } else {
//                    int delta = failedMarks - nm;
//                    lblComparison.setText("  🔴 DROPPED: " + nm + " vs " + failedMarks + " (-" + delta + " marks)");
//                    lblComparison.setBackground(new Color(248, 215, 218));
//                    lblComparison.setForeground(new Color(114, 28, 36));
//                }
//            } catch (NumberFormatException ex) {
//                lblComparison.setText("  ⚠️ Please enter a valid number.");
//                lblComparison.setBackground(new Color(248, 215, 218));
//            }
//        }
//
//        private void save(int failedMarks) {
//            String txt = txtNewMarks.getText().trim();
//            if (txt.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Please enter marks first!", "Required", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            int nm;
//            try { nm = Integer.parseInt(txt); } catch (NumberFormatException ex) {
//                JOptionPane.showMessageDialog(this, "Invalid number!", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//            if (nm < 0 || nm > currentMaxMarks) {
//                JOptionPane.showMessageDialog(this, "Marks must be 0–" + currentMaxMarks, "Validation", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//                PreparedStatement chk = conn.prepareStatement(
//                    "SELECT COUNT(*) FROM remedial_marks WHERE session_id=? AND student_id=?");
//                chk.setInt(1, sessionId); chk.setInt(2, selectedStudentId);
//                ResultSet cr = chk.executeQuery();
//                boolean exists = cr.next() && cr.getInt(1) > 0;
//                cr.close(); chk.close();
//
//                if (exists) {
//                    PreparedStatement upd = conn.prepareStatement(
//                        "UPDATE remedial_marks SET new_marks=? WHERE session_id=? AND student_id=?");
//                    upd.setInt(1, nm); upd.setInt(2, sessionId); upd.setInt(3, selectedStudentId);
//                    upd.executeUpdate();
//                } else {
//                    PreparedStatement ins = conn.prepareStatement(
//                        "INSERT INTO remedial_marks (session_id, student_id, new_marks) VALUES (?,?,?)");
//                    ins.setInt(1, sessionId); ins.setInt(2, selectedStudentId); ins.setInt(3, nm);
//                    ins.executeUpdate();
//                }
//                String result = nm > failedMarks ? "🟢 Improved!" : nm == failedMarks ? "🟡 No Change" : "🔴 Dropped";
//                JOptionPane.showMessageDialog(this,
//                    "✅ Marks saved!\n" + selectedStudentName + ": " + nm + "/" + currentMaxMarks +
//                    "\nOriginal: " + failedMarks + "/" + currentMaxMarks + "\n" + result,
//                    "Marks Saved", JOptionPane.INFORMATION_MESSAGE);
//                updateStatus("Marks saved for " + selectedStudentName + ": " + nm + " → " + result, SUCCESS_COLOR);
//                dispose();
//            } catch (Exception e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }
//
//    // ============================================================
//    // INNER DIALOG: Progress Report
//    // ============================================================
//    private class ProgressDialog extends JDialog {
//        ProgressDialog(JFrame owner, int sessionId) {
//            super(owner, "📊 Progress Report — " + selectedStudentName, true);
//            setSize(640, 560);
//            setLocationRelativeTo(owner);
//            setLayout(new BorderLayout(10, 10));
//            getContentPane().setBackground(new Color(248, 249, 250));
//
//            // Header
//            JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
//            strip.setBackground(new Color(22, 160, 133));
//            JLabel hdr = new JLabel("📊  Student Progress Report");
//            hdr.setFont(new Font("Segoe UI", Font.BOLD, 16));
//            hdr.setForeground(Color.WHITE);
//            strip.add(hdr);
//            add(strip, BorderLayout.NORTH);
//
//            JTextArea report = new JTextArea();
//            report.setEditable(false);
//            report.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//            report.setMargin(new Insets(15, 20, 15, 20));
//            report.setBackground(CARD_COLOR);
//            report.setLineWrap(true);
//            report.setWrapStyleWord(true);
//
//            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//                // Remedial tracking record
//                PreparedStatement ps = conn.prepareStatement(
//                    "SELECT r.failed_marks, r.max_marks, r.pass_marks, r.remedial_status, r.failed_percentage, " +
//                    "       s.name, s.roll_no, e.exam_name " +
//                    "FROM remedial_tracking r " +
//                    "JOIN student s ON r.student_id = s.student_id " +
//                    "JOIN exam e ON r.failed_exam_id = e.exam_id " +
//                    "WHERE r.remedial_id = ?");
//                ps.setInt(1, selectedRemedialId);
//                ResultSet rs = ps.executeQuery();
//
//                if (rs.next()) {
//                    String name      = rs.getString("name");
//                    String rollNo    = rs.getString("roll_no");
//                    String examName  = rs.getString("exam_name");
//                    int failedMarks  = rs.getInt("failed_marks");
//                    int maxM         = rs.getInt("max_marks");
//                    int passM        = rs.getInt("pass_marks");
//                    double failedPct = rs.getDouble("failed_percentage");
//                    String rStatus   = rs.getString("remedial_status");
//
//                    // Marks from selected session
//                    PreparedStatement mps = conn.prepareStatement(
//                        "SELECT new_marks FROM remedial_marks WHERE session_id = ? AND student_id = ?");
//                    mps.setInt(1, sessionId); mps.setInt(2, selectedStudentId);
//                    ResultSet mrs = mps.executeQuery();
//                    Integer newMarks = mrs.next() ? mrs.getInt("new_marks") : null;
//                    mrs.close(); mps.close();
//
//                    // All sessions marks — best and latest
//                    PreparedStatement aps = conn.prepareStatement(
//                        "SELECT rm.new_marks, rs.session_date, rs.topic_covered " +
//                        "FROM remedial_marks rm JOIN remedial_sessions rs ON rm.session_id = rs.session_id " +
//                        "WHERE rs.remedial_id = ? AND rm.student_id = ? " +
//                        "ORDER BY rs.session_date");
//                    aps.setInt(1, selectedRemedialId); aps.setInt(2, selectedStudentId);
//                    ResultSet ars = aps.executeQuery();
//
//                    StringBuilder history = new StringBuilder();
//                    int best = failedMarks, latest = failedMarks, entryCount = 0;
//                    while (ars.next()) {
//                        int m = ars.getInt("new_marks");
//                        if (m > best) best = m;
//                        latest = m;
//                        history.append("  • ").append(ars.getDate("session_date"))
//                               .append("  [").append(ars.getString("topic_covered")).append("] → ")
//                               .append(m).append("/").append(maxM)
//                               .append(m > failedMarks ? "  ✅ Improved" : m == failedMarks ? "  🟡 Same" : "  ❌ Dropped")
//                               .append("\n");
//                        entryCount++;
//                    }
//                    ars.close(); aps.close();
//
//                    // Attendance
//                    int totalS    = getTotalSessions(selectedStudentId);
//                    int attendedS = getAttendedSessions(selectedStudentId);
//                    double attPct = totalS > 0 ? (attendedS * 100.0 / totalS) : 0;
//
//                    // Build report text
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("══════════════════════════════════════════════════\n");
//                    sb.append("   📋 REMEDIAL PROGRESS REPORT\n");
//                    sb.append("══════════════════════════════════════════════════\n\n");
//                    sb.append("  Student : ").append(name).append("  (Roll: ").append(rollNo).append(")\n");
//                    sb.append("  Exam    : ").append(examName).append("\n");
//                    sb.append("  Status  : ").append(rStatus).append("\n\n");
//                    sb.append("──────────────────────────────────────────────────\n");
//                    sb.append("  📉 ORIGINAL FAILED MARKS\n");
//                    sb.append("     ").append(failedMarks).append(" / ").append(maxM)
//                      .append("   (").append(String.format("%.1f", failedPct)).append("%)")
//                      .append("   Pass Mark: ").append(passM).append("\n\n");
//
//                    if (newMarks != null) {
//                        int delta = newMarks - failedMarks;
//                        double newPct = (newMarks * 100.0) / maxM;
//                        sb.append("  🏅 CURRENT SESSION MARKS\n");
//                        sb.append("     ").append(newMarks).append(" / ").append(maxM)
//                          .append("   (").append(String.format("%.1f", newPct)).append("%)\n");
//                        if (delta > 0) {
//                            sb.append("     🟢 IMPROVED by +").append(delta).append(" marks (+")
//                              .append(String.format("%.1f", (delta * 100.0 / maxM))).append("%)\n");
//                            if (newMarks >= passM)
//                                sb.append("     🎉 PASSED! Student has cleared the subject!\n");
//                            else
//                                sb.append("     📈 Still ").append(passM - newMarks).append(" marks below passing.\n");
//                        } else if (delta == 0) {
//                            sb.append("     🟡 NO CHANGE — same as original failed marks.\n");
//                        } else {
//                            sb.append("     🔴 DROPPED by ").append(-delta).append(" marks.\n");
//                        }
//                    } else {
//                        sb.append("  ⏳ No marks entered for this session yet.\n");
//                    }
//
//                    if (entryCount > 0) {
//                        sb.append("\n──────────────────────────────────────────────────\n");
//                        sb.append("  📈 ALL SESSIONS MARKS HISTORY (").append(entryCount).append(" entries)\n");
//                        sb.append("     Best Score : ").append(best).append("/").append(maxM).append("\n");
//                        sb.append("     Latest     : ").append(latest).append("/").append(maxM).append("\n\n");
//                        sb.append(history);
//                    }
//
//                    sb.append("\n──────────────────────────────────────────────────\n");
//                    sb.append("  📊 ATTENDANCE\n");
//                    sb.append("     ").append(attendedS).append(" / ").append(totalS)
//                      .append(" sessions attended  (").append(String.format("%.0f", attPct)).append("%)\n");
//                    if (attPct < 50)
//                        sb.append("     ⚠️ Low attendance! Encourage student to attend more.\n");
//                    else if (attPct >= 75)
//                        sb.append("     ✅ Good attendance!\n");
//
//                    sb.append("\n══════════════════════════════════════════════════\n");
//
//                    report.setText(sb.toString());
//                    report.setCaretPosition(0);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                report.setText("Error generating report:\n" + e.getMessage());
//            }
//
//            add(new JScrollPane(report), BorderLayout.CENTER);
//
//            JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
//            btns.setBackground(new Color(248, 249, 250));
//            JButton btnClose = createBtn("✅ Close", SUCCESS_COLOR, Color.WHITE);
//            btnClose.addActionListener(e -> dispose());
//            btns.add(btnClose);
//            add(btns, BorderLayout.SOUTH);
//        }
//    }
//
//    // ============================================================
//    // DATABASE HELPERS
//    // ============================================================
//    private int getStudentIdFromRemedial(int remedialId) {
//        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//            PreparedStatement ps = conn.prepareStatement("SELECT student_id FROM remedial_tracking WHERE remedial_id = ?");
//            ps.setInt(1, remedialId);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) return rs.getInt("student_id");
//        } catch (Exception e) { e.printStackTrace(); }
//        return -1;
//    }
//
//    private int getTotalSessions(int studentId) {
//        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//            PreparedStatement ps = conn.prepareStatement(
//                "SELECT COUNT(*) FROM remedial_sessions rs " +
//                "JOIN remedial_tracking r ON rs.remedial_id = r.remedial_id WHERE r.student_id = ?");
//            ps.setInt(1, studentId);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) return rs.getInt(1);
//        } catch (Exception e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    private int getAttendedSessions(int studentId) {
//        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//            PreparedStatement ps = conn.prepareStatement(
//                "SELECT COUNT(DISTINCT sa.session_id) FROM session_attendance sa " +
//                "JOIN remedial_sessions rs ON sa.session_id = rs.session_id " +
//                "JOIN remedial_tracking r ON rs.remedial_id = r.remedial_id " +
//                "WHERE r.student_id = ? AND sa.attendance_status = 'Present'");
//            ps.setInt(1, studentId);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) return rs.getInt(1);
//        } catch (Exception e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    private int getFailedMarks(int studentId) {
//        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
//            PreparedStatement ps = conn.prepareStatement(
//                "SELECT failed_marks FROM remedial_tracking WHERE student_id = ? AND subject_id = ? AND failed_exam_id = ?");
//            ps.setInt(1, studentId); ps.setInt(2, currentSubjectId); ps.setInt(3, currentExamId);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) return rs.getInt("failed_marks");
//        } catch (Exception e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    // ============================================================
//    // UI HELPERS
//    // ============================================================
//    private void updateStatus(String msg, Color c) {
//        statusLabel.setText(msg);
//        statusLabel.setForeground(c);
//    }
//
//    private void disableSessionButtons() {
//        btnEditSession.setEnabled(false);
//        btnDeleteSession.setEnabled(false);
//        btnTakeAttendance.setEnabled(false);
//        btnEnterMarks.setEnabled(false);
//        btnViewProgress.setEnabled(false);
//    }
//
//    private JButton createBtn(String text, Color bg, Color fg) {
//        JButton b = new JButton(text);
//        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        b.setForeground(fg);
//        b.setBackground(bg);
//        b.setFocusPainted(false);
//        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        b.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
//        b.addMouseListener(new MouseAdapter() {
//            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
//            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
//        });
//        return b;
//    }
//
//    private JComboBox<String> createComboBox(int width) {
//        JComboBox<String> c = new JComboBox<>();
//        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        c.setPreferredSize(new Dimension(width, 34));
//        c.setBackground(Color.WHITE);
//        return c;
//    }
//
//    private JLabel label(String text) {
//        JLabel l = new JLabel(text);
//        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        return l;
//    }
//
//    private JLabel formLabel(String text) {
//        JLabel l = new JLabel(text);
//        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        l.setForeground(new Color(60, 60, 60));
//        return l;
//    }
//
//    private JTextField styledField(String def) {
//        JTextField f = new JTextField(def);
//        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        f.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(new Color(200, 200, 200)),
//            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
//        return f;
//    }
//
//    private Border cardBorder(String title) {
//        return BorderFactory.createCompoundBorder(
//            BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(210, 210, 210)),
//                "  " + title + "  ",
//                javax.swing.border.TitledBorder.LEFT,
//                javax.swing.border.TitledBorder.TOP,
//                new Font("Segoe UI", Font.BOLD, 13),
//                PRIMARY_COLOR),
//            new EmptyBorder(6, 6, 6, 6));
//    }
//
//    private void styleTable(JTable t) {
//        t.setRowHeight(30);
//        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        t.setGridColor(new Color(235, 235, 235));
//        t.setSelectionBackground(new Color(173, 216, 230));
//        t.setSelectionForeground(Color.BLACK);
//        t.setAutoCreateRowSorter(true);
//        JTableHeader h = t.getTableHeader();
//        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        h.setBackground(PRIMARY_COLOR);
//        h.setForeground(Color.WHITE);
//        h.setReorderingAllowed(false);
//        t.setIntercellSpacing(new Dimension(10, 2));
//        t.setShowHorizontalLines(true);
//        t.setShowVerticalLines(false);
//    }
//
//    private void styleSessionsTable(JTable t) {
//        styleTable(t);
//        // Color-code the Marks vs Failed column (col 6) and Attendance (col 7)
//        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable tbl, Object value,
//                    boolean sel, boolean focus, int row, int col) {
//                Component c = super.getTableCellRendererComponent(tbl, value, sel, focus, row, col);
//                if (!sel) {
//                    String s = value == null ? "" : value.toString();
//                    if (col == 6) {
//                        if (s.contains("🟢")) {
//                            c.setBackground(new Color(212, 237, 218));
//                            c.setForeground(new Color(21, 87, 36));
//                        } else if (s.contains("🔴")) {
//                            c.setBackground(new Color(248, 215, 218));
//                            c.setForeground(new Color(114, 28, 36));
//                        } else if (s.contains("🟡")) {
//                            c.setBackground(new Color(255, 243, 205));
//                            c.setForeground(new Color(133, 100, 0));
//                        } else {
//                            c.setBackground(new Color(245, 245, 245));
//                            c.setForeground(Color.DARK_GRAY);
//                        }
//                    } else if (col == 7) {
//                        if ("Present".equals(s)) {
//                            c.setBackground(new Color(212, 237, 218));
//                            c.setForeground(new Color(21, 87, 36));
//                        } else if ("Absent".equals(s)) {
//                            c.setBackground(new Color(248, 215, 218));
//                            c.setForeground(new Color(114, 28, 36));
//                        } else if ("Late".equals(s)) {
//                            c.setBackground(new Color(255, 243, 205));
//                            c.setForeground(new Color(133, 100, 0));
//                        } else {
//                            c.setBackground(Color.WHITE);
//                            c.setForeground(Color.DARK_GRAY);
//                        }
//                    } else {
//                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
//                        c.setForeground(Color.BLACK);
//                    }
//                    ((JLabel) c).setFont(new Font("Segoe UI", Font.PLAIN, 12));
//                }
//                return c;
//            }
//        });
//    }
//
//    private void styleComboColors(JComboBox<String> combo) {
//        combo.setRenderer(new DefaultListCellRenderer() {
//            @Override
//            public Component getListCellRendererComponent(JList<?> list, Object value,
//                    int index, boolean isSelected, boolean cellHasFocus) {
//                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//                String s = value == null ? "" : value.toString();
//                if (!isSelected) {
//                    if ("Present".equals(s)) { c.setBackground(new Color(212, 237, 218)); c.setForeground(new Color(21, 87, 36)); }
//                    else if ("Absent".equals(s)) { c.setBackground(new Color(248, 215, 218)); c.setForeground(new Color(114, 28, 36)); }
//                    else if ("Late".equals(s)) { c.setBackground(new Color(255, 243, 205)); c.setForeground(new Color(133, 100, 0)); }
//                }
//                return c;
//            }
//        });
//    }
//
//    // ============================================================
//    // MAIN
//    // ============================================================
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame mock = new JFrame("Faculty Dashboard");
//            mock.setSize(1000, 650);
//            mock.setVisible(true);
//            new RemedialSessionManagementUI(1, mock);
//        });
//    }
//}

package com.college.sms.ui;

import com.college.sms.dao.*;
import com.college.sms.model.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalTime;
import java.util.List;

public class RemedialSessionManagementUI extends JFrame {

    private int facultyId;
    private JFrame previousUI;

    private JComboBox<String> comboClass;
    private JComboBox<String> comboSubject;
    private JComboBox<String> comboExam;

    private JTable remedialStudentsTable;
    private DefaultTableModel remedialStudentsModel;

    private JTable sessionsTable;
    private DefaultTableModel sessionsModel;

    private JButton btnBack, btnCreateSession, btnLoadSessions, btnRefresh;
    private JButton btnEditSession, btnDeleteSession, btnTakeAttendance, btnEnterMarks, btnViewProgress;

    private ClassDAO classDAO             = new ClassDAO();
    private SubjectDAO subjectDAO         = new SubjectDAO();
    private ExamDAO examDAO               = new ExamDAO();
    private RemedialDAO remedialDAO       = new RemedialDAO();
    private RemedialSessionDAO sessionDAO = new RemedialSessionDAO();
    private SessionAttendanceDAO attendanceDAO = new SessionAttendanceDAO();
    private StudentDAO studentDAO         = new StudentDAO();

    private int currentClassId      = -1;
    private int currentSubjectId    = -1;
    private int currentExamId       = -1;
    private int currentMaxMarks     = 0;
    private int currentPassMarks    = 0;
    private int selectedRemedialId  = -1;
    private int selectedStudentId   = -1;
    private String selectedStudentName = "";

    private JLabel statusLabel;

    private static final Color PRIMARY_COLOR    = new Color(52, 73, 94);
    private static final Color PRIMARY_LIGHT    = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR    = new Color(39, 174, 96);
    private static final Color WARNING_COLOR    = new Color(243, 156, 18);
    private static final Color DANGER_COLOR     = new Color(231, 76, 60);
    private static final Color INFO_COLOR       = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR       = Color.WHITE;
    private static final Color HEADER_BG        = new Color(44, 62, 80);

    public RemedialSessionManagementUI(int facultyId, JFrame previousUI) {
        this.facultyId  = facultyId;
        this.previousUI = previousUI;
        initComponents();
        loadClasses();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Remedial Session Management | Faculty ID: " + facultyId);
        setSize(1450, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND_COLOR);
        add(buildNorthPanel(),    BorderLayout.NORTH);
        add(buildMainSplitPane(), BorderLayout.CENTER);
        add(buildStatusBar(),     BorderLayout.SOUTH);
    }

    // ─── NORTH ───────────────────────────────────────────────────
    private JPanel buildNorthPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND_COLOR);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(0, 62));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel title = new JLabel("Remedial Session Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        btnBack = createBtn("Back to Dashboard", PRIMARY_LIGHT, Color.WHITE);
        btnBack.setPreferredSize(new Dimension(190, 38));
        btnBack.addActionListener(e -> navigateBack());
        header.add(btnBack, BorderLayout.EAST);
        wrapper.add(header, BorderLayout.NORTH);

        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        filter.setBackground(CARD_COLOR);
        filter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)),
            new EmptyBorder(4, 15, 4, 15)));

        filter.add(label("Class:"));
        comboClass = createComboBox(200);
        comboClass.addActionListener(e -> {
            String sel = (String) comboClass.getSelectedItem();
            if (sel != null && !sel.contains("--")) {
                currentClassId = Integer.parseInt(sel.split(" - ")[0].trim());
                loadSubjects();
            }
        });
        filter.add(comboClass);

        filter.add(label("Subject:"));
        comboSubject = createComboBox(200);
        comboSubject.addActionListener(e -> {
            String sel = (String) comboSubject.getSelectedItem();
            if (sel != null && !sel.contains("--")) {
                currentSubjectId = Integer.parseInt(sel.split(" - ")[0].trim());
                loadExams();
            }
        });
        filter.add(comboSubject);

        filter.add(label("Exam:"));
        comboExam = createComboBox(260);
        comboExam.addActionListener(e -> {
            String sel = (String) comboExam.getSelectedItem();
            if (sel != null && !sel.contains("--")) {
                currentExamId = Integer.parseInt(sel.split(":")[0].trim());
                try {
                    Exam ex = examDAO.getExamById(currentExamId);
                    currentMaxMarks  = ex.getMaxMarks();
                    currentPassMarks = ex.getPassMarks();
                } catch (Exception ex2) { ex2.printStackTrace(); }
                loadRemedialStudents();
            }
        });
        filter.add(comboExam);

        btnRefresh = createBtn("Refresh", new Color(127, 140, 141), Color.WHITE);
        btnRefresh.setPreferredSize(new Dimension(90, 34));
        btnRefresh.addActionListener(e -> { loadRemedialStudents(); loadSessions(); });
        filter.add(btnRefresh);

        wrapper.add(filter, BorderLayout.CENTER);
        return wrapper;
    }

    // ─── CENTER ──────────────────────────────────────────────────
    private JSplitPane buildMainSplitPane() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(520);
        split.setResizeWeight(0.36);
        split.setBorder(new EmptyBorder(8, 8, 8, 8));

        // LEFT
        JPanel left = new JPanel(new BorderLayout(0, 6));
        left.setBackground(CARD_COLOR);
        left.setBorder(cardBorder("Students in Remedial Tracking"));

        remedialStudentsModel = new DefaultTableModel(
            new String[]{"Remedial ID", "Student Name", "Roll No", "Failed Marks", "Status", "Attendance"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        remedialStudentsTable = new JTable(remedialStudentsModel);
        styleTable(remedialStudentsTable);
        hideColumn(remedialStudentsTable, 0);
        remedialStudentsTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        remedialStudentsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        remedialStudentsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        remedialStudentsTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        remedialStudentsTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        remedialStudentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = remedialStudentsTable.getSelectedRow();
                if (row >= 0) {
                    selectedRemedialId  = (int) remedialStudentsModel.getValueAt(row, 0);
                    selectedStudentId   = getStudentIdFromRemedial(selectedRemedialId);
                    selectedStudentName = remedialStudentsModel.getValueAt(row, 1).toString();
                    btnCreateSession.setEnabled(true);
                    btnLoadSessions.setEnabled(true);
                    sessionsModel.setRowCount(0);
                    updateStatus("Selected: " + selectedStudentName + "  —  Click Load Sessions.", INFO_COLOR);
                }
            }
        });
        left.add(new JScrollPane(remedialStudentsTable), BorderLayout.CENTER);

        JPanel leftAct = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        leftAct.setBackground(new Color(248, 249, 250));
        leftAct.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        btnCreateSession = createBtn("+ New Session", SUCCESS_COLOR, Color.WHITE);
        btnCreateSession.setEnabled(false);
        btnCreateSession.addActionListener(e -> openCreateSessionDialog());
        leftAct.add(btnCreateSession);
        btnLoadSessions = createBtn("Load Sessions", INFO_COLOR, Color.WHITE);
        btnLoadSessions.setEnabled(false);
        btnLoadSessions.addActionListener(e -> loadSessions());
        leftAct.add(btnLoadSessions);
        left.add(leftAct, BorderLayout.SOUTH);
        split.setLeftComponent(left);

        // RIGHT
        JPanel right = new JPanel(new BorderLayout(0, 6));
        right.setBackground(CARD_COLOR);
        right.setBorder(cardBorder("Sessions — CRUD | Marks | Attendance"));

        sessionsModel = new DefaultTableModel(
            new String[]{"Session ID", "Date", "Time", "Topic", "Location", "Status", "Marks vs Failed", "Attendance"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        sessionsTable = new JTable(sessionsModel);
        styleSessionsTable(sessionsTable);
        hideColumn(sessionsTable, 0);
        sessionsTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        sessionsTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        sessionsTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        sessionsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        sessionsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        sessionsTable.getColumnModel().getColumn(6).setPreferredWidth(155);
        sessionsTable.getColumnModel().getColumn(7).setPreferredWidth(110);

        sessionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean has = sessionsTable.getSelectedRow() >= 0;
                btnEditSession.setEnabled(has);
                btnDeleteSession.setEnabled(has);
                btnTakeAttendance.setEnabled(has);
                btnEnterMarks.setEnabled(has);
                btnViewProgress.setEnabled(has);
            }
        });
        right.add(new JScrollPane(sessionsTable), BorderLayout.CENTER);

        JPanel rightAct = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 6));
        rightAct.setBackground(new Color(248, 249, 250));
        rightAct.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        btnEditSession = createBtn("Edit", new Color(41, 128, 185), Color.WHITE);
        btnEditSession.setEnabled(false);
        btnEditSession.addActionListener(e -> openEditSessionDialog());
        rightAct.add(btnEditSession);

        btnDeleteSession = createBtn("Delete", DANGER_COLOR, Color.WHITE);
        btnDeleteSession.setEnabled(false);
        btnDeleteSession.addActionListener(e -> deleteSelectedSession());
        rightAct.add(btnDeleteSession);

        rightAct.add(new JSeparator(JSeparator.VERTICAL) {{ setPreferredSize(new Dimension(2, 28)); }});

        btnTakeAttendance = createBtn("Attendance", WARNING_COLOR, Color.WHITE);
        btnTakeAttendance.setEnabled(false);
        btnTakeAttendance.setToolTipText("Mark Present / Absent / Late for this session");
        btnTakeAttendance.addActionListener(e -> openAttendanceDialog());
        rightAct.add(btnTakeAttendance);

        btnEnterMarks = createBtn("Enter Marks", new Color(142, 68, 173), Color.WHITE);
        btnEnterMarks.setEnabled(false);
        btnEnterMarks.setToolTipText("Enter new marks & compare with original failed marks");
        btnEnterMarks.addActionListener(e -> openEnterMarksDialog());
        rightAct.add(btnEnterMarks);

        btnViewProgress = createBtn("Progress", new Color(22, 160, 133), Color.WHITE);
        btnViewProgress.setEnabled(false);
        btnViewProgress.setToolTipText("View detailed progress report");
        btnViewProgress.addActionListener(e -> openProgressDialog());
        rightAct.add(btnViewProgress);

        right.add(rightAct, BorderLayout.SOUTH);
        split.setRightComponent(right);
        return split;
    }

    private JLabel buildStatusBar() {
        statusLabel = new JLabel("Ready. Select Class -> Subject -> Exam to begin.");
        statusLabel.setBorder(new EmptyBorder(6, 15, 6, 15));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(236, 240, 241));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(60, 60, 60));
        return statusLabel;
    }

    // ─── NAVIGATION ──────────────────────────────────────────────
    private void navigateBack() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            if (previousUI != null) { previousUI.setVisible(true); previousUI.toFront(); }
            else new FacultyDashboard(facultyId).setVisible(true);
        });
    }

    // ─── DATA LOADING ────────────────────────────────────────────
    private void loadClasses() {
        comboClass.removeAllItems(); comboSubject.removeAllItems(); comboExam.removeAllItems();
        remedialStudentsModel.setRowCount(0); sessionsModel.setRowCount(0);
        try {
            List<String[]> cls = classDAO.getClassesByFaculty(facultyId);
            if (cls.isEmpty()) { comboClass.addItem("-- No classes --"); return; }
            for (String[] c : cls) comboClass.addItem(c[0] + " - " + c[1]);
        } catch (Exception e) { comboClass.addItem("-- Error --"); }
    }

    private void loadSubjects() {
        comboSubject.removeAllItems(); comboExam.removeAllItems();
        remedialStudentsModel.setRowCount(0); sessionsModel.setRowCount(0);
        try {
            List<Subject> subs = subjectDAO.getSubjectsByClassAndFaculty(currentClassId, facultyId);
            if (subs.isEmpty()) { comboSubject.addItem("-- No subjects --"); return; }
            for (Subject s : subs) comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
        } catch (Exception e) { comboSubject.addItem("-- Error --"); }
    }

    private void loadExams() {
        comboExam.removeAllItems(); remedialStudentsModel.setRowCount(0); sessionsModel.setRowCount(0);
        try {
            List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, currentClassId, currentSubjectId);
            if (exams.isEmpty()) { comboExam.addItem("-- No exams --"); return; }
            for (Exam ex : exams)
                comboExam.addItem(ex.getExamId() + ":" + ex.getExamName()
                    + " (Max:" + ex.getMaxMarks() + " Pass:" + ex.getPassMarks() + ")");
        } catch (Exception e) { comboExam.addItem("-- Error --"); }
    }

    private void loadRemedialStudents() {
        remedialStudentsModel.setRowCount(0); sessionsModel.setRowCount(0);
        selectedRemedialId = -1; selectedStudentId = -1; selectedStudentName = "";
        btnCreateSession.setEnabled(false); btnLoadSessions.setEnabled(false);
        disableSessionButtons();
        if (currentExamId <= 0) return;
        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT r.remedial_id, r.student_id, s.name, s.roll_no, r.failed_marks, r.max_marks, r.remedial_status " +
                "FROM remedial_tracking r JOIN student s ON r.student_id = s.student_id " +
                "WHERE r.subject_id = ? AND r.failed_exam_id = ? ORDER BY s.name");
            ps.setInt(1, currentSubjectId); ps.setInt(2, currentExamId);
            ResultSet rs = ps.executeQuery();
            int cnt = 0;
            while (rs.next()) {
                int sid = rs.getInt("student_id");
                remedialStudentsModel.addRow(new Object[]{
                    rs.getInt("remedial_id"), rs.getString("name"), rs.getString("roll_no"),
                    rs.getInt("failed_marks") + "/" + rs.getInt("max_marks"),
                    rs.getString("remedial_status"),
                    getAttendedSessions(sid) + "/" + getTotalSessions(sid)
                });
                cnt++;
            }
            updateStatus("Loaded " + cnt + " student(s).", SUCCESS_COLOR);
        } catch (Exception e) { e.printStackTrace(); updateStatus("Error: " + e.getMessage(), DANGER_COLOR); }
    }

    private void loadSessions() {
        sessionsModel.setRowCount(0); disableSessionButtons();
        if (selectedRemedialId <= 0 || selectedStudentId <= 0) {
            updateStatus("Select a student first.", WARNING_COLOR); return;
        }
        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT rs.session_id, rs.session_date, rs.session_time, rs.topic_covered, rs.location, rs.session_status, " +
                "       sa.attendance_status, rm.new_marks " +
                "FROM remedial_sessions rs " +
                "LEFT JOIN session_attendance sa ON rs.session_id = sa.session_id AND sa.student_id = ? " +
                "LEFT JOIN remedial_marks rm     ON rs.session_id = rm.session_id  AND rm.student_id = ? " +
                "WHERE rs.remedial_id = ? ORDER BY rs.session_date DESC, rs.session_time DESC");
            ps.setInt(1, selectedStudentId); ps.setInt(2, selectedStudentId); ps.setInt(3, selectedRemedialId);
            ResultSet rs = ps.executeQuery();
            int fm = getFailedMarks(selectedStudentId), cnt = 0;
            while (rs.next()) {
                Integer nm = rs.getObject("new_marks", Integer.class);
                String marksDisplay;
                if (nm == null)       marksDisplay = "Not Entered";
                else if (nm > fm)     marksDisplay = "IMPROVED: " + nm + "/" + currentMaxMarks + " (+" + (nm - fm) + ")";
                else if (nm == fm)    marksDisplay = "NO CHANGE: " + nm + "/" + currentMaxMarks;
                else                  marksDisplay = "DROPPED: " + nm + "/" + currentMaxMarks + " (-" + (fm - nm) + ")";

                String att = rs.getString("attendance_status");
                sessionsModel.addRow(new Object[]{
                    rs.getInt("session_id"), rs.getDate("session_date"), rs.getTime("session_time"),
                    rs.getString("topic_covered"), rs.getString("location"), rs.getString("session_status"),
                    marksDisplay, att == null ? "Not Marked" : att
                });
                cnt++;
            }
            updateStatus("Loaded " + cnt + " session(s) for " + selectedStudentName, INFO_COLOR);
        } catch (Exception e) { e.printStackTrace(); updateStatus("Error: " + e.getMessage(), DANGER_COLOR); }
    }

    // ─── CRUD ────────────────────────────────────────────────────
    private void openCreateSessionDialog() {
        if (selectedRemedialId <= 0 || selectedStudentId <= 0) {
            JOptionPane.showMessageDialog(this, "Select a student first!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new SessionDialog(this, false, -1).setVisible(true);
        loadSessions(); loadRemedialStudents();
    }

    private void openEditSessionDialog() {
        int row = sessionsTable.getSelectedRow(); if (row < 0) return;
        new SessionDialog(this, true, (int) sessionsModel.getValueAt(row, 0)).setVisible(true);
        loadSessions(); loadRemedialStudents();
    }

    private void deleteSelectedSession() {
        int row = sessionsTable.getSelectedRow(); if (row < 0) return;
        int sid = (int) sessionsModel.getValueAt(row, 0);
        String topic = String.valueOf(sessionsModel.getValueAt(row, 3));
        int c = JOptionPane.showConfirmDialog(this,
            "<html><b>Delete Session?</b><br>Topic: <b>" + topic + "</b><br><br>" +
            "This will also delete attendance & marks for this session.<br><b>Cannot be undone!</b></html>",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
            conn.prepareStatement("DELETE FROM session_attendance WHERE session_id = " + sid).executeUpdate();
            conn.prepareStatement("DELETE FROM remedial_marks WHERE session_id = "      + sid).executeUpdate();
            conn.prepareStatement("DELETE FROM remedial_sessions WHERE session_id = "   + sid).executeUpdate();
            JOptionPane.showMessageDialog(this, "Session deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            updateStatus("Session deleted.", SUCCESS_COLOR);
            loadSessions(); loadRemedialStudents();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAttendanceDialog() {
        int row = sessionsTable.getSelectedRow(); if (row < 0) return;
        // Guard: student must be selected
        if (selectedStudentId <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the left panel first!",
                "No Student", JOptionPane.WARNING_MESSAGE); return;
        }
        int sessionId = (int) sessionsModel.getValueAt(row, 0);
        new AttendanceDialog(this, sessionId).setVisible(true);
        loadSessions(); loadRemedialStudents();
    }

    private void openEnterMarksDialog() {
        int row = sessionsTable.getSelectedRow(); if (row < 0) return;
        new EnterMarksDialog(this, (int) sessionsModel.getValueAt(row, 0)).setVisible(true);
        loadSessions();
    }

    private void openProgressDialog() {
        int row = sessionsTable.getSelectedRow(); if (row < 0) return;
        new ProgressDialog(this, (int) sessionsModel.getValueAt(row, 0)).setVisible(true);
    }

    // ─── SESSION DIALOG ──────────────────────────────────────────
    private class SessionDialog extends JDialog {
        private final boolean isEdit;
        private final int sessionId;
        private JSpinner dateSpinner, timeSpinner;
        private JTextField txtTopic, txtLocation, txtDuration;
        private JTextArea txtNotes;
        private JComboBox<String> comboStatus;

        SessionDialog(JFrame owner, boolean isEdit, int sessionId) {
            super(owner, isEdit ? "Edit Session" : "Create New Session", true);
            this.isEdit = isEdit; this.sessionId = sessionId;
            buildUI(); if (isEdit) loadData();
        }

        private void buildUI() {
            setSize(520, 520); setLocationRelativeTo(getOwner());
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(new Color(248, 249, 250));

            JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            strip.setBackground(isEdit ? new Color(41, 128, 185) : SUCCESS_COLOR);
            JLabel lbl = new JLabel(isEdit ? "Edit Remedial Session" : "Create Remedial Session");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 16)); lbl.setForeground(Color.WHITE);
            strip.add(lbl); add(strip, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(CARD_COLOR); form.setBorder(new EmptyBorder(15, 25, 10, 25));
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(6, 6, 6, 6); g.fill = GridBagConstraints.HORIZONTAL;

            g.gridx=0; g.gridy=0; g.weightx=0.3; form.add(formLabel("Date:"), g);
            g.gridx=1; g.weightx=0.7;
            dateSpinner = new JSpinner(new SpinnerDateModel());
            dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
            dateSpinner.setPreferredSize(new Dimension(180, 32)); form.add(dateSpinner, g);

            g.gridx=0; g.gridy=1; form.add(formLabel("Time:"), g);
            g.gridx=1; timeSpinner = new JSpinner(new SpinnerDateModel());
            timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm")); form.add(timeSpinner, g);

            g.gridx=0; g.gridy=2; form.add(formLabel("Duration (min):"), g);
            g.gridx=1; txtDuration = styledField("60"); form.add(txtDuration, g);

            g.gridx=0; g.gridy=3; form.add(formLabel("Topic:"), g);
            g.gridx=1; txtTopic = styledField(""); form.add(txtTopic, g);

            g.gridx=0; g.gridy=4; form.add(formLabel("Location:"), g);
            g.gridx=1; txtLocation = styledField(""); form.add(txtLocation, g);

            g.gridx=0; g.gridy=5; form.add(formLabel("Status:"), g);
            g.gridx=1; comboStatus = new JComboBox<>(new String[]{"Scheduled","Completed","Cancelled"});
            comboStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13)); form.add(comboStatus, g);

            g.gridx=0; g.gridy=6; g.anchor=GridBagConstraints.NORTHWEST; form.add(formLabel("Notes:"), g);
            g.gridx=1; g.weighty=1.0; g.fill=GridBagConstraints.BOTH;
            txtNotes = new JTextArea(3, 20); txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtNotes.setLineWrap(true); form.add(new JScrollPane(txtNotes), g);

            add(form, BorderLayout.CENTER);

            JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
            bp.setBackground(new Color(248, 249, 250));
            JButton bs = createBtn(isEdit ? "Update Session" : "Create Session",
                isEdit ? new Color(41,128,185) : SUCCESS_COLOR, Color.WHITE);
            bs.addActionListener(e -> save()); bp.add(bs);
            JButton bc = createBtn("Cancel", new Color(149,165,166), Color.WHITE);
            bc.addActionListener(e -> dispose()); bp.add(bc);
            add(bp, BorderLayout.SOUTH);
        }

        private void loadData() {
            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM remedial_sessions WHERE session_id=?");
                ps.setInt(1, sessionId); ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    dateSpinner.setValue(rs.getDate("session_date"));
                    timeSpinner.setValue(rs.getTime("session_time"));
                    txtDuration.setText(String.valueOf(rs.getInt("duration_minutes")));
                    txtTopic.setText(rs.getString("topic_covered"));
                    txtLocation.setText(rs.getString("location"));
                    comboStatus.setSelectedItem(rs.getString("session_status"));
                    String n = rs.getString("notes"); if (n != null) txtNotes.setText(n);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        private void save() {
            String topic = txtTopic.getText().trim();
            if (topic.isEmpty()) { JOptionPane.showMessageDialog(this,"Topic required!","Validation",JOptionPane.WARNING_MESSAGE); return; }
            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
                int dur = 60; try { dur = Integer.parseInt(txtDuration.getText().trim()); } catch (Exception ignored) {}
                if (isEdit) {
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE remedial_sessions SET session_date=?,session_time=?,duration_minutes=?,topic_covered=?,location=?,session_status=?,notes=? WHERE session_id=?");
                    ps.setDate(1,new Date(((java.util.Date)dateSpinner.getValue()).getTime()));
                    ps.setTime(2,new Time(((java.util.Date)timeSpinner.getValue()).getTime()));
                    ps.setInt(3,dur); ps.setString(4,topic); ps.setString(5,txtLocation.getText().trim());
                    ps.setString(6,(String)comboStatus.getSelectedItem()); ps.setString(7,txtNotes.getText().trim());
                    ps.setInt(8,sessionId); ps.executeUpdate();
                    updateStatus("Session updated.", SUCCESS_COLOR);
                } else {
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO remedial_sessions(remedial_id,session_date,session_time,duration_minutes,topic_covered,location,faculty_id,session_status,notes) VALUES(?,?,?,?,?,?,?,?,?)");
                    ps.setInt(1,selectedRemedialId);
                    ps.setDate(2,new Date(((java.util.Date)dateSpinner.getValue()).getTime()));
                    ps.setTime(3,new Time(((java.util.Date)timeSpinner.getValue()).getTime()));
                    ps.setInt(4,dur); ps.setString(5,topic); ps.setString(6,txtLocation.getText().trim());
                    ps.setInt(7,facultyId); ps.setString(8,(String)comboStatus.getSelectedItem()); ps.setString(9,txtNotes.getText().trim());
                    ps.executeUpdate(); updateStatus("Session created.", SUCCESS_COLOR);
                }
                JOptionPane.showMessageDialog(this,"Session "+(isEdit?"updated":"created")+" successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
        }
    }

    // ─── ATTENDANCE DIALOG  (BUG FIXED) ──────────────────────────
    private class AttendanceDialog extends JDialog {
        private final int sessionId;
        private JComboBox<String> comboAttStatus;
        private JTextField txtCheckIn;
        private JComboBox<String> comboRating;
        private JTextField txtFeedback;

        AttendanceDialog(JFrame owner, int sessionId) {
            super(owner, "Attendance — Session #" + sessionId + " | " + selectedStudentName, true);
            this.sessionId = sessionId;
            buildUI();
        }

        private void buildUI() {
            setSize(560, 500); setLocationRelativeTo(getOwner());
            setLayout(new BorderLayout(0, 0));
            getContentPane().setBackground(new Color(248, 249, 250));

            // Header
            JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
            strip.setBackground(WARNING_COLOR);
            JLabel hdr = new JLabel("Mark Attendance — Session #" + sessionId);
            hdr.setFont(new Font("Segoe UI", Font.BOLD, 15)); hdr.setForeground(Color.WHITE);
            strip.add(hdr);
            add(strip, BorderLayout.NORTH);

            // Form
            int total    = getTotalSessions(selectedStudentId);
            int attended = getAttendedSessions(selectedStudentId);
            double pct   = total > 0 ? (attended * 100.0 / total) : 0;

            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(CARD_COLOR);
            form.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 15, 6, 15),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220)),
                    new EmptyBorder(14, 18, 14, 18))));
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(8, 8, 8, 8); g.fill = GridBagConstraints.HORIZONTAL;

            // Student info
            g.gridx=0; g.gridy=0; g.gridwidth=2;
            JLabel infoLbl = new JLabel("<html>Student: <b>" + selectedStudentName + "</b>" +
                "&nbsp;&nbsp; Previous Attendance: <b>" + attended + "/" + total +
                " (" + String.format("%.0f", pct) + "%)</b></html>");
            infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            infoLbl.setForeground(PRIMARY_COLOR);
            form.add(infoLbl, g); g.gridwidth=1;

            // Status
            g.gridx=0; g.gridy=1; g.weightx=0.35; form.add(formLabel("Attendance Status:"), g);
            g.gridx=1; g.weightx=0.65;
            comboAttStatus = new JComboBox<>(new String[]{"Present","Absent","Late"});
            comboAttStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
            styleComboColors(comboAttStatus);
            form.add(comboAttStatus, g);

            // Check-in time
            g.gridx=0; g.gridy=2; form.add(formLabel("Check-In Time (HH:mm):"), g);
            g.gridx=1; txtCheckIn = styledField(LocalTime.now().toString().substring(0, 5));
            form.add(txtCheckIn, g);

            // Rating
            g.gridx=0; g.gridy=3; form.add(formLabel("Performance Rating:"), g);
            g.gridx=1; comboRating = new JComboBox<>(new String[]{"Excellent","Good","Average","Poor"});
            comboRating.setFont(new Font("Segoe UI", Font.PLAIN, 13)); form.add(comboRating, g);

            // Feedback
            g.gridx=0; g.gridy=4; form.add(formLabel("Feedback (optional):"), g);
            g.gridx=1; txtFeedback = styledField(""); form.add(txtFeedback, g);

            // Quick-select strip — ONLY changes dropdown, does NOT call save()
            JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            quickPanel.setBackground(new Color(240, 244, 248));
            quickPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(210, 215, 220)),
                new EmptyBorder(2, 10, 2, 10)));
            JLabel ql = new JLabel("Quick select  (then click Save Attendance):");
            ql.setFont(new Font("Segoe UI", Font.ITALIC, 12)); ql.setForeground(new Color(90,90,90));
            quickPanel.add(ql);

            JButton qP = createBtn("Present", SUCCESS_COLOR, Color.WHITE);
            qP.addActionListener(e -> { comboAttStatus.setSelectedItem("Present"); });
            quickPanel.add(qP);

            JButton qA = createBtn("Absent", DANGER_COLOR, Color.WHITE);
            qA.addActionListener(e -> { comboAttStatus.setSelectedItem("Absent"); });
            quickPanel.add(qA);

            JButton qL = createBtn("Late", WARNING_COLOR, Color.WHITE);
            qL.addActionListener(e -> { comboAttStatus.setSelectedItem("Late"); });
            quickPanel.add(qL);

            // Wrap form + quick strip into CENTER
            JPanel center = new JPanel(new BorderLayout());
            center.setBackground(new Color(248, 249, 250));
            center.add(form, BorderLayout.CENTER);
            center.add(quickPanel, BorderLayout.SOUTH);
            add(center, BorderLayout.CENTER);

            // Load existing attendance (so edits show current values)
            loadExistingAttendance();

            // ── SOUTH: big Save button ──────────────────────────────
            JPanel btnPanel = new JPanel(new BorderLayout(8, 0));
            btnPanel.setBackground(new Color(248, 249, 250));
            btnPanel.setBorder(new EmptyBorder(10, 15, 12, 15));

            JButton btnSave = new JButton("  Save Attendance  ");
            btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btnSave.setBackground(new Color(39, 174, 96));
            btnSave.setForeground(Color.WHITE);
            btnSave.setFocusPainted(false);
            btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnSave.setBorder(BorderFactory.createEmptyBorder(11, 30, 11, 30));
            btnSave.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btnSave.setBackground(new Color(33, 150, 83)); }
                public void mouseExited(MouseEvent e)  { btnSave.setBackground(new Color(39, 174, 96)); }
            });
            // ✅ ONLY the Save button calls save()
            btnSave.addActionListener(e -> save());

            JButton btnCancel = createBtn("Cancel", new Color(149, 165, 166), Color.WHITE);
            btnCancel.addActionListener(e -> dispose());

            btnPanel.add(btnSave, BorderLayout.CENTER);
            btnPanel.add(btnCancel, BorderLayout.EAST);
            add(btnPanel, BorderLayout.SOUTH);
        }

        // Pre-populate form with previously saved attendance if it exists
        private void loadExistingAttendance() {
            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT attendance_status, check_in_time, performance_rating, faculty_feedback " +
                    "FROM session_attendance WHERE session_id = ? AND student_id = ?");
                ps.setInt(1, sessionId);
                ps.setInt(2, selectedStudentId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String s = rs.getString("attendance_status");
                    if (s != null) comboAttStatus.setSelectedItem(s);
                    Time t = rs.getTime("check_in_time");
                    if (t != null) txtCheckIn.setText(t.toString().substring(0, 5));
                    String r = rs.getString("performance_rating");
                    if (r != null) comboRating.setSelectedItem(r);
                    String fb = rs.getString("faculty_feedback");
                    if (fb != null) txtFeedback.setText(fb);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        // ✅ FIXED: called ONLY by the Save button
        private void save() {
            // Guard
            if (selectedStudentId <= 0) {
                JOptionPane.showMessageDialog(this,
                    "No student selected. Close and select a student first.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String status   = (String) comboAttStatus.getSelectedItem();
            String checkIn  = txtCheckIn.getText().trim();
            String rating   = (String) comboRating.getSelectedItem();
            String feedback = txtFeedback.getText().trim();

            // Validate & normalise time  (accept H:mm or HH:mm)
            if (!checkIn.matches("\\d{1,2}:\\d{2}(:\\d{2})?")) {
                JOptionPane.showMessageDialog(this,
                    "Check-in time must be in HH:mm format, e.g. 09:30",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (checkIn.indexOf(':') == 1) checkIn = "0" + checkIn;   // pad "9:30" -> "09:30"
            if (checkIn.length() == 5)     checkIn += ":00";           // "09:30" -> "09:30:00"

            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {

                // Check for existing record
                PreparedStatement chk = conn.prepareStatement(
                    "SELECT COUNT(*) FROM session_attendance WHERE session_id = ? AND student_id = ?");
                chk.setInt(1, sessionId); chk.setInt(2, selectedStudentId);
                ResultSet cr = chk.executeQuery();
                boolean exists = cr.next() && cr.getInt(1) > 0;
                cr.close(); chk.close();

                if (exists) {
                    // ── UPDATE existing record ──
                    PreparedStatement upd = conn.prepareStatement(
                        "UPDATE session_attendance " +
                        "SET attendance_status = ?, check_in_time = ?, " +
                        "    performance_rating = ?, faculty_feedback = ? " +
                        "WHERE session_id = ? AND student_id = ?");
                    upd.setString(1, status);
                    upd.setTime(2, Time.valueOf(checkIn));
                    upd.setString(3, rating);
                    upd.setString(4, feedback);
                    upd.setInt(5, sessionId);
                    upd.setInt(6, selectedStudentId);
                    upd.executeUpdate();
                    upd.close();
                } else {
                    // ── INSERT new record ──
                    PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO session_attendance " +
                        "(session_id, student_id, attendance_status, check_in_time, performance_rating, faculty_feedback) " +
                        "VALUES (?, ?, ?, ?, ?, ?)");
                    ins.setInt(1, sessionId);
                    ins.setInt(2, selectedStudentId);
                    ins.setString(3, status);
                    ins.setTime(4, Time.valueOf(checkIn));
                    ins.setString(5, rating);
                    ins.setString(6, feedback);
                    ins.executeUpdate();
                    ins.close();
                }

                JOptionPane.showMessageDialog(this,
                    "Attendance saved!\n\n" +
                    "Student : " + selectedStudentName + "\n" +
                    "Status  : " + status + "\n" +
                    "Check-in: " + checkIn.substring(0, 5) + "\n" +
                    "Rating  : " + rating,
                    "Saved", JOptionPane.INFORMATION_MESSAGE);

                updateStatus("Attendance saved: " + selectedStudentName + " -> " + status, SUCCESS_COLOR);
                dispose();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Failed to save attendance:\n" + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ─── ENTER MARKS DIALOG ──────────────────────────────────────
    private class EnterMarksDialog extends JDialog {
        private final int sessionId;
        private JTextField txtNewMarks;
        private JLabel lblComparison;

        EnterMarksDialog(JFrame owner, int sessionId) {
            super(owner, "Enter Marks — Session #" + sessionId, true);
            this.sessionId = sessionId; buildUI();
        }

        private void buildUI() {
            setSize(480, 360); setLocationRelativeTo(getOwner());
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(new Color(248, 249, 250));

            JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            strip.setBackground(new Color(142, 68, 173));
            JLabel hdr = new JLabel("Enter Marks — Compare with Original Failed Marks");
            hdr.setFont(new Font("Segoe UI", Font.BOLD, 14)); hdr.setForeground(Color.WHITE);
            strip.add(hdr); add(strip, BorderLayout.NORTH);

            int fm = getFailedMarks(selectedStudentId);
            JPanel info = new JPanel(new GridBagLayout());
            info.setBackground(CARD_COLOR); info.setBorder(new EmptyBorder(15, 25, 10, 25));
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(8,8,8,8); g.fill = GridBagConstraints.HORIZONTAL;

            g.gridx=0;g.gridy=0;g.weightx=0.4; info.add(formLabel("Student:"),g);
            g.gridx=1;g.weightx=0.6;
            JLabel ls = new JLabel(selectedStudentName); ls.setFont(new Font("Segoe UI",Font.BOLD,13));
            info.add(ls,g);

            g.gridx=0;g.gridy=1; info.add(formLabel("Original Failed Marks:"),g);
            g.gridx=1;
            JLabel lf = new JLabel(fm + " / " + currentMaxMarks + "  (Pass: " + currentPassMarks + ")");
            lf.setFont(new Font("Segoe UI",Font.BOLD,13)); lf.setForeground(DANGER_COLOR);
            info.add(lf,g);

            g.gridx=0;g.gridy=2; info.add(formLabel("New Marks (0-" + currentMaxMarks + "):"),g);
            g.gridx=1; txtNewMarks = styledField(""); info.add(txtNewMarks,g);

            g.gridx=0;g.gridy=3;g.gridwidth=2;
            lblComparison = new JLabel("  Enter marks above to see comparison.");
            lblComparison.setFont(new Font("Segoe UI",Font.BOLD,13));
            lblComparison.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,210,210)),new EmptyBorder(8,12,8,12)));
            lblComparison.setOpaque(true); lblComparison.setBackground(new Color(245,245,245));
            info.add(lblComparison,g);

            txtNewMarks.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) { updateComp(fm); }
            });

            add(info, BorderLayout.CENTER);
            loadExistingMarks(); updateComp(fm);

            JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER,12,10));
            bp.setBackground(new Color(248,249,250));
            JButton bs = createBtn("Save Marks", new Color(142,68,173), Color.WHITE);
            bs.addActionListener(e -> save(fm)); bp.add(bs);
            JButton bc = createBtn("Cancel", new Color(149,165,166), Color.WHITE);
            bc.addActionListener(e -> dispose()); bp.add(bc);
            add(bp, BorderLayout.SOUTH);
        }

        private void loadExistingMarks() {
            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT new_marks FROM remedial_marks WHERE session_id=? AND student_id=?");
                ps.setInt(1,sessionId); ps.setInt(2,selectedStudentId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) txtNewMarks.setText(String.valueOf(rs.getInt("new_marks")));
            } catch (Exception e) { e.printStackTrace(); }
        }

        private void updateComp(int fm) {
            String t = txtNewMarks.getText().trim();
            if (t.isEmpty()) { lblComparison.setText("  Enter marks above."); lblComparison.setBackground(new Color(245,245,245)); lblComparison.setForeground(Color.DARK_GRAY); return; }
            try {
                int nm = Integer.parseInt(t);
                if (nm < 0 || nm > currentMaxMarks) { lblComparison.setText("  Marks must be 0 to "+currentMaxMarks); lblComparison.setBackground(new Color(255,243,205)); lblComparison.setForeground(new Color(133,100,0)); return; }
                if (nm > fm) {
                    lblComparison.setText("  IMPROVED: "+nm+" vs "+fm+" (+"+(nm-fm)+" marks)  "+(nm>=currentPassMarks?"PASSED!":"Still below pass"));
                    lblComparison.setBackground(new Color(212,237,218)); lblComparison.setForeground(new Color(21,87,36));
                } else if (nm == fm) {
                    lblComparison.setText("  NO CHANGE: Still "+nm+" marks"); lblComparison.setBackground(new Color(255,243,205)); lblComparison.setForeground(new Color(133,100,0));
                } else {
                    lblComparison.setText("  DROPPED: "+nm+" vs "+fm+" (-"+(fm-nm)+" marks)"); lblComparison.setBackground(new Color(248,215,218)); lblComparison.setForeground(new Color(114,28,36));
                }
            } catch (NumberFormatException ex) { lblComparison.setText("  Enter a valid number."); lblComparison.setBackground(new Color(248,215,218)); }
        }

        private void save(int fm) {
            String t = txtNewMarks.getText().trim();
            if (t.isEmpty()) { JOptionPane.showMessageDialog(this,"Enter marks first!","Required",JOptionPane.WARNING_MESSAGE); return; }
            int nm; try { nm = Integer.parseInt(t); } catch (Exception e) { JOptionPane.showMessageDialog(this,"Invalid number.","Error",JOptionPane.ERROR_MESSAGE); return; }
            if (nm < 0 || nm > currentMaxMarks) { JOptionPane.showMessageDialog(this,"Marks must be 0-"+currentMaxMarks,"Validation",JOptionPane.WARNING_MESSAGE); return; }
            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
                PreparedStatement chk = conn.prepareStatement("SELECT COUNT(*) FROM remedial_marks WHERE session_id=? AND student_id=?");
                chk.setInt(1,sessionId); chk.setInt(2,selectedStudentId);
                ResultSet cr = chk.executeQuery(); boolean ex = cr.next()&&cr.getInt(1)>0; cr.close(); chk.close();
                if (ex) {
                    PreparedStatement u = conn.prepareStatement("UPDATE remedial_marks SET new_marks=? WHERE session_id=? AND student_id=?");
                    u.setInt(1,nm); u.setInt(2,sessionId); u.setInt(3,selectedStudentId); u.executeUpdate();
                } else {
                    PreparedStatement i = conn.prepareStatement("INSERT INTO remedial_marks(session_id,student_id,new_marks) VALUES(?,?,?)");
                    i.setInt(1,sessionId); i.setInt(2,selectedStudentId); i.setInt(3,nm); i.executeUpdate();
                }
                String res = nm>fm?"Improved":nm==fm?"No Change":"Dropped";
                JOptionPane.showMessageDialog(this,"Marks saved!\n"+selectedStudentName+": "+nm+"/"+currentMaxMarks+"\nOriginal: "+fm+"/"+currentMaxMarks+"\nResult: "+res,"Saved",JOptionPane.INFORMATION_MESSAGE);
                updateStatus("Marks saved: "+selectedStudentName+" -> "+nm+" ("+res+")", SUCCESS_COLOR);
                dispose();
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
        }
    }

    // ─── PROGRESS DIALOG ─────────────────────────────────────────
    private class ProgressDialog extends JDialog {
        ProgressDialog(JFrame owner, int sessionId) {
            super(owner, "Progress Report — " + selectedStudentName, true);
            setSize(640, 560); setLocationRelativeTo(owner);
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(new Color(248, 249, 250));

            JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            strip.setBackground(new Color(22, 160, 133));
            JLabel hdr = new JLabel("Student Progress Report");
            hdr.setFont(new Font("Segoe UI", Font.BOLD, 16)); hdr.setForeground(Color.WHITE);
            strip.add(hdr); add(strip, BorderLayout.NORTH);

            JTextArea report = new JTextArea();
            report.setEditable(false);
            report.setFont(new Font("Courier New", Font.PLAIN, 13));
            report.setMargin(new Insets(15, 20, 15, 20));
            report.setBackground(CARD_COLOR);
            report.setLineWrap(true); report.setWrapStyleWord(true);

            try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT r.failed_marks,r.max_marks,r.pass_marks,r.remedial_status,r.failed_percentage,s.name,s.roll_no,e.exam_name " +
                    "FROM remedial_tracking r JOIN student s ON r.student_id=s.student_id JOIN exam e ON r.failed_exam_id=e.exam_id WHERE r.remedial_id=?");
                ps.setInt(1, selectedRemedialId); ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String name=rs.getString("name"), rollNo=rs.getString("roll_no"), examName=rs.getString("exam_name"), rStatus=rs.getString("remedial_status");
                    int fm=rs.getInt("failed_marks"), maxM=rs.getInt("max_marks"), passM=rs.getInt("pass_marks");
                    double fPct=rs.getDouble("failed_percentage");

                    PreparedStatement mps = conn.prepareStatement("SELECT new_marks FROM remedial_marks WHERE session_id=? AND student_id=?");
                    mps.setInt(1,sessionId); mps.setInt(2,selectedStudentId);
                    ResultSet mrs=mps.executeQuery(); Integer nm=mrs.next()?mrs.getInt("new_marks"):null; mrs.close(); mps.close();

                    PreparedStatement aps = conn.prepareStatement(
                        "SELECT rm.new_marks,rs2.session_date,rs2.topic_covered FROM remedial_marks rm " +
                        "JOIN remedial_sessions rs2 ON rm.session_id=rs2.session_id " +
                        "WHERE rs2.remedial_id=? AND rm.student_id=? ORDER BY rs2.session_date");
                    aps.setInt(1,selectedRemedialId); aps.setInt(2,selectedStudentId);
                    ResultSet ars=aps.executeQuery();
                    StringBuilder hist=new StringBuilder(); int best=fm,latest=fm,cnt=0;
                    while(ars.next()){ int m=ars.getInt("new_marks"); if(m>best)best=m; latest=m;
                        hist.append("  * ").append(ars.getDate("session_date")).append("  [").append(ars.getString("topic_covered")).append("]  -> ").append(m).append("/").append(maxM).append(m>fm?"  IMPROVED":m==fm?"  SAME":"  DROPPED").append("\n"); cnt++; }
                    ars.close(); aps.close();

                    int ts=getTotalSessions(selectedStudentId), at=getAttendedSessions(selectedStudentId);
                    double ap=ts>0?(at*100.0/ts):0;

                    StringBuilder sb=new StringBuilder();
                    sb.append("==================================================\n   REMEDIAL PROGRESS REPORT\n==================================================\n\n");
                    sb.append("  Student : ").append(name).append("  (Roll: ").append(rollNo).append(")\n");
                    sb.append("  Exam    : ").append(examName).append("\n");
                    sb.append("  Status  : ").append(rStatus).append("\n\n");
                    sb.append("--------------------------------------------------\n  ORIGINAL FAILED MARKS\n");
                    sb.append("     ").append(fm).append(" / ").append(maxM).append("  (").append(String.format("%.1f",fPct)).append("%)  Pass: ").append(passM).append("\n\n");
                    if(nm!=null){
                        int d=nm-fm;
                        sb.append("  CURRENT SESSION MARKS\n     ").append(nm).append(" / ").append(maxM).append("  (").append(String.format("%.1f",nm*100.0/maxM)).append("%)\n");
                        if(d>0){ sb.append("     >> IMPROVED by +").append(d).append(" marks\n"); sb.append(nm>=passM?"     >> PASSED!\n":"     >> Still "+(passM-nm)+" marks below passing.\n"); }
                        else if(d==0) sb.append("     >> NO CHANGE\n");
                        else sb.append("     >> DROPPED by ").append(-d).append(" marks\n");
                    } else sb.append("  No marks entered for this session yet.\n");
                    if(cnt>0){
                        sb.append("\n--------------------------------------------------\n  ALL SESSIONS HISTORY (").append(cnt).append(" entries)\n");
                        sb.append("     Best  : ").append(best).append("/").append(maxM).append("\n");
                        sb.append("     Latest: ").append(latest).append("/").append(maxM).append("\n\n").append(hist);
                    }
                    sb.append("\n--------------------------------------------------\n  ATTENDANCE\n");
                    sb.append("     ").append(at).append(" / ").append(ts).append(" sessions  (").append(String.format("%.0f",ap)).append("%)\n");
                    if(ap<50) sb.append("     >> Low attendance!\n"); else if(ap>=75) sb.append("     >> Good attendance!\n");
                    sb.append("\n==================================================\n");
                    report.setText(sb.toString()); report.setCaretPosition(0);
                }
            } catch (Exception e) { e.printStackTrace(); report.setText("Error: "+e.getMessage()); }

            add(new JScrollPane(report), BorderLayout.CENTER);
            JPanel btns=new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
            btns.setBackground(new Color(248,249,250));
            JButton bc=createBtn("Close",SUCCESS_COLOR,Color.WHITE); bc.addActionListener(e->dispose()); btns.add(bc);
            add(btns, BorderLayout.SOUTH);
        }
    }

    // ─── DB HELPERS ──────────────────────────────────────────────
    private int getStudentIdFromRemedial(int remedialId) {
        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT student_id FROM remedial_tracking WHERE remedial_id=?");
            ps.setInt(1, remedialId); ResultSet rs=ps.executeQuery(); if(rs.next()) return rs.getInt("student_id");
        } catch (Exception e) { e.printStackTrace(); } return -1;
    }

    private int getTotalSessions(int studentId) {
        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM remedial_sessions rs JOIN remedial_tracking r ON rs.remedial_id=r.remedial_id WHERE r.student_id=?");
            ps.setInt(1,studentId); ResultSet rs=ps.executeQuery(); if(rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); } return 0;
    }

    private int getAttendedSessions(int studentId) {
        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(DISTINCT sa.session_id) FROM session_attendance sa " +
                "JOIN remedial_sessions rs ON sa.session_id=rs.session_id " +
                "JOIN remedial_tracking r ON rs.remedial_id=r.remedial_id " +
                "WHERE r.student_id=? AND sa.attendance_status='Present'");
            ps.setInt(1,studentId); ResultSet rs=ps.executeQuery(); if(rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); } return 0;
    }

    private int getFailedMarks(int studentId) {
        try (Connection conn = com.college.sms.util.DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT failed_marks FROM remedial_tracking WHERE student_id=? AND subject_id=? AND failed_exam_id=?");
            ps.setInt(1,studentId); ps.setInt(2,currentSubjectId); ps.setInt(3,currentExamId);
            ResultSet rs=ps.executeQuery(); if(rs.next()) return rs.getInt("failed_marks");
        } catch (Exception e) { e.printStackTrace(); } return 0;
    }

    // ─── UI HELPERS ──────────────────────────────────────────────
    private void updateStatus(String msg, Color c) { statusLabel.setText(msg); statusLabel.setForeground(c); }

    private void disableSessionButtons() {
        btnEditSession.setEnabled(false); btnDeleteSession.setEnabled(false);
        btnTakeAttendance.setEnabled(false); btnEnterMarks.setEnabled(false); btnViewProgress.setEnabled(false);
    }

    private void hideColumn(JTable t, int col) {
        t.getColumnModel().getColumn(col).setMinWidth(0);
        t.getColumnModel().getColumn(col).setMaxWidth(0);
        t.getColumnModel().getColumn(col).setWidth(0);
    }

    private JButton createBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI",Font.BOLD,12)); b.setForeground(fg); b.setBackground(bg);
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(7,13,7,13));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private JComboBox<String> createComboBox(int width) {
        JComboBox<String> c=new JComboBox<>();
        c.setFont(new Font("Segoe UI",Font.PLAIN,13));
        c.setPreferredSize(new Dimension(width,34)); c.setBackground(Color.WHITE); return c;
    }

    private JLabel label(String text) { JLabel l=new JLabel(text); l.setFont(new Font("Segoe UI",Font.PLAIN,13)); return l; }

    private JLabel formLabel(String text) { JLabel l=new JLabel(text); l.setFont(new Font("Segoe UI",Font.BOLD,12)); l.setForeground(new Color(60,60,60)); return l; }

    private JTextField styledField(String def) {
        JTextField f=new JTextField(def); f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200,200,200)),BorderFactory.createEmptyBorder(4,8,4,8)));
        return f;
    }

    private Border cardBorder(String title) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(210,210,210)),"  "+title+"  ",
                javax.swing.border.TitledBorder.LEFT,javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI",Font.BOLD,13),PRIMARY_COLOR),
            new EmptyBorder(6,6,6,6));
    }

    private void styleTable(JTable t) {
        t.setRowHeight(30); t.setFont(new Font("Segoe UI",Font.PLAIN,12));
        t.setGridColor(new Color(235,235,235)); t.setSelectionBackground(new Color(173,216,230)); t.setSelectionForeground(Color.BLACK);
        t.setAutoCreateRowSorter(true);
        JTableHeader h=t.getTableHeader(); h.setFont(new Font("Segoe UI",Font.BOLD,12));
        h.setBackground(PRIMARY_COLOR); h.setForeground(Color.WHITE); h.setReorderingAllowed(false);
        t.setIntercellSpacing(new Dimension(10,2)); t.setShowHorizontalLines(true); t.setShowVerticalLines(false);
    }

    private void styleSessionsTable(JTable t) {
        styleTable(t);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, sel, focus, row, col);
                if (!sel) {
                    String s = value == null ? "" : value.toString();
                    if (col == 6) {
                        if (s.contains("IMPROVED"))     { c.setBackground(new Color(212,237,218)); c.setForeground(new Color(21,87,36)); }
                        else if (s.contains("DROPPED")) { c.setBackground(new Color(248,215,218)); c.setForeground(new Color(114,28,36)); }
                        else if (s.contains("NO CHANGE")){ c.setBackground(new Color(255,243,205)); c.setForeground(new Color(133,100,0)); }
                        else { c.setBackground(new Color(245,245,245)); c.setForeground(Color.DARK_GRAY); }
                    } else if (col == 7) {
                        if ("Present".equals(s))      { c.setBackground(new Color(212,237,218)); c.setForeground(new Color(21,87,36)); }
                        else if ("Absent".equals(s))  { c.setBackground(new Color(248,215,218)); c.setForeground(new Color(114,28,36)); }
                        else if ("Late".equals(s))    { c.setBackground(new Color(255,243,205)); c.setForeground(new Color(133,100,0)); }
                        else { c.setBackground(Color.WHITE); c.setForeground(Color.DARK_GRAY); }
                    } else {
                        c.setBackground(row%2==0 ? Color.WHITE : new Color(248,249,250));
                        c.setForeground(Color.BLACK);
                    }
                    ((JLabel)c).setFont(new Font("Segoe UI",Font.PLAIN,12));
                }
                return c;
            }
        });
    }

    private void styleComboColors(JComboBox<String> combo) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String s = value == null ? "" : value.toString();
                if (!isSelected) {
                    if ("Present".equals(s))     { c.setBackground(new Color(212,237,218)); c.setForeground(new Color(21,87,36)); }
                    else if ("Absent".equals(s)) { c.setBackground(new Color(248,215,218)); c.setForeground(new Color(114,28,36)); }
                    else if ("Late".equals(s))   { c.setBackground(new Color(255,243,205)); c.setForeground(new Color(133,100,0)); }
                }
                return c;
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame mock = new JFrame("Faculty Dashboard");
            mock.setSize(1000, 650); mock.setVisible(true);
            new RemedialSessionManagementUI(1, mock);
        });
    }
}