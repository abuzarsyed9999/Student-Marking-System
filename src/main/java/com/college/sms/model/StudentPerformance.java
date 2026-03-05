package com.college.sms.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing a student's cumulative performance across multiple exams for a subject.
 * Used by SubjectPerformanceUI to calculate:
 * - Total marks obtained across all exams
 * - Total maximum marks
 * - Overall percentage
 * - Average marks per exam
 */
public class StudentPerformance {
    private int studentId;
    private String rollNo;
    private String studentName;
    private Map<Integer, Integer> examMarks;      // examId -> marks obtained
    private Map<Integer, Integer> examMaxMarks;   // examId -> max marks for exam
    private int totalObtained;
    private int totalMax;
    private double percentage;
    private double avgMarks;

    /**
     * Constructor to initialize student details.
     * 
     * @param studentId   Student's unique ID from database
     * @param rollNo      Student's roll number (e.g., "24691A0567")
     * @param studentName Student's full name
     */
    public StudentPerformance(int studentId, String rollNo, String studentName) {
        this.studentId = studentId;
        this.rollNo = rollNo;
        this.studentName = studentName;
        this.examMarks = new HashMap<>();
        this.examMaxMarks = new HashMap<>();
        this.totalObtained = 0;
        this.totalMax = 0;
        this.percentage = 0.0;
        this.avgMarks = 0.0;
    }

    /**
     * Add marks for a specific exam.
     * 
     * @param examId    Exam ID (from exam table)
     * @param marks     Marks obtained by student in this exam
     * @param maxMarks  Maximum marks for this exam
     */
    public void addExamMark(int examId, int marks, int maxMarks) {
        examMarks.put(examId, marks);
        examMaxMarks.put(examId, maxMarks);
    }

    /**
     * Calculate total marks, percentage, and average marks.
     * MUST be called after adding all exam marks.
     */
    public void calculateTotals() {
        // Sum all obtained marks
        totalObtained = examMarks.values().stream().mapToInt(Integer::intValue).sum();
        
        // Sum all maximum marks
        totalMax = examMaxMarks.values().stream().mapToInt(Integer::intValue).sum();
        
        // Calculate percentage: (totalObtained / totalMax) * 100
        percentage = (totalMax > 0) ? (totalObtained * 100.0) / totalMax : 0.0;
        
        // Calculate average marks per exam
        avgMarks = (examMarks.size() > 0) ? (totalObtained * 1.0 / examMarks.size()) : 0.0;
    }

    // ======================
    // GETTERS (NO SETTERS NEEDED - IMMUTABLE AFTER CALCULATION)
    // ======================

    public int getStudentId() { return studentId; }
    public String getRollNo() { return rollNo; }
    public String getStudentName() { return studentName; }
    public Map<Integer, Integer> getExamMarks() { return examMarks; }
    public int getTotalObtained() { return totalObtained; }
    public int getTotalMax() { return totalMax; }
    public double getPercentage() { return percentage; }
    public double getAvgMarks() { return avgMarks; }

    // ======================
    // OPTIONAL: For debugging
    // ======================
    @Override
    public String toString() {
        return "StudentPerformance{" +
                "rollNo='" + rollNo + '\'' +
                ", name='" + studentName + '\'' +
                ", total=" + totalObtained + "/" + totalMax +
                ", %= " + String.format("%.1f", percentage) +
                ", avg=" + String.format("%.2f", avgMarks) +
                '}';
    }
}