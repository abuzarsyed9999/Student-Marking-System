package com.college.sms.dao;
//
import com.college.sms.util.DBConnection;
import java.sql.*;
//
public class RemedialDAO {

    // ✅ Add student to remedial tracking
    public boolean addToRemedial(int studentId, int examId, int subjectId, int classId, 
                                int facultyId, int failedMarks, int maxMarks, 
                                int passMarks, String facultyNotes) {
        String sql = "INSERT INTO remedial_tracking (student_id, failed_exam_id, subject_id, " +
                    "class_id, faculty_id, failed_marks, max_marks, pass_marks, " +
                    "failed_percentage, failed_date, faculty_notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE(), ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            double percentage = (maxMarks > 0) ? (failedMarks * 100.0 / maxMarks) : 0.0;
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, examId);
            pstmt.setInt(3, subjectId);
            pstmt.setInt(4, classId);
            pstmt.setInt(5, facultyId);
            pstmt.setInt(6, failedMarks);
            pstmt.setInt(7, maxMarks);
            pstmt.setInt(8, passMarks);
            pstmt.setDouble(9, percentage);
            pstmt.setString(10, facultyNotes);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding to remedial: " + e.getMessage());
            return false;
        }
    }

    // ✅ Check if student already has active remedial record
    public boolean hasActiveRemedial(int studentId, int subjectId) {
        String sql = "SELECT COUNT(*) FROM remedial_tracking " +
                    "WHERE student_id = ? AND subject_id = ? AND remedial_status = 'Active'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next() && rs.getInt(1) > 0;
            
        } catch (SQLException e) {
            System.err.println("Error checking active remedial: " + e.getMessage());
            return false;
        }
    }
    
//    --
 // ✅ Remove student from remedial tracking (with cascade delete)
//    public boolean removeFromRemedial(int remedialId) {
//        try (Connection conn = DBConnection.getConnection()) {
//            conn.setAutoCommit(false); // Start transaction
//            
//            try {
//                // Delete child records first (due to FK constraints)
//                // 1. Delete session attendance records
//                PreparedStatement ps1 = conn.prepareStatement(
//                    "DELETE sa FROM session_attendance sa " +
//                    "JOIN remedial_sessions rs ON sa.session_id = rs.session_id " +
//                    "WHERE rs.remedial_id = ?"
//                );
//                ps1.setInt(1, remedialId);
//                ps1.executeUpdate();
//                ps1.close();
//                
//                // 2. Delete remedial marks records
//                PreparedStatement ps2 = conn.prepareStatement(
//                    "DELETE rm FROM remedial_marks rm " +
//                    "JOIN remedial_sessions rs ON rm.session_id = rs.session_id " +
//                    "WHERE rs.remedial_id = ?"
//                );
//                ps2.setInt(1, remedialId);
//                ps2.executeUpdate();
//                ps2.close();
//                
//                // 3. Delete remedial sessions
//                PreparedStatement ps3 = conn.prepareStatement(
//                    "DELETE FROM remedial_sessions WHERE remedial_id = ?"
//                );
//                ps3.setInt(1, remedialId);
//                ps3.executeUpdate();
//                ps3.close();
//                
//                // 4. Finally, delete the remedial tracking record
//                PreparedStatement ps4 = conn.prepareStatement(
//                    "DELETE FROM remedial_tracking WHERE remedial_id = ?"
//                );
//                ps4.setInt(1, remedialId);
//                int affected = ps4.executeUpdate();
//                ps4.close();
//                
//                conn.commit(); // Commit transaction
//                return affected > 0;
//                
//            } catch (SQLException e) {
//                conn.rollback(); // Rollback on error
//                throw e;
//            } finally {
//                conn.setAutoCommit(true); // Reset auto-commit
//            }
//            
//        } catch (SQLException e) {
//            System.err.println("Error removing from remedial: " + e.getMessage());
//            return false;
//        }
//    }
    
 // ✅ Remove student from remedial tracking
    public boolean removeFromRemedial(int remedialId) {
        String sql = "DELETE FROM remedial_tracking WHERE remedial_id = ?";
        try (Connection conn = com.college.sms.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, remedialId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing from remedial: " + e.getMessage());
            return false;
        }
    }
//    --
    
 // ✅ Remove student from remedial tracking (with cascade delete)
//    public boolean removeFromRemedial(int remedialId) {
//        // If your schema has ON DELETE CASCADE on foreign keys, use this simplified version:
//        String sql = "DELETE FROM remedial_tracking WHERE remedial_id = ?";
//        try (Connection conn = com.college.sms.util.DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, remedialId);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error removing from remedial: " + e.getMessage());
//            return false;
//        }
//    }
//    --
}

