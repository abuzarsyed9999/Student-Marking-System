package com.college.sms.model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class RemedialSession {
    private int sessionId;
    private int remedialId;
    private Date sessionDate;
    private Time sessionTime;
    private int durationMinutes;
    private String topicCovered;
    private String location;
    private int facultyId;
    private String facultyName;
    private String sessionStatus; // Scheduled, Completed, Cancelled
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Calculated fields
    private int totalStudents;
    private int presentCount;
    private int absentCount;
    private double attendancePercentage;

    // Constructors
    public RemedialSession() {}

    // Getters & Setters
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public int getRemedialId() { return remedialId; }
    public void setRemedialId(int remedialId) { this.remedialId = remedialId; }
    public Date getSessionDate() { return sessionDate; }
    public void setSessionDate(Date sessionDate) { this.sessionDate = sessionDate; }
    public Time getSessionTime() { return sessionTime; }
    public void setSessionTime(Time sessionTime) { this.sessionTime = sessionTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getTopicCovered() { return topicCovered; }
    public void setTopicCovered(String topicCovered) { this.topicCovered = topicCovered; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }
    public String getSessionStatus() { return sessionStatus; }
    public void setSessionStatus(String sessionStatus) { this.sessionStatus = sessionStatus; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
    public int getPresentCount() { return presentCount; }
    public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
    public int getAbsentCount() { return absentCount; }
    public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    // ✅ Helper Methods
    public String getFormattedDateTime() {
        return sessionDate + " " + sessionTime;
    }

    public String getStatusColor() {
        switch (sessionStatus) {
            case "Completed": return "🟢";
            case "Scheduled": return "🔵";
            case "Cancelled": return "🔴";
            default: return "⚪";
        }
    }
}