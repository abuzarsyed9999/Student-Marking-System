package com.college.sms.model;

public class Exam {
    private int examId;
    private String examName;
    private int maxMarks;
    private int passMarks;
    private int classId;
    private int subjectId;
    private int facultyId;

    // Constructor
    public Exam(int examId, String examName, int maxMarks, int passMarks, int classId, int subjectId, int facultyId) {
        this.examId = examId;
        this.examName = examName;
        this.maxMarks = maxMarks;
        this.passMarks = passMarks;
        this.classId = classId;
        this.subjectId = subjectId;
        this.facultyId = facultyId;
    }

    // Getters and Setters
    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public int getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(int maxMarks) {
        this.maxMarks = maxMarks;
    }

    public int getPassMarks() {
        return passMarks;
    }

    public void setPassMarks(int passMarks) {
        this.passMarks = passMarks;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }
}
