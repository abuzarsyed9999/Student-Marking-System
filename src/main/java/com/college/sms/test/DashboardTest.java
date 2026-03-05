package com.college.sms.test;

import com.college.sms.ui.FacultyDashboard;

public class DashboardTest {

    public static void main(String[] args) {

        int facultyId = 1; // simulate logged-in faculty
        new FacultyDashboard(facultyId).setVisible(true);
    }
}
