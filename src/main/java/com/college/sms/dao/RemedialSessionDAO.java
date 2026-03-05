package com.college.sms.dao;

import com.college.sms.model.RemedialSession;
import com.college.sms.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RemedialSessionDAO {

    // ✅ Create new remedial session
    public int createSession(RemedialSession session) {
        String sql = "INSERT INTO remedial_sessions (remedial_id, session_date, session_time, " +
                    "duration_minutes, topic_covered, location, faculty_id, notes, session_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, session.getRemedialId());
            pstmt.setDate(2, session.getSessionDate());
            pstmt.setTime(3, session.getSessionTime());
            pstmt.setInt(4, session.getDurationMinutes());
            pstmt.setString(5, session.getTopicCovered());
            pstmt.setString(6, session.getLocation());
            pstmt.setInt(7, session.getFacultyId());
            pstmt.setString(8, session.getNotes());
            pstmt.setString(9, session.getSessionStatus());
            
            int affected = pstmt.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Return generated session_id
                }
            }
            return -1;
            
        } catch (SQLException e) {
            System.err.println("Error creating session: " + e.getMessage());
            return -1;
        }
    }

    // ✅ UPDATE SESSION - THE MISSING METHOD ✅
    public boolean updateSession(RemedialSession session) {
        String sql = "UPDATE remedial_sessions SET " +
                    "session_date = ?, session_time = ?, duration_minutes = ?, " +
                    "topic_covered = ?, location = ?, session_status = ?, " +
                    "notes = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE session_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, session.getSessionDate());
            pstmt.setTime(2, session.getSessionTime());
            pstmt.setInt(3, session.getDurationMinutes());
            pstmt.setString(4, session.getTopicCovered());
            pstmt.setString(5, session.getLocation());
            pstmt.setString(6, session.getSessionStatus());
            pstmt.setString(7, session.getNotes());
            pstmt.setInt(8, session.getSessionId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating session: " + e.getMessage());
            return false;
        }
    }

    // ✅ Get all sessions for a faculty
    public List<RemedialSession> getSessionsByFaculty(int facultyId) {
        List<RemedialSession> sessions = new ArrayList<>();
        String sql = "SELECT rs.*, " +
                    "(SELECT COUNT(*) FROM session_attendance sa WHERE sa.session_id = rs.session_id) as total_students, " +
                    "(SELECT COUNT(*) FROM session_attendance sa WHERE sa.session_id = rs.session_id AND sa.attendance_status = 'Present') as present_count " +
                    "FROM remedial_sessions rs " +
                    "WHERE rs.faculty_id = ? " +
                    "ORDER BY rs.session_date DESC, rs.session_time DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RemedialSession session = new RemedialSession();
                session.setSessionId(rs.getInt("session_id"));
                session.setRemedialId(rs.getInt("remedial_id"));
                session.setSessionDate(rs.getDate("session_date"));
                session.setSessionTime(rs.getTime("session_time"));
                session.setDurationMinutes(rs.getInt("duration_minutes"));
                session.setTopicCovered(rs.getString("topic_covered"));
                session.setLocation(rs.getString("location"));
                session.setFacultyId(rs.getInt("faculty_id"));
                session.setSessionStatus(rs.getString("session_status"));
                session.setNotes(rs.getString("notes"));
                session.setTotalStudents(rs.getInt("total_students"));
                session.setPresentCount(rs.getInt("present_count"));
                
                if (session.getTotalStudents() > 0) {
                    session.setAttendancePercentage(
                        (session.getPresentCount() * 100.0) / session.getTotalStudents()
                    );
                }
                
                sessions.add(session);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sessions: " + e.getMessage());
        }
        return sessions;
    }

    // ✅ Get sessions for a specific remedial record
    public List<RemedialSession> getSessionsByRemedial(int remedialId) {
        List<RemedialSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM remedial_sessions WHERE remedial_id = ? ORDER BY session_date DESC, session_time DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, remedialId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RemedialSession session = new RemedialSession();
                session.setSessionId(rs.getInt("session_id"));
                session.setRemedialId(rs.getInt("remedial_id"));
                session.setSessionDate(rs.getDate("session_date"));
                session.setSessionTime(rs.getTime("session_time"));
                session.setDurationMinutes(rs.getInt("duration_minutes"));
                session.setTopicCovered(rs.getString("topic_covered"));
                session.setLocation(rs.getString("location"));
                session.setSessionStatus(rs.getString("session_status"));
                session.setNotes(rs.getString("notes"));
                sessions.add(session);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sessions: " + e.getMessage());
        }
        return sessions;
    }

    // ✅ Get single session by ID
    public RemedialSession getSessionById(int sessionId) {
        String sql = "SELECT * FROM remedial_sessions WHERE session_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sessionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                RemedialSession session = new RemedialSession();
                session.setSessionId(rs.getInt("session_id"));
                session.setRemedialId(rs.getInt("remedial_id"));
                session.setSessionDate(rs.getDate("session_date"));
                session.setSessionTime(rs.getTime("session_time"));
                session.setDurationMinutes(rs.getInt("duration_minutes"));
                session.setTopicCovered(rs.getString("topic_covered"));
                session.setLocation(rs.getString("location"));
                session.setFacultyId(rs.getInt("faculty_id"));
                session.setSessionStatus(rs.getString("session_status"));
                session.setNotes(rs.getString("notes"));
                return session;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching session: " + e.getMessage());
        }
        return null;
    }

    // ✅ Delete session
    public boolean deleteSession(int sessionId) {
        String sql = "DELETE FROM remedial_sessions WHERE session_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sessionId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting session: " + e.getMessage());
            return false;
        }
    }

    // ✅ Update session status only
    public boolean updateSessionStatus(int sessionId, String status) {
        String sql = "UPDATE remedial_sessions SET session_status = ?, updated_at = CURRENT_TIMESTAMP WHERE session_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, sessionId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating session status: " + e.getMessage());
            return false;
        }
    }

    // ✅ Get upcoming sessions for a faculty
    public List<RemedialSession> getUpcomingSessions(int facultyId) {
        List<RemedialSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM remedial_sessions " +
                    "WHERE faculty_id = ? AND session_status = 'Scheduled' " +
                    "AND (session_date > CURDATE() OR (session_date = CURDATE() AND session_time > CURTIME())) " +
                    "ORDER BY session_date ASC, session_time ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RemedialSession session = new RemedialSession();
                session.setSessionId(rs.getInt("session_id"));
                session.setRemedialId(rs.getInt("remedial_id"));
                session.setSessionDate(rs.getDate("session_date"));
                session.setSessionTime(rs.getTime("session_time"));
                session.setTopicCovered(rs.getString("topic_covered"));
                session.setLocation(rs.getString("location"));
                session.setSessionStatus(rs.getString("session_status"));
                sessions.add(session);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching upcoming sessions: " + e.getMessage());
        }
        return sessions;
    }
}