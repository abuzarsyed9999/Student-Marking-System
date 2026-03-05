package com.college.sms.ui;

import com.college.sms.dao.ClassDAO;
import com.college.sms.dao.ExamDAO;
import com.college.sms.dao.RemedialDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.dao.SubjectDAO;
import com.college.sms.model.Exam;
import com.college.sms.model.Student;
import com.college.sms.model.Subject;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;

public class RemedialManagementUI extends JFrame {

    private int facultyId;
    private JFrame previousUI;
    private JComboBox<String> comboClass;
    private JComboBox<String> comboSubject;
    private JComboBox<String> comboExam;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JButton btnBack, btnAddRemedial, btnRefresh;

    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;
    private ExamDAO examDAO;
    private StudentDAO studentDAO;
    private RemedialDAO remedialDAO;

    private int currentClassId   = -1;
    private int currentSubjectId = -1;
    private int currentExamId    = -1;
    private int currentMaxMarks  = 0;
    private int currentPassMarks = 0;

    private static final Color PRIMARY_COLOR    = new Color(52,  73,  94);
    private static final Color PRIMARY_LIGHT    = new Color(41,  128, 185);
    private static final Color SUCCESS_COLOR    = new Color(46,  204, 113);
    private static final Color WARNING_COLOR    = new Color(243, 156, 18);
    private static final Color DANGER_COLOR     = new Color(231, 76,  60);
    private static final Color INFO_COLOR       = new Color(52,  152, 219);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR       = Color.WHITE;

    // =========================================================
    // BUTTON RENDERER — purely visual, no interaction
    // =========================================================
    private static class ButtonRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 4));
            panel.setBackground(isSelected ? INFO_COLOR : Color.WHITE);

            JButton btn = new JButton("🗑️ Remove");
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setForeground(Color.WHITE);
            btn.setBackground(DANGER_COLOR);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            panel.add(btn);
            return panel;
        }
    }

    // =========================================================
    // BUTTON EDITOR — handles actual click interaction
    // =========================================================
    private class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel   panel  = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 4));
        private final JButton  button = new JButton("🗑️ Remove");
        private int            clickedRow = -1;

        ButtonEditor() {
            button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            button.setForeground(Color.WHITE);
            button.setBackground(DANGER_COLOR);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            panel.setBackground(Color.WHITE);
            panel.add(button);

            button.addActionListener(e -> {
                // Stop editing first so the row index is valid
                fireEditingStopped();
                if (clickedRow >= 0) {
                    // Convert view row to model row (handles sorting)
                    int modelRow = studentTable.convertRowIndexToModel(clickedRow);
                    removeStudentFromRemedial(modelRow);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            clickedRow = row;          // capture the view-row at edit time
            panel.setBackground(isSelected ? INFO_COLOR : Color.WHITE);
            return panel;
        }

        @Override public Object getCellEditorValue()              { return ""; }
        @Override public boolean isCellEditable(EventObject e)    { return true; }
        @Override public boolean shouldSelectCell(EventObject e)  { return true; }
    }

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public RemedialManagementUI(int facultyId, JFrame previousUI) {
        this.facultyId  = facultyId;
        this.previousUI = previousUI;
        classDAO    = new ClassDAO();
        subjectDAO  = new SubjectDAO();
        examDAO     = new ExamDAO();
        studentDAO  = new StudentDAO();
        remedialDAO = new RemedialDAO();

        initComponents();
        loadClasses();
        setVisible(true);
    }

    // =========================================================
    // UI INIT
    // =========================================================
    private void initComponents() {
        setTitle("🔧 Remedial Tracking | Faculty ID: " + facultyId);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND_COLOR);

        // ---- NORTH WRAPPER ----
        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = new JLabel("🔧 Remedial Tracking Center");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        btnBack = createButton("⇦ Back to Dashboard", PRIMARY_LIGHT, Color.WHITE);
        btnBack.setPreferredSize(new Dimension(190, 38));
        btnBack.addActionListener(e -> navigateBack());
        headerPanel.add(btnBack, BorderLayout.EAST);

        northWrapper.add(headerPanel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(CARD_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                new EmptyBorder(5, 15, 5, 15)));

        filterPanel.add(createLabel("🏫 Class:"));
        comboClass = createComboBox();
        comboClass.addActionListener(e -> {
            if (comboClass.getSelectedItem() != null
                    && !comboClass.getSelectedItem().toString().contains("--")) {
                currentClassId = Integer.parseInt(
                        comboClass.getSelectedItem().toString().split(" - ")[0].trim());
                loadSubjects();
            }
        });
        filterPanel.add(comboClass);

        filterPanel.add(createLabel("📚 Subject:"));
        comboSubject = createComboBox();
        comboSubject.addActionListener(e -> {
            if (comboSubject.getSelectedItem() != null
                    && !comboSubject.getSelectedItem().toString().contains("--")) {
                currentSubjectId = Integer.parseInt(
                        comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
                loadExams();
            }
        });
        filterPanel.add(comboSubject);

        filterPanel.add(createLabel("📝 Exam:"));
        comboExam = createComboBox();
        comboExam.addActionListener(e -> {
            if (comboExam.getSelectedItem() != null
                    && !comboExam.getSelectedItem().toString().contains("--")) {
                currentExamId = Integer.parseInt(
                        comboExam.getSelectedItem().toString().split(":")[0].trim());
                try {
                    Exam exam = examDAO.getExamById(currentExamId);
                    currentMaxMarks  = exam.getMaxMarks();
                    currentPassMarks = exam.getPassMarks();
                } catch (Exception ex) {
                    System.err.println("Error loading exam: " + ex.getMessage());
                }
                loadFailedStudents();
            }
        });
        filterPanel.add(comboExam);

        btnRefresh = createButton("🔄 Refresh", new Color(149, 165, 166), Color.WHITE);
        btnRefresh.setPreferredSize(new Dimension(100, 34));
        btnRefresh.addActionListener(e -> loadFailedStudents());
        filterPanel.add(btnRefresh);

        northWrapper.add(filterPanel, BorderLayout.CENTER);
        add(northWrapper, BorderLayout.NORTH);

        // ---- CENTER TABLE ----
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Columns: 0=RemedialID(hidden), 1=StudentID(hidden), 2=RollNo, 3=Name,
        //          4=Marks, 5=MaxMarks, 6=PassMarks, 7=Result, 8=Status, 9=Actions
        tableModel = new DefaultTableModel(
                new String[]{"Remedial ID", "ID", "Roll No", "Name",
                             "Marks", "Max Marks", "Pass Marks", "Result", "Status", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 9; // Only Actions column is editable (triggers ButtonEditor)
            }
        };

        studentTable = new JTable(tableModel);
        styleTable(studentTable);

        // Hide Remedial ID (col 0) and Student ID (col 1)
        hideColumn(0);
        hideColumn(1);

        // Assign renderer + editor to Actions column
        studentTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        studentTable.getColumn("Actions").setCellEditor(new ButtonEditor());

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnAddRemedial.setEnabled(studentTable.getSelectedRow() >= 0);
            }
        });

        JScrollPane tableScroll = new JScrollPane(studentTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        // Action row below table
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setBackground(CARD_COLOR);

        btnAddRemedial = createButton("➕ Add to Remedial", new Color(142, 68, 173), Color.WHITE);
        btnAddRemedial.setPreferredSize(new Dimension(160, 36));
        btnAddRemedial.setEnabled(false);
        btnAddRemedial.addActionListener(e -> addToRemedial());
        actionPanel.add(btnAddRemedial);

        tablePanel.add(actionPanel, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.CENTER);

        // ---- SOUTH STATUS ----
        statusLabel = new JLabel("Ready. Select class, subject & exam to view failed students.");
        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setBackground(new Color(236, 240, 241));
        statusLabel.setForeground(new Color(75, 75, 75));
        statusLabel.setOpaque(true);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(statusLabel, BorderLayout.SOUTH);
    }

    /** Zero out a column so it is invisible but still accessible in the model. */
    private void hideColumn(int col) {
        studentTable.getColumnModel().getColumn(col).setMinWidth(0);
        studentTable.getColumnModel().getColumn(col).setMaxWidth(0);
        studentTable.getColumnModel().getColumn(col).setWidth(0);
    }

    // =========================================================
    // BACK NAVIGATION
    // =========================================================
    private void navigateBack() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            if (previousUI != null) {
                previousUI.setVisible(true);
                previousUI.toFront();
                previousUI.requestFocus();
            } else {
                new FacultyDashboard(facultyId).setVisible(true);
            }
        });
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(220, 34));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        return combo;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(235, 235, 235));
        table.setSelectionBackground(INFO_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected && column == 7) {
                    if ("Fail".equals(value != null ? value.toString() : "")) {
                        c.setBackground(new Color(255, 235, 238));
                        c.setForeground(new Color(183, 28, 28));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                } else if (!isSelected) {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(120);
        table.getColumnModel().getColumn(9).setPreferredWidth(120);
    }

    private void updateStatus(String msg, Color color) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
            statusLabel.setForeground(color);
        }
    }

    // =========================================================
    // DATA LOADING
    // =========================================================
    private void loadClasses() {
        comboClass.removeAllItems();
        comboSubject.removeAllItems();
        comboExam.removeAllItems();
        tableModel.setRowCount(0);
        updateStatus("Loading classes...", WARNING_COLOR);

        try {
            List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
            if (classes.isEmpty()) {
                comboClass.addItem("-- No classes --");
                updateStatus("⚠️ No classes assigned", WARNING_COLOR);
            } else {
                for (String[] c : classes) comboClass.addItem(c[0] + " - " + c[1]);
                comboClass.setSelectedIndex(0);
                loadSubjects();
            }
        } catch (Exception e) {
            comboClass.addItem("-- Error --");
            updateStatus("❌ " + e.getMessage(), DANGER_COLOR);
        }
    }

    private void loadSubjects() {
        comboSubject.removeAllItems();
        comboExam.removeAllItems();
        tableModel.setRowCount(0);

        if (comboClass.getSelectedItem() == null
                || comboClass.getSelectedItem().toString().contains("--")) {
            comboSubject.addItem("-- Select class --");
            return;
        }
        try {
            int classId = Integer.parseInt(
                    comboClass.getSelectedItem().toString().split(" - ")[0]);
            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
            if (subjects.isEmpty()) {
                comboSubject.addItem("-- No subjects --");
            } else {
                for (Subject s : subjects)
                    comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
                comboSubject.setSelectedIndex(0);
                loadExams();
            }
        } catch (Exception e) {
            comboSubject.addItem("-- Error --");
        }
    }

    private void loadExams() {
        comboExam.removeAllItems();
        tableModel.setRowCount(0);

        if (comboSubject.getSelectedItem() == null
                || comboSubject.getSelectedItem().toString().contains("--")) {
            comboExam.addItem("-- Select subject --");
            return;
        }
        try {
            int subjectId = Integer.parseInt(
                    comboSubject.getSelectedItem().toString().split(" - ")[0]);
            int classId   = Integer.parseInt(
                    comboClass.getSelectedItem().toString().split(" - ")[0]);
            List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, classId, subjectId);
            if (exams.isEmpty()) {
                comboExam.addItem("-- No exams --");
            } else {
                for (Exam ex : exams)
                    comboExam.addItem(ex.getExamId() + ":" + ex.getExamName()
                            + " (Max: " + ex.getMaxMarks() + ", Pass: " + ex.getPassMarks() + ")");
                comboExam.setSelectedIndex(0);
            }
        } catch (Exception e) {
            comboExam.addItem("-- Error --");
        }
    }

    private void loadFailedStudents() {
        tableModel.setRowCount(0);
        btnAddRemedial.setEnabled(false);

        if (comboExam.getSelectedItem() == null
                || comboExam.getSelectedItem().toString().contains("--")) {
            updateStatus("⚠️ Select exam first", WARNING_COLOR);
            return;
        }

        try {
            List<String[]> students = studentDAO.getStudentsByClass(currentClassId);
            if (students.isEmpty()) {
                updateStatus("ℹ️ No students", INFO_COLOR);
                return;
            }

            int failedCount = 0;
            for (String[] s : students) {
                int    studentId = Integer.parseInt(s[0]);
                String rollNo    = s[1];
                String name      = s[2];
                int    marks     = studentDAO.getMarksByExam(studentId, currentExamId);

                if (marks < currentPassMarks) {
                    int    remedialId = getRemedialId(studentId, currentSubjectId, currentExamId);
                    String status     = remedialDAO.hasActiveRemedial(studentId, currentSubjectId)
                            ? "🔧 Already in Remedial"
                            : "⚪ Add to Remedial";

                    tableModel.addRow(new Object[]{
                            remedialId,       // col 0 – hidden
                            studentId,        // col 1 – hidden
                            rollNo,           // col 2
                            name,             // col 3
                            marks,            // col 4
                            currentMaxMarks,  // col 5
                            currentPassMarks, // col 6
                            "Fail",           // col 7
                            status,           // col 8
                            ""                // col 9 – Actions
                    });
                    failedCount++;
                }
            }

            updateStatus(failedCount == 0
                    ? "✓ No failed students for this exam"
                    : String.format("✓ Found %d failed student(s) | Select one to add to remedial", failedCount),
                    SUCCESS_COLOR);

        } catch (Exception e) {
            updateStatus("❌ " + e.getMessage(), DANGER_COLOR);
            e.printStackTrace();
        }
    }

    private int getRemedialId(int studentId, int subjectId, int examId) {
        try {
            java.sql.Connection conn = com.college.sms.util.DBConnection.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT remedial_id FROM remedial_tracking " +
                    "WHERE student_id = ? AND subject_id = ? AND failed_exam_id = ?");
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.setInt(3, examId);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("remedial_id");
                rs.close(); pstmt.close(); conn.close();
                return id;
            }
            rs.close(); pstmt.close(); conn.close();
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    // =========================================================
    // ADD TO REMEDIAL
    // =========================================================
    private void addToRemedial() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student first!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view row → model row
        int modelRow  = studentTable.convertRowIndexToModel(row);
        int studentId = Integer.parseInt(tableModel.getValueAt(modelRow, 1).toString());
        String rollNo = tableModel.getValueAt(modelRow, 2).toString();
        String name   = tableModel.getValueAt(modelRow, 3).toString();
        int    marks  = Integer.parseInt(tableModel.getValueAt(modelRow, 4).toString());
        String status = tableModel.getValueAt(modelRow, 8).toString();

        if (status.contains("Already")) {
            JOptionPane.showMessageDialog(this,
                    name + " (" + rollNo + ") is already in remedial tracking!",
                    "Already Added", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>➕ Add to Remedial Tracking</b><br><br>"
                + "Student: <b>" + name + "</b> (" + rollNo + ")<br>"
                + "Exam: <b>" + comboExam.getSelectedItem() + "</b><br>"
                + "Marks: <b>" + marks + "/" + currentMaxMarks + "</b><br><br>"
                + "This will:<br>"
                + "• Track this student's progress<br>"
                + "• Allow scheduling remedial sessions<br>"
                + "• Measure improvement in future exams<br><br>"
                + "<b>Add to remedial tracking?</b></html>",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean success = remedialDAO.addToRemedial(
                    studentId, currentExamId, currentSubjectId, currentClassId,
                    facultyId, marks, currentMaxMarks, currentPassMarks,
                    "Added via Remedial Management UI");

            if (success) {
                tableModel.setValueAt("🔧 Added", modelRow, 8);
                btnAddRemedial.setEnabled(false);
                updateStatus("✓ Added " + name + " to remedial tracking", SUCCESS_COLOR);
                JOptionPane.showMessageDialog(this,
                        "✅ Successfully added to remedial tracking!\n\n"
                        + "Next steps:\n"
                        + "• Schedule remedial sessions\n"
                        + "• Track attendance\n"
                        + "• Update progress after next exam",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                updateStatus("❌ Failed to add", DANGER_COLOR);
                JOptionPane.showMessageDialog(this, "Failed to add student to remedial tracking",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            updateStatus("❌ " + e.getMessage(), DANGER_COLOR);
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // =========================================================
    // REMOVE FROM REMEDIAL  (called from ButtonEditor)
    // =========================================================
    private void removeStudentFromRemedial(int modelRow) {
        try {
            Object remedialIdObj = tableModel.getValueAt(modelRow, 0);
            int remedialId = remedialIdObj != null
                    ? Integer.parseInt(remedialIdObj.toString()) : -1;

            String studentName = tableModel.getValueAt(modelRow, 3).toString();
            String rollNo      = tableModel.getValueAt(modelRow, 2).toString();

            if (remedialId == -1) {
                JOptionPane.showMessageDialog(this,
                        studentName + " (" + rollNo + ") has not been added to remedial tracking yet.",
                        "Not in Remedial", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "<html><b>🗑️ Remove from Remedial Tracking?</b><br><br>"
                    + "Student: <b>" + studentName + "</b> (" + rollNo + ")<br><br>"
                    + "⚠️ This will:<br>"
                    + "• Remove student from remedial tracking<br>"
                    + "• Delete all associated remedial sessions<br>"
                    + "• Delete all attendance records<br>"
                    + "• Delete all marks entries<br><br>"
                    + "<b>This action CANNOT be undone!</b><br><br>"
                    + "Are you sure you want to proceed?</html>",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) return;

            boolean success = remedialDAO.removeFromRemedial(remedialId);

            if (success) {
                tableModel.removeRow(modelRow);
                updateStatus("✓ Removed " + studentName + " from remedial tracking", SUCCESS_COLOR);
                JOptionPane.showMessageDialog(this,
                        "✅ Student removed from remedial tracking successfully!",
                        "Removed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                updateStatus("❌ Failed to remove", DANGER_COLOR);
                JOptionPane.showMessageDialog(this,
                        "Failed to remove student from remedial tracking",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            updateStatus("❌ " + e.getMessage(), DANGER_COLOR);
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame mock = new JFrame("Faculty Dashboard");
            mock.setSize(1000, 650);
            mock.setVisible(true);
            new RemedialManagementUI(1, mock);
        });
    }
}