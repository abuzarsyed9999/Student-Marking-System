package com.college.sms.test;

import com.college.sms.dao.FacultyDAO;

public class FacultyLoginTest {

    public static void main(String[] args) {

        FacultyDAO dao = new FacultyDAO();

        int facultyId = dao.loginFaculty("abuzar@gmail.com", "1234");

        if (facultyId != -1) {
            System.out.println("Login successful. Faculty ID = " + facultyId);
        } else {
            System.out.println("Invalid email or password");
        }
    }
}
