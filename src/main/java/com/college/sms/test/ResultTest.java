package com.college.sms.test;

import com.college.sms.dao.ResultDAO;

public class ResultTest {

    public static void main(String[] args) {

        ResultDAO dao = new ResultDAO();

        int subjectId = 1; // Mathematics
        int examId = 1;    // MID-1

        dao.viewPassFailBySubjectAndExam(subjectId, examId);
    }
}
