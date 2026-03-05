package com.college.sms.test;

import com.college.sms.dao.MarksDAO;

public class MarksTest {

    public static void main(String[] args) {

        MarksDAO dao = new MarksDAO();

        // Use existing IDs from DB
        int studentId = 1;
        int subjectId = 1;
        int examId = 1;

        dao.addMarks(studentId, subjectId, examId, 18);
        dao.viewMarksByStudent(studentId);
    }
}
