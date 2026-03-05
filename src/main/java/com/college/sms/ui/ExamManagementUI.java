//package com.college.sms.ui;
//
//import com.college.sms.dao.ExamDAO;
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ExamManagementUI extends JFrame {
//
//    private JTextField txtExamName, txtMaxMarks, txtPassMarks;
//    private JTable examTable;
//    private DefaultTableModel tableModel;
//
//    private ExamDAO examDAO;
//    private int facultyId;
//    private JFrame previousUI; // reference to dashboard
//
//    public ExamManagementUI(int facultyId, JFrame previousUI) throws SQLException {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        examDAO = new ExamDAO();
//        initUI();
//    }
//
//    private void initUI() {
//        setTitle("Exam Management");
//        setSize(700, 500);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout(10, 10));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        // ===== Top Panel =====
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//        txtExamName = new JTextField(10);
//        txtMaxMarks = new JTextField(5);
//        txtPassMarks = new JTextField(5);
//
//        JButton btnAdd = new JButton("Add Exam");
//        JButton btnUpdate = new JButton("Update Exam");
//        JButton btnDelete = new JButton("Delete Exam");
//        JButton btnBack = new JButton("Back");
//
//        topPanel.add(new JLabel("Exam Name:"));
//        topPanel.add(txtExamName);
//        topPanel.add(new JLabel("Max Marks:"));
//        topPanel.add(txtMaxMarks);
//        topPanel.add(new JLabel("Pass Marks:"));
//        topPanel.add(txtPassMarks);
//        topPanel.add(btnAdd);
//        topPanel.add(btnUpdate);
//        topPanel.add(btnDelete);
//        topPanel.add(btnBack);
//
//        add(topPanel, BorderLayout.NORTH);
//
//        // ===== Table =====
//        tableModel = new DefaultTableModel(
//                new String[]{"ID", "Exam Name", "Max Marks", "Pass Marks"}, 0
//        ) {
//            @Override
//            public boolean isCellEditable(int row, int col) { return false; }
//        };
//        examTable = new JTable(tableModel);
//        add(new JScrollPane(examTable), BorderLayout.CENTER);
//
//        loadExams();
//
//        // ===== Button Actions =====
//        btnAdd.addActionListener(e -> addExam());
//        btnUpdate.addActionListener(e -> updateExam());
//        btnDelete.addActionListener(e -> deleteExam());
//        btnBack.addActionListener(e -> {
//            dispose();
//            previousUI.setVisible(true); // back to dashboard
//        });
//
//        examTable.getSelectionModel().addListSelectionListener(e -> {
//            int row = examTable.getSelectedRow();
//            if (row >= 0) {
//                txtExamName.setText(tableModel.getValueAt(row, 1).toString());
//                txtMaxMarks.setText(tableModel.getValueAt(row, 2).toString());
//                txtPassMarks.setText(tableModel.getValueAt(row, 3).toString());
//            }
//        });
//
//        setVisible(true);
//    }
//
//    private void loadExams() {
//        tableModel.setRowCount(0);
//        List<String[]> exams = examDAO.getExamsByFaculty(facultyId); // method we will create
//        for (String[] e : exams) {
//            tableModel.addRow(new Object[]{e[0], e[1], e[2], e[3]});
//        }
//    }
//
//    private void addExam() {
//        try {
//            String name = txtExamName.getText().trim();
//            int max = Integer.parseInt(txtMaxMarks.getText().trim());
//            int pass = Integer.parseInt(txtPassMarks.getText().trim());
//            if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter exam name"); return; }
//            examDAO.addExam(name, max, pass, facultyId);
//            clearFields();
//            loadExams();
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Enter valid numbers for marks");
//        }
//    }
//
//    private void updateExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an exam to update"); return; }
//        try {
//            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//            String name = txtExamName.getText().trim();
//            int max = Integer.parseInt(txtMaxMarks.getText().trim());
//            int pass = Integer.parseInt(txtPassMarks.getText().trim());
//            examDAO.updateExam(id, name, max, pass);
//            clearFields();
//            loadExams();
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Enter valid numbers for marks");
//        }
//    }
//
//    private void deleteExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an exam to delete"); return; }
//        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//        examDAO.deleteExam(id);
//        clearFields();
//        loadExams();
//    }
//
//    private void clearFields() {
//        txtExamName.setText("");
//        txtMaxMarks.setText("");
//        txtPassMarks.setText("");
//        examTable.clearSelection();
//    }
//
//}
//
//package com.college.sms.ui;
//
//import com.college.sms.dao.ExamDAO;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ExamManagementUI extends JFrame {
//
//    private JComboBox<String> comboClass;
//    private JTextField txtExamName, txtMaxMarks, txtPassMarks;
//    private JTable examTable;
//    private DefaultTableModel tableModel;
//    private JComboBox<String> comboSubject;
//
//    private ExamDAO examDAO;
//    private int facultyId;
//    private JFrame previousUI;
//
//    public ExamManagementUI(int facultyId, JFrame previousUI) throws SQLException {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        examDAO = new ExamDAO();
//        initUI();
//    }
//
//    private void initUI() {
//        setTitle("Exam Management");
//        setSize(800, 500);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout(10, 10));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        // ===== TOP PANEL =====
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//
//        comboClass = new JComboBox<>();
//        txtExamName = new JTextField(10);
//        txtMaxMarks = new JTextField(5);
//        txtPassMarks = new JTextField(5);
//
//        JButton btnAdd = new JButton("Add Exam");
//        JButton btnUpdate = new JButton("Update Exam");
//        JButton btnDelete = new JButton("Delete Exam");
//        JButton btnBack = new JButton("Back");
//
//        topPanel.add(new JLabel("Class:"));
//        topPanel.add(comboClass);
//        topPanel.add(new JLabel("Exam Name:"));
//        topPanel.add(txtExamName);
//        topPanel.add(new JLabel("Max Marks:"));
//        topPanel.add(txtMaxMarks);
//        topPanel.add(new JLabel("Pass Marks:"));
//        topPanel.add(txtPassMarks);
//        topPanel.add(btnAdd);
//        topPanel.add(btnUpdate);
//        topPanel.add(btnDelete);
//        topPanel.add(btnBack);
//
//        add(topPanel, BorderLayout.NORTH);
//
//        // ===== TABLE =====
//        tableModel = new DefaultTableModel(
//                new String[]{"ID", "Exam Name", "Max Marks", "Pass Marks"}, 0
//        ) {
//            public boolean isCellEditable(int r, int c) { return false; }
//        };
//
//        examTable = new JTable(tableModel);
//        add(new JScrollPane(examTable), BorderLayout.CENTER);
//
//        loadClasses();
//
//        // ===== ACTIONS =====
//        btnAdd.addActionListener(e -> addExam());
//        btnUpdate.addActionListener(e -> updateExam());
//        btnDelete.addActionListener(e -> deleteExam());
//
//        btnBack.addActionListener(e -> {
//            dispose();
//            previousUI.setVisible(true);
//        });
//
//        examTable.getSelectionModel().addListSelectionListener(e -> {
//            int row = examTable.getSelectedRow();
//            if (row >= 0) {
//                txtExamName.setText(tableModel.getValueAt(row, 1).toString());
//                txtMaxMarks.setText(tableModel.getValueAt(row, 2).toString());
//                txtPassMarks.setText(tableModel.getValueAt(row, 3).toString());
//            }
//        });
//
//        setVisible(true);
//    }
//
//    // ===== LOAD CLASSES =====
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        List<String[]> classes = examDAO.getClassesByFaculty(facultyId);
//        for (String[] c : classes) {
//            comboClass.addItem(c[0] + " - " + c[1]);
//        }
//        if (comboClass.getItemCount() > 0)
//            comboClass.setSelectedIndex(0);
//    }
//
//    private void addExam() {
//        try {
//            if (comboClass.getSelectedItem() == null) {
//                JOptionPane.showMessageDialog(this, "Select class");
//                return;
//            }
//
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0]);
//            String name = txtExamName.getText().trim();
//            int max = Integer.parseInt(txtMaxMarks.getText().trim());
//            int pass = Integer.parseInt(txtPassMarks.getText().trim());
//
//            if (name.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Enter exam name");
//                return;
//            }
//
//            examDAO.addExam(name, max, pass, facultyId, classId);
//            clearFields();
//            loadExamsForClass(classId);
//
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Enter valid numbers");
//        }
//    }
//
//    private void updateExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) return;
//
//        try {
//            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//            examDAO.updateExam(
//                    id,
//                    txtExamName.getText(),
//                    Integer.parseInt(txtMaxMarks.getText()),
//                    Integer.parseInt(txtPassMarks.getText())
//            );
//            clearFields();
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(this, "Invalid input");
//        }
//    }
//
//    private void deleteExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) return;
//
//        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//        examDAO.deleteExam(id);
//        clearFields();
//    }
//
//    private void loadExamsForClass(int classId) {
//        tableModel.setRowCount(0);
//        List<String[]> exams = examDAO.getExamsByFaculty(facultyId);
//        for (String[] e : exams)
//            tableModel.addRow(new Object[]{e[0], e[1], e[2], e[3]});
//    }
//
//    private void clearFields() {
//        txtExamName.setText("");
//        txtMaxMarks.setText("");
//        txtPassMarks.setText("");
//        examTable.clearSelection();
//    }
//}
//

//
//package com.college.sms.ui;
//
//import com.college.sms.dao.ExamDAO;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ExamManagementUI extends JFrame {
//
//    private JComboBox<String> comboClass;
//    private JComboBox<String> comboSubject;
//    private JTextField txtExamName, txtMaxMarks, txtPassMarks;
//    private JTable examTable;
//    private DefaultTableModel tableModel;
//
//    private ExamDAO examDAO;
//    private int facultyId;
//    private JFrame previousUI;
//
//    public ExamManagementUI(int facultyId, JFrame previousUI) throws SQLException {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        examDAO = new ExamDAO();
//        initUI();
//    }
//
//    private void initUI() {
//        setTitle("Exam Management");
//        setSize(900, 500);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout(10, 10));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        // ===== TOP PANEL =====
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//
//        comboClass = new JComboBox<>();
//        comboSubject = new JComboBox<>();
//
//        txtExamName = new JTextField(10);
//        txtMaxMarks = new JTextField(5);
//        txtPassMarks = new JTextField(5);
//
//        JButton btnAdd = new JButton("Add Exam");
//        JButton btnUpdate = new JButton("Update Exam");
//        JButton btnDelete = new JButton("Delete Exam");
//        JButton btnBack = new JButton("Back");
//
//        topPanel.add(new JLabel("Class:"));
//        topPanel.add(comboClass);
//        topPanel.add(new JLabel("Subject:"));
//        topPanel.add(comboSubject);
//        topPanel.add(new JLabel("Exam Name:"));
//        topPanel.add(txtExamName);
//        topPanel.add(new JLabel("Max Marks:"));
//        topPanel.add(txtMaxMarks);
//        topPanel.add(new JLabel("Pass Marks:"));
//        topPanel.add(txtPassMarks);
//
//        topPanel.add(btnAdd);
//        topPanel.add(btnUpdate);
//        topPanel.add(btnDelete);
//        topPanel.add(btnBack);
//
//        add(topPanel, BorderLayout.NORTH);
//
//        // ===== TABLE =====
//        tableModel = new DefaultTableModel(
//                new String[]{"ID", "Exam Name", "Max Marks", "Pass Marks"}, 0
//        ) {
//            public boolean isCellEditable(int r, int c) { return false; }
//        };
//
//        examTable = new JTable(tableModel);
//        add(new JScrollPane(examTable), BorderLayout.CENTER);
//
//        loadClasses();
//
//        // ===== ACTIONS =====
//        comboClass.addActionListener(e -> loadSubjects());
//
//        btnAdd.addActionListener(e -> addExam());
//        btnUpdate.addActionListener(e -> updateExam());
//        btnDelete.addActionListener(e -> deleteExam());
//
//        btnBack.addActionListener(e -> {
//            dispose();
//            previousUI.setVisible(true);
//        });
//
//        examTable.getSelectionModel().addListSelectionListener(e -> {
//            int row = examTable.getSelectedRow();
//            if (row >= 0) {
//                txtExamName.setText(tableModel.getValueAt(row, 1).toString());
//                txtMaxMarks.setText(tableModel.getValueAt(row, 2).toString());
//                txtPassMarks.setText(tableModel.getValueAt(row, 3).toString());
//            }
//        });
//
//        setVisible(true);
//    }
//
//    // ===== LOAD CLASSES =====
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        List<String[]> classes = examDAO.getClassesByFaculty(facultyId);
//        for (String[] c : classes) {
//            comboClass.addItem(c[0] + " - " + c[1]);
//        }
//        if (comboClass.getItemCount() > 0)
//            comboClass.setSelectedIndex(0);
//    }
//
//    // ===== LOAD SUBJECTS =====
//    private void loadSubjects() {
//        comboSubject.removeAllItems();
//        if (comboClass.getSelectedItem() == null) return;
//
//        int classId = Integer.parseInt(
//                comboClass.getSelectedItem().toString().split(" - ")[0]
//        );
//
//        List<String[]> subjects =
//                examDAO.getSubjectsByClassAndFaculty(classId, facultyId);
//
//        for (String[] s : subjects) {
//            comboSubject.addItem(s[0] + " - " + s[1]);
//        }
//    }
//
//    // ===== ADD EXAM =====
//    private void addExam() {
//        try {
//            if (comboClass.getSelectedItem() == null ||
//                comboSubject.getSelectedItem() == null) {
//                JOptionPane.showMessageDialog(this, "Select class and subject");
//                return;
//            }
//
//            int classId = Integer.parseInt(
//                    comboClass.getSelectedItem().toString().split(" - ")[0]
//            );
//            int subjectId = Integer.parseInt(
//                    comboSubject.getSelectedItem().toString().split(" - ")[0]
//            );
//
//            String name = txtExamName.getText().trim();
//            int max = Integer.parseInt(txtMaxMarks.getText().trim());
//            int pass = Integer.parseInt(txtPassMarks.getText().trim());
//
//            if (name.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Enter exam name");
//                return;
//            }
//
//            examDAO.addExam(name, max, pass, facultyId, classId, subjectId);
//            clearFields();
//            loadExamsForClass(classId);
//
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Enter valid numbers");
//        }
//    }
//
//    private void updateExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) return;
//
//        try {
//            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//            examDAO.updateExam(
//                    id,
//                    txtExamName.getText(),
//                    Integer.parseInt(txtMaxMarks.getText()),
//                    Integer.parseInt(txtPassMarks.getText())
//            );
//            clearFields();
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(this, "Invalid input");
//        }
//    }
//
//    private void deleteExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) return;
//
//        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//        examDAO.deleteExam(id);
//        clearFields();
//    }
//
//    private void loadExamsForClass(int classId) {
//        tableModel.setRowCount(0);
//        List<String[]> exams =
//                examDAO.getExamsByClassAndFaculty(classId, facultyId);
//
//        for (String[] e : exams) {
//            tableModel.addRow(new Object[]{e[0], e[1], e[2], e[3]});
//        }
//    }
//
//    private void clearFields() {
//        txtExamName.setText("");
//        txtMaxMarks.setText("");
//        txtPassMarks.setText("");
//        examTable.clearSelection();
//    }
//}
//

//
//package com.college.sms.ui;
//
//import com.college.sms.dao.ExamDAO;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ExamManagementUI extends JFrame {
//
//    private JComboBox<String> comboClass;
//    private JComboBox<String> comboSubject;
//    private JTextField txtExamName, txtMaxMarks, txtPassMarks;
//    private JTable examTable;
//    private DefaultTableModel tableModel;
//
//    private ExamDAO examDAO;
//    private int facultyId;
//    private JFrame previousUI;
//
//    public ExamManagementUI(int facultyId, JFrame previousUI) throws SQLException {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        this.examDAO = new ExamDAO();
//        initUI();
//    }
//
//    private void initUI() {
//        setTitle("Exam Management");
//        setSize(900, 500);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout(10, 10));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//
//        comboClass = new JComboBox<>();
//        comboSubject = new JComboBox<>();
//
//        txtExamName = new JTextField(10);
//        txtMaxMarks = new JTextField(5);
//        txtPassMarks = new JTextField(5);
//
//        JButton btnAdd = new JButton("Add Exam");
//        JButton btnUpdate = new JButton("Update Exam");
//        JButton btnDelete = new JButton("Delete Exam");
//        JButton btnBack = new JButton("Back");
//
//        topPanel.add(new JLabel("Class:"));
//        topPanel.add(comboClass);
//        topPanel.add(new JLabel("Subject:"));
//        topPanel.add(comboSubject);
//        topPanel.add(new JLabel("Exam Name:"));
//        topPanel.add(txtExamName);
//        topPanel.add(new JLabel("Max Marks:"));
//        topPanel.add(txtMaxMarks);
//        topPanel.add(new JLabel("Pass Marks:"));
//        topPanel.add(txtPassMarks);
//
//        topPanel.add(btnAdd);
//        topPanel.add(btnUpdate);
//        topPanel.add(btnDelete);
//        topPanel.add(btnBack);
//
//        add(topPanel, BorderLayout.NORTH);
//
//        tableModel = new DefaultTableModel(
//                new String[]{"ID", "Exam Name", "Max Marks", "Pass Marks"}, 0
//        ) {
//            public boolean isCellEditable(int r, int c) { return false; }
//        };
//
//        examTable = new JTable(tableModel);
//        add(new JScrollPane(examTable), BorderLayout.CENTER);
//
//        loadClasses();
//
//        comboClass.addActionListener(e -> loadSubjects());
//        comboSubject.addActionListener(e -> loadExams());
//
//        btnAdd.addActionListener(e -> addExam());
//        btnUpdate.addActionListener(e -> updateExam());
//        btnDelete.addActionListener(e -> deleteExam());
//
//        btnBack.addActionListener(e -> {
//            dispose();
//            previousUI.setVisible(true);
//        });
//
//        examTable.getSelectionModel().addListSelectionListener(e -> {
//            int row = examTable.getSelectedRow();
//            if (row >= 0) {
//                txtExamName.setText(tableModel.getValueAt(row, 1).toString());
//                txtMaxMarks.setText(tableModel.getValueAt(row, 2).toString());
//                txtPassMarks.setText(tableModel.getValueAt(row, 3).toString());
//            }
//        });
//
//        setVisible(true);
//    }
//
//    // ===== LOAD CLASSES =====
//    private void loadClasses() {
//        comboClass.removeAllItems();
//
//        List<String[]> classes = examDAO.getClassesByFaculty(facultyId);
//        for (String[] c : classes) {
//            comboClass.addItem(c[0] + " - " + c[1]);
//        }
//
//        if (comboClass.getItemCount() > 0) {
//            comboClass.setSelectedIndex(0);
//        }
//    }
//
//    // ===== LOAD SUBJECTS =====
//    private void loadSubjects() {
//        comboSubject.removeAllItems();
//        tableModel.setRowCount(0);
//
//        if (comboClass.getSelectedItem() == null) return;
//
//        int classId = Integer.parseInt(
//                comboClass.getSelectedItem().toString().split(" - ")[0]
//        );
//
//        List<String[]> subjects =
//                examDAO.getSubjectsByClassAndFaculty(classId, facultyId);
//
//        comboSubject.addItem("-1 - Select Subject");
//
//        for (String[] s : subjects) {
//            comboSubject.addItem(s[0] + " - " + s[1]);
//        }
//
//        comboSubject.setSelectedIndex(0);
//    }
//
//    // ===== LOAD EXAMS =====
//    private void loadExams() {
//        tableModel.setRowCount(0);
//
//        if (comboSubject.getSelectedItem() == null) return;
//
//        String selected = comboSubject.getSelectedItem().toString();
//        int subjectId = Integer.parseInt(selected.split(" - ")[0]);
//
//        if (subjectId <= 0) return;
//
//        int classId = Integer.parseInt(
//                comboClass.getSelectedItem().toString().split(" - ")[0]
//        );
//
//        List<String[]> exams =
//                examDAO.getExamsByClassFacultyAndSubject(
//                        classId, facultyId, subjectId);
//
//        for (String[] e : exams) {
//            tableModel.addRow(new Object[]{e[0], e[1], e[2], e[3]});
//        }
//    }
//
//    // ===== ADD EXAM =====
//    private void addExam() {
//        try {
//            String sub = comboSubject.getSelectedItem().toString();
//            int subjectId = Integer.parseInt(sub.split(" - ")[0]);
//
//            if (subjectId <= 0) {
//                JOptionPane.showMessageDialog(this, "Select a subject");
//                return;
//            }
//
//            int classId = Integer.parseInt(
//                    comboClass.getSelectedItem().toString().split(" - ")[0]
//            );
//
//            String name = txtExamName.getText().trim();
//            int max = Integer.parseInt(txtMaxMarks.getText().trim());
//            int pass = Integer.parseInt(txtPassMarks.getText().trim());
//
//            examDAO.addExam(name, max, pass, facultyId, classId, subjectId);
//            clearFields();
//            loadExams();
//
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Invalid input");
//        }
//    }
//
//    private void updateExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) return;
//
//        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//
//        examDAO.updateExam(
//                id,
//                txtExamName.getText(),
//                Integer.parseInt(txtMaxMarks.getText()),
//                Integer.parseInt(txtPassMarks.getText())
//        );
//
//        clearFields();
//        loadExams();
//    }
//
//    private void deleteExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) return;
//
//        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//        examDAO.deleteExam(id);
//
//        clearFields();
//        loadExams();
//    }
//
//    private void clearFields() {
//        txtExamName.setText("");
//        txtMaxMarks.setText("");
//        txtPassMarks.setText("");
//        examTable.clearSelection();
//    }
//}

//
//package com.college.sms.ui;
//
//import com.college.sms.dao.ExamDAO;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ExamManagementUI extends JFrame {
//
//    private JComboBox<String> comboClass;
//    private JComboBox<String> comboSubject;
//    private JTextField txtExamName, txtMaxMarks, txtPassMarks;
//    private JTable examTable;
//    private DefaultTableModel tableModel;
//
//    private ExamDAO examDAO;
//    private int facultyId;
//    private JFrame previousUI;
//
//    public ExamManagementUI(int facultyId, JFrame previousUI) throws SQLException {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        examDAO = new ExamDAO();
//        initUI();
//    }
//
//    private void initUI() {
//        setTitle("Exam Management");
//        setSize(900, 500);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout(10, 10));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//
//        comboClass = new JComboBox<>();
//        comboSubject = new JComboBox<>();
//
//        txtExamName = new JTextField(10);
//        txtMaxMarks = new JTextField(5);
//        txtPassMarks = new JTextField(5);
//
//        JButton btnAdd = new JButton("Add Exam");
//        JButton btnUpdate = new JButton("Update Exam");
//        JButton btnDelete = new JButton("Delete Exam");
//        JButton btnBack = new JButton("Back");
//
//        topPanel.add(new JLabel("Class:"));
//        topPanel.add(comboClass);
//        topPanel.add(new JLabel("Subject:"));
//        topPanel.add(comboSubject);
//        topPanel.add(new JLabel("Exam Name:"));
//        topPanel.add(txtExamName);
//        topPanel.add(new JLabel("Max Marks:"));
//        topPanel.add(txtMaxMarks);
//        topPanel.add(new JLabel("Pass Marks:"));
//        topPanel.add(txtPassMarks);
//        topPanel.add(btnAdd);
//        topPanel.add(btnUpdate);
//        topPanel.add(btnDelete);
//        topPanel.add(btnBack);
//
//        add(topPanel, BorderLayout.NORTH);
//
//        tableModel = new DefaultTableModel(
//                new String[]{"ID", "Exam Name", "Max Marks", "Pass Marks"}, 0
//        ) {
//            public boolean isCellEditable(int r, int c) { return false; }
//        };
//
//        examTable = new JTable(tableModel);
//        add(new JScrollPane(examTable), BorderLayout.CENTER);
//
//        loadClasses();
//
//        // 🔥 IMPORTANT FIXES
//        comboClass.addActionListener(e -> loadSubjects());
//        comboSubject.addActionListener(e -> loadExams());
//
//        btnAdd.addActionListener(e -> addExam());
//        btnUpdate.addActionListener(e -> updateExam());
//        btnDelete.addActionListener(e -> deleteExam());
//
//        btnBack.addActionListener(e -> {
//            dispose();
//            previousUI.setVisible(true);
//        });
//
//        examTable.getSelectionModel().addListSelectionListener(e -> {
//            int row = examTable.getSelectedRow();
//            if (row >= 0) {
//                txtExamName.setText(tableModel.getValueAt(row, 1).toString());
//                txtMaxMarks.setText(tableModel.getValueAt(row, 2).toString());
//                txtPassMarks.setText(tableModel.getValueAt(row, 3).toString());
//            }
//        });
//
//        setVisible(true);
//    }
//
//    // ===== LOAD CLASSES =====
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        List<String[]> classes = examDAO.getClassesByFaculty(facultyId);
//        for (String[] c : classes) {
//            comboClass.addItem(c[0] + " - " + c[1]);
//        }
//        if (comboClass.getItemCount() > 0)
//            comboClass.setSelectedIndex(0);
//    }
//
//    // ===== LOAD SUBJECTS =====
////    private void loadSubjects() {
////        comboSubject.removeAllItems();
////        tableModel.setRowCount(0);
////
////        if (comboClass.getSelectedItem() == null) return;
////
////        int classId = Integer.parseInt(
////                comboClass.getSelectedItem().toString().split(" - ")[0]
////        );
////
////        List<String[]> subjects =
////                examDAO.getSubjectsByClassAndFaculty(classId, facultyId);
////
////        for (String[] s : subjects) {
////            comboSubject.addItem(s[0] + " - " + s[1]);
////        }
////
////        if (comboSubject.getItemCount() > 0)
////            comboSubject.setSelectedIndex(0);
////    }
//    
//    private void loadSubjects() {
//
//        comboSubject.removeAllItems();
//
//        if (comboClass.getSelectedItem() == null) return;
//
//        int classId = Integer.parseInt(
//            comboClass.getSelectedItem().toString().split(" - ")[0]
//        );
//
//        List<String[]> subjects =
//            examDAO.getSubjectsByClassAndFaculty(classId, facultyId);
//
//        if (subjects.isEmpty()) {
//            comboSubject.addItem("No subjects found");
//            return;
//        }
//
//        for (String[] s : subjects) {
//            comboSubject.addItem(s[0] + " - " + s[1]);
//        }
//
//        comboSubject.setSelectedIndex(0);
//
//        // 🔥 VERY IMPORTANT
//        loadExamsForClass(classId);
//    }
//    
//    private void loadExamsForClass(int classId) {
//
//        tableModel.setRowCount(0); // clear table
//
//        List<String[]> exams =
//                examDAO.getExamsByClassAndFaculty(classId, facultyId);
//
//        for (String[] e : exams) {
//            tableModel.addRow(new Object[]{
//                    e[0], // exam_id
//                    e[1], // exam_name
//                    e[2], // max_marks
//                    e[3]  // pass_marks
//            });
//        }
//    }
//
//
//
//    // ===== LOAD EXAMS =====
//    private void loadExams() {
//        tableModel.setRowCount(0);
//
//        if (comboClass.getSelectedItem() == null ||
//            comboSubject.getSelectedItem() == null)
//            return;
//
//        int classId = Integer.parseInt(
//                comboClass.getSelectedItem().toString().split(" - ")[0]
//        );
//        int subjectId = Integer.parseInt(
//                comboSubject.getSelectedItem().toString().split(" - ")[0]
//        );
//
//        List<String[]> exams =
//                examDAO.getExamsByClassFacultyAndSubject(
//                        classId, facultyId, subjectId);
//
//        for (String[] e : exams) {
//            tableModel.addRow(new Object[]{e[0], e[1], e[2], e[3]});
//        }
//    }
//
//    // ===== ADD EXAM =====
//    private void addExam() {
//        try {
//            if (comboClass.getSelectedItem() == null ||
//                comboSubject.getSelectedItem() == null) {
//                JOptionPane.showMessageDialog(this, "Select class and subject");
//                return;
//            }
//
//            int classId = Integer.parseInt(
//                    comboClass.getSelectedItem().toString().split(" - ")[0]
//            );
//            int subjectId = Integer.parseInt(
//                    comboSubject.getSelectedItem().toString().split(" - ")[0]
//            );
//
//            String name = txtExamName.getText().trim();
//            int max = Integer.parseInt(txtMaxMarks.getText().trim());
//            int pass = Integer.parseInt(txtPassMarks.getText().trim());
//
//            if (name.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Enter exam name");
//                return;
//            }
//
//            examDAO.addExam(name, max, pass, facultyId, classId, subjectId);
//            clearFields();
//            loadExams();
//
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Enter valid numbers");
//        }
//    }
//
//    private void updateExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) return;
//
//        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//
//        examDAO.updateExam(
//                id,
//                txtExamName.getText(),
//                Integer.parseInt(txtMaxMarks.getText()),
//                Integer.parseInt(txtPassMarks.getText())
//        );
//
//        clearFields();
//        loadExams();
//    }
//
//    private void deleteExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) return;
//
//        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//        examDAO.deleteExam(id);
//        clearFields();
//        loadExams();
//    }
//
//    private void clearFields() {
//        txtExamName.setText("");
//        txtMaxMarks.setText("");
//        txtPassMarks.setText("");
//        examTable.clearSelection();
//    }
//}
//

//
//package com.college.sms.ui;
//
//import com.college.sms.dao.ExamDAO;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ExamManagementUI extends JFrame {
//
//    private JComboBox<String> comboClass;
//    private JComboBox<String> comboSubject;
//    private JTextField txtExamName, txtMaxMarks, txtPassMarks;
//    private JTable examTable;
//    private DefaultTableModel tableModel;
//
//    private ExamDAO examDAO;
//    private int facultyId;
//    private JFrame previousUI;
//
//    // 🔴 IMPORTANT FLAG
//    private boolean isLoadingSubjects = false;
//
//    public ExamManagementUI(int facultyId, JFrame previousUI) throws SQLException {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        this.examDAO = new ExamDAO();
//        initUI();
//    }
//
//    private void initUI() {
//        setTitle("Exam Management");
//        setSize(900, 500);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout(10, 10));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//
//        comboClass = new JComboBox<>();
//        comboSubject = new JComboBox<>();
//
//        txtExamName = new JTextField(10);
//        txtMaxMarks = new JTextField(5);
//        txtPassMarks = new JTextField(5);
//
//        JButton btnAdd = new JButton("Add Exam");
//        JButton btnUpdate = new JButton("Update Exam");
//        JButton btnDelete = new JButton("Delete Exam");
//        JButton btnBack = new JButton("Back");
//
//        topPanel.add(new JLabel("Class:"));
//        topPanel.add(comboClass);
//        topPanel.add(new JLabel("Subject:"));
//        topPanel.add(comboSubject);
//        topPanel.add(new JLabel("Exam Name:"));
//        topPanel.add(txtExamName);
//        topPanel.add(new JLabel("Max Marks:"));
//        topPanel.add(txtMaxMarks);
//        topPanel.add(new JLabel("Pass Marks:"));
//        topPanel.add(txtPassMarks);
//
//        topPanel.add(btnAdd);
//        topPanel.add(btnUpdate);
//        topPanel.add(btnDelete);
//        topPanel.add(btnBack);
//
//        add(topPanel, BorderLayout.NORTH);
//
//        tableModel = new DefaultTableModel(
//                new String[]{"ID", "Exam Name", "Max Marks", "Pass Marks"}, 0
//        ) {
//            public boolean isCellEditable(int r, int c) {
//                return false;
//            }
//        };
//
//        examTable = new JTable(tableModel);
//        add(new JScrollPane(examTable), BorderLayout.CENTER);
//
//        // 🔹 LOAD DATA
//        loadClasses();
//
//        // 🔹 LISTENERS
//        comboClass.addActionListener(e -> loadSubjects());
//
//        comboSubject.addActionListener(e -> {
//            if (!isLoadingSubjects) {
//                loadExams();
//            }
//        });
//
//        btnAdd.addActionListener(e -> addExam());
//        btnUpdate.addActionListener(e -> updateExam());
//        btnDelete.addActionListener(e -> deleteExam());
//
//        btnBack.addActionListener(e -> {
//            dispose();
//            previousUI.setVisible(true);
//        });
//
//        examTable.getSelectionModel().addListSelectionListener(e -> {
//            int row = examTable.getSelectedRow();
//            if (row >= 0) {
//                txtExamName.setText(tableModel.getValueAt(row, 1).toString());
//                txtMaxMarks.setText(tableModel.getValueAt(row, 2).toString());
//                txtPassMarks.setText(tableModel.getValueAt(row, 3).toString());
//            }
//        });
//
//        setVisible(true);
//    }
//
//    // ===== LOAD CLASSES =====
//    private void loadClasses() {
//        comboClass.removeAllItems();
//
//        List<String[]> classes = examDAO.getClassesByFaculty(facultyId);
//        for (String[] c : classes) {
//            comboClass.addItem(c[0] + " - " + c[1]);
//        }
//
//        if (comboClass.getItemCount() > 0) {
//            comboClass.setSelectedIndex(0);
//        }
//    }
//
//    // ===== LOAD SUBJECTS =====
//    private void loadSubjects() {
//        isLoadingSubjects = true;
//
//        comboSubject.removeAllItems();
//        tableModel.setRowCount(0);
//
//        if (comboClass.getSelectedItem() == null) {
//            isLoadingSubjects = false;
//            return;
//        }
//
//        int classId = Integer.parseInt(
//                comboClass.getSelectedItem().toString().split(" - ")[0]
//        );
//
//        comboSubject.addItem("-1 - Select Subject");
//
//        List<String[]> subjects =
//                examDAO.getSubjectsByClassAndFaculty(classId, facultyId);
//
//        for (String[] s : subjects) {
//            comboSubject.addItem(s[0] + " - " + s[1]);
//        }
//
//        comboSubject.setSelectedIndex(0);
//        isLoadingSubjects = false;
//    }
//
//    // ===== LOAD EXAMS =====
//    private void loadExams() {
//        tableModel.setRowCount(0);
//
//        if (comboSubject.getSelectedItem() == null) return;
//
//        String selected = comboSubject.getSelectedItem().toString();
//        int subjectId = Integer.parseInt(selected.split(" - ")[0]);
//
//        if (subjectId <= 0) return;
//
//        int classId = Integer.parseInt(
//                comboClass.getSelectedItem().toString().split(" - ")[0]
//        );
//
//        List<String[]> exams =
//                examDAO.getExamsByClassFacultyAndSubject(
//                        classId, facultyId, subjectId);
//
//        for (String[] e : exams) {
//            tableModel.addRow(new Object[]{
//                    e[0], e[1], e[2], e[3]
//            });
//        }
//    }
//
//    // ===== ADD EXAM =====
//    private void addExam() {
//        try {
//            String sub = comboSubject.getSelectedItem().toString();
//            int subjectId = Integer.parseInt(sub.split(" - ")[0]);
//
//            if (subjectId <= 0) {
//                JOptionPane.showMessageDialog(this, "Select a subject");
//                return;
//            }
//
//            int classId = Integer.parseInt(
//                    comboClass.getSelectedItem().toString().split(" - ")[0]
//            );
//
//            String name = txtExamName.getText().trim();
//            int max = Integer.parseInt(txtMaxMarks.getText().trim());
//            int pass = Integer.parseInt(txtPassMarks.getText().trim());
//
//            examDAO.addExam(name, max, pass, facultyId, classId, subjectId);
//            clearFields();
//            loadExams();
//
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Invalid input");
//        }
//    }
//
//    private void updateExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) return;
//
//        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//
//        examDAO.updateExam(
//                id,
//                txtExamName.getText(),
//                Integer.parseInt(txtMaxMarks.getText()),
//                Integer.parseInt(txtPassMarks.getText())
//        );
//
//        clearFields();
//        loadExams();
//    }
//
//    private void deleteExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) return;
//
//        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//        examDAO.deleteExam(id);
//
//        clearFields();
//        loadExams();
//    }
//
//    private void clearFields() {
//        txtExamName.setText("");
//        txtMaxMarks.setText("");
//        txtPassMarks.setText("");
//        examTable.clearSelection();
//    }
//}
//
//package com.college.sms.ui;
//
//import com.college.sms.dao.ClassDAO;
//import com.college.sms.dao.ExamDAO;
//import com.college.sms.dao.SubjectDAO;
//import com.college.sms.model.Exam;
//import com.college.sms.model.Subject;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ExamManagementUI extends JFrame {
//
//    private JComboBox<String> comboClass;
//    private JComboBox<String> comboSubject;
//    private JTextField txtExamName, txtMaxMarks, txtPassMarks;
//    private JTable examTable;
//    private DefaultTableModel tableModel;
//
//    private ExamDAO examDAO;
//    private ClassDAO classDAO;
//    private SubjectDAO subjectDAO;
//    private int facultyId;
//    private JFrame previousUI;
//
//    private boolean isLoadingSubjects = false;
//
//    public ExamManagementUI(int facultyId, JFrame previousUI) throws SQLException {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        this.examDAO = new ExamDAO();
//        this.classDAO = new ClassDAO();
//        this.subjectDAO = new SubjectDAO();
//        initUI();
//    }
//
//    private void initUI() {
//        setTitle("Exam Management");
//        setSize(900, 500);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout(10, 10));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//
//        comboClass = new JComboBox<>();
//        comboSubject = new JComboBox<>();
//
//        txtExamName = new JTextField(15);
//        txtMaxMarks = new JTextField(5);
//        txtPassMarks = new JTextField(5);
//
//        JButton btnAdd = new JButton("Add Exam");
//        JButton btnUpdate = new JButton("Update Exam");
//        JButton btnDelete = new JButton("Delete Exam");
//        JButton btnBack = new JButton("Back");
//
//        topPanel.add(new JLabel("Class:"));
//        topPanel.add(comboClass);
//        topPanel.add(new JLabel("Subject:"));
//        topPanel.add(comboSubject);
//        topPanel.add(new JLabel("Exam Name:"));
//        topPanel.add(txtExamName);
//        topPanel.add(new JLabel("Max Marks:"));
//        topPanel.add(txtMaxMarks);
//        topPanel.add(new JLabel("Pass Marks:"));
//        topPanel.add(txtPassMarks);
//        topPanel.add(btnAdd);
//        topPanel.add(btnUpdate);
//        topPanel.add(btnDelete);
//        topPanel.add(btnBack);
//
//        add(topPanel, BorderLayout.NORTH);
//
//        tableModel = new DefaultTableModel(
//                new String[]{"ID", "Exam Name", "Max Marks", "Pass Marks"}, 0
//        ) {
//            public boolean isCellEditable(int r, int c) {
//                return false;
//            }
//        };
//
//        examTable = new JTable(tableModel);
//        add(new JScrollPane(examTable), BorderLayout.CENTER);
//
//        // Load initial data
//        loadClasses();
//
//        // Listeners
//        comboClass.addActionListener(e -> {
//            if (comboClass.getSelectedItem() != null) loadSubjects();
//        });
//
//        comboSubject.addActionListener(e -> {
//            if (!isLoadingSubjects && comboSubject.getSelectedItem() != null) {
//                loadExams();
//            }
//        });
//
//        btnAdd.addActionListener(e -> addExam());
//        btnUpdate.addActionListener(e -> updateExam());
//        btnDelete.addActionListener(e -> deleteExam());
//        btnBack.addActionListener(e -> {
//            dispose();
//            previousUI.setVisible(true);
//        });
//
//        examTable.getSelectionModel().addListSelectionListener(e -> {
//            int row = examTable.getSelectedRow();
//            if (row >= 0) {
//                txtExamName.setText(tableModel.getValueAt(row, 1).toString());
//                txtMaxMarks.setText(tableModel.getValueAt(row, 2).toString());
//                txtPassMarks.setText(tableModel.getValueAt(row, 3).toString());
//            }
//        });
//
//        setVisible(true);
//    }
//
//    // ===== LOAD CLASSES =====
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        comboSubject.removeAllItems();
//        tableModel.setRowCount(0);
//
//        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
//        if (classes.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "No classes assigned to you!");
//            comboClass.addItem("-- No classes --");
//        } else {
//            for (String[] c : classes) {
//                comboClass.addItem(c[0] + " - " + c[1]);
//            }
//            comboClass.setSelectedIndex(0);
//            loadSubjects();
//        }
//    }
//
//    // ===== LOAD SUBJECTS (FIXED: Uses List<Subject> properly) =====
//    private void loadSubjects() {
//        isLoadingSubjects = true;
//        comboSubject.removeAllItems();
//        tableModel.setRowCount(0);
//
//        if (comboClass.getSelectedItem() == null || 
//            "-- No classes --".equals(comboClass.getSelectedItem().toString())) {
//            isLoadingSubjects = false;
//            return;
//        }
//
//        // ✅ SAFE PARSING with validation
//        int classId;
//        try {
//            classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Invalid class selection");
//            isLoadingSubjects = false;
//            return;
//        }
//
//        // ✅ CORRECT: Use List<Subject> directly (no String[] conversion needed)
//        List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
//        
//        if (subjects.isEmpty()) {
//            comboSubject.addItem("-- No subjects assigned --");
//        } else {
//            for (Subject s : subjects) {
//                comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
//            }
//            comboSubject.setSelectedIndex(0);
//        }
//        
//        isLoadingSubjects = false;
//        loadExams();
//    }
//
//    // ===== LOAD EXAMS (FIXED: Uses List<Exam> properly) =====
//    private void loadExams() {
//        tableModel.setRowCount(0);
//
//        if (comboSubject.getSelectedItem() == null || 
//            "-- No subjects assigned --".equals(comboSubject.getSelectedItem().toString())) {
//            return;
//        }
//
//        // ✅ SAFE PARSING with validation
//        int subjectId;
//        try {
//            subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//        } catch (Exception e) {
//            return;
//        }
//
//        // ✅ CORRECT: Use List<Exam> directly
//        List<Exam> exams = examDAO.getExamsBySubject(subjectId);
//        
//        for (Exam ex : exams) {
//            // Only show exams created by this faculty for this subject
//            if (ex.getFacultyId() == facultyId && ex.getSubjectId() == subjectId) {
//                tableModel.addRow(new Object[]{
//                    ex.getExamId(),
//                    ex.getExamName(),
//                    ex.getMaxMarks(),
//                    ex.getPassMarks()
//                });
//            }
//        }
//    }
//
//    // ===== ADD EXAM =====
//    private void addExam() {
//        try {
//            // Validate subject selection
//            if (comboSubject.getSelectedItem() == null || 
//                "-- No subjects assigned --".equals(comboSubject.getSelectedItem().toString())) {
//                JOptionPane.showMessageDialog(this, "Please select a valid subject");
//                return;
//            }
//
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//
//            String name = txtExamName.getText().trim();
//            if (name.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Exam name cannot be empty");
//                return;
//            }
//
//            int max = Integer.parseInt(txtMaxMarks.getText().trim());
//            int pass = Integer.parseInt(txtPassMarks.getText().trim());
//
//            if (pass > max) {
//                JOptionPane.showMessageDialog(this, "Pass marks cannot exceed max marks");
//                return;
//            }
//
//            examDAO.addExam(name, max, pass, facultyId, classId, subjectId);
//            JOptionPane.showMessageDialog(this, "Exam added successfully!");
//            clearFields();
//            loadExams();
//
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for marks");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error adding exam: " + ex.getMessage());
//        }
//    }
//
//    private void updateExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) {
//            JOptionPane.showMessageDialog(this, "Please select an exam to update");
//            return;
//        }
//
//        try {
//            int examId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//            String name = txtExamName.getText().trim();
//            int max = Integer.parseInt(txtMaxMarks.getText().trim());
//            int pass = Integer.parseInt(txtPassMarks.getText().trim());
//
//            if (pass > max) {
//                JOptionPane.showMessageDialog(this, "Pass marks cannot exceed max marks");
//                return;
//            }
//
//            if (examDAO.updateExam(examId, name, max, pass)) {
//                JOptionPane.showMessageDialog(this, "Exam updated successfully!");
//                clearFields();
//                loadExams();
//            } else {
//                JOptionPane.showMessageDialog(this, "Failed to update exam");
//            }
//
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Please enter valid numeric values");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error updating exam");
//        }
//    }
//
//    private void deleteExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) {
//            JOptionPane.showMessageDialog(this, "Please select an exam to delete");
//            return;
//        }
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//            "Are you sure you want to delete this exam?",
//            "Confirm Delete",
//            JOptionPane.YES_NO_OPTION);
//
//        if (confirm == JOptionPane.YES_OPTION) {
//            try {
//                int examId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//                if (examDAO.deleteExam(examId)) {
//                    JOptionPane.showMessageDialog(this, "Exam deleted successfully!");
//                    clearFields();
//                    loadExams();
//                } else {
//                    JOptionPane.showMessageDialog(this, "Failed to delete exam");
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                JOptionPane.showMessageDialog(this, "Error deleting exam");
//            }
//        }
//    }
//
//    private void clearFields() {
//        txtExamName.setText("");
//        txtMaxMarks.setText("");
//        txtPassMarks.setText("");
//        examTable.clearSelection();
//    }
//}

//
//package com.college.sms.ui;
//
//import com.college.sms.dao.ClassDAO;
//import com.college.sms.dao.ExamDAO;
//import com.college.sms.dao.SubjectDAO;
//import com.college.sms.model.Exam;
//import com.college.sms.model.Subject;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ExamManagementUI extends JFrame {
//
//    private JComboBox<String> comboClass;
//    private JComboBox<String> comboSubject;
//    private JTextField txtExamName, txtMaxMarks, txtPassMarks;
//    private JTable examTable;
//    private DefaultTableModel tableModel;
//
//    private ExamDAO examDAO;
//    private ClassDAO classDAO;
//    private SubjectDAO subjectDAO;  // ✅ MUST USE THIS FOR SUBJECTS
//    private int facultyId;
//    private JFrame previousUI;
//
//    private boolean isLoadingSubjects = false;
//
//    public ExamManagementUI(int facultyId, JFrame previousUI) throws SQLException {
//        this.facultyId = facultyId;
//        this.previousUI = previousUI;
//        this.examDAO = new ExamDAO();
//        this.classDAO = new ClassDAO();
//        this.subjectDAO = new SubjectDAO();  // ✅ INITIALIZED
//        initUI();
//    }
//
//    private void initUI() {
//        setTitle("Exam Management");
//        setSize(900, 500);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout(10, 10));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//
//        comboClass = new JComboBox<>();
//        comboSubject = new JComboBox<>();
//
//        txtExamName = new JTextField(15);
//        txtMaxMarks = new JTextField(5);
//        txtPassMarks = new JTextField(5);
//
//        JButton btnAdd = new JButton("Add Exam");
//        JButton btnUpdate = new JButton("Update Exam");
//        JButton btnDelete = new JButton("Delete Exam");
//        JButton btnBack = new JButton("Back");
//
//        topPanel.add(new JLabel("Class:"));
//        topPanel.add(comboClass);
//        topPanel.add(new JLabel("Subject:"));
//        topPanel.add(comboSubject);
//        topPanel.add(new JLabel("Exam Name:"));
//        topPanel.add(txtExamName);
//        topPanel.add(new JLabel("Max Marks:"));
//        topPanel.add(txtMaxMarks);
//        topPanel.add(new JLabel("Pass Marks:"));
//        topPanel.add(txtPassMarks);
//        topPanel.add(btnAdd);
//        topPanel.add(btnUpdate);
//        topPanel.add(btnDelete);
//        topPanel.add(btnBack);
//
//        add(topPanel, BorderLayout.NORTH);
//
//        tableModel = new DefaultTableModel(
//                new String[]{"ID", "Exam Name", "Max Marks", "Pass Marks"}, 0
//        ) {
//            public boolean isCellEditable(int r, int c) {
//                return false;
//            }
//        };
//
//        examTable = new JTable(tableModel);
//        add(new JScrollPane(examTable), BorderLayout.CENTER);
//
//        // Load initial data
//        loadClasses();
//
//        // Listeners
//        comboClass.addActionListener(e -> {
//            if (comboClass.getSelectedItem() != null) loadSubjects();
//        });
//
//        comboSubject.addActionListener(e -> {
//            if (!isLoadingSubjects && comboSubject.getSelectedItem() != null) {
//                loadExams();
//            }
//        });
//
//        btnAdd.addActionListener(e -> addExam());
//        btnUpdate.addActionListener(e -> updateExam());
//        btnDelete.addActionListener(e -> deleteExam());
//        btnBack.addActionListener(e -> {
//            dispose();
//            if (previousUI != null) previousUI.setVisible(true);
//        });
//
//        examTable.getSelectionModel().addListSelectionListener(e -> {
//            int row = examTable.getSelectedRow();
//            if (row >= 0) {
//                txtExamName.setText(tableModel.getValueAt(row, 1).toString());
//                txtMaxMarks.setText(tableModel.getValueAt(row, 2).toString());
//                txtPassMarks.setText(tableModel.getValueAt(row, 3).toString());
//            }
//        });
//
//        setVisible(true);
//    }
//
//    // ===== LOAD CLASSES =====
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        comboSubject.removeAllItems();
//        tableModel.setRowCount(0);
//
//        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
//        if (classes.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "No classes assigned to you!");
//            comboClass.addItem("-- No classes --");
//        } else {
//            for (String[] c : classes) {
//                comboClass.addItem(c[0] + " - " + c[1]);
//            }
//            if (comboClass.getItemCount() > 0) {
//                comboClass.setSelectedIndex(0);
//                loadSubjects();
//            }
//        }
//    }
//
//    // ===== LOAD SUBJECTS (FIXED: Uses SubjectDAO correctly) =====
//    private void loadSubjects() {
//        isLoadingSubjects = true;
//        comboSubject.removeAllItems();
//        tableModel.setRowCount(0);
//
//        if (comboClass.getSelectedItem() == null || 
//            "-- No classes --".equals(comboClass.getSelectedItem().toString())) {
//            isLoadingSubjects = false;
//            return;
//        }
//
//        // ✅ SAFE PARSING
//        int classId;
//        try {
//            classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Invalid class selection format!");
//            isLoadingSubjects = false;
//            return;
//        }
//
//        // ✅ CRITICAL FIX: Use SubjectDAO (NOT ExamDAO) with BOTH filters
//        List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
//        
//        if (subjects.isEmpty()) {
//            comboSubject.addItem("-- No subjects assigned to you in this class --");
//            JOptionPane.showMessageDialog(this, 
//                "No subjects found for this class.\n" +
//                "Please add subjects in Subject Management first.\n" +
//                "Make sure subjects are assigned to your faculty account.",
//                "No Subjects Found",
//                JOptionPane.WARNING_MESSAGE);
//        } else {
//            for (Subject s : subjects) {
//                comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
//            }
//            comboSubject.setSelectedIndex(0);
//        }
//        
//        isLoadingSubjects = false;
//        loadExams();
//    }
//
//    // ===== LOAD EXAMS =====
//    private void loadExams() {
//        tableModel.setRowCount(0);
//
//        if (comboSubject.getSelectedItem() == null || 
//            comboSubject.getSelectedItem().toString().contains("-- No subjects")) {
//            return;
//        }
//
//        // ✅ SAFE PARSING
//        int subjectId, classId;
//        try {
//            subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//            classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//        } catch (Exception e) {
//            return;
//        }
//
//        // ✅ Get exams filtered by subject
//        List<Exam> exams = examDAO.getExamsBySubject(subjectId);
//        
//        // Client-side filter to ensure only current faculty's exams shown
//        for (Exam ex : exams) {
//            if (ex.getFacultyId() == facultyId && 
//                ex.getClassId() == classId && 
//                ex.getSubjectId() == subjectId) {
//                
//                tableModel.addRow(new Object[]{
//                    ex.getExamId(),
//                    ex.getExamName(),
//                    ex.getMaxMarks(),
//                    ex.getPassMarks()
//                });
//            }
//        }
//    }
//
//    // ===== ADD EXAM =====
//    private void addExam() {
//        try {
//            if (comboSubject.getSelectedItem() == null || 
//                comboSubject.getSelectedItem().toString().contains("-- No subjects")) {
//                JOptionPane.showMessageDialog(this, "Please select a valid subject");
//                return;
//            }
//
//            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
//            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
//
//            String name = txtExamName.getText().trim();
//            if (name.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Exam name cannot be empty");
//                return;
//            }
//
//            int max = Integer.parseInt(txtMaxMarks.getText().trim());
//            int pass = Integer.parseInt(txtPassMarks.getText().trim());
//
//            if (pass > max) {
//                JOptionPane.showMessageDialog(this, "Pass marks cannot exceed max marks");
//                return;
//            }
//
//            examDAO.addExam(name, max, pass, facultyId, classId, subjectId);
//            JOptionPane.showMessageDialog(this, "Exam added successfully!");
//            clearFields();
//            loadExams();
//
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for marks");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error adding exam: " + ex.getMessage());
//        }
//    }
//
//    private void updateExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) {
//            JOptionPane.showMessageDialog(this, "Please select an exam to update");
//            return;
//        }
//
//        try {
//            int examId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//            String name = txtExamName.getText().trim();
//            int max = Integer.parseInt(txtMaxMarks.getText().trim());
//            int pass = Integer.parseInt(txtPassMarks.getText().trim());
//
//            if (pass > max) {
//                JOptionPane.showMessageDialog(this, "Pass marks cannot exceed max marks");
//                return;
//            }
//
//            if (examDAO.updateExam(examId, name, max, pass)) {
//                JOptionPane.showMessageDialog(this, "Exam updated successfully!");
//                clearFields();
//                loadExams();
//            } else {
//                JOptionPane.showMessageDialog(this, "Failed to update exam");
//            }
//
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Please enter valid numeric values");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error updating exam");
//        }
//    }
//
//    private void deleteExam() {
//        int row = examTable.getSelectedRow();
//        if (row < 0) {
//            JOptionPane.showMessageDialog(this, "Please select an exam to delete");
//            return;
//        }
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//            "Are you sure you want to delete this exam?",
//            "Confirm Delete",
//            JOptionPane.YES_NO_OPTION);
//
//        if (confirm == JOptionPane.YES_OPTION) {
//            try {
//                int examId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
//                if (examDAO.deleteExam(examId)) {
//                    JOptionPane.showMessageDialog(this, "Exam deleted successfully!");
//                    clearFields();
//                    loadExams();
//                } else {
//                    JOptionPane.showMessageDialog(this, "Failed to delete exam");
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                JOptionPane.showMessageDialog(this, "Error deleting exam");
//            }
//        }
//    }
//
//    private void clearFields() {
//        txtExamName.setText("");
//        txtMaxMarks.setText("");
//        txtPassMarks.setText("");
//        examTable.clearSelection();
//    }
//}


//--
package com.college.sms.ui;

import com.college.sms.dao.ClassDAO;
import com.college.sms.dao.ExamDAO;
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
import java.sql.SQLException;
import java.util.List;

public class ExamManagementUI extends JFrame {

    private JComboBox<String> comboClass;
    private JComboBox<String> comboSubject;
    private JTextField txtExamName, txtMaxMarks, txtPassMarks;
    private JTable examTable;
    private DefaultTableModel tableModel;

    private ExamDAO examDAO;
    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;
    private int facultyId;
    private JFrame previousUI;
    private boolean isLoadingSubjects = false;
    private JLabel statusLabel;

    public ExamManagementUI(int facultyId, JFrame previousUI) throws SQLException {
        this.facultyId = facultyId;
        this.previousUI = previousUI;
        this.examDAO = new ExamDAO();
        this.classDAO = new ClassDAO();
        this.subjectDAO = new SubjectDAO();
        initUI();
    }

    private void initUI() {
        setTitle("📝 Exam Management | Faculty ID: " + facultyId);
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 247, 250));

        // ===== TOP SECTION WRAPPER (CRITICAL FIX: Prevents component replacement in BorderLayout.NORTH) =====
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setOpaque(false);

        // ===== HEADER PANEL (NOW VISIBLE WITH WORKING BACK BUTTON) =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(new EmptyBorder(0, 25, 0, 25));

        JLabel titleLabel = new JLabel("🎓 Exam Management Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // ✅ BACK BUTTON - NOW VISIBLE AND FULLY FUNCTIONAL
        JButton btnBack = createModernButton("⇦ Back to Dashboard", new Color(52, 152, 219), Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnBack.setPreferredSize(new Dimension(220, 45));
        btnBack.addActionListener(e -> {
            dispose(); // Close current window
            if (previousUI != null) {
                previousUI.setVisible(true); // Show previous dashboard
                previousUI.toFront();        // Bring to front
                previousUI.requestFocus();   // Focus the window
            }
        });
        headerPanel.add(btnBack, BorderLayout.EAST);

        topWrapper.add(headerPanel);

        // ===== CONTROL PANEL (Class/Subject Filters + Exam Form) =====
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "✏️ Configure Exam",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 17),
            new Color(52, 152, 219)
        ));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setPreferredSize(new Dimension(0, 160));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Class Selector
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(createControlLabel("🏫 Class:"), gbc);
        
        gbc.gridx = 1;
        comboClass = createModernComboBox(240);
        controlPanel.add(comboClass, gbc);

        // Subject Selector
        gbc.gridx = 2;
        controlPanel.add(createControlLabel("📚 Subject:"), gbc);
        
        gbc.gridx = 3;
        comboSubject = createModernComboBox(260);
        controlPanel.add(comboSubject, gbc);

        // Exam Name
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(createControlLabel("📝 Exam Name:"), gbc);
        
        gbc.gridx = 1;
        txtExamName = createModernTextField(240);
        controlPanel.add(txtExamName, gbc);

        // Max Marks
        gbc.gridx = 2;
        controlPanel.add(createControlLabel("💯 Max Marks:"), gbc);
        
        gbc.gridx = 3;
        txtMaxMarks = createModernTextField(100);
        controlPanel.add(txtMaxMarks, gbc);

        // Pass Marks
        gbc.gridx = 4;
        controlPanel.add(createControlLabel("✅ Pass Marks:"), gbc);
        
        gbc.gridx = 5;
        txtPassMarks = createModernTextField(100);
        controlPanel.add(txtPassMarks, gbc);

        topWrapper.add(controlPanel);

        // ===== ACTION BUTTONS PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(new EmptyBorder(5, 0, 15, 0));

        JButton btnAdd = createModernButton("✨ Add New Exam", new Color(46, 204, 113), Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(180, 42));
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAdd.addActionListener(e -> addExam());
        buttonPanel.add(btnAdd);

        JButton btnUpdate = createModernButton("🔄 Update Exam", new Color(241, 196, 15), Color.BLACK);
        btnUpdate.setPreferredSize(new Dimension(180, 42));
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnUpdate.addActionListener(e -> updateExam());
        buttonPanel.add(btnUpdate);

        JButton btnDelete = createModernButton("🗑️ Delete Exam", new Color(231, 76, 60), Color.WHITE);
        btnDelete.setPreferredSize(new Dimension(180, 42));
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnDelete.addActionListener(e -> deleteExam());
        buttonPanel.add(btnDelete);

        JButton btnRefresh = createModernButton("⟳ Refresh List", new Color(155, 89, 182), Color.WHITE);
        btnRefresh.setPreferredSize(new Dimension(180, 42));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRefresh.addActionListener(e -> loadExams());
        buttonPanel.add(btnRefresh);

        topWrapper.add(buttonPanel);
        
        // ADD COMPLETE TOP SECTION TO NORTH (prevents component replacement)
        add(topWrapper, BorderLayout.NORTH);

        // ===== EXAM TABLE PANEL =====
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(
            new String[]{"Exam ID", "Exam Name", "Max Marks", "Pass Marks", "Subject", "Class"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        examTable = new JTable(tableModel);
        examTable.setRowHeight(34);
        examTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        examTable.setGridColor(new Color(230, 230, 230));
        examTable.setSelectionBackground(new Color(52, 152, 219));
        examTable.setSelectionForeground(Color.WHITE);
        examTable.setIntercellSpacing(new Dimension(0, 2));

        // Header styling
        JTableHeader header = examTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(44, 62, 80));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 42));

        // Zebra striping renderer
        examTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
                    
                    // Highlight pass marks column if close to max marks
                    if (column == 3) {
                        try {
                            int pass = Integer.parseInt(value.toString());
                            int max = Integer.parseInt(t.getValueAt(row, 2).toString());
                            if ((double) pass / max > 0.85) {
                                c.setForeground(new Color(211, 47, 47)); // Red for high pass threshold
                                c.setFont(getFont().deriveFont(Font.BOLD));
                            }
                        } catch (Exception ignored) {}
                    }
                }
                return c;
            }
        });

        // Hide Exam ID column
        examTable.getColumnModel().getColumn(0).setMinWidth(0);
        examTable.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane tableScroll = new JScrollPane(examTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204), 1));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        // Table title
        JLabel tableTitle = new JLabel("📋 Exam Records");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(new Color(44, 62, 80));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        add(tablePanel, BorderLayout.CENTER);

        // ===== STATUS BAR =====
        statusLabel = new JLabel("Ready to manage exams. Select class and subject to begin.");
        statusLabel.setBorder(new EmptyBorder(10, 15, 10, 15));
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
            if (!isLoadingSubjects && comboSubject.getSelectedItem() != null && !comboSubject.getSelectedItem().toString().contains("--")) {
                loadExams();
            }
        });

        examTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = examTable.getSelectedRow();
                if (row >= 0) {
                    txtExamName.setText(tableModel.getValueAt(row, 1).toString());
                    txtMaxMarks.setText(tableModel.getValueAt(row, 2).toString());
                    txtPassMarks.setText(tableModel.getValueAt(row, 3).toString());
                    statusLabel.setText("✏️ Selected exam: " + tableModel.getValueAt(row, 1).toString() + 
                                      " | Max: " + tableModel.getValueAt(row, 2).toString() + 
                                      " | Pass: " + tableModel.getValueAt(row, 3).toString());
                }
            }
        });

        // Load initial data
        loadClasses();
        setVisible(true);
    }

    private JLabel createControlLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(44, 62, 80));
        return label;
    }

    private JComboBox<String> createModernComboBox(int width) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(width, 38));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        return combo;
    }

    private JTextField createModernTextField(int width) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(width, 38));
        field.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JButton createModernButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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

    // ===== DATA LOADING METHODS =====
    private void loadClasses() {
        comboClass.removeAllItems();
        comboSubject.removeAllItems();
        tableModel.setRowCount(0);

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
            if (comboClass.getItemCount() > 0) {
                comboClass.setSelectedIndex(0);
                loadSubjects();
            }
        }
    }

    private void loadSubjects() {
        isLoadingSubjects = true;
        comboSubject.removeAllItems();
        tableModel.setRowCount(0);

        if (comboClass.getSelectedItem() == null || 
            "-- No classes --".equals(comboClass.getSelectedItem().toString())) {
            comboSubject.addItem("-- Select class first --");
            isLoadingSubjects = false;
            statusLabel.setText("Select a class to view available subjects");
            return;
        }

        int classId;
        try {
            classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
        } catch (Exception e) {
            statusLabel.setText("❌ Invalid class selection format");
            isLoadingSubjects = false;
            return;
        }

        List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
        
        if (subjects.isEmpty()) {
            comboSubject.addItem("-- No subjects assigned to you in this class --");
            statusLabel.setText("⚠️ No subjects found. Add subjects in Subject Management first.");
        } else {
            for (Subject s : subjects) {
                comboSubject.addItem(s.getSubjectId() + " - " + s.getSubjectName());
            }
            comboSubject.setSelectedIndex(0);
        }
        
        isLoadingSubjects = false;
        loadExams();
    }

    private void loadExams() {
        tableModel.setRowCount(0);

        if (comboSubject.getSelectedItem() == null || 
            comboSubject.getSelectedItem().toString().contains("--")) {
            statusLabel.setText("Select a valid subject to view exams");
            return;
        }

        int subjectId, classId;
        try {
            subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());
            classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
        } catch (Exception e) {
            statusLabel.setText("❌ Invalid selection format");
            return;
        }

        List<Exam> exams = examDAO.getExamsBySubject(subjectId);
        int examCount = 0;
        
        for (Exam ex : exams) {
            if (ex.getFacultyId() == facultyId && 
                ex.getClassId() == classId && 
                ex.getSubjectId() == subjectId) {
                
                // Get subject and class names for display
                String subjectName = comboSubject.getSelectedItem().toString().split(" - ")[1].trim();
                String className = comboClass.getSelectedItem().toString().split(" - ")[1].trim();
                
                tableModel.addRow(new Object[]{
                    ex.getExamId(),
                    ex.getExamName(),
                    ex.getMaxMarks(),
                    ex.getPassMarks(),
                    subjectName,
                    className
                });
                examCount++;
            }
        }
        
        statusLabel.setText(String.format("✓ Loaded %d exams for selected subject", examCount));
        if (examCount == 0) {
            statusLabel.setText("ℹ️ No exams found for this subject. Click 'Add New Exam' to create one.");
        }
    }

    // ===== EXAM OPERATIONS =====
    private void addExam() {
        try {
            if (comboSubject.getSelectedItem() == null || 
                comboSubject.getSelectedItem().toString().contains("--")) {
                showError("Please select a valid subject");
                return;
            }

            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            int subjectId = Integer.parseInt(comboSubject.getSelectedItem().toString().split(" - ")[0].trim());

            String name = txtExamName.getText().trim();
            if (name.isEmpty()) {
                showError("Exam name cannot be empty");
                return;
            }

            int max = Integer.parseInt(txtMaxMarks.getText().trim());
            int pass = Integer.parseInt(txtPassMarks.getText().trim());

            if (max <= 0) {
                showError("Max marks must be greater than 0");
                return;
            }
            
            if (pass <= 0) {
                showError("Pass marks must be greater than 0");
                return;
            }

            if (pass > max) {
                showError("Pass marks cannot exceed max marks");
                return;
            }

            examDAO.addExam(name, max, pass, facultyId, classId, subjectId);
            showSuccess("Exam added successfully!");
            clearFields();
            loadExams();

        } catch (NumberFormatException ex) {
            showError("Please enter valid numeric values for marks");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error adding exam: " + ex.getMessage());
        }
    }

    private void updateExam() {
        int row = examTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an exam to update");
            return;
        }

        try {
            int examId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            String name = txtExamName.getText().trim();
            int max = Integer.parseInt(txtMaxMarks.getText().trim());
            int pass = Integer.parseInt(txtPassMarks.getText().trim());

            if (name.isEmpty()) {
                showError("Exam name cannot be empty");
                return;
            }

            if (max <= 0 || pass <= 0) {
                showError("Marks values must be greater than 0");
                return;
            }

            if (pass > max) {
                showError("Pass marks cannot exceed max marks");
                return;
            }

            if (examDAO.updateExam(examId, name, max, pass)) {
                showSuccess("Exam updated successfully!");
                clearFields();
                loadExams();
            } else {
                showError("Failed to update exam");
            }

        } catch (NumberFormatException ex) {
            showError("Please enter valid numeric values");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error updating exam");
        }
    }

    private void deleteExam() {
        int row = examTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an exam to delete");
            return;
        }

        String examName = tableModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>⚠️ Confirm Deletion</b><br><br>" +
            "Are you sure you want to delete exam:<br>" +
            "<b style='color:#e74c3c;'>" + examName + "</b><br><br>" +
            "<span style='color:#7f8c8d;'>This action cannot be undone!</span></html>",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int examId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                if (examDAO.deleteExam(examId)) {
                    showSuccess("Exam deleted successfully!");
                    clearFields();
                    loadExams();
                } else {
                    showError("Failed to delete exam");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Error deleting exam: " + ex.getMessage());
            }
        }
    }

    private void clearFields() {
        txtExamName.setText("");
        txtMaxMarks.setText("");
        txtPassMarks.setText("");
        examTable.clearSelection();
        statusLabel.setText("Fields cleared. Ready for new operation.");
    }

    // ===== UTILITY METHODS =====
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            "<html><span style='color:#e74c3c; font-weight:bold;'>Error:</span><br>" + message + "</html>",
            "Operation Failed",
            JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("❌ " + message);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
            "<html><span style='color:#27ae60; font-weight:bold;'>Success:</span><br>" + message + "</html>",
            "Operation Successful",
            JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("✓ " + message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Create mock dashboard for testing back button
                JFrame mockDashboard = new JFrame("Faculty Dashboard");
                mockDashboard.setSize(1000, 650);
                mockDashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mockDashboard.setLocationRelativeTo(null);
                
                JPanel content = new JPanel();
                content.setBackground(new Color(240, 242, 245));
                content.setLayout(new BorderLayout(20, 20));
                
                JLabel welcome = new JLabel("🏫 Faculty Dashboard", SwingConstants.CENTER);
                welcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
                welcome.setForeground(new Color(44, 62, 80));
                content.add(welcome, BorderLayout.CENTER);
                
                mockDashboard.setContentPane(content);
                mockDashboard.setVisible(true);
                
                // Open Exam Management UI - PASS DASHBOARD AS PREVIOUS UI
                new ExamManagementUI(1, mockDashboard);
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Application failed to start!\nError: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}