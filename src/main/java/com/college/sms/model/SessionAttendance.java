package com.college.sms.model;

import java.sql.Time;
import java.sql.Timestamp;

public class SessionAttendance {
    private int attendanceId;
    private int sessionId;
    private int studentId;
    private String studentName;
    private String rollNo;
    private String attendanceStatus; // Present, Absent, Late
    private Time checkInTime;
    private Time checkOutTime;
    private String performanceRating; // Excellent, Good, Average, Poor
    private String facultyFeedback;
    private Timestamp markedAt;

    // Constructors
    public SessionAttendance() {}

    // Getters & Setters
    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }
    public Time getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Time checkInTime) { this.checkInTime = checkInTime; }
    public Time getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(Time checkOutTime) { this.checkOutTime = checkOutTime; }
    public String getPerformanceRating() { return performanceRating; }
    public void setPerformanceRating(String performanceRating) { this.performanceRating = performanceRating; }
    public String getFacultyFeedback() { return facultyFeedback; }
    public void setFacultyFeedback(String facultyFeedback) { this.facultyFeedback = facultyFeedback; }
    public Timestamp getMarkedAt() { return markedAt; }
    public void setMarkedAt(Timestamp markedAt) { this.markedAt = markedAt; }

    // ✅ Helper Methods
    public String getStatusIcon() {
        switch (attendanceStatus) {
            case "Present": return "✅";
            case "Absent": return "❌";
            case "Late": return "⚠️";
            default: return "⏳";
        }
    }

    public String getRatingColor() {
        switch (performanceRating) {
            case "Excellent": return "🟢";
            case "Good": return "🔵";
            case "Average": return "🟡";
            case "Poor": return "🔴";
            default: return "⚪";
        }
    }
}