package com.college.sms.test;

import com.college.sms.dao.StudentDAO;

public class StudentTest {

    public static void main(String[] args) {

        int classId = 1; // use an existing class ID

        StudentDAO dao = new StudentDAO();

        dao.addStudent("CSE001", "Ravi Kumar", classId);
        dao.addStudent("CSE002", "Anita Sharma", classId);

        dao.viewStudentsByClass(classId);
    }
}
