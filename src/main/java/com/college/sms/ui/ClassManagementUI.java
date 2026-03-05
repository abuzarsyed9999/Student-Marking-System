//package com.college.sms.ui;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.List;
//import com.college.sms.dao.ClassDAO;
//
//public class ClassManagementUI extends JFrame {
//
//    private int facultyId;
//    private ClassDAO classDAO;
//
//    public ClassManagementUI(int facultyId) {
//
//        this.facultyId = facultyId;
//        this.classDAO = new ClassDAO();
//
//        setTitle("Class Management");
//        setSize(500, 400);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        JLabel lblClassName = new JLabel("Class Name:");
//        JTextField txtClassName = new JTextField(15);
//
//        JButton btnAddClass = new JButton("Add Class");
//        JButton btnViewClasses = new JButton("View Classes");
//        JButton btnBack = new JButton("Back");
//
//        JTextArea outputArea = new JTextArea();
//        outputArea.setEditable(false);
//        JScrollPane scrollPane = new JScrollPane(outputArea);
//
//        // ADD CLASS
//        btnAddClass.addActionListener(e -> {
//            String className = txtClassName.getText().trim();
//
//            if (className.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Class name required");
//                return;
//            }
//
//            boolean added = classDAO.addClass(className, facultyId);
//            if (added) {
//                JOptionPane.showMessageDialog(this, "Class added successfully");
//                txtClassName.setText("");
//            } else {
//                JOptionPane.showMessageDialog(this, "Failed to add class");
//            }
//        });
//
//        // VIEW CLASSES (FIXED)
//        btnViewClasses.addActionListener(e -> {
//            outputArea.setText("");
//            List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
//
//            if (classes.isEmpty()) {
//                outputArea.setText("No classes found.");
//            } else {
//                for (String[] cls : classes) {
//                    outputArea.append(
//                        "ID: " + cls[0] + " | Class Name: " + cls[1] + "\n"
//                    );
//                }
//            }
//        });
//
//        // BACK BUTTON
//        btnBack.addActionListener(e -> {
//            dispose(); // close current window
//            // new FacultyDashboardUI(facultyId).setVisible(true); // optional
//        });
//
//        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
//        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        inputPanel.add(lblClassName);
//        inputPanel.add(txtClassName);
//        inputPanel.add(btnAddClass);
//        inputPanel.add(btnViewClasses);
//        inputPanel.add(btnBack);
//
//        add(inputPanel, BorderLayout.NORTH);
//        add(scrollPane, BorderLayout.CENTER);
//    }
//}
package com.college.sms.ui;

import com.college.sms.dao.ClassDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ClassManagementUI extends JFrame {

    private int facultyId;
    private ClassDAO classDAO;
    private DefaultTableModel tableModel;
    private JTable classTable;
    private JTextField txtClassName;
    private JButton btnAdd, btnDelete, btnRefresh, btnBack;
    private JLabel statusLabel;

    public ClassManagementUI(int facultyId) {
        this.facultyId = facultyId;
        this.classDAO = new ClassDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("🏫 Class Management | Faculty ID: " + facultyId);
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 247, 250));

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Class"));
        formPanel.setBackground(Color.WHITE);
        
        formPanel.add(new JLabel("Class Name:"));
        txtClassName = new JTextField(20);
        txtClassName.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(txtClassName);
        
        btnAdd = new JButton("➕ Add Class");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        formPanel.add(btnAdd);
        
        add(formPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] columns = {"Class ID", "Class Name"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        classTable = new JTable(tableModel);
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classTable.setRowHeight(28);
        classTable.setFont(new Font("Arial", Font.PLAIN, 13));
        classTable.getTableHeader().setBackground(new Color(41, 128, 185));
        classTable.getTableHeader().setForeground(Color.WHITE);
        
        // Row selection handler
        classTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnDelete.setEnabled(classTable.getSelectedRow() != -1);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(classTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Classes"));
        add(scrollPane, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        buttonPanel.setBackground(Color.WHITE);
        
        btnDelete = new JButton("🗑️ Delete Class");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setEnabled(false); // Disabled until row selected
        
        btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        
        btnBack = new JButton("🔙 Back to Dashboard");
        btnBack.setBackground(new Color(142, 68, 173));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);
        
        // ===== STATUS BAR =====
        statusLabel = new JLabel("Ready to manage classes");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statusLabel.setBackground(new Color(236, 240, 241));
        statusLabel.setOpaque(true);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // ===== LAYOUT =====
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        bottomPanel.setBackground(Color.WHITE);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== ACTION LISTENERS =====
        btnAdd.addActionListener(e -> addClass());
        btnDelete.addActionListener(e -> deleteClass());
        btnRefresh.addActionListener(e -> {
            loadData();
            clearForm();
        });
        btnBack.addActionListener(e -> {
            dispose();
            new FacultyDashboard(facultyId).setVisible(true);
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);
        
        if (classes.isEmpty()) {
            statusLabel.setText("⚠️ No classes assigned. Add a new class to get started.");
            JOptionPane.showMessageDialog(this,
                "No classes found for your account.\nClick 'Add Class' to create your first class.",
                "Empty List", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (String[] cls : classes) {
                tableModel.addRow(new Object[]{cls[0], cls[1]});
            }
            statusLabel.setText("Loaded " + classes.size() + " class(es)");
        }
    }

    private void addClass() {
        String className = txtClassName.getText().trim();
        
        if (className.isEmpty()) {
            showError("Class name cannot be empty");
            txtClassName.requestFocus();
            return;
        }
        
        if (className.length() < 2) {
            showError("Class name must be at least 2 characters");
            return;
        }
        
        boolean added = classDAO.addClass(className, facultyId);
        
        if (added) {
            showSuccess("✅ Class '" + className + "' added successfully!");
            clearForm();
            loadData();
            statusLabel.setText("✓ Class added: " + className);
        } else {
            showError("Failed to add class. Class name might already exist.");
        }
    }

    private void deleteClass() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a class to delete");
            return;
        }
        
        int classId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String className = tableModel.getValueAt(selectedRow, 1).toString();
        
        // Show detailed confirmation with workflow guidance
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>⚠️ Confirm Deletion</b><br><br>" +
            "Delete class: <b>" + className + "</b><br><br>" +
            "<font color='#e74c3c'>❗ This action cannot be undone!</font><br><br>" +
            "<b>Requirements:</b><br>" +
            "✓ No students enrolled<br>" +
            "✓ No subjects assigned</html>",
            "Delete Class",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        // ✅ CRITICAL: Pass facultyId for security check
        String result = classDAO.deleteClass(classId, facultyId);
        
        if ("SUCCESS".equals(result)) {
            showSuccess("✅ Class '" + className + "' deleted successfully!");
            clearForm();
            loadData();
            statusLabel.setText("✓ Class deleted: " + className);
        } else {
            // Show enhanced error message with workflow guidance
            showError("<html><b>Cannot delete class:</b><br>" + 
                     result.replace("\n", "<br>") + "</html>");
        }
    }

    private void clearForm() {
        txtClassName.setText("");
        txtClassName.requestFocus();
        btnDelete.setEnabled(false);
        classTable.clearSelection();
        statusLabel.setText("Form cleared. Ready for new entry.");
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new ClassManagementUI(1).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Database connection failed!",
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}