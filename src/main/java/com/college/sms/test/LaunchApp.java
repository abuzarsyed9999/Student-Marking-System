package com.college.sms.test;

import com.college.sms.ui.FacultyLoginUI;

import javax.swing.SwingUtilities;

public class LaunchApp {
    public static void main(String[] args) {
        // Make sure GUI runs on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new FacultyLoginUI().setVisible(true);
        });
    }
}








