// 
//package com.college.sms.ui;
//
//import com.college.sms.dao.ClassDAO;
//import com.college.sms.dao.ExamDAO;
//import com.college.sms.dao.MarksDAO;
//import com.college.sms.dao.StudentDAO;
//import com.college.sms.dao.SubjectDAO;
//import com.college.sms.model.Exam;
//import com.college.sms.model.Subject;
//
//import javax.swing.*;
//import javax.swing.border.TitledBorder;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.sql.SQLException;
//import java.util.List;
//
//public class MarksManagementUI extends JFrame {
//
//    private JComboBox<String> comboClass, comboSubject, comboExam;
//    private JTextField txtMarks;
//    private JTable studentTable, marksTable;
//    private DefaultTableModel studentModel, marksModel;
//    private ClassDAO classDAO;
//    private StudentDAO studentDAO;
//    private SubjectDAO subjectDAO;
//    private ExamDAO examDAO;
//    private MarksDAO marksDAO;
//    private int facultyId;
//    private JFrame previousUI;
//    private int selectedStudentId = -1;
//    private JLabel statusLabel;
//
//    // ✅ BACKWARD-COMPATIBLE CONSTRUCTOR (fixes FacultyDashboard call)
//    public MarksManagementUI(JFrame previousUI) throws SQLException {
//        if (previousUI instanceof FacultyDashboard) {
//            this.facultyId = ((FacultyDashboard) previousUI).getFacultyId();
//        } else {
//            this.facultyId = 1; // Fallback for testing
//        }
//        this.previousUI = previousUI;
//        this.classDAO = new ClassDAO();
//        this.studentDAO = new StudentDAO();
//        this.subjectDAO = new SubjectDAO();
//        this.examDAO = new ExamDAO();
//        this.marksDAO = new MarksDAO();
//        initComponents();
//        loadData();
//    }
//
//    // ✅ PROPER CONSTRUCTOR (recommended)
//    public MarksManagementUI(int facultyId, JFrame previousUI) throws SQLException {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        this.classDAO = new ClassDAO();
//        this.studentDAO = new StudentDAO();
//        this.subjectDAO = new SubjectDAO();
//        this.examDAO = new ExamDAO();
//        this.marksDAO = new MarksDAO();
//        initComponents();
//        loadData();
//    }
//
//    private void initComponents() {
//        setTitle("📝 Marks Management | Faculty ID: " + facultyId);
//        setSize(1050, 680);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(new BorderLayout(15, 15));
//        getContentPane().setBackground(new Color(248, 249, 252));
//
//        // ===== HEADER PANEL =====
//        JPanel headerPanel = new JPanel(new BorderLayout());
//        headerPanel.setBackground(new Color(41, 128, 185));
//        headerPanel.setPreferredSize(new Dimension(0, 70)); // Fixed width issue
//        
//        JLabel lblTitle = new JLabel("Manage Student Marks");
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
//        lblTitle.setForeground(Color.WHITE);
//        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
//        headerPanel.add(lblTitle, BorderLayout.WEST);
//        
//        JButton btnBack = new JButton("🔙 Back to Dashboard");
//        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        btnBack.setBackground(new Color(243, 156, 18));
//        btnBack.setForeground(Color.WHITE);
//        btnBack.setFocusPainted(false);
//        btnBack.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
//        btnBack.addActionListener(e -> {
//            dispose();
//            if (previousUI != null) previousUI.setVisible(true);
//        });
//        headerPanel.add(btnBack, BorderLayout.EAST);
//
//        // ===== CLASS SELECTION PANEL =====
//        JPanel classPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
//        classPanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
//            "Step 1: Select Your Class",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 15),
//            new Color(41, 128, 185)
//        ));
//        classPanel.setBackground(Color.WHITE);
//        
//        classPanel.add(new JLabel("🏫 Class:"));
//        comboClass = new JComboBox<>();
//        comboClass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        comboClass.setPreferredSize(new Dimension(250, 32));
//        classPanel.add(comboClass);
//
//        // ✅ CRITICAL FIX: Combine header and class panels vertically
//        JPanel topPanel = new JPanel(new BorderLayout());
//        topPanel.add(headerPanel, BorderLayout.NORTH);
//        topPanel.add(classPanel, BorderLayout.CENTER);
//        add(topPanel, BorderLayout.NORTH);
//
//        // ===== SPLIT PANE: Students + Marks Entry =====
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//        splitPane.setDividerLocation(420);
//        splitPane.setResizeWeight(0.4);
//
//        // ===== STUDENTS TABLE =====
//        studentModel = new DefaultTableModel(new String[]{"ID", "Roll No", "Name"}, 0) {
//            public boolean isCellEditable(int row, int column) { return false; }
//        };
//        
//        studentTable = new JTable(studentModel);
//        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        studentTable.setRowHeight(28);
//        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
//        studentTable.getTableHeader().setBackground(new Color(52, 152, 219));
//        studentTable.getTableHeader().setForeground(Color.WHITE);
//        
//        // Zebra striping
//        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            public Component getTableCellRendererComponent(JTable table, Object value,
//                                                          boolean isSelected, boolean hasFocus,
//                                                          int row, int column) {
//                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                if (!isSelected) {
//                    c.setBackground(row % 2 == 0 ? new Color(248, 249, 252) : Color.WHITE);
//                }
//                return c;
//            }
//        });
//        
//        // Student selection handler
//        studentTable.getSelectionModel().addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) {
//                handleStudentSelection();
//            }
//        });
//        
//        JScrollPane studentScroll = new JScrollPane(studentTable);
//        studentScroll.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(52, 152, 219)),
//            "Step 2: Select Student",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 14),
//            new Color(52, 152, 219)
//        ));
//        splitPane.setLeftComponent(studentScroll);
//
//        // ===== MARKS ENTRY PANEL =====
//        JPanel marksPanel = new JPanel(new BorderLayout(15, 15));
//        marksPanel.setBackground(Color.WHITE);
//        marksPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        // Form panel (5 rows to accommodate new button)
//        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 12));
//        formPanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
//            "Step 3: Enter Marks",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 15),
//            new Color(155, 89, 182)
//        ));
//        formPanel.setBackground(Color.WHITE);
//
//        formPanel.add(createLabel("📚 Subject:"));
//        comboSubject = new JComboBox<>();
//        comboSubject.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        comboSubject.setPreferredSize(new Dimension(200, 32));
//        formPanel.add(comboSubject);
//
//        formPanel.add(createLabel("📝 Exam:"));
//        comboExam = new JComboBox<>();
//        comboExam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        comboExam.setPreferredSize(new Dimension(200, 32));
//        formPanel.add(comboExam);
//
//        formPanel.add(createLabel("💯 Marks Obtained:"));
//        txtMarks = new JTextField();
//        txtMarks.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        txtMarks.setPreferredSize(new Dimension(200, 32));
//        formPanel.add(txtMarks);
//
//        formPanel.add(new JLabel()); // spacer
//        
//        // ✅ NEW BUTTON: View Result Summary (minimal implementation)
//        JButton btnResultSummary = new JButton("📊 View Result Summary");
//        btnResultSummary.setBackground(new Color(155, 89, 182));
//        btnResultSummary.setForeground(Color.WHITE);
//        btnResultSummary.setFocusPainted(false);
//        btnResultSummary.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        btnResultSummary.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
//        btnResultSummary.addActionListener(e -> {
//            new ResultSummaryUI(facultyId, MarksManagementUI.this).setVisible(true);
//            setVisible(false); // Hide current UI while viewing results
//        });
//        formPanel.add(btnResultSummary);
//
//        JButton btnAdd = createButton("✅ Save Marks", new Color(46, 204, 113));
//        formPanel.add(btnAdd);
//
//        marksPanel.add(formPanel, BorderLayout.NORTH);
//
//        // Marks table
//        marksModel = new DefaultTableModel(
//            new String[]{"Stu ID", "Sub ID", "Exam ID", "Roll No", "Student", "Subject", "Exam", "Marks", "Result"}, 0
//        ) {
//            public boolean isCellEditable(int row, int column) { return false; }
//        };
//        
//        marksTable = new JTable(marksModel);
//        marksTable.setRowHeight(28);
//        marksTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        marksTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
//        marksTable.getTableHeader().setBackground(new Color(142, 68, 173));
//        marksTable.getTableHeader().setForeground(Color.WHITE);
//        
//        // Hide ID columns
//        for (int i = 0; i < 3; i++) {
//            marksTable.getColumnModel().getColumn(i).setMinWidth(0);
//            marksTable.getColumnModel().getColumn(i).setMaxWidth(0);
//        }
//        
//        // Color-coded results
//        marksTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
//            public Component getTableCellRendererComponent(JTable table, Object value,
//                                                          boolean isSelected, boolean hasFocus,
//                                                          int row, int column) {
//                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                String result = value.toString();
//                if ("Pass".equalsIgnoreCase(result)) {
//                    c.setBackground(new Color(200, 230, 201));
//                    c.setForeground(new Color(27, 94, 32));
//                } else {
//                    c.setBackground(new Color(255, 205, 210));
//                    c.setForeground(new Color(183, 28, 28));
//                }
//                return c;
//            }
//        });
//        
//        JScrollPane marksScroll = new JScrollPane(marksTable);
//        marksScroll.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(142, 68, 173)),
//            "Marks Records for Selected Class",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 14),
//            new Color(142, 68, 173)
//        ));
//        marksPanel.add(marksScroll, BorderLayout.CENTER);
//
//        splitPane.setRightComponent(marksPanel);
//        add(splitPane, BorderLayout.CENTER);
//
//        // ===== STATUS BAR =====
//        statusLabel = new JLabel("Select a class to begin managing marks");
//        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
//        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        statusLabel.setBackground(new Color(236, 240, 241));
//        statusLabel.setOpaque(true);
//        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        add(statusLabel, BorderLayout.SOUTH);
//
//        // ===== ACTION LISTENERS =====
//        comboClass.addActionListener(e -> {
//            if (comboClass.getSelectedItem() != null && !comboClass.getSelectedItem().toString().contains("--")) {
//                loadStudents();
//                loadSubjects();
//            }
//        });
//        
//        comboSubject.addActionListener(e -> {
//            if (comboSubject.getSelectedItem() != null && selectedStudentId != -1) {
//                loadExams();
//            }
//        });
//        
//        btnAdd.addActionListener(e -> saveMarks());
//    }
//
//    private JLabel createLabel(String text) {
//        JLabel label = new JLabel(text);
//        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        label.setForeground(new Color(51, 51, 51));
//        return label;
//    }
//
//    private JButton createButton(String text, Color bgColor) {
//        JButton btn = new JButton(text);
//        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        btn.setForeground(Color.WHITE);
//        btn.setBackground(bgColor);
//        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
//        btn.setFocusPainted(false);
//        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        
//        // Hover effect
//        btn.addMouseListener(new MouseAdapter() {
//            public void mouseEntered(MouseEvent e) {
//                btn.setBackground(bgColor.darker());
//            }
//            public void mouseExited(MouseEvent e) {
//                btn.setBackground(bgColor);
//            }
//        });
//        
//        return btn;
//    }
//
//    private void loadData() {
//        loadClasses();
//    }
//
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
//        
//        if (classes.isEmpty()) {
//            comboClass.addItem("-- No classes assigned to you --");
//            statusLabel.setText("⚠️ No classes assigned. Contact admin to assign classes.");
//            JOptionPane.showMessageDialog(this,
//                "No classes found for your account!\nContact admin to assign classes to your faculty account.",
//                "No Classes Available", JOptionPane.WARNING_MESSAGE);
//        } else {
//            for (String[] c : classes) {
//                comboClass.addItem(c[0] + " - " + c[1]);
//            }
//            comboClass.setSelectedIndex(0);
//            loadStudents();
//            loadSubjects();
//        }
//    }
//
//    private void loadStudents() {
//        studentModel.setRowCount(0);
//        selectedStudentId = -1;
//        
//        if (comboClass.getSelectedItem() == null || 
//            comboClass.getSelectedItem().toString().contains("-- No classes")) {
//            statusLabel.setText("⚠️ Select a valid class first");
//            return;
//        }
//        
//        try {
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            List<String[]> students = studentDAO.getStudentsByClass(classId);
//            
//            if (students.isEmpty()) {
//                statusLabel.setText("No students found in this class. Add students first in Student Management.");
//            } else {
//                for (String[] s : students) {
//                    studentModel.addRow(new Object[]{s[0], s[1], s[2]});
//                }
//                statusLabel.setText("Loaded " + students.size() + " student(s) for this class");
//            }
//        } catch (Exception e) {
//            statusLabel.setText("Error loading students: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void loadSubjects() {
//        comboSubject.removeAllItems();
//        
//        if (comboClass.getSelectedItem() == null || 
//            comboClass.getSelectedItem().toString().contains("-- No classes")) {
//            comboSubject.addItem("-- Select class first --");
//            return;
//        }
//        
//        try {
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
//            
//            if (subjects.isEmpty()) {
//                comboSubject.addItem("-- No subjects assigned --");
//                statusLabel.setText("⚠️ No subjects found for this class. Add subjects in Subject Management first.");
//            } else {
//                for (Subject s : subjects) {
//                    comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
//                }
//                comboSubject.setSelectedIndex(0);
//                if (selectedStudentId != -1) {
//                    loadExams();
//                }
//            }
//        } catch (Exception e) {
//            comboSubject.addItem("-- Error loading subjects --");
//            statusLabel.setText("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void loadExams() {
//        comboExam.removeAllItems();
//        
//        if (comboSubject.getSelectedItem() == null || 
//            comboSubject.getSelectedItem().toString().contains("--") ||
//            selectedStudentId == -1) {
//            comboExam.addItem("-- Select subject & student first --");
//            return;
//        }
//        
//        try {
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//            
//            // ✅ Uses faculty-isolated exam filtering
//            List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, classId, subjectId);
//            
//            if (exams.isEmpty()) {
//                comboExam.addItem("-- No exams created --");
//                statusLabel.setText("⚠️ No exams found for this subject. Create exams in Exam Management first.");
//            } else {
//                for (Exam ex : exams) {
//                    comboExam.addItem(ex.getExamId() + " - " + ex.getExamName());
//                }
//                comboExam.setSelectedIndex(0);
//            }
//        } catch (Exception e) {
//            comboExam.addItem("-- Error loading exams --");
//            statusLabel.setText("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    // ✅ CRITICAL FIX: Added missing method
//    private void handleStudentSelection() {
//        int selectedRow = studentTable.getSelectedRow();
//        if (selectedRow == -1) {
//            selectedStudentId = -1;
//            statusLabel.setText("Select a student to enter marks");
//            return;
//        }
//        
//        selectedStudentId = Integer.parseInt(studentModel.getValueAt(selectedRow, 0).toString());
//        String studentName = studentModel.getValueAt(selectedRow, 2).toString();
//        statusLabel.setText("Selected student: " + studentName + " (ID: " + selectedStudentId + ")");
//        
//        // Reload subjects/exams for this student
//        loadSubjects();
//        if (comboSubject.getItemCount() > 0 && !comboSubject.getSelectedItem().toString().contains("--")) {
//            loadExams();
//        }
//    }
//
//    private void saveMarks() {
//        // Validation
//        if (selectedStudentId == -1) {
//            showError("Please select a student first");
//            return;
//        }
//        
//        if (comboSubject.getItemCount() == 0 || 
//            comboSubject.getSelectedItem().toString().contains("--")) {
//            showError("No valid subjects available. Add subjects in Subject Management first.");
//            return;
//        }
//        
//        if (comboExam.getItemCount() == 0 || 
//            comboExam.getSelectedItem().toString().contains("--")) {
//            showError("No valid exams available. Create exams in Exam Management first.");
//            return;
//        }
//        
//        String marksText = txtMarks.getText().trim();
//        if (marksText.isEmpty()) {
//            showError("Please enter marks obtained");
//            txtMarks.requestFocus();
//            return;
//        }
//        
//        int marks;
//        try {
//            marks = Integer.parseInt(marksText);
//            if (marks < 0 || marks > 100) {
//                showError("Marks must be between 0 and 100");
//                return;
//            }
//        } catch (NumberFormatException e) {
//            showError("Invalid marks value. Enter a number between 0-100");
//            return;
//        }
//        
//        try {
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//            int examId = Integer.parseInt(comboExam.getSelectedItem().toString().split(" - ")[0].trim());
//            
//            // Save marks
//            boolean saved = marksDAO.addMarks(selectedStudentId, subjectId, examId, marks);
//            
//            if (saved) {
//                showSuccess("✅ Marks saved successfully for student ID: " + selectedStudentId);
//                txtMarks.setText("");
//                loadMarksForClass(classId);
//                // Reload exams to refresh available options
//                loadExams();
//                statusLabel.setText("✓ Marks saved: " + marks + " for student ID " + selectedStudentId);
//            } else {
//                showError("Failed to save marks. Record might already exist for this exam.");
//            }
//        } catch (Exception e) {
//            showError("Error saving marks: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void loadMarksForClass(int classId) {
//        marksModel.setRowCount(0);
//        List<String[]> marksData = marksDAO.getMarksForTableByClass(classId);
//        
//        for (String[] row : marksData) {
//            marksModel.addRow(new Object[]{
//                row[6], row[7], row[8],  // IDs (hidden)
//                row[0], row[1], row[2], row[3], row[4], row[5]  // Visible columns
//            });
//        }
//        
//        if (marksData.isEmpty()) {
//            statusLabel.setText("No marks records found for this class. Enter marks using the form above.");
//        } else {
//            statusLabel.setText("Loaded " + marksData.size() + " marks record(s) for this class");
//        }
//    }
//
//    private void showSuccess(String message) {
//        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
//    }
//
//    private void showError(String message) {
//        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                new MarksManagementUI(1, null).setVisible(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(null,
//                    "Database connection failed!\nCheck your DB configuration.",
//                    "Startup Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//    }
//}

package com.college.sms.ui;

import com.college.sms.dao.ClassDAO;
import com.college.sms.dao.ExamDAO;
import com.college.sms.dao.MarksDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.dao.SubjectDAO;
import com.college.sms.model.Exam;
import com.college.sms.model.Subject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class MarksManagementUI extends JFrame {

    private JComboBox<String> comboClass, comboSubject, comboExam;
    private JTextField txtMarks;
    private JTable studentTable, marksTable;
    private DefaultTableModel studentModel, marksModel;
    private ClassDAO classDAO;
    private StudentDAO studentDAO;
    private SubjectDAO subjectDAO;
    private ExamDAO examDAO;
    private MarksDAO marksDAO;
    private int facultyId;
    private JFrame previousUI;
    private int selectedStudentId = -1;
    private JLabel statusLabel;

    // ✅ BACKWARD-COMPATIBLE CONSTRUCTOR (fixes FacultyDashboard call)
    public MarksManagementUI(JFrame previousUI) throws SQLException {
        if (previousUI instanceof FacultyDashboard) {
            this.facultyId = ((FacultyDashboard) previousUI).getFacultyId();
        } else {
            this.facultyId = 1; // Fallback for testing
        }
        this.previousUI = previousUI;
        this.classDAO = new ClassDAO();
        this.studentDAO = new StudentDAO();
        this.subjectDAO = new SubjectDAO();
        this.examDAO = new ExamDAO();
        this.marksDAO = new MarksDAO();
        initComponents();
        loadData();
    }

    // ✅ PROPER CONSTRUCTOR (recommended)
    public MarksManagementUI(int facultyId, JFrame previousUI) throws SQLException {
        this.facultyId = facultyId;
        this.previousUI = previousUI;
        this.classDAO = new ClassDAO();
        this.studentDAO = new StudentDAO();
        this.subjectDAO = new SubjectDAO();
        this.examDAO = new ExamDAO();
        this.marksDAO = new MarksDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("📝 Marks Management | Faculty ID: " + facultyId);
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(248, 249, 252));

        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(0, 70));
        
        JLabel lblTitle = new JLabel("Manage Student Marks");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        JButton btnBack = new JButton("🔙 Back to Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setBackground(new Color(243, 156, 18));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnBack.addActionListener(e -> {
            dispose();
            if (previousUI != null) previousUI.setVisible(true);
        });
        headerPanel.add(btnBack, BorderLayout.EAST);

        // ===== CLASS SELECTION PANEL =====
        JPanel classPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        classPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            "Step 1: Select Your Class",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 15),
            new Color(41, 128, 185)
        ));
        classPanel.setBackground(Color.WHITE);
        
        classPanel.add(new JLabel("🏫 Class:"));
        comboClass = new JComboBox<>();
        comboClass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboClass.setPreferredSize(new Dimension(250, 32));
        classPanel.add(comboClass);

        // ✅ CRITICAL FIX: Combine header and class panels vertically
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(classPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ===== SPLIT PANE: Students + Marks Entry =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(420);
        splitPane.setResizeWeight(0.4);

        // ===== STUDENTS TABLE =====
        studentModel = new DefaultTableModel(new String[]{"ID", "Roll No", "Name"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        studentTable = new JTable(studentModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(28);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(new Color(52, 152, 219));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        
        // Zebra striping
        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus,
                                                          int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(248, 249, 252) : Color.WHITE);
                }
                return c;
            }
        });
        
        // Student selection handler
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleStudentSelection();
            }
        });
        
        JScrollPane studentScroll = new JScrollPane(studentTable);
        studentScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219)),
            "Step 2: Select Student",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        splitPane.setLeftComponent(studentScroll);

        // ===== MARKS ENTRY PANEL =====
        JPanel marksPanel = new JPanel(new BorderLayout(15, 15));
        marksPanel.setBackground(Color.WHITE);
        marksPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel (6 rows to accommodate new Delete button)
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 12));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
            "Step 3: Enter/Manage Marks",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 15),
            new Color(155, 89, 182)
        ));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(createLabel("📚 Subject:"));
        comboSubject = new JComboBox<>();
        comboSubject.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboSubject.setPreferredSize(new Dimension(200, 32));
        formPanel.add(comboSubject);

        formPanel.add(createLabel("📝 Exam:"));
        comboExam = new JComboBox<>();
        comboExam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboExam.setPreferredSize(new Dimension(200, 32));
        formPanel.add(comboExam);

        formPanel.add(createLabel("💯 Marks Obtained:"));
        txtMarks = new JTextField();
        txtMarks.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMarks.setPreferredSize(new Dimension(200, 32));
        formPanel.add(txtMarks);

        formPanel.add(new JLabel()); // spacer
        
        // ✅ NEW BUTTON: View Result Summary
        JButton btnResultSummary = new JButton("📊 View Result Summary");
        btnResultSummary.setBackground(new Color(155, 89, 182));
        btnResultSummary.setForeground(Color.WHITE);
        btnResultSummary.setFocusPainted(false);
        btnResultSummary.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnResultSummary.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnResultSummary.addActionListener(e -> {
            new ResultSummaryUI(facultyId, MarksManagementUI.this).setVisible(true);
            setVisible(false);
        });
        formPanel.add(btnResultSummary);

        // ✅ NEW BUTTON: Delete Marks (ADDED HERE - Minimal UI change)
        JButton btnDelete = new JButton("🗑️ Delete Marks");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnDelete.addActionListener(e -> deleteMarks()); // Linked to new method below
        formPanel.add(btnDelete);

        JButton btnAdd = createButton("✅ Save Marks", new Color(46, 204, 113));
        formPanel.add(btnAdd);

        marksPanel.add(formPanel, BorderLayout.NORTH);

        // Marks table
        marksModel = new DefaultTableModel(
            new String[]{"Stu ID", "Sub ID", "Exam ID", "Roll No", "Student", "Subject", "Exam", "Marks", "Result"}, 0
        ) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        marksTable = new JTable(marksModel);
        marksTable.setRowHeight(28);
        marksTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        marksTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        marksTable.getTableHeader().setBackground(new Color(142, 68, 173));
        marksTable.getTableHeader().setForeground(Color.WHITE);
        
        // Hide ID columns
        for (int i = 0; i < 3; i++) {
            marksTable.getColumnModel().getColumn(i).setMinWidth(0);
            marksTable.getColumnModel().getColumn(i).setMaxWidth(0);
        }
        
        // Color-coded results
        marksTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus,
                                                          int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String result = value.toString();
                if ("Pass".equalsIgnoreCase(result)) {
                    c.setBackground(new Color(200, 230, 201));
                    c.setForeground(new Color(27, 94, 32));
                } else {
                    c.setBackground(new Color(255, 205, 210));
                    c.setForeground(new Color(183, 28, 28));
                }
                return c;
            }
        });
        
        // ✅ CRITICAL: Add row selection listener for delete operation
        marksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        marksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = marksTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Auto-fill form fields when a record is selected (user-friendly enhancement)
                    txtMarks.setText(marksModel.getValueAt(selectedRow, 7).toString());
                    
                    // Auto-select subject/exam if possible
                    try {
                        String subjectText = marksModel.getValueAt(selectedRow, 5).toString();
                        String examText = marksModel.getValueAt(selectedRow, 6).toString();
                        
                        // Find matching subject in combo
                        for (int i = 0; i < comboSubject.getItemCount(); i++) {
                            if (comboSubject.getItemAt(i).toString().contains(subjectText)) {
                                comboSubject.setSelectedIndex(i);
                                break;
                            }
                        }
                        
                        // Find matching exam in combo
                        for (int i = 0; i < comboExam.getItemCount(); i++) {
                            if (comboExam.getItemAt(i).toString().contains(examText)) {
                                comboExam.setSelectedIndex(i);
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        // Ignore if auto-fill fails - not critical
                    }
                }
            }
        });
        
        JScrollPane marksScroll = new JScrollPane(marksTable);
        marksScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(142, 68, 173)),
            "Marks Records for Selected Class",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(142, 68, 173)
        ));
        marksPanel.add(marksScroll, BorderLayout.CENTER);

        splitPane.setRightComponent(marksPanel);
        add(splitPane, BorderLayout.CENTER);

        // ===== STATUS BAR =====
        statusLabel = new JLabel("Select a class to begin managing marks");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setBackground(new Color(236, 240, 241));
        statusLabel.setOpaque(true);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // ===== ACTION LISTENERS =====
        comboClass.addActionListener(e -> {
            if (comboClass.getSelectedItem() != null && !comboClass.getSelectedItem().toString().contains("--")) {
                loadStudents();
                loadSubjects();
                // ✅ CRITICAL FIX #1: Load marks immediately after class selection
                try {
                    int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
                    loadMarksForClass(classId);
                } catch (Exception ex) {
                    statusLabel.setText("Error loading marks: " + ex.getMessage());
                }
            }
        });
        
        comboSubject.addActionListener(e -> {
            if (comboSubject.getSelectedItem() != null && selectedStudentId != -1) {
                loadExams();
            }
        });
        
        btnAdd.addActionListener(e -> saveMarks());
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(51, 51, 51));
        return label;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }

    private void loadData() {
        loadClasses();
    }

    private void loadClasses() {
        comboClass.removeAllItems();
        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
        
        if (classes.isEmpty()) {
            comboClass.addItem("-- No classes assigned to you --");
            statusLabel.setText("⚠️ No classes assigned. Contact admin to assign classes.");
            JOptionPane.showMessageDialog(this,
                "No classes found for your account!\nContact admin to assign classes to your faculty account.",
                "No Classes Available", JOptionPane.WARNING_MESSAGE);
        } else {
            for (String[] c : classes) {
                comboClass.addItem(c[0] + " - " + c[1]);
            }
            comboClass.setSelectedIndex(0);
            loadStudents();
            loadSubjects();
            // ✅ CRITICAL FIX #2: Load marks AFTER class selection (initial load)
            try {
                int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
                loadMarksForClass(classId);
            } catch (Exception ex) {
                statusLabel.setText("Error loading initial marks: " + ex.getMessage());
            }
        }
    }

    private void loadStudents() {
        studentModel.setRowCount(0);
        selectedStudentId = -1;
        
        if (comboClass.getSelectedItem() == null || 
            comboClass.getSelectedItem().toString().contains("-- No classes")) {
            statusLabel.setText("⚠️ Select a valid class first");
            return;
        }
        
        try {
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            List<String[]> students = studentDAO.getStudentsByClass(classId);
            
            if (students.isEmpty()) {
                statusLabel.setText("No students found in this class. Add students first in Student Management.");
            } else {
                for (String[] s : students) {
                    studentModel.addRow(new Object[]{s[0], s[1], s[2]});
                }
                statusLabel.setText("Loaded " + students.size() + " student(s) for this class");
            }
            
            // ✅ CRITICAL FIX #3: Reload marks when students change
            loadMarksForClass(classId);
            
        } catch (Exception e) {
            statusLabel.setText("Error loading students: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSubjects() {
        comboSubject.removeAllItems();
        
        if (comboClass.getSelectedItem() == null || 
            comboClass.getSelectedItem().toString().contains("-- No classes")) {
            comboSubject.addItem("-- Select class first --");
            return;
        }
        
        try {
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
            
            if (subjects.isEmpty()) {
                comboSubject.addItem("-- No subjects assigned --");
                statusLabel.setText("⚠️ No subjects found for this class. Add subjects in Subject Management first.");
            } else {
                for (Subject s : subjects) {
                    comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
                }
                comboSubject.setSelectedIndex(0);
                if (selectedStudentId != -1) {
                    loadExams();
                }
            }
        } catch (Exception e) {
            comboSubject.addItem("-- Error loading subjects --");
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadExams() {
        comboExam.removeAllItems();
        
        if (comboSubject.getSelectedItem() == null || 
            comboSubject.getSelectedItem().toString().contains("--") ||
            selectedStudentId == -1) {
            comboExam.addItem("-- Select subject & student first --");
            return;
        }
        
        try {
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
            
            // ✅ Uses faculty-isolated exam filtering
            List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, classId, subjectId);
            
            if (exams.isEmpty()) {
                comboExam.addItem("-- No exams created --");
                statusLabel.setText("⚠️ No exams found for this subject. Create exams in Exam Management first.");
            } else {
                for (Exam ex : exams) {
                    comboExam.addItem(ex.getExamId() + " - " + ex.getExamName());
                }
                comboExam.setSelectedIndex(0);
            }
        } catch (Exception e) {
            comboExam.addItem("-- Error loading exams --");
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ CRITICAL FIX: Added missing method
    private void handleStudentSelection() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            selectedStudentId = -1;
            statusLabel.setText("Select a student to enter marks");
            return;
        }
        
        selectedStudentId = Integer.parseInt(studentModel.getValueAt(selectedRow, 0).toString());
        String studentName = studentModel.getValueAt(selectedRow, 2).toString();
        statusLabel.setText("Selected student: " + studentName + " (ID: " + selectedStudentId + ")");
        
        // Reload subjects/exams for this student
        loadSubjects();
        if (comboSubject.getItemCount() > 0 && !comboSubject.getSelectedItem().toString().contains("--")) {
            loadExams();
        }
    }

    // ✅ NEW METHOD: Delete marks operation (Minimal, safe implementation)
    private void deleteMarks() {
        // Validation: Must have a selected row in marks table
        int selectedRow = marksTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a marks record to delete from the table below");
            marksTable.requestFocus();
            return;
        }
        
        // Extract critical IDs from hidden columns (0=studentId, 2=examId)
        int studentId;
        int examId;
        String studentName;
        String examName;
        
        try {
            studentId = Integer.parseInt(marksModel.getValueAt(selectedRow, 0).toString());
            examId = Integer.parseInt(marksModel.getValueAt(selectedRow, 2).toString());
            studentName = marksModel.getValueAt(selectedRow, 4).toString();
            examName = marksModel.getValueAt(selectedRow, 6).toString();
        } catch (Exception e) {
            showError("Error reading selected record. Please try again.");
            return;
        }
        
        // Confirmation dialog (user safety)
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>⚠️ Confirm Deletion</b><br><br>" +
            "Are you sure you want to DELETE marks for:<br>" +
            "<b style='color:#2c3e50;'>Student:</b> " + studentName + "<br>" +
            "<b style='color:#2c3e50;'>Exam:</b> " + examName + "<br><br>" +
            "<span style='color:#e74c3c;'>This action cannot be undone!</span></html>",
            "Delete Marks Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return; // User cancelled
        }
        
        // Perform deletion
        try {
            boolean deleted = marksDAO.deleteMarks(studentId, examId);
            
            if (deleted) {
                // Success handling
                showSuccess("✅ Marks successfully deleted for:\n" + studentName + " (" + examName + ")");
                
                // Refresh marks table
                if (comboClass.getSelectedItem() != null && !comboClass.getSelectedItem().toString().contains("--")) {
                    int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
                    loadMarksForClass(classId);
                }
                
                // Clear form fields
                txtMarks.setText("");
                statusLabel.setText("✓ Deleted marks for " + studentName + " in " + examName);
                
                // Keep student selection intact for next operation
                if (selectedStudentId != -1) {
                    for (int i = 0; i < studentModel.getRowCount(); i++) {
                        if (Integer.parseInt(studentModel.getValueAt(i, 0).toString()) == selectedStudentId) {
                            studentTable.setRowSelectionInterval(i, i);
                            break;
                        }
                    }
                }
                
            } else {
                // Failure handling (record not found)
                showError("Deletion failed. Record might have been already deleted.\n" +
                         "Refreshing marks table...");
                if (comboClass.getSelectedItem() != null && !comboClass.getSelectedItem().toString().contains("--")) {
                    int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
                    loadMarksForClass(classId);
                }
            }
        } catch (Exception e) {
            // Error handling
            showError("Error deleting marks:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveMarks() {
        // Validation
        if (selectedStudentId == -1) {
            showError("Please select a student first");
            return;
        }
        
        if (comboSubject.getItemCount() == 0 || 
            comboSubject.getSelectedItem().toString().contains("--")) {
            showError("No valid subjects available. Add subjects in Subject Management first.");
            return;
        }
        
        if (comboExam.getItemCount() == 0 || 
            comboExam.getSelectedItem().toString().contains("--")) {
            showError("No valid exams available. Create exams in Exam Management first.");
            return;
        }
        
        String marksText = txtMarks.getText().trim();
        if (marksText.isEmpty()) {
            showError("Please enter marks obtained");
            txtMarks.requestFocus();
            return;
        }
        
        int marks;
        try {
            marks = Integer.parseInt(marksText);
            if (marks < 0 || marks > 100) {
                showError("Marks must be between 0 and 100");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Invalid marks value. Enter a number between 0-100");
            return;
        }
        
        try {
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
            int examId = Integer.parseInt(comboExam.getSelectedItem().toString().split(" - ")[0].trim());
            
            // Save marks
            boolean saved = marksDAO.addMarks(selectedStudentId, subjectId, examId, marks);
            
            if (saved) {
                showSuccess("✅ Marks saved successfully for student ID: " + selectedStudentId);
                txtMarks.setText("");
                loadMarksForClass(classId);
                // Reload exams to refresh available options
                loadExams();
                statusLabel.setText("✓ Marks saved: " + marks + " for student ID " + selectedStudentId);
            } else {
                showError("Failed to save marks. Record might already exist for this exam.");
            }
        } catch (Exception e) {
            showError("Error saving marks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMarksForClass(int classId) {
        marksModel.setRowCount(0);
        List<String[]> marksData = marksDAO.getMarksForTableByClass(classId);
        
        for (String[] row : marksData) {
            marksModel.addRow(new Object[]{
                row[6], row[7], row[8],  // IDs (hidden)
                row[0], row[1], row[2], row[3], row[4], row[5]  // Visible columns
            });
        }
        
        if (marksData.isEmpty()) {
            statusLabel.setText("No marks records found for this class. Enter marks using the form above.");
        } else {
            statusLabel.setText("Loaded " + marksData.size() + " marks record(s) for this class");
        }
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new MarksManagementUI(1, null).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Database connection failed!\nCheck your DB configuration.",
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}