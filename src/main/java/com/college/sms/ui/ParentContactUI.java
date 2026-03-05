package com.college.sms.ui;

import com.college.sms.dao.ClassDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.model.Student;
import com.college.sms.util.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class ParentContactUI extends JFrame {
    
    private int facultyId;
    private JFrame previousUI;
    private JComboBox<String> comboClass;
    private JList<String> studentList;
    private DefaultListModel<String> listModel;
    private JTextField txtRollNo, txtName, txtMobile, txtEmail;
    private JCheckBox chkConsent;
    private JButton btnSave, btnUpdate, btnDelete, btnClear;
    private ClassDAO classDAO;
    private StudentDAO studentDAO;
    private JLabel statusLabel;
    
    // Table for displaying parent contact summary
    private JTable contactTable;
    private DefaultTableModel tableModel;
    
    // Currently selected student ID
    private int selectedStudentId = -1;

    public ParentContactUI(int facultyId, JFrame previousUI) {
        this.facultyId = facultyId;
        this.previousUI = previousUI;
        classDAO = new ClassDAO();
        studentDAO = new StudentDAO();

        setTitle("👨‍👩‍👧 Parent Contact Management | Faculty ID: " + facultyId);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 247, 250));

        initComponents();
        loadClasses();
        setVisible(true);
    }

    private void initComponents() {
        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = new JLabel("👨‍👩‍👧 Parent Contact Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton btnBack = createModernButton("⇦ Back", new Color(41, 128, 185), Color.WHITE);
        btnBack.setPreferredSize(new Dimension(150, 40));
        btnBack.addActionListener(e -> {
            dispose();
            if (previousUI != null) {
                previousUI.setVisible(true);
                previousUI.toFront();
            }
        });
        headerPanel.add(btnBack, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ===== MAIN CONTENT (SPLIT PANE) =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(5);
        splitPane.setBackground(new Color(245, 247, 250));

        // ===== LEFT PANEL - STUDENT LIST =====
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        leftPanel.setPreferredSize(new Dimension(380, 0));

        // Class selector
        JPanel classPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        classPanel.setBackground(Color.WHITE);
        classPanel.add(new JLabel("🏫 Select Class:"));
        comboClass = new JComboBox<>();
        comboClass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboClass.setPreferredSize(new Dimension(240, 35));
        comboClass.setBackground(Color.WHITE);
        comboClass.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        comboClass.addActionListener(e -> {
            loadStudents();
            loadContactTable();
        });
        classPanel.add(comboClass);
        leftPanel.add(classPanel, BorderLayout.NORTH);

        // Student list
        listModel = new DefaultListModel<>();
        studentList = new JList<>(listModel);
        studentList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setFixedCellHeight(45);
        studentList.setCellRenderer(new StudentListCellRenderer());
        studentList.setBackground(new Color(248, 250, 252));
        studentList.setSelectionBackground(new Color(52, 152, 219));
        studentList.setSelectionForeground(Color.WHITE);

        JScrollPane listScroll = new JScrollPane(studentList);
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204), 1));
        leftPanel.add(listScroll, BorderLayout.CENTER);

        JLabel listInfoLabel = new JLabel("Select a student to edit details");
        listInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        listInfoLabel.setForeground(new Color(120, 120, 120));
        listInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(listInfoLabel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // ===== RIGHT PANEL - FIXED LAYOUT =====
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ✅ FIX: Use BoxLayout to stack Form + Buttons + Table vertically
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // --- Form Section ---
        JPanel formSection = new JPanel(new BorderLayout(10, 10));
        formSection.setBackground(Color.WHITE);
        formSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        formSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel formTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formTitlePanel.setBackground(Color.WHITE);
        JLabel formTitle = new JLabel("📝 Parent Contact Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(new Color(52, 73, 94));
        formTitlePanel.add(formTitle);
        formSection.add(formTitlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new TitledBorder(
            new LineBorder(new Color(52, 152, 219), 2),
            "Student & Parent Information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 15),
            new Color(52, 152, 219)
        ));
        formPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createLabel("📋 Roll No:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtRollNo = new JTextField();
        txtRollNo.setEditable(false);
        txtRollNo.setBackground(new Color(240, 240, 240));
        txtRollNo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtRollNo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createLabel("👤 Student Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtName = new JTextField();
        txtName.setEditable(false);
        txtName.setBackground(new Color(240, 240, 240));
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(200, 200, 200));
        formPanel.add(sep1, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0;
        formPanel.add(createLabel("📱 Parent Mobile:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtMobile = new JTextField();
        txtMobile.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMobile.setPreferredSize(new Dimension(0, 35));
        txtMobile.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        formPanel.add(txtMobile, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(createLabel("📧 Parent Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEmail.setPreferredSize(new Dimension(0, 35));
        txtEmail.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        formPanel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        chkConsent = new JCheckBox("✅ Parent consents to receive communications (SMS/Email)");
        chkConsent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkConsent.setForeground(new Color(52, 73, 94));
        chkConsent.setBackground(Color.WHITE);
        chkConsent.setSelected(true);
        formPanel.add(chkConsent, gbc);

        formSection.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(formSection);
        contentPanel.add(Box.createVerticalStrut(10));

        // --- Action Buttons Section (NOW VISIBLE!) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        btnSave = createModernButton("💾 Save Details", new Color(46, 204, 113), Color.WHITE);
        btnSave.setPreferredSize(new Dimension(140, 42));
        btnSave.setEnabled(false);
        btnSave.addActionListener(e -> saveParentDetails());
        buttonPanel.add(btnSave);

        btnUpdate = createModernButton("✏️ Update", new Color(52, 152, 219), Color.WHITE);
        btnUpdate.setPreferredSize(new Dimension(140, 42));
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> saveParentDetails());
        buttonPanel.add(btnUpdate);

        btnDelete = createModernButton("🗑️ Clear", new Color(231, 76, 60), Color.WHITE);
        btnDelete.setPreferredSize(new Dimension(140, 42));
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> clearParentDetails());
        buttonPanel.add(btnDelete);

        btnClear = createModernButton("🔄 Reset", new Color(149, 165, 166), Color.WHITE);
        btnClear.setPreferredSize(new Dimension(140, 42));
        btnClear.addActionListener(e -> resetForm());
        buttonPanel.add(btnClear);

        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // --- Table Section ---
        JPanel tableSection = new JPanel(new BorderLayout(10, 10));
        tableSection.setBackground(Color.WHITE);
        tableSection.setBorder(new TitledBorder(
            new LineBorder(new Color(52, 152, 219), 2),
            "📋 Parent Contact Summary (Click row to edit)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 15),
            new Color(52, 152, 219)
        ));
        tableSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        tableModel = new DefaultTableModel(
            new String[]{"Student ID", "Roll No", "Name", "Mobile", "Email", "Consent", "Status"}, 0
        ) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        contactTable = new JTable(tableModel);
        contactTable.setRowHeight(32);
        contactTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contactTable.setGridColor(new Color(230, 230, 230));
        contactTable.setSelectionBackground(new Color(52, 152, 219));
        contactTable.setSelectionForeground(Color.WHITE);

        contactTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected && column == 6) {
                    String status = (String) t.getValueAt(row, 6);
                    if ("✅ Complete".equals(status)) {
                        c.setBackground(new Color(200, 230, 201));
                        c.setForeground(new Color(27, 94, 32));
                    } else {
                        c.setBackground(new Color(255, 243, 205));
                        c.setForeground(new Color(137, 104, 0));
                    }
                }
                return c;
            }
        });

        contactTable.getColumnModel().getColumn(0).setMinWidth(0);
        contactTable.getColumnModel().getColumn(0).setMaxWidth(0);
        contactTable.getColumnModel().getColumn(0).setWidth(0);
        contactTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        contactTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        contactTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        contactTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        contactTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        contactTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        contactTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = contactTable.getSelectedRow();
                if (row >= 0) {
                    try {
                        int studentId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                        selectedStudentId = studentId;
                        loadStudentDetails(studentId);
                        updateButtonStates(true);
                    } catch (NumberFormatException ex) {
                        statusLabel.setText("❌ Error selecting student");
                    }
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(contactTable);
        tableScroll.setPreferredSize(new Dimension(0, 200));
        tableSection.add(tableScroll, BorderLayout.CENTER);

        contentPanel.add(tableSection);

        // Add contentPanel to rightPanel
        rightPanel.add(contentPanel, BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // ===== STATUS BAR =====
        statusLabel = new JLabel("Ready. Select a class to manage parent contacts.");
        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setBackground(new Color(236, 240, 241));
        statusLabel.setForeground(new Color(75, 75, 75));
        statusLabel.setOpaque(true);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(statusLabel, BorderLayout.SOUTH);

        // ===== STUDENT LIST SELECTION LISTENER =====
        studentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = studentList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    String selectedValue = listModel.getElementAt(selectedIndex);
                    String[] parts = selectedValue.split(" \\| ");
                    if (parts.length >= 1) {
                        try {
                            selectedStudentId = Integer.parseInt(parts[0].replace("ID: ", "").trim());
                            loadStudentDetails(selectedStudentId);
                            updateButtonStates(true);
                        } catch (NumberFormatException ex) {
                            statusLabel.setText("❌ Error parsing student ID");
                        }
                    }
                }
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    private JButton createModernButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void updateButtonStates(boolean studentSelected) {
        btnSave.setEnabled(studentSelected);
        btnUpdate.setEnabled(studentSelected);
        btnDelete.setEnabled(studentSelected);
    }

    private void loadClasses() {
        comboClass.removeAllItems();
        List<String[]> classes = classDAO.getClassesByFaculty(facultyId);

        if (classes.isEmpty()) {
            comboClass.addItem("-- No classes assigned --");
            statusLabel.setText("⚠️ No classes assigned. Contact admin.");
            JOptionPane.showMessageDialog(this,
                "No classes found for your account!",
                "No Classes", JOptionPane.WARNING_MESSAGE);
        } else {
            for (String[] c : classes) {
                comboClass.addItem(c[0] + " - " + c[1]);
            }
            comboClass.setSelectedIndex(0);
            loadStudents();
            loadContactTable();
        }
    }

    private void loadStudents() {
        listModel.clear();
        resetForm();

        if (comboClass.getSelectedItem() == null || 
            comboClass.getSelectedItem().toString().contains("--")) {
            statusLabel.setText("⚠️ Select a valid class");
            return;
        }

        try {
            int classId = Integer.parseInt(
                comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            
            List<String[]> students = studentDAO.getStudentsByClass(classId);

            if (students.isEmpty()) {
                statusLabel.setText("ℹ️ No students in this class");
                listModel.addElement("No students found");
            } else {
                for (String[] s : students) {
                    String displayText = String.format("ID: %s | Roll: %s | %s", 
                        s[0], s[1], s[2]);
                    listModel.addElement(displayText);
                }
                statusLabel.setText("✓ Loaded " + students.size() + " students");
            }
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading students: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadContactTable() {
        tableModel.setRowCount(0);

        if (comboClass.getSelectedItem() == null || 
            comboClass.getSelectedItem().toString().contains("--")) {
            return;
        }

        try {
            int classId = Integer.parseInt(
                comboClass.getSelectedItem().toString().split(" - ")[0].trim());
            
            List<String[]> students = studentDAO.getStudentsByClass(classId);

            for (String[] s : students) {
                int studentId = Integer.parseInt(s[0]);
                String rollNo = s[1];
                String name = s[2];
                
                Student student = studentDAO.getStudentById(studentId);
                
                String mobile = (student != null && student.getParentMobile() != null) ? student.getParentMobile() : "-";
                String email = (student != null && student.getParentEmail() != null) ? student.getParentEmail() : "-";
                String consent = (student != null && student.getConsentToCommunicate()) ? "✅ Yes" : "❌ No";
                String status = (!mobile.equals("-") || !email.equals("-")) ? "✅ Complete" : "⚠️ Pending";
                
                tableModel.addRow(new Object[]{
                    studentId, rollNo, name, mobile, email, consent, status
                });
            }
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading contact table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStudentDetails(int studentId) {
        try {
            Student student = studentDAO.getStudentById(studentId);
            
            if (student != null) {
                txtRollNo.setText(student.getRollNo());
                txtName.setText(student.getName());
                txtMobile.setText(student.getParentMobile());
                txtEmail.setText(student.getParentEmail());
                chkConsent.setSelected(student.getConsentToCommunicate() != null ? student.getConsentToCommunicate() : true);
                statusLabel.setText("✓ Editing: " + student.getName());
            } else {
                statusLabel.setText("⚠️ Student not found");
                resetForm();
            }
        } catch (SQLException e) {
            statusLabel.setText("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveParentDetails() {
        if (selectedStudentId <= 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a student first!",
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mobile = txtMobile.getText().trim();
        String email = txtEmail.getText().trim();
        boolean consent = chkConsent.isSelected();

        if (mobile.isEmpty() && email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please provide at least mobile OR email!",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address!",
                "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!mobile.isEmpty() && !mobile.matches("^[0-9]{10,15}$")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid mobile number (10-15 digits)!",
                "Invalid Mobile", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = studentDAO.updateParentContact(selectedStudentId, mobile, email, consent);
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "✅ Parent details saved successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("✓ Details saved for student ID: " + selectedStudentId);
                loadStudents();
                loadContactTable();
                for (int i = 0; i < listModel.size(); i++) {
                    if (listModel.getElementAt(i).startsWith("ID: " + selectedStudentId)) {
                        studentList.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Failed to save details. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "❌ Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearParentDetails() {
        if (selectedStudentId <= 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a student first!",
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "⚠️ Are you sure you want to clear parent contact details?\n\n" +
            "This will remove mobile, email and reset consent.\n\n" +
            "This action cannot be undone!",
            "Confirm Clear", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = studentDAO.updateParentContact(selectedStudentId, "", "", true);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "✅ Parent details cleared successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    statusLabel.setText("✓ Details cleared for student ID: " + selectedStudentId);
                    loadContactTable();
                    loadStudentDetails(selectedStudentId);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "❌ Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void resetForm() {
        txtRollNo.setText("");
        txtName.setText("");
        txtMobile.setText("");
        txtEmail.setText("");
        chkConsent.setSelected(true);
        selectedStudentId = -1;
        updateButtonStates(false);
        statusLabel.setText("Form reset. Select a student to edit.");
    }

    private class StudentListCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(new EmptyBorder(5, 10, 5, 10));
            if (isSelected) {
                setBackground(new Color(52, 152, 219));
                setForeground(Color.WHITE);
            } else {
                setBackground(index % 2 == 0 ? new Color(248, 250, 252) : Color.WHITE);
                setForeground(new Color(52, 73, 94));
            }
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                JFrame mockDashboard = new JFrame("Faculty Dashboard");
                mockDashboard.setSize(1000, 650);
                mockDashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mockDashboard.setVisible(true);
                new ParentContactUI(1, mockDashboard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}