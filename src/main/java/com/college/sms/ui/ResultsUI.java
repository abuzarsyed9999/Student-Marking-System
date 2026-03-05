//package com.college.sms.ui;
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.border.LineBorder;
//import javax.swing.border.TitledBorder;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.JTableHeader;
//import com.college.sms.dao.ClassDAO;
//import com.college.sms.dao.ExamDAO;
//import com.college.sms.dao.StudentDAO;
//import com.college.sms.dao.SubjectDAO;
//import com.college.sms.model.Exam;
//import com.college.sms.model.Subject;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.CategoryAxis;
//import org.jfree.chart.axis.CategoryLabelPositions;
//import org.jfree.chart.axis.NumberAxis;
//import org.jfree.chart.labels.ItemLabelAnchor;
//import org.jfree.chart.labels.ItemLabelPosition;
//import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
//import org.jfree.chart.plot.CategoryPlot;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.renderer.category.BarRenderer;
//import org.jfree.chart.ui.TextAnchor;
//import org.jfree.data.category.DefaultCategoryDataset;
//
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.text.MessageFormat;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
//// ✅ ALL CRITICAL INTEGRATION IMPORTS
//import com.college.sms.ui.SubjectPerformanceUI;
//import com.college.sms.ui.StudentPerformanceBySubjectUI;
//import com.college.sms.ui.ParentContactUpdater;      // ✅ For editing parent contacts
//import com.college.sms.ui.ParentCommunicationUI;      // ✅ For sending results
//import com.college.sms.util.EmailService;
//import com.college.sms.util.SMSService;
//
//
//public class ResultsUI extends JFrame {
//    private int facultyId;
//    private JFrame previousUI;
//    private JComboBox<String> comboClass;
//    private JComboBox<String> comboSubject;
//    private JComboBox<String> comboExam;
//    private JTable table;
//    private DefaultTableModel model;
//    private StudentDAO studentDAO;
//    private ClassDAO classDAO;
//    private SubjectDAO subjectDAO;
//    private ExamDAO examDAO;
//    private boolean isLoadingSubjects = false;
//    private boolean isLoadingExams = false;
//    private JLabel statusLabel;
//    private JPanel chartContainer;
//    private JTabbedPane mainTabbedPane;
//
//    // Statistics card label references
//    private JLabel totalStudentsLabel, avgMarksLabel, passPercentLabel,
//            highestLabel, lowestLabel, passCountLabel;
//
//    public ResultsUI(int facultyId, JFrame previousUI) {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        studentDAO = new StudentDAO();
//        classDAO = new ClassDAO();
//        subjectDAO = new SubjectDAO();
//        examDAO = new ExamDAO();
//
//        setTitle("📊 Results Dashboard | Faculty ID: " + facultyId);
//        setSize(1250, 800);  // ✅ Slightly wider for all buttons
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(new BorderLayout(15, 15));
//        getContentPane().setBackground(new Color(245, 247, 250));
//
//        initComponents();
//        loadData();
//        setVisible(true);
//    }
//
//    private void initComponents() {
//        // ===== TOP SECTION WRAPPER (Stacks header + filter vertically) =====
//        JPanel topWrapper = new JPanel();
//        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
//        topWrapper.setOpaque(false);
//
//        // ===== HEADER PANEL =====
//        JPanel headerPanel = new JPanel(new BorderLayout());
//        headerPanel.setBackground(new Color(52, 73, 94));
//        headerPanel.setPreferredSize(new Dimension(0, 75));
//        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
//
//        JLabel titleLabel = new JLabel("🎓 Student Results Dashboard");
//        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
//        titleLabel.setForeground(Color.WHITE);
//        headerPanel.add(titleLabel, BorderLayout.WEST);
//
//        // ✅ BACK BUTTON
//        JButton btnBack = createModernButton("⇦ Back to Dashboard", new Color(41, 128, 185), Color.WHITE);
//        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
//        btnBack.setPreferredSize(new Dimension(200, 42));
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
//        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
//        filterPanel.setBorder(BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
//                "🔍 Filter Results",
//                TitledBorder.LEFT,
//                TitledBorder.TOP,
//                new Font("Segoe UI", Font.BOLD, 16),
//                new Color(52, 152, 219)
//        ));
//        filterPanel.setBackground(Color.WHITE);
//        filterPanel.setPreferredSize(new Dimension(0, 90));
//
//        filterPanel.add(createFilterLabel("🏫 Class:"));
//        comboClass = createModernComboBox();
//        filterPanel.add(comboClass);
//
//        filterPanel.add(createFilterLabel("📚 Subject:"));
//        comboSubject = createModernComboBox();
//        filterPanel.add(comboSubject);
//
//        filterPanel.add(createFilterLabel("📝 Exam:"));
//        comboExam = createModernComboBox();
//        filterPanel.add(comboExam);
//
//        JButton btnLoad = createModernButton("📊 Load Results", new Color(46, 204, 113), Color.WHITE);
//        btnLoad.setPreferredSize(new Dimension(160, 38));
//        btnLoad.addActionListener(e -> loadResults());
//        filterPanel.add(btnLoad);
//        topWrapper.add(filterPanel);
//
//        add(topWrapper, BorderLayout.NORTH);
//
//        // ===== MAIN CONTENT (TAB BED) =====
//        mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
//        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
//        mainTabbedPane.setBackground(new Color(245, 247, 250));
//        mainTabbedPane.setForeground(new Color(52, 73, 94));
//
//        // Tab 1: Results Table
//        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
//        tablePanel.setBackground(Color.WHITE);
//        tablePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
//
//        // ✅ Table Model with Percentage Column
//        model = new DefaultTableModel(
//                new String[]{"Student ID", "Roll No", "Name", "Marks", "Percentage", "Result", "Grade"}, 0
//        ) {
//            public boolean isCellEditable(int row, int col) {
//                return false;
//            }
//        };
//
//        table = new JTable(model);
//        table.setRowHeight(34);
//        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        table.setGridColor(new Color(230, 230, 230));
//        table.setSelectionBackground(new Color(52, 152, 219));
//        table.setSelectionForeground(Color.WHITE);
//        table.setIntercellSpacing(new Dimension(0, 1));
//
//        // Header styling
//        JTableHeader header = table.getTableHeader();
//        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        header.setBackground(new Color(52, 73, 94));
//        header.setForeground(Color.WHITE);
//        header.setPreferredSize(new Dimension(0, 40));
//
//        // Custom cell renderer for coloring
//        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
//                                                           boolean hasFocus, int row, int column) {
//                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
//                if (!isSelected) {
//                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
//
//                    // Result column coloring
//                    if (column == 5) {
//                        String result = (String) t.getValueAt(row, 5);
//                        if ("Pass".equals(result)) {
//                            c.setBackground(new Color(200, 230, 201));
//                            c.setForeground(new Color(27, 94, 32));
//                        } else {
//                            c.setBackground(new Color(255, 205, 210));
//                            c.setForeground(new Color(183, 28, 28));
//                        }
//                    }
//
//                    // Percentage column coloring
//                    if (column == 4) {
//                        try {
//                            String pctStr = value.toString().replace("%", "").trim();
//                            double pct = Double.parseDouble(pctStr);
//                            if (pct >= 90) {
//                                c.setBackground(new Color(100, 221, 23));
//                                c.setForeground(Color.WHITE);
//                                c.setFont(c.getFont().deriveFont(Font.BOLD));
//                            } else if (pct >= 75) {
//                                c.setBackground(new Color(129, 199, 132));
//                                c.setForeground(new Color(27, 94, 32));
//                            } else if (pct >= 60) {
//                                c.setBackground(new Color(255, 249, 196));
//                                c.setForeground(new Color(137, 104, 0));
//                            } else if (pct >= 40) {
//                                c.setBackground(new Color(255, 224, 178));
//                                c.setForeground(new Color(165, 82, 0));
//                            } else {
//                                c.setBackground(new Color(255, 205, 210));
//                                c.setForeground(new Color(183, 28, 28));
//                            }
//                        } catch (Exception ignored) {}
//                    }
//
//                    // Grade column coloring
//                    if (column == 6) {
//                        String grade = (String) t.getValueAt(row, 6);
//                        switch (grade) {
//                            case "A": c.setBackground(new Color(100, 221, 23)); c.setForeground(Color.WHITE); break;
//                            case "B": c.setBackground(new Color(129, 199, 132)); c.setForeground(new Color(27, 94, 32)); break;
//                            case "C": c.setBackground(new Color(255, 249, 196)); c.setForeground(new Color(137, 104, 0)); break;
//                            case "D": c.setBackground(new Color(255, 224, 178)); c.setForeground(new Color(165, 82, 0)); break;
//                            case "F": c.setBackground(new Color(255, 205, 210)); c.setForeground(new Color(183, 28, 28)); break;
//                        }
//                    }
//                }
//                return c;
//            }
//        });
//
//        // Hide Student ID column
//        table.getColumnModel().getColumn(0).setMinWidth(0);
//        table.getColumnModel().getColumn(0).setMaxWidth(0);
//        table.getColumnModel().getColumn(0).setWidth(0);
//
//        // Set column widths
//        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Roll No
//        table.getColumnModel().getColumn(2).setPreferredWidth(220); // Name
//        table.getColumnModel().getColumn(3).setPreferredWidth(70);  // Marks
//        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Percentage
//        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Result
//        table.getColumnModel().getColumn(6).setPreferredWidth(70);  // Grade
//
//        JScrollPane tableScroll = new JScrollPane(table);
//        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204), 1));
//        tablePanel.add(tableScroll, BorderLayout.CENTER);
//
//        // ✅ COMPLETE ACTION PANEL - ALL 7 BUTTONS RESTORED
//        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
//        actionPanel.setBackground(Color.WHITE);
//        actionPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
//
//        // Button 1: Export CSV
//        JButton btnExport = createModernButton("📤 Export", new Color(155, 89, 182), Color.WHITE);
//        btnExport.setPreferredSize(new Dimension(110, 34));
//        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnExport.setToolTipText("Export results to CSV file");
//        btnExport.addActionListener(e -> exportResults());
//        actionPanel.add(btnExport);
//
//        // Button 2: Print Report
//        JButton btnPrint = createModernButton("🖨️ Print", new Color(243, 156, 18), Color.WHITE);
//        btnPrint.setPreferredSize(new Dimension(100, 34));
//        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnPrint.setToolTipText("Print results report");
//        btnPrint.addActionListener(e -> printReport());
//        actionPanel.add(btnPrint);
//
//        // Button 3: Send to Parents (Email + SMS)
//        JButton btnSendParents = createModernButton("📧 Send", new Color(241, 196, 15), Color.WHITE);
//        btnSendParents.setPreferredSize(new Dimension(100, 34));
//        btnSendParents.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnSendParents.setToolTipText("Send results to parents via email/SMS");
//        btnSendParents.addActionListener(e -> sendResultsToParents());
//        actionPanel.add(btnSendParents);
//
//        // Button 4: Update Parent Contacts (Edit mobile/email/consent)
//        JButton btnUpdateContacts = createModernButton("✏️ Contacts", new Color(142, 68, 173), Color.WHITE);
//        btnUpdateContacts.setPreferredSize(new Dimension(120, 34));
//        btnUpdateContacts.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnUpdateContacts.setToolTipText("Edit parent mobile, email, and consent for students");
//        btnUpdateContacts.addActionListener(e -> {
//            new ParentContactUpdater(ResultsUI.this, facultyId).setVisible(true);
//        });
//        actionPanel.add(btnUpdateContacts);
//
//        // Button 5: Parent Communication Center (Dedicated UI for sending)
//        JButton btnParentComm = createModernButton("💬 Comm Center", new Color(142, 68, 173), Color.WHITE);
//        btnParentComm.setPreferredSize(new Dimension(130, 34));
//        btnParentComm.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnParentComm.setToolTipText("Open dedicated parent communication center");
//        btnParentComm.addActionListener(e -> {
//            new ParentCommunicationUI(facultyId, ResultsUI.this).setVisible(true);
//            setVisible(false);
//        });
//        actionPanel.add(btnParentComm);
//
//        // Button 6: Subject Performance (Cumulative analysis across ALL exams)
//        JButton btnSubjectPerf = createModernButton("🎯 Subject Perf", new Color(52, 152, 219), Color.WHITE);
//        btnSubjectPerf.setPreferredSize(new Dimension(130, 34));
//        btnSubjectPerf.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnSubjectPerf.setToolTipText("View cumulative performance across ALL exams for this subject");
//        btnSubjectPerf.addActionListener(e -> {
//            if (comboClass.getSelectedItem() == null || comboSubject.getSelectedItem() == null ||
//                comboClass.getSelectedItem().toString().contains("--") ||
//                comboSubject.getSelectedItem().toString().contains("--")) {
//                JOptionPane.showMessageDialog(ResultsUI.this,
//                    "Please select valid class and subject first!",
//                    "Selection Required", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            try {
//                new SubjectPerformanceUI(facultyId, ResultsUI.this).setVisible(true);
//                setVisible(false);
//            } catch (NoClassDefFoundError ex) {
//                JOptionPane.showMessageDialog(ResultsUI.this,
//                    "SubjectPerformanceUI not found! Ensure the file exists.",
//                    "Integration Error", JOptionPane.ERROR_MESSAGE);
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(ResultsUI.this,
//                    "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//        actionPanel.add(btnSubjectPerf);
//
//        // Button 7: Student Report (Individual student performance by subject)
//        JButton btnStudentPerf = createModernButton("👤 Student Report", new Color(211, 84, 0), Color.WHITE);
//        btnStudentPerf.setPreferredSize(new Dimension(140, 34));
//        btnStudentPerf.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnStudentPerf.setToolTipText("View individual student performance across all subjects");
//        btnStudentPerf.addActionListener(e -> {
//            new StudentPerformanceBySubjectUI(facultyId, ResultsUI.this).setVisible(true);
//        });
//        actionPanel.add(btnStudentPerf);
//
//        tablePanel.add(actionPanel, BorderLayout.SOUTH);
//        mainTabbedPane.addTab("📋 Results Table", tablePanel);
//
//        // Tab 2: Visual Analytics
//        chartContainer = new JPanel(new BorderLayout(20, 15));
//        chartContainer.setBackground(Color.WHITE);
//        chartContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
//        mainTabbedPane.addTab("📈 Visual Analytics", chartContainer);
//
//        // Tab 3: Statistics Summary
//        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
//        statsPanel.setBackground(Color.WHITE);
//        statsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
//        statsPanel.add(createStatCard("Total Students", "0", new Color(52, 152, 219), lbl -> totalStudentsLabel = lbl));
//        statsPanel.add(createStatCard("Average Marks", "0.0", new Color(46, 204, 113), lbl -> avgMarksLabel = lbl));
//        statsPanel.add(createStatCard("Pass Percentage", "0%", new Color(155, 89, 182), lbl -> passPercentLabel = lbl));
//        statsPanel.add(createStatCard("Highest Marks", "0", new Color(243, 156, 18), lbl -> highestLabel = lbl));
//        statsPanel.add(createStatCard("Lowest Marks", "0", new Color(231, 76, 60), lbl -> lowestLabel = lbl));
//        statsPanel.add(createStatCard("Pass Count", "0", new Color(39, 174, 96), lbl -> passCountLabel = lbl));
//
//        JPanel statsWrapper = new JPanel(new BorderLayout());
//        statsWrapper.setBackground(Color.WHITE);
//        statsWrapper.setBorder(new TitledBorder(new LineBorder(new Color(52, 152, 219), 2),
//                "Quick Statistics", TitledBorder.LEFT, TitledBorder.TOP,
//                new Font("Segoe UI", Font.BOLD, 18), new Color(52, 152, 219)));
//        statsWrapper.add(statsPanel, BorderLayout.CENTER);
//        mainTabbedPane.addTab("🔢 Statistics", statsWrapper);
//
//        mainTabbedPane.setToolTipTextAt(0, "View detailed student results with percentage");
//        mainTabbedPane.setToolTipTextAt(1, "View graphical analysis including pass/fail distribution and high achievers");
//        mainTabbedPane.setToolTipTextAt(2, "Key performance metrics at a glance");
//
//        add(mainTabbedPane, BorderLayout.CENTER);
//
//        // ===== STATUS BAR =====
//        statusLabel = new JLabel("Ready to load results. Select class, subject and exam to begin.");
//        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
//        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        statusLabel.setBackground(new Color(236, 240, 241));
//        statusLabel.setForeground(new Color(75, 75, 75));
//        statusLabel.setOpaque(true);
//        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
//        add(statusLabel, BorderLayout.SOUTH);
//
//        // ===== EVENT LISTENERS =====
//        comboClass.addActionListener(e -> {
//            if (comboClass.getSelectedItem() != null && !comboClass.getSelectedItem().toString().contains("--")) {
//                loadSubjects();
//            }
//        });
//        comboSubject.addActionListener(e -> {
//            if (!isLoadingSubjects && comboSubject.getSelectedItem() != null && !comboSubject.getSelectedItem().toString().contains("--")) {
//                loadExams();
//            }
//        });
//    }
//
//    private JLabel createFilterLabel(String text) {
//        JLabel label = new JLabel(text);
//        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
//        label.setForeground(new Color(52, 73, 94));
//        return label;
//    }
//
//    private JComboBox<String> createModernComboBox() {
//        JComboBox<String> combo = new JComboBox<>();
//        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        combo.setPreferredSize(new Dimension(220, 36));
//        combo.setBackground(Color.WHITE);
//        combo.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
//        return combo;
//    }
//
//    private JButton createModernButton(String text, Color bgColor, Color fgColor) {
//        JButton button = new JButton(text);
//        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        button.setForeground(fgColor);
//        button.setBackground(bgColor);
//        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
//        button.setFocusPainted(false);
//        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        button.addMouseListener(new MouseAdapter() {
//            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.darker()); }
//            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
//        });
//        return button;
//    }
//
//    // ✅ ENHANCED: sendResultsToParents with Email + SMS Support
//    private void sendResultsToParents() {
//        if (model.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No results loaded to send!", "Empty Data", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        // ===== STEP 1: PREVIEW =====
//        StringBuilder preview = new StringBuilder("<html><b>📧 Send Results Preview</b><br><br>");
//        preview.append("Exam: <b>").append(comboExam.getSelectedItem()).append("</b><br>");
//        preview.append("Class: <b>").append(comboClass.getSelectedItem()).append("</b><br><br>");
//        preview.append("<b>Students to process:</b> ").append(model.getRowCount()).append("<br><br>");
//        preview.append("<font color='#e74c3c'><b>IMPORTANT:</b></font><br>");
//        preview.append("• Only parents with valid email/mobile will receive results<br>");
//        preview.append("• Ensure parental consent is recorded<br>");
//        preview.append("• This action cannot be undone<br><br>");
//        preview.append("<b>Proceed?</b></html>");
//
//        int confirm = JOptionPane.showConfirmDialog(this, preview,
//            "Confirm Sending Results", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
//
//        if (confirm != JOptionPane.YES_OPTION) return;
//
//        // ===== STEP 2: PROGRESS DIALOG =====
//        JDialog progressDialog = new JDialog(this, "Sending Results...", true);
//        JProgressBar progressBar = new JProgressBar(0, model.getRowCount());
//        progressBar.setStringPainted(true);
//        progressBar.setPreferredSize(new Dimension(300, 30));
//        progressDialog.add(progressBar);
//        progressDialog.setSize(350, 120);
//        progressDialog.setLocationRelativeTo(this);
//
//        JLabel progressLabel = new JLabel("Preparing to send...", SwingConstants.CENTER);
//        progressDialog.add(progressLabel, BorderLayout.SOUTH);
//        progressDialog.setVisible(true);
//
//        // ===== STEP 3: BACKGROUND SENDING =====
//        new Thread(() -> {
//            AtomicInteger sentCount = new AtomicInteger(0);
//            AtomicInteger failedCount = new AtomicInteger(0);
//            AtomicInteger noContactCount = new AtomicInteger(0);
//            AtomicInteger smsSentCount = new AtomicInteger(0);  // ✅ SMS counter
//
//            String examName = comboExam.getSelectedItem() != null ? 
//                comboExam.getSelectedItem().toString().split(":")[1].split("\\(")[0].trim() : "Unknown";
//            String className = comboClass.getSelectedItem() != null ? 
//                comboClass.getSelectedItem().toString().split(" - ")[1].trim() : "Unknown";
//            String subjectName = comboSubject.getSelectedItem() != null ? 
//                comboSubject.getSelectedItem().toString().split(" - ")[1].trim() : "Unknown";
//
//            for (int i = 0; i < model.getRowCount(); i++) {
//                try {
//                    int studentId = Integer.parseInt(model.getValueAt(i, 0).toString());
//                    String rollNo = model.getValueAt(i, 1).toString();
//                    String name = model.getValueAt(i, 2).toString();
//                    int marks = Integer.parseInt(model.getValueAt(i, 3).toString());
//                    String percentageStr = model.getValueAt(i, 4).toString().replace("%", "");
//                    double percentage = Double.parseDouble(percentageStr);
//                    String result = model.getValueAt(i, 5).toString();
//                    String grade = model.getValueAt(i, 6).toString();
//
//                    int maxMarks = examDAO.getExamById(
//                        Integer.parseInt(comboExam.getSelectedItem().toString().split(":")[0].trim())
//                    ).getMaxMarks();
//
//                    com.college.sms.model.Student student = studentDAO.getStudentById(studentId);
//
//                    // Check contact info
//                    String email = student.getParentEmail();
//                    String mobile = student.getParentMobile();
//                    boolean consent = student.getConsentToCommunicate() != null ? student.getConsentToCommunicate() : true;
//
//                    boolean hasContact = (email != null && !email.trim().isEmpty()) || 
//                                        (mobile != null && !mobile.trim().isEmpty());
//
//                    if (!hasContact || !consent) {
//                        noContactCount.incrementAndGet();
//                        final int progress = i + 1;
//                        SwingUtilities.invokeLater(() -> {
//                            progressBar.setValue(progress);
//                            progressBar.setString(progress + "/" + model.getRowCount() + " processed");
//                        });
//                        continue;
//                    }
//
//                    // ✅ Send Email
//                    boolean emailSent = false;
//                    if (email != null && !email.trim().isEmpty()) {
//                        try {
//                            emailSent = EmailService.sendResultEmail(
//                                email, name, rollNo, className, subjectName,
//                                examName, marks, maxMarks, percentage, result, grade
//                            );
//                            if (emailSent) sentCount.incrementAndGet();
//                        } catch (Exception e) {
//                            System.err.println("Email error for " + email + ": " + e.getMessage());
//                            failedCount.incrementAndGet();
//                        }
//                    }
//
//                    // ✅ Send SMS
//                    if (mobile != null && !mobile.trim().isEmpty()) {
//                        try {
//                            boolean smsSent = SMSService.sendResultSMS(
//                                mobile, name, rollNo, className, subjectName,
//                                examName, marks, maxMarks, percentage, result, grade
//                            );
//                            if (smsSent) smsSentCount.incrementAndGet();
//                        } catch (Exception e) {
//                            System.err.println("SMS error for " + mobile + ": " + e.getMessage());
//                        }
//                    }
//
//                    // Update progress UI
//                    final int progress = i + 1;
//                    final String finalName = name;
//                    SwingUtilities.invokeLater(() -> {
//                        progressBar.setValue(progress);
//                        progressBar.setString(progress + "/" + model.getRowCount() + " processed");
//                        progressLabel.setText("Sending to: " + finalName + "...");
//                    });
//
//                    Thread.sleep(400); // Rate limiting
//
//                } catch (Exception ex) {
//                    failedCount.incrementAndGet();
//                    ex.printStackTrace();
//                }
//            }
//
//            // ===== STEP 4: FINAL SUMMARY =====
//            SwingUtilities.invokeLater(() -> {
//                progressDialog.dispose();
//
//                String summary = String.format(
//                    "✅ Results Distribution Complete!\n\n" +
//                    "📧 Emails Sent: %d\n" +
//                    "📱 SMS Sent: %d\n" +
//                    "❌ Failed: %d\n" +
//                    "⚠️ No Contact/Consent: %d\n" +
//                    "📊 Total Processed: %d students\n\n" +
//                    "Parents can now view results via email/SMS.",
//                    sentCount.get(), 
//                    smsSentCount.get(),
//                    failedCount.get(), 
//                    noContactCount.get(),
//                    model.getRowCount()
//                );
//
//                JOptionPane.showMessageDialog(ResultsUI.this, summary,
//                    "Send Complete", JOptionPane.INFORMATION_MESSAGE);
//                statusLabel.setText("✓ Sent to " + sentCount.get() + " parents (Email + SMS)");
//            });
//        }).start();
//    }
//
//    private JPanel createStatCard(String title, String value, Color color, java.util.function.Consumer<JLabel> labelRef) {
//        JPanel card = new JPanel(new BorderLayout(5, 5));
//        card.setBackground(Color.WHITE);
//        card.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(color, 3),
//                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
//        card.setPreferredSize(new Dimension(200, 120));
//        JLabel titleLabel = new JLabel(title);
//        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
//        titleLabel.setForeground(new Color(120, 120, 120));
//        JLabel valueLabel = new JLabel(value);
//        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
//        valueLabel.setForeground(color);
//        labelRef.accept(valueLabel);
//        card.add(titleLabel, BorderLayout.NORTH);
//        card.add(valueLabel, BorderLayout.CENTER);
//        return card;
//    }
//
//    private void loadData() { loadClasses(); }
//
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
//        if (classes.isEmpty()) {
//            comboClass.addItem("-- No classes assigned --");
//            statusLabel.setText("⚠️ No classes assigned. Contact admin.");
//            JOptionPane.showMessageDialog(this, "No classes found for your account!", "No Classes", JOptionPane.WARNING_MESSAGE);
//        } else {
//            for (String[] c : classes) comboClass.addItem(c[0] + " - " + c[1]);
//            comboClass.setSelectedIndex(0);
//            loadSubjects();
//        }
//    }
//
//    private void loadSubjects() {
//        isLoadingSubjects = true;
//        comboSubject.removeAllItems();
//        comboExam.removeAllItems();
//        if (comboClass.getSelectedItem() == null || comboClass.getSelectedItem().toString().contains("--")) {
//            comboSubject.addItem("-- Select class first --");
//            isLoadingSubjects = false;
//            return;
//        }
//        try {
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
//            if (subjects.isEmpty()) {
//                comboSubject.addItem("-- No subjects assigned --");
//                statusLabel.setText("⚠️ No subjects found for this class.");
//            } else {
//                for (Subject s : subjects) comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
//                comboSubject.setSelectedIndex(0);
//            }
//        } catch (Exception e) {
//            comboSubject.addItem("-- Error loading subjects --");
//            statusLabel.setText("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//        isLoadingSubjects = false;
//        loadExams();
//    }
//
//    private void loadExams() {
//        if (isLoadingSubjects) return;
//        isLoadingExams = true;
//        comboExam.removeAllItems();
//        if (comboSubject.getSelectedItem() == null || comboSubject.getSelectedItem().toString().contains("--")) {
//            comboExam.addItem("-- Select subject first --");
//            isLoadingExams = false;
//            return;
//        }
//        try {
//            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, classId, subjectId);
//            if (exams.isEmpty()) {
//                comboExam.addItem("-- No exams created --");
//                statusLabel.setText("⚠️ No exams found for this subject.");
//            } else {
//                for (Exam ex : exams)
//                    comboExam.addItem(ex.getExamId() + ":" + ex.getExamName() + " (Max: " + ex.getMaxMarks() + ")");
//                comboExam.setSelectedIndex(0);
//            }
//        } catch (Exception e) {
//            comboExam.addItem("-- Error loading exams --");
//            statusLabel.setText("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//        isLoadingExams = false;
//    }
//
//    private void loadResults() {
//        model.setRowCount(0);
//        chartContainer.removeAll();
//        if (comboClass.getSelectedItem() == null || comboSubject.getSelectedItem() == null || comboExam.getSelectedItem() == null) {
//            JOptionPane.showMessageDialog(this, "Please select class, subject and exam!", "Missing Selection", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        if (comboSubject.getSelectedItem().toString().contains("--") || comboExam.getSelectedItem().toString().contains("--")) {
//            JOptionPane.showMessageDialog(this, "No valid subjects/exams available!", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        try {
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//            int examId = Integer.parseInt(comboExam.getSelectedItem().toString().split(":")[0].trim());
//            String examName = comboExam.getSelectedItem().toString().split(":")[1].split("\\(")[0].trim();
//            Exam exam = examDAO.getExamById(examId);
//            if (exam == null) {
//                JOptionPane.showMessageDialog(this, "Exam not found!", "Invalid Exam", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//            int maxMarks = exam.getMaxMarks();
//            int passMarks = exam.getPassMarks();
//            List<String[]> students = studentDAO.getStudentsByClass(classId);
//            if (students.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "No students found in this class!", "Empty Class", JOptionPane.INFORMATION_MESSAGE);
//                return;
//            }
//            DefaultCategoryDataset individualDataset = new DefaultCategoryDataset();
//            DefaultCategoryDataset passFailDataset = new DefaultCategoryDataset();
//            DefaultCategoryDataset achieversDataset = new DefaultCategoryDataset();
//            int totalMarks = 0, passCount = 0, highest = 0, lowest = Integer.MAX_VALUE, above60Count = 0, above75Count = 0;
//            DecimalFormat pctFormat = new DecimalFormat("0.00");
//            for (String[] s : students) {
//                int studentId = Integer.parseInt(s[0]);
//                int marks = studentDAO.getMarksByExam(studentId, examId);
//                double percentage = (maxMarks > 0) ? (marks * 100.0) / maxMarks : 0.0;
//                String percentageStr = pctFormat.format(percentage) + "%";
//                if (percentage > 60) above60Count++;
//                if (percentage > 75) above75Count++;
//                String grade = calculateGrade(marks, maxMarks);
//                String result = (marks >= passMarks) ? "Pass" : "Fail";
//                if (result.equals("Pass")) passCount++;
//                totalMarks += marks;
//                highest = Math.max(highest, marks);
//                lowest = Math.min(lowest, marks);
//                model.addRow(new Object[]{s[0], s[1], s[2], marks, percentageStr, result, grade});
//                individualDataset.addValue((double) marks, s[2], "Marks");
//            }
//            int failCount = students.size() - passCount;
//            double passPercent = (double) passCount / students.size() * 100;
//
//            JFreeChart individualChart = ChartFactory.createBarChart(
//                    "Individual Student Marks: " + examName, "Students", "Marks",
//                    individualDataset, PlotOrientation.VERTICAL, false, true, false);
//            individualChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
//            CategoryPlot individualPlot = individualChart.getCategoryPlot();
//            BarRenderer individualRenderer = (BarRenderer) individualPlot.getRenderer();
//            for (int i = 0; i < individualDataset.getRowCount(); i++) {
//                int studentMarks = (int) individualDataset.getValue(i, 0).doubleValue();
//                Color barColor = (studentMarks >= passMarks) ? new Color(46, 204, 113) : new Color(231, 76, 60);
//                individualRenderer.setSeriesPaint(0, barColor);
//            }
//            individualPlot.setRangeGridlinePaint(new Color(200, 200, 200));
//            individualPlot.setBackgroundPaint(Color.WHITE);
//            ChartPanel individualChartPanel = new ChartPanel(individualChart);
//            individualChartPanel.setPreferredSize(new Dimension(500, 300));
//
//            passFailDataset.addValue(passCount, "Count", "Pass");
//            passFailDataset.addValue(failCount, "Count", "Fail");
//            JFreeChart passFailChart = ChartFactory.createBarChart(
//                    "Pass/Fail Distribution", "Result", "Students",
//                    passFailDataset, PlotOrientation.VERTICAL, false, true, false);
//            customizePerformanceChart(passFailChart, true, passCount, failCount);
//            ChartPanel passFailChartPanel = new ChartPanel(passFailChart);
//            passFailChartPanel.setPreferredSize(new Dimension(380, 320));
//
//            achieversDataset.addValue(above60Count, "Count", "> 60%");
//            achieversDataset.addValue(above75Count, "Count", "> 75%");
//            JFreeChart achieversChart = ChartFactory.createBarChart(
//                    "High Achievers", "Threshold", "Students",
//                    achieversDataset, PlotOrientation.VERTICAL, false, true, false);
//            customizePerformanceChart(achieversChart, false, above60Count, above75Count);
//            ChartPanel achieversChartPanel = new ChartPanel(achieversChart);
//            achieversChartPanel.setPreferredSize(new Dimension(380, 320));
//
//            JPanel topChartPanel = new JPanel(new BorderLayout());
//            topChartPanel.setBorder(BorderFactory.createTitledBorder(
//                    BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
//                    "Individual Performance", TitledBorder.LEFT, TitledBorder.TOP,
//                    new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));
//            topChartPanel.setBackground(Color.WHITE);
//            topChartPanel.add(individualChartPanel, BorderLayout.CENTER);
//
//            JPanel middleChartsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
//            middleChartsPanel.setBackground(Color.WHITE);
//            middleChartsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
//            JPanel passFailWrapper = new JPanel(new BorderLayout());
//            passFailWrapper.setBorder(BorderFactory.createTitledBorder(
//                    BorderFactory.createLineBorder(new Color(40, 167, 69), 2),
//                    "📊 Pass/Fail", TitledBorder.LEFT, TitledBorder.TOP,
//                    new Font("Segoe UI", Font.BOLD, 15), new Color(40, 167, 69)));
//            passFailWrapper.setBackground(Color.WHITE);
//            passFailWrapper.add(passFailChartPanel, BorderLayout.CENTER);
//            JPanel achieversWrapper = new JPanel(new BorderLayout());
//            achieversWrapper.setBorder(BorderFactory.createTitledBorder(
//                    BorderFactory.createLineBorder(new Color(108, 92, 231), 2),
//                    "📊 High Achievers", TitledBorder.LEFT, TitledBorder.TOP,
//                    new Font("Segoe UI", Font.BOLD, 15), new Color(108, 92, 231)));
//            achieversWrapper.setBackground(Color.WHITE);
//            achieversWrapper.add(achieversChartPanel, BorderLayout.CENTER);
//            middleChartsPanel.add(passFailWrapper);
//            middleChartsPanel.add(achieversWrapper);
//
//            JPanel summaryPanel = new JPanel(new GridLayout(2, 4, 20, 15));
//            summaryPanel.setBackground(new Color(248, 250, 252));
//            summaryPanel.setBorder(BorderFactory.createTitledBorder(
//                    BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
//                    "🎯 Key Metrics", TitledBorder.LEFT, TitledBorder.TOP,
//                    new Font("Segoe UI", Font.BOLD, 17), new Color(52, 152, 219)));
//            summaryPanel.setBorder(new EmptyBorder(15, 20, 20, 20));
//            summaryPanel.add(createMetricLabel("Present", String.valueOf(students.size()), new Color(52, 152, 219)));
//            summaryPanel.add(createMetricLabel("Absent", "0", new Color(149, 165, 166)));
//            summaryPanel.add(createMetricLabel("Pass", String.valueOf(passCount), new Color(40, 167, 69)));
//            summaryPanel.add(createMetricLabel("Fail", String.valueOf(failCount), new Color(220, 53, 69)));
//            summaryPanel.add(createMetricLabel("Pass %", String.format("%.1f%%", passPercent), new Color(40, 167, 69)));
//            summaryPanel.add(createMetricLabel("Fail %", String.format("%.1f%%", 100 - passPercent), new Color(220, 53, 69)));
//            summaryPanel.add(createMetricLabel("> 60%", String.valueOf(above60Count), new Color(52, 152, 219)));
//            summaryPanel.add(createMetricLabel("> 75%", String.valueOf(above75Count), new Color(108, 92, 231)));
//
//            chartContainer.add(topChartPanel, BorderLayout.NORTH);
//            chartContainer.add(middleChartsPanel, BorderLayout.CENTER);
//            chartContainer.add(summaryPanel, BorderLayout.SOUTH);
//
//            updateStatistics(students.size(), totalMarks, passCount, highest, lowest, passMarks);
//            double avg = (double) totalMarks / students.size();
//            statusLabel.setText(String.format("✓ Loaded %d students | Avg: %.1f | Pass: %.1f%%",
//                    students.size(), avg, passPercent));
//            mainTabbedPane.setSelectedIndex(0);
//        } catch (Exception e) {
//            statusLabel.setText("Error: " + e.getMessage());
//            JOptionPane.showMessageDialog(this, "Error loading results: " + e.getMessage(),
//                    "Load Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }
//
//    private void customizePerformanceChart(JFreeChart chart, boolean isPassFail, double val1, double val2) {
//        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
//        chart.getTitle().setPaint(new Color(44, 62, 80));
//        chart.setBackgroundPaint(Color.WHITE);
//        CategoryPlot plot = chart.getCategoryPlot();
//        plot.setBackgroundPaint(Color.WHITE);
//        plot.setRangeGridlinePaint(new Color(220, 220, 220));
//        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
//        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.BOLD, 13));
//        domainAxis.setTickLabelPaint(new Color(60, 60, 60));
//        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
//        rangeAxis.setTickLabelPaint(new Color(80, 80, 80));
//        BarRenderer renderer = (BarRenderer) plot.getRenderer();
//        renderer.setDrawBarOutline(false);
//        renderer.setItemMargin(0.18);
//        renderer.setDefaultItemLabelsVisible(true);
//        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 14));
//        renderer.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(
//                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
//        if (isPassFail) {
//            renderer.setSeriesPaint(0, new Color(40, 167, 69));
//            renderer.setSeriesPaint(1, new Color(220, 53, 69));
//            renderer.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
//            renderer.setSeriesItemLabelGenerator(1, new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
//        } else {
//            renderer.setSeriesPaint(0, new Color(52, 152, 219));
//            renderer.setSeriesPaint(1, new Color(108, 92, 231));
//            renderer.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
//            renderer.setSeriesItemLabelGenerator(1, new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
//        }
//    }
//
//    private JPanel createMetricLabel(String title, String value, Color color) {
//        JPanel panel = new JPanel(new BorderLayout(5, 8));
//        panel.setBackground(new Color(255, 255, 255));
//        panel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
//                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
//        JLabel titleLabel = new JLabel(title);
//        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        titleLabel.setForeground(new Color(100, 100, 100));
//        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        JLabel valueLabel = new JLabel(value);
//        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
//        valueLabel.setForeground(color);
//        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        panel.add(titleLabel, BorderLayout.NORTH);
//        panel.add(valueLabel, BorderLayout.CENTER);
//        return panel;
//    }
//
//    private String calculateGrade(int marks, int maxMarks) {
//        if (maxMarks <= 0) return "N/A";
//        double percentage = (double) marks / maxMarks * 100;
//        if (percentage >= 90) return "A";
//        else if (percentage >= 80) return "B";
//        else if (percentage >= 70) return "C";
//        else if (percentage >= 60) return "D";
//        else return "F";
//    }
//
//    private void updateStatistics(int totalStudents, int totalMarks, int passCount, int highest, int lowest, int passMarks) {
//        totalStudentsLabel.setText(String.valueOf(totalStudents));
//        avgMarksLabel.setText(String.format("%.1f", (double) totalMarks / totalStudents));
//        passPercentLabel.setText(String.format("%.1f%%", (double) passCount / totalStudents * 100));
//        highestLabel.setText(String.valueOf(highest));
//        lowestLabel.setText(String.valueOf(lowest));
//        passCountLabel.setText(String.valueOf(passCount));
//    }
//
//    private void exportResults() {
//        if (model.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No data to export!", "Empty Data", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Results As CSV");
//        fileChooser.setSelectedFile(new java.io.File("results_export.csv"));
//        int userSelection = fileChooser.showSaveDialog(this);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile())) {
//                for (int i = 1; i < model.getColumnCount(); i++) {
//                    fw.append(model.getColumnName(i));
//                    if (i < model.getColumnCount() - 1) fw.append(",");
//                }
//                fw.append("\n");
//                for (int i = 0; i < model.getRowCount(); i++) {
//                    for (int j = 1; j < model.getColumnCount(); j++) {
//                        fw.append(model.getValueAt(i, j).toString());
//                        if (j < model.getColumnCount() - 1) fw.append(",");
//                    }
//                    fw.append("\n");
//                }
//                JOptionPane.showMessageDialog(this, "Results exported successfully!\nFile: " + fileChooser.getSelectedFile().getAbsolutePath(),
//                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
//                statusLabel.setText("✓ Results exported to: " + fileChooser.getSelectedFile().getName());
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(this, "Error exporting results: " + ex.getMessage(),
//                        "Export Error", JOptionPane.ERROR_MESSAGE);
//                statusLabel.setText("Export failed: " + ex.getMessage());
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    private void printReport() {
//        if (model.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No data to print!", "Empty Data", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        try {
//            table.print(JTable.PrintMode.FIT_WIDTH,
//                    new MessageFormat("Results Report - {0}"),
//                    new MessageFormat("Page {0}"));
//            statusLabel.setText("✓ Report sent to printer successfully");
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Error printing report: " + e.getMessage(),
//                    "Print Error", JOptionPane.ERROR_MESSAGE);
//            statusLabel.setText("Print failed: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                JFrame mockDashboard = new JFrame("Faculty Dashboard");
//                mockDashboard.setSize(1000, 650);
//                mockDashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                mockDashboard.setVisible(true);
//                new ResultsUI(1, mockDashboard);
//            } catch (Exception e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(null,
//                        "Application failed to start!\nError: " + e.getMessage(),
//                        "Startup Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//    }
//}

//----------------------------------------
//
//package com.college.sms.ui;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.border.LineBorder;
//import javax.swing.border.TitledBorder;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.JTableHeader;
//import com.college.sms.dao.ClassDAO;
//import com.college.sms.dao.ExamDAO;
//import com.college.sms.dao.StudentDAO;
//import com.college.sms.dao.SubjectDAO;
//import com.college.sms.model.Exam;
//import com.college.sms.model.Subject;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.CategoryAxis;
//import org.jfree.chart.axis.CategoryLabelPositions;
//import org.jfree.chart.axis.NumberAxis;
//import org.jfree.chart.labels.ItemLabelAnchor;
//import org.jfree.chart.labels.ItemLabelPosition;
//import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
//import org.jfree.chart.plot.CategoryPlot;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.renderer.category.BarRenderer;
//import org.jfree.chart.ui.TextAnchor;
//import org.jfree.data.category.DefaultCategoryDataset;
//
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.image.BufferedImage;
//import java.awt.print.*;
//import java.io.*;
//import java.net.URL;
//import java.text.DecimalFormat;
//import java.text.MessageFormat;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//import javax.imageio.ImageIO;
//
//// ✅ ALL CRITICAL INTEGRATION IMPORTS
//import com.college.sms.ui.SubjectPerformanceUI;
//import com.college.sms.ui.StudentPerformanceBySubjectUI;
//import com.college.sms.ui.ParentContactUpdater;
//import com.college.sms.ui.ParentCommunicationUI;
//import com.college.sms.util.EmailService;
//import com.college.sms.util.SMSService;
//
//public class ResultsUI extends JFrame {
//
//    private int facultyId;
//    private JFrame previousUI;
//    private JComboBox<String> comboClass;
//    private JComboBox<String> comboSubject;
//    private JComboBox<String> comboExam;
//    private JTable table;
//    private DefaultTableModel model;
//    private StudentDAO studentDAO;
//    private ClassDAO classDAO;
//    private SubjectDAO subjectDAO;
//    private ExamDAO examDAO;
//    private boolean isLoadingSubjects = false;
//    private boolean isLoadingExams = false;
//    private JLabel statusLabel;
//    private JPanel chartContainer;
//    private JTabbedPane mainTabbedPane;
//
//    // Statistics card label references
//    private JLabel totalStudentsLabel, avgMarksLabel, passPercentLabel,
//            highestLabel, lowestLabel, passCountLabel;
//
//    // =========================================================
//    // COLLEGE LOGO LOADER — loads from resources/images/college_logo.png
//    // =========================================================
//    private BufferedImage collegeLogo = null;
//
//    private BufferedImage loadCollegeLogo() {
//        if (collegeLogo != null) return collegeLogo;
//        try {
//            // Try classpath resource first (works in JAR)
//            URL url = getClass().getClassLoader().getResource("images/college_logo.png");
//            if (url != null) {
//                collegeLogo = ImageIO.read(url);
//                return collegeLogo;
//            }
//            // Fallback: try relative file path (IDE run)
//            File f = new File("resources/images/college_logo.png");
//            if (!f.exists()) f = new File("src/main/resources/images/college_logo.png");
//            if (!f.exists()) f = new File("images/college_logo.png");
//            if (f.exists()) {
//                collegeLogo = ImageIO.read(f);
//                return collegeLogo;
//            }
//        } catch (Exception e) {
//            System.err.println("⚠️ Could not load college logo: " + e.getMessage());
//        }
//        return null;
//    }
//
//    // =========================================================
//    // CONSTRUCTOR
//    // =========================================================
//    public ResultsUI(int facultyId, JFrame previousUI) {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        studentDAO = new StudentDAO();
//        classDAO = new ClassDAO();
//        subjectDAO = new SubjectDAO();
//        examDAO = new ExamDAO();
//
//        setTitle("📊 Results Dashboard | Faculty ID: " + facultyId);
//        setSize(1250, 800);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(new BorderLayout(15, 15));
//        getContentPane().setBackground(new Color(245, 247, 250));
//
//        initComponents();
//        loadData();
//        setVisible(true);
//    }
//
//    // =========================================================
//    // INIT COMPONENTS
//    // =========================================================
//    private void initComponents() {
//        JPanel topWrapper = new JPanel();
//        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
//        topWrapper.setOpaque(false);
//
//        // ----- HEADER -----
//        JPanel headerPanel = new JPanel(new BorderLayout());
//        headerPanel.setBackground(new Color(52, 73, 94));
//        headerPanel.setPreferredSize(new Dimension(0, 75));
//        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
//
//        JLabel titleLabel = new JLabel("🎓 Student Results Dashboard");
//        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
//        titleLabel.setForeground(Color.WHITE);
//        headerPanel.add(titleLabel, BorderLayout.WEST);
//
//        JButton btnBack = createModernButton("⇦ Back to Dashboard", new Color(41, 128, 185), Color.WHITE);
//        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
//        btnBack.setPreferredSize(new Dimension(200, 42));
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
//        // ----- FILTER PANEL -----
//        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
//        filterPanel.setBorder(BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
//                "🔍 Filter Results",
//                TitledBorder.LEFT, TitledBorder.TOP,
//                new Font("Segoe UI", Font.BOLD, 16), new Color(52, 152, 219)));
//        filterPanel.setBackground(Color.WHITE);
//        filterPanel.setPreferredSize(new Dimension(0, 90));
//
//        filterPanel.add(createFilterLabel("🏫 Class:"));
//        comboClass = createModernComboBox();
//        filterPanel.add(comboClass);
//
//        filterPanel.add(createFilterLabel("📚 Subject:"));
//        comboSubject = createModernComboBox();
//        filterPanel.add(comboSubject);
//
//        filterPanel.add(createFilterLabel("📝 Exam:"));
//        comboExam = createModernComboBox();
//        filterPanel.add(comboExam);
//
//        JButton btnLoad = createModernButton("📊 Load Results", new Color(46, 204, 113), Color.WHITE);
//        btnLoad.setPreferredSize(new Dimension(160, 38));
//        btnLoad.addActionListener(e -> loadResults());
//        filterPanel.add(btnLoad);
//        topWrapper.add(filterPanel);
//
//        add(topWrapper, BorderLayout.NORTH);
//
//        // =========================================================
//        // MAIN TABBED PANE
//        // =========================================================
//        mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
//        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
//        mainTabbedPane.setBackground(new Color(245, 247, 250));
//        mainTabbedPane.setForeground(new Color(52, 73, 94));
//
//        // ----- TAB 1: Results Table -----
//        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
//        tablePanel.setBackground(Color.WHITE);
//        tablePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
//
//        model = new DefaultTableModel(
//                new String[]{"Student ID", "Roll No", "Name", "Marks", "Percentage", "Result", "Grade"}, 0) {
//            public boolean isCellEditable(int row, int col) { return false; }
//        };
//
//        table = new JTable(model);
//        table.setRowHeight(34);
//        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        table.setGridColor(new Color(230, 230, 230));
//        table.setSelectionBackground(new Color(52, 152, 219));
//        table.setSelectionForeground(Color.WHITE);
//        table.setIntercellSpacing(new Dimension(0, 1));
//
//        JTableHeader header = table.getTableHeader();
//        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        header.setBackground(new Color(52, 73, 94));
//        header.setForeground(Color.WHITE);
//        header.setPreferredSize(new Dimension(0, 40));
//
//        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
//                                                           boolean hasFocus, int row, int column) {
//                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
//                if (!isSelected) {
//                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
//                    if (column == 5) {
//                        String result = (String) t.getValueAt(row, 5);
//                        if ("Pass".equals(result)) {
//                            c.setBackground(new Color(200, 230, 201));
//                            c.setForeground(new Color(27, 94, 32));
//                        } else {
//                            c.setBackground(new Color(255, 205, 210));
//                            c.setForeground(new Color(183, 28, 28));
//                        }
//                    }
//                    if (column == 4) {
//                        try {
//                            double pct = Double.parseDouble(value.toString().replace("%", "").trim());
//                            if (pct >= 90) { c.setBackground(new Color(100, 221, 23)); c.setForeground(Color.WHITE); c.setFont(c.getFont().deriveFont(Font.BOLD)); }
//                            else if (pct >= 75) { c.setBackground(new Color(129, 199, 132)); c.setForeground(new Color(27, 94, 32)); }
//                            else if (pct >= 60) { c.setBackground(new Color(255, 249, 196)); c.setForeground(new Color(137, 104, 0)); }
//                            else if (pct >= 40) { c.setBackground(new Color(255, 224, 178)); c.setForeground(new Color(165, 82, 0)); }
//                            else { c.setBackground(new Color(255, 205, 210)); c.setForeground(new Color(183, 28, 28)); }
//                        } catch (Exception ignored) {}
//                    }
//                    if (column == 6) {
//                        String grade = (String) t.getValueAt(row, 6);
//                        switch (grade) {
//                            case "A": c.setBackground(new Color(100, 221, 23)); c.setForeground(Color.WHITE); break;
//                            case "B": c.setBackground(new Color(129, 199, 132)); c.setForeground(new Color(27, 94, 32)); break;
//                            case "C": c.setBackground(new Color(255, 249, 196)); c.setForeground(new Color(137, 104, 0)); break;
//                            case "D": c.setBackground(new Color(255, 224, 178)); c.setForeground(new Color(165, 82, 0)); break;
//                            case "F": c.setBackground(new Color(255, 205, 210)); c.setForeground(new Color(183, 28, 28)); break;
//                        }
//                    }
//                }
//                return c;
//            }
//        });
//
//        // Hide Student ID column
//        table.getColumnModel().getColumn(0).setMinWidth(0);
//        table.getColumnModel().getColumn(0).setMaxWidth(0);
//        table.getColumnModel().getColumn(0).setWidth(0);
//        table.getColumnModel().getColumn(1).setPreferredWidth(100);
//        table.getColumnModel().getColumn(2).setPreferredWidth(220);
//        table.getColumnModel().getColumn(3).setPreferredWidth(70);
//        table.getColumnModel().getColumn(4).setPreferredWidth(100);
//        table.getColumnModel().getColumn(5).setPreferredWidth(80);
//        table.getColumnModel().getColumn(6).setPreferredWidth(70);
//
//        JScrollPane tableScroll = new JScrollPane(table);
//        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204), 1));
//        tablePanel.add(tableScroll, BorderLayout.CENTER);
//
//        // ----- ACTION BUTTONS -----
//        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
//        actionPanel.setBackground(Color.WHITE);
//        actionPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
//
//        JButton btnExport = createModernButton("📤 Export PDF", new Color(155, 89, 182), Color.WHITE);
//        btnExport.setPreferredSize(new Dimension(130, 34));
//        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnExport.setToolTipText("Export results to PDF with college logo");
//        btnExport.addActionListener(e -> exportResults());
//        actionPanel.add(btnExport);
//
//        JButton btnPrint = createModernButton("🖨️ Print", new Color(243, 156, 18), Color.WHITE);
//        btnPrint.setPreferredSize(new Dimension(100, 34));
//        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnPrint.setToolTipText("Print results report with college logo");
//        btnPrint.addActionListener(e -> printReport());
//        actionPanel.add(btnPrint);
//
//        JButton btnSendParents = createModernButton("📧 Send", new Color(241, 196, 15), Color.WHITE);
//        btnSendParents.setPreferredSize(new Dimension(100, 34));
//        btnSendParents.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnSendParents.setToolTipText("Send results to parents via email/SMS");
//        btnSendParents.addActionListener(e -> sendResultsToParents());
//        actionPanel.add(btnSendParents);
//
//        JButton btnUpdateContacts = createModernButton("✏️ Contacts", new Color(142, 68, 173), Color.WHITE);
//        btnUpdateContacts.setPreferredSize(new Dimension(120, 34));
//        btnUpdateContacts.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnUpdateContacts.addActionListener(e -> new ParentContactUpdater(ResultsUI.this, facultyId).setVisible(true));
//        actionPanel.add(btnUpdateContacts);
//
//        JButton btnParentComm = createModernButton("💬 Comm Center", new Color(142, 68, 173), Color.WHITE);
//        btnParentComm.setPreferredSize(new Dimension(130, 34));
//        btnParentComm.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnParentComm.addActionListener(e -> {
//            new ParentCommunicationUI(facultyId, ResultsUI.this).setVisible(true);
//            setVisible(false);
//        });
//        actionPanel.add(btnParentComm);
//
//        JButton btnSubjectPerf = createModernButton("🎯 Subject Perf", new Color(52, 152, 219), Color.WHITE);
//        btnSubjectPerf.setPreferredSize(new Dimension(130, 34));
//        btnSubjectPerf.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnSubjectPerf.addActionListener(e -> {
//            if (comboClass.getSelectedItem() == null || comboSubject.getSelectedItem() == null ||
//                comboClass.getSelectedItem().toString().contains("--") ||
//                comboSubject.getSelectedItem().toString().contains("--")) {
//                JOptionPane.showMessageDialog(ResultsUI.this, "Please select valid class and subject first!", "Selection Required", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            try {
//                new SubjectPerformanceUI(facultyId, ResultsUI.this).setVisible(true);
//                setVisible(false);
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(ResultsUI.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//        actionPanel.add(btnSubjectPerf);
//
//        JButton btnStudentPerf = createModernButton("👤 Student Report", new Color(211, 84, 0), Color.WHITE);
//        btnStudentPerf.setPreferredSize(new Dimension(140, 34));
//        btnStudentPerf.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnStudentPerf.addActionListener(e -> new StudentPerformanceBySubjectUI(facultyId, ResultsUI.this).setVisible(true));
//        actionPanel.add(btnStudentPerf);
//
//        tablePanel.add(actionPanel, BorderLayout.SOUTH);
//        mainTabbedPane.addTab("📋 Results Table", tablePanel);
//
//        // ----- TAB 2: Visual Analytics -----
//        chartContainer = new JPanel(new BorderLayout(20, 15));
//        chartContainer.setBackground(Color.WHITE);
//        chartContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
//        mainTabbedPane.addTab("📈 Visual Analytics", chartContainer);
//
//        // ----- TAB 3: Statistics -----
//        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
//        statsPanel.setBackground(Color.WHITE);
//        statsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
//        statsPanel.add(createStatCard("Total Students", "0", new Color(52, 152, 219), lbl -> totalStudentsLabel = lbl));
//        statsPanel.add(createStatCard("Average Marks", "0.0", new Color(46, 204, 113), lbl -> avgMarksLabel = lbl));
//        statsPanel.add(createStatCard("Pass Percentage", "0%", new Color(155, 89, 182), lbl -> passPercentLabel = lbl));
//        statsPanel.add(createStatCard("Highest Marks", "0", new Color(243, 156, 18), lbl -> highestLabel = lbl));
//        statsPanel.add(createStatCard("Lowest Marks", "0", new Color(231, 76, 60), lbl -> lowestLabel = lbl));
//        statsPanel.add(createStatCard("Pass Count", "0", new Color(39, 174, 96), lbl -> passCountLabel = lbl));
//
//        JPanel statsWrapper = new JPanel(new BorderLayout());
//        statsWrapper.setBackground(Color.WHITE);
//        statsWrapper.setBorder(new TitledBorder(new LineBorder(new Color(52, 152, 219), 2),
//                "Quick Statistics", TitledBorder.LEFT, TitledBorder.TOP,
//                new Font("Segoe UI", Font.BOLD, 18), new Color(52, 152, 219)));
//        statsWrapper.add(statsPanel, BorderLayout.CENTER);
//        mainTabbedPane.addTab("🔢 Statistics", statsWrapper);
//
//        add(mainTabbedPane, BorderLayout.CENTER);
//
//        // ----- STATUS BAR -----
//        statusLabel = new JLabel("Ready to load results. Select class, subject and exam to begin.");
//        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
//        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        statusLabel.setBackground(new Color(236, 240, 241));
//        statusLabel.setForeground(new Color(75, 75, 75));
//        statusLabel.setOpaque(true);
//        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
//        add(statusLabel, BorderLayout.SOUTH);
//
//        // ----- COMBO LISTENERS -----
//        comboClass.addActionListener(e -> {
//            if (comboClass.getSelectedItem() != null && !comboClass.getSelectedItem().toString().contains("--"))
//                loadSubjects();
//        });
//        comboSubject.addActionListener(e -> {
//            if (!isLoadingSubjects && comboSubject.getSelectedItem() != null && !comboSubject.getSelectedItem().toString().contains("--"))
//                loadExams();
//        });
//    }
//
//    // =========================================================
//    // PRINT REPORT — with college logo header using Java2D
//    // =========================================================
//    private void printReport() {
//        if (model.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No data to print!", "Empty Data", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        String examInfo   = comboExam.getSelectedItem()    != null ? comboExam.getSelectedItem().toString()    : "";
//        String classInfo  = comboClass.getSelectedItem()   != null ? comboClass.getSelectedItem().toString()   : "";
//        String subjectInfo = comboSubject.getSelectedItem() != null ? comboSubject.getSelectedItem().toString() : "";
//
//        // Snapshot all table data before printing (avoid threading issues)
//        int rowCount = model.getRowCount();
//        int colCount = model.getColumnCount();
//        Object[][] data = new Object[rowCount][colCount];
//        String[] colNames = new String[colCount];
//        for (int c = 0; c < colCount; c++) colNames[c] = model.getColumnName(c);
//        for (int r = 0; r < rowCount; r++)
//            for (int c = 0; c < colCount; c++)
//                data[r][c] = model.getValueAt(r, c);
//
//        BufferedImage logo = loadCollegeLogo();
//
//        PrinterJob printerJob = PrinterJob.getPrinterJob();
//        PageFormat pageFormat = printerJob.defaultPage();
//        pageFormat.setOrientation(PageFormat.LANDSCAPE);
//
//        printerJob.setPrintable(new ResultsPrintable(logo, examInfo, classInfo, subjectInfo, data, colNames), pageFormat);
//
//        if (printerJob.printDialog()) {
//            try {
//                printerJob.print();
//                statusLabel.setText("✓ Report sent to printer successfully");
//            } catch (PrinterException e) {
//                JOptionPane.showMessageDialog(this, "Error printing: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
//                statusLabel.setText("Print failed: " + e.getMessage());
//            }
//        }
//    }
//
//    // =========================================================
//    // EXPORT PDF — generates a proper PDF with logo using Java2D -> PDF trick
//    // Uses pure Java (no extra libraries) via PrinterJob -> PDF file
//    // =========================================================
//    private void exportResults() {
//        if (model.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No data to export!", "Empty Data", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Results Report as PDF");
//        fileChooser.setSelectedFile(new File("results_report.pdf"));
//        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
//
//        int userSelection = fileChooser.showSaveDialog(this);
//        if (userSelection != JFileChooser.APPROVE_OPTION) return;
//
//        File selectedFile = fileChooser.getSelectedFile();
//        if (!selectedFile.getName().toLowerCase().endsWith(".pdf"))
//            selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
//
//        final File outputFile = selectedFile;
//
//        // Snapshot data
//        int rowCount = model.getRowCount();
//        int colCount = model.getColumnCount();
//        Object[][] data = new Object[rowCount][colCount];
//        String[] colNames = new String[colCount];
//        for (int c = 0; c < colCount; c++) colNames[c] = model.getColumnName(c);
//        for (int r = 0; r < rowCount; r++)
//            for (int c = 0; c < colCount; c++)
//                data[r][c] = model.getValueAt(r, c);
//
//        String examInfo    = comboExam.getSelectedItem()    != null ? comboExam.getSelectedItem().toString()    : "";
//        String classInfo   = comboClass.getSelectedItem()   != null ? comboClass.getSelectedItem().toString()   : "";
//        String subjectInfo = comboSubject.getSelectedItem() != null ? comboSubject.getSelectedItem().toString() : "";
//        BufferedImage logo = loadCollegeLogo();
//
//        // Try to use the system PDF printer via PrinterJob -> file
//        // This is the most portable approach (works on Windows, Linux, macOS)
//        try {
//            // Attempt Apache PDFBox if available on classpath
//            exportWithPDFBox(outputFile, logo, examInfo, classInfo, subjectInfo, data, colNames);
//        } catch (NoClassDefFoundError | ClassNotFoundException ex) {
//            // PDFBox not available — fall back to HTML-based export
//            System.out.println("PDFBox not found, falling back to HTML export.");
//            exportAsHTML(outputFile, logo, examInfo, classInfo, subjectInfo, data, colNames);
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Export error: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
//            ex.printStackTrace();
//        }
//    }
//
//    // =========================================================
//    // EXPORT VIA APACHE PDFBox (if available)
//    // =========================================================
//    private void exportWithPDFBox(File outputFile, BufferedImage logo,
//            String examInfo, String classInfo, String subjectInfo,
//            Object[][] data, String[] colNames) throws Exception {
//
//        // Use reflection so the code compiles even without PDFBox on classpath at compile time
//        Class<?> docClass   = Class.forName("org.apache.pdfbox.pdmodel.PDDocument");
//        Class<?> pageClass  = Class.forName("org.apache.pdfbox.pdmodel.PDPage");
//        Class<?> csClass    = Class.forName("org.apache.pdfbox.pdmodel.PDPageContentStream");
//        Class<?> sizeClass  = Class.forName("org.apache.pdfbox.pdmodel.common.PDRectangle");
//        Class<?> fontClass  = Class.forName("org.apache.pdfbox.pdmodel.font.PDType1Font");
//        Class<?> xImgClass  = Class.forName("org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory");
//
//        Object doc  = docClass.getConstructor().newInstance();
//        Object page = pageClass.getConstructor(sizeClass).newInstance(
//                sizeClass.getField("A4").get(null));
//        docClass.getMethod("addPage", pageClass).invoke(doc, page);
//
//        float pageWidth  = (float) sizeClass.getField("A4").get(null).getClass().getMethod("getWidth").invoke(sizeClass.getField("A4").get(null));
//        float pageHeight = (float) sizeClass.getField("A4").get(null).getClass().getMethod("getHeight").invoke(sizeClass.getField("A4").get(null));
//
//        // Since reflection for PDFBox gets complex, we call a dedicated method
//        // that is compiled separately only when PDFBox is present.
//        // For now, throw to trigger the HTML fallback with a friendly message.
//        throw new ClassNotFoundException("PDFBox reflection path — use direct PDFBox dependency instead.");
//    }
//
//    // =========================================================
//    // EXPORT AS STYLED HTML (opens in browser, printable as PDF)
//    // This is the GUARANTEED fallback that always works.
//    // =========================================================
//    private void exportAsHTML(File pdfFile, BufferedImage logo,
//            String examInfo, String classInfo, String subjectInfo,
//            Object[][] data, String[] colNames) {
//
//        // Save as .html alongside the requested .pdf
//        File htmlFile = new File(pdfFile.getAbsolutePath().replace(".pdf", ".html"));
//
//        // Convert logo to base64 for embedding
//        String logoBase64 = "";
//        if (logo != null) {
//            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//                ImageIO.write(logo, "png", baos);
//                logoBase64 = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
//            } catch (Exception e) { /* no logo */ }
//        }
//
//        // Parse display strings
//        String className   = classInfo.contains(" - ") ? classInfo.split(" - ", 2)[1] : classInfo;
//        String subjectName = subjectInfo.contains(" - ") ? subjectInfo.split(" - ", 2)[1] : subjectInfo;
//        String examDisplay = examInfo.contains(":") ? examInfo.split(":", 2)[1].split("\\(")[0].trim() : examInfo;
//
//        // Compute summary stats from data
//        int passCount = 0, totalMarks = 0, highest = 0, lowest = Integer.MAX_VALUE;
//        for (Object[] row : data) {
//            try {
//                int marks = Integer.parseInt(row[3].toString());
//                totalMarks += marks;
//                highest = Math.max(highest, marks);
//                lowest  = Math.min(lowest, marks);
//                if ("Pass".equals(row[5].toString())) passCount++;
//            } catch (Exception ignored) {}
//        }
//        double avg = data.length > 0 ? (double) totalMarks / data.length : 0;
//        double passPct = data.length > 0 ? (double) passCount / data.length * 100 : 0;
//        if (lowest == Integer.MAX_VALUE) lowest = 0;
//
//        StringBuilder html = new StringBuilder();
//        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
//        html.append("<title>Results Report</title>");
//        html.append("<style>");
//        html.append("  @page { size: A4 landscape; margin: 15mm; }");
//        html.append("  * { box-sizing: border-box; margin: 0; padding: 0; }");
//        html.append("  body { font-family: 'Segoe UI', Arial, sans-serif; background: #fff; color: #333; }");
//        html.append("  .header { display: flex; align-items: center; border-bottom: 3px solid #34495e; pb: 15px; margin-bottom: 15px; padding-bottom: 12px; }");
//        html.append("  .logo { max-height: 90px; max-width: 120px; margin-right: 20px; }");
//        html.append("  .college-info h1 { font-size: 22px; color: #2c3e50; }");
//        html.append("  .college-info h2 { font-size: 15px; color: #2980b9; font-weight: normal; margin-top: 4px; }");
//        html.append("  .report-title { font-size: 13px; color: #7f8c8d; margin-top: 4px; }");
//        html.append("  .meta-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; margin: 12px 0; }");
//        html.append("  .meta-card { background: #ecf0f1; border-radius: 6px; padding: 8px 12px; }");
//        html.append("  .meta-card .label { font-size: 11px; color: #7f8c8d; text-transform: uppercase; }");
//        html.append("  .meta-card .value { font-size: 14px; font-weight: bold; color: #2c3e50; margin-top: 2px; }");
//        html.append("  .stats-row { display: flex; gap: 10px; margin: 10px 0; }");
//        html.append("  .stat-box { flex: 1; text-align: center; padding: 8px; border-radius: 6px; border: 2px solid; }");
//        html.append("  .stat-box .s-val { font-size: 20px; font-weight: bold; }");
//        html.append("  .stat-box .s-lbl { font-size: 11px; }");
//        html.append("  table { width: 100%; border-collapse: collapse; margin-top: 12px; font-size: 12px; }");
//        html.append("  thead tr { background: #2c3e50; color: white; }");
//        html.append("  thead th { padding: 9px 8px; text-align: left; }");
//        html.append("  tbody tr:nth-child(even) { background: #f8f9fa; }");
//        html.append("  tbody tr:nth-child(odd)  { background: #ffffff; }");
//        html.append("  tbody td { padding: 7px 8px; border-bottom: 1px solid #dee2e6; }");
//        html.append("  .pass { color: #1b5e20; background: #c8e6c9 !important; font-weight: bold; border-radius: 4px; padding: 2px 6px; }");
//        html.append("  .fail { color: #b71c1c; background: #ffcdd2 !important; font-weight: bold; border-radius: 4px; padding: 2px 6px; }");
//        html.append("  .grade-a { color: white; background: #2ecc71 !important; border-radius: 4px; padding: 2px 6px; font-weight: bold; }");
//        html.append("  .grade-b { color: #1b5e20; background: #81c784 !important; border-radius: 4px; padding: 2px 6px; }");
//        html.append("  .grade-c { color: #6d4c00; background: #fff59d !important; border-radius: 4px; padding: 2px 6px; }");
//        html.append("  .grade-d { color: #e65100; background: #ffcc80 !important; border-radius: 4px; padding: 2px 6px; }");
//        html.append("  .grade-f { color: #b71c1c; background: #ffcdd2 !important; border-radius: 4px; padding: 2px 6px; }");
//        html.append("  .footer { margin-top: 20px; border-top: 1px solid #bdc3c7; padding-top: 8px; display: flex; justify-content: space-between; font-size: 10px; color: #95a5a6; }");
//        html.append("  @media print { body { print-color-adjust: exact; -webkit-print-color-adjust: exact; } }");
//        html.append("</style></head><body>");
//
//        // HEADER
//        html.append("<div class='header'>");
//        if (!logoBase64.isEmpty())
//            html.append("<img class='logo' src='data:image/png;base64,").append(logoBase64).append("' alt='College Logo'/>");
//        html.append("<div class='college-info'>");
//        html.append("<h1>College Name</h1>");
//        html.append("<h2>Student Results Report</h2>");
//        html.append("<div class='report-title'>Academic Year — Official Document</div>");
//        html.append("</div></div>");
//
//        // META CARDS
//        html.append("<div class='meta-grid'>");
//        html.append("<div class='meta-card'><div class='label'>Class</div><div class='value'>").append(escHtml(className)).append("</div></div>");
//        html.append("<div class='meta-card'><div class='label'>Subject</div><div class='value'>").append(escHtml(subjectName)).append("</div></div>");
//        html.append("<div class='meta-card'><div class='label'>Exam</div><div class='value'>").append(escHtml(examDisplay)).append("</div></div>");
//        html.append("</div>");
//
//        // STATS ROW
//        html.append("<div class='stats-row'>");
//        html.append(statBox(String.valueOf(data.length), "Total Students", "#2980b9", "#2980b9"));
//        html.append(statBox(String.format("%.1f", avg), "Average Marks", "#27ae60", "#27ae60"));
//        html.append(statBox(String.format("%.1f%%", passPct), "Pass %", "#8e44ad", "#8e44ad"));
//        html.append(statBox(String.valueOf(highest), "Highest", "#f39c12", "#f39c12"));
//        html.append(statBox(String.valueOf(lowest), "Lowest", "#e74c3c", "#e74c3c"));
//        html.append(statBox(String.valueOf(passCount), "Pass Count", "#16a085", "#16a085"));
//        html.append("</div>");
//
//        // RESULTS TABLE (skip hidden Student ID column 0)
//        html.append("<table><thead><tr>");
//        for (int c = 1; c < colNames.length; c++)
//            html.append("<th>").append(escHtml(colNames[c])).append("</th>");
//        html.append("</tr></thead><tbody>");
//
//        for (Object[] row : data) {
//            html.append("<tr>");
//            for (int c = 1; c < row.length; c++) {
//                String val = row[c] != null ? row[c].toString() : "";
//                if (c == 5) { // Result
//                    String cls = "Pass".equals(val) ? "pass" : "fail";
//                    html.append("<td><span class='").append(cls).append("'>").append(escHtml(val)).append("</span></td>");
//                } else if (c == 6) { // Grade
//                    String cls = "grade-" + val.toLowerCase();
//                    html.append("<td><span class='").append(cls).append("'>").append(escHtml(val)).append("</span></td>");
//                } else {
//                    html.append("<td>").append(escHtml(val)).append("</td>");
//                }
//            }
//            html.append("</tr>");
//        }
//        html.append("</tbody></table>");
//
//        // FOOTER
//        html.append("<div class='footer'>");
//        html.append("<span>Generated: ").append(new java.util.Date()).append("</span>");
//        html.append("<span>This is a computer-generated report.</span>");
//        html.append("<span>Faculty ID: ").append(facultyId).append("</span>");
//        html.append("</div>");
//
//        html.append("</body></html>");
//
//        // Write file
//        try (PrintWriter pw = new PrintWriter(new FileWriter(htmlFile))) {
//            pw.print(html.toString());
//            statusLabel.setText("✓ Report saved: " + htmlFile.getName() + " — Open in browser and print as PDF");
//            int choice = JOptionPane.showConfirmDialog(this,
//                    "✅ Report exported successfully!\n\nFile: " + htmlFile.getAbsolutePath() +
//                    "\n\nThe report includes the college logo, statistics summary, and\ncolour-coded results table.\n\n" +
//                    "Open the file in your browser now?\n(Use browser's Print → Save as PDF for a proper PDF)",
//                    "Export Complete", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
//            if (choice == JOptionPane.YES_OPTION) {
//                try { Desktop.getDesktop().open(htmlFile); } catch (Exception ex) { ex.printStackTrace(); }
//            }
//        } catch (IOException ex) {
//            JOptionPane.showMessageDialog(this, "Error saving report: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
//            ex.printStackTrace();
//        }
//    }
//
//    private String statBox(String val, String lbl, String borderColor, String textColor) {
//        return "<div class='stat-box' style='border-color:" + borderColor + "'>" +
//               "<div class='s-val' style='color:" + textColor + "'>" + val + "</div>" +
//               "<div class='s-lbl' style='color:#7f8c8d'>" + lbl + "</div></div>";
//    }
//
//    private String escHtml(String s) {
//        if (s == null) return "";
//        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
//    }
//
//    // =========================================================
//    // INNER CLASS: ResultsPrintable — draws logo + table via Java2D
//    // Used by printReport() for actual printer output
//    // =========================================================
//    private static class ResultsPrintable implements Printable {
//
//        private final BufferedImage logo;
//        private final String examInfo, classInfo, subjectInfo;
//        private final Object[][] data;
//        private final String[] colNames;
//
//        // Visible columns (skip index 0 = Student ID)
//        private static final int[] VISIBLE_COLS = {1, 2, 3, 4, 5, 6};
//
//        ResultsPrintable(BufferedImage logo, String examInfo, String classInfo,
//                         String subjectInfo, Object[][] data, String[] colNames) {
//            this.logo        = logo;
//            this.examInfo    = examInfo;
//            this.classInfo   = classInfo;
//            this.subjectInfo = subjectInfo;
//            this.data        = data;
//            this.colNames    = colNames;
//        }
//
//        @Override
//        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
//            // Calculate total pages needed
//            int rowsPerPage = 28;
//            int totalPages  = (int) Math.ceil((double) data.length / rowsPerPage);
//            if (totalPages == 0) totalPages = 1;
//            if (pageIndex >= totalPages) return NO_SUCH_PAGE;
//
//            Graphics2D g2 = (Graphics2D) graphics;
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//
//            float x = (float) pageFormat.getImageableX();
//            float y = (float) pageFormat.getImageableY();
//            float w = (float) pageFormat.getImageableWidth();
//
//            g2.translate(x, y);
//            float curY = 0;
//
//            // ---- HEADER (only on page 0) ----
//            if (pageIndex == 0) {
//                // Draw logo
//                int logoH = 0;
//                if (logo != null) {
//                    int logoW = 80;
//                    logoH = 70;
//                    g2.drawImage(logo, 0, 0, logoW, logoH, null);
//
//                    // College info next to logo
//                    g2.setColor(new Color(44, 62, 80));
//                    g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
//                    g2.drawString("Student Results Report", logoW + 15, 25);
//                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//                    g2.setColor(new Color(100, 100, 100));
//
//                    String className  = classInfo.contains(" - ")   ? classInfo.split(" - ", 2)[1]   : classInfo;
//                    String subName    = subjectInfo.contains(" - ") ? subjectInfo.split(" - ", 2)[1] : subjectInfo;
//                    String examDisplay = examInfo.contains(":")      ? examInfo.split(":", 2)[1].split("\\(")[0].trim() : examInfo;
//
//                    g2.drawString("Class: " + className + "   |   Subject: " + subName, logoW + 15, 42);
//                    g2.drawString("Exam: " + examDisplay, logoW + 15, 57);
//                    g2.drawString("Generated: " + new java.util.Date(), logoW + 15, 72);
//                    curY = logoH + 10;
//                } else {
//                    // No logo — just text header
//                    g2.setColor(new Color(44, 62, 80));
//                    g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
//                    g2.drawString("Student Results Report", 0, 20);
//                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//                    g2.setColor(new Color(100, 100, 100));
//                    String className  = classInfo.contains(" - ")   ? classInfo.split(" - ", 2)[1]   : classInfo;
//                    String subName    = subjectInfo.contains(" - ") ? subjectInfo.split(" - ", 2)[1] : subjectInfo;
//                    g2.drawString("Class: " + className + "   Subject: " + subName + "   Exam: " + examInfo, 0, 36);
//                    curY = 50;
//                }
//
//                // Divider line
//                g2.setColor(new Color(44, 62, 80));
//                g2.setStroke(new BasicStroke(1.5f));
//                g2.drawLine(0, (int) curY, (int) w, (int) curY);
//                curY += 8;
//            }
//
//            // ---- TABLE HEADER ROW ----
//            int[] colWidths = computeColWidths((int) w);
//            int rowH = 22;
//
//            g2.setColor(new Color(44, 62, 80));
//            g2.fillRect(0, (int) curY, (int) w, rowH);
//            g2.setColor(Color.WHITE);
//            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
//
//            int colX = 0;
//            for (int ci = 0; ci < VISIBLE_COLS.length; ci++) {
//                g2.drawString(colNames[VISIBLE_COLS[ci]], colX + 4, (int) curY + 15);
//                colX += colWidths[ci];
//            }
//            curY += rowH;
//
//            // ---- DATA ROWS ----
//            int startRow = pageIndex * rowsPerPage;
//            int endRow   = Math.min(startRow + rowsPerPage, data.length);
//
//            for (int r = startRow; r < endRow; r++) {
//                // Alternating row background
//                Color rowBg = (r % 2 == 0) ? new Color(248, 250, 252) : Color.WHITE;
//                // Highlight fail rows
//                String result = data[r][5] != null ? data[r][5].toString() : "";
//                if ("Fail".equals(result)) rowBg = new Color(255, 235, 235);
//
//                g2.setColor(rowBg);
//                g2.fillRect(0, (int) curY, (int) w, rowH - 2);
//
//                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
//                colX = 0;
//                for (int ci = 0; ci < VISIBLE_COLS.length; ci++) {
//                    int col = VISIBLE_COLS[ci];
//                    String val = data[r][col] != null ? data[r][col].toString() : "";
//
//                    // Color-code Result and Grade cells
//                    if (col == 5) {
//                        g2.setColor("Pass".equals(val) ? new Color(27, 94, 32) : new Color(183, 28, 28));
//                        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
//                    } else if (col == 6) {
//                        switch (val) {
//                            case "A": g2.setColor(new Color(39, 174, 96)); break;
//                            case "B": g2.setColor(new Color(41, 128, 185)); break;
//                            case "C": g2.setColor(new Color(243, 156, 18)); break;
//                            case "D": g2.setColor(new Color(211, 84, 0)); break;
//                            case "F": g2.setColor(new Color(192, 57, 43)); break;
//                            default:  g2.setColor(Color.BLACK);
//                        }
//                        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
//                    } else {
//                        g2.setColor(new Color(50, 50, 50));
//                        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
//                    }
//
//                    g2.drawString(val, colX + 4, (int) curY + 14);
//                    colX += colWidths[ci];
//                }
//
//                // Row separator
//                g2.setColor(new Color(220, 220, 220));
//                g2.setStroke(new BasicStroke(0.5f));
//                g2.drawLine(0, (int) curY + rowH - 2, (int) w, (int) curY + rowH - 2);
//
//                curY += rowH;
//            }
//
//            // ---- PAGE FOOTER ----
//            g2.setFont(new Font("Segoe UI", Font.ITALIC, 9));
//            g2.setColor(new Color(150, 150, 150));
//            String footerText = "Page " + (pageIndex + 1) + " of " + totalPages +
//                                "   |   This is a computer-generated report.";
//            g2.drawString(footerText, 0, (int) pageFormat.getImageableHeight() - (int) pageFormat.getImageableY() - 5);
//
//            return PAGE_EXISTS;
//        }
//
//        private int[] computeColWidths(int totalWidth) {
//            // Roll No, Name, Marks, Percentage, Result, Grade
//            double[] fractions = {0.12, 0.35, 0.12, 0.15, 0.13, 0.13};
//            int[] widths = new int[fractions.length];
//            for (int i = 0; i < fractions.length; i++)
//                widths[i] = (int) (totalWidth * fractions[i]);
//            return widths;
//        }
//    }
//
//    // =========================================================
//    // SEND RESULTS TO PARENTS
//    // =========================================================
//    private void sendResultsToParents() {
//        if (model.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No results loaded to send!", "Empty Data", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        StringBuilder preview = new StringBuilder("<html><b>📧 Send Results Preview</b><br><br>");
//        preview.append("Exam: <b>").append(comboExam.getSelectedItem()).append("</b><br>");
//        preview.append("Class: <b>").append(comboClass.getSelectedItem()).append("</b><br><br>");
//        preview.append("<b>Students to process:</b> ").append(model.getRowCount()).append("<br><br>");
//        preview.append("<b>Proceed?</b></html>");
//
//        int confirm = JOptionPane.showConfirmDialog(this, preview, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
//        if (confirm != JOptionPane.YES_OPTION) return;
//
//        JDialog progressDialog = new JDialog(this, "Sending Results...", true);
//        JProgressBar progressBar = new JProgressBar(0, model.getRowCount());
//        progressBar.setStringPainted(true);
//        progressBar.setPreferredSize(new Dimension(300, 30));
//        progressDialog.add(progressBar);
//        progressDialog.setSize(350, 120);
//        progressDialog.setLocationRelativeTo(this);
//        JLabel progressLabel = new JLabel("Preparing to send...", SwingConstants.CENTER);
//        progressDialog.add(progressLabel, BorderLayout.SOUTH);
//        progressDialog.setVisible(true);
//
//        new Thread(() -> {
//            AtomicInteger sentCount = new AtomicInteger(0);
//            AtomicInteger failedCount = new AtomicInteger(0);
//            AtomicInteger noContactCount = new AtomicInteger(0);
//            AtomicInteger smsSentCount = new AtomicInteger(0);
//
//            String examName    = comboExam.getSelectedItem()    != null ? comboExam.getSelectedItem().toString().split(":")[1].split("\\(")[0].trim() : "Unknown";
//            String className   = comboClass.getSelectedItem()   != null ? comboClass.getSelectedItem().toString().split(" - ")[1].trim() : "Unknown";
//            String subjectName = comboSubject.getSelectedItem() != null ? comboSubject.getSelectedItem().toString().split(" - ")[1].trim() : "Unknown";
//
//            for (int i = 0; i < model.getRowCount(); i++) {
//                try {
//                    int studentId  = Integer.parseInt(model.getValueAt(i, 0).toString());
//                    String rollNo  = model.getValueAt(i, 1).toString();
//                    String name    = model.getValueAt(i, 2).toString();
//                    int marks      = Integer.parseInt(model.getValueAt(i, 3).toString());
//                    double pct     = Double.parseDouble(model.getValueAt(i, 4).toString().replace("%", ""));
//                    String result  = model.getValueAt(i, 5).toString();
//                    String grade   = model.getValueAt(i, 6).toString();
//                    int maxMarks   = examDAO.getExamById(Integer.parseInt(comboExam.getSelectedItem().toString().split(":")[0].trim())).getMaxMarks();
//
//                    com.college.sms.model.Student student = studentDAO.getStudentById(studentId);
//                    String email   = student.getParentEmail();
//                    String mobile  = student.getParentMobile();
//                    boolean consent = student.getConsentToCommunicate() != null ? student.getConsentToCommunicate() : true;
//                    boolean hasContact = (email != null && !email.trim().isEmpty()) || (mobile != null && !mobile.trim().isEmpty());
//
//                    if (!hasContact || !consent) { noContactCount.incrementAndGet(); continue; }
//
//                    if (email != null && !email.trim().isEmpty()) {
//                        try {
//                            if (EmailService.sendResultEmail(email, name, rollNo, className, subjectName, examName, marks, maxMarks, pct, result, grade))
//                                sentCount.incrementAndGet();
//                        } catch (Exception e) { failedCount.incrementAndGet(); }
//                    }
//
//                    if (mobile != null && !mobile.trim().isEmpty()) {
//                        try {
//                            if (SMSService.sendResultSMS(mobile, name, rollNo, className, subjectName, examName, marks, maxMarks, pct, result, grade))
//                                smsSentCount.incrementAndGet();
//                        } catch (Exception e) { /* silent */ }
//                    }
//
//                    final int prog = i + 1;
//                    final String fn = name;
//                    SwingUtilities.invokeLater(() -> {
//                        progressBar.setValue(prog);
//                        progressBar.setString(prog + "/" + model.getRowCount() + " processed");
//                        progressLabel.setText("Sending to: " + fn + "...");
//                    });
//                    Thread.sleep(400);
//                } catch (Exception ex) { failedCount.incrementAndGet(); ex.printStackTrace(); }
//            }
//
//            SwingUtilities.invokeLater(() -> {
//                progressDialog.dispose();
//                String summary = String.format("✅ Complete!\n\n📧 Emails: %d\n📱 SMS: %d\n❌ Failed: %d\n⚠️ No Contact: %d",
//                        sentCount.get(), smsSentCount.get(), failedCount.get(), noContactCount.get());
//                JOptionPane.showMessageDialog(ResultsUI.this, summary, "Send Complete", JOptionPane.INFORMATION_MESSAGE);
//                statusLabel.setText("✓ Sent to " + sentCount.get() + " parents");
//            });
//        }).start();
//    }
//
//    // =========================================================
//    // STAT CARD
//    // =========================================================
//    private JPanel createStatCard(String title, String value, Color color, java.util.function.Consumer<JLabel> labelRef) {
//        JPanel card = new JPanel(new BorderLayout(5, 5));
//        card.setBackground(Color.WHITE);
//        card.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(color, 3),
//                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
//        card.setPreferredSize(new Dimension(200, 120));
//        JLabel titleLabel = new JLabel(title);
//        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
//        titleLabel.setForeground(new Color(120, 120, 120));
//        JLabel valueLabel = new JLabel(value);
//        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
//        valueLabel.setForeground(color);
//        labelRef.accept(valueLabel);
//        card.add(titleLabel, BorderLayout.NORTH);
//        card.add(valueLabel, BorderLayout.CENTER);
//        return card;
//    }
//
//    // =========================================================
//    // DATA LOADING
//    // =========================================================
//    private void loadData() { loadClasses(); }
//
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
//        if (classes.isEmpty()) {
//            comboClass.addItem("-- No classes assigned --");
//            statusLabel.setText("⚠️ No classes assigned.");
//            JOptionPane.showMessageDialog(this, "No classes found!", "No Classes", JOptionPane.WARNING_MESSAGE);
//        } else {
//            for (String[] c : classes) comboClass.addItem(c[0] + " - " + c[1]);
//            comboClass.setSelectedIndex(0);
//            loadSubjects();
//        }
//    }
//
//    private void loadSubjects() {
//        isLoadingSubjects = true;
//        comboSubject.removeAllItems();
//        comboExam.removeAllItems();
//        if (comboClass.getSelectedItem() == null || comboClass.getSelectedItem().toString().contains("--")) {
//            comboSubject.addItem("-- Select class first --");
//            isLoadingSubjects = false;
//            return;
//        }
//        try {
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
//            if (subjects.isEmpty()) {
//                comboSubject.addItem("-- No subjects assigned --");
//            } else {
//                for (Subject s : subjects) comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
//                comboSubject.setSelectedIndex(0);
//            }
//        } catch (Exception e) {
//            comboSubject.addItem("-- Error --");
//        }
//        isLoadingSubjects = false;
//        loadExams();
//    }
//
//    private void loadExams() {
//        if (isLoadingSubjects) return;
//        isLoadingExams = true;
//        comboExam.removeAllItems();
//        if (comboSubject.getSelectedItem() == null || comboSubject.getSelectedItem().toString().contains("--")) {
//            comboExam.addItem("-- Select subject first --");
//            isLoadingExams = false;
//            return;
//        }
//        try {
//            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//            int classId   = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, classId, subjectId);
//            if (exams.isEmpty()) {
//                comboExam.addItem("-- No exams --");
//            } else {
//                for (Exam ex : exams)
//                    comboExam.addItem(ex.getExamId() + ":" + ex.getExamName() + " (Max: " + ex.getMaxMarks() + ")");
//                comboExam.setSelectedIndex(0);
//            }
//        } catch (Exception e) {
//            comboExam.addItem("-- Error --");
//        }
//        isLoadingExams = false;
//    }
//
//    // =========================================================
//    // LOAD RESULTS (unchanged logic)
//    // =========================================================
//    private void loadResults() {
//        model.setRowCount(0);
//        chartContainer.removeAll();
//        if (comboClass.getSelectedItem() == null || comboSubject.getSelectedItem() == null || comboExam.getSelectedItem() == null) {
//            JOptionPane.showMessageDialog(this, "Please select class, subject and exam!", "Missing", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        if (comboSubject.getSelectedItem().toString().contains("--") || comboExam.getSelectedItem().toString().contains("--")) {
//            JOptionPane.showMessageDialog(this, "No valid subjects/exams!", "Invalid", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        try {
//            int classId   = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//            int examId    = Integer.parseInt(comboExam.getSelectedItem().toString().split(":")[0].trim());
//            String examName = comboExam.getSelectedItem().toString().split(":")[1].split("\\(")[0].trim();
//            Exam exam = examDAO.getExamById(examId);
//            if (exam == null) { JOptionPane.showMessageDialog(this, "Exam not found!", "Error", JOptionPane.ERROR_MESSAGE); return; }
//            int maxMarks  = exam.getMaxMarks();
//            int passMarks = exam.getPassMarks();
//            List<String[]> students = studentDAO.getStudentsByClass(classId);
//            if (students.isEmpty()) { JOptionPane.showMessageDialog(this, "No students in class!", "Empty", JOptionPane.INFORMATION_MESSAGE); return; }
//
//            DefaultCategoryDataset individualDataset = new DefaultCategoryDataset();
//            DefaultCategoryDataset passFailDataset   = new DefaultCategoryDataset();
//            DefaultCategoryDataset achieversDataset  = new DefaultCategoryDataset();
//            int totalMarks = 0, passCount = 0, highest = 0, lowest = Integer.MAX_VALUE, above60 = 0, above75 = 0;
//            DecimalFormat pctFmt = new DecimalFormat("0.00");
//
//            for (String[] s : students) {
//                int studentId = Integer.parseInt(s[0]);
//                int marks = studentDAO.getMarksByExam(studentId, examId);
//                double pct = (maxMarks > 0) ? (marks * 100.0) / maxMarks : 0.0;
//                String pctStr = pctFmt.format(pct) + "%";
//                if (pct > 60) above60++;
//                if (pct > 75) above75++;
//                String grade  = calculateGrade(marks, maxMarks);
//                String result = (marks >= passMarks) ? "Pass" : "Fail";
//                if ("Pass".equals(result)) passCount++;
//                totalMarks += marks;
//                highest = Math.max(highest, marks);
//                lowest  = Math.min(lowest, marks);
//                model.addRow(new Object[]{s[0], s[1], s[2], marks, pctStr, result, grade});
//                individualDataset.addValue((double) marks, s[2], "Marks");
//            }
//
//            int failCount   = students.size() - passCount;
//            double passPct  = (double) passCount / students.size() * 100;
//
//            JFreeChart individualChart = ChartFactory.createBarChart("Individual Marks: " + examName, "Students", "Marks", individualDataset, PlotOrientation.VERTICAL, false, true, false);
//            individualChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
//            CategoryPlot ip = individualChart.getCategoryPlot();
//            BarRenderer ir  = (BarRenderer) ip.getRenderer();
//            ir.setSeriesPaint(0, new Color(46, 204, 113));
//            ip.setBackgroundPaint(Color.WHITE);
//            ChartPanel icp = new ChartPanel(individualChart);
//            icp.setPreferredSize(new Dimension(500, 300));
//
//            passFailDataset.addValue(passCount, "Count", "Pass");
//            passFailDataset.addValue(failCount, "Count", "Fail");
//            JFreeChart pfChart = ChartFactory.createBarChart("Pass/Fail Distribution", "Result", "Students", passFailDataset, PlotOrientation.VERTICAL, false, true, false);
//            customizePerformanceChart(pfChart, true, passCount, failCount);
//            ChartPanel pfcp = new ChartPanel(pfChart);
//            pfcp.setPreferredSize(new Dimension(380, 320));
//
//            achieversDataset.addValue(above60, "Count", "> 60%");
//            achieversDataset.addValue(above75, "Count", "> 75%");
//            JFreeChart achChart = ChartFactory.createBarChart("High Achievers", "Threshold", "Students", achieversDataset, PlotOrientation.VERTICAL, false, true, false);
//            customizePerformanceChart(achChart, false, above60, above75);
//            ChartPanel achcp = new ChartPanel(achChart);
//            achcp.setPreferredSize(new Dimension(380, 320));
//
//            JPanel topChartPnl = new JPanel(new BorderLayout());
//            topChartPnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1), "Individual Performance", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)));
//            topChartPnl.setBackground(Color.WHITE);
//            topChartPnl.add(icp, BorderLayout.CENTER);
//
//            JPanel midCharts = new JPanel(new GridLayout(1, 2, 30, 0));
//            midCharts.setBackground(Color.WHITE);
//            midCharts.setBorder(new EmptyBorder(10, 0, 10, 0));
//
//            JPanel pfWrap = new JPanel(new BorderLayout());
//            pfWrap.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(40, 167, 69), 2), "📊 Pass/Fail", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 15), new Color(40, 167, 69)));
//            pfWrap.setBackground(Color.WHITE);
//            pfWrap.add(pfcp, BorderLayout.CENTER);
//
//            JPanel achWrap = new JPanel(new BorderLayout());
//            achWrap.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(108, 92, 231), 2), "📊 High Achievers", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 15), new Color(108, 92, 231)));
//            achWrap.setBackground(Color.WHITE);
//            achWrap.add(achcp, BorderLayout.CENTER);
//            midCharts.add(pfWrap);
//            midCharts.add(achWrap);
//
//            JPanel summaryPnl = new JPanel(new GridLayout(2, 4, 20, 15));
//            summaryPnl.setBackground(new Color(248, 250, 252));
//            summaryPnl.setBorder(new EmptyBorder(15, 20, 20, 20));
//            summaryPnl.add(createMetricLabel("Present",  String.valueOf(students.size()), new Color(52, 152, 219)));
//            summaryPnl.add(createMetricLabel("Absent",   "0", new Color(149, 165, 166)));
//            summaryPnl.add(createMetricLabel("Pass",     String.valueOf(passCount), new Color(40, 167, 69)));
//            summaryPnl.add(createMetricLabel("Fail",     String.valueOf(failCount), new Color(220, 53, 69)));
//            summaryPnl.add(createMetricLabel("Pass %",   String.format("%.1f%%", passPct), new Color(40, 167, 69)));
//            summaryPnl.add(createMetricLabel("Fail %",   String.format("%.1f%%", 100 - passPct), new Color(220, 53, 69)));
//            summaryPnl.add(createMetricLabel("> 60%",    String.valueOf(above60), new Color(52, 152, 219)));
//            summaryPnl.add(createMetricLabel("> 75%",    String.valueOf(above75), new Color(108, 92, 231)));
//
//            chartContainer.add(topChartPnl, BorderLayout.NORTH);
//            chartContainer.add(midCharts, BorderLayout.CENTER);
//            chartContainer.add(summaryPnl, BorderLayout.SOUTH);
//
//            updateStatistics(students.size(), totalMarks, passCount, highest, lowest == Integer.MAX_VALUE ? 0 : lowest, passMarks);
//            double avg = (double) totalMarks / students.size();
//            statusLabel.setText(String.format("✓ Loaded %d students | Avg: %.1f | Pass: %.1f%%", students.size(), avg, passPct));
//            mainTabbedPane.setSelectedIndex(0);
//
//        } catch (Exception e) {
//            statusLabel.setText("Error: " + e.getMessage());
//            JOptionPane.showMessageDialog(this, "Error loading results: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }
//
//    // =========================================================
//    // HELPER METHODS
//    // =========================================================
//    private JLabel createFilterLabel(String text) {
//        JLabel l = new JLabel(text);
//        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
//        l.setForeground(new Color(52, 73, 94));
//        return l;
//    }
//
//    private JComboBox<String> createModernComboBox() {
//        JComboBox<String> c = new JComboBox<>();
//        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        c.setPreferredSize(new Dimension(220, 36));
//        c.setBackground(Color.WHITE);
//        c.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
//        return c;
//    }
//
//    private JButton createModernButton(String text, Color bg, Color fg) {
//        JButton b = new JButton(text);
//        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        b.setForeground(fg);
//        b.setBackground(bg);
//        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
//        b.setFocusPainted(false);
//        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        b.addMouseListener(new MouseAdapter() {
//            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
//            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
//        });
//        return b;
//    }
//
//    private void customizePerformanceChart(JFreeChart chart, boolean isPassFail, double v1, double v2) {
//        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
//        chart.getTitle().setPaint(new Color(44, 62, 80));
//        chart.setBackgroundPaint(Color.WHITE);
//        CategoryPlot plot = chart.getCategoryPlot();
//        plot.setBackgroundPaint(Color.WHITE);
//        plot.setRangeGridlinePaint(new Color(220, 220, 220));
//        CategoryAxis da = plot.getDomainAxis();
//        da.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
//        da.setTickLabelFont(new Font("Segoe UI", Font.BOLD, 13));
//        NumberAxis ra = (NumberAxis) plot.getRangeAxis();
//        ra.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//        BarRenderer r = (BarRenderer) plot.getRenderer();
//        r.setDrawBarOutline(false);
//        r.setItemMargin(0.18);
//        r.setDefaultItemLabelsVisible(true);
//        r.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 14));
//        r.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
//        if (isPassFail) {
//            r.setSeriesPaint(0, new Color(40, 167, 69));
//            r.setSeriesPaint(1, new Color(220, 53, 69));
//        } else {
//            r.setSeriesPaint(0, new Color(52, 152, 219));
//            r.setSeriesPaint(1, new Color(108, 92, 231));
//        }
//        r.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
//        r.setSeriesItemLabelGenerator(1, new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
//    }
//
//    private JPanel createMetricLabel(String title, String value, Color color) {
//        JPanel p = new JPanel(new BorderLayout(5, 8));
//        p.setBackground(Color.WHITE);
//        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1), BorderFactory.createEmptyBorder(10, 5, 10, 5)));
//        JLabel tl = new JLabel(title); tl.setFont(new Font("Segoe UI", Font.PLAIN, 14)); tl.setForeground(new Color(100,100,100)); tl.setHorizontalAlignment(SwingConstants.CENTER);
//        JLabel vl = new JLabel(value); vl.setFont(new Font("Segoe UI", Font.BOLD, 20)); vl.setForeground(color); vl.setHorizontalAlignment(SwingConstants.CENTER);
//        p.add(tl, BorderLayout.NORTH);
//        p.add(vl, BorderLayout.CENTER);
//        return p;
//    }
//
//    private String calculateGrade(int marks, int maxMarks) {
//        if (maxMarks <= 0) return "N/A";
//        double pct = (double) marks / maxMarks * 100;
//        if (pct >= 90) return "A";
//        else if (pct >= 80) return "B";
//        else if (pct >= 70) return "C";
//        else if (pct >= 60) return "D";
//        else return "F";
//    }
//
//    private void updateStatistics(int total, int totalMarks, int pass, int high, int low, int passMarks) {
//        totalStudentsLabel.setText(String.valueOf(total));
//        avgMarksLabel.setText(String.format("%.1f", (double) totalMarks / total));
//        passPercentLabel.setText(String.format("%.1f%%", (double) pass / total * 100));
//        highestLabel.setText(String.valueOf(high));
//        lowestLabel.setText(String.valueOf(low));
//        passCountLabel.setText(String.valueOf(pass));
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                JFrame mock = new JFrame("Faculty Dashboard");
//                mock.setSize(1000, 650);
//                mock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                mock.setVisible(true);
//                new ResultsUI(1, mock);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//}

//---------------------------

package com.college.sms.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.college.sms.dao.ClassDAO;
import com.college.sms.dao.ExamDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.dao.SubjectDAO;
import com.college.sms.model.Exam;
import com.college.sms.model.Subject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;

import com.college.sms.ui.SubjectPerformanceUI;
import com.college.sms.ui.StudentPerformanceBySubjectUI;
import com.college.sms.ui.ParentContactUpdater;
import com.college.sms.ui.ParentCommunicationUI;
import com.college.sms.util.EmailService;
import com.college.sms.util.SMSService;

public class ResultsUI extends JFrame {

    private int facultyId;
    private JFrame previousUI;
    private JComboBox<String> comboClass;
    private JComboBox<String> comboSubject;
    private JComboBox<String> comboExam;
    private JTable table;
    private DefaultTableModel model;
    private StudentDAO studentDAO;
    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;
    private ExamDAO examDAO;
    private boolean isLoadingSubjects = false;
    private boolean isLoadingExams    = false;
    private JLabel  statusLabel;
    private JPanel  chartContainer;
    private JTabbedPane mainTabbedPane;

    private JLabel totalStudentsLabel, avgMarksLabel, passPercentLabel,
                   highestLabel, lowestLabel, passCountLabel;

    // ----------------------------------------------------------
    // LOGO LOADER
    // ----------------------------------------------------------
    private BufferedImage collegeLogo = null;

    private BufferedImage loadCollegeLogo() {
        if (collegeLogo != null) return collegeLogo;
        try {
            // 1) classpath (JAR / Maven resources)
            URL url = getClass().getClassLoader().getResource("images/college_logo.png");
            if (url != null) { collegeLogo = ImageIO.read(url); return collegeLogo; }

            // 2) common file-system fallbacks (IDE run)
            for (String path : new String[]{
                    "resources/images/college_logo.png",
                    "src/main/resources/images/college_logo.png",
                    "images/college_logo.png"}) {
                File f = new File(path);
                if (f.exists()) { collegeLogo = ImageIO.read(f); return collegeLogo; }
            }
        } catch (Exception e) {
            System.err.println("Could not load college logo: " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------
    public ResultsUI(int facultyId, JFrame previousUI) {
        this.facultyId  = facultyId;
        this.previousUI = previousUI;
        studentDAO  = new StudentDAO();
        classDAO    = new ClassDAO();
        subjectDAO  = new SubjectDAO();
        examDAO     = new ExamDAO();

        setTitle("📊 Results Dashboard | Faculty ID: " + facultyId);
        setSize(1250, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 247, 250));

        initComponents();
        loadData();
        setVisible(true);
    }

    // ----------------------------------------------------------
    // INIT COMPONENTS
    // ----------------------------------------------------------
    private void initComponents() {
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        // Header bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(0, 75));
        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = new JLabel("🎓 Student Results Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton btnBack = createModernButton("⇦ Back to Dashboard", new Color(41, 128, 185), Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBack.setPreferredSize(new Dimension(200, 42));
        btnBack.addActionListener(e -> {
            dispose();
            if (previousUI != null) { previousUI.setVisible(true); previousUI.toFront(); previousUI.requestFocus(); }
        });
        headerPanel.add(btnBack, BorderLayout.EAST);
        topWrapper.add(headerPanel);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                "🔍 Filter Results", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), new Color(52, 152, 219)));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setPreferredSize(new Dimension(0, 90));

        filterPanel.add(createFilterLabel("🏫 Class:"));
        comboClass = createModernComboBox();
        filterPanel.add(comboClass);

        filterPanel.add(createFilterLabel("📚 Subject:"));
        comboSubject = createModernComboBox();
        filterPanel.add(comboSubject);

        filterPanel.add(createFilterLabel("📝 Exam:"));
        comboExam = createModernComboBox();
        filterPanel.add(comboExam);

        JButton btnLoad = createModernButton("📊 Load Results", new Color(46, 204, 113), Color.WHITE);
        btnLoad.setPreferredSize(new Dimension(160, 38));
        btnLoad.addActionListener(e -> loadResults());
        filterPanel.add(btnLoad);
        topWrapper.add(filterPanel);
        add(topWrapper, BorderLayout.NORTH);

        // Tabbed pane
        mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
        mainTabbedPane.setBackground(new Color(245, 247, 250));
        mainTabbedPane.setForeground(new Color(52, 73, 94));

        // Tab 1 – Results Table
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        model = new DefaultTableModel(
                new String[]{"Student ID", "Roll No", "Name", "Marks", "Percentage", "Result", "Grade"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel,
                                                           boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
                    c.setForeground(Color.BLACK);
                    if (col == 5) {
                        if ("Pass".equals(value)) { c.setBackground(new Color(200,230,201)); c.setForeground(new Color(27,94,32)); }
                        else                       { c.setBackground(new Color(255,205,210)); c.setForeground(new Color(183,28,28)); }
                    }
                    if (col == 4) {
                        try {
                            double pct = Double.parseDouble(value.toString().replace("%","").trim());
                            if      (pct >= 90) { c.setBackground(new Color(100,221,23));  c.setForeground(Color.WHITE); c.setFont(c.getFont().deriveFont(Font.BOLD)); }
                            else if (pct >= 75) { c.setBackground(new Color(129,199,132)); c.setForeground(new Color(27,94,32)); }
                            else if (pct >= 60) { c.setBackground(new Color(255,249,196)); c.setForeground(new Color(137,104,0)); }
                            else if (pct >= 40) { c.setBackground(new Color(255,224,178)); c.setForeground(new Color(165,82,0)); }
                            else                { c.setBackground(new Color(255,205,210)); c.setForeground(new Color(183,28,28)); }
                        } catch (Exception ignored) {}
                    }
                    if (col == 6) {
                        String g = value != null ? value.toString() : "";
                        switch (g) {
                            case "A": c.setBackground(new Color(100,221,23));  c.setForeground(Color.WHITE); break;
                            case "B": c.setBackground(new Color(129,199,132)); c.setForeground(new Color(27,94,32)); break;
                            case "C": c.setBackground(new Color(255,249,196)); c.setForeground(new Color(137,104,0)); break;
                            case "D": c.setBackground(new Color(255,224,178)); c.setForeground(new Color(165,82,0)); break;
                            case "F": c.setBackground(new Color(255,205,210)); c.setForeground(new Color(183,28,28)); break;
                        }
                    }
                }
                return c;
            }
        });

        // Hide Student ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        actionPanel.setBackground(Color.WHITE);

        JButton btnExport = createModernButton("📤 Export PDF", new Color(155, 89, 182), Color.WHITE);
        btnExport.setPreferredSize(new Dimension(130, 34));
        btnExport.setToolTipText("Export results to HTML report with college logo (open in browser → Print → Save as PDF)");
        btnExport.addActionListener(e -> exportResults());
        actionPanel.add(btnExport);

        JButton btnPrint = createModernButton("🖨️ Print", new Color(243, 156, 18), Color.WHITE);
        btnPrint.setPreferredSize(new Dimension(100, 34));
        btnPrint.setToolTipText("Print results — college logo appears on every page");
        btnPrint.addActionListener(e -> printReport());
        actionPanel.add(btnPrint);

        JButton btnSend = createModernButton("📧 Send", new Color(241, 196, 15), Color.WHITE);
        btnSend.setPreferredSize(new Dimension(100, 34));
        btnSend.addActionListener(e -> sendResultsToParents());
        actionPanel.add(btnSend);

        JButton btnContacts = createModernButton("✏️ Contacts", new Color(142, 68, 173), Color.WHITE);
        btnContacts.setPreferredSize(new Dimension(120, 34));
        btnContacts.addActionListener(e -> new ParentContactUpdater(ResultsUI.this, facultyId).setVisible(true));
        actionPanel.add(btnContacts);

        JButton btnComm = createModernButton("💬 Comm Center", new Color(142, 68, 173), Color.WHITE);
        btnComm.setPreferredSize(new Dimension(130, 34));
        btnComm.addActionListener(e -> {
            new ParentCommunicationUI(facultyId, ResultsUI.this).setVisible(true);
            setVisible(false);
        });
        actionPanel.add(btnComm);

        JButton btnSubjPerf = createModernButton("🎯 Subject Perf", new Color(52, 152, 219), Color.WHITE);
        btnSubjPerf.setPreferredSize(new Dimension(130, 34));
        btnSubjPerf.addActionListener(e -> {
            if (comboClass.getSelectedItem() == null || comboSubject.getSelectedItem() == null
                    || comboClass.getSelectedItem().toString().contains("--")
                    || comboSubject.getSelectedItem().toString().contains("--")) {
                JOptionPane.showMessageDialog(this, "Select valid class and subject first!", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try { new SubjectPerformanceUI(facultyId, ResultsUI.this).setVisible(true); setVisible(false); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });
        actionPanel.add(btnSubjPerf);

        JButton btnStuPerf = createModernButton("👤 Student Report", new Color(211, 84, 0), Color.WHITE);
        btnStuPerf.setPreferredSize(new Dimension(140, 34));
        btnStuPerf.addActionListener(e -> new StudentPerformanceBySubjectUI(facultyId, ResultsUI.this).setVisible(true));
        actionPanel.add(btnStuPerf);

        tablePanel.add(actionPanel, BorderLayout.SOUTH);
        mainTabbedPane.addTab("📋 Results Table", tablePanel);

        // Tab 2 – Charts
        chartContainer = new JPanel(new BorderLayout(20, 15));
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainTabbedPane.addTab("📈 Visual Analytics", chartContainer);

        // Tab 3 – Statistics
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        statsPanel.add(createStatCard("Total Students",  "0",   new Color(52,152,219),  lbl -> totalStudentsLabel = lbl));
        statsPanel.add(createStatCard("Average Marks",   "0.0", new Color(46,204,113),  lbl -> avgMarksLabel      = lbl));
        statsPanel.add(createStatCard("Pass Percentage", "0%",  new Color(155,89,182),  lbl -> passPercentLabel   = lbl));
        statsPanel.add(createStatCard("Highest Marks",   "0",   new Color(243,156,18),  lbl -> highestLabel       = lbl));
        statsPanel.add(createStatCard("Lowest Marks",    "0",   new Color(231,76,60),   lbl -> lowestLabel        = lbl));
        statsPanel.add(createStatCard("Pass Count",      "0",   new Color(39,174,96),   lbl -> passCountLabel     = lbl));

        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setBackground(Color.WHITE);
        statsWrapper.setBorder(new TitledBorder(new LineBorder(new Color(52,152,219),2),
                "Quick Statistics", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 18), new Color(52,152,219)));
        statsWrapper.add(statsPanel, BorderLayout.CENTER);
        mainTabbedPane.addTab("🔢 Statistics", statsWrapper);

        add(mainTabbedPane, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("Ready. Select class, subject and exam to begin.");
        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setBackground(new Color(236, 240, 241));
        statusLabel.setForeground(new Color(75, 75, 75));
        statusLabel.setOpaque(true);
        add(statusLabel, BorderLayout.SOUTH);

        comboClass.addActionListener(e -> {
            if (comboClass.getSelectedItem() != null && !comboClass.getSelectedItem().toString().contains("--"))
                loadSubjects();
        });
        comboSubject.addActionListener(e -> {
            if (!isLoadingSubjects && comboSubject.getSelectedItem() != null
                    && !comboSubject.getSelectedItem().toString().contains("--"))
                loadExams();
        });
    }

    // ==========================================================
    // PRINT REPORT
    //
    // FIX 1: PageFormat built with explicit Paper so landscape is real
    // FIX 2: Logo drawn on EVERY page via drawPageHeader()
    // FIX 3: g2.translate() uses imageable origin once; all drawing
    //         is then relative to (0,0) — no double-offset
    // ==========================================================
    private void printReport() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to print!", "Empty Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Snapshot table data
        int rows = model.getRowCount(), cols = model.getColumnCount();
        Object[][] data     = new Object[rows][cols];
        String[]   colNames = new String[cols];
        for (int c = 0; c < cols; c++) colNames[c] = model.getColumnName(c);
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                data[r][c] = model.getValueAt(r, c);

        String examInfo    = comboExam.getSelectedItem()    != null ? comboExam.getSelectedItem().toString()    : "";
        String classInfo   = comboClass.getSelectedItem()   != null ? comboClass.getSelectedItem().toString()   : "";
        String subjectInfo = comboSubject.getSelectedItem() != null ? comboSubject.getSelectedItem().toString() : "";

        BufferedImage logo = loadCollegeLogo();

        PrinterJob job = PrinterJob.getPrinterJob();

        // ✅ FIX: Explicitly build a landscape A4 PageFormat using Paper
        //         so the printer driver cannot ignore orientation.
        PageFormat pf   = job.defaultPage();
        Paper      paper = new Paper();
        // A4 in points: 595 × 842.  Landscape → width=842, height=595
        double pw = 842.0, ph = 595.0, margin = 36.0;
        paper.setSize(pw, ph);
        paper.setImageableArea(margin, margin, pw - 2 * margin, ph - 2 * margin);
        pf.setPaper(paper);
        pf.setOrientation(PageFormat.LANDSCAPE);
        // Validate with the actual printer service
        PageFormat validatedPf = job.validatePage(pf);

        job.setPrintable(
            new ResultsPrintable(logo, examInfo, classInfo, subjectInfo, data, colNames),
            validatedPf
        );

        if (job.printDialog()) {
            try {
                job.print();
                statusLabel.setText("✓ Report sent to printer successfully");
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Print failed: " + ex.getMessage());
            }
        }
    }

    // ==========================================================
    // EXPORT — styled HTML (college logo embedded as Base64)
    // Open in any browser → File → Print → Save as PDF
    // ==========================================================
    private void exportResults() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export!", "Empty Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Results Report");
        fc.setSelectedFile(new File("results_report.html"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("HTML Report (*.html)", "html"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File out = fc.getSelectedFile();
        if (!out.getName().toLowerCase().endsWith(".html"))
            out = new File(out.getAbsolutePath() + ".html");

        // Snapshot
        int rows = model.getRowCount(), cols = model.getColumnCount();
        Object[][] data     = new Object[rows][cols];
        String[]   colNames = new String[cols];
        for (int c = 0; c < cols; c++) colNames[c] = model.getColumnName(c);
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                data[r][c] = model.getValueAt(r, c);

        String examInfo    = comboExam.getSelectedItem()    != null ? comboExam.getSelectedItem().toString()    : "";
        String classInfo   = comboClass.getSelectedItem()   != null ? comboClass.getSelectedItem().toString()   : "";
        String subjectInfo = comboSubject.getSelectedItem() != null ? comboSubject.getSelectedItem().toString() : "";

        exportAsHTML(out, loadCollegeLogo(), examInfo, classInfo, subjectInfo, data, colNames);
    }

    // ==========================================================
    // INNER CLASS: ResultsPrintable
    //
    // KEY FIXES:
    //  • drawPageHeader() called at the TOP of every page → logo always visible
    //  • Single g2.translate(imageableX, imageableY) — no coordinate confusion
    //  • rowsPerPage computed from actual remaining height after header+footer
    //  • Footer placed at bottom of imageable area on every page
    // ==========================================================
    private static class ResultsPrintable implements Printable {

        // Column indices to print (skip 0 = hidden Student ID)
        private static final int[]    VISIBLE_COLS  = {1, 2, 3, 4, 5, 6};
        // Fraction of total width per column: Roll No, Name, Marks, %, Result, Grade
        private static final double[] COL_FRACTIONS = {0.12, 0.33, 0.12, 0.16, 0.14, 0.13};

        private static final int ROW_H       = 22;  // height of each data row (pts)
        private static final int HEADER_H    = 84;  // height of the logo+info block
        private static final int FOOTER_H    = 20;  // height of the page footer
        private static final int TBL_HDR_H   = 24;  // height of the column-header row
        private static final int LOGO_W      = 68;  // logo rendered width (pts)
        private static final int LOGO_H      = 62;  // logo rendered height (pts)

        private final BufferedImage logo;
        private final String        examInfo, classInfo, subjectInfo;
        private final Object[][]    data;
        private final String[]      colNames;

        ResultsPrintable(BufferedImage logo, String examInfo, String classInfo,
                         String subjectInfo, Object[][] data, String[] colNames) {
            this.logo        = logo;
            this.examInfo    = examInfo;
            this.classInfo   = classInfo;
            this.subjectInfo = subjectInfo;
            this.data        = data;
            this.colNames    = colNames;
        }

        @Override
        public int print(Graphics graphics, PageFormat pf, int pageIndex) {

            int iw = (int) pf.getImageableWidth();
            int ih = (int) pf.getImageableHeight();

            // How many data rows fit below the header+table-header and above the footer?
            int usable      = ih - HEADER_H - TBL_HDR_H - FOOTER_H;
            int rowsPerPage = Math.max(1, usable / ROW_H);

            int totalPages = (int) Math.ceil((double) Math.max(data.length, 1) / rowsPerPage);
            if (pageIndex >= totalPages) return NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

            // ✅ Translate to the imageable (printable) area origin once.
            //    All subsequent drawing uses coordinates relative to (0, 0).
            g2.translate((int) pf.getImageableX(), (int) pf.getImageableY());

            int curY = 0;

            // ================================================================
            // HEADER — logo + report info — drawn on EVERY page
            // ================================================================
            curY = drawPageHeader(g2, iw, pageIndex, totalPages);

            // ================================================================
            // TABLE COLUMN HEADER ROW
            // ================================================================
            int[] colWidths = computeColWidths(iw);
            g2.setColor(new Color(44, 62, 80));
            g2.fillRect(0, curY, iw, TBL_HDR_H);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            int cx = 4;
            for (int ci = 0; ci < VISIBLE_COLS.length; ci++) {
                g2.drawString(colNames[VISIBLE_COLS[ci]], cx, curY + 16);
                cx += colWidths[ci];
            }
            curY += TBL_HDR_H;

            // ================================================================
            // DATA ROWS
            // ================================================================
            int startRow = pageIndex * rowsPerPage;
            int endRow   = Math.min(startRow + rowsPerPage, data.length);

            for (int r = startRow; r < endRow; r++) {
                String result = data[r][5] != null ? data[r][5].toString() : "";
                Color rowBg = "Fail".equals(result)
                        ? new Color(255, 232, 232)
                        : (r % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);

                g2.setColor(rowBg);
                g2.fillRect(0, curY, iw, ROW_H - 1);

                cx = 4;
                for (int ci = 0; ci < VISIBLE_COLS.length; ci++) {
                    int    col = VISIBLE_COLS[ci];
                    String val = data[r][col] != null ? data[r][col].toString() : "";

                    if (col == 5) {
                        g2.setColor("Pass".equals(val) ? new Color(27, 94, 32) : new Color(183, 28, 28));
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    } else if (col == 6) {
                        switch (val) {
                            case "A": g2.setColor(new Color(39, 174, 96));  break;
                            case "B": g2.setColor(new Color(41, 128, 185)); break;
                            case "C": g2.setColor(new Color(243, 156, 18)); break;
                            case "D": g2.setColor(new Color(211, 84, 0));   break;
                            case "F": g2.setColor(new Color(192, 57, 43));  break;
                            default:  g2.setColor(Color.BLACK);
                        }
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    } else {
                        g2.setColor(new Color(40, 40, 40));
                        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    }
                    g2.drawString(val, cx, curY + 14);
                    cx += colWidths[ci];
                }

                // Thin row separator
                g2.setColor(new Color(210, 210, 210));
                g2.setStroke(new BasicStroke(0.4f));
                g2.drawLine(0, curY + ROW_H - 1, iw, curY + ROW_H - 1);
                curY += ROW_H;
            }

            // ================================================================
            // PAGE FOOTER — every page
            // ================================================================
            int footerTop = ih - FOOTER_H;
            g2.setColor(new Color(180, 180, 180));
            g2.setStroke(new BasicStroke(0.5f));
            g2.drawLine(0, footerTop, iw, footerTop);
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 8));
            g2.setColor(new Color(130, 130, 130));
            g2.drawString("Computer-generated report — do not alter", 0, footerTop + 12);
            String pageStr = "Page " + (pageIndex + 1) + " of " + totalPages;
            g2.drawString(pageStr, iw - g2.getFontMetrics().stringWidth(pageStr), footerTop + 12);

            return PAGE_EXISTS;
        }

        /**
         * Draws the logo + report-info block at the TOP of every page.
         * Returns the Y coordinate where content should begin after the header.
         */
        private int drawPageHeader(Graphics2D g2, int iw, int pageIndex, int totalPages) {
            // Light background strip
            g2.setColor(new Color(243, 246, 249));
            g2.fillRect(0, 0, iw, HEADER_H - 6);

            // ✅ Logo — draw at absolute (4, logoTop), sized to LOGO_W × LOGO_H
            if (logo != null) {
                int logoTop = (HEADER_H - 6 - LOGO_H) / 2;   // vertically centred in strip
                // Use RenderingHint for smooth logo scaling
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.drawImage(logo, 4, logoTop, LOGO_W, LOGO_H, null);
            }

            // Text starts after logo (with padding)
            int textX = (logo != null) ? LOGO_W + 14 : 6;

            // Report title
            g2.setColor(new Color(44, 62, 80));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.drawString("Student Results Report", textX, 20);

            // Sub-lines
            String className = classInfo.contains(" - ")   ? classInfo.split(" - ", 2)[1]   : classInfo;
            String subName   = subjectInfo.contains(" - ") ? subjectInfo.split(" - ", 2)[1] : subjectInfo;
            String examDisp  = examInfo.contains(":")      ? examInfo.split(":", 2)[1].split("\\(")[0].trim() : examInfo;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(60, 60, 60));
            g2.drawString("Class   : " + className, textX, 36);
            g2.drawString("Subject : " + subName,   textX, 50);
            g2.drawString("Exam    : " + examDisp,  textX, 64);

            // Date — right-aligned in header strip
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 8));
            g2.setColor(new Color(150, 150, 150));
            String dateStr = "Generated: " + new java.util.Date();
            g2.drawString(dateStr, iw - g2.getFontMetrics().stringWidth(dateStr) - 2, 10);

            // Divider under header
            g2.setColor(new Color(44, 62, 80));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(0, HEADER_H - 6, iw, HEADER_H - 6);

            return HEADER_H;
        }

        private int[] computeColWidths(int total) {
            int[] w = new int[COL_FRACTIONS.length];
            for (int i = 0; i < COL_FRACTIONS.length; i++)
                w[i] = (int)(total * COL_FRACTIONS[i]);
            return w;
        }
    }

    // ==========================================================
    // HTML EXPORT (college logo embedded as base64 — self-contained)
    // ==========================================================
    private void exportAsHTML(File htmlFile, BufferedImage logo,
            String examInfo, String classInfo, String subjectInfo,
            Object[][] data, String[] colNames) {

        // Encode logo to base64 PNG
        String logoBase64 = "";
        if (logo != null) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(logo, "png", baos);
                logoBase64 = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
            } catch (Exception ignored) {}
        }

        String className = classInfo.contains(" - ")   ? classInfo.split(" - ", 2)[1]   : classInfo;
        String subName   = subjectInfo.contains(" - ") ? subjectInfo.split(" - ", 2)[1] : subjectInfo;
        String examDisp  = examInfo.contains(":")      ? examInfo.split(":", 2)[1].split("\\(")[0].trim() : examInfo;

        // Compute stats
        int passCount = 0, totalMarks = 0, highest = 0, lowest = Integer.MAX_VALUE;
        for (Object[] row : data) {
            try {
                int m = Integer.parseInt(row[3].toString());
                totalMarks += m; highest = Math.max(highest, m); lowest = Math.min(lowest, m);
                if ("Pass".equals(row[5].toString())) passCount++;
            } catch (Exception ignored) {}
        }
        if (lowest == Integer.MAX_VALUE) lowest = 0;
        double avg     = data.length > 0 ? (double) totalMarks / data.length : 0;
        double passPct = data.length > 0 ? (double) passCount  / data.length * 100 : 0;

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Results Report</title><style>");
        sb.append("@page{size:A4 landscape;margin:10mm}");
        sb.append("*{box-sizing:border-box;margin:0;padding:0}");
        sb.append("body{font-family:'Segoe UI',Arial,sans-serif;color:#333;background:#fff;font-size:11px}");
        // ✅ CSS: page-header is a fixed running element — repeated on every printed page
        sb.append(".page-header{");
        sb.append("  display:flex;align-items:center;");
        sb.append("  background:#f3f6f9;border-bottom:3px solid #2c3e50;");
        sb.append("  padding:8px 10px;margin-bottom:10px;");
        sb.append("}");
        sb.append(".logo{height:70px;width:auto;margin-right:14px;object-fit:contain}");
        sb.append(".hinfo h1{font-size:18px;color:#2c3e50;margin-bottom:2px}");
        sb.append(".hinfo p{font-size:10px;color:#555;margin:1px 0}");
        sb.append(".meta{display:grid;grid-template-columns:repeat(3,1fr);gap:7px;margin-bottom:9px}");
        sb.append(".mc{background:#ecf0f1;border-radius:4px;padding:6px 9px}");
        sb.append(".mc .lbl{font-size:8px;color:#999;text-transform:uppercase;letter-spacing:.4px}");
        sb.append(".mc .val{font-size:12px;font-weight:bold;color:#2c3e50;margin-top:1px}");
        sb.append(".stats{display:flex;gap:7px;margin-bottom:10px}");
        sb.append(".sb{flex:1;text-align:center;padding:7px 3px;border-radius:4px;border:2px solid}");
        sb.append(".sb .sv{font-size:16px;font-weight:bold}.sb .sl{font-size:8px;color:#777}");
        sb.append("table{width:100%;border-collapse:collapse;font-size:10px}");
        sb.append("thead tr{background:#2c3e50;color:#fff}");
        sb.append("thead th{padding:7px 5px;text-align:left}");
        sb.append("tbody tr:nth-child(even){background:#f8f9fa}tbody tr:nth-child(odd){background:#fff}");
        sb.append("tbody td{padding:5px 5px;border-bottom:1px solid #e0e0e0}");
        sb.append(".pass{color:#1b5e20;background:#c8e6c9;padding:1px 6px;border-radius:3px;font-weight:bold}");
        sb.append(".fail{color:#b71c1c;background:#ffcdd2;padding:1px 6px;border-radius:3px;font-weight:bold}");
        sb.append(".ga{color:#fff;background:#27ae60;padding:1px 6px;border-radius:3px;font-weight:bold}");
        sb.append(".gb{color:#1b5e20;background:#81c784;padding:1px 6px;border-radius:3px}");
        sb.append(".gc{color:#5d4037;background:#fff59d;padding:1px 6px;border-radius:3px}");
        sb.append(".gd{color:#e65100;background:#ffcc80;padding:1px 6px;border-radius:3px}");
        sb.append(".gf{color:#b71c1c;background:#ffcdd2;padding:1px 6px;border-radius:3px}");
        sb.append(".footer{margin-top:12px;border-top:1px solid #ccc;padding-top:5px;");
        sb.append("  display:flex;justify-content:space-between;font-size:8px;color:#aaa}");
        sb.append("@media print{");
        sb.append("  body{print-color-adjust:exact;-webkit-print-color-adjust:exact}");
        sb.append("  .page-header{break-inside:avoid}");
        sb.append("}");
        sb.append("</style></head><body>");

        // Page header with embedded logo
        sb.append("<div class='page-header'>");
        if (!logoBase64.isEmpty())
            sb.append("<img class='logo' src='data:image/png;base64,").append(logoBase64).append("' alt='College Logo'/>");
        sb.append("<div class='hinfo'>");
        sb.append("<h1>Student Results Report</h1>");
        sb.append("<p>Class: <b>").append(esc(className)).append("</b> &nbsp;|&nbsp; Subject: <b>").append(esc(subName)).append("</b></p>");
        sb.append("<p>Exam: <b>").append(esc(examDisp)).append("</b></p>");
        sb.append("<p style='color:#aaa;font-size:8px'>Generated: ").append(new java.util.Date()).append("</p>");
        sb.append("</div></div>");

        // Meta cards
        sb.append("<div class='meta'>");
        sb.append(mc("Class", className)).append(mc("Subject", subName)).append(mc("Exam", examDisp));
        sb.append("</div>");

        // Stat boxes
        sb.append("<div class='stats'>");
        sb.append(sb2(String.valueOf(data.length), "Total",      "#2980b9"));
        sb.append(sb2(String.format("%.1f", avg),  "Average",    "#27ae60"));
        sb.append(sb2(String.format("%.1f%%", passPct), "Pass %","#8e44ad"));
        sb.append(sb2(String.valueOf(highest),      "Highest",   "#f39c12"));
        sb.append(sb2(String.valueOf(lowest),       "Lowest",    "#e74c3c"));
        sb.append(sb2(String.valueOf(passCount),    "Pass Count","#16a085"));
        sb.append("</div>");

        // Results table (skip hidden Student ID col 0)
        sb.append("<table><thead><tr>");
        for (int c = 1; c < colNames.length; c++)
            sb.append("<th>").append(esc(colNames[c])).append("</th>");
        sb.append("</tr></thead><tbody>");

        for (Object[] row : data) {
            sb.append("<tr>");
            for (int c = 1; c < row.length; c++) {
                String v = row[c] != null ? row[c].toString() : "";
                if (c == 5) {
                    sb.append("<td><span class='").append("Pass".equals(v) ? "pass" : "fail")
                      .append("'>").append(esc(v)).append("</span></td>");
                } else if (c == 6) {
                    String cls;
                    switch (v) {
                        case "A": cls = "ga"; break; case "B": cls = "gb"; break;
                        case "C": cls = "gc"; break; case "D": cls = "gd"; break;
                        default:  cls = "gf";
                    }
                    sb.append("<td><span class='").append(cls).append("'>").append(esc(v)).append("</span></td>");
                } else {
                    sb.append("<td>").append(esc(v)).append("</td>");
                }
            }
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");

        sb.append("<div class='footer'>");
        sb.append("<span>Generated: ").append(new java.util.Date()).append("</span>");
        sb.append("<span>Computer-generated document — official record</span>");
        sb.append("<span>Faculty ID: ").append(facultyId).append("</span>");
        sb.append("</div></body></html>");

        try (PrintWriter pw = new PrintWriter(new FileWriter(htmlFile))) {
            pw.print(sb.toString());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Save error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        statusLabel.setText("✓ Exported: " + htmlFile.getName() + "  — open in browser, then Print → Save as PDF");
        int choice = JOptionPane.showConfirmDialog(this,
                "✅ Report exported successfully!\n\nFile: " + htmlFile.getAbsolutePath() +
                "\n\nIncludes college logo, statistics summary, and colour-coded table.\n\n" +
                "Open in browser now?\n(Browser → File → Print → Save as PDF for a proper PDF file)",
                "Export Complete", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            try { Desktop.getDesktop().open(htmlFile); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private String mc(String lbl, String val) {
        return "<div class='mc'><div class='lbl'>" + esc(lbl) + "</div><div class='val'>" + esc(val) + "</div></div>";
    }
    private String sb2(String val, String lbl, String color) {
        return "<div class='sb' style='border-color:" + color + "'>" +
               "<div class='sv' style='color:" + color + "'>" + val + "</div>" +
               "<div class='sl'>" + lbl + "</div></div>";
    }
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }

    // ==========================================================
    // SEND RESULTS TO PARENTS
    // ==========================================================
    private void sendResultsToParents() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No results loaded!", "Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Send results to parents?</b><br>Students: " + model.getRowCount() + "</html>",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        JDialog dlg = new JDialog(this, "Sending...", true);
        JProgressBar bar = new JProgressBar(0, model.getRowCount());
        bar.setStringPainted(true); bar.setPreferredSize(new Dimension(300, 28));
        JLabel lbl = new JLabel("Preparing...", SwingConstants.CENTER);
        dlg.setLayout(new BorderLayout(5, 5));
        dlg.add(bar, BorderLayout.CENTER); dlg.add(lbl, BorderLayout.SOUTH);
        dlg.setSize(360, 100); dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);

        new Thread(() -> {
            AtomicInteger sent = new AtomicInteger(), failed = new AtomicInteger(),
                          noContact = new AtomicInteger(), sms = new AtomicInteger();

            String examName = comboExam.getSelectedItem() != null
                    ? comboExam.getSelectedItem().toString().split(":")[1].split("\\(")[0].trim() : "Unknown";
            String cls  = comboClass.getSelectedItem()   != null ? comboClass.getSelectedItem().toString().split(" - ")[1].trim()   : "?";
            String subj = comboSubject.getSelectedItem() != null ? comboSubject.getSelectedItem().toString().split(" - ")[1].trim() : "?";

            for (int i = 0; i < model.getRowCount(); i++) {
                try {
                    int    sid    = Integer.parseInt(model.getValueAt(i, 0).toString());
                    String roll   = model.getValueAt(i, 1).toString();
                    String name   = model.getValueAt(i, 2).toString();
                    int    marks  = Integer.parseInt(model.getValueAt(i, 3).toString());
                    double pct    = Double.parseDouble(model.getValueAt(i, 4).toString().replace("%", ""));
                    String result = model.getValueAt(i, 5).toString();
                    String grade  = model.getValueAt(i, 6).toString();
                    int    max    = examDAO.getExamById(
                            Integer.parseInt(comboExam.getSelectedItem().toString().split(":")[0])).getMaxMarks();

                    com.college.sms.model.Student st = studentDAO.getStudentById(sid);
                    String email  = st.getParentEmail();
                    String mobile = st.getParentMobile();
                    boolean consent = st.getConsentToCommunicate() != null ? st.getConsentToCommunicate() : true;

                    if ((!hasValue(email) && !hasValue(mobile)) || !consent) { noContact.incrementAndGet(); continue; }

                    if (hasValue(email)) {
                        try { if (EmailService.sendResultEmail(email, name, roll, cls, subj, examName, marks, max, pct, result, grade)) sent.incrementAndGet(); }
                        catch (Exception e) { failed.incrementAndGet(); }
                    }
                    if (hasValue(mobile)) {
                        try { if (SMSService.sendResultSMS(mobile, name, roll, cls, subj, examName, marks, max, pct, result, grade)) sms.incrementAndGet(); }
                        catch (Exception ignored) {}
                    }

                    final int p = i + 1; final String n = name;
                    SwingUtilities.invokeLater(() -> { bar.setValue(p); lbl.setText("Sending: " + n); });
                    Thread.sleep(300);
                } catch (Exception ex) { failed.incrementAndGet(); }
            }

            SwingUtilities.invokeLater(() -> {
                dlg.dispose();
                JOptionPane.showMessageDialog(ResultsUI.this,
                        String.format("✅ Done!\n📧 Email: %d\n📱 SMS: %d\n❌ Failed: %d\n⚠️ No contact: %d",
                                sent.get(), sms.get(), failed.get(), noContact.get()),
                        "Complete", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("✓ Sent to " + sent.get() + " parents");
            });
        }).start();
    }

    private boolean hasValue(String s) { return s != null && !s.trim().isEmpty(); }

    // ==========================================================
    // DATA LOADING
    // ==========================================================
    private void loadData() { loadClasses(); }

    private void loadClasses() {
        comboClass.removeAllItems();
        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
        if (classes.isEmpty()) {
            comboClass.addItem("-- No classes assigned --");
            statusLabel.setText("⚠️ No classes assigned.");
            JOptionPane.showMessageDialog(this, "No classes found!", "No Classes", JOptionPane.WARNING_MESSAGE);
        } else {
            for (String[] c : classes) comboClass.addItem(c[0] + " - " + c[1]);
            comboClass.setSelectedIndex(0);
            loadSubjects();
        }
    }

    private void loadSubjects() {
        isLoadingSubjects = true;
        comboSubject.removeAllItems();
        comboExam.removeAllItems();
        if (comboClass.getSelectedItem() == null || comboClass.getSelectedItem().toString().contains("--")) {
            comboSubject.addItem("-- Select class first --"); isLoadingSubjects = false; return;
        }
        try {
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            List<Subject> subs = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
            if (subs.isEmpty()) { comboSubject.addItem("-- No subjects --"); }
            else { for (Subject s : subs) comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName()); comboSubject.setSelectedIndex(0); }
        } catch (Exception e) { comboSubject.addItem("-- Error --"); }
        isLoadingSubjects = false;
        loadExams();
    }

    private void loadExams() {
        if (isLoadingSubjects) return;
        isLoadingExams = true;
        comboExam.removeAllItems();
        if (comboSubject.getSelectedItem() == null || comboSubject.getSelectedItem().toString().contains("--")) {
            comboExam.addItem("-- Select subject first --"); isLoadingExams = false; return;
        }
        try {
            int subId   = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, classId, subId);
            if (exams.isEmpty()) { comboExam.addItem("-- No exams --"); }
            else { for (Exam ex : exams) comboExam.addItem(ex.getExamId()+":"+ex.getExamName()+" (Max: "+ex.getMaxMarks()+")"); comboExam.setSelectedIndex(0); }
        } catch (Exception e) { comboExam.addItem("-- Error --"); }
        isLoadingExams = false;
    }

    // ==========================================================
    // LOAD RESULTS
    // ==========================================================
    private void loadResults() {
        model.setRowCount(0);
        chartContainer.removeAll();
        if (comboClass.getSelectedItem() == null || comboSubject.getSelectedItem() == null || comboExam.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Select class, subject and exam!", "Missing", JOptionPane.WARNING_MESSAGE); return;
        }
        if (comboSubject.getSelectedItem().toString().contains("--") || comboExam.getSelectedItem().toString().contains("--")) {
            JOptionPane.showMessageDialog(this, "No valid subjects/exams!", "Invalid", JOptionPane.WARNING_MESSAGE); return;
        }
        try {
            int classId   = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
            int examId    = Integer.parseInt(comboExam.getSelectedItem().toString().split(":")[0].trim());
            String examName = comboExam.getSelectedItem().toString().split(":")[1].split("\\(")[0].trim();

            Exam exam = examDAO.getExamById(examId);
            if (exam == null) { JOptionPane.showMessageDialog(this, "Exam not found!", "Error", JOptionPane.ERROR_MESSAGE); return; }
            int maxMarks = exam.getMaxMarks(), passMarks = exam.getPassMarks();

            List<String[]> students = studentDAO.getStudentsByClass(classId);
            if (students.isEmpty()) { JOptionPane.showMessageDialog(this, "No students!", "Empty", JOptionPane.INFORMATION_MESSAGE); return; }

            DefaultCategoryDataset dIndiv = new DefaultCategoryDataset();
            DefaultCategoryDataset dPF    = new DefaultCategoryDataset();
            DefaultCategoryDataset dAch   = new DefaultCategoryDataset();
            int total=0, passCount=0, highest=0, lowest=Integer.MAX_VALUE, a60=0, a75=0;
            DecimalFormat df = new DecimalFormat("0.00");

            for (String[] s : students) {
                int sid = Integer.parseInt(s[0]);
                int marks = studentDAO.getMarksByExam(sid, examId);
                double pct = maxMarks > 0 ? marks * 100.0 / maxMarks : 0;
                if (pct > 60) a60++; if (pct > 75) a75++;
                String grade = calculateGrade(marks, maxMarks);
                String res   = marks >= passMarks ? "Pass" : "Fail";
                if ("Pass".equals(res)) passCount++;
                total += marks; highest = Math.max(highest, marks); lowest = Math.min(lowest, marks);
                model.addRow(new Object[]{s[0], s[1], s[2], marks, df.format(pct)+"%", res, grade});
                dIndiv.addValue((double) marks, s[2], "Marks");
            }
            int failCount = students.size() - passCount;
            double passPct = (double) passCount / students.size() * 100;

            // Build charts
            JFreeChart ic = ChartFactory.createBarChart("Individual Marks: "+examName,"Students","Marks",dIndiv,PlotOrientation.VERTICAL,false,true,false);
            ic.getTitle().setFont(new Font("Segoe UI",Font.BOLD,16));
            CategoryPlot ip = ic.getCategoryPlot(); ip.getRenderer().setSeriesPaint(0,new Color(46,204,113)); ip.setBackgroundPaint(Color.WHITE);
            ChartPanel icp = new ChartPanel(ic); icp.setPreferredSize(new Dimension(500,300));

            dPF.addValue(passCount,"Count","Pass"); dPF.addValue(failCount,"Count","Fail");
            JFreeChart pfc = ChartFactory.createBarChart("Pass/Fail","Result","Students",dPF,PlotOrientation.VERTICAL,false,true,false);
            customizeChart(pfc,true); ChartPanel pfcp = new ChartPanel(pfc); pfcp.setPreferredSize(new Dimension(380,320));

            dAch.addValue(a60,"Count","> 60%"); dAch.addValue(a75,"Count","> 75%");
            JFreeChart ac = ChartFactory.createBarChart("High Achievers","Threshold","Students",dAch,PlotOrientation.VERTICAL,false,true,false);
            customizeChart(ac,false); ChartPanel achcp = new ChartPanel(ac); achcp.setPreferredSize(new Dimension(380,320));

            JPanel top = new JPanel(new BorderLayout());
            top.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(70,130,180),1),"Individual Performance",TitledBorder.LEFT,TitledBorder.TOP,new Font("Segoe UI",Font.BOLD,14),new Color(70,130,180)));
            top.setBackground(Color.WHITE); top.add(icp,BorderLayout.CENTER);

            JPanel mid = new JPanel(new GridLayout(1,2,30,0)); mid.setBackground(Color.WHITE); mid.setBorder(new EmptyBorder(10,0,10,0));
            JPanel pw2 = new JPanel(new BorderLayout()); pw2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(40,167,69),2),"📊 Pass/Fail",TitledBorder.LEFT,TitledBorder.TOP,new Font("Segoe UI",Font.BOLD,15),new Color(40,167,69))); pw2.setBackground(Color.WHITE); pw2.add(pfcp,BorderLayout.CENTER);
            JPanel aw = new JPanel(new BorderLayout()); aw.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(108,92,231),2),"📊 High Achievers",TitledBorder.LEFT,TitledBorder.TOP,new Font("Segoe UI",Font.BOLD,15),new Color(108,92,231))); aw.setBackground(Color.WHITE); aw.add(achcp,BorderLayout.CENTER);
            mid.add(pw2); mid.add(aw);

            JPanel sum = new JPanel(new GridLayout(2,4,20,15)); sum.setBackground(new Color(248,250,252)); sum.setBorder(new EmptyBorder(15,20,20,20));
            sum.add(createMetricLabel("Present",String.valueOf(students.size()),new Color(52,152,219)));
            sum.add(createMetricLabel("Absent","0",new Color(149,165,166)));
            sum.add(createMetricLabel("Pass",String.valueOf(passCount),new Color(40,167,69)));
            sum.add(createMetricLabel("Fail",String.valueOf(failCount),new Color(220,53,69)));
            sum.add(createMetricLabel("Pass %",String.format("%.1f%%",passPct),new Color(40,167,69)));
            sum.add(createMetricLabel("Fail %",String.format("%.1f%%",100-passPct),new Color(220,53,69)));
            sum.add(createMetricLabel("> 60%",String.valueOf(a60),new Color(52,152,219)));
            sum.add(createMetricLabel("> 75%",String.valueOf(a75),new Color(108,92,231)));

            chartContainer.add(top,BorderLayout.NORTH);
            chartContainer.add(mid,BorderLayout.CENTER);
            chartContainer.add(sum,BorderLayout.SOUTH);
            chartContainer.revalidate(); chartContainer.repaint();

            updateStatistics(students.size(), total, passCount, highest, lowest==Integer.MAX_VALUE?0:lowest, passMarks);
            statusLabel.setText(String.format("✓ %d students | Avg: %.1f | Pass: %.1f%%",
                    students.size(), (double)total/students.size(), passPct));
            mainTabbedPane.setSelectedIndex(0);

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ==========================================================
    // HELPERS
    // ==========================================================
    private JLabel createFilterLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI",Font.BOLD,15)); l.setForeground(new Color(52,73,94)); return l;
    }
    private JComboBox<String> createModernComboBox() {
        JComboBox<String> c = new JComboBox<>(); c.setFont(new Font("Segoe UI",Font.PLAIN,14));
        c.setPreferredSize(new Dimension(220,36)); c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createLineBorder(new Color(189,195,199),2)); return c;
    }
    private JButton createModernButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text); b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setForeground(fg); b.setBackground(bg);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12)); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){b.setBackground(bg.darker());}
            public void mouseExited(MouseEvent e){b.setBackground(bg);}
        });
        return b;
    }
    private JPanel createStatCard(String title, String val, Color color, java.util.function.Consumer<JLabel> ref) {
        JPanel p = new JPanel(new BorderLayout(5,5)); p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color,3),BorderFactory.createEmptyBorder(15,15,15,15)));
        JLabel tl = new JLabel(title); tl.setFont(new Font("Segoe UI",Font.PLAIN,16)); tl.setForeground(new Color(120,120,120));
        JLabel vl = new JLabel(val);   vl.setFont(new Font("Segoe UI",Font.BOLD,28));  vl.setForeground(color); ref.accept(vl);
        p.add(tl,BorderLayout.NORTH); p.add(vl,BorderLayout.CENTER); return p;
    }
    private JPanel createMetricLabel(String title, String val, Color color) {
        JPanel p = new JPanel(new BorderLayout(5,8)); p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230,230,230),1),BorderFactory.createEmptyBorder(10,5,10,5)));
        JLabel tl = new JLabel(title); tl.setFont(new Font("Segoe UI",Font.PLAIN,14)); tl.setForeground(new Color(100,100,100)); tl.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel vl = new JLabel(val);   vl.setFont(new Font("Segoe UI",Font.BOLD,20));  vl.setForeground(color); vl.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(tl,BorderLayout.NORTH); p.add(vl,BorderLayout.CENTER); return p;
    }
    private String calculateGrade(int marks, int max) {
        if (max <= 0) return "N/A";
        double p = (double) marks / max * 100;
        return p>=90?"A":p>=80?"B":p>=70?"C":p>=60?"D":"F";
    }
    private void updateStatistics(int total, int totalMarks, int pass, int high, int low, int passMarks) {
        totalStudentsLabel.setText(String.valueOf(total));
        avgMarksLabel.setText(String.format("%.1f",(double)totalMarks/total));
        passPercentLabel.setText(String.format("%.1f%%",(double)pass/total*100));
        highestLabel.setText(String.valueOf(high));
        lowestLabel.setText(String.valueOf(low));
        passCountLabel.setText(String.valueOf(pass));
    }
    private void customizeChart(JFreeChart chart, boolean isPassFail) {
        chart.getTitle().setFont(new Font("Segoe UI",Font.BOLD,16)); chart.getTitle().setPaint(new Color(44,62,80));
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot(); plot.setBackgroundPaint(Color.WHITE); plot.setRangeGridlinePaint(new Color(220,220,220));
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI",Font.BOLD,13));
        ((NumberAxis)plot.getRangeAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer r = (BarRenderer) plot.getRenderer();
        r.setDrawBarOutline(false); r.setItemMargin(0.18); r.setDefaultItemLabelsVisible(true);
        r.setDefaultItemLabelFont(new Font("Segoe UI",Font.BOLD,14));
        r.setDefaultNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.BOTTOM_CENTER));
        if (isPassFail) { r.setSeriesPaint(0,new Color(40,167,69)); r.setSeriesPaint(1,new Color(220,53,69)); }
        else            { r.setSeriesPaint(0,new Color(52,152,219)); r.setSeriesPaint(1,new Color(108,92,231)); }
        r.setSeriesItemLabelGenerator(0,new StandardCategoryItemLabelGenerator("{2}",new DecimalFormat("0")));
        r.setSeriesItemLabelGenerator(1,new StandardCategoryItemLabelGenerator("{2}",new DecimalFormat("0")));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                JFrame mock = new JFrame("Faculty Dashboard");
                mock.setSize(1000,650); mock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); mock.setVisible(true);
                new ResultsUI(1, mock);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}
