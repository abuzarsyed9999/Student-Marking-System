//package com.college.sms.ui;
//
//import com.college.sms.dao.StudentDAO;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//
//public class StudentManagementUI extends JFrame {
//
//    private JTextField txtStudentId, txtRollNo, txtName, txtClassId;
//    private JTable table;
//    private DefaultTableModel model;
//
//    private StudentDAO studentDAO = new StudentDAO();
//
//    public StudentManagementUI() {
//
//        setTitle("Student Management");
//        setSize(750, 450);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        // -------- INPUT PANEL --------
//        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
//
//        inputPanel.add(new JLabel("Student ID (for update/delete):"));
//        txtStudentId = new JTextField();
//        inputPanel.add(txtStudentId);
//
//        inputPanel.add(new JLabel("Roll No:"));
//        txtRollNo = new JTextField();
//        inputPanel.add(txtRollNo);
//
//        inputPanel.add(new JLabel("Name:"));
//        txtName = new JTextField();
//        inputPanel.add(txtName);
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
//
//        buttonPanel.add(btnAdd);
//        buttonPanel.add(btnUpdate);
//        buttonPanel.add(btnDelete);
//        buttonPanel.add(btnRefresh);
//
//        // -------- TABLE --------
//        model = new DefaultTableModel(
//                new String[]{"Student ID", "Roll No", "Name", "Class ID"}, 0
//        );
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
//        btnAdd.addActionListener(e -> addStudent());
//        btnUpdate.addActionListener(e -> updateStudent());
//        btnDelete.addActionListener(e -> deleteStudent());
//        btnRefresh.addActionListener(e -> loadStudents());
//
//        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
//
//        loadStudents();
//    }
//
//    // ---------------- CRUD METHODS ----------------
//
//    private void addStudent() {
//        try {
//            String rollNo = txtRollNo.getText().trim();
//            String name = txtName.getText().trim();
//            int classId = Integer.parseInt(txtClassId.getText().trim());
//
//            boolean added = studentDAO.addStudent(rollNo, name, classId);
//
//            if (added) {
//                JOptionPane.showMessageDialog(this, "Student added successfully");
//                loadStudents();
//                clearFields();
//            } else {
//                JOptionPane.showMessageDialog(this, "Failed to add student");
//            }
//
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Invalid input");
//        }
//    }
//
//    private void updateStudent() {
//        try {
//            int studentId = Integer.parseInt(txtStudentId.getText().trim());
//            String rollNo = txtRollNo.getText().trim();
//            String name = txtName.getText().trim();
//            int classId = Integer.parseInt(txtClassId.getText().trim());
//
//            boolean updated = studentDAO.updateStudent(studentId, rollNo, name, classId);
//
//            if (updated) {
//                JOptionPane.showMessageDialog(this, "Student updated");
//                loadStudents();
//                clearFields();
//            } else {
//                JOptionPane.showMessageDialog(this, "Update failed");
//            }
//
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Invalid input");
//        }
//    }
//
//    private void deleteStudent() {
//        try {
//            int studentId = Integer.parseInt(txtStudentId.getText().trim());
//
//            int result = studentDAO.deleteStudent(studentId);
//
//            if (result == -1) {
//                JOptionPane.showMessageDialog(this,
//                        "Cannot delete student.\nMarks exist for this student.");
//            } else if (result == 1) {
//                JOptionPane.showMessageDialog(this, "Student deleted");
//                loadStudents();
//                clearFields();
//            } else {
//                JOptionPane.showMessageDialog(this, "Delete failed");
//            }
//
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Invalid Student ID");
//        }
//    }
//
//    private void loadStudents() {
//        model.setRowCount(0);
//        studentDAO.getAllStudents(model);   // ✅ MATCHES YOUR DAO
//    }
//
//    private void fillFormFromTable() {
//        int row = table.getSelectedRow();
//        if (row != -1) {
//            txtStudentId.setText(model.getValueAt(row, 0).toString());
//            txtRollNo.setText(model.getValueAt(row, 1).toString());
//            txtName.setText(model.getValueAt(row, 2).toString());
//            txtClassId.setText(model.getValueAt(row, 3).toString());
//        }
//    }
//
//    private void clearFields() {
//        txtStudentId.setText("");
//        txtRollNo.setText("");
//        txtName.setText("");
//        txtClassId.setText("");
//    }
//}

//-------
//package com.college.sms.ui;
//
//import com.college.sms.dao.StudentDAO;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//
//public class StudentManagementUI extends JFrame {
//
//    private JTextField txtStudentId, txtRollNo, txtName, txtClassId;
//    private JTable table;
//    private DefaultTableModel model;
//
//    private StudentDAO studentDAO = new StudentDAO();
//    private JFrame previousUI; // reference to previous page
//
//    // Constructor with previous JFrame
//    public StudentManagementUI(JFrame previousUI) {
//        this.previousUI = previousUI;
//
//        setTitle("Student Management");
//        setSize(750, 450);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        // -------- INPUT PANEL --------
//        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
//
//        inputPanel.add(new JLabel("Student ID (for update/delete):"));
//        txtStudentId = new JTextField();
//        inputPanel.add(txtStudentId);
//
//        inputPanel.add(new JLabel("Roll No:"));
//        txtRollNo = new JTextField();
//        inputPanel.add(txtRollNo);
//
//        inputPanel.add(new JLabel("Name:"));
//        txtName = new JTextField();
//        inputPanel.add(txtName);
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
//        JButton btnBack = new JButton("Back"); // NEW Back button
//
//        buttonPanel.add(btnAdd);
//        buttonPanel.add(btnUpdate);
//        buttonPanel.add(btnDelete);
//        buttonPanel.add(btnRefresh);
//        buttonPanel.add(btnBack); // add back button to panel
//
//        // -------- TABLE --------
//        model = new DefaultTableModel(
//                new String[]{"Student ID", "Roll No", "Name", "Class ID"}, 0
//        );
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
//        btnAdd.addActionListener(e -> addStudent());
//        btnUpdate.addActionListener(e -> updateStudent());
//        btnDelete.addActionListener(e -> deleteStudent());
//        btnRefresh.addActionListener(e -> loadStudents());
//        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
//
//        // ---- BACK BUTTON LOGIC ----
//        btnBack.addActionListener(e -> {
//            dispose(); // close StudentManagementUI
//            if (previousUI != null) {
//                previousUI.setVisible(true); // show previous page
//            }
//        });
//
//        loadStudents();
//    }
//
//    // ---------------- CRUD METHODS ----------------
//
//    private void addStudent() {
//        try {
//            String rollNo = txtRollNo.getText().trim();
//            String name = txtName.getText().trim();
//            int classId = Integer.parseInt(txtClassId.getText().trim());
//
//            boolean added = studentDAO.addStudent(rollNo, name, classId);
//
//            if (added) {
//                JOptionPane.showMessageDialog(this, "Student added successfully");
//                loadStudents();
//                clearFields();
//            } else {
//                JOptionPane.showMessageDialog(this, "Failed to add student");
//            }
//
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Invalid input");
//        }
//    }
//
//    private void updateStudent() {
//        try {
//            int studentId = Integer.parseInt(txtStudentId.getText().trim());
//            String rollNo = txtRollNo.getText().trim();
//            String name = txtName.getText().trim();
//            int classId = Integer.parseInt(txtClassId.getText().trim());
//
//            boolean updated = studentDAO.updateStudent(studentId, rollNo, name, classId);
//
//            if (updated) {
//                JOptionPane.showMessageDialog(this, "Student updated");
//                loadStudents();
//                clearFields();
//            } else {
//                JOptionPane.showMessageDialog(this, "Update failed");
//            }
//
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Invalid input");
//        }
//    }
//
//    private void deleteStudent() {
//        try {
//            int studentId = Integer.parseInt(txtStudentId.getText().trim());
//
//            int result = studentDAO.deleteStudent(studentId);
//
//            if (result == -1) {
//                JOptionPane.showMessageDialog(this,
//                        "Cannot delete student.\nMarks exist for this student.");
//            } else if (result == 1) {
//                JOptionPane.showMessageDialog(this, "Student deleted");
//                loadStudents();
//                clearFields();
//            } else {
//                JOptionPane.showMessageDialog(this, "Delete failed");
//            }
//
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Invalid Student ID");
//        }
//    }
//
//    private void loadStudents() {
//        model.setRowCount(0);
//        studentDAO.getAllStudents(model);
//    }
//
//    private void fillFormFromTable() {
//        int row = table.getSelectedRow();
//        if (row != -1) {
//            txtStudentId.setText(model.getValueAt(row, 0).toString());
//            txtRollNo.setText(model.getValueAt(row, 1).toString());
//            txtName.setText(model.getValueAt(row, 2).toString());
//            txtClassId.setText(model.getValueAt(row, 3).toString());
//        }
//    }
//
//    private void clearFields() {
//        txtStudentId.setText("");
//        txtRollNo.setText("");
//        txtName.setText("");
//        txtClassId.setText("");
//    }
//}

//---
package com.college.sms.ui;

import com.college.sms.dao.ClassDAO;
import com.college.sms.dao.StudentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class StudentManagementUI extends JFrame {

    private JTextField txtRollNo, txtName;
    private JComboBox<String> comboClass;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO;
    private ClassDAO classDAO;
    private JFrame previousUI;
    private int facultyId;
    private JButton btnUpdate, btnDelete;
    private JLabel statusLabel; // ✅ DECLARED AS CLASS FIELD (critical fix)

    public StudentManagementUI(int facultyId, JFrame previousUI) {
        this.facultyId = facultyId;
        this.previousUI = previousUI;
        this.studentDAO = new StudentDAO();
        this.classDAO = new ClassDAO();

        setTitle("Student Management - Faculty ID: " + facultyId);
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Roll Number *"));
        txtRollNo = new JTextField();
        formPanel.add(txtRollNo);

        formPanel.add(new JLabel("Student Name *"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Class *"));
        comboClass = new JComboBox<>();
        formPanel.add(comboClass);

        add(formPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Roll No", "Name", "Class", "Class ID"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(25);
        
        // Hide Class ID column
        studentTable.getColumnModel().getColumn(4).setMinWidth(0);
        studentTable.getColumnModel().getColumn(4).setMaxWidth(0);
        
        // ✅ CRITICAL FIX: Use ListSelectionListener (not MouseListener) for reliable selection
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleRowSelection();
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Students in Your Classes"));
        add(scrollPane, BorderLayout.CENTER);

        // ===== BUTTONS PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnAdd = new JButton("Add Student");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        
        btnUpdate = new JButton("Update Student");
        btnUpdate.setBackground(new Color(52, 152, 219));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setEnabled(false);
        
        btnDelete = new JButton("Delete Student");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setEnabled(false);
        
        JButton btnClear = new JButton("Clear Form");
        btnClear.setBackground(new Color(142, 68, 173));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        
        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.setBackground(new Color(243, 156, 18));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);

        // Status bar (declared as class field)
        statusLabel = new JLabel("Ready to manage students");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== ACTION LISTENERS =====
        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> {
            dispose();
            if (previousUI != null) previousUI.setVisible(true);
        });

        // ===== LOAD DATA =====
        loadClasses();
        loadStudents();

        setVisible(true);
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
        }
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        List<String[]> students = studentDAO.getStudentsByFaculty(facultyId);
        for (String[] s : students) {
            tableModel.addRow(new Object[]{s[0], s[1], s[2], s[3], s[4]});
        }
    }

    private void addStudent() {
        String rollNo = txtRollNo.getText().trim();
        String name = txtName.getText().trim();
        
        if (rollNo.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Roll Number and Name are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (comboClass.getItemCount() == 0 || comboClass.getSelectedItem().toString().contains("-- No classes")) {
            JOptionPane.showMessageDialog(this, "Select a valid class!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            boolean added = studentDAO.addStudent(rollNo, name, classId, facultyId);
            
            if (added) {
                JOptionPane.showMessageDialog(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadStudents();
                statusLabel.setText("✓ Student added: " + name);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student. Roll No may exist or class not assigned to you.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String rollNo = txtRollNo.getText().trim();
        String name = txtName.getText().trim();
        
        if (rollNo.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Roll Number and Name are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int studentId = Integer.parseInt(studentTable.getValueAt(row, 0).toString());
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            
            boolean updated = studentDAO.updateStudent(studentId, rollNo, name, classId, facultyId);
            
            if (updated) {
                JOptionPane.showMessageDialog(this, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadStudents();
                statusLabel.setText("✓ Student updated: " + name);
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Student may not belong to your classes.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String name = studentTable.getValueAt(row, 2).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete student '" + name + "'?\nThis cannot be undone!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try {
            int studentId = Integer.parseInt(studentTable.getValueAt(row, 0).toString());
            String result = studentDAO.deleteStudent(studentId, facultyId);
            
            if ("SUCCESS".equals(result)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadStudents();
                statusLabel.setText("✓ Student deleted: " + name);
            } else {
                JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ✅ CRITICAL FIX: Proper row selection handling
    private void handleRowSelection() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            return;
        }
        
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        
        txtRollNo.setText(studentTable.getValueAt(row, 1).toString());
        txtName.setText(studentTable.getValueAt(row, 2).toString());
        
        String classId = studentTable.getValueAt(row, 4).toString();
        for (int i = 0; i < comboClass.getItemCount(); i++) {
            if (comboClass.getItemAt(i).toString().startsWith(classId + " - ")) {
                comboClass.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        txtRollNo.setText("");
        txtName.setText("");
        if (comboClass.getItemCount() > 0) {
            comboClass.setSelectedIndex(0);
        }
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        studentTable.clearSelection();
        statusLabel.setText("Form cleared. Ready for new entry.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new StudentManagementUI(1, null).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
//--