package com.college.sms.model;

public class StudentResult {
    private int studentId;
    private String rollNo;
    private String studentName;
    private double averageMarks;
    private boolean passed;

    // No-argument constructor
    public StudentResult() {
    }

    public StudentResult(int studentId, String rollNo, String studentName, double averageMarks, boolean passed) {
        this.studentId = studentId;
        this.rollNo = rollNo;
        this.studentName = studentName;
        this.averageMarks = averageMarks;
        this.passed = passed;
    }

    // Getters and setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public double getAverageMarks() { return averageMarks; }
    public void setAverageMarks(double averageMarks) { this.averageMarks = averageMarks; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
}
