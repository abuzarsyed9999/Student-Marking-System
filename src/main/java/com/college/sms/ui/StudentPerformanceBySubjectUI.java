//package com.college.sms.ui;
//
//import com.college.sms.dao.ClassDAO;
//import com.college.sms.dao.ExamDAO;
//import com.college.sms.dao.StudentDAO;
//import com.college.sms.dao.SubjectDAO;
//import com.college.sms.model.Exam;
//import com.college.sms.model.Subject;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.border.LineBorder;
//import javax.swing.border.TitledBorder;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.JTableHeader;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.io.FileWriter;
//import java.text.DecimalFormat;
//import java.util.*;
//import java.util.List;
//
//public class StudentPerformanceBySubjectUI extends JFrame {
//
//    private JComboBox<String> comboClass;
//    private JList<StudentItem> studentList;
//    private DefaultListModel<StudentItem> studentListModel;
//    private JTable examPerformanceTable, subjectSummaryTable;
//    private DefaultTableModel examTableModel, subjectSummaryModel;
//    private JTextField searchField;
//    private JLabel studentInfoLabel, statusLabel;
//    private ClassDAO classDAO;
//    private SubjectDAO subjectDAO;
//    private ExamDAO examDAO;
//    private StudentDAO studentDAO;
//    private int facultyId;
//    private JFrame previousUI;
//    private boolean isLoadingStudents = false;
//    private int selectedStudentId = -1;
//    private String selectedStudentName = "";
//    private String selectedStudentRollNo = "";
//    private int currentClassId = -1;
//
//    public StudentPerformanceBySubjectUI(int facultyId, JFrame previousUI) {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        this.classDAO = new ClassDAO();
//        this.subjectDAO = new SubjectDAO();
//        this.examDAO = new ExamDAO();
//        this.studentDAO = new StudentDAO();
//        initComponents();
//        loadData();
//        setVisible(true);
//    }
//
//    private void initComponents() {
//        setTitle("🎓 Student Performance by Subject | Faculty ID: " + facultyId);
//        setSize(1300, 780);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(new BorderLayout(15, 15));
//        getContentPane().setBackground(new Color(245, 247, 250));
//
//        // ===== TOP SECTION WRAPPER =====
//        JPanel topWrapper = new JPanel();
//        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
//        topWrapper.setOpaque(false);
//
//        // ===== HEADER PANEL =====
//        JPanel headerPanel = new JPanel(new BorderLayout());
//        headerPanel.setBackground(new Color(41, 128, 185));
//        headerPanel.setPreferredSize(new Dimension(0, 75));
//        headerPanel.setBorder(new EmptyBorder(0, 25, 0, 25));
//
//        JLabel titleLabel = new JLabel("📊 Individual Student Performance Across All Subjects");
//        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
//        titleLabel.setForeground(Color.WHITE);
//        headerPanel.add(titleLabel, BorderLayout.WEST);
//
//        JButton btnBack = createModernButton("⇦ Back to Results", new Color(30, 136, 56), Color.WHITE);
//        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
//        btnBack.setPreferredSize(new Dimension(190, 42));
//        btnBack.addActionListener(e -> {
//            dispose();
//            if (previousUI != null) {
//                previousUI.setVisible(true);
//                previousUI.toFront();
//                previousUI.requestFocus();
//            }
//        });
//        headerPanel.add(btnBack, BorderLayout.EAST);
//        topWrapper.add(headerPanel);
//
//        // ===== FILTER PANEL =====
//        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 18));
//        filterPanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
//            "🔍 Select Class to View Students",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 17),
//            new Color(41, 128, 185)
//        ));
//        filterPanel.setBackground(Color.WHITE);
//        filterPanel.setPreferredSize(new Dimension(0, 90));
//
//        filterPanel.add(new JLabel("🏫 Class:"));
//        comboClass = createModernComboBox();
//        filterPanel.add(comboClass);
//
//        JButton btnLoad = createModernButton("👥 Load Students", new Color(142, 68, 173), Color.WHITE);
//        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 15));
//        btnLoad.setPreferredSize(new Dimension(180, 42));
//        btnLoad.addActionListener(e -> loadStudents());
//        filterPanel.add(btnLoad);
//        topWrapper.add(filterPanel);
//
//        add(topWrapper, BorderLayout.NORTH);
//
//        // ===== MAIN SPLIT PANE (STUDENTS LEFT | PERFORMANCE RIGHT) =====
//        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//        mainSplitPane.setDividerLocation(380);
//        mainSplitPane.setResizeWeight(0.3);
//
//        // ===== LEFT PANEL: STUDENT LIST =====
//        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
//        leftPanel.setBackground(Color.WHITE);
//        leftPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
//
//        // Search panel
//        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
//        searchPanel.setBackground(Color.WHITE);
//        searchPanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
//            "🔎 Search Student (Roll No or Name)",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 14),
//            new Color(52, 152, 219)
//        ));
//        searchPanel.setPreferredSize(new Dimension(0, 60));
//
//        searchField = new JTextField(18);
//        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        searchField.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
//        searchField.setPreferredSize(new Dimension(280, 34));
//        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
//            public void keyReleased(java.awt.event.KeyEvent evt) {
//                filterStudents(searchField.getText());
//            }
//        });
//        searchPanel.add(searchField);
//        leftPanel.add(searchPanel, BorderLayout.NORTH);
//
//        // Student list
//        studentListModel = new DefaultListModel<>();
//        studentList = new JList<>(studentListModel);
//        studentList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
//        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        studentList.setBackground(Color.WHITE);
//        studentList.setCellRenderer(new StudentListCellRenderer());
//        studentList.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
//        studentList.setSelectionBackground(new Color(52, 152, 219));
//        studentList.setSelectionForeground(Color.WHITE);
//
//        studentList.addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting() && studentList.getSelectedValue() != null) {
//                StudentItem item = studentList.getSelectedValue();
//                selectedStudentId = item.getStudentId();
//                selectedStudentName = item.getName();
//                selectedStudentRollNo = item.getRollNo();
//                loadStudentPerformance();
//            }
//        });
//
//        JScrollPane studentScroll = new JScrollPane(studentList);
//        studentScroll.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
//            "👥 Students in Selected Class",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 15),
//            new Color(52, 152, 219)
//        ));
//        leftPanel.add(studentScroll, BorderLayout.CENTER);
//
//        // Status label
//        JLabel studentCountLabel = new JLabel("No students loaded");
//        studentCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        studentCountLabel.setForeground(new Color(100, 100, 100));
//        studentCountLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
//        leftPanel.add(studentCountLabel, BorderLayout.SOUTH);
//
//        mainSplitPane.setLeftComponent(leftPanel);
//
//        // ===== RIGHT PANEL: STUDENT PERFORMANCE =====
//        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
//        rightPanel.setBackground(Color.WHITE);
//        rightPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
//
//        // Student info header
//        studentInfoLabel = new JLabel("⇦ Select a student from the left panel to view detailed performance");
//        studentInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
//        studentInfoLabel.setForeground(new Color(44, 62, 80));
//        studentInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        studentInfoLabel.setBorder(new EmptyBorder(15, 20, 15, 20));
//        studentInfoLabel.setBackground(new Color(248, 250, 252));
//        studentInfoLabel.setOpaque(true);
//        rightPanel.add(studentInfoLabel, BorderLayout.NORTH);
//
//        // ===== EXAM PERFORMANCE TABLE =====
//        JPanel examPanel = new JPanel(new BorderLayout(10, 10));
//        examPanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
//            "📝 Exam-wise Performance Across All Subjects",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 16),
//            new Color(52, 152, 219)
//        ));
//        examPanel.setBackground(Color.WHITE);
//
//        examTableModel = new DefaultTableModel(
//            new String[]{"Subject", "Exam", "Marks", "Max", "Pass Marks", "Percentage", "Result"}, 0
//        ) {
//            public boolean isCellEditable(int row, int col) { return false; }
//        };
//
//        examPerformanceTable = new JTable(examTableModel);
//        examPerformanceTable.setRowHeight(34);
//        examPerformanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        examPerformanceTable.setGridColor(new Color(230, 230, 230));
//        examPerformanceTable.setSelectionBackground(new Color(52, 152, 219));
//        examPerformanceTable.setSelectionForeground(Color.WHITE);
//        examPerformanceTable.setIntercellSpacing(new Dimension(0, 2));
//
//        JTableHeader examHeader = examPerformanceTable.getTableHeader();
//        examHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        examHeader.setBackground(new Color(52, 73, 94));
//        examHeader.setForeground(Color.WHITE);
//        examHeader.setPreferredSize(new Dimension(0, 42));
//
//        // Color-coded rendering for exam table
//        examPerformanceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
//                                                           boolean hasFocus, int row, int column) {
//                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
//                if (!isSelected) {
//                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
//                    
//                    // Color-code Result column (index 6)
//                    if (column == 6) {
//                        String result = value.toString();
//                        if ("Pass".equals(result) || "✓".equals(result)) {
//                            c.setBackground(new Color(200, 230, 201, 60));
//                            c.setForeground(new Color(27, 94, 32));
//                            c.setFont(c.getFont().deriveFont(Font.BOLD));
//                        } else {
//                            c.setBackground(new Color(255, 205, 210, 60));
//                            c.setForeground(new Color(183, 28, 28));
//                            c.setFont(c.getFont().deriveFont(Font.BOLD));
//                        }
//                    }
//                    
//                    // Color-code Percentage column (index 5)
//                    if (column == 5) {
//                        try {
//                            String pctStr = value.toString().replace("%", "").trim();
//                            double pct = Double.parseDouble(pctStr);
//                            if (pct >= 90) c.setForeground(new Color(27, 94, 32));
//                            else if (pct >= 75) c.setForeground(new Color(40, 116, 166));
//                            else if (pct >= 60) c.setForeground(new Color(137, 104, 0));
//                            else if (pct >= 40) c.setForeground(new Color(165, 82, 0));
//                            else c.setForeground(new Color(183, 28, 28));
//                        } catch (Exception ignored) {}
//                    }
//                }
//                return c;
//            }
//        });
//
//        JScrollPane examScroll = new JScrollPane(examPerformanceTable);
//        examPanel.add(examScroll, BorderLayout.CENTER);
//        rightPanel.add(examPanel, BorderLayout.CENTER);
//
//        // ===== SUBJECT SUMMARY TABLE =====
//        JPanel summaryPanel = new JPanel(new BorderLayout(10, 10));
//        summaryPanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
//            "🎯 Subject-wise Summary",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 16),
//            new Color(41, 128, 185)
//        ));
//        summaryPanel.setBackground(Color.WHITE);
//        summaryPanel.setPreferredSize(new Dimension(0, 200));
//
//        subjectSummaryModel = new DefaultTableModel(
//            new String[]{"Subject", "Total Obtained", "Total Max", "Percentage", "Result"}, 0
//        ) {
//            public boolean isCellEditable(int row, int col) { return false; }
//        };
//
//        subjectSummaryTable = new JTable(subjectSummaryModel);
//        subjectSummaryTable.setRowHeight(34);
//        subjectSummaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        subjectSummaryTable.setGridColor(new Color(230, 230, 230));
//        subjectSummaryTable.setSelectionBackground(new Color(52, 152, 219));
//        subjectSummaryTable.setSelectionForeground(Color.WHITE);
//        subjectSummaryTable.setIntercellSpacing(new Dimension(0, 2));
//
//        JTableHeader summaryHeader = subjectSummaryTable.getTableHeader();
//        summaryHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        summaryHeader.setBackground(new Color(41, 128, 185));
//        summaryHeader.setForeground(Color.WHITE);
//        summaryHeader.setPreferredSize(new Dimension(0, 42));
//
//        // Color-coded rendering for summary table
//        subjectSummaryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
//                                                           boolean hasFocus, int row, int column) {
//                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
//                if (!isSelected) {
//                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
//                    
//                    // Color-code Result column (index 4)
//                    if (column == 4) {
//                        String result = value.toString();
//                        if ("Pass".equals(result)) {
//                            c.setBackground(new Color(200, 230, 201, 60));
//                            c.setForeground(new Color(27, 94, 32));
//                            c.setFont(c.getFont().deriveFont(Font.BOLD, 15f));
//                        } else {
//                            c.setBackground(new Color(255, 205, 210, 60));
//                            c.setForeground(new Color(183, 28, 28));
//                            c.setFont(c.getFont().deriveFont(Font.BOLD, 15f));
//                        }
//                    }
//                    
//                    // Color-code Percentage column (index 3)
//                    if (column == 3) {
//                        try {
//                            String pctStr = value.toString().replace("%", "").trim();
//                            double pct = Double.parseDouble(pctStr);
//                            if (pct >= 90) c.setForeground(new Color(27, 94, 32));
//                            else if (pct >= 75) c.setForeground(new Color(40, 116, 166));
//                            else if (pct >= 60) c.setForeground(new Color(137, 104, 0));
//                            else if (pct >= 40) c.setForeground(new Color(165, 82, 0));
//                            else c.setForeground(new Color(183, 28, 28));
//                        } catch (Exception ignored) {}
//                    }
//                }
//                return c;
//            }
//        });
//
//        JScrollPane summaryScroll = new JScrollPane(subjectSummaryTable);
//        summaryPanel.add(summaryScroll, BorderLayout.CENTER);
//        rightPanel.add(summaryPanel, BorderLayout.SOUTH);
//
//        // ===== ACTION PANEL =====
//        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
//        actionPanel.setBackground(Color.WHITE);
//        
//        JButton btnExport = createModernButton("📤 Export Full Report", new Color(155, 89, 182), Color.WHITE);
//        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        btnExport.setPreferredSize(new Dimension(190, 40));
//        btnExport.addActionListener(e -> exportStudentReport());
//        actionPanel.add(btnExport);
//        
//        rightPanel.add(actionPanel, BorderLayout.SOUTH);
//
//        mainSplitPane.setRightComponent(rightPanel);
//        add(mainSplitPane, BorderLayout.CENTER);
//
//        // ===== STATUS BAR =====
//        statusLabel = new JLabel("Ready to analyze student performance. Select class and click 'Load Students'.");
//        statusLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
//        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
//        statusLabel.setBackground(new Color(236, 240, 241));
//        statusLabel.setForeground(new Color(75, 75, 75));
//        statusLabel.setOpaque(true);
//        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
//        add(statusLabel, BorderLayout.SOUTH);
//
//        // ===== EVENT LISTENERS =====
//        comboClass.addActionListener(e -> {
//            if (comboClass.getSelectedItem() != null && !comboClass.getSelectedItem().toString().contains("--")) {
//                currentClassId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//                studentListModel.clear();
//                studentInfoLabel.setText("⇦ Select a student from the left panel to view detailed performance");
//                examTableModel.setRowCount(0);
//                subjectSummaryModel.setRowCount(0);
//            }
//        });
//    }
//
//    private JComboBox<String> createModernComboBox() {
//        JComboBox<String> combo = new JComboBox<>();
//        combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
//        combo.setPreferredSize(new Dimension(260, 42));
//        combo.setBackground(Color.WHITE);
//        combo.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
//        return combo;
//    }
//
//    private JButton createModernButton(String text, Color bgColor, Color fgColor) {
//        JButton button = new JButton(text);
//        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        button.setForeground(fgColor);
//        button.setBackground(bgColor);
//        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
//        button.setFocusPainted(false);
//        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        
//        button.addMouseListener(new MouseAdapter() {
//            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.darker()); }
//            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
//        });
//        return button;
//    }
//
//    // Custom cell renderer for student list
//    private class StudentListCellRenderer extends JLabel implements ListCellRenderer<StudentItem> {
//        public StudentListCellRenderer() {
//            setOpaque(true);
//            setFont(new Font("Segoe UI", Font.PLAIN, 15));
//            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
//        }
//
//        @Override
//        public Component getListCellRendererComponent(JList<? extends StudentItem> list, StudentItem value,
//                                                      int index, boolean isSelected, boolean cellHasFocus) {
//            setText("<html><b>" + value.getRollNo() + "</b> - " + value.getName() + "</html>");
//            if (isSelected) {
//                setBackground(new Color(52, 152, 219));
//                setForeground(Color.WHITE);
//            } else {
//                setBackground(Color.WHITE);
//                setForeground(new Color(44, 62, 80));
//            }
//            return this;
//        }
//    }
//
//    // Student item for list model
//    private class StudentItem {
//        private int studentId;
//        private String rollNo;
//        private String name;
//        
//        public StudentItem(int studentId, String rollNo, String name) {
//            this.studentId = studentId;
//            this.rollNo = rollNo;
//            this.name = name;
//        }
//        
//        public int getStudentId() { return studentId; }
//        public String getRollNo() { return rollNo; }
//        public String getName() { return name; }
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
//            comboClass.addItem("-- No classes assigned --");
//            statusLabel.setText("⚠️ No classes assigned to you. Contact admin.");
//        } else {
//            for (String[] c : classes) {
//                comboClass.addItem(c[0] + " - " + c[1]);
//            }
//            comboClass.setSelectedIndex(0);
//            currentClassId = Integer.parseInt(classes.get(0)[0]);
//        }
//    }
//
//    private void loadStudents() {
//        studentListModel.clear();
//        examTableModel.setRowCount(0);
//        subjectSummaryModel.setRowCount(0);
//        studentInfoLabel.setText("⇦ Select a student from the left panel to view detailed performance");
//        selectedStudentId = -1;
//        
//        if (comboClass.getSelectedItem() == null || comboClass.getSelectedItem().toString().contains("--")) {
//            JOptionPane.showMessageDialog(this, "Please select a valid class!", "Missing Selection", JOptionPane.WARNING_MESSAGE);
//            statusLabel.setText("⚠️ Select valid class");
//            return;
//        }
//
//        try {
//            currentClassId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            String className = comboClass.getSelectedItem().toString().split(" - ")[1].trim();
//            
//            List<String[]> students = studentDAO.getStudentsByClass(currentClassId);
//            if (students.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "No students found in this class!", "Empty Class", JOptionPane.INFORMATION_MESSAGE);
//                statusLabel.setText("⚠️ No students found in " + className);
//                return;
//            }
//            
//            // Populate student list
//            for (String[] student : students) {
//                int studentId = Integer.parseInt(student[0]);
//                String rollNo = student[1];
//                String name = student[2];
//                studentListModel.addElement(new StudentItem(studentId, rollNo, name));
//            }
//            
//            statusLabel.setText(String.format("✓ Loaded %d students for %s. Select a student to view performance.", 
//                students.size(), className));
//            searchField.setText("");
//            
//        } catch (Exception e) {
//            statusLabel.setText("❌ Error loading students: " + e.getMessage());
//            JOptionPane.showMessageDialog(this, "Error loading students:\n" + e.getMessage(), 
//                "Load Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }
//
//    private void filterStudents(String searchText) {
//        if (searchText.trim().isEmpty()) {
//            studentList.setModel(studentListModel);
//            return;
//        }
//        
//        DefaultListModel<StudentItem> filteredModel = new DefaultListModel<>();
//        for (int i = 0; i < studentListModel.size(); i++) {
//            StudentItem item = studentListModel.getElementAt(i);
//            if (item.getRollNo().toLowerCase().contains(searchText.toLowerCase()) || 
//                item.getName().toLowerCase().contains(searchText.toLowerCase())) {
//                filteredModel.addElement(item);
//            }
//        }
//        studentList.setModel(filteredModel);
//    }
//
//    private void loadStudentPerformance() {
//        examTableModel.setRowCount(0);
//        subjectSummaryModel.setRowCount(0);
//        
//        if (selectedStudentId == -1 || currentClassId == -1) {
//            studentInfoLabel.setText("Select a student and load class first");
//            return;
//        }
//        
//        try {
//            // Update student info label
//            studentInfoLabel.setText("<html><b>" + selectedStudentName + "</b> (" + selectedStudentRollNo + ")</html>");
//            
//            // Get all subjects for this class and faculty
//            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(currentClassId, facultyId);
//            if (subjects.isEmpty()) {
//                statusLabel.setText("⚠️ No subjects found for this class. Add subjects first.");
//                JOptionPane.showMessageDialog(this, 
//                    "No subjects found for this class!\nAdd subjects in Subject Management first.",
//                    "No Subjects", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            
//            DecimalFormat pctFormat = new DecimalFormat("0.0");
//            Map<Integer, SubjectSummary> subjectSummaries = new HashMap<>();
//            
//            // Process each subject
//            for (Subject subject : subjects) {
//                int subjectId = subject.getSubjectId();
//                String subjectName = subject.getSubjectName();
//                
//                // Get all exams for this subject
//                List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, currentClassId, subjectId);
//                if (exams.isEmpty()) continue; // Skip subjects with no exams
//                
//                int subjectTotalObtained = 0;
//                int subjectTotalMax = 0;
//                
//                // Process each exam in the subject
//                for (Exam exam : exams) {
//                    int marks = studentDAO.getMarksByExam(selectedStudentId, exam.getExamId());
//                    int maxMarks = exam.getMaxMarks();
//                    int passMarks = exam.getPassMarks();
//                    
//                    // Calculate exam percentage
//                    double examPercentage = (maxMarks > 0) ? (marks * 100.0 / maxMarks) : 0.0;
//                    String pctStr = pctFormat.format(examPercentage) + "%";
//                    
//                    // Determine exam result
//                    String examResult = (marks >= passMarks) ? "Pass ✓" : "Fail ✗";
//                    
//                    // Add to exam performance table
//                    examTableModel.addRow(new Object[]{
//                        subjectName,
//                        exam.getExamName(),
//                        marks,
//                        maxMarks,
//                        passMarks,
//                        pctStr,
//                        examResult
//                    });
//                    
//                    // Accumulate for subject summary
//                    subjectTotalObtained += marks;
//                    subjectTotalMax += maxMarks;
//                }
//                
//                // Calculate subject percentage and result
//                double subjectPercentage = (subjectTotalMax > 0) ? (subjectTotalObtained * 100.0 / subjectTotalMax) : 0.0;
//                String subjectPctStr = pctFormat.format(subjectPercentage) + "%";
//                String subjectResult = (subjectPercentage >= 40.0) ? "Pass" : "Fail";
//                
//                // Store for subject summary table
//                subjectSummaries.put(subjectId, new SubjectSummary(
//                    subjectName, subjectTotalObtained, subjectTotalMax, 
//                    subjectPercentage, subjectResult
//                ));
//            }
//            
//            // Populate subject summary table
//            List<SubjectSummary> summaries = new ArrayList<>(subjectSummaries.values());
//            summaries.sort(Comparator.comparingDouble(SubjectSummary::getPercentage).reversed());
//            
//            for (SubjectSummary summary : summaries) {
//                subjectSummaryModel.addRow(new Object[]{
//                    summary.getSubjectName(),
//                    summary.getTotalObtained(),
//                    summary.getTotalMax(),
//                    String.format("%.1f%%", summary.getPercentage()),
//                    summary.getResult()
//                });
//            }
//            
//            // Update status
//            statusLabel.setText(String.format("✓ Showing performance for %s (%s) across %d subjects", 
//                selectedStudentName, selectedStudentRollNo, summaries.size()));
//            
//        } catch (Exception e) {
//            statusLabel.setText("❌ Error loading performance: " + e.getMessage());
//            JOptionPane.showMessageDialog(this, "Error loading performance:\n" + e.getMessage(), 
//                "Load Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }
//
//    // Subject summary data class
//    private static class SubjectSummary {
//        private String subjectName;
//        private int totalObtained;
//        private int totalMax;
//        private double percentage;
//        private String result;
//        
//        public SubjectSummary(String subjectName, int totalObtained, int totalMax, 
//                             double percentage, String result) {
//            this.subjectName = subjectName;
//            this.totalObtained = totalObtained;
//            this.totalMax = totalMax;
//            this.percentage = percentage;
//            this.result = result;
//        }
//        
//        public String getSubjectName() { return subjectName; }
//        public int getTotalObtained() { return totalObtained; }
//        public int getTotalMax() { return totalMax; }
//        public double getPercentage() { return percentage; }
//        public String getResult() { return result; }
//    }
//
//    private void exportStudentReport() {
//        if (selectedStudentId == -1 || examTableModel.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, 
//                "No student performance data to export!\nSelect a student first.",
//                "Empty Report", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Student Performance Report");
//        fileChooser.setSelectedFile(new java.io.File(selectedStudentRollNo + "_full_performance_report.csv"));
//        
//        int userSelection = fileChooser.showSaveDialog(this);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile())) {
//                // Write header
//                fw.append("STUDENT PERFORMANCE REPORT\n");
//                fw.append("Student: " + selectedStudentName + "\n");
//                fw.append("Roll No: " + selectedStudentRollNo + "\n");
//                fw.append("Class: " + comboClass.getSelectedItem().toString().split(" - ")[1].trim() + "\n");
//                fw.append("Generated: " + new java.util.Date() + "\n\n");
//                
//                // Write exam performance table
//                fw.append("EXAM-WISE PERFORMANCE\n");
//                for (int i = 0; i < examTableModel.getColumnCount(); i++) {
//                    fw.append(examTableModel.getColumnName(i));
//                    if (i < examTableModel.getColumnCount() - 1) fw.append(",");
//                }
//                fw.append("\n");
//                
//                for (int i = 0; i < examTableModel.getRowCount(); i++) {
//                    for (int j = 0; j < examTableModel.getColumnCount(); j++) {
//                        fw.append(examTableModel.getValueAt(i, j).toString());
//                        if (j < examTableModel.getColumnCount() - 1) fw.append(",");
//                    }
//                    fw.append("\n");
//                }
//                
//                fw.append("\n\n");
//                
//                // Write subject summary table
//                fw.append("SUBJECT-WISE SUMMARY\n");
//                for (int i = 0; i < subjectSummaryModel.getColumnCount(); i++) {
//                    fw.append(subjectSummaryModel.getColumnName(i));
//                    if (i < subjectSummaryModel.getColumnCount() - 1) fw.append(",");
//                }
//                fw.append("\n");
//                
//                for (int i = 0; i < subjectSummaryModel.getRowCount(); i++) {
//                    for (int j = 0; j < subjectSummaryModel.getColumnCount(); j++) {
//                        fw.append(subjectSummaryModel.getValueAt(i, j).toString());
//                        if (j < subjectSummaryModel.getColumnCount() - 1) fw.append(",");
//                    }
//                    fw.append("\n");
//                }
//                
//                JOptionPane.showMessageDialog(this, 
//                    "✅ Student performance report exported successfully!\nFile: " + fileChooser.getSelectedFile().getName(),
//                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
//                statusLabel.setText("✓ Report exported: " + fileChooser.getSelectedFile().getName());
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(this, "❌ Error exporting report:\n" + ex.getMessage(),
//                    "Export Error", JOptionPane.ERROR_MESSAGE);
//                statusLabel.setText("Export failed: " + ex.getMessage());
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                JFrame mockResultsUI = new JFrame("Results UI");
//                mockResultsUI.setSize(1000, 650);
//                mockResultsUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                mockResultsUI.setVisible(true);
//                new StudentPerformanceBySubjectUI(1, mockResultsUI).setVisible(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(null,
//                    "Application failed to start!\nError: " + e.getMessage(),
//                    "Startup Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//    }
//}

//--
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

public class StudentPerformanceBySubjectUI extends JFrame {

    private JComboBox<String> comboClass;
    private JList<StudentItem> studentList;
    private DefaultListModel<StudentItem> studentListModel;
    private JTable examPerformanceTable, subjectSummaryTable;
    private DefaultTableModel examTableModel, subjectSummaryModel;
    private JTextField searchField;
    private JLabel studentInfoLabel, statusLabel;
    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;
    private ExamDAO examDAO;
    private StudentDAO studentDAO;
    private int facultyId;
    private JFrame previousUI;
    private int selectedStudentId = -1;
    private String selectedStudentName = "";
    private String selectedStudentRollNo = "";
    private int currentClassId = -1;

    public StudentPerformanceBySubjectUI(int facultyId, JFrame previousUI) {
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
        setTitle("🎓 Student Performance by Subject | Faculty ID: " + facultyId);
        setSize(1300, 820);
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

        JLabel titleLabel = new JLabel("📊 Individual Student Performance Across All Subjects");
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
            "🔍 Select Class to View Students",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 17),
            new Color(41, 128, 185)
        ));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setPreferredSize(new Dimension(0, 90));

        filterPanel.add(new JLabel("🏫 Class:"));
        comboClass = createModernComboBox();
        filterPanel.add(comboClass);

        JButton btnLoad = createModernButton("👥 Load Students", new Color(142, 68, 173), Color.WHITE);
        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLoad.setPreferredSize(new Dimension(180, 42));
        btnLoad.addActionListener(e -> loadStudents());
        filterPanel.add(btnLoad);
        topWrapper.add(filterPanel);

        add(topWrapper, BorderLayout.NORTH);

        // ===== MAIN SPLIT PANE =====
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(380);
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

        searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        searchField.setPreferredSize(new Dimension(280, 34));
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
        studentList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setBackground(Color.WHITE);
        studentList.setCellRenderer(new StudentListCellRenderer());
        studentList.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        studentList.setSelectionBackground(new Color(52, 152, 219));
        studentList.setSelectionForeground(Color.WHITE);

        studentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentList.getSelectedValue() != null) {
                StudentItem item = studentList.getSelectedValue();
                selectedStudentId = item.getStudentId();
                selectedStudentName = item.getName();
                selectedStudentRollNo = item.getRollNo();
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

        JLabel studentCountLabel = new JLabel("No students loaded");
        studentCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentCountLabel.setForeground(new Color(100, 100, 100));
        studentCountLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        leftPanel.add(studentCountLabel, BorderLayout.SOUTH);

        mainSplitPane.setLeftComponent(leftPanel);

        // ===== RIGHT PANEL: PERFORMANCE TABLES =====
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Student info header
        studentInfoLabel = new JLabel("⇦ Select a student from the left panel to view detailed performance");
        studentInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        studentInfoLabel.setForeground(new Color(44, 62, 80));
        studentInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        studentInfoLabel.setBorder(new EmptyBorder(15, 20, 15, 20));
        studentInfoLabel.setBackground(new Color(248, 250, 252));
        studentInfoLabel.setOpaque(true);
        rightPanel.add(studentInfoLabel, BorderLayout.NORTH);

        // ===== EXAM PERFORMANCE TABLE (CENTER) =====
        JPanel examPanel = new JPanel(new BorderLayout(10, 10));
        examPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "📝 Exam-wise Performance Across All Subjects",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(52, 152, 219)
        ));
        examPanel.setBackground(Color.WHITE);

        examTableModel = new DefaultTableModel(
            new String[]{"Subject", "Exam", "Marks", "Max", "Pass Marks", "Percentage", "Result"}, 0
        ) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        examPerformanceTable = new JTable(examTableModel);
        examPerformanceTable.setRowHeight(34);
        examPerformanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        examPerformanceTable.setGridColor(new Color(230, 230, 230));
        examPerformanceTable.setSelectionBackground(new Color(52, 152, 219));
        examPerformanceTable.setSelectionForeground(Color.WHITE);
        examPerformanceTable.setIntercellSpacing(new Dimension(0, 2));

        JTableHeader examHeader = examPerformanceTable.getTableHeader();
        examHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        examHeader.setBackground(new Color(52, 73, 94));
        examHeader.setForeground(Color.WHITE);
        examHeader.setPreferredSize(new Dimension(0, 42));

        examPerformanceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
                    if (column == 6) {
                        String result = value.toString();
                        if ("Pass".equals(result) || "✓".equals(result)) {
                            c.setBackground(new Color(200, 230, 201, 60));
                            c.setForeground(new Color(27, 94, 32));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setBackground(new Color(255, 205, 210, 60));
                            c.setForeground(new Color(183, 28, 28));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    }
                    if (column == 5) {
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

        JScrollPane examScroll = new JScrollPane(examPerformanceTable);
        examPanel.add(examScroll, BorderLayout.CENTER);
        rightPanel.add(examPanel, BorderLayout.CENTER);

        // ✅ FIX: Bottom section with BOTH Summary Table + Action Buttons
        JPanel bottomSection = new JPanel(new BorderLayout(10, 10));
        bottomSection.setBackground(Color.WHITE);

        // Subject Summary Table
        JPanel summaryPanel = new JPanel(new BorderLayout(10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            "🎯 Subject-wise Summary",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(41, 128, 185)
        ));
        summaryPanel.setBackground(Color.WHITE);

        subjectSummaryModel = new DefaultTableModel(
            new String[]{"Subject", "Total Obtained", "Total Max", "Percentage", "Result"}, 0
        ) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        subjectSummaryTable = new JTable(subjectSummaryModel);
        subjectSummaryTable.setRowHeight(34);
        subjectSummaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subjectSummaryTable.setGridColor(new Color(230, 230, 230));
        subjectSummaryTable.setSelectionBackground(new Color(52, 152, 219));
        subjectSummaryTable.setSelectionForeground(Color.WHITE);
        subjectSummaryTable.setIntercellSpacing(new Dimension(0, 2));

        JTableHeader summaryHeader = subjectSummaryTable.getTableHeader();
        summaryHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summaryHeader.setBackground(new Color(41, 128, 185));
        summaryHeader.setForeground(Color.WHITE);
        summaryHeader.setPreferredSize(new Dimension(0, 42));

        subjectSummaryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
                    if (column == 4) {
                        String result = value.toString();
                        if ("Pass".equals(result)) {
                            c.setBackground(new Color(200, 230, 201, 60));
                            c.setForeground(new Color(27, 94, 32));
                            c.setFont(c.getFont().deriveFont(Font.BOLD, 15f));
                        } else {
                            c.setBackground(new Color(255, 205, 210, 60));
                            c.setForeground(new Color(183, 28, 28));
                            c.setFont(c.getFont().deriveFont(Font.BOLD, 15f));
                        }
                    }
                    if (column == 3) {
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

        JScrollPane summaryScroll = new JScrollPane(subjectSummaryTable);
        summaryScroll.setPreferredSize(new Dimension(0, 180));
        summaryPanel.add(summaryScroll, BorderLayout.CENTER);
        bottomSection.add(summaryPanel, BorderLayout.CENTER);

        // Action Buttons (below summary table)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JButton btnExport = createModernButton("📤 Export Full Report", new Color(155, 89, 182), Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExport.setPreferredSize(new Dimension(190, 40));
        btnExport.addActionListener(e -> exportStudentReport());
        actionPanel.add(btnExport);
        
        bottomSection.add(actionPanel, BorderLayout.SOUTH);

        // Add bottom section to right panel
        rightPanel.add(bottomSection, BorderLayout.SOUTH);

        mainSplitPane.setRightComponent(rightPanel);
        add(mainSplitPane, BorderLayout.CENTER);

        // ===== STATUS BAR =====
        statusLabel = new JLabel("Ready to analyze student performance. Select class and click 'Load Students'.");
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
                currentClassId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
                studentListModel.clear();
                studentInfoLabel.setText("⇦ Select a student from the left panel to view detailed performance");
                examTableModel.setRowCount(0);
                subjectSummaryModel.setRowCount(0);
            }
        });
    }

    private JComboBox<String> createModernComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        combo.setPreferredSize(new Dimension(260, 42));
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

    private class StudentListCellRenderer extends JLabel implements ListCellRenderer<StudentItem> {
        public StudentListCellRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 15));
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        }
        @Override
        public Component getListCellRendererComponent(JList<? extends StudentItem> list, StudentItem value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            setText("<html><b>" + value.getRollNo() + "</b> - " + value.getName() + "</html>");
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
    }

    private void loadData() { loadClasses(); }

    private void loadClasses() {
        comboClass.removeAllItems();
        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
        if (classes.isEmpty()) {
            comboClass.addItem("-- No classes assigned --");
            statusLabel.setText("⚠️ No classes assigned to you. Contact admin.");
        } else {
            for (String[] c : classes) comboClass.addItem(c[0] + " - " + c[1]);
            comboClass.setSelectedIndex(0);
            currentClassId = Integer.parseInt(classes.get(0)[0]);
        }
    }

    private void loadStudents() {
        studentListModel.clear();
        examTableModel.setRowCount(0);
        subjectSummaryModel.setRowCount(0);
        studentInfoLabel.setText("⇦ Select a student from the left panel to view detailed performance");
        selectedStudentId = -1;
        
        if (comboClass.getSelectedItem() == null || comboClass.getSelectedItem().toString().contains("--")) {
            JOptionPane.showMessageDialog(this, "Please select a valid class!", "Missing Selection", JOptionPane.WARNING_MESSAGE);
            statusLabel.setText("⚠️ Select valid class");
            return;
        }

        try {
            currentClassId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            String className = comboClass.getSelectedItem().toString().split(" - ")[1].trim();
            List<String[]> students = studentDAO.getStudentsByClass(currentClassId);
            
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No students found in this class!", "Empty Class", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("⚠️ No students found in " + className);
                return;
            }
            
            for (String[] student : students) {
                int studentId = Integer.parseInt(student[0]);
                String rollNo = student[1];
                String name = student[2];
                studentListModel.addElement(new StudentItem(studentId, rollNo, name));
            }
            statusLabel.setText(String.format("✓ Loaded %d students for %s. Select a student to view performance.", 
                students.size(), className));
            searchField.setText("");
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading students: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading students:\n" + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterStudents(String searchText) {
        if (searchText.trim().isEmpty()) {
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
        subjectSummaryModel.setRowCount(0);
        
        if (selectedStudentId == -1 || currentClassId == -1) {
            studentInfoLabel.setText("Select a student and load class first");
            return;
        }
        
        try {
            studentInfoLabel.setText("<html><b>" + selectedStudentName + "</b> (" + selectedStudentRollNo + ")</html>");
            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(currentClassId, facultyId);
            
            if (subjects.isEmpty()) {
                statusLabel.setText("⚠️ No subjects found for this class. Add subjects first.");
                JOptionPane.showMessageDialog(this, "No subjects found for this class!\nAdd subjects in Subject Management first.", "No Subjects", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            DecimalFormat pctFormat = new DecimalFormat("0.0");
            Map<Integer, SubjectSummary> subjectSummaries = new HashMap<>();
            
            for (Subject subject : subjects) {
                int subjectId = subject.getSubjectId();
                String subjectName = subject.getSubjectName();
                List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, currentClassId, subjectId);
                if (exams.isEmpty()) continue;
                
                int subjectTotalObtained = 0;
                int subjectTotalMax = 0;
                
                for (Exam exam : exams) {
                    int marks = studentDAO.getMarksByExam(selectedStudentId, exam.getExamId());
                    int maxMarks = exam.getMaxMarks();
                    int passMarks = exam.getPassMarks();
                    double examPercentage = (maxMarks > 0) ? (marks * 100.0 / maxMarks) : 0.0;
                    String pctStr = pctFormat.format(examPercentage) + "%";
                    String examResult = (marks >= passMarks) ? "Pass ✓" : "Fail ✗";
                    
                    examTableModel.addRow(new Object[]{
                        subjectName, exam.getExamName(), marks, maxMarks, passMarks, pctStr, examResult
                    });
                    
                    subjectTotalObtained += marks;
                    subjectTotalMax += maxMarks;
                }
                
                double subjectPercentage = (subjectTotalMax > 0) ? (subjectTotalObtained * 100.0 / subjectTotalMax) : 0.0;
                String subjectPctStr = pctFormat.format(subjectPercentage) + "%";
                String subjectResult = (subjectPercentage >= 40.0) ? "Pass" : "Fail";
                
                subjectSummaries.put(subjectId, new SubjectSummary(
                    subjectName, subjectTotalObtained, subjectTotalMax, subjectPercentage, subjectResult
                ));
            }
            
            List<SubjectSummary> summaries = new ArrayList<>(subjectSummaries.values());
            summaries.sort(Comparator.comparingDouble(SubjectSummary::getPercentage).reversed());
            
            for (SubjectSummary summary : summaries) {
                subjectSummaryModel.addRow(new Object[]{
                    summary.getSubjectName(),
                    summary.getTotalObtained(),
                    summary.getTotalMax(),
                    String.format("%.1f%%", summary.getPercentage()),
                    summary.getResult()
                });
            }
            
            statusLabel.setText(String.format("✓ Showing performance for %s (%s) across %d subjects", 
                selectedStudentName, selectedStudentRollNo, summaries.size()));
            
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading performance: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading performance:\n" + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private static class SubjectSummary {
        private String subjectName;
        private int totalObtained;
        private int totalMax;
        private double percentage;
        private String result;
        
        public SubjectSummary(String subjectName, int totalObtained, int totalMax, double percentage, String result) {
            this.subjectName = subjectName;
            this.totalObtained = totalObtained;
            this.totalMax = totalMax;
            this.percentage = percentage;
            this.result = result;
        }
        public String getSubjectName() { return subjectName; }
        public int getTotalObtained() { return totalObtained; }
        public int getTotalMax() { return totalMax; }
        public double getPercentage() { return percentage; }
        public String getResult() { return result; }
    }

    private void exportStudentReport() {
        if (selectedStudentId == -1 || examTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No student performance data to export!\nSelect a student first.", "Empty Report", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Student Performance Report");
        fileChooser.setSelectedFile(new java.io.File(selectedStudentRollNo + "_full_performance_report.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile())) {
                fw.append("STUDENT PERFORMANCE REPORT\n");
                fw.append("Student: " + selectedStudentName + "\n");
                fw.append("Roll No: " + selectedStudentRollNo + "\n");
                fw.append("Class: " + comboClass.getSelectedItem().toString().split(" - ")[1].trim() + "\n");
                fw.append("Generated: " + new java.util.Date() + "\n\n");
                
                fw.append("EXAM-WISE PERFORMANCE\n");
                for (int i = 0; i < examTableModel.getColumnCount(); i++) {
                    fw.append(examTableModel.getColumnName(i));
                    if (i < examTableModel.getColumnCount() - 1) fw.append(",");
                }
                fw.append("\n");
                for (int i = 0; i < examTableModel.getRowCount(); i++) {
                    for (int j = 0; j < examTableModel.getColumnCount(); j++) {
                        fw.append(examTableModel.getValueAt(i, j).toString());
                        if (j < examTableModel.getColumnCount() - 1) fw.append(",");
                    }
                    fw.append("\n");
                }
                
                fw.append("\n\nSUBJECT-WISE SUMMARY\n");
                for (int i = 0; i < subjectSummaryModel.getColumnCount(); i++) {
                    fw.append(subjectSummaryModel.getColumnName(i));
                    if (i < subjectSummaryModel.getColumnCount() - 1) fw.append(",");
                }
                fw.append("\n");
                for (int i = 0; i < subjectSummaryModel.getRowCount(); i++) {
                    for (int j = 0; j < subjectSummaryModel.getColumnCount(); j++) {
                        fw.append(subjectSummaryModel.getValueAt(i, j).toString());
                        if (j < subjectSummaryModel.getColumnCount() - 1) fw.append(",");
                    }
                    fw.append("\n");
                }
                
                JOptionPane.showMessageDialog(this, "✅ Student performance report exported successfully!\nFile: " + fileChooser.getSelectedFile().getName(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("✓ Report exported: " + fileChooser.getSelectedFile().getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error exporting report:\n" + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
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
                new StudentPerformanceBySubjectUI(1, mockResultsUI).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Application failed to start!\nError: " + e.getMessage(), "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}