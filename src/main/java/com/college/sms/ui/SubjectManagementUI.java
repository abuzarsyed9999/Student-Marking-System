//package com.college.sms.ui;
//
//import com.college.sms.dao.SubjectDAO;
//import com.college.sms.model.Subject;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class SubjectManagementUI extends JFrame {
//
//    private FacultyDashboard dashboard;
//    private JTextField txtSubjectId, txtSubjectName, txtClassId;
//    private JTable table;
//    private DefaultTableModel model;
//    private SubjectDAO subjectDAO;
//
//    public SubjectManagementUI(FacultyDashboard dashboard) throws SQLException {
//        this.dashboard = dashboard;
//        subjectDAO = new SubjectDAO();
//
//        setTitle("Subject Management");
//        setSize(700, 450);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        // -------- INPUT PANEL --------
//        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
//        inputPanel.setBorder(BorderFactory.createTitledBorder("Subject Details"));
//
//        inputPanel.add(new JLabel("Subject ID (for update/delete):"));
//        txtSubjectId = new JTextField();
//        inputPanel.add(txtSubjectId);
//
//        inputPanel.add(new JLabel("Subject Name:"));
//        txtSubjectName = new JTextField();
//        inputPanel.add(txtSubjectName);
//
//        inputPanel.add(new JLabel("Class ID:"));
//        txtClassId = new JTextField();
//        inputPanel.add(txtClassId);
//
//        // -------- BUTTON PANEL --------
//        JPanel buttonPanel = new JPanel();
//
//        JButton btnAdd = new JButton("Add");
//        JButton btnUpdate = new JButton("Update");
//        JButton btnDelete = new JButton("Delete");
//        JButton btnRefresh = new JButton("Refresh");
//        JButton btnBack = new JButton("Back");
//
//        buttonPanel.add(btnAdd);
//        buttonPanel.add(btnUpdate);
//        buttonPanel.add(btnDelete);
//        buttonPanel.add(btnRefresh);
//        buttonPanel.add(btnBack);
//
//        // -------- TABLE --------
//        model = new DefaultTableModel(new String[]{"Subject ID", "Subject Name", "Class ID"}, 0);
//        table = new JTable(model);
//        JScrollPane scrollPane = new JScrollPane(table);
//
//        // -------- MAIN LAYOUT --------
//        setLayout(new BorderLayout(10, 10));
//        add(inputPanel, BorderLayout.NORTH);
//        add(scrollPane, BorderLayout.CENTER);
//        add(buttonPanel, BorderLayout.SOUTH);
//
//        // -------- ACTIONS --------
//        btnAdd.addActionListener(e -> addSubject());
//        btnUpdate.addActionListener(e -> updateSubject());
//        btnDelete.addActionListener(e -> deleteSubject());
//        btnRefresh.addActionListener(e -> loadSubjects());
//        btnBack.addActionListener(e -> {
//            dispose();
//            dashboard.setVisible(true); // go back to dashboard
//        });
//
//        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
//
//        loadSubjects();
//        setVisible(true);
//    }
//
//    // ---------------- CRUD METHODS ----------------
//    private void addSubject() {
//        try {
//            String name = txtSubjectName.getText().trim();
//            int classId = Integer.parseInt(txtClassId.getText().trim());
//
//            boolean added = subjectDAO.addSubject(name, classId);
//            if (added) {
//                JOptionPane.showMessageDialog(this, "Subject added successfully");
//                loadSubjects();
//                clearFields();
//            } else {
//                JOptionPane.showMessageDialog(this, "Failed to add subject");
//            }
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Invalid input");
//        }
//    }
//
//    private void updateSubject() {
//        try {
//            int subjectId = Integer.parseInt(txtSubjectId.getText().trim());
//            String name = txtSubjectName.getText().trim();
//            int classId = Integer.parseInt(txtClassId.getText().trim());
//
//            boolean updated = subjectDAO.updateSubject(subjectId, name, classId);
//            if (updated) {
//                JOptionPane.showMessageDialog(this, "Subject updated successfully");
//                loadSubjects();
//                clearFields();
//            } else {
//                JOptionPane.showMessageDialog(this, "Update failed");
//            }
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Invalid input");
//        }
//    }
//
//    private void deleteSubject() {
//        try {
//            int subjectId = Integer.parseInt(txtSubjectId.getText().trim());
//
//            boolean deleted = subjectDAO.deleteSubject(subjectId);
//            if (deleted) {
//                JOptionPane.showMessageDialog(this, "Subject deleted successfully");
//                loadSubjects();
//                clearFields();
//            } else {
//                JOptionPane.showMessageDialog(this, "Delete failed. Subject may have assigned marks.");
//            }
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Invalid Subject ID");
//        }
//    }
//
//    private void loadSubjects() {
//        model.setRowCount(0);
//        List<Subject> subjects = subjectDAO.getAllSubjects();
//        for (Subject s : subjects) {
//            model.addRow(new Object[]{s.getSubjectId(), s.getSubjectName(), s.getClassId()});
//        }
//    }
//
//    private void fillFormFromTable() {
//        int row = table.getSelectedRow();
//        if (row != -1) {
//            txtSubjectId.setText(model.getValueAt(row, 0).toString());
//            txtSubjectName.setText(model.getValueAt(row, 1).toString());
//            txtClassId.setText(model.getValueAt(row, 2).toString());
//        }
//    }
//
//    private void clearFields() {
//        txtSubjectId.setText("");
//        txtSubjectName.setText("");
//        txtClassId.setText("");
//    }
//
//    // ----- Main for testing -----
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//			try {
//				new SubjectManagementUI(new FacultyDashboard(1));
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		});
//    }
//}

//package com.college.sms.ui;
//
//import com.college.sms.dao.ClassDAO;
//import com.college.sms.dao.SubjectDAO;
//import com.college.sms.model.Subject;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class SubjectManagementUI extends JFrame {
//
//    private JComboBox<String> comboClass;
//    private JTextField txtSubjectName;
//    private JTable subjectTable;
//    private DefaultTableModel tableModel;
//
//    private SubjectDAO subjectDAO;
//    private ClassDAO classDAO;
//    private int facultyId;
//
//    public SubjectManagementUI(int facultyId) throws SQLException {
//        this.facultyId = facultyId;
//        subjectDAO = new SubjectDAO();
//        classDAO = new ClassDAO();
//
//        initUI();
//    }
//
//    private void initUI() {
//        setTitle("Subject Management");
//        setSize(700, 500);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout(10, 10));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        // ===== Top Panel =====
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//        comboClass = new JComboBox<>();
//        txtSubjectName = new JTextField(15);
//        JButton btnAdd = new JButton("Add Subject");
//        JButton btnDelete = new JButton("Delete Subject");
//
//        topPanel.add(new JLabel("Class:"));
//        topPanel.add(comboClass);
//        topPanel.add(new JLabel("Subject Name:"));
//        topPanel.add(txtSubjectName);
//        topPanel.add(btnAdd);
//        topPanel.add(btnDelete);
//
//        add(topPanel, BorderLayout.NORTH);
//
//        // ===== Table =====
//        tableModel = new DefaultTableModel(new String[]{"ID", "Subject Name"}, 0) {
//            @Override
//            public boolean isCellEditable(int row, int col) {
//                return false;
//            }
//        };
//        subjectTable = new JTable(tableModel);
//        add(new JScrollPane(subjectTable), BorderLayout.CENTER);
//
//        loadClasses();
//        loadSubjects();
//
//        // ===== Button Actions =====
//        comboClass.addActionListener(e -> loadSubjects());
//
//        btnAdd.addActionListener(e -> addSubject());
//        btnDelete.addActionListener(e -> deleteSubject());
//
//        setVisible(true);
//    }
//
//    private void loadClasses() {
//        comboClass.removeAllItems();
//        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
//        for (String[] c : classes) {
//            comboClass.addItem(c[0] + " - " + c[1]); // class_id - class_name
//        }
//        if (comboClass.getItemCount() > 0) comboClass.setSelectedIndex(0);
//    }
//
//    private void loadSubjects() {
//        tableModel.setRowCount(0);
//        if (comboClass.getSelectedItem() == null) return;
//        int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0]);
//        List<Subject> subjects = subjectDAO.getSubjectsByClass(classId);
//        for (Subject s : subjects) {
//            tableModel.addRow(new Object[]{s.getSubjectId(), s.getSubjectName()});
//        }
//    }
//
//    private void addSubject() {
//        if (txtSubjectName.getText().trim().isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Enter subject name");
//            return;
//        }
//        int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0]);
//        subjectDAO.addSubject(txtSubjectName.getText().trim(), classId);
//        txtSubjectName.setText("");
//        loadSubjects();
//    }
//
//    private void deleteSubject() {
//        int row = subjectTable.getSelectedRow();
//        if (row < 0) {
//            JOptionPane.showMessageDialog(this, "Select a subject to delete");
//            return;
//        }
//        int subjectId = (int) tableModel.getValueAt(row, 0);
//        subjectDAO.deleteSubject(subjectId);
//        loadSubjects();
//    }
//
//}
package com.college.sms.ui;

import com.college.sms.dao.ClassDAO;
import com.college.sms.dao.SubjectDAO;
import com.college.sms.model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class SubjectManagementUI extends JFrame {

    private JComboBox<String> comboClass;
    private JTextField txtSubjectName;
    private JTable subjectTable;
    private DefaultTableModel tableModel;
    private SubjectDAO subjectDAO;
    private ClassDAO classDAO;
    private int facultyId;
    private JFrame previousUI;
    private JButton btnDelete;
    private JLabel statusLabel;

    public SubjectManagementUI(int facultyId, JFrame previousUI) throws SQLException {
        this.facultyId = facultyId;
        this.previousUI = previousUI;
        this.subjectDAO = new SubjectDAO();
        this.classDAO = new ClassDAO();
        initComponents();
        loadClasses();
    }

    private void initComponents() {
        setTitle("Subject Management - Faculty ID: " + facultyId);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Subject Details"));
        
        formPanel.add(new JLabel("Class:"));
        comboClass = new JComboBox<>();
        formPanel.add(comboClass);
        
        formPanel.add(new JLabel("Subject Name:"));
        txtSubjectName = new JTextField();
        formPanel.add(txtSubjectName);
        
        add(formPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Subject Name", "Class"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        subjectTable = new JTable(tableModel);
        subjectTable.setRowHeight(25);
        subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // ✅ CRITICAL FIX: Proper row selection listener
        subjectTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = subjectTable.getSelectedRow();
                if (row >= 0) {
                    btnDelete.setEnabled(true);
                    txtSubjectName.setText(tableModel.getValueAt(row, 1).toString());
                }
            }
        });
        
        add(new JScrollPane(subjectTable), BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton btnAdd = new JButton("Add Subject");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        
        btnDelete = new JButton("Delete Subject");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setEnabled(false); // Disabled until row selected
        
        JButton btnClear = new JButton("Clear");
        JButton btnBack = new JButton("Back");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);
        
        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        comboClass.addActionListener(e -> loadSubjects());
        btnAdd.addActionListener(e -> addSubject());
        btnDelete.addActionListener(e -> deleteSubject());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> {
            dispose();
            if (previousUI != null) previousUI.setVisible(true);
        });
    }

    private void loadClasses() {
        comboClass.removeAllItems();
        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
        
        if (classes.isEmpty()) {
            comboClass.addItem("-- No classes --");
            JOptionPane.showMessageDialog(this, "No classes assigned to you!", "Info", JOptionPane.WARNING_MESSAGE);
        } else {
            for (String[] c : classes) {
                comboClass.addItem(c[0] + " - " + c[1]);
            }
            loadSubjects();
        }
    }

    private void loadSubjects() {
        tableModel.setRowCount(0);
        
        if (comboClass.getItemCount() == 0 || 
            comboClass.getSelectedItem().toString().contains("-- No classes")) {
            return;
        }
        
        try {
            String item = comboClass.getSelectedItem().toString();
            int classId = Integer.parseInt(item.split(" - ")[0]);
            String className = item.split(" - ")[1];
            
            List<Subject> subjects = subjectDAO.getSubjectsByClassAndFaculty(classId, facultyId);
            
            for (Subject s : subjects) {
                tableModel.addRow(new Object[]{s.getSubjectId(), s.getSubjectName(), className});
            }
            
            if (subjects.isEmpty()) {
                statusLabel.setText("No subjects found. Add a new subject.");
            } else {
                statusLabel.setText("Loaded " + subjects.size() + " subjects");
            }
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void addSubject() {
        String name = txtSubjectName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Subject name required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String item = comboClass.getSelectedItem().toString();
            int classId = Integer.parseInt(item.split(" - ")[0]);
            
            if (subjectDAO.addSubject(name, classId, facultyId)) {
                JOptionPane.showMessageDialog(this, "Subject added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadSubjects();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add subject", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSubject() {
        int row = subjectTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a subject to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int subjectId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        String name = tableModel.getValueAt(row, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete subject '" + name + "'?\nDelete all exams for this subject first!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        String result = subjectDAO.deleteSubject(subjectId);
        
        if ("SUCCESS".equals(result)) {
            JOptionPane.showMessageDialog(this, "Subject deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadSubjects();
        } else {
            JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtSubjectName.setText("");
        btnDelete.setEnabled(false);
        subjectTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new SubjectManagementUI(1, null).setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}