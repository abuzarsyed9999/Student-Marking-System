package com.college.sms.model;

public class Subject {

    private int subjectId;
    private String subjectName;
    private int classId;
    private int facultyId; // <-- add this

    // ----- Constructors -----
    
    public Subject(int subjectId, String subjectName, int classId) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.classId = classId;
    }

    public Subject() {}

    public Subject(int subjectId, String subjectName, int classId, int facultyId) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.classId = classId;
        this.facultyId = facultyId;
    }

    // ----- Getters & Setters -----
    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }
}
