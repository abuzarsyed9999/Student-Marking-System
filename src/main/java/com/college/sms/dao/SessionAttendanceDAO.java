package com.college.sms.dao;

import com.college.sms.model.SessionAttendance;
import com.college.sms.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionAttendanceDAO {

    // ✅ Mark attendance for a student
    public boolean markAttendance(SessionAttendance attendance) {
        String sql = "INSERT INTO session_attendance (session_id, student_id, attendance_status, " +
                    "check_in_time, check_out_time, performance_rating, faculty_feedback) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "attendance_status = VALUES(attendance_status), " +
                    "check_in_time = VALUES(check_in_time), " +
                    "check_out_time = VALUES(check_out_time), " +
                    "performance_rating = VALUES(performance_rating), " +
                    "faculty_feedback = VALUES(faculty_feedback), " +
                    "marked_at = CURRENT_TIMESTAMP";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attendance.getSessionId());
            pstmt.setInt(2, attendance.getStudentId());
            pstmt.setString(3, attendance.getAttendanceStatus());
            pstmt.setTime(4, attendance.getCheckInTime());
            pstmt.setTime(5, attendance.getCheckOutTime());
            pstmt.setString(6, attendance.getPerformanceRating());
            pstmt.setString(7, attendance.getFacultyFeedback());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error marking attendance: " + e.getMessage());
            return false;
        }
    }

    // ✅ Get attendance for a session
    public List<SessionAttendance> getSessionAttendance(int sessionId) {
        List<SessionAttendance> attendanceList = new ArrayList<>();
        String sql = "SELECT sa.*, s.name as student_name, s.roll_no " +
                    "FROM session_attendance sa " +
                    "JOIN student s ON sa.student_id = s.student_id " +
                    "WHERE sa.session_id = ? " +
                    "ORDER BY s.roll_no";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sessionId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                SessionAttendance attendance = new SessionAttendance();
                attendance.setAttendanceId(rs.getInt("attendance_id"));
                attendance.setSessionId(rs.getInt("session_id"));
                attendance.setStudentId(rs.getInt("student_id"));
                attendance.setStudentName(rs.getString("student_name"));
                attendance.setRollNo(rs.getString("roll_no"));
                attendance.setAttendanceStatus(rs.getString("attendance_status"));
                attendance.setCheckInTime(rs.getTime("check_in_time"));
                attendance.setCheckOutTime(rs.getTime("check_out_time"));
                attendance.setPerformanceRating(rs.getString("performance_rating"));
                attendance.setFacultyFeedback(rs.getString("faculty_feedback"));
                attendance.setMarkedAt(rs.getTimestamp("marked_at"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching attendance: " + e.getMessage());
        }
        return attendanceList;
    }

    // ✅ Get attendance history for a student
    public List<SessionAttendance> getStudentAttendanceHistory(int studentId) {
        List<SessionAttendance> attendanceList = new ArrayList<>();
        String sql = "SELECT sa.*, rs.session_date, rs.topic_covered " +
                    "FROM session_attendance sa " +
                    "JOIN remedial_sessions rs ON sa.session_id = rs.session_id " +
                    "WHERE sa.student_id = ? " +
                    "ORDER BY rs.session_date DESC, rs.session_time DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                SessionAttendance attendance = new SessionAttendance();
                attendance.setAttendanceId(rs.getInt("attendance_id"));
                attendance.setSessionId(rs.getInt("session_id"));
                attendance.setStudentId(rs.getInt("student_id"));
                attendance.setAttendanceStatus(rs.getString("attendance_status"));
                attendance.setPerformanceRating(rs.getString("performance_rating"));
                attendance.setFacultyFeedback(rs.getString("faculty_feedback"));
                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student attendance: " + e.getMessage());
        }
        return attendanceList;
    }

    // ✅ Get attendance statistics for a session
    public AttendanceStats getSessionStats(int sessionId) {
        AttendanceStats stats = new AttendanceStats();
        String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "SUM(CASE WHEN attendance_status = 'Present' THEN 1 ELSE 0 END) as present, " +
                    "SUM(CASE WHEN attendance_status = 'Absent' THEN 1 ELSE 0 END) as absent, " +
                    "SUM(CASE WHEN attendance_status = 'Late' THEN 1 ELSE 0 END) as late " +
                    "FROM session_attendance WHERE session_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sessionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                stats.setTotal(rs.getInt("total"));
                stats.setPresent(rs.getInt("present"));
                stats.setAbsent(rs.getInt("absent"));
                stats.setLate(rs.getInt("late"));
                stats.setAttendancePercentage(
                    stats.getTotal() > 0 ? (stats.getPresent() * 100.0 / stats.getTotal()) : 0
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching session stats: " + e.getMessage());
        }
        return stats;
    }

    // ✅ Bulk mark attendance (for all students in session)
    public int bulkMarkAttendance(int sessionId, List<SessionAttendance> attendanceList) {
        int count = 0;
        for (SessionAttendance attendance : attendanceList) {
            attendance.setSessionId(sessionId);
            if (markAttendance(attendance)) {
                count++;
            }
        }
        return count;
    }
}

// Helper class for stats
class AttendanceStats {
    private int total;
    private int present;
    private int absent;
    private int late;
    private double attendancePercentage;

    // Getters & Setters
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getPresent() { return present; }
    public void setPresent(int present) { this.present = present; }
    public int getAbsent() { return absent; }
    public void setAbsent(int absent) { this.absent = absent; }
    public int getLate() { return late; }
    public void setLate(int late) { this.late = late; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
}