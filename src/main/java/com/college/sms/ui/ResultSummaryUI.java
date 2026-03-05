//package com.college.sms.ui;

//
//import com.college.sms.dao.ClassDAO;
//import com.college.sms.dao.ExamDAO;
//import com.college.sms.dao.MarksDAO;
//import com.college.sms.model.StudentResult;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.List;
//
//public class ResultSummaryUI extends JFrame {
//
//    private JComboBox<String> comboClass, comboExam;
//    private JTable resultTable;
//    private DefaultTableModel tableModel;
//
//    private ClassDAO classDAO;
//    private ExamDAO examDAO;
//    private MarksDAO marksDAO;
//
//    public ResultSummaryUI() {
//        classDAO = new ClassDAO();
//        examDAO = new ExamDAO(); // Correct DAO for exams
//        marksDAO = new MarksDAO();
//
//        setTitle("Result Summary - Passed / Failed Students");
//        setSize(900, 600);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(new BorderLayout(10, 10));
//
//        // ===== Top Panel: Filters + Back Button =====
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
//        topPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
//
//        // Back button
//        JButton btnBack = new JButton("Back");
//        btnBack.addActionListener(e -> {
//            // Close this frame to go back to previous screen
//            dispose();
//        });
//        topPanel.add(btnBack);
//
//        comboClass = new JComboBox<>();
//        comboExam = new JComboBox<>();
//        topPanel.add(new JLabel("Class:"));
//        topPanel.add(comboClass);
//        topPanel.add(new JLabel("Exam:"));
//        topPanel.add(comboExam);
//
//        JButton btnLoad = new JButton("Load Results");
//        topPanel.add(btnLoad);
//
//        add(topPanel, BorderLayout.NORTH);
//
//        // ===== Table =====
//        tableModel = new DefaultTableModel(
//                new String[]{"Roll No", "Student Name", "Average Marks", "Result"}, 0
//        ) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//
//        resultTable = new JTable(tableModel);
//
//        // Custom cell renderer for coloring rows
//        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value,
//                                                           boolean isSelected, boolean hasFocus,
//                                                           int row, int column) {
//                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                String result = table.getValueAt(row, 3).toString();
//                if ("Passed".equalsIgnoreCase(result)) {
//                    c.setBackground(new Color(144, 238, 144)); // light green
//                } else {
//                    c.setBackground(new Color(255, 182, 193)); // light red
//                }
//
//                if (isSelected) {
//                    c.setBackground(new Color(135, 206, 250)); // light blue selection
//                }
//
//                return c;
//            }
//        });
//
//        JScrollPane scroll = new JScrollPane(resultTable);
//        scroll.setBorder(BorderFactory.createTitledBorder("Student Results"));
//        add(scroll, BorderLayout.CENTER);
//
//        // ===== Load initial data =====
//        loadClasses();
//        loadExams();
//
//        // ===== Button Actions =====
//        btnLoad.addActionListener(e -> loadResults());
//
//        setVisible(true);
//    }
//
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        List<String[]> classes = classDAO.getAllClasses();
//        for (String[] c : classes) {
//            comboClass.addItem(c[0] + " - " + c[1]);
//        }
//        if (comboClass.getItemCount() > 0) {
//            comboClass.setSelectedIndex(0);
//        }
//    }
//
//    private void loadExams() {
//        comboExam.removeAllItems();
//        List<String[]> exams = examDAO.getAllExams(); // Use ExamDAO here
//        for (String[] e : exams) {
//            comboExam.addItem(e[0] + " - " + e[1]);
//        }
//        if (comboExam.getItemCount() > 0) comboExam.setSelectedIndex(0);
//    }
//
//    private void loadResults() {
//        tableModel.setRowCount(0);
//        if (comboClass.getSelectedItem() == null || comboExam.getSelectedItem() == null) return;
//
//        int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0]);
//        int examId = Integer.parseInt(comboExam.getSelectedItem().toString().split(" - ")[0]);
//
//        // Fetch results
//        List<StudentResult> results = marksDAO.getResultsForClassExam(classId, examId);
//
//        for (StudentResult sr : results) {
//            tableModel.addRow(new Object[]{
//                    sr.getRollNo(),
//                    sr.getStudentName(),
//                    String.format("%.2f", sr.getAverageMarks()),
//                    sr.isPassed() ? "Passed" : "Failed"
//            });
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(ResultSummaryUI::new);
//    }
//}

package com.college.sms.ui;

import com.college.sms.dao.ClassDAO;
import com.college.sms.dao.ExamDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.dao.SubjectDAO;
import com.college.sms.model.Exam;
import com.college.sms.model.Subject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

public class ResultSummaryUI extends JFrame {

    private JComboBox<String> comboClass, comboSubject, comboExam;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;
    private ExamDAO examDAO;
    private StudentDAO studentDAO;
    private int facultyId;
    private JFrame previousUI;
    private JLabel statusLabel;
    private boolean isLoadingSubjects = false;
    private boolean isLoadingExams = false;

    // Statistics card label references (FIXED: Robust updating)
    private JLabel totalStudentsLabel, passCountLabel, failCountLabel, passPercentLabel;

    public ResultSummaryUI(int facultyId, JFrame previousUI) {
        this.facultyId = facultyId;
        this.previousUI = previousUI;
        this.classDAO = new ClassDAO();
        this.subjectDAO = new SubjectDAO();
        this.examDAO = new ExamDAO();
        this.studentDAO = new StudentDAO();
        initComponents();
        loadData();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("📊 Result Summary | Faculty ID: " + facultyId);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 247, 250));

        // ===== TOP SECTION WRAPPER (CRITICAL FIX: Prevents component replacement) =====
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        // ===== HEADER PANEL (WITH VISIBLE BACK BUTTON) =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(0, 75));
        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = new JLabel("🎓 Student Performance Summary");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // ✅ FIXED: PROMINENT BACK BUTTON (Visible + Functional)
        JButton btnBack = createModernButton("⇦ Back to Marks Management", new Color(41, 128, 185), Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBack.setPreferredSize(new Dimension(240, 42)); // Wider for better visibility
        btnBack.addActionListener(e -> {
            dispose();
            if (previousUI != null) {
                previousUI.setVisible(true);
                previousUI.toFront();
                previousUI.requestFocus();
            } else {
                // Fallback: Open Marks Management UI directly
                try {
                    new MarksManagementUI(facultyId, null).setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Marks Management not found!\nReturning to Faculty Dashboard...",
                        "Navigation Error", JOptionPane.WARNING_MESSAGE);
                    try {
                        new FacultyDashboard(facultyId).setVisible(true);
                    } catch (Exception e2) {
                        new FacultyLoginUI().setVisible(true);
                    }
                }
            }
        });
        headerPanel.add(btnBack, BorderLayout.EAST);

        topWrapper.add(headerPanel);

        // ===== FILTER PANEL (WITH WORKING SUBJECT DROPDOWN) =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "🔍 Filter Results",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(52, 152, 219)
        ));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setPreferredSize(new Dimension(0, 90));

        filterPanel.add(createFilterLabel("🏫 Class:"));
        comboClass = createModernComboBox();
        filterPanel.add(comboClass);

        filterPanel.add(createFilterLabel("📚 Subject:"));  // ✅ SUBJECT DROPDOWN VISIBLE
        comboSubject = createModernComboBox();               // ✅ SUBJECT DROPDOWN VISIBLE
        filterPanel.add(comboSubject);

        filterPanel.add(createFilterLabel("📝 Exam:"));
        comboExam = createModernComboBox();
        filterPanel.add(comboExam);

        JButton btnLoad = createModernButton("📊 Load Results", new Color(46, 204, 113), Color.WHITE);
        btnLoad.setPreferredSize(new Dimension(160, 38));
        btnLoad.addActionListener(e -> loadResults());
        filterPanel.add(btnLoad);

        topWrapper.add(filterPanel);
        add(topWrapper, BorderLayout.NORTH); // ✅ CRITICAL: Single component in NORTH

        // ===== RESULTS TABLE (WITH CORRECT COLOR CODING) =====
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        tableModel = new DefaultTableModel(
            new String[]{"#", "Roll No", "Student Name", "Marks", "Percentage", "Result", "Grade"}, 0
        ) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        resultTable = new JTable(tableModel);
        resultTable.setRowHeight(34); // Increased for better readability
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.setGridColor(new Color(230, 230, 230));
        resultTable.setSelectionBackground(new Color(52, 152, 219));
        resultTable.setSelectionForeground(Color.WHITE);
        resultTable.setIntercellSpacing(new Dimension(0, 1));

        // Header styling
        JTableHeader header = resultTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        // ✅ FIXED: PROPER COLOR CODING (No mixed colors)
        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    // ✅ ZEBRA STRIPING (Clean alternating rows)
                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
                    
                    // ✅ RESULT COLUMN (Index 5) - Entire cell colored
                    if (column == 5) {
                        String result = (String) t.getValueAt(row, 5);
                        if ("Pass".equals(result)) {
                            c.setBackground(new Color(200, 230, 201)); // Light green
                            c.setForeground(new Color(27, 94, 32));     // Dark green text
                        } else {
                            c.setBackground(new Color(255, 205, 210)); // Light red
                            c.setForeground(new Color(183, 28, 28));    // Dark red text
                        }
                    }
                    
                    // ✅ GRADE COLUMN (Index 6) - Grade-specific colors
                    if (column == 6) {
                        String grade = (String) t.getValueAt(row, 6);
                        switch (grade) {
                            case "A": 
                                c.setBackground(new Color(100, 221, 23));  // Bright green
                                c.setForeground(Color.WHITE);
                                c.setFont(c.getFont().deriveFont(Font.BOLD));
                                break;
                            case "B": 
                                c.setBackground(new Color(129, 199, 132)); // Light green
                                c.setForeground(new Color(27, 94, 32));
                                break;
                            case "C": 
                                c.setBackground(new Color(255, 249, 196)); // Light yellow
                                c.setForeground(new Color(137, 104, 0));
                                break;
                            case "D": 
                                c.setBackground(new Color(255, 224, 178)); // Light orange
                                c.setForeground(new Color(165, 82, 0));
                                break;
                            case "F": 
                                c.setBackground(new Color(255, 205, 210)); // Light red
                                c.setForeground(new Color(183, 28, 28));
                                break;
                            default:
                                c.setBackground(Color.WHITE);
                        }
                    }
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204), 1));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionPanel.setBackground(Color.WHITE);
        
        JButton btnExport = createModernButton("📤 Export CSV", new Color(155, 89, 182), Color.WHITE);
        btnExport.setPreferredSize(new Dimension(150, 36));
        btnExport.addActionListener(e -> exportResults());
        actionPanel.add(btnExport);
        
        tablePanel.add(actionPanel, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.CENTER);

        // ===== STATISTICS PANEL (WITH ROBUST LABEL REFERENCES) =====
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "📈 Performance Summary",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 17),
            new Color(52, 152, 219)
        ));
        statsPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // ✅ FIXED: Store label references for robust updates
        statsPanel.add(createStatCard("Total Students", "0", new Color(52, 152, 219), lbl -> totalStudentsLabel = lbl));
        statsPanel.add(createStatCard("Pass Count", "0", new Color(46, 204, 113), lbl -> passCountLabel = lbl));
        statsPanel.add(createStatCard("Fail Count", "0", new Color(231, 76, 60), lbl -> failCountLabel = lbl));
        statsPanel.add(createStatCard("Pass %", "0.0%", new Color(155, 89, 182), lbl -> passPercentLabel = lbl));

        add(statsPanel, BorderLayout.SOUTH);

        // ===== STATUS BAR =====
        statusLabel = new JLabel("Ready to load results. Select class, subject and exam to begin.");
        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setBackground(new Color(236, 240, 241));
        statusLabel.setForeground(new Color(75, 75, 75));
        statusLabel.setOpaque(true);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(statusLabel, BorderLayout.SOUTH);

        // ===== EVENT LISTENERS =====
        comboClass.addActionListener(e -> {
            if (comboClass.getSelectedItem() != null && !comboClass.getSelectedItem().toString().contains("--")) {
                loadSubjects();
            }
        });

        comboSubject.addActionListener(e -> {
            if (!isLoadingSubjects && comboSubject.getSelectedItem() != null 
                && !comboSubject.getSelectedItem().toString().contains("--")) {
                loadExams();
            }
        });
    }

    private JLabel createFilterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    private JComboBox<String> createModernComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(220, 36));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        return combo;
    }

    private JButton createModernButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    // ✅ FIXED: Store label reference for robust stats updates
    private JPanel createStatCard(String title, String value, Color color, java.util.function.Consumer<JLabel> labelRef) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(200, 100));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(120, 120, 120));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        
        labelRef.accept(valueLabel); // Store reference
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    private void loadData() {
        loadClasses();
    }

    private void loadClasses() {
        comboClass.removeAllItems();
        comboSubject.removeAllItems();
        comboExam.removeAllItems();

        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
        
        if (classes.isEmpty()) {
            comboClass.addItem("-- No classes assigned to you --");
            statusLabel.setText("⚠️ No classes assigned. Contact admin to assign classes.");
            JOptionPane.showMessageDialog(this,
                "No classes found for your account!\nContact admin to assign classes to your faculty account.",
                "No Classes Available", JOptionPane.WARNING_MESSAGE);
        } else {
            for (String[] c : classes) {
                comboClass.addItem(c[0] + " - " + c[1]); // Format: "ID - Name"
            }
            comboClass.setSelectedIndex(0);
            loadSubjects();
        }
    }

    private void loadSubjects() {
        isLoadingSubjects = true;
        comboSubject.removeAllItems();
        comboExam.removeAllItems();

        if (comboClass.getSelectedItem() == null || "-- No classes --".equals(comboClass.getSelectedItem().toString())) {
            comboSubject.addItem("-- Select class first --");
            isLoadingSubjects = false;
            return;
        }

        try {
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
            
            if (subjects.isEmpty()) {
                comboSubject.addItem("-- No subjects assigned to you --");
                statusLabel.setText("⚠️ No subjects found for this class. Add subjects in Subject Management first.");
            } else {
                for (Subject s : subjects) {
                    comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName()); // Format: "ID - Name"
                }
                comboSubject.setSelectedIndex(0);
            }
        } catch (Exception e) {
            comboSubject.addItem("-- Error loading subjects --");
            statusLabel.setText("Error loading subjects: " + e.getMessage());
            e.printStackTrace();
        }
        isLoadingSubjects = false;
        loadExams();
    }

    private void loadExams() {
        if (isLoadingSubjects) return;
        isLoadingExams = true;
        comboExam.removeAllItems();

        if (comboSubject.getSelectedItem() == null || "-- No subjects assigned to you --".equals(comboSubject.getSelectedItem().toString())) {
            comboExam.addItem("-- Select subject first --");
            isLoadingExams = false;
            return;
        }

        try {
            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, classId, subjectId);
            
            if (exams.isEmpty()) {
                comboExam.addItem("-- No exams created for this subject --");
                statusLabel.setText("⚠️ No exams found for this subject. Create exams in Exam Management first.");
            } else {
                for (Exam ex : exams) {
                    // Format: "ID:Exam Name (Max: X)"
                    comboExam.addItem(ex.getExamId() + ":" + ex.getExamName() + " (Max: " + ex.getMaxMarks() + ")");
                }
                comboExam.setSelectedIndex(0);
            }
        } catch (Exception e) {
            comboExam.addItem("-- Error loading exams --");
            statusLabel.setText("Error loading exams: " + e.getMessage());
            e.printStackTrace();
        }
        isLoadingExams = false;
    }

    private void loadResults() {
        tableModel.setRowCount(0);
        
        if (comboClass.getSelectedItem() == null || comboSubject.getSelectedItem() == null || comboExam.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select class, subject and exam!", "Missing Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (comboSubject.getSelectedItem().toString().contains("--") || comboExam.getSelectedItem().toString().contains("--")) {
            JOptionPane.showMessageDialog(this, "No valid subjects/exams available!", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // ✅ CORRECT PARSING FOR ALL 3 DROPDOWNS
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
            int examId = Integer.parseInt(comboExam.getSelectedItem().toString().split(":")[0].trim());
            String examName = comboExam.getSelectedItem().toString().split(":")[1].split("\\(")[0].trim();

            Exam exam = examDAO.getExamById(examId);
            if (exam == null) {
                JOptionPane.showMessageDialog(this, "Exam not found! Please verify exam configuration.", 
                    "Invalid Exam", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("❌ Exam ID " + examId + " not found in database");
                return;
            }
            
            int maxMarks = exam.getMaxMarks();
            int passMarks = exam.getPassMarks();

            List<String[]> students = studentDAO.getStudentsByClass(classId);
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No students found in this class!", "Empty Class", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("No students found in selected class");
                return;
            }

            int passCount = 0;
            int failCount = 0;
            double totalMarks = 0;
            DecimalFormat pctFormat = new DecimalFormat("0.00");

            for (int i = 0; i < students.size(); i++) {
                String[] s = students.get(i);
                int studentId = Integer.parseInt(s[0]);
                int marks = studentDAO.getMarksByExam(studentId, examId);
                
                double percentage = (maxMarks > 0) ? (marks * 100.0) / maxMarks : 0.0;
                String percentageStr = pctFormat.format(percentage) + "%";
                String result = (marks >= passMarks) ? "Pass" : "Fail";
                String grade = calculateGrade(marks, maxMarks);
                
                if (result.equals("Pass")) passCount++;
                else failCount++;
                
                totalMarks += marks;
                
                tableModel.addRow(new Object[]{
                    i + 1,          // #
                    s[1],           // Roll No
                    s[2],           // Name
                    marks,          // Marks
                    percentageStr,  // Percentage
                    result,         // Result
                    grade           // Grade
                });
            }

            int totalStudents = students.size();
            double passPercent = (totalStudents > 0) ? (passCount * 100.0 / totalStudents) : 0.0;
            double avgMarks = (totalStudents > 0) ? (totalMarks / totalStudents) : 0.0;

            // ✅ FIXED: Update stats using stored label references (no fragile indexing)
            totalStudentsLabel.setText(String.valueOf(totalStudents));
            passCountLabel.setText(String.valueOf(passCount));
            failCountLabel.setText(String.valueOf(failCount));
            passPercentLabel.setText(String.format("%.1f%%", passPercent));

            statusLabel.setText(String.format("✓ Loaded %d students | Pass: %.1f%% | Avg: %.1f/%d | Highest: %.1f%%", 
                totalStudents, passPercent, avgMarks, maxMarks, (passCount > 0 ? (totalMarks/passCount)/maxMarks*100 : 0)));
            
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading results: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading results:\n" + e.getMessage(), 
                "Load Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String calculateGrade(int marks, int maxMarks) {
        if (maxMarks <= 0) return "N/A";
        double percentage = (double) marks / maxMarks * 100;
        if (percentage >= 90) return "A";
        else if (percentage >= 80) return "B";
        else if (percentage >= 70) return "C";
        else if (percentage >= 60) return "D";
        else return "F";
    }

    private void exportResults() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export!", "Empty Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Results Summary As CSV");
        fileChooser.setSelectedFile(new java.io.File("results_summary.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter fw = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                // Write headers
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    fw.append(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) fw.append(",");
                }
                fw.append("\n");

                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        fw.append(tableModel.getValueAt(i, j).toString());
                        if (j < tableModel.getColumnCount() - 1) fw.append(",");
                    }
                    fw.append("\n");
                }
                JOptionPane.showMessageDialog(this, "Results exported successfully!\nFile: " + fileChooser.getSelectedFile().getAbsolutePath(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("✓ Results exported to: " + fileChooser.getSelectedFile().getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting results:\n" + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Export failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                // Test with mock Marks Management UI
                JFrame mockMarksUI = new JFrame("Marks Management");
                mockMarksUI.setSize(1000, 650);
                mockMarksUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mockMarksUI.setVisible(true);
                
                new ResultSummaryUI(1, mockMarksUI).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Application failed to start!\nError: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}