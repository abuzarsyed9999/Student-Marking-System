package com.college.sms.test;

import com.college.sms.dao.AnalyticsDAO;

public class AnalyticsTest {

    public static void main(String[] args) {

        AnalyticsDAO dao = new AnalyticsDAO();

        int classId = 1;
        int subjectId = 1;
        int examId = 1;

        dao.classAnalytics(classId, subjectId, examId);
    }
}
