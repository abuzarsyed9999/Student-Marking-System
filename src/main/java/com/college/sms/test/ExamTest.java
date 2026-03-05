package com.college.sms.test;

import com.college.sms.dao.ExamDAO;

public class ExamTest {

    public static void main(String[] args) {

        ExamDAO dao = new ExamDAO();

        dao.addExam("MID-1", 30, 12);
        dao.addExam("MID-2", 30, 12);
        dao.addExam("MID-3", 40, 16);

        dao.viewExams();
    }
}
