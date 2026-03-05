package com.college.sms.ui;

import com.college.sms.dao.ClassDAO;
import com.college.sms.dao.ExamDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.dao.SubjectDAO;
import com.college.sms.model.Exam;
import com.college.sms.model.Student;
import com.college.sms.model.Subject;
import com.college.sms.util.EmailService;
import com.college.sms.util.SMSService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 
 * ParentCommunicationUI — sends exam results to parents via Email/SMS.
 *
 * Navigation flow:
 *   FacultyDashboard → ResultsUI → ParentCommunicationUI
 *
 * The "Back" button returns to ResultsUI (re-shows it) and disposes this window.
 */
public class ParentCommunicationUI extends JFrame {

    // ── Navigation ──────────────────────────────────────────────────────────
    private final int     facultyId;
    private final JFrame  resultsUI;        // ← reference to ResultsUI (previously "facultyDashboard")

    // ── Filters ─────────────────────────────────────────────────────────────
    private JComboBox<String> comboClass;
    private JComboBox<String> comboSubject;
    private JComboBox<String> comboExam;

    // ── Table ────────────────────────────────────────────────────────────────
    private JTable            contactTable;
    private DefaultTableModel tableModel;

    // ── Buttons ──────────────────────────────────────────────────────────────
    private JButton btnSendSelected;
    private JButton btnSendAll;
    private JButton btnRefresh;
    private JButton btnBack;

    // ── Status bar ───────────────────────────────────────────────────────────
    private JLabel statusLabel;

    // ── Summary labels (updated dynamically) ─────────────────────────────────
    private JLabel lblSent;
    private JLabel lblPending;
    private JLabel lblHasEmail;
    private JLabel lblHasMobile;

    // ── DAOs ─────────────────────────────────────────────────────────────────
    private final ClassDAO   classDAO   = new ClassDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final ExamDAO    examDAO    = new ExamDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    // ── State ────────────────────────────────────────────────────────────────
    private int selectedStudentId = -1;
    private int currentClassId    = -1;
    private int currentSubjectId  = -1;
    private int currentExamId     = -1;
    private int currentMaxMarks   = 0;

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color PRIMARY_COLOR    = new Color(52,  73,  94);
    private static final Color PRIMARY_LIGHT    = new Color(41,  128, 185);
    private static final Color SUCCESS_COLOR    = new Color(46,  204, 113);
    private static final Color WARNING_COLOR    = new Color(243, 156, 18);
    private static final Color DANGER_COLOR     = new Color(231, 76,  60);
    private static final Color INFO_COLOR       = new Color(52,  152, 219);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR       = Color.WHITE;

    // ════════════════════════════════════════════════════════════════════════
    //  Constructor
    // ════════════════════════════════════════════════════════════════════════

    /**
     * @param facultyId  ID of the logged-in faculty member.
     * @param resultsUI  The ResultsUI window to return to when Back is pressed.
     *                   Pass {@code null} only if there is no prior window
     *                   (the constructor will still open safely).
     */
    public ParentCommunicationUI(int facultyId, JFrame resultsUI) {
        this.facultyId = facultyId;
        this.resultsUI = resultsUI;

        initComponents();
        loadClasses();
        setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UI Bootstrap
    // ════════════════════════════════════════════════════════════════════════

    private void initComponents() {
        setTitle("📧 Parent Communication Center  |  Faculty ID: " + facultyId);
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND_COLOR);

        add(buildHeader(),     BorderLayout.NORTH);
        add(buildFilterBar(),  BorderLayout.CENTER);   // wrapped in a wrapper below
        add(buildStatusBar(),  BorderLayout.SOUTH);

        // Re-arrange: header (NORTH), body (CENTER), status (SOUTH)
        // We already added header to NORTH; replace CENTER with the real body.
        remove(buildFilterBar()); // remove the one added above

        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(BACKGROUND_COLOR);
        body.add(buildFilterBar(), BorderLayout.NORTH);
        body.add(buildMainBody(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    // ── Header ───────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(0, 64));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel title = new JLabel("📧  Parent Communication Center");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // ── BACK BUTTON (navigates to ResultsUI) ────────────────────────────
        btnBack = createStyledButton("⇦  Back to Results", PRIMARY_LIGHT, Color.WHITE);
        btnBack.setPreferredSize(new Dimension(190, 40));
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setToolTipText("Return to Results UI");
        btnBack.addActionListener(e -> navigateBackToResultsUI());
        header.add(btnBack, BorderLayout.EAST);

        return header;
    }

    // ── Filter bar ───────────────────────────────────────────────────────────

    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bar.setBackground(CARD_COLOR);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                new EmptyBorder(4, 10, 4, 10)));

        bar.add(makeLabel("🏫  Class:"));
        comboClass = createStyledCombo(230);
        comboClass.addActionListener(e -> onClassSelected());
        bar.add(comboClass);

        bar.add(makeLabel("📚  Subject:"));
        comboSubject = createStyledCombo(230);
        comboSubject.addActionListener(e -> onSubjectSelected());
        bar.add(comboSubject);

        bar.add(makeLabel("📝  Exam:"));
        comboExam = createStyledCombo(270);
        comboExam.addActionListener(e -> onExamSelected());
        bar.add(comboExam);

        btnRefresh = createStyledButton("🔄  Refresh", new Color(127, 140, 141), Color.WHITE);
        btnRefresh.setPreferredSize(new Dimension(110, 36));
        btnRefresh.addActionListener(e -> loadStudents());
        bar.add(btnRefresh);

        return bar;
    }

    // ── Main body (table + side panel) ───────────────────────────────────────

    private JSplitPane buildMainBody() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTablePanel(), buildSidePanel());
        split.setDividerLocation(700);
        split.setResizeWeight(0.65);
        split.setBorder(new EmptyBorder(10, 10, 10, 10));
        split.setBackground(BACKGROUND_COLOR);
        return split;
    }

    // ── Table panel ──────────────────────────────────────────────────────────

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                new EmptyBorder(10, 10, 10, 10)));

        // Table model – column 0 is hidden (student ID)
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Roll No", "Name", "Parent Email", "Parent Mobile", "Consent", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        contactTable = new JTable(tableModel);
        applyTableStyle(contactTable);

        contactTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = contactTable.getSelectedRow();
                if (row >= 0) {
                    try {
                        selectedStudentId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                        btnSendSelected.setEnabled(true);
                    } catch (Exception ex) {
                        selectedStudentId = -1;
                        btnSendSelected.setEnabled(false);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(contactTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ── Side panel (actions + summary + tips) ────────────────────────────────

    private JPanel buildSidePanel() {
        JPanel side = new JPanel(new BorderLayout(10, 14));
        side.setBackground(CARD_COLOR);
        side.setBorder(new EmptyBorder(6, 10, 6, 6));

        // Action buttons
        JPanel actions = new JPanel(new GridLayout(2, 1, 0, 10));
        actions.setBackground(CARD_COLOR);

        btnSendSelected = createStyledButton("📧  Send to Selected", INFO_COLOR, Color.WHITE);
        btnSendSelected.setEnabled(false);
        btnSendSelected.addActionListener(e -> sendToSelectedStudents());
        actions.add(btnSendSelected);

        btnSendAll = createStyledButton("📨  Send All Eligible", SUCCESS_COLOR, Color.WHITE);
        btnSendAll.setEnabled(false);
        btnSendAll.addActionListener(e -> sendToAllEligible());
        actions.add(btnSendAll);

        side.add(actions, BorderLayout.NORTH);

        // Summary stats
        side.add(buildSummaryPanel(), BorderLayout.CENTER);

        // Tips
        JLabel tips = new JLabel("<html><b>💡  Legend</b><br><br>" +
                "✅&nbsp;Ready &nbsp;— has email + consent<br>" +
                "⚠️&nbsp;No Consent — opted out<br>" +
                "❌&nbsp;No Email — cannot send</html>");
        tips.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tips.setForeground(new Color(90, 90, 90));
        tips.setBorder(new EmptyBorder(8, 0, 0, 0));
        side.add(tips, BorderLayout.SOUTH);

        return side;
    }

    // ── Summary panel ────────────────────────────────────────────────────────

    private JPanel buildSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 8));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(INFO_COLOR, 2),
                        "  📊  Summary  ", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13), INFO_COLOR),
                new EmptyBorder(10, 10, 10, 10)));

        lblSent     = createValLabel("0", SUCCESS_COLOR);
        lblPending  = createValLabel("0", WARNING_COLOR);
        lblHasEmail = createValLabel("0", INFO_COLOR);
        lblHasMobile = createValLabel("0", PRIMARY_LIGHT);

        panel.add(buildStatCard("📧  Results Sent",  lblSent,      SUCCESS_COLOR));
        panel.add(buildStatCard("⏳  Pending",        lblPending,   WARNING_COLOR));
        panel.add(buildStatCard("📧  Has Email",      lblHasEmail,  INFO_COLOR));
        panel.add(buildStatCard("📱  Has Mobile",     lblHasMobile, PRIMARY_LIGHT));

        return panel;
    }

    private JPanel buildStatCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 2));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 1),
                new EmptyBorder(6, 10, 6, 10)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(100, 100, 100));
        card.add(lbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ── Status bar ───────────────────────────────────────────────────────────

    private JLabel buildStatusBar() {
        statusLabel = new JLabel("  Ready.  Select class → subject → exam to load students.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(75, 75, 75));
        statusLabel.setBackground(new Color(236, 240, 241));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(new EmptyBorder(7, 14, 7, 14));
        return statusLabel;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  BACK NAVIGATION  ←  KEY FIX
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Disposes this window and returns the user to ResultsUI.
     * If the ResultsUI reference is null (edge case), falls back to FacultyDashboard.
     */
    private void navigateBackToResultsUI() {
        dispose();   // close ParentCommunicationUI

        SwingUtilities.invokeLater(() -> {
            if (resultsUI != null) {
                resultsUI.setVisible(true);
                resultsUI.toFront();
                resultsUI.requestFocus();
                System.out.println("✅ Returned to ResultsUI");
            } else {
                // Fallback: open a fresh ResultsUI
                System.err.println("⚠️ resultsUI reference is null — opening new ResultsUI");
                new ResultsUI(facultyId, null).setVisible(true);
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    //  COMBO CHANGE HANDLERS
    // ════════════════════════════════════════════════════════════════════════

    private void onClassSelected() {
        Object sel = comboClass.getSelectedItem();
        if (sel == null || sel.toString().contains("--")) return;
        try {
            currentClassId = Integer.parseInt(sel.toString().split(" - ")[0].trim());
            loadSubjects();
        } catch (NumberFormatException ignored) {}
    }

    private void onSubjectSelected() {
        Object sel = comboSubject.getSelectedItem();
        if (sel == null || sel.toString().contains("--")) return;
        try {
            currentSubjectId = Integer.parseInt(sel.toString().split(" - ")[0].trim());
            loadExams();
        } catch (NumberFormatException ignored) {}
    }

    private void onExamSelected() {
        Object sel = comboExam.getSelectedItem();
        if (sel == null || sel.toString().contains("--")) return;
        try {
            currentExamId = Integer.parseInt(sel.toString().split(":")[0].trim());
            Exam exam = examDAO.getExamById(currentExamId);
            if (exam != null) currentMaxMarks = exam.getMaxMarks();
            loadStudents();
        } catch (Exception ex) {
            System.err.println("Exam load error: " + ex.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DATA LOADING
    // ════════════════════════════════════════════════════════════════════════

    private void loadClasses() {
        comboClass.removeAllItems();
        comboSubject.removeAllItems();
        comboExam.removeAllItems();
        tableModel.setRowCount(0);
        updateStatus("Loading classes…", WARNING_COLOR);

        try {
            List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
            if (classes.isEmpty()) {
                comboClass.addItem("-- No classes assigned --");
                updateStatus("⚠️  No classes assigned to this faculty.", WARNING_COLOR);
            } else {
                for (String[] c : classes)
                    comboClass.addItem(c[0] + " - " + c[1]);
                comboClass.setSelectedIndex(0);
                loadSubjects();
            }
        } catch (Exception e) {
            comboClass.addItem("-- Error loading --");
            updateStatus("❌  " + e.getMessage(), DANGER_COLOR);
            e.printStackTrace();
        }
    }

    private void loadSubjects() {
        comboSubject.removeAllItems();
        comboExam.removeAllItems();
        tableModel.setRowCount(0);

        Object sel = comboClass.getSelectedItem();
        if (sel == null || sel.toString().contains("--")) {
            comboSubject.addItem("-- Select a class first --");
            return;
        }
        try {
            int classId = Integer.parseInt(sel.toString().split(" - ")[0].trim());
            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
            if (subjects.isEmpty()) {
                comboSubject.addItem("-- No subjects found --");
            } else {
                for (Subject s : subjects)
                    comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
                comboSubject.setSelectedIndex(0);
                loadExams();
            }
        } catch (Exception e) {
            comboSubject.addItem("-- Error --");
            e.printStackTrace();
        }
    }

    private void loadExams() {
        comboExam.removeAllItems();
        tableModel.setRowCount(0);

        Object selSub = comboSubject.getSelectedItem();
        Object selCls = comboClass.getSelectedItem();
        if (selSub == null || selSub.toString().contains("--")
         || selCls == null || selCls.toString().contains("--")) {
            comboExam.addItem("-- Select a subject first --");
            return;
        }
        try {
            int subjectId = Integer.parseInt(selSub.toString().split(" - ")[0].trim());
            int classId   = Integer.parseInt(selCls.toString().split(" - ")[0].trim());
            List<Exam> exams = examDAO.getExamsByFacultyClassSubject(facultyId, classId, subjectId);
            if (exams.isEmpty()) {
                comboExam.addItem("-- No exams found --");
            } else {
                for (Exam ex : exams)
                    comboExam.addItem(ex.getExamId() + ":" + ex.getExamName()
                            + "  (Max: " + ex.getMaxMarks() + ")");
                comboExam.setSelectedIndex(0);
            }
        } catch (Exception e) {
            comboExam.addItem("-- Error --");
            e.printStackTrace();
        }
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        btnSendSelected.setEnabled(false);
        btnSendAll.setEnabled(false);
        selectedStudentId = -1;

        Object selExam = comboExam.getSelectedItem();
        if (selExam == null || selExam.toString().contains("--")) {
            updateStatus("⚠️  Please select an exam to load students.", WARNING_COLOR);
            return;
        }

        updateStatus("Loading students…", INFO_COLOR);

        try {
            List<String[]> students = studentDAO.getStudentsByClass(currentClassId);
            if (students.isEmpty()) {
                updateStatus("ℹ️  No students found in this class.", INFO_COLOR);
                resetSummary(0, 0, 0, 0, 0);
                return;
            }

            int total = 0, ready = 0, noConsent = 0, noContact = 0, hasEmail = 0, hasMobile = 0;

            for (String[] s : students) {
                int    studentId = Integer.parseInt(s[0]);
                String rollNo    = s[1];
                String name      = s[2];

                Student student = studentDAO.getStudentById(studentId);
                if (student == null) continue;

                String  email   = nullSafe(student.getParentEmail());
                String  mobile  = nullSafe(student.getParentMobile());
                boolean consent = student.getConsentToCommunicate() == null
                                  || student.getConsentToCommunicate();

                boolean hasEm = !email.isEmpty();
                boolean hasMb = !mobile.isEmpty();
                if (hasEm) hasEmail++;
                if (hasMb) hasMobile++;

                String status;
                if (!hasEm) {
                    status = "❌ No Email";
                    noContact++;
                } else if (!consent) {
                    status = "⚠️ No Consent";
                    noConsent++;
                } else {
                    status = "✅ Ready";
                    ready++;
                }

                tableModel.addRow(new Object[]{
                    studentId, rollNo, name,
                    hasEm ? email  : "—",
                    hasMb ? mobile : "—",
                    consent ? "✅ Yes" : "❌ No",
                    status
                });
                total++;
            }

            resetSummary(0, total - ready, hasEmail, hasMobile, total);
            lblSent.setText("0");
            lblPending.setText(String.valueOf(total - ready));
            lblHasEmail.setText(String.valueOf(hasEmail));
            lblHasMobile.setText(String.valueOf(hasMobile));

            updateStatus(String.format(
                    "✓  Loaded %d students  |  Ready: %d  |  Has Email: %d  |  Has Mobile: %d",
                    total, ready, hasEmail, hasMobile), SUCCESS_COLOR);

            if (ready > 0) btnSendAll.setEnabled(true);

        } catch (Exception e) {
            updateStatus("❌  Error loading students: " + e.getMessage(), DANGER_COLOR);
            e.printStackTrace();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SENDING
    // ════════════════════════════════════════════════════════════════════════

    private void sendToSelectedStudents() {
        if (selectedStudentId <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a student first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Integer> ids = new ArrayList<>();
        ids.add(selectedStudentId);
        sendResults(ids);
    }

    private void sendToAllEligible() {
        List<Integer> eligible = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ("✅ Ready".equals(tableModel.getValueAt(i, 6))) {
                eligible.add(Integer.parseInt(tableModel.getValueAt(i, 0).toString()));
            }
        }
        if (eligible.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No eligible students to send results to.",
                    "Nothing to Send", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Send exam results to " + eligible.size() + " eligible student(s)?",
                "Confirm Bulk Send", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) sendResults(eligible);
    }

    private void sendResults(List<Integer> studentIds) {
        if (studentIds.isEmpty()) return;

        // Collect context strings once
        String examCombo    = comboExam.getSelectedItem().toString();
        String examName     = examCombo.split(":")[1].split("\\(")[0].trim();
        String className    = comboClass.getSelectedItem().toString().split(" - ", 2)[1].trim();
        String subjectName  = comboSubject.getSelectedItem().toString().split(" - ", 2)[1].trim();
        int    examId       = currentExamId;
        int    maxMarks     = currentMaxMarks;

        // Progress dialog
        JDialog progressDialog = new JDialog(this, "Sending Results…", true);
        JProgressBar bar = new JProgressBar(0, studentIds.size());
        bar.setStringPainted(true);
        bar.setPreferredSize(new Dimension(320, 30));
        JLabel progressMsg = new JLabel("  Preparing…", SwingConstants.LEFT);
        progressMsg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel pd = new JPanel(new BorderLayout(10, 10));
        pd.setBorder(new EmptyBorder(20, 20, 20, 20));
        pd.add(bar, BorderLayout.CENTER);
        pd.add(progressMsg, BorderLayout.SOUTH);
        progressDialog.setContentPane(pd);
        progressDialog.pack();
        progressDialog.setLocationRelativeTo(this);

        AtomicInteger emailSent = new AtomicInteger();
        AtomicInteger smsSent   = new AtomicInteger();
        AtomicInteger failed    = new AtomicInteger();

        Thread worker = new Thread(() -> {
            for (int sid : studentIds) {
                try {
                    // Look up row data from table model
                    String rollNo = "", name = "", email = "", mobile = "";
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (Integer.parseInt(tableModel.getValueAt(i, 0).toString()) == sid) {
                            rollNo = tableModel.getValueAt(i, 1).toString();
                            name   = tableModel.getValueAt(i, 2).toString();
                            email  = tableModel.getValueAt(i, 3).toString().replace("—", "");
                            mobile = tableModel.getValueAt(i, 4).toString().replace("—", "");
                            break;
                        }
                    }

                    boolean hasEm = !email.isEmpty();
                    boolean hasMb = !mobile.isEmpty();

                    if (!hasEm && !hasMb) {
                        failed.incrementAndGet();
                        continue;
                    }

                    // Fetch marks
                    int marks = 0; String result = "Fail"; String grade = "F"; double pct = 0;
                    try {
                        marks = studentDAO.getMarksByExam(sid, examId);
                        pct   = maxMarks > 0 ? (marks * 100.0 / maxMarks) : 0;
                        int pass = examDAO.getExamById(examId).getPassMarks();
                        result = marks >= pass ? "Pass" : "Fail";
                        grade  = calculateGrade(marks, maxMarks);
                    } catch (Exception ex) {
                        System.err.println("Marks fetch error for student " + sid + ": " + ex.getMessage());
                    }

                    // Send Email
                    if (hasEm) {
                        try {
                            boolean ok = EmailService.sendResultEmail(
                                    email, name, rollNo, className, subjectName,
                                    examName, marks, maxMarks, pct, result, grade);
                            if (ok) emailSent.incrementAndGet();
                            else    failed.incrementAndGet();
                        } catch (Exception ex) {
                            failed.incrementAndGet();
                            System.err.println("Email error: " + ex.getMessage());
                        }
                    }

                    // Send SMS
                    if (hasMb) {
                        try {
                            boolean ok = SMSService.sendResultSMS(
                                    mobile, name, rollNo, className, subjectName,
                                    examName, marks, maxMarks, pct, result, grade);
                            if (ok) smsSent.incrementAndGet();
                        } catch (Exception ex) {
                            System.err.println("SMS error: " + ex.getMessage());
                        }
                    }

                    // Update progress bar
                    int done = emailSent.get() + failed.get();
                    String rowName = name;
                    SwingUtilities.invokeLater(() -> {
                        bar.setValue(done);
                        bar.setString(done + " / " + studentIds.size());
                        progressMsg.setText("  Sent to: " + rowName);
                    });

                    Thread.sleep(250);  // slight throttle

                } catch (Exception ex) {
                    failed.incrementAndGet();
                    ex.printStackTrace();
                }
            }

            // Finish up on EDT
            int es = emailSent.get(), ss = smsSent.get(), f = failed.get(), total = studentIds.size();
            SwingUtilities.invokeLater(() -> {
                progressDialog.dispose();

                // Update summary cards
                int prevSent = parseLabelInt(lblSent);
                lblSent.setText(String.valueOf(prevSent + es));
                lblPending.setText(String.valueOf(Math.max(0, parseLabelInt(lblPending) - es)));

                JOptionPane.showMessageDialog(ParentCommunicationUI.this,
                        String.format(
                                "<html><b>✅  Send Complete</b><br><br>" +
                                "📧  Emails sent : <b>%d</b><br>" +
                                "📱  SMS sent    : <b>%d</b><br>" +
                                "❌  Failed      : <b>%d</b><br>" +
                                "📊  Total       : <b>%d</b></html>",
                                es, ss, f, total),
                        "Send Results — Done", JOptionPane.INFORMATION_MESSAGE);

                updateStatus(String.format(
                        "✓  Email sent: %d  |  SMS sent: %d  |  Failed: %d", es, ss, f),
                        SUCCESS_COLOR);
            });
        });

        worker.setDaemon(true);
        worker.start();

        // Show progress dialog (blocks EDT until worker finishes and calls dispose())
        progressDialog.setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════════════

    private String calculateGrade(int marks, int max) {
        if (max <= 0) return "N/A";
        double pct = marks * 100.0 / max;
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B";
        if (pct >= 60) return "C";
        if (pct >= 50) return "D";
        return "F";
    }

    private void resetSummary(int sent, int pending, int hasEmail, int hasMobile, int total) {
        lblSent.setText(String.valueOf(sent));
        lblPending.setText(String.valueOf(pending));
        lblHasEmail.setText(String.valueOf(hasEmail));
        lblHasMobile.setText(String.valueOf(hasMobile));
    }

    private void updateStatus(String msg, Color color) {
        if (statusLabel != null) {
            statusLabel.setText("  " + msg);
            statusLabel.setForeground(color);
        }
    }

    private static String nullSafe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static int parseLabelInt(JLabel lbl) {
        try { return Integer.parseInt(lbl.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    // ── Widget factory helpers ────────────────────────────────────────────────

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JComboBox<String> createStyledCombo(int width) {
        JComboBox<String> cb = new JComboBox<>();
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setPreferredSize(new Dimension(width, 34));
        cb.setBackground(Color.WHITE);
        return cb;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(70, 70, 70));
        return lbl;
    }

    private JLabel createValLabel(String value, Color color) {
        JLabel lbl = new JLabel(value, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(color);
        return lbl;
    }

    // ── Table styling ────────────────────────────────────────────────────────

    private void applyTableStyle(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(235, 235, 235));
        table.setSelectionBackground(INFO_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Status column renderer (column 6)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected && col == 6 && value != null) {
                    String v = value.toString();
                    if (v.startsWith("✅")) {
                        c.setBackground(new Color(232, 245, 233));
                        c.setForeground(new Color(27,  94,  32));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if (v.startsWith("⚠️")) {
                        c.setBackground(new Color(255, 248, 225));
                        c.setForeground(new Color(130, 90,  0));
                    } else {
                        c.setBackground(new Color(255, 235, 238));
                        c.setForeground(new Color(183, 28,  28));
                    }
                } else if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        // Hide ID column (col 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumnModel().getColumn(1).setPreferredWidth(85);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(75);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ENTRY POINT  (for isolated testing only)
    // ════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            // Mock ResultsUI window for standalone testing
            JFrame mockResultsUI = new JFrame("Results UI (mock)");
            mockResultsUI.setSize(1000, 650);
            mockResultsUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mockResultsUI.setLocationRelativeTo(null);
            mockResultsUI.setVisible(false); // hidden until Back is pressed

            new ParentCommunicationUI(1, mockResultsUI);
        });
    }
}