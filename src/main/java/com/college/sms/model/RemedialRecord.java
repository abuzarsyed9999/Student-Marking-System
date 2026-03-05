package com.college.sms.model;

public class RemedialRecord {
    private int remedialId;
    private int studentId;
    private String studentName;
    private String rollNo;
    private int failedExamId;
    private String failedExamName;
    private int subjectId;
    private String subjectName;
    private int classId;
    private String className;
    private int failedMarks;
    private int maxMarks;
    private int passMarks;
    private double failedPercentage;
    private String remedialStatus; // Active, Improved, No_Improvement, Completed
    private Integer nextExamMarks;
    private Double improvementPercentage;
    private String facultyNotes;

    // Constructors
    public RemedialRecord() {}

    // Getters & Setters
    public int getRemedialId() { return remedialId; }
    public void setRemedialId(int remedialId) { this.remedialId = remedialId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public int getFailedExamId() { return failedExamId; }
    public void setFailedExamId(int failedExamId) { this.failedExamId = failedExamId; }
    public String getFailedExamName() { return failedExamName; }
    public void setFailedExamName(String failedExamName) { this.failedExamName = failedExamName; }
    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public int getFailedMarks() { return failedMarks; }
    public void setFailedMarks(int failedMarks) { this.failedMarks = failedMarks; }
    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }
    public int getPassMarks() { return passMarks; }
    public void setPassMarks(int passMarks) { this.passMarks = passMarks; }
    public double getFailedPercentage() { return failedPercentage; }
    public void setFailedPercentage(double failedPercentage) { this.failedPercentage = failedPercentage; }
    public String getRemedialStatus() { return remedialStatus; }
    public void setRemedialStatus(String remedialStatus) { this.remedialStatus = remedialStatus; }
    public Integer getNextExamMarks() { return nextExamMarks; }
    public void setNextExamMarks(Integer nextExamMarks) { this.nextExamMarks = nextExamMarks; }
    public Double getImprovementPercentage() { return improvementPercentage; }
    public void setImprovementPercentage(Double improvementPercentage) { this.improvementPercentage = improvementPercentage; }
    public String getFacultyNotes() { return facultyNotes; }
    public void setFacultyNotes(String facultyNotes) { this.facultyNotes = facultyNotes; }
}