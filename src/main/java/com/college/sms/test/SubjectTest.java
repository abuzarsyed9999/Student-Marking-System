package com.college.sms.test;

import com.college.sms.dao.SubjectDAO;

public class SubjectTest {

    public static void main(String[] args) {

        int classId = 1; // existing class

        SubjectDAO dao = new SubjectDAO();

        dao.addSubject("Mathematics", classId);
        dao.addSubject("Operating Systems", classId);

        dao.viewSubjectsByClass(classId);
    }
}
