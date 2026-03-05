package com.college.sms.test;

import com.college.sms.dao.ClassDAO;

public class ClassTest {

    public static void main(String[] args) {

        int facultyId = 1; // use logged-in faculty ID

        ClassDAO dao = new ClassDAO();

        dao.addClass("CSE-A", facultyId);
        dao.addClass("CSE-B", facultyId);

        dao.viewClasses(facultyId);
    }
}
