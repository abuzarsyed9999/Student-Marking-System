//package com.college.sms.dao;
//
//import com.college.sms.model.StudentResult;
//import com.college.sms.util.DBConnection;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MarksDAO {
//
//    // Add marks
//    public boolean addMarks(int studentId, int subjectId, int examId, int marks) {
//        String sql = "INSERT INTO marks (student_id, subject_id, exam_id, marks_obtained) VALUES (?, ?, ?, ?)";
//        try (Connection con = DBConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//
//            ps.setInt(1, studentId);
//            ps.setInt(2, subjectId);
//            ps.setInt(3, examId);
//            ps.setInt(4, marks);
//
//            return ps.executeUpdate() > 0;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // Update marks
//    public boolean updateMarks(int studentId, int subjectId, int examId, int marks) {
//        String sql = "UPDATE marks SET marks_obtained = ? WHERE student_id = ? AND subject_id = ? AND exam_id = ?";
//        try (Connection con = DBConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//
//            ps.setInt(1, marks);
//            ps.setInt(2, studentId);
//            ps.setInt(3, subjectId);
//            ps.setInt(4, examId);
//
//            return ps.executeUpdate() > 0;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // Delete marks
//    public boolean deleteMarks(int studentId, int subjectId, int examId) {
//        String sql = "DELETE FROM marks WHERE student_id = ? AND subject_id = ? AND exam_id = ?";
//        try (Connection con = DBConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//
//            ps.setInt(1, studentId);
//            ps.setInt(2, subjectId);
//            ps.setInt(3, examId);
//
//            return ps.executeUpdate() > 0;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // Get marks for a class (for table)
//    public List<String[]> getMarksForTableByClass(int classId) {
//        List<String[]> list = new ArrayList<>();
//        String sql = "SELECT s.roll_no, s.name, sub.subject_name, e.exam_name, m.marks_obtained, " +
//                     "CASE WHEN m.marks_obtained >= 40 THEN 'Pass' ELSE 'Fail' END AS result, " +
//                     "s.student_id, sub.subject_id, e.exam_id " +
//                     "FROM marks m " +
//                     "JOIN student s ON m.student_id = s.student_id " +
//                     "JOIN subject sub ON m.subject_id = sub.subject_id " +
//                     "JOIN exam e ON m.exam_id = e.exam_id " +
//                     "WHERE s.class_id = ?";
//
//        try (Connection con = DBConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//            ps.setInt(1, classId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                list.add(new String[]{
//                        rs.getString("roll_no"),
//                        rs.getString("name"),
//                        rs.getString("subject_name"),
//                        rs.getString("exam_name"),
//                        rs.getString("marks_obtained"),
//                        rs.getString("result"),
//                        rs.getString("student_id"),
//                        rs.getString("subject_id"),
//                        rs.getString("exam_id")
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    public List<String> getSubjectsForStudent(int studentId) {
//        List<String> subjects = new ArrayList<>();
//        String sql = "SELECT subject_id FROM marks WHERE student_id = ?";
//
//        try (Connection con = DBConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//
//            ps.setInt(1, studentId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                subjects.add(String.valueOf(rs.getInt("subject_id")));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return subjects;
//    }
//
//public boolean exists(int studentId, int subjectId, int examId) {
//    String sql = "SELECT COUNT(*) FROM marks WHERE student_id=? AND subject_id=? AND exam_id=?";
//    
//    // Use your existing DAO connection
//    try (Connection conn = DBConnection.getConnection(); // or however you get the connection
//         PreparedStatement ps = conn.prepareStatement(sql)) {
//
//        ps.setInt(1, studentId);
//        ps.setInt(2, subjectId);
//        ps.setInt(3, examId);
//
//        try (ResultSet rs = ps.executeQuery()) {
//            if (rs.next()) {
//                return rs.getInt(1) > 0;
//            }
//        }
//
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }
//
//    return false;
//}
//
//  
//public List<StudentResult> getResultsForClassExam(int classId, int examId) {
//    List<StudentResult> list = new ArrayList<>();
//    String sql = "SELECT s.roll_no, s.name, AVG(m.marks_obtained) as avg_marks, " +
//                 "CASE WHEN MIN(m.marks_obtained) >= 35 THEN 1 ELSE 0 END as passed " +
//                 "FROM marks m " +
//                 "JOIN student s ON m.student_id = s.student_id " +  // <-- changed students -> student
//                 "WHERE s.class_id=? AND m.exam_id=? " +
//                 "GROUP BY s.student_id, s.roll_no, s.name";
//
//    try (Connection conn = DBConnection.getConnection();
//         PreparedStatement ps = conn.prepareStatement(sql)) {
//
//        ps.setInt(1, classId);
//        ps.setInt(2, examId);
//
//        try (ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                StudentResult sr = new StudentResult();
//                sr.setRollNo(rs.getString("roll_no"));
//                sr.setStudentName(rs.getString("name"));
//                sr.setAverageMarks(rs.getDouble("avg_marks"));
//                sr.setPassed(rs.getInt("passed") == 1);
//                list.add(sr);
//            }
//        }
//
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }
//
//    return list;
//}
//
//public List<String[]> getMarksForStudent(int studentId) {
//    List<String[]> results = new ArrayList<>();
//    String sql = "SELECT s.subject_name, e.exam_name, m.marks_obtained, " +
//                 "CASE WHEN m.marks_obtained >= e.pass_marks THEN 'Pass' ELSE 'Fail' END AS result " +
//                 "FROM marks m " +
//                 "JOIN subject s ON m.subject_id = s.subject_id " +
//                 "JOIN exam e ON m.exam_id = e.exam_id " +
//                 "WHERE m.student_id = ?";
//
//    try (Connection conn = DBConnection.getConnection();
//         PreparedStatement ps = conn.prepareStatement(sql)) {
//
//        ps.setInt(1, studentId);
//        ResultSet rs = ps.executeQuery();
//
//        while (rs.next()) {
//            String subject = rs.getString("subject_name");
//            String exam = rs.getString("exam_name");
//            String marks = rs.getString("marks_obtained");
//            String result = rs.getString("result");
//            results.add(new String[]{subject, exam, marks, result});
//        }
//
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }
//
//    return results;
//}

//}

//----

package com.college.sms.dao;

import com.college.sms.model.StudentResult;
import com.college.sms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksDAO {

    // ✅ ADD MARKS (with exam_id for proper linking)
    public boolean addMarks(int studentId, int subjectId, int examId, int marks) {
        // First check if record exists (prevent duplicates)
        if (exists(studentId, subjectId, examId)) {
            // Update existing record instead of inserting duplicate
            return updateMarks(studentId, subjectId, examId, marks);
        }
        
        String sql = "INSERT INTO marks (student_id, subject_id, exam_id, marks_obtained) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setInt(3, examId);
            ps.setInt(4, marks);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ UPDATE MARKS
    public boolean updateMarks(int studentId, int subjectId, int examId, int marks) {
        String sql = "UPDATE marks SET marks_obtained = ? WHERE student_id = ? AND subject_id = ? AND exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, marks);
            ps.setInt(2, studentId);
            ps.setInt(3, subjectId);
            ps.setInt(4, examId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ DELETE MARKS
    public boolean deleteMarks(int studentId, int subjectId, int examId) {
        String sql = "DELETE FROM marks WHERE student_id = ? AND subject_id = ? AND exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setInt(3, examId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
//    --
 // ADD TO com.college.sms.dao.MarksDAO IF MISSING
    public boolean deleteMarks(int studentId, int examId) throws SQLException {
        String sql = "DELETE FROM marks WHERE student_id = ? AND exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            return ps.executeUpdate() > 0;
        }
    }
//    --
    

    // ✅ CHECK IF MARKS EXIST (prevent duplicates)
    public boolean exists(int studentId, int subjectId, int examId) {
        String sql = "SELECT COUNT(*) FROM marks WHERE student_id=? AND subject_id=? AND exam_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setInt(3, examId);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ GET MARKS FOR CLASS (faculty-isolated via JOIN)
    public List<String[]> getMarksForTableByClass(int classId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT s.roll_no, s.name, sub.subject_name, e.exam_name, m.marks_obtained, " +
                     "CASE WHEN m.marks_obtained >= e.pass_marks THEN 'Pass' ELSE 'Fail' END AS result, " +
                     "s.student_id, sub.subject_id, e.exam_id " +
                     "FROM marks m " +
                     "JOIN student s ON m.student_id = s.student_id " +
                     "JOIN subject sub ON m.subject_id = sub.subject_id " +
                     "JOIN exam e ON m.exam_id = e.exam_id " +
                     "WHERE s.class_id = ? " +
                     "ORDER BY s.name, sub.subject_name, e.exam_name";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("roll_no"),
                    rs.getString("name"),
                    rs.getString("subject_name"),
                    rs.getString("exam_name"),
                    rs.getString("marks_obtained"),
                    rs.getString("result"),
                    rs.getString("student_id"),
                    rs.getString("subject_id"),
                    rs.getString("exam_id")
                });
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ GET RESULTS FOR CLASS+EXAM (for ResultSummaryUI)
    public List<StudentResult> getResultsForClassExam(int classId, int examId) {
        List<StudentResult> list = new ArrayList<>();
        String sql = "SELECT s.roll_no, s.name, m.marks_obtained as avg_marks, " +
                     "CASE WHEN m.marks_obtained >= e.pass_marks THEN 1 ELSE 0 END as passed " +
                     "FROM marks m " +
                     "JOIN student s ON m.student_id = s.student_id " +
                     "JOIN exam e ON m.exam_id = e.exam_id " +
                     "WHERE s.class_id = ? AND m.exam_id = ? " +
                     "ORDER BY s.name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, classId);
            ps.setInt(2, examId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentResult sr = new StudentResult();
                    sr.setRollNo(rs.getString("roll_no"));
                    sr.setStudentName(rs.getString("name"));
                    sr.setAverageMarks(rs.getDouble("avg_marks"));
                    sr.setPassed(rs.getInt("passed") == 1);
                    list.add(sr);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ GET MARKS FOR STUDENT (for student view)
    public List<String[]> getMarksForStudent(int studentId) {
        List<String[]> results = new ArrayList<>();
        String sql = "SELECT s.subject_name, e.exam_name, m.marks_obtained, " +
                     "CASE WHEN m.marks_obtained >= e.pass_marks THEN 'Pass' ELSE 'Fail' END AS result " +
                     "FROM marks m " +
                     "JOIN subject s ON m.subject_id = s.subject_id " +
                     "JOIN exam e ON m.exam_id = e.exam_id " +
                     "WHERE m.student_id = ? " +
                     "ORDER BY s.subject_name, e.exam_name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                results.add(new String[]{
                    rs.getString("subject_name"),
                    rs.getString("exam_name"),
                    rs.getString("marks_obtained"),
                    rs.getString("result")
                });
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    
//    --
 // ADD THIS METHOD TO com.college.sms.dao.MarksDAO
    public List<SubjectPerformanceUI.StudentPerformance> getSubjectPerformance(
            int classId, int subjectId, int requiredExamCount) throws SQLException {
        
        List<SubjectPerformanceUI.StudentPerformance> performances = new ArrayList<>();
        
        // Step 1: Get all exams for this subject+class
        String examSql = "SELECT exam_id, exam_name, max_marks FROM exam WHERE class_id = ? AND subject_id = ?";
        Map<Integer, Exam> exams = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(examSql)) {
            ps.setInt(1, classId);
            ps.setInt(2, subjectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Exam exam = new Exam(
                    rs.getInt("exam_id"),
                    rs.getString("exam_name"),
                    rs.getInt("max_marks"),
                    0, // pass_marks not needed here
                    classId,
                    subjectId,
                    0  // faculty_id not needed here
                );
                exams.put(exam.getExamId(), exam);
            }
        }
        
        if (exams.isEmpty()) return performances;
        
        // Step 2: Get students with marks in ALL required exams
        String sql = "SELECT " +
                     "s.student_id, s.roll_no, s.name, " +
                     "m.exam_id, m.marks_obtained, " +
                     "e.max_marks " +
                     "FROM student s " +
                     "JOIN marks m ON s.student_id = m.student_id " +
                     "JOIN exam e ON m.exam_id = e.exam_id " +
                     "WHERE s.class_id = ? AND e.subject_id = ? " +
                     "ORDER BY s.name, e.exam_name";
        
        Map<Integer, SubjectPerformanceUI.StudentPerformance> studentMap = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.setInt(2, subjectId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                String rollNo = rs.getString("roll_no");
                String name = rs.getString("name");
                int examId = rs.getInt("exam_id");
                int marks = rs.getInt("marks_obtained");
                int maxMarks = rs.getInt("max_marks");
                
                SubjectPerformanceUI.StudentPerformance sp = studentMap.get(studentId);
                if (sp == null) {
                    sp = new SubjectPerformanceUI.StudentPerformance(studentId, rollNo, name);
                    studentMap.put(studentId, sp);
                }
                sp.addExamMark(examId, marks, maxMarks);
            }
        }
        
        // Filter students who have marks for ALL required exams
        for (SubjectPerformanceUI.StudentPerformance sp : studentMap.values()) {
            if (sp.getExamMarks().size() == requiredExamCount) {
                sp.calculateTotals();
                performances.add(sp);
            }
        }
        
        // Sort by percentage descending
        performances.sort((a, b) -> Double.compare(b.getPercentage(), a.getPercentage()));
        
        return performances;
    }
//    --
    
}