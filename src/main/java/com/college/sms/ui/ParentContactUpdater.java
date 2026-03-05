package com.college.sms.ui;

import com.college.sms.dao.StudentDAO;
import com.college.sms.model.Student;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class ParentContactUpdater extends JDialog {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO;
    private JComboBox<String> comboClass;
    private int facultyId;
    private JLabel statusLabel;
    
    // ✅ Edit form components
    private JTextField txtRollNo, txtName, txtMobile, txtEmail;
    private JCheckBox chkConsent;
    private JLabel lblStudentId; // Hidden label to store selected student ID
    
    // ✅ FIX: Store updatedModel as class variable for direct access
    private DefaultTableModel updatedModel;
    
    // ✅ Mobile number validation pattern (Indian format)
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^(\\+91|91|0)?[6-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public ParentContactUpdater(JFrame parent, int facultyId) {
        super(parent, "👨‍👩‍👧 Update Parent Contacts", true);
        this.facultyId = facultyId;
        studentDAO = new StudentDAO();
        setSize(1100, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 247, 250));
        initUI();
    }

    private void initUI() {
        // ===== MAIN SPLIT PANE: Left (Students) | Right (Edit Form) =====
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(450);
        mainSplitPane.setResizeWeight(0.4);
        mainSplitPane.setContinuousLayout(true);
        mainSplitPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        // ===== LEFT PANEL: Class Selection + Student List =====
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top: Class selector
        JPanel classPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        classPanel.setBackground(Color.WHITE);
        classPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        classPanel.add(new JLabel("🏫 Class:"));
        comboClass = new JComboBox<>();
        comboClass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboClass.setPreferredSize(new Dimension(250, 35));
        comboClass.setBackground(Color.WHITE);
        comboClass.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        classPanel.add(comboClass);
        
        JButton btnLoad = createActionButton("🔄 Load", new Color(52, 152, 219));
        btnLoad.setPreferredSize(new Dimension(100, 35));
        btnLoad.addActionListener(e -> loadStudents());
        classPanel.add(btnLoad);
        
        leftPanel.add(classPanel, BorderLayout.NORTH);

        // Middle: Student table
        String[] columns = {"ID", "Roll No", "Name", "Mobile", "Email", "Consent"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(32);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.setGridColor(new Color(230, 230, 230));
        studentTable.setSelectionBackground(new Color(52, 152, 219));
        studentTable.setSelectionForeground(Color.WHITE);
        studentTable.setAutoCreateRowSorter(true);
        
        // Hide ID column
        studentTable.getColumnModel().getColumn(0).setMinWidth(0);
        studentTable.getColumnModel().getColumn(0).setMaxWidth(0);
        studentTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        
        // Custom renderer for consent column
        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected && column == 5) {
                    Boolean consent = (Boolean) value;
                    if (consent != null && consent) {
                        c.setForeground(new Color(27, 94, 32));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(183, 28, 28));
                    }
                }
                return c;
            }
        });
        
        // Row selection listener - load student data into edit form
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = studentTable.getSelectedRow();
                if (row >= 0) {
                    loadStudentIntoForm(row);
                }
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(studentTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "📋 Students in Selected Class",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        leftPanel.add(tableScroll, BorderLayout.CENTER);
        
        // Bottom: Info label
        JLabel infoLabel = new JLabel("💡 Select a student to edit parent contact details");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        leftPanel.add(infoLabel, BorderLayout.SOUTH);

        mainSplitPane.setLeftComponent(leftPanel);

        // ===== RIGHT PANEL: Edit Form =====
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Form title
        JLabel formTitle = new JLabel("✏️ Edit Parent Contact Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(new Color(52, 73, 94));
        formTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        rightPanel.add(formTitle, BorderLayout.NORTH);

        // Form fields panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 12));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Hidden field for student ID
        lblStudentId = new JLabel("-1");
        lblStudentId.setVisible(false);
        formPanel.add(lblStudentId);
        formPanel.add(new JLabel()); // Empty cell

        // Roll No (read-only)
        formPanel.add(createFormLabel("Roll No:"));
        txtRollNo = createReadOnlyField();
        formPanel.add(txtRollNo);

        // Name (read-only)
        formPanel.add(createFormLabel("Student Name:"));
        txtName = createReadOnlyField();
        formPanel.add(txtName);

        // Parent Mobile (editable)
        formPanel.add(createFormLabel("Parent Mobile:"));
        txtMobile = createEditableField("Enter mobile (e.g., 9876543210)");
        formPanel.add(txtMobile);

        // Parent Email (editable)
        formPanel.add(createFormLabel("Parent Email:"));
        txtEmail = createEditableField("Enter email (e.g., parent@example.com)");
        formPanel.add(txtEmail);

        // Consent checkbox
        formPanel.add(createFormLabel("Consent to Communicate:"));
        chkConsent = new JCheckBox("Allow email/SMS notifications");
        chkConsent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkConsent.setBackground(Color.WHITE);
        chkConsent.setForeground(new Color(52, 73, 94));
        formPanel.add(chkConsent);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        // Action buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnSave = createActionButton("💾 Save", new Color(46, 204, 113));
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.addActionListener(e -> saveChanges());
        buttonPanel.add(btnSave);

        JButton btnUpdate = createActionButton("🔄 Update", new Color(52, 152, 219));
        btnUpdate.setPreferredSize(new Dimension(100, 35));
        btnUpdate.addActionListener(e -> updateStudent());
        buttonPanel.add(btnUpdate);

        JButton btnDelete = createActionButton("🗑️ Clear", new Color(231, 76, 60));
        btnDelete.setPreferredSize(new Dimension(100, 35));
        btnDelete.addActionListener(e -> clearForm());
        buttonPanel.add(btnDelete);

        JButton btnCancel = createActionButton("❌ Cancel", new Color(149, 165, 166));
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnCancel);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainSplitPane.setRightComponent(rightPanel);
        add(mainSplitPane, BorderLayout.CENTER);

        // ===== BOTTOM PANEL: Updated Data Display =====
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(248, 250, 252));
        bottomPanel.setBorder(new TitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "✅ Recently Updated Student Data",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        bottomPanel.setPreferredSize(new Dimension(0, 120));

        // Table to show updated data
        String[] updatedCols = {"Roll No", "Name", "Mobile", "Email", "Consent", "Status"};
        // ✅ FIX: Store as class variable for direct access
        updatedModel = new DefaultTableModel(updatedCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        JTable updatedTable = new JTable(updatedModel);
        updatedTable.setRowHeight(30);
        updatedTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        updatedTable.setGridColor(new Color(220, 220, 220));
        
        // Custom renderer for status column
        updatedTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected && column == 5) {
                    String status = value.toString();
                    if ("✅ Saved".equals(status)) {
                        c.setBackground(new Color(200, 230, 201));
                        c.setForeground(new Color(27, 94, 32));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if ("⚠️ Invalid".equals(status)) {
                        c.setBackground(new Color(255, 243, 205));
                        c.setForeground(new Color(137, 104, 0));
                    }
                }
                return c;
            }
        });
        
        JScrollPane updatedScroll = new JScrollPane(updatedTable);
        updatedScroll.setPreferredSize(new Dimension(0, 60));
        bottomPanel.add(updatedScroll, BorderLayout.CENTER);

        // Clear updated table button
        JButton btnClearUpdated = new JButton("🗑️ Clear History");
        btnClearUpdated.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnClearUpdated.setPreferredSize(new Dimension(130, 28));
        btnClearUpdated.addActionListener(e -> {
            if (updatedModel != null) {  // ✅ Null check
                updatedModel.setRowCount(0);
            }
        });
        bottomPanel.add(btnClearUpdated, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // ===== STATUS BAR =====
        statusLabel = new JLabel("Ready. Select a class and click 'Load' to view students.");
        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setBackground(new Color(236, 240, 241));
        statusLabel.setForeground(new Color(75, 75, 75));
        statusLabel.setOpaque(true);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(statusLabel, BorderLayout.SOUTH);

        // ===== LOAD CLASSES ON START =====
        loadClasses();
    }

    // ===== HELPER METHODS =====

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    private JTextField createReadOnlyField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setEditable(false);
        field.setBackground(new Color(245, 247, 250));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        return field;
    }

    private JTextField createEditableField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setEditable(true);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        field.putClientProperty("placeholder", placeholder);
        return field;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }

    // ✅ Validate Indian mobile number format
    private boolean isValidMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) return true;
        return MOBILE_PATTERN.matcher(mobile.trim()).matches();
    }

    // ✅ Validate email format
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    // ✅ Format mobile to Msg91 compatible format
    private String formatMobileForSMS(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) return null;
        String cleaned = mobile.replaceAll("[\\s\\-\\(\\)\\+]", "");
        if (cleaned.startsWith("0")) {
            cleaned = "91" + cleaned.substring(1);
        } else if (!cleaned.startsWith("91")) {
            cleaned = "91" + cleaned;
        }
        return cleaned.matches("^91\\d{10}$") ? cleaned : null;
    }

    // ✅ Update status label with color coding
    private void updateStatus(String message, Color color) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
        }
    }

    // ✅ Load classes into dropdown
    private void loadClasses() {
        comboClass.removeAllItems();
        updateStatus("Loading classes...", new Color(243, 156, 18));
        
        try {
            java.sql.Connection conn = com.college.sms.util.DBConnection.getConnection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(
                "SELECT class_id, class_name FROM class WHERE faculty_id = " + facultyId
            );
            while (rs.next()) {
                comboClass.addItem(rs.getInt("class_id") + " - " + rs.getString("class_name"));
            }
            conn.close();
            
            if (comboClass.getItemCount() > 0) {
                comboClass.setSelectedIndex(0);
                updateStatus("✓ Classes loaded. Click 'Load' to view students.", new Color(46, 204, 113));
            } else {
                comboClass.addItem("-- No classes assigned --");
                updateStatus("⚠️ No classes found for your account", new Color(243, 156, 18));
            }
        } catch (Exception e) {
            comboClass.addItem("-- Error loading classes --");
            updateStatus("❌ Error: " + e.getMessage(), new Color(231, 76, 60));
            e.printStackTrace();
        }
    }

    // ✅ Load students into table
    private void loadStudents() {
        tableModel.setRowCount(0);
        clearForm();
        
        if (comboClass.getSelectedItem() == null) return;
        
        updateStatus("Loading students...", new Color(243, 156, 18));
        
        try {
            int classId = Integer.parseInt(comboClass.getSelectedItem().toString().split(" - ")[0]);
            List<String[]> students = studentDAO.getStudentsByClass(classId);
            
            for (String[] s : students) {
                Student fullStudent = studentDAO.getStudentById(Integer.parseInt(s[0]));
                
                Object[] row = {
                    fullStudent.getStudentId(),
                    fullStudent.getRollNo(),
                    fullStudent.getName(),
                    fullStudent.getParentMobile() != null ? fullStudent.getParentMobile() : "-",
                    fullStudent.getParentEmail() != null ? fullStudent.getParentEmail() : "-",
                    fullStudent.getConsentToCommunicate() != null ? fullStudent.getConsentToCommunicate() : false
                };
                tableModel.addRow(row);
            }
            
            updateStatus(String.format("✓ Loaded %d students. Select one to edit.", students.size()), 
                new Color(46, 204, 113));
                
        } catch (Exception e) {
            updateStatus("❌ Error: " + e.getMessage(), new Color(231, 76, 60));
            JOptionPane.showMessageDialog(this, "Error loading students:\n" + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ✅ Load selected student data into edit form
    private void loadStudentIntoForm(int row) {
        try {
            int studentId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            Student student = studentDAO.getStudentById(studentId);
            
            lblStudentId.setText(String.valueOf(studentId));
            txtRollNo.setText(student.getRollNo());
            txtName.setText(student.getName());
            txtMobile.setText(student.getParentMobile() != null ? student.getParentMobile() : "");
            txtEmail.setText(student.getParentEmail() != null ? student.getParentEmail() : "");
            chkConsent.setSelected(student.getConsentToCommunicate() != null ? student.getConsentToCommunicate() : false);
            
            updateStatus("✓ Loaded " + student.getName() + " - Edit details and click Save", 
                new Color(52, 152, 219));
                
        } catch (Exception e) {
            updateStatus("❌ Error loading student: " + e.getMessage(), new Color(231, 76, 60));
            e.printStackTrace();
        }
    }

    // ✅ Save changes to database
    private void saveChanges() {
        String studentIdStr = lblStudentId.getText();
        if ("-1".equals(studentIdStr)) {
            JOptionPane.showMessageDialog(this, "Please select a student first!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int studentId = Integer.parseInt(studentIdStr);
        String mobile = txtMobile.getText().trim();
        String email = txtEmail.getText().trim();
        boolean consent = chkConsent.isSelected();
        
        // Validate inputs
        if (!mobile.isEmpty() && !isValidMobile(mobile)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid mobile format! Use format: 9876543210 or +919876543210", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtMobile.requestFocus();
            return;
        }
        
        if (!email.isEmpty() && !isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid email format! Use format: parent@example.com", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        try {
            // Format mobile for SMS compatibility
            String formattedMobile = formatMobileForSMS(mobile);
            
            // Update database
            boolean success = studentDAO.updateParentContact(studentId, mobile, email, consent);
            
            if (success) {
                // Update table row
                int tableRow = findStudentInTable(studentId);
                if (tableRow >= 0) {
                    tableModel.setValueAt(mobile.isEmpty() ? "-" : mobile, tableRow, 3);
                    tableModel.setValueAt(email.isEmpty() ? "-" : email, tableRow, 4);
                    tableModel.setValueAt(consent, tableRow, 5);
                }
                
                // ✅ FIX: Use direct reference to updatedModel
                addToUpdateHistory(txtRollNo.getText(), txtName.getText(), 
                    mobile, email, consent, "✅ Saved");
                
                updateStatus("✓ Saved successfully! Mobile formatted for SMS: " + 
                    (formattedMobile != null ? formattedMobile : "N/A"), 
                    new Color(46, 204, 113));
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Parent contact details updated successfully!\n\n" +
                    "📱 SMS-ready mobile: " + (formattedMobile != null ? formattedMobile : "Not provided") + "\n" +
                    "📧 Email: " + (email.isEmpty() ? "Not provided" : email) + "\n" +
                    "✅ Consent: " + (consent ? "Granted" : "Not granted"),
                    "Update Complete", JOptionPane.INFORMATION_MESSAGE);
                    
            } else {
                updateStatus("❌ Save failed", new Color(231, 76, 60));
                JOptionPane.showMessageDialog(this, "Failed to update database", 
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            updateStatus("❌ Error: " + e.getMessage(), new Color(231, 76, 60));
            JOptionPane.showMessageDialog(this, "Error saving:\n" + e.getMessage(), 
                "Save Failed", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ✅ Update student (alias for save - same functionality)
    private void updateStudent() {
        saveChanges(); // Same as save
    }

    // ✅ Clear form fields
    private void clearForm() {
        lblStudentId.setText("-1");
        txtRollNo.setText("");
        txtName.setText("");
        txtMobile.setText("");
        txtEmail.setText("");
        chkConsent.setSelected(false);
        studentTable.clearSelection();
        updateStatus("Form cleared. Select a student to edit.", new Color(100, 100, 100));
    }

    // ✅ Find student row in table by ID
    private int findStudentInTable(int studentId) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (Integer.parseInt(tableModel.getValueAt(i, 0).toString()) == studentId) {
                return i;
            }
        }
        return -1;
    }

    // ✅ FIX: Simplified method using direct updatedModel reference
    private void addToUpdateHistory(String rollNo, String name, String mobile, 
            String email, boolean consent, String status) {
        if (updatedModel == null) return;  // ✅ Null check
        
        updatedModel.addRow(new Object[]{
            rollNo, name, 
            mobile.isEmpty() ? "-" : mobile,
            email.isEmpty() ? "-" : email,
            consent ? "✅ Yes" : "❌ No",
            status
        });
        
        // Keep only last 5 entries
        if (updatedModel.getRowCount() > 5) {
            updatedModel.removeRow(0);
        }
    }
}