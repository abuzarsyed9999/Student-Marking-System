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
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class SubjectPerformanceUI extends JFrame {

    private JComboBox<String> comboClass, comboSubject, comboFailedExam;
    private JTable performanceTable, failedStudentsTable, failedByExamTable;
    private DefaultTableModel tableModel, failedTableModel, failedByExamModel;
    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;
    private ExamDAO examDAO;
    private StudentDAO studentDAO;
    private int facultyId;
    private JFrame previousUI;
    private JLabel statusLabel;
    private boolean isLoadingSubjects = false;
    private List<Exam> currentExams = new ArrayList<>();
    private JTabbedPane mainTabbedPane;
    private JPanel chartsPanel;
    private List<StudentPerformance> allPerformances = new ArrayList<>();

    // Statistics labels
    private JLabel totalStudentsLabel, avgPercentLabel, highestPercentLabel, passCountLabel, failCountLabel;

    public SubjectPerformanceUI(int facultyId, JFrame previousUI) {
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
        setTitle("📊 Subject Performance Analysis | Faculty ID: " + facultyId);
        setSize(1250, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(248, 250, 252));

        // ===== TOP SECTION WRAPPER =====
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(new EmptyBorder(0, 25, 0, 25));

        JLabel titleLabel = new JLabel("🎓 Subject-wise Performance Analysis");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton btnBack = createModernButton("⇦ Back to Results", new Color(30, 136, 56), Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnBack.setPreferredSize(new Dimension(210, 45));
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
            "🔍 Select Analysis Parameters",
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

        JButton btnLoad = createModernButton("✨ Analyze Performance", new Color(142, 68, 173), Color.WHITE);
        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLoad.setPreferredSize(new Dimension(200, 42));
        btnLoad.addActionListener(e -> loadPerformanceData());
        filterPanel.add(btnLoad);
        topWrapper.add(filterPanel);

        add(topWrapper, BorderLayout.NORTH);

        // ===== MAIN CONTENT (TAB BED) =====
        mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mainTabbedPane.setBackground(new Color(248, 250, 252));
        mainTabbedPane.setForeground(new Color(41, 128, 185));

        // Tab 1: All Students Performance (Cumulative - KEPT AS REQUESTED)
        mainTabbedPane.addTab("📋 Cumulative Performance", createPerformanceTableTab());

        // Tab 2: Failed Students Report (Cumulative failures)
        mainTabbedPane.addTab("❌ Overall Failed", createFailedStudentsTab());

        // Tab 3: NEW - Failed by Individual Exam (YOUR REQUEST)
        mainTabbedPane.addTab("⚠️ Failed by Exam", createFailedByExamTab());

        // Tab 4: Visual Analytics
        chartsPanel = new JPanel(new BorderLayout(25, 25));
        chartsPanel.setBackground(Color.WHITE);
        chartsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainTabbedPane.addTab("📈 Visual Analytics", chartsPanel);

        // Tab 5: Statistics Summary
        mainTabbedPane.addTab("🔢 Performance Metrics", createStatisticsTab());

        add(mainTabbedPane, BorderLayout.CENTER);

        // ===== STATUS BAR =====
        statusLabel = new JLabel("Ready to analyze subject performance. Select class and subject, then click 'Analyze Performance'.");
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
                // Wait for explicit button click
            }
        });
    }

    private JPanel createPerformanceTableTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(new String[]{"#", "Roll No", "Student Name"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        performanceTable = new JTable(tableModel);
        performanceTable.setRowHeight(38);
        performanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        performanceTable.setGridColor(new Color(236, 240, 241));
        performanceTable.setSelectionBackground(new Color(52, 152, 219));
        performanceTable.setSelectionForeground(Color.WHITE);
        performanceTable.setIntercellSpacing(new Dimension(0, 2));

        JTableHeader header = performanceTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));

        // Color-coded rendering
        performanceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
                    
                    // Color-code percentage column (second last)
                    if (column == tableModel.getColumnCount() - 2) {
                        try {
                            String pctStr = value.toString().replace("%", "").trim();
                            double pct = Double.parseDouble(pctStr);
                            if (pct >= 90) {
                                c.setBackground(new Color(46, 204, 113, 40));
                                c.setForeground(new Color(27, 94, 32));
                                c.setFont(c.getFont().deriveFont(Font.BOLD));
                            } else if (pct >= 75) {
                                c.setBackground(new Color(52, 152, 219, 30));
                                c.setForeground(new Color(40, 116, 166));
                            } else if (pct >= 60) {
                                c.setBackground(new Color(241, 196, 15, 30));
                                c.setForeground(new Color(146, 112, 6));
                            } else if (pct >= 40) {
                                c.setBackground(new Color(243, 156, 18, 30));
                                c.setForeground(new Color(165, 82, 0));
                            } else {
                                c.setBackground(new Color(231, 76, 60, 30));
                                c.setForeground(new Color(165, 38, 20));
                            }
                        } catch (Exception ignored) {}
                    }
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(performanceTable);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "📊 Cumulative Performance Across All Exams",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(52, 152, 219)
        ));
        panel.add(scroll, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        actionPanel.setBackground(Color.WHITE);
        
        JButton btnExport = createModernButton("📤 Export Full Report", new Color(155, 89, 182), Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExport.setPreferredSize(new Dimension(180, 40));
        btnExport.addActionListener(e -> exportFullReport());
        actionPanel.add(btnExport);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFailedStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        failedTableModel = new DefaultTableModel(
            new String[]{"#", "Roll No", "Student Name", "Total Obtained", "Total Max", "Percentage", "Status"}, 0
        ) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        failedStudentsTable = new JTable(failedTableModel);
        failedStudentsTable.setRowHeight(38);
        failedStudentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        failedStudentsTable.setGridColor(new Color(236, 240, 241));
        failedStudentsTable.setSelectionBackground(new Color(231, 76, 60));
        failedStudentsTable.setSelectionForeground(Color.WHITE);
        failedStudentsTable.setIntercellSpacing(new Dimension(0, 2));

        JTableHeader header = failedStudentsTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(231, 76, 60));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));

        // Red highlight for failed students
        failedStudentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(255, 245, 245) : new Color(255, 250, 250));
                    
                    // Highlight percentage column in red
                    if (column == 5) {
                        try {
                            String pctStr = value.toString().replace("%", "").trim();
                            double pct = Double.parseDouble(pctStr);
                            if (pct < 40) {
                                c.setBackground(new Color(255, 235, 235));
                                c.setForeground(new Color(165, 38, 20));
                                c.setFont(c.getFont().deriveFont(Font.BOLD));
                            }
                        } catch (Exception ignored) {}
                    }
                    
                    // Highlight Status column
                    if (column == 6 && "FAILED".equals(value)) {
                        c.setBackground(new Color(255, 235, 235));
                        c.setForeground(new Color(165, 38, 20));
                        c.setFont(c.getFont().deriveFont(Font.BOLD, 15f));
                    }
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(failedStudentsTable);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
            "❌ Students Below 40% Pass Threshold (Cumulative)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(231, 76, 60)
        ));
        panel.add(scroll, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        actionPanel.setBackground(Color.WHITE);
        
        JButton btnExportFailed = createModernButton("📤 Export Failed Report", new Color(231, 76, 60), Color.WHITE);
        btnExportFailed.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExportFailed.setPreferredSize(new Dimension(200, 40));
        btnExportFailed.addActionListener(e -> exportFailedReport());
        actionPanel.add(btnExportFailed);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ✅ NEW TAB: Failed Students by Individual Exam (YOUR REQUEST)
    private JPanel createFailedByExamTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Exam selection panel
        JPanel examPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        examPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
            "Select Exam to View Failed Students",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 15),
            new Color(231, 76, 60)
        ));
        examPanel.setBackground(Color.WHITE);
        examPanel.setPreferredSize(new Dimension(0, 70));

        examPanel.add(new JLabel("📝 Exam:"));
        comboFailedExam = new JComboBox<>();
        comboFailedExam.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comboFailedExam.setPreferredSize(new Dimension(300, 40));
        comboFailedExam.setBackground(Color.WHITE);
        comboFailedExam.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        examPanel.add(comboFailedExam);

        JButton btnLoadFailed = createModernButton("🔍 Load Failed Students", new Color(231, 76, 60), Color.WHITE);
        btnLoadFailed.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLoadFailed.setPreferredSize(new Dimension(200, 40));
        btnLoadFailed.addActionListener(e -> loadFailedByExam());
        examPanel.add(btnLoadFailed);

        panel.add(examPanel, BorderLayout.NORTH);

        // Failed students table for selected exam
        failedByExamModel = new DefaultTableModel(
            new String[]{"#", "Roll No", "Student Name", "Marks Obtained", "Pass Marks", "Max Marks", "Result"}, 0
        ) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        failedByExamTable = new JTable(failedByExamModel);
        failedByExamTable.setRowHeight(38);
        failedByExamTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        failedByExamTable.setGridColor(new Color(236, 240, 241));
        failedByExamTable.setSelectionBackground(new Color(231, 76, 60));
        failedByExamTable.setSelectionForeground(Color.WHITE);
        failedByExamTable.setIntercellSpacing(new Dimension(0, 2));

        JTableHeader header = failedByExamTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(231, 76, 60));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));

        // Red highlight for failed students
        failedByExamTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(255, 245, 245) : new Color(255, 250, 250));
                    
                    // Highlight Marks Obtained column in red if failed
                    if (column == 3) {
                        try {
                            int marks = Integer.parseInt(value.toString());
                            int passMarks = Integer.parseInt(t.getValueAt(row, 4).toString());
                            if (marks < passMarks) {
                                c.setBackground(new Color(255, 235, 235));
                                c.setForeground(new Color(165, 38, 20));
                                c.setFont(c.getFont().deriveFont(Font.BOLD));
                            }
                        } catch (Exception ignored) {}
                    }
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(failedByExamTable);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
            "⚠️ Students Who Failed Selected Exam",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(231, 76, 60)
        ));
        panel.add(scroll, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        actionPanel.setBackground(Color.WHITE);
        
        JButton btnExportExamFailed = createModernButton("📤 Export Failed for Exam", new Color(220, 53, 69), Color.WHITE);
        btnExportExamFailed.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExportExamFailed.setPreferredSize(new Dimension(220, 40));
        btnExportExamFailed.addActionListener(e -> exportFailedByExam());
        actionPanel.add(btnExportExamFailed);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStatisticsTab() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 25, 25));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        panel.add(createStatCard("🎓 Total Students", "0", new Color(52, 152, 219), lbl -> totalStudentsLabel = lbl));
        panel.add(createStatCard("📊 Avg Percentage", "0.0%", new Color(46, 204, 113), lbl -> avgPercentLabel = lbl));
        panel.add(createStatCard("⭐ Highest Score", "0.0%", new Color(241, 196, 15), lbl -> highestPercentLabel = lbl));
        panel.add(createStatCard("✅ Pass Count", "0", new Color(39, 174, 96), lbl -> passCountLabel = lbl));
        panel.add(createStatCard("❌ Fail Count", "0", new Color(231, 76, 60), lbl -> failCountLabel = lbl));
        panel.add(createStatCard("📈 Pass %", "0.0%", new Color(40, 167, 69), lbl -> {}));
        panel.add(createStatCard("📉 Fail %", "0.0%", new Color(220, 53, 69), lbl -> {}));
        panel.add(createStatCard("🎯 Pass Threshold", "40%", new Color(142, 68, 173), lbl -> {}));
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            "🎯 Key Performance Indicators",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 18),
            new Color(41, 128, 185)
        ));
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
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

    private JPanel createStatCard(String title, String value, Color color, java.util.function.Consumer<JLabel> labelRef) {
        JPanel card = new JPanel(new BorderLayout(10, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(240, 140));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        labelRef.accept(valueLabel);
        
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

    private void loadPerformanceData() {
        tableModel.setRowCount(0);
        failedTableModel.setRowCount(0);
        failedByExamModel.setRowCount(0);
        comboFailedExam.removeAllItems();
        chartsPanel.removeAll();
        chartsPanel.add(new JLabel("📊 Generating professional analytics...", SwingConstants.CENTER), BorderLayout.CENTER);
        chartsPanel.revalidate(); 
        chartsPanel.repaint();
        
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

            currentExams = examDAO.getExamsByFacultyClassSubject(facultyId, classId, subjectId);
            if (currentExams.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No exams found for this subject!\nCreate exams in Exam Management first.",
                    "No Exams Available", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("⚠️ No exams found for " + subjectName);
                return;
            }
            
            // Populate exam dropdown for "Failed by Exam" tab
            for (Exam exam : currentExams) {
                comboFailedExam.addItem(exam.getExamId() + " - " + exam.getExamName() + " (Pass: " + exam.getPassMarks() + "/" + exam.getMaxMarks() + ")");
            }
            if (comboFailedExam.getItemCount() > 0) {
                comboFailedExam.setSelectedIndex(0);
            }
            
            List<String[]> students = studentDAO.getStudentsByClass(classId);
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No students found in this class!", "Empty Class", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("⚠️ No students found in " + className);
                return;
            }
            
            allPerformances.clear();
            DecimalFormat pctFormat = new DecimalFormat("0.0");
            DecimalFormat avgFormat = new DecimalFormat("0.00");
            int passCount = 0;
            int failCount = 0;
            double totalPercent = 0;
            double highestPercent = 0;
            
            // Calculate cumulative performance for each student
            for (String[] student : students) {
                int studentId = Integer.parseInt(student[0]);
                String rollNo = student[1];
                String name = student[2];
                
                StudentPerformance sp = new StudentPerformance(studentId, rollNo, name);
                
                for (Exam exam : currentExams) {
                    int marks = studentDAO.getMarksByExam(studentId, exam.getExamId());
                    sp.addExamMark(exam.getExamId(), marks, exam.getMaxMarks());
                }
                
                sp.calculateTotals();
                allPerformances.add(sp);
                
                totalPercent += sp.getPercentage();
                if (sp.getPercentage() > highestPercent) highestPercent = sp.getPercentage();
                if (sp.getPercentage() >= 40) passCount++;
                else failCount++;
            }
            
            // Build table model for cumulative performance
            String[] columnNames = buildColumnNames(currentExams);
            tableModel.setColumnIdentifiers(columnNames);
            
            for (int i = 0; i < allPerformances.size(); i++) {
                StudentPerformance sp = allPerformances.get(i);
                List<Object> rowData = new ArrayList<>();
                rowData.add(i + 1);
                rowData.add(sp.getRollNo());
                rowData.add(sp.getStudentName());
                
                for (Exam exam : currentExams) {
                    Integer marks = sp.getExamMarks().get(exam.getExamId());
                    rowData.add(marks != null ? marks.toString() : "0");
                }
                
                rowData.add(sp.getTotalObtained());
                rowData.add(sp.getTotalMax());
                rowData.add(pctFormat.format(sp.getPercentage()) + "%");
                rowData.add(avgFormat.format(sp.getAvgMarks()));
                
                tableModel.addRow(rowData.toArray());
            }
            
            // Build table model for cumulative failed students ONLY
            failedTableModel.setRowCount(0);
            int failIndex = 1;
            for (StudentPerformance sp : allPerformances) {
                if (sp.getPercentage() < 40) { // Failed students (below 40% cumulative)
                    failedTableModel.addRow(new Object[]{
                        failIndex++,
                        sp.getRollNo(),
                        sp.getStudentName(),
                        sp.getTotalObtained(),
                        sp.getTotalMax(),
                        pctFormat.format(sp.getPercentage()) + "%",
                        "FAILED"
                    });
                }
            }
            
            // Update statistics
            int totalStudents = allPerformances.size();
            double avgPercent = totalPercent / totalStudents;
            double passPercent = (passCount * 100.0) / totalStudents;
            double failPercent = (failCount * 100.0) / totalStudents;
            
            totalStudentsLabel.setText(String.valueOf(totalStudents));
            avgPercentLabel.setText(pctFormat.format(avgPercent) + "%");
            highestPercentLabel.setText(pctFormat.format(highestPercent) + "%");
            passCountLabel.setText(String.valueOf(passCount));
            failCountLabel.setText(String.valueOf(failCount));
            
            // Update statistics tab labels (pass/fail percentages)
            updateStatisticsTab(passPercent, failPercent);
            
            // Generate professional charts
            generateProfessionalCharts(allPerformances, className, subjectName, currentExams, passCount, failCount);
            
            statusLabel.setText(String.format("✓ Analyzed %d students for %s | Pass: %.1f%% (%d) | Fail: %.1f%% (%d)", 
                totalStudents, subjectName, passPercent, passCount, failPercent, failCount));
            
            // Auto-size columns
            for (int i = 0; i < performanceTable.getColumnCount(); i++) {
                performanceTable.getColumnModel().getColumn(i).setPreferredWidth(
                    i < 3 ? 110 : (i >= tableModel.getColumnCount() - 2 ? 90 : 85)
                );
            }
            
            // Auto-size failed students table columns
            for (int i = 0; i < failedStudentsTable.getColumnCount(); i++) {
                failedStudentsTable.getColumnModel().getColumn(i).setPreferredWidth(
                    i < 3 ? 110 : (i == 6 ? 100 : 90)
                );
            }
            
            // Switch to Charts tab to show beautiful analytics
            mainTabbedPane.setSelectedIndex(3);
            
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading performance data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading performance data:\n" + e.getMessage(), 
                "Load Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateStatisticsTab(double passPercent, double failPercent) {
        try {
            Component[] statsComponents = ((JPanel)mainTabbedPane.getComponentAt(4)).getComponents();
            if (statsComponents.length > 0 && statsComponents[0] instanceof JPanel) {
                JPanel statsPanel = (JPanel)statsComponents[0];
                if (statsPanel.getComponentCount() >= 6) {
                    ((JLabel)((JPanel)statsPanel.getComponent(5)).getComponent(1)).setText(String.format("%.1f%%", passPercent));
                    ((JLabel)((JPanel)statsPanel.getComponent(6)).getComponent(1)).setText(String.format("%.1f%%", failPercent));
                }
            }
        } catch (Exception e) {
            // Ignore if tab structure changes
        }
    }

    // ✅ NEW METHOD: Load failed students for selected exam
    private void loadFailedByExam() {
        failedByExamModel.setRowCount(0);
        
        if (comboFailedExam.getItemCount() == 0 || comboFailedExam.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "No exams available to analyze!", "No Exams", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Parse exam ID from selected item
            String selectedItem = comboFailedExam.getSelectedItem().toString();
            int examId = Integer.parseInt(selectedItem.split(" - ")[0].trim());
            
            // Find exam details
            Exam selectedExam = null;
            for (Exam exam : currentExams) {
                if (exam.getExamId() == examId) {
                    selectedExam = exam;
                    break;
                }
            }
            
            if (selectedExam == null) {
                JOptionPane.showMessageDialog(this, "Selected exam not found in current subject!", "Exam Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get class ID from current selection
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            
            // Get all students in class
            List<String[]> students = studentDAO.getStudentsByClass(classId);
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No students found in this class!", "Empty Class", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Filter students who failed THIS exam
            int failCount = 0;
            DecimalFormat pctFormat = new DecimalFormat("0.0");
            
            for (int i = 0; i < students.size(); i++) {
                String[] student = students.get(i);
                int studentId = Integer.parseInt(student[0]);
                String rollNo = student[1];
                String name = student[2];
                
                // Get marks for THIS exam only
                int marks = studentDAO.getMarksByExam(studentId, examId);
                
                // Check if student failed THIS exam (marks < pass marks)
                if (marks < selectedExam.getPassMarks()) {
                    failCount++;
                    
                    // Calculate percentage for THIS exam only
                    double percentage = (selectedExam.getMaxMarks() > 0) ? 
                        (marks * 100.0 / selectedExam.getMaxMarks()) : 0.0;
                    
                    failedByExamModel.addRow(new Object[]{
                        failCount,
                        rollNo,
                        name,
                        marks,
                        selectedExam.getPassMarks(),
                        selectedExam.getMaxMarks(),
                        "FAILED (" + pctFormat.format(percentage) + "%)"
                    });
                }
            }
            
            // Update status
            if (failCount == 0) {
                statusLabel.setText("✅ No students failed " + selectedExam.getExamName() + " exam!");
                JOptionPane.showMessageDialog(this, 
                    "🎉 Excellent! All students passed " + selectedExam.getExamName() + " exam!",
                    "No Failures", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLabel.setText(String.format("⚠️ %d student(s) failed %s exam (Pass: %d/%d)", 
                    failCount, selectedExam.getExamName(), selectedExam.getPassMarks(), selectedExam.getMaxMarks()));
            }
            
            // Auto-size columns
            for (int i = 0; i < failedByExamTable.getColumnCount(); i++) {
                failedByExamTable.getColumnModel().getColumn(i).setPreferredWidth(
                    i < 3 ? 110 : (i == 6 ? 140 : 90)
                );
            }
            
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading failed students: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading failed students:\n" + e.getMessage(), 
                "Load Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String[] buildColumnNames(List<Exam> exams) {
        List<String> columns = new ArrayList<>();
        columns.add("#");
        columns.add("Roll No");
        columns.add("Student Name");
        
        for (Exam exam : exams) {
            columns.add("<html>" + exam.getExamName() + "<br><small>(Max: " + exam.getMaxMarks() + ")</small></html>");
        }
        
        columns.add("<html>Total<br>Obtained</html>");
        columns.add("<html>Total<br>Max</html>");
        columns.add("<html>Percentage<br>(Total/Max)</html>");
        columns.add("<html>Avg/Exam<br>(Total/Count)</html>");
        
        return columns.toArray(new String[0]);
    }

    private void generateProfessionalCharts(List<StudentPerformance> performances, 
                                            String className, String subjectName, 
                                            List<Exam> exams, int passCount, int failCount) {
        chartsPanel.removeAll();
        
        // ===== CHARTS LAYOUT =====
        JPanel chartsWrapper = new JPanel(new GridLayout(1, 2, 30, 0));
        chartsWrapper.setBackground(Color.WHITE);
        chartsWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Chart 1: Pass/Fail Distribution (Enhanced)
        JFreeChart passFailChart = createPassFailChart(passCount, failCount, subjectName, className);
        ChartPanel passFailPanel = new ChartPanel(passFailChart);
        passFailPanel.setPreferredSize(new Dimension(550, 450));
        
        // Chart 2: Percentage Distribution (Enhanced)
        JFreeChart distributionChart = createDistributionChart(performances, subjectName, className);
        ChartPanel distributionPanel = new ChartPanel(distributionChart);
        distributionPanel.setPreferredSize(new Dimension(550, 450));
        
        chartsWrapper.add(createChartWrapper(passFailPanel, "📊 Pass/Fail Distribution"));
        chartsWrapper.add(createChartWrapper(distributionPanel, "📈 Percentage Distribution"));
        
        chartsPanel.add(chartsWrapper, BorderLayout.CENTER);
        
        // ===== BOTTOM: KEY INSIGHTS PANEL =====
        JPanel insightsPanel = new JPanel(new BorderLayout(15, 15));
        insightsPanel.setBackground(new Color(241, 248, 255));
        insightsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(65, 131, 215), 2),
            "💡 Performance Insights",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 18),
            new Color(41, 128, 185)
        ));
        insightsPanel.setPreferredSize(new Dimension(0, 120));
        
        double avgPercent = performances.stream().mapToDouble(StudentPerformance::getPercentage).average().orElse(0);
        StringBuilder insights = new StringBuilder();
        
        if (failCount > 0) {
            insights.append("⚠️ Action Required: ").append(failCount).append(" student(s) below 40% pass threshold. ");
            insights.append("Consider remedial sessions for these students.");
        } else {
            insights.append("✅ Excellent! All students passed the subject. ");
            if (avgPercent >= 75) {
                insights.append("Class average (").append(String.format("%.1f", avgPercent)).append("%) exceeds expectations.");
            }
        }
        
        JLabel insightsLabel = new JLabel("<html><b>Insight:</b> " + insights.toString() + "</html>");
        insightsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        insightsLabel.setForeground(new Color(44, 62, 80));
        insightsLabel.setBorder(new EmptyBorder(10, 15, 10, 15));
        insightsPanel.add(insightsLabel, BorderLayout.CENTER);
        
        chartsPanel.add(insightsPanel, BorderLayout.SOUTH);
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    private JFreeChart createPassFailChart(int passCount, int failCount, String subjectName, String className) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(passCount, "Students", "Pass");
        dataset.addValue(failCount, "Students", "Fail");
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Pass/Fail Analysis: " + subjectName + " (" + className + ")",
            "Result",
            "Number of Students",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        // Professional styling
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        chart.getTitle().setPaint(new Color(44, 62, 80));
        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setDomainGridlinePaint(new Color(230, 230, 230));
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        domainAxis.setTickLabelPaint(new Color(60, 60, 60));
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 15));
        domainAxis.setLabelPaint(new Color(44, 62, 80));
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 13));
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 15));
        rangeAxis.setLabelPaint(new Color(44, 62, 80));
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(true);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setItemMargin(0.25);
        renderer.setDefaultOutlinePaint(new Color(200, 200, 200));
        renderer.setDefaultOutlineStroke(new java.awt.BasicStroke(2f));
        
        // Color scheme: Green for Pass, Red for Fail
        renderer.setSeriesPaint(0, new Color(46, 204, 113));  // Pass = Green
        renderer.setSeriesPaint(1, new Color(231, 76, 60));   // Fail = Red
        
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 16));
        renderer.setDefaultItemLabelPaint(new Color(44, 62, 80));
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
            ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        
        renderer.setSeriesItemLabelGenerator(0, 
            new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
        
        return chart;
    }

    private JFreeChart createDistributionChart(List<StudentPerformance> performances, String subjectName, String className) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        int[] bins = new int[5];
        String[] binLabels = {"0-40%", "40-60%", "60-75%", "75-90%", "90-100%"};
        
        for (StudentPerformance sp : performances) {
            double pct = sp.getPercentage();
            if (pct < 40) bins[0]++;
            else if (pct < 60) bins[1]++;
            else if (pct < 75) bins[2]++;
            else if (pct < 90) bins[3]++;
            else bins[4]++;
        }
        
        for (int i = 0; i < bins.length; i++) {
            if (bins[i] > 0) {
                dataset.addValue(bins[i], "Students", binLabels[i]);
            }
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Percentage Distribution: " + subjectName + " (" + className + ")",
            "Performance Range",
            "Number of Students",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        // Professional styling
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        chart.getTitle().setPaint(new Color(44, 62, 80));
        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setDomainGridlinePaint(new Color(230, 230, 230));
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.BOLD, 13));
        domainAxis.setTickLabelPaint(new Color(60, 60, 60));
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        domainAxis.setLabelPaint(new Color(44, 62, 80));
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        rangeAxis.setTickLabelPaint(new Color(80, 80, 80));
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        rangeAxis.setLabelPaint(new Color(44, 62, 80));
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(true);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setItemMargin(0.15);
        renderer.setDefaultOutlinePaint(new Color(200, 200, 200));
        renderer.setDefaultOutlineStroke(new java.awt.BasicStroke(1.5f));
        
        // Professional color scheme (academic gradient)
        renderer.setSeriesPaint(0, new Color(231, 76, 60));    // 0-40%: Red (Fail)
        renderer.setSeriesPaint(1, new Color(243, 156, 18));   // 40-60%: Orange
        renderer.setSeriesPaint(2, new Color(241, 196, 15));   // 60-75%: Yellow
        renderer.setSeriesPaint(3, new Color(46, 204, 113));   // 75-90%: Green
        renderer.setSeriesPaint(4, new Color(52, 152, 219));   // 90-100%: Blue
        
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        renderer.setDefaultItemLabelPaint(new Color(44, 62, 80));
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
            ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        
        renderer.setSeriesItemLabelGenerator(0, 
            new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
        
        return chart;
    }

    private JPanel createChartWrapper(ChartPanel chartPanel, String title) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(108, 92, 231), 2),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(108, 92, 231)
        ));
        wrapper.add(chartPanel, BorderLayout.CENTER);
        return wrapper;
    }

    // ✅ NEW FEATURE: Export Failed Students for Selected Exam
    private void exportFailedByExam() {
        if (failedByExamModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "No failed students to export!\nEither load failed students first or all students passed this exam.",
                "Empty Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Get exam name from dropdown
        String examInfo = comboFailedExam.getSelectedItem().toString();
        String examName = examInfo.split(" - ")[1].split(" \\(")[0].trim();
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Failed Students Report for " + examName);
        fileChooser.setSelectedFile(new java.io.File("failed_students_" + examName.replace(" ", "_") + ".csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile())) {
                // Write headers
                for (int i = 0; i < failedByExamModel.getColumnCount(); i++) {
                    fw.append(failedByExamModel.getColumnName(i).replaceAll("<[^>]*>", "").trim());
                    if (i < failedByExamModel.getColumnCount() - 1) fw.append(",");
                }
                fw.append("\n");

                // Write data
                for (int i = 0; i < failedByExamModel.getRowCount(); i++) {
                    for (int j = 0; j < failedByExamModel.getColumnCount(); j++) {
                        fw.append(failedByExamModel.getValueAt(i, j).toString());
                        if (j < failedByExamModel.getColumnCount() - 1) fw.append(",");
                    }
                    fw.append("\n");
                }
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Failed students report exported successfully!\nExam: " + examName + 
                    "\nTotal Failed: " + failedByExamModel.getRowCount() + " students\nFile: " + 
                    fileChooser.getSelectedFile().getName(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("✓ Failed report exported: " + fileChooser.getSelectedFile().getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error exporting report:\n" + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Export failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // Export failed students report (cumulative)
    private void exportFailedReport() {
        if (failedTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "No failed students to export!\nAll students scored above 40% pass threshold.",
                "Empty Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Failed Students Report (Cumulative)");
        fileChooser.setSelectedFile(new java.io.File("failed_students_cumulative_report.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile())) {
                // Write headers
                for (int i = 0; i < failedTableModel.getColumnCount(); i++) {
                    fw.append(failedTableModel.getColumnName(i).replaceAll("<[^>]*>", "").trim());
                    if (i < failedTableModel.getColumnCount() - 1) fw.append(",");
                }
                fw.append("\n");

                // Write data
                for (int i = 0; i < failedTableModel.getRowCount(); i++) {
                    for (int j = 0; j < failedTableModel.getColumnCount(); j++) {
                        fw.append(failedTableModel.getValueAt(i, j).toString());
                        if (j < failedTableModel.getColumnCount() - 1) fw.append(",");
                    }
                    fw.append("\n");
                }
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Failed students report exported successfully!\nTotal Failed: " + failedTableModel.getRowCount() + " students\nFile: " + fileChooser.getSelectedFile().getName(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("✓ Failed report exported: " + fileChooser.getSelectedFile().getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error exporting report:\n" + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Export failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // Export full report (all students)
    private void exportFullReport() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export!", "Empty Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Full Performance Report");
        fileChooser.setSelectedFile(new java.io.File("subject_performance_full_report.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile())) {
                // Write headers
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    fw.append(tableModel.getColumnName(i).replaceAll("<[^>]*>", "").replaceAll("\\s+", " ").trim());
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
                JOptionPane.showMessageDialog(this, "✅ Full performance report exported successfully!\nFile: " + fileChooser.getSelectedFile().getAbsolutePath(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("✓ Full report exported: " + fileChooser.getSelectedFile().getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error exporting report:\n" + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Export failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // ✅ CORRECT MODEL CLASS (Embedded)
    static class StudentPerformance {
        private int studentId;
        private String rollNo;
        private String studentName;
        private Map<Integer, Integer> examMarks = new HashMap<>();
        private Map<Integer, Integer> examMaxMarks = new HashMap<>();
        private int totalObtained = 0;
        private int totalMax = 0;
        private double percentage = 0.0;
        private double avgMarks = 0.0;
        
        public StudentPerformance(int studentId, String rollNo, String studentName) {
            this.studentId = studentId;
            this.rollNo = rollNo;
            this.studentName = studentName;
        }
        
        public void addExamMark(int examId, int marks, int maxMarks) {
            examMarks.put(examId, marks);
            examMaxMarks.put(examId, maxMarks);
        }
        
        public void calculateTotals() {
            totalObtained = examMarks.values().stream().mapToInt(Integer::intValue).sum();
            totalMax = examMaxMarks.values().stream().mapToInt(Integer::intValue).sum();
            percentage = (totalMax > 0) ? (totalObtained * 100.0 / totalMax) : 0.0;
            avgMarks = (examMarks.size() > 0) ? (totalObtained * 1.0 / examMarks.size()) : 0.0;
        }
        
        public int getStudentId() { return studentId; }
        public String getRollNo() { return rollNo; }
        public String getStudentName() { return studentName; }
        public Map<Integer, Integer> getExamMarks() { return examMarks; }
        public int getTotalObtained() { return totalObtained; }
        public int getTotalMax() { return totalMax; }
        public double getPercentage() { return percentage; }
        public double getAvgMarks() { return avgMarks; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                JFrame mockResultsUI = new JFrame("Results UI");
                mockResultsUI.setSize(1000, 650);
                mockResultsUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mockResultsUI.setVisible(true);
                new SubjectPerformanceUI(1, mockResultsUI).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Application failed to start!\nError: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

//
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
//import java.text.DecimalFormat;
//import java.util.*;
//import java.util.List;
//
//public class SubjectPerformanceUI extends JFrame {
//
//    private JComboBox<String> comboClass, comboSubject;
//    private JTable performanceTable, failedStudentsTable;
//    private DefaultTableModel tableModel, failedTableModel;
//    private ClassDAO classDAO;
//    private SubjectDAO subjectDAO;
//    private ExamDAO examDAO;
//    private StudentDAO studentDAO;
//    private int facultyId;
//    private JFrame previousUI;
//    private JLabel statusLabel;
//    private boolean isLoadingSubjects = false;
//    private List<Exam> currentExams = new ArrayList<>();
//    private JTabbedPane mainTabbedPane;
//    private JPanel chartsPanel;
//    private List<StudentPerformance> allPerformances = new ArrayList<>();
//
//    // Statistics labels
//    private JLabel totalStudentsLabel, avgPercentLabel, highestPercentLabel, passCountLabel, failCountLabel;
//
//    public SubjectPerformanceUI(int facultyId, JFrame previousUI) {
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
//        setTitle("📊 Subject Performance Analysis | Faculty ID: " + facultyId);
//        setSize(1250, 820);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLayout(new BorderLayout(15, 15));
//        getContentPane().setBackground(new Color(248, 250, 252));
//
//        // ===== TOP SECTION WRAPPER =====
//        JPanel topWrapper = new JPanel();
//        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
//        topWrapper.setOpaque(false);
//
//        // ===== HEADER PANEL =====
//        JPanel headerPanel = new JPanel(new BorderLayout());
//        headerPanel.setBackground(new Color(41, 128, 185));
//        headerPanel.setPreferredSize(new Dimension(0, 80));
//        headerPanel.setBorder(new EmptyBorder(0, 25, 0, 25));
//
//        JLabel titleLabel = new JLabel("🎓 Subject-wise Cumulative Performance Analysis");
//        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
//        titleLabel.setForeground(Color.WHITE);
//        headerPanel.add(titleLabel, BorderLayout.WEST);
//
//        JButton btnBack = createModernButton("⇦ Back to Results", new Color(30, 136, 56), Color.WHITE);
//        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        btnBack.setPreferredSize(new Dimension(210, 45));
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
//            "🔍 Select Analysis Parameters",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 17),
//            new Color(41, 128, 185)
//        ));
//        filterPanel.setBackground(Color.WHITE);
//        filterPanel.setPreferredSize(new Dimension(0, 100));
//
//        filterPanel.add(createFilterLabel("🏫 Class:"));
//        comboClass = createModernComboBox();
//        filterPanel.add(comboClass);
//
//        filterPanel.add(createFilterLabel("📚 Subject:"));
//        comboSubject = createModernComboBox();
//        filterPanel.add(comboSubject);
//
//        JButton btnLoad = createModernButton("✨ Analyze Performance", new Color(142, 68, 173), Color.WHITE);
//        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 15));
//        btnLoad.setPreferredSize(new Dimension(200, 42));
//        btnLoad.addActionListener(e -> loadPerformanceData());
//        filterPanel.add(btnLoad);
//        topWrapper.add(filterPanel);
//
//        add(topWrapper, BorderLayout.NORTH);
//
//        // ===== MAIN CONTENT (TAB BED) =====
//        mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
//        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        mainTabbedPane.setBackground(new Color(248, 250, 252));
//        mainTabbedPane.setForeground(new Color(41, 128, 185));
//
//        // Tab 1: All Students Performance
//        mainTabbedPane.addTab("📋 All Students", createPerformanceTableTab());
//
//        // Tab 2: Failed Students Report (NEW FEATURE)
//        mainTabbedPane.addTab("❌ Failed Students", createFailedStudentsTab());
//
//        // Tab 3: Visual Analytics
//        chartsPanel = new JPanel(new BorderLayout(25, 25));
//        chartsPanel.setBackground(Color.WHITE);
//        chartsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
//        mainTabbedPane.addTab("📈 Visual Analytics", chartsPanel);
//
//        // Tab 4: Statistics Summary
//        mainTabbedPane.addTab("🔢 Performance Metrics", createStatisticsTab());
//
//        add(mainTabbedPane, BorderLayout.CENTER);
//
//        // ===== STATUS BAR =====
//        statusLabel = new JLabel("Ready to analyze subject performance. Select class and subject, then click 'Analyze Performance'.");
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
//                loadSubjects();
//            }
//        });
//
//        comboSubject.addActionListener(e -> {
//            if (!isLoadingSubjects && comboSubject.getSelectedItem() != null 
//                && !comboSubject.getSelectedItem().toString().contains("--")) {
//                // Wait for explicit button click
//            }
//        });
//    }
//
//    private JPanel createPerformanceTableTab() {
//        JPanel panel = new JPanel(new BorderLayout(15, 15));
//        panel.setBackground(Color.WHITE);
//        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
//
//        tableModel = new DefaultTableModel(new String[]{"#", "Roll No", "Student Name"}, 0) {
//            public boolean isCellEditable(int row, int col) { return false; }
//        };
//
//        performanceTable = new JTable(tableModel);
//        performanceTable.setRowHeight(38);
//        performanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        performanceTable.setGridColor(new Color(236, 240, 241));
//        performanceTable.setSelectionBackground(new Color(52, 152, 219));
//        performanceTable.setSelectionForeground(Color.WHITE);
//        performanceTable.setIntercellSpacing(new Dimension(0, 2));
//
//        JTableHeader header = performanceTable.getTableHeader();
//        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        header.setBackground(new Color(52, 73, 94));
//        header.setForeground(Color.WHITE);
//        header.setPreferredSize(new Dimension(0, 45));
//
//        // Color-coded rendering
//        performanceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
//                                                           boolean hasFocus, int row, int column) {
//                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
//                if (!isSelected) {
//                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
//                    
//                    // Color-code percentage column (second last)
//                    if (column == tableModel.getColumnCount() - 2) {
//                        try {
//                            String pctStr = value.toString().replace("%", "").trim();
//                            double pct = Double.parseDouble(pctStr);
//                            if (pct >= 90) {
//                                c.setBackground(new Color(46, 204, 113, 40));
//                                c.setForeground(new Color(27, 94, 32));
//                                c.setFont(c.getFont().deriveFont(Font.BOLD));
//                            } else if (pct >= 75) {
//                                c.setBackground(new Color(52, 152, 219, 30));
//                                c.setForeground(new Color(40, 116, 166));
//                            } else if (pct >= 60) {
//                                c.setBackground(new Color(241, 196, 15, 30));
//                                c.setForeground(new Color(146, 112, 6));
//                            } else if (pct >= 40) {
//                                c.setBackground(new Color(243, 156, 18, 30));
//                                c.setForeground(new Color(165, 82, 0));
//                            } else {
//                                c.setBackground(new Color(231, 76, 60, 30));
//                                c.setForeground(new Color(165, 38, 20));
//                            }
//                        } catch (Exception ignored) {}
//                    }
//                }
//                return c;
//            }
//        });
//
//        JScrollPane scroll = new JScrollPane(performanceTable);
//        scroll.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
//            "📊 Student Performance Across All Assessments",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 16),
//            new Color(52, 152, 219)
//        ));
//        panel.add(scroll, BorderLayout.CENTER);
//
//        // Action buttons
//        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
//        actionPanel.setBackground(Color.WHITE);
//        
//        JButton btnExport = createModernButton("📤 Export Full Report", new Color(155, 89, 182), Color.WHITE);
//        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        btnExport.setPreferredSize(new Dimension(180, 40));
//        btnExport.addActionListener(e -> exportFullReport());
//        actionPanel.add(btnExport);
//        
//        panel.add(actionPanel, BorderLayout.SOUTH);
//        return panel;
//    }
//
//    private JPanel createFailedStudentsTab() {
//        JPanel panel = new JPanel(new BorderLayout(15, 15));
//        panel.setBackground(Color.WHITE);
//        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
//
//        failedTableModel = new DefaultTableModel(
//            new String[]{"#", "Roll No", "Student Name", "Total Obtained", "Total Max", "Percentage", "Status"}, 0
//        ) {
//            public boolean isCellEditable(int row, int col) { return false; }
//        };
//
//        failedStudentsTable = new JTable(failedTableModel);
//        failedStudentsTable.setRowHeight(38);
//        failedStudentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        failedStudentsTable.setGridColor(new Color(236, 240, 241));
//        failedStudentsTable.setSelectionBackground(new Color(231, 76, 60));
//        failedStudentsTable.setSelectionForeground(Color.WHITE);
//        failedStudentsTable.setIntercellSpacing(new Dimension(0, 2));
//
//        JTableHeader header = failedStudentsTable.getTableHeader();
//        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        header.setBackground(new Color(231, 76, 60));
//        header.setForeground(Color.WHITE);
//        header.setPreferredSize(new Dimension(0, 45));
//
//        // Red highlight for failed students
//        failedStudentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
//                                                           boolean hasFocus, int row, int column) {
//                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
//                if (!isSelected) {
//                    c.setBackground(row % 2 == 0 ? new Color(255, 245, 245) : new Color(255, 250, 250));
//                    
//                    // Highlight percentage column in red
//                    if (column == 5) {
//                        try {
//                            String pctStr = value.toString().replace("%", "").trim();
//                            double pct = Double.parseDouble(pctStr);
//                            if (pct < 40) {
//                                c.setBackground(new Color(255, 235, 235));
//                                c.setForeground(new Color(165, 38, 20));
//                                c.setFont(c.getFont().deriveFont(Font.BOLD));
//                            }
//                        } catch (Exception ignored) {}
//                    }
//                    
//                    // Highlight Status column
//                    if (column == 6 && "FAILED".equals(value)) {
//                        c.setBackground(new Color(255, 235, 235));
//                        c.setForeground(new Color(165, 38, 20));
//                        c.setFont(c.getFont().deriveFont(Font.BOLD, 15f));
//                    }
//                }
//                return c;
//            }
//        });
//
//        JScrollPane scroll = new JScrollPane(failedStudentsTable);
//        scroll.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
//            "❌ Students Below Pass Percentage (40%)",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 16),
//            new Color(231, 76, 60)
//        ));
//        panel.add(scroll, BorderLayout.CENTER);
//
//        // Action buttons
//        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
//        actionPanel.setBackground(Color.WHITE);
//        
//        JButton btnExportFailed = createModernButton("📤 Export Failed Report", new Color(231, 76, 60), Color.WHITE);
//        btnExportFailed.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        btnExportFailed.setPreferredSize(new Dimension(200, 40));
//        btnExportFailed.addActionListener(e -> exportFailedReport());
//        actionPanel.add(btnExportFailed);
//        
//        panel.add(actionPanel, BorderLayout.SOUTH);
//        return panel;
//    }
//
//    private JPanel createStatisticsTab() {
//        JPanel panel = new JPanel(new GridLayout(2, 4, 25, 25));
//        panel.setBackground(Color.WHITE);
//        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
//        
//        panel.add(createStatCard("🎓 Total Students", "0", new Color(52, 152, 219), lbl -> totalStudentsLabel = lbl));
//        panel.add(createStatCard("📊 Avg Percentage", "0.0%", new Color(46, 204, 113), lbl -> avgPercentLabel = lbl));
//        panel.add(createStatCard("⭐ Highest Score", "0.0%", new Color(241, 196, 15), lbl -> highestPercentLabel = lbl));
//        panel.add(createStatCard("✅ Pass Count", "0", new Color(39, 174, 96), lbl -> passCountLabel = lbl));
//        panel.add(createStatCard("❌ Fail Count", "0", new Color(231, 76, 60), lbl -> failCountLabel = lbl));
//        panel.add(createStatCard("📈 Pass %", "0.0%", new Color(40, 167, 69), lbl -> {})); // Placeholder
//        panel.add(createStatCard("📉 Fail %", "0.0%", new Color(220, 53, 69), lbl -> {}));
//        panel.add(createStatCard("🎯 Pass Threshold", "40%", new Color(142, 68, 173), lbl -> {}));
//        
//        JPanel wrapper = new JPanel(new BorderLayout());
//        wrapper.setBackground(Color.WHITE);
//        wrapper.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
//            "🎯 Key Performance Indicators",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 18),
//            new Color(41, 128, 185)
//        ));
//        wrapper.add(panel, BorderLayout.CENTER);
//        return wrapper;
//    }
//
//    private JLabel createFilterLabel(String text) {
//        JLabel label = new JLabel(text);
//        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        label.setForeground(new Color(41, 128, 185));
//        return label;
//    }
//
//    private JComboBox<String> createModernComboBox() {
//        JComboBox<String> combo = new JComboBox<>();
//        combo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
//        combo.setPreferredSize(new Dimension(240, 42));
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
//    private JPanel createStatCard(String title, String value, Color color, java.util.function.Consumer<JLabel> labelRef) {
//        JPanel card = new JPanel(new BorderLayout(10, 15));
//        card.setBackground(Color.WHITE);
//        card.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(color, 3),
//            BorderFactory.createEmptyBorder(20, 20, 20, 20)
//        ));
//        card.setPreferredSize(new Dimension(240, 140));
//        
//        JLabel titleLabel = new JLabel(title);
//        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
//        titleLabel.setForeground(new Color(100, 100, 100));
//        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        
//        JLabel valueLabel = new JLabel(value);
//        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
//        valueLabel.setForeground(color);
//        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        labelRef.accept(valueLabel);
//        
//        card.add(titleLabel, BorderLayout.NORTH);
//        card.add(valueLabel, BorderLayout.CENTER);
//        
//        return card;
//    }
//
//    private void loadData() {
//        loadClasses();
//    }
//
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        comboSubject.removeAllItems();
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
//            loadSubjects();
//        }
//    }
//
//    private void loadSubjects() {
//        isLoadingSubjects = true;
//        comboSubject.removeAllItems();
//        if (comboClass.getSelectedItem() == null || "-- No classes --".equals(comboClass.getSelectedItem().toString())) {
//            comboSubject.addItem("-- Select class first --");
//            isLoadingSubjects = false;
//            return;
//        }
//        try {
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
//            if (subjects.isEmpty()) {
//                comboSubject.addItem("-- No subjects assigned --");
//                statusLabel.setText("⚠️ No subjects found for this class");
//            } else {
//                for (Subject s : subjects) {
//                    comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
//                }
//                comboSubject.setSelectedIndex(0);
//            }
//        } catch (Exception e) {
//            comboSubject.addItem("-- Error loading subjects --");
//            statusLabel.setText("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//        isLoadingSubjects = false;
//    }
//
//    private void loadPerformanceData() {
//        tableModel.setRowCount(0);
//        failedTableModel.setRowCount(0);
//        chartsPanel.removeAll();
//        chartsPanel.add(new JLabel("📊 Generating professional analytics...", SwingConstants.CENTER), BorderLayout.CENTER);
//        chartsPanel.revalidate(); 
//        chartsPanel.repaint();
//        
//        if (comboClass.getSelectedItem() == null || comboSubject.getSelectedItem() == null) {
//            JOptionPane.showMessageDialog(this, "Please select class and subject!", "Missing Selection", JOptionPane.WARNING_MESSAGE);
//            statusLabel.setText("⚠️ Select valid class and subject");
//            return;
//        }
//
//        if (comboSubject.getSelectedItem().toString().contains("--")) {
//            JOptionPane.showMessageDialog(this, "No valid subject selected!", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
//            statusLabel.setText("⚠️ Select valid subject");
//            return;
//        }
//
//        try {
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//            String className = comboClass.getSelectedItem().toString().split(" - ")[1].trim();
//            String subjectName = comboSubject.getSelectedItem().toString().split(" - ")[1].trim();
//
//            currentExams = examDAO.getExamsByFacultyClassSubject(facultyId, classId, subjectId);
//            if (currentExams.isEmpty()) {
//                JOptionPane.showMessageDialog(this, 
//                    "No exams found for this subject!\nCreate exams in Exam Management first.",
//                    "No Exams Available", JOptionPane.INFORMATION_MESSAGE);
//                statusLabel.setText("⚠️ No exams found for " + subjectName);
//                return;
//            }
//            
//            List<String[]> students = studentDAO.getStudentsByClass(classId);
//            if (students.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "No students found in this class!", "Empty Class", JOptionPane.INFORMATION_MESSAGE);
//                statusLabel.setText("⚠️ No students found in " + className);
//                return;
//            }
//            
//            allPerformances.clear();
//            DecimalFormat pctFormat = new DecimalFormat("0.0");
//            DecimalFormat avgFormat = new DecimalFormat("0.00");
//            int passCount = 0;
//            int failCount = 0;
//            double totalPercent = 0;
//            double highestPercent = 0;
//            
//            // Calculate performance for each student
//            for (String[] student : students) {
//                int studentId = Integer.parseInt(student[0]);
//                String rollNo = student[1];
//                String name = student[2];
//                
//                StudentPerformance sp = new StudentPerformance(studentId, rollNo, name);
//                
//                for (Exam exam : currentExams) {
//                    int marks = studentDAO.getMarksByExam(studentId, exam.getExamId());
//                    sp.addExamMark(exam.getExamId(), marks, exam.getMaxMarks());
//                }
//                
//                sp.calculateTotals();
//                allPerformances.add(sp);
//                
//                totalPercent += sp.getPercentage();
//                if (sp.getPercentage() > highestPercent) highestPercent = sp.getPercentage();
//                if (sp.getPercentage() >= 40) passCount++;
//                else failCount++;
//            }
//            
//            // Build table model for all students
//            String[] columnNames = buildColumnNames(currentExams);
//            tableModel.setColumnIdentifiers(columnNames);
//            
//            for (int i = 0; i < allPerformances.size(); i++) {
//                StudentPerformance sp = allPerformances.get(i);
//                List<Object> rowData = new ArrayList<>();
//                rowData.add(i + 1);
//                rowData.add(sp.getRollNo());
//                rowData.add(sp.getStudentName());
//                
//                for (Exam exam : currentExams) {
//                    Integer marks = sp.getExamMarks().get(exam.getExamId());
//                    rowData.add(marks != null ? marks.toString() : "0");
//                }
//                
//                rowData.add(sp.getTotalObtained());
//                rowData.add(sp.getTotalMax());
//                rowData.add(pctFormat.format(sp.getPercentage()) + "%");
//                rowData.add(avgFormat.format(sp.getAvgMarks()));
//                
//                tableModel.addRow(rowData.toArray());
//            }
//            
//            // Build table model for failed students ONLY
//            failedTableModel.setRowCount(0);
//            int failIndex = 1;
//            for (StudentPerformance sp : allPerformances) {
//                if (sp.getPercentage() < 40) { // Failed students (below 40%)
//                    failedTableModel.addRow(new Object[]{
//                        failIndex++,
//                        sp.getRollNo(),
//                        sp.getStudentName(),
//                        sp.getTotalObtained(),
//                        sp.getTotalMax(),
//                        pctFormat.format(sp.getPercentage()) + "%",
//                        "FAILED"
//                    });
//                }
//            }
//            
//            // Update statistics
//            int totalStudents = allPerformances.size();
//            double avgPercent = totalPercent / totalStudents;
//            double passPercent = (passCount * 100.0) / totalStudents;
//            double failPercent = (failCount * 100.0) / totalStudents;
//            
//            totalStudentsLabel.setText(String.valueOf(totalStudents));
//            avgPercentLabel.setText(pctFormat.format(avgPercent) + "%");
//            highestPercentLabel.setText(pctFormat.format(highestPercent) + "%");
//            passCountLabel.setText(String.valueOf(passCount));
//            failCountLabel.setText(String.valueOf(failCount));
//            
//            // Update statistics tab labels (pass/fail percentages)
//            Component[] statsComponents = ((JPanel)mainTabbedPane.getComponentAt(3)).getComponents();
//            if (statsComponents.length > 0 && statsComponents[0] instanceof JPanel) {
//                JPanel statsPanel = (JPanel)statsComponents[0];
//                if (statsPanel.getComponentCount() >= 6) {
//                    ((JLabel)((JPanel)statsPanel.getComponent(5)).getComponent(1)).setText(String.format("%.1f%%", passPercent));
//                    ((JLabel)((JPanel)statsPanel.getComponent(6)).getComponent(1)).setText(String.format("%.1f%%", failPercent));
//                }
//            }
//            
//            // Generate professional charts
//            generateProfessionalCharts(allPerformances, className, subjectName, currentExams, passCount, failCount);
//            
//            statusLabel.setText(String.format("✓ Analyzed %d students for %s | Pass: %.1f%% (%d) | Fail: %.1f%% (%d)", 
//                totalStudents, subjectName, passPercent, passCount, failPercent, failCount));
//            
//            // Auto-size columns
//            for (int i = 0; i < performanceTable.getColumnCount(); i++) {
//                performanceTable.getColumnModel().getColumn(i).setPreferredWidth(
//                    i < 3 ? 110 : (i >= tableModel.getColumnCount() - 2 ? 90 : 85)
//                );
//            }
//            
//            // Auto-size failed students table columns
//            for (int i = 0; i < failedStudentsTable.getColumnCount(); i++) {
//                failedStudentsTable.getColumnModel().getColumn(i).setPreferredWidth(
//                    i < 3 ? 110 : (i == 6 ? 100 : 90)
//                );
//            }
//            
//            // Switch to Charts tab to show beautiful analytics
//            mainTabbedPane.setSelectedIndex(2);
//            
//        } catch (Exception e) {
//            statusLabel.setText("❌ Error loading performance data: " + e.getMessage());
//            JOptionPane.showMessageDialog(this, "Error loading performance data:\n" + e.getMessage(), 
//                "Load Error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }
//
//    private String[] buildColumnNames(List<Exam> exams) {
//        List<String> columns = new ArrayList<>();
//        columns.add("#");
//        columns.add("Roll No");
//        columns.add("Student Name");
//        
//        for (Exam exam : exams) {
//            columns.add("<html>" + exam.getExamName() + "<br><small>(Max: " + exam.getMaxMarks() + ")</small></html>");
//        }
//        
//        columns.add("<html>Total<br>Obtained</html>");
//        columns.add("<html>Total<br>Max</html>");
//        columns.add("<html>Percentage<br>(Total/Max)</html>");
//        columns.add("<html>Avg/Exam<br>(Total/Count)</html>");
//        
//        return columns.toArray(new String[0]);
//    }
//
//    private void generateProfessionalCharts(List<StudentPerformance> performances, 
//                                            String className, String subjectName, 
//                                            List<Exam> exams, int passCount, int failCount) {
//        chartsPanel.removeAll();
//        
//        // ===== CHARTS LAYOUT =====
//        JPanel chartsWrapper = new JPanel(new GridLayout(1, 2, 30, 0));
//        chartsWrapper.setBackground(Color.WHITE);
//        chartsWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
//        
//        // Chart 1: Pass/Fail Distribution (Enhanced)
//        JFreeChart passFailChart = createPassFailChart(passCount, failCount, subjectName, className);
//        ChartPanel passFailPanel = new ChartPanel(passFailChart);
//        passFailPanel.setPreferredSize(new Dimension(550, 450));
//        
//        // Chart 2: Percentage Distribution (Enhanced)
//        JFreeChart distributionChart = createDistributionChart(performances, subjectName, className);
//        ChartPanel distributionPanel = new ChartPanel(distributionChart);
//        distributionPanel.setPreferredSize(new Dimension(550, 450));
//        
//        chartsWrapper.add(createChartWrapper(passFailPanel, "📊 Pass/Fail Distribution"));
//        chartsWrapper.add(createChartWrapper(distributionPanel, "📈 Percentage Distribution"));
//        
//        chartsPanel.add(chartsWrapper, BorderLayout.CENTER);
//        
//        // ===== BOTTOM: KEY INSIGHTS PANEL =====
//        JPanel insightsPanel = new JPanel(new BorderLayout(15, 15));
//        insightsPanel.setBackground(new Color(241, 248, 255));
//        insightsPanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(65, 131, 215), 2),
//            "💡 Performance Insights",
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 18),
//            new Color(41, 128, 185)
//        ));
//        insightsPanel.setPreferredSize(new Dimension(0, 120));
//        
//        double avgPercent = performances.stream().mapToDouble(StudentPerformance::getPercentage).average().orElse(0);
//        StringBuilder insights = new StringBuilder();
//        
//        if (failCount > 0) {
//            insights.append("⚠️ Action Required: ").append(failCount).append(" student(s) below 40% pass threshold. ");
//            insights.append("Consider remedial sessions for these students.");
//        } else {
//            insights.append("✅ Excellent! All students passed the subject. ");
//            if (avgPercent >= 75) {
//                insights.append("Class average (").append(String.format("%.1f", avgPercent)).append("%) exceeds expectations.");
//            }
//        }
//        
//        JLabel insightsLabel = new JLabel("<html><b>Insight:</b> " + insights.toString() + "</html>");
//        insightsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
//        insightsLabel.setForeground(new Color(44, 62, 80));
//        insightsLabel.setBorder(new EmptyBorder(10, 15, 10, 15));
//        insightsPanel.add(insightsLabel, BorderLayout.CENTER);
//        
//        chartsPanel.add(insightsPanel, BorderLayout.SOUTH);
//        chartsPanel.revalidate();
//        chartsPanel.repaint();
//    }
//
//    private JFreeChart createPassFailChart(int passCount, int failCount, String subjectName, String className) {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        dataset.addValue(passCount, "Students", "Pass");
//        dataset.addValue(failCount, "Students", "Fail");
//        
//        JFreeChart chart = ChartFactory.createBarChart(
//            "Pass/Fail Analysis: " + subjectName + " (" + className + ")",
//            "Result",
//            "Number of Students",
//            dataset,
//            PlotOrientation.VERTICAL,
//            false,
//            true,
//            false
//        );
//        
//        // Professional styling
//        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
//        chart.getTitle().setPaint(new Color(44, 62, 80));
//        chart.setBackgroundPaint(Color.WHITE);
//        
//        CategoryPlot plot = chart.getCategoryPlot();
//        plot.setBackgroundPaint(Color.WHITE);
//        plot.setRangeGridlinePaint(new Color(220, 220, 220));
//        plot.setDomainGridlinePaint(new Color(230, 230, 230));
//        
//        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.BOLD, 14));
//        domainAxis.setTickLabelPaint(new Color(60, 60, 60));
//        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 15));
//        domainAxis.setLabelPaint(new Color(44, 62, 80));
//        
//        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 13));
//        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 15));
//        rangeAxis.setLabelPaint(new Color(44, 62, 80));
//        
//        BarRenderer renderer = (BarRenderer) plot.getRenderer();
//        renderer.setDrawBarOutline(true);
//        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
//        renderer.setShadowVisible(false);
//        renderer.setItemMargin(0.25);
//        renderer.setDefaultOutlinePaint(new Color(200, 200, 200));
//        renderer.setDefaultOutlineStroke(new java.awt.BasicStroke(2f));
//        
//        // Color scheme: Green for Pass, Red for Fail
//        renderer.setSeriesPaint(0, new Color(46, 204, 113));  // Pass = Green
//        renderer.setSeriesPaint(1, new Color(231, 76, 60));   // Fail = Red
//        
//        renderer.setDefaultItemLabelsVisible(true);
//        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 16));
//        renderer.setDefaultItemLabelPaint(new Color(44, 62, 80));
//        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
//            ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
//        
//        renderer.setSeriesItemLabelGenerator(0, 
//            new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
//        
//        return chart;
//    }
//
//    private JFreeChart createDistributionChart(List<StudentPerformance> performances, String subjectName, String className) {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        
//        int[] bins = new int[5];
//        String[] binLabels = {"0-40%", "40-60%", "60-75%", "75-90%", "90-100%"};
//        
//        for (StudentPerformance sp : performances) {
//            double pct = sp.getPercentage();
//            if (pct < 40) bins[0]++;
//            else if (pct < 60) bins[1]++;
//            else if (pct < 75) bins[2]++;
//            else if (pct < 90) bins[3]++;
//            else bins[4]++;
//        }
//        
//        for (int i = 0; i < bins.length; i++) {
//            if (bins[i] > 0) {
//                dataset.addValue(bins[i], "Students", binLabels[i]);
//            }
//        }
//        
//        JFreeChart chart = ChartFactory.createBarChart(
//            "Percentage Distribution: " + subjectName + " (" + className + ")",
//            "Performance Range",
//            "Number of Students",
//            dataset,
//            PlotOrientation.VERTICAL,
//            false,
//            true,
//            false
//        );
//        
//        // Professional styling
//        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
//        chart.getTitle().setPaint(new Color(44, 62, 80));
//        chart.setBackgroundPaint(Color.WHITE);
//        
//        CategoryPlot plot = chart.getCategoryPlot();
//        plot.setBackgroundPaint(Color.WHITE);
//        plot.setRangeGridlinePaint(new Color(220, 220, 220));
//        plot.setDomainGridlinePaint(new Color(230, 230, 230));
//        
//        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
//        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.BOLD, 13));
//        domainAxis.setTickLabelPaint(new Color(60, 60, 60));
//        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
//        domainAxis.setLabelPaint(new Color(44, 62, 80));
//        
//        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
//        rangeAxis.setTickLabelPaint(new Color(80, 80, 80));
//        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
//        rangeAxis.setLabelPaint(new Color(44, 62, 80));
//        
//        BarRenderer renderer = (BarRenderer) plot.getRenderer();
//        renderer.setDrawBarOutline(true);
//        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
//        renderer.setShadowVisible(false);
//        renderer.setItemMargin(0.15);
//        renderer.setDefaultOutlinePaint(new Color(200, 200, 200));
//        renderer.setDefaultOutlineStroke(new java.awt.BasicStroke(1.5f));
//        
//        // Professional color scheme (academic gradient)
//        renderer.setSeriesPaint(0, new Color(231, 76, 60));    // 0-40%: Red (Fail)
//        renderer.setSeriesPaint(1, new Color(243, 156, 18));   // 40-60%: Orange
//        renderer.setSeriesPaint(2, new Color(241, 196, 15));   // 60-75%: Yellow
//        renderer.setSeriesPaint(3, new Color(46, 204, 113));   // 75-90%: Green
//        renderer.setSeriesPaint(4, new Color(52, 152, 219));   // 90-100%: Blue
//        
//        renderer.setDefaultItemLabelsVisible(true);
//        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 14));
//        renderer.setDefaultItemLabelPaint(new Color(44, 62, 80));
//        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
//            ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
//        
//        renderer.setSeriesItemLabelGenerator(0, 
//            new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
//        
//        return chart;
//    }
//
//    private JPanel createChartWrapper(ChartPanel chartPanel, String title) {
//        JPanel wrapper = new JPanel(new BorderLayout());
//        wrapper.setBackground(Color.WHITE);
//        wrapper.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createLineBorder(new Color(108, 92, 231), 2),
//            title,
//            TitledBorder.LEFT,
//            TitledBorder.TOP,
//            new Font("Segoe UI", Font.BOLD, 16),
//            new Color(108, 92, 231)
//        ));
//        wrapper.add(chartPanel, BorderLayout.CENTER);
//        return wrapper;
//    }
//
//    // ✅ NEW FEATURE: Export Failed Students Report
//    private void exportFailedReport() {
//        if (failedTableModel.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, 
//                "No failed students to export!\nAll students scored above 40% pass threshold.",
//                "Empty Report", JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }
//
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Failed Students Report");
//        fileChooser.setSelectedFile(new java.io.File("failed_students_report.csv"));
//        
//        int userSelection = fileChooser.showSaveDialog(this);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile())) {
//                // Write headers
//                for (int i = 0; i < failedTableModel.getColumnCount(); i++) {
//                    fw.append(failedTableModel.getColumnName(i).replaceAll("<[^>]*>", "").trim());
//                    if (i < failedTableModel.getColumnCount() - 1) fw.append(",");
//                }
//                fw.append("\n");
//
//                // Write data
//                for (int i = 0; i < failedTableModel.getRowCount(); i++) {
//                    for (int j = 0; j < failedTableModel.getColumnCount(); j++) {
//                        fw.append(failedTableModel.getValueAt(i, j).toString());
//                        if (j < failedTableModel.getColumnCount() - 1) fw.append(",");
//                    }
//                    fw.append("\n");
//                }
//                
//                JOptionPane.showMessageDialog(this, 
//                    "✅ Failed students report exported successfully!\nTotal Failed: " + failedTableModel.getRowCount() + " students\nFile: " + fileChooser.getSelectedFile().getName(),
//                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
//                statusLabel.setText("✓ Failed report exported: " + fileChooser.getSelectedFile().getName());
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(this, "❌ Error exporting report:\n" + ex.getMessage(),
//                    "Export Error", JOptionPane.ERROR_MESSAGE);
//                statusLabel.setText("Export failed: " + ex.getMessage());
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    // Export full report (all students)
//    private void exportFullReport() {
//        if (tableModel.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No data to export!", "Empty Data", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Full Performance Report");
//        fileChooser.setSelectedFile(new java.io.File("subject_performance_full_report.csv"));
//        
//        int userSelection = fileChooser.showSaveDialog(this);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile())) {
//                // Write headers
//                for (int i = 0; i < tableModel.getColumnCount(); i++) {
//                    fw.append(tableModel.getColumnName(i).replaceAll("<[^>]*>", "").replaceAll("\\s+", " ").trim());
//                    if (i < tableModel.getColumnCount() - 1) fw.append(",");
//                }
//                fw.append("\n");
//
//                // Write data
//                for (int i = 0; i < tableModel.getRowCount(); i++) {
//                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
//                        fw.append(tableModel.getValueAt(i, j).toString());
//                        if (j < tableModel.getColumnCount() - 1) fw.append(",");
//                    }
//                    fw.append("\n");
//                }
//                JOptionPane.showMessageDialog(this, "✅ Full performance report exported successfully!\nFile: " + fileChooser.getSelectedFile().getAbsolutePath(),
//                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
//                statusLabel.setText("✓ Full report exported: " + fileChooser.getSelectedFile().getName());
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(this, "❌ Error exporting report:\n" + ex.getMessage(),
//                    "Export Error", JOptionPane.ERROR_MESSAGE);
//                statusLabel.setText("Export failed: " + ex.getMessage());
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    // ✅ CORRECT MODEL CLASS (Embedded)
//    static class StudentPerformance {
//        private int studentId;
//        private String rollNo;
//        private String studentName;
//        private Map<Integer, Integer> examMarks = new HashMap<>();
//        private Map<Integer, Integer> examMaxMarks = new HashMap<>();
//        private int totalObtained = 0;
//        private int totalMax = 0;
//        private double percentage = 0.0;
//        private double avgMarks = 0.0;
//        
//        public StudentPerformance(int studentId, String rollNo, String studentName) {
//            this.studentId = studentId;
//            this.rollNo = rollNo;
//            this.studentName = studentName;
//        }
//        
//        public void addExamMark(int examId, int marks, int maxMarks) {
//            examMarks.put(examId, marks);
//            examMaxMarks.put(examId, maxMarks);
//        }
//        
//        public void calculateTotals() {
//            totalObtained = examMarks.values().stream().mapToInt(Integer::intValue).sum();
//            totalMax = examMaxMarks.values().stream().mapToInt(Integer::intValue).sum();
//            percentage = (totalMax > 0) ? (totalObtained * 100.0 / totalMax) : 0.0;
//            avgMarks = (examMarks.size() > 0) ? (totalObtained * 1.0 / examMarks.size()) : 0.0;
//        }
//        
//        public int getStudentId() { return studentId; }
//        public String getRollNo() { return rollNo; }
//        public String getStudentName() { return studentName; }
//        public Map<Integer, Integer> getExamMarks() { return examMarks; }
//        public int getTotalObtained() { return totalObtained; }
//        public int getTotalMax() { return totalMax; }
//        public double getPercentage() { return percentage; }
//        public double getAvgMarks() { return avgMarks; }
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
//                new SubjectPerformanceUI(1, mockResultsUI).setVisible(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(null,
//                    "Application failed to start!\nError: " + e.getMessage(),
//                    "Startup Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//    }
//}
