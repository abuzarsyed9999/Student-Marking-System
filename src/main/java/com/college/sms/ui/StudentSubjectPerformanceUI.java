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
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class StudentSubjectPerformanceUI extends JFrame {

    private JComboBox<String> comboClass, comboSubject;
    private JList<StudentItem> studentList;
    private DefaultListModel<StudentItem> studentListModel;
    private JTable examPerformanceTable;
    private DefaultTableModel examTableModel;
    private JLabel lblStudentInfo, lblOverallPercentage, lblOverallResult;
    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;
    private ExamDAO examDAO;
    private StudentDAO studentDAO;
    private int facultyId;
    private JFrame previousUI;
    private JLabel statusLabel;
    private boolean isLoadingSubjects = false;
    private List<Exam> currentExams = new ArrayList<>();
    private JTextField searchField;
    private int selectedStudentId = -1;
    private String selectedStudentName = "";
    private String selectedStudentRollNo = "";

    public StudentSubjectPerformanceUI(int facultyId, JFrame previousUI) {
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
        setTitle("🎓 Student Subject Performance | Faculty ID: " + facultyId);
        setSize(1250, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 247, 250));

        // ===== TOP SECTION WRAPPER =====
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(0, 75));
        headerPanel.setBorder(new EmptyBorder(0, 25, 0, 25));

        JLabel titleLabel = new JLabel("📊 Individual Student Performance by Subject");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton btnBack = createModernButton("⇦ Back to Results", new Color(30, 136, 56), Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBack.setPreferredSize(new Dimension(190, 42));
        btnBack.addActionListener(e -> {
            dispose();
            if (previousUI != null) {
                previousUI.setVisible(true);
                previousUI.toFront();
                previousUI.requestFocus();
            }
        });
        headerPanel.add(btnBack, BorderLayout.EAST);
        topWrapper.add(headerPanel);

        // ===== FILTER PANEL =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 18));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            "🔍 Select Class & Subject",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 17),
            new Color(41, 128, 185)
        ));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setPreferredSize(new Dimension(0, 100));

        filterPanel.add(createFilterLabel("🏫 Class:"));
        comboClass = createModernComboBox();
        filterPanel.add(comboClass);

        filterPanel.add(createFilterLabel("📚 Subject:"));
        comboSubject = createModernComboBox();
        filterPanel.add(comboSubject);

        JButton btnLoad = createModernButton("✨ Load Students", new Color(142, 68, 173), Color.WHITE);
        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLoad.setPreferredSize(new Dimension(180, 42));
        btnLoad.addActionListener(e -> loadStudents());
        filterPanel.add(btnLoad);
        topWrapper.add(filterPanel);

        add(topWrapper, BorderLayout.NORTH);

        // ===== MAIN CONTENT (SPLIT PANE) =====
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(350);
        mainSplitPane.setResizeWeight(0.3);

        // ===== LEFT PANEL: STUDENT LIST =====
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
            "🔎 Search Student",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        searchPanel.setPreferredSize(new Dimension(0, 60));

        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        searchField.setPreferredSize(new Dimension(250, 34));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterStudents(searchField.getText());
            }
        });
        searchPanel.add(searchField);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Student list
        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        studentList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setBackground(Color.WHITE);
        studentList.setCellRenderer(new StudentListCellRenderer());
        studentList.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        studentList.setSelectionBackground(new Color(52, 152, 219));
        studentList.setSelectionForeground(Color.WHITE);

        // Student selection handler
        studentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentList.getSelectedValue() != null) {
                StudentItem selectedItem = studentList.getSelectedValue();
                selectedStudentId = selectedItem.getStudentId();
                selectedStudentName = selectedItem.getName();
                selectedStudentRollNo = selectedItem.getRollNo();
                loadStudentPerformance();
            }
        });

        JScrollPane studentScroll = new JScrollPane(studentList);
        studentScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "👥 Students in Selected Class",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 15),
            new Color(52, 152, 219)
        ));
        leftPanel.add(studentScroll, BorderLayout.CENTER);

        // Status label for student count
        JLabel studentCountLabel = new JLabel("No students loaded");
        studentCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentCountLabel.setForeground(new Color(100, 100, 100));
        studentCountLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        leftPanel.add(studentCountLabel, BorderLayout.SOUTH);

        mainSplitPane.setLeftComponent(leftPanel);

        // ===== RIGHT PANEL: STUDENT PERFORMANCE =====
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Student info panel
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBackground(new Color(248, 250, 252));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            "🎓 Selected Student",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(41, 128, 185)
        ));
        infoPanel.setPreferredSize(new Dimension(0, 80));

        lblStudentInfo = new JLabel("Select a student from the left panel to view performance");
        lblStudentInfo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblStudentInfo.setForeground(new Color(44, 62, 80));
        lblStudentInfo.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(lblStudentInfo, BorderLayout.CENTER);
        rightPanel.add(infoPanel, BorderLayout.NORTH);

        // Exam performance table
        examTableModel = new DefaultTableModel(
            new String[]{"Exam Name", "Marks Obtained", "Max Marks", "Pass Marks", "Percentage", "Result"}, 0
        ) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        examPerformanceTable = new JTable(examTableModel);
        examPerformanceTable.setRowHeight(36);
        examPerformanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        examPerformanceTable.setGridColor(new Color(230, 230, 230));
        examPerformanceTable.setSelectionBackground(new Color(52, 152, 219));
        examPerformanceTable.setSelectionForeground(Color.WHITE);
        examPerformanceTable.setIntercellSpacing(new Dimension(0, 2));

        JTableHeader header = examPerformanceTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 42));

        // Color-coded rendering
        examPerformanceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    // Zebra striping
                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
                    
                    // Color-code Result column (index 5)
                    if (column == 5) {
                        String result = value.toString();
                        if ("Pass".equals(result)) {
                            c.setBackground(new Color(200, 230, 201, 60));
                            c.setForeground(new Color(27, 94, 32));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setBackground(new Color(255, 205, 210, 60));
                            c.setForeground(new Color(183, 28, 28));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    }
                    
                    // Color-code Percentage column (index 4)
                    if (column == 4) {
                        try {
                            String pctStr = value.toString().replace("%", "").trim();
                            double pct = Double.parseDouble(pctStr);
                            if (pct >= 90) c.setForeground(new Color(27, 94, 32));
                            else if (pct >= 75) c.setForeground(new Color(40, 116, 166));
                            else if (pct >= 60) c.setForeground(new Color(137, 104, 0));
                            else if (pct >= 40) c.setForeground(new Color(165, 82, 0));
                            else c.setForeground(new Color(183, 28, 28));
                        } catch (Exception ignored) {}
                    }
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(examPerformanceTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "📝 Exam-wise Performance",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(52, 152, 219)
        ));
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 15));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            "🎯 Overall Performance Summary",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(41, 128, 185)
        ));
        summaryPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel totalPanel = new JPanel(new BorderLayout(5, 5));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel totalLabel = new JLabel("Total Obtained");
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        totalLabel.setForeground(new Color(100, 100, 100));
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblOverallPercentage = new JLabel("0/0");
        lblOverallPercentage.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblOverallPercentage.setForeground(new Color(52, 152, 219));
        lblOverallPercentage.setHorizontalAlignment(SwingConstants.CENTER);
        totalPanel.add(totalLabel, BorderLayout.NORTH);
        totalPanel.add(lblOverallPercentage, BorderLayout.CENTER);

        JPanel pctPanel = new JPanel(new BorderLayout(5, 5));
        pctPanel.setBackground(Color.WHITE);
        pctPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel pctLabel = new JLabel("Overall Percentage");
        pctLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pctLabel.setForeground(new Color(100, 100, 100));
        pctLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel pctValue = new JLabel("0.0%");
        pctValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pctValue.setForeground(new Color(46, 204, 113));
        pctValue.setHorizontalAlignment(SwingConstants.CENTER);
        pctPanel.add(pctLabel, BorderLayout.NORTH);
        pctPanel.add(pctValue, BorderLayout.CENTER);

        JPanel resultPanel = new JPanel(new BorderLayout(5, 5));
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel resultLabel = new JLabel("Overall Result");
        resultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultLabel.setForeground(new Color(100, 100, 100));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblOverallResult = new JLabel("N/A");
        lblOverallResult.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblOverallResult.setForeground(new Color(231, 76, 60));
        lblOverallResult.setHorizontalAlignment(SwingConstants.CENTER);
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(lblOverallResult, BorderLayout.CENTER);

        summaryPanel.add(totalPanel);
        summaryPanel.add(pctPanel);
        summaryPanel.add(resultPanel);
        rightPanel.add(summaryPanel, BorderLayout.SOUTH);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        actionPanel.setBackground(Color.WHITE);
        
        JButton btnExport = createModernButton("📤 Export Student Report", new Color(155, 89, 182), Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExport.setPreferredSize(new Dimension(200, 40));
        btnExport.addActionListener(e -> exportStudentReport());
        actionPanel.add(btnExport);
        
        rightPanel.add(actionPanel, BorderLayout.SOUTH);

        mainSplitPane.setRightComponent(rightPanel);
        add(mainSplitPane, BorderLayout.CENTER);

        // ===== STATUS BAR =====
        statusLabel = new JLabel("Ready to analyze student performance. Select class and subject, then click 'Load Students'.");
        statusLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
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
                // Do NOT auto-load students - wait for explicit button click
            }
        });
    }

    private JLabel createFilterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(41, 128, 185));
        return label;
    }

    private JComboBox<String> createModernComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        combo.setPreferredSize(new Dimension(240, 42));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        return combo;
    }

    private JButton createModernButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }

    // Custom cell renderer for student list
    private class StudentListCellRenderer extends JLabel implements ListCellRenderer<StudentItem> {
        public StudentListCellRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 15));
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends StudentItem> list, StudentItem value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.getRollNo() + " - " + value.getName());
            if (isSelected) {
                setBackground(new Color(52, 152, 219));
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(new Color(44, 62, 80));
            }
            return this;
        }
    }

    // Student item for list model
    private class StudentItem {
        private int studentId;
        private String rollNo;
        private String name;
        
        public StudentItem(int studentId, String rollNo, String name) {
            this.studentId = studentId;
            this.rollNo = rollNo;
            this.name = name;
        }
        
        public int getStudentId() { return studentId; }
        public String getRollNo() { return rollNo; }
        public String getName() { return name; }
        public String getDisplayText() { return rollNo + " - " + name; }
    }

    private void loadData() {
        loadClasses();
    }

    private void loadClasses() {
        comboClass.removeAllItems();
        comboSubject.removeAllItems();
        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
        
        if (classes.isEmpty()) {
            comboClass.addItem("-- No classes assigned --");
            statusLabel.setText("⚠️ No classes assigned to you. Contact admin.");
        } else {
            for (String[] c : classes) {
                comboClass.addItem(c[0] + " - " + c[1]);
            }
            comboClass.setSelectedIndex(0);
            loadSubjects();
        }
    }

    private void loadSubjects() {
        isLoadingSubjects = true;
        comboSubject.removeAllItems();
        if (comboClass.getSelectedItem() == null || "-- No classes --".equals(comboClass.getSelectedItem().toString())) {
            comboSubject.addItem("-- Select class first --");
            isLoadingSubjects = false;
            return;
        }
        try {
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
            if (subjects.isEmpty()) {
                comboSubject.addItem("-- No subjects assigned --");
                statusLabel.setText("⚠️ No subjects found for this class");
            } else {
                for (Subject s : subjects) {
                    comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
                }
                comboSubject.setSelectedIndex(0);
            }
        } catch (Exception e) {
            comboSubject.addItem("-- Error loading subjects --");
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
        isLoadingSubjects = false;
    }

    private void loadStudents() {
        studentListModel.clear();
        examTableModel.setRowCount(0);
        lblStudentInfo.setText("Select a student from the left panel to view performance");
        lblOverallPercentage.setText("0/0");
        lblOverallResult.setText("N/A");
        
        if (comboClass.getSelectedItem() == null || comboSubject.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select class and subject!", "Missing Selection", JOptionPane.WARNING_MESSAGE);
            statusLabel.setText("⚠️ Select valid class and subject");
            return;
        }

        if (comboSubject.getSelectedItem().toString().contains("--")) {
            JOptionPane.showMessageDialog(this, "No valid subject selected!", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            statusLabel.setText("⚠️ Select valid subject");
            return;
        }

        try {
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
            String className = comboClass.getSelectedItem().toString().split(" - ")[1].trim();
            String subjectName = comboSubject.getSelectedItem().toString().split(" - ")[1].trim();

            // Get ALL exams for this subject+class
            currentExams = examDAO.getExamsByFacultyClassSubject(facultyId, classId, subjectId);
            if (currentExams.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No exams found for this subject!\nCreate exams in Exam Management first.",
                    "No Exams Available", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("⚠️ No exams found for " + subjectName);
                return;
            }
            
            // Get ALL students in this class
            List<String[]> students = studentDAO.getStudentsByClass(classId);
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No students found in this class!", "Empty Class", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("⚠️ No students found in " + className);
                return;
            }
            
            // Populate student list
            for (String[] student : students) {
                int studentId = Integer.parseInt(student[0]);
                String rollNo = student[1];
                String name = student[2];
                studentListModel.addElement(new StudentItem(studentId, rollNo, name));
            }
            
            statusLabel.setText(String.format("✓ Loaded %d students for %s (%s). Select a student to view performance.", 
                students.size(), subjectName, className));
            
            // Clear search field
            searchField.setText("");
            
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading students: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading students:\n" + e.getMessage(), 
                "Load Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterStudents(String searchText) {
        if (searchText.trim().isEmpty()) {
            // Show all students
            studentList.setModel(studentListModel);
            return;
        }
        
        DefaultListModel<StudentItem> filteredModel = new DefaultListModel<>();
        for (int i = 0; i < studentListModel.size(); i++) {
            StudentItem item = studentListModel.getElementAt(i);
            if (item.getRollNo().toLowerCase().contains(searchText.toLowerCase()) || 
                item.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredModel.addElement(item);
            }
        }
        studentList.setModel(filteredModel);
    }

    private void loadStudentPerformance() {
        examTableModel.setRowCount(0);
        
        if (selectedStudentId == -1 || currentExams.isEmpty()) {
            lblStudentInfo.setText("Select a student and load exams first");
            lblOverallPercentage.setText("0/0");
            lblOverallResult.setText("N/A");
            return;
        }
        
        try {
            // Update student info label
            lblStudentInfo.setText(selectedStudentRollNo + " - " + selectedStudentName);
            
            int totalObtained = 0;
            int totalMax = 0;
            DecimalFormat pctFormat = new DecimalFormat("0.0");
            
            // Populate exam performance table
            for (Exam exam : currentExams) {
                int marks = studentDAO.getMarksByExam(selectedStudentId, exam.getExamId());
                int maxMarks = exam.getMaxMarks();
                int passMarks = exam.getPassMarks();
                
                // Calculate percentage for this exam
                double examPercentage = (maxMarks > 0) ? (marks * 100.0 / maxMarks) : 0.0;
                String pctStr = pctFormat.format(examPercentage) + "%";
                
                // Determine pass/fail for this exam
                String examResult = (marks >= passMarks) ? "Pass" : "Fail";
                
                // Add to table
                examTableModel.addRow(new Object[]{
                    exam.getExamName(),
                    marks,
                    maxMarks,
                    passMarks,
                    pctStr,
                    examResult
                });
                
                // Update totals
                totalObtained += marks;
                totalMax += maxMarks;
            }
            
            // Calculate overall percentage
            double overallPercentage = (totalMax > 0) ? (totalObtained * 100.0 / totalMax) : 0.0;
            String overallPctStr = pctFormat.format(overallPercentage) + "%";
            
            // Determine overall result
            String overallResult = (overallPercentage >= 40.0) ? "PASS" : "FAIL";
            Color resultColor = (overallPercentage >= 40.0) ? new Color(46, 204, 113) : new Color(231, 76, 60);
            
            // Update summary labels
            lblOverallPercentage.setText(totalObtained + "/" + totalMax);
            lblOverallResult.setText(overallResult);
            lblOverallResult.setForeground(resultColor);
            
            // Update status
            statusLabel.setText(String.format("✓ Showing performance for %s (%s) in selected subject | Overall: %.1f%% (%s)", 
                selectedStudentName, selectedStudentRollNo, overallPercentage, overallResult));
            
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading student performance: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading performance:\n" + e.getMessage(), 
                "Load Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void exportStudentReport() {
        if (selectedStudentId == -1 || examTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "No student performance data to export!\nSelect a student first.",
                "Empty Report", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Student Performance Report");
        fileChooser.setSelectedFile(new java.io.File(selectedStudentRollNo + "_performance_report.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile())) {
                // Write student info header
                fw.append("Student Performance Report\n");
                fw.append("Student: " + selectedStudentName + "\n");
                fw.append("Roll No: " + selectedStudentRollNo + "\n");
                fw.append("Class: " + comboClass.getSelectedItem().toString().split(" - ")[1].trim() + "\n");
                fw.append("Subject: " + comboSubject.getSelectedItem().toString().split(" - ")[1].trim() + "\n");
                fw.append("\n");
                
                // Write exam performance headers
                for (int i = 0; i < examTableModel.getColumnCount(); i++) {
                    fw.append(examTableModel.getColumnName(i));
                    if (i < examTableModel.getColumnCount() - 1) fw.append(",");
                }
                fw.append("\n");
                
                // Write exam performance data
                for (int i = 0; i < examTableModel.getRowCount(); i++) {
                    for (int j = 0; j < examTableModel.getColumnCount(); j++) {
                        fw.append(examTableModel.getValueAt(i, j).toString());
                        if (j < examTableModel.getColumnCount() - 1) fw.append(",");
                    }
                    fw.append("\n");
                }
                
                // Write summary
                fw.append("\n");
                fw.append("Overall Summary\n");
                fw.append("Total Obtained,Total Max Marks,Overall Percentage,Overall Result\n");
                fw.append(lblOverallPercentage.getText().replace("/", ",") + "," + 
                         lblOverallResult.getText().replace("%", "") + "," + 
                         lblOverallResult.getText());
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Student performance report exported successfully!\nFile: " + fileChooser.getSelectedFile().getName(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("✓ Report exported: " + fileChooser.getSelectedFile().getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error exporting report:\n" + ex.getMessage(),
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
                JFrame mockResultsUI = new JFrame("Results UI");
                mockResultsUI.setSize(1000, 650);
                mockResultsUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mockResultsUI.setVisible(true);
                new StudentSubjectPerformanceUI(1, mockResultsUI).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Application failed to start!\nError: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}