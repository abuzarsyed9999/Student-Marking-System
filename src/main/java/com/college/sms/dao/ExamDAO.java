////package com.college.sms.dao;
////
////import com.college.sms.model.StudentResult;
////import com.college.sms.util.DBConnection;
////
////import java.sql.Connection;
////import java.sql.PreparedStatement;
////import java.sql.ResultSet;
////import java.sql.SQLException;
////import java.util.ArrayList;
////import java.util.List;
////
////public class ExamDAO {
////
////    // ---------------- ADD EXAM ----------------
////    public boolean addExam(String examName, int maxMarks, int passMarks) {
////        String sql = "INSERT INTO exam (exam_name, max_marks, pass_marks) VALUES (?, ?, ?)";
////        try (Connection con = DBConnection.getConnection();
////             PreparedStatement ps = con.prepareStatement(sql)) {
////
////            ps.setString(1, examName);
////            ps.setInt(2, maxMarks);
////            ps.setInt(3, passMarks);
////
////            return ps.executeUpdate() > 0;
////
////        } catch (Exception e) {
////            System.out.println("Error adding exam: " + e.getMessage());
////            e.printStackTrace();
////        }
////        return false;
////    }
////
////    // ---------------- VIEW EXAMS (Console) ----------------
////    public void viewExams() {
////        String sql = "SELECT exam_id, exam_name, max_marks, pass_marks FROM exam";
////
////        try (Connection con = DBConnection.getConnection();
////             PreparedStatement ps = con.prepareStatement(sql);
////             ResultSet rs = ps.executeQuery()) {
////
////            System.out.println("Available Exams:");
////            while (rs.next()) {
////                System.out.println(
////                    rs.getInt("exam_id") + " - " +
////                    rs.getString("exam_name") +
////                    " (Max: " + rs.getInt("max_marks") +
////                    ", Pass: " + rs.getInt("pass_marks") + ")"
////                );
////            }
////
////        } catch (Exception e) {
////            System.out.println("Error fetching exams: " + e.getMessage());
////            e.printStackTrace();
////        }
////    }
////
////    // ---------------- GET ALL EXAMS (FOR UI DROPDOWN) ----------------
////    public List<String[]> getAllExams() {
////        List<String[]> list = new ArrayList<>();
////        String sql = "SELECT exam_id, exam_name FROM exam";
////
////        try (Connection con = DBConnection.getConnection();
////             PreparedStatement ps = con.prepareStatement(sql);
////             ResultSet rs = ps.executeQuery()) {
////
////            while (rs.next()) {
////                list.add(new String[]{
////                        String.valueOf(rs.getInt("exam_id")),
////                        rs.getString("exam_name")
////                });
////            }
////
////        } catch (Exception e) {
////            System.out.println("Error fetching exams for UI: " + e.getMessage());
////            e.printStackTrace();
////        }
////
////        return list;
////    }
//////    public List<StudentResult> getResultsForClassExam(int classId, int examId) {
//////        List<StudentResult> list = new ArrayList<>();
//////        String sql = "SELECT s.roll_no, s.name, AVG(m.marks) as avg_marks, " +
//////                     "CASE WHEN MIN(m.marks) >= 35 THEN 1 ELSE 0 END as passed " +
//////                     "FROM marks m " +
//////                     "JOIN students s ON m.student_id = s.student_id " +
//////                     "WHERE s.class_id=? AND m.exam_id=? " +
//////                     "GROUP BY s.student_id, s.roll_no, s.name";
//////        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//////            ps.setInt(1, classId);
//////            ps.setInt(2, examId);
//////            ResultSet rs = ps.executeQuery();
//////            while (rs.next()) {
//////                StudentResult sr = new StudentResult();
//////                sr.setRollNo(rs.getString("roll_no"));
//////                sr.setStudentName(rs.getString("name"));
//////                sr.setAverageMarks(rs.getDouble("avg_marks"));
//////                sr.setPassed(rs.getInt("passed") == 1);
//////                list.add(sr);
//////            }
//////        } catch (SQLException e) {
//////            e.printStackTrace();
//////        }
//////        return list;
//////    }
////
////
////public List<StudentResult> getResultsForClassExam(int classId, int examId) {
////    List<StudentResult> list = new ArrayList<>();
////    String sql = "SELECT s.roll_no, s.name, AVG(m.marks) as avg_marks, " +
////                 "CASE WHEN MIN(m.marks) >= 35 THEN 1 ELSE 0 END as passed " +
////                 "FROM marks m " +
////                 "JOIN students s ON m.student_id = s.student_id " +
////                 "WHERE s.class_id=? AND m.exam_id=? " +
////                 "GROUP BY s.student_id, s.roll_no, s.name";
////
////    // Use try-with-resources to auto-close connection, statement, and result set
////    try (Connection conn = DBConnection.getConnection();
////         PreparedStatement ps = conn.prepareStatement(sql)) {
////
////        ps.setInt(1, classId);
////        ps.setInt(2, examId);
////
////        try (ResultSet rs = ps.executeQuery()) {
////            while (rs.next()) {
////                StudentResult sr = new StudentResult();
////                sr.setRollNo(rs.getString("roll_no"));
////                sr.setStudentName(rs.getString("name"));
////                sr.setAverageMarks(rs.getDouble("avg_marks"));
////                sr.setPassed(rs.getInt("passed") == 1);
////                list.add(sr);
////            }
////        }
////
////    } catch (SQLException e) {
////        e.printStackTrace();
////    }
////
////    return list;
////}
//////	--
////public List<String[]> getExamsByFaculty(int facultyId) { 
////    List<String[]> exams = new ArrayList<>();
////    String sql = "SELECT exam_id, exam_name, max_marks, pass_marks FROM exam WHERE faculty_id=?";
////    try (Connection conn = DBConnection.getConnection();
////         PreparedStatement ps = conn.prepareStatement(sql)) {
////        ps.setInt(1, facultyId);
////        ResultSet rs = ps.executeQuery();
////        while (rs.next()) {
////            exams.add(new String[]{
////                String.valueOf(rs.getInt("exam_id")),
////                rs.getString("exam_name"),
////                String.valueOf(rs.getInt("max_marks")),
////                String.valueOf(rs.getInt("pass_marks"))
////            });
////        }
////    } catch (SQLException e) { e.printStackTrace(); }
////    return exams;
////}
////
////public void addExam(String name, int max, int pass, int facultyId) {
////    String sql = "INSERT INTO exam(exam_name, max_marks, pass_marks, faculty_id) VALUES(?,?,?,?)";
////    try (Connection conn = DBConnection.getConnection();
////         PreparedStatement ps = conn.prepareStatement(sql)) {
////        ps.setString(1, name);
////        ps.setInt(2, max);
////        ps.setInt(3, pass);
////        ps.setInt(4, facultyId);
////        ps.executeUpdate();
////    } catch (SQLException e) { e.printStackTrace(); }
////}
////
////public void updateExam(int id, String name, int max, int pass) {
////    String sql = "UPDATE exam SET exam_name=?, max_marks=?, pass_marks=? WHERE exam_id=?";
////    try (Connection conn = DBConnection.getConnection();
////         PreparedStatement ps = conn.prepareStatement(sql)) {
////        ps.setString(1, name);
////        ps.setInt(2, max);
////        ps.setInt(3, pass);
////        ps.setInt(4, id);
////        ps.executeUpdate();
////    } catch (SQLException e) { e.printStackTrace(); }
////}
////
////public void deleteExam(int id) {
////    String sql = "DELETE FROM exam WHERE exam_id=?";
////    try (Connection conn = DBConnection.getConnection();
////         PreparedStatement ps = conn.prepareStatement(sql)) {
////        ps.setInt(1, id);
////        ps.executeUpdate();
////    } catch (SQLException e) { e.printStackTrace(); }
////}
////
//////--
////}
//
//
//package com.college.sms.dao;
//
//import com.college.sms.model.Exam;
//import com.college.sms.model.StudentResult;
//import com.college.sms.util.DBConnection;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ExamDAO {
//
//    // ✅ ADD EXAM (FACULTY BASED)
// 
////	public void addExam(String name, int max, int pass, int facultyId, int classId) {
////	    String sql = "INSERT INTO exam (exam_name, max_marks, pass_marks, faculty_id, class_id) VALUES (?,?,?,?,?)";
////
////	    try (Connection conn = DBConnection.getConnection();
////	         PreparedStatement ps = conn.prepareStatement(sql)) {
////
////	        ps.setString(1, name);
////	        ps.setInt(2, max);
////	        ps.setInt(3, pass);
////	        ps.setInt(4, facultyId);
////	        ps.setInt(5, classId);
////
////	        ps.executeUpdate();
////	    } catch (SQLException e) {
////	        e.printStackTrace();
////	    }
////	}
////	public void addExam(String name, int max, int pass,
////            int facultyId, int classId, int subjectId) {
////
////String sql = "INSERT INTO exam " +
////         "(exam_name, max_marks, pass_marks, faculty_id, class_id, subject_id) " +
////         "VALUES (?, ?, ?, ?, ?, ?)";
////
////try (Connection conn = DBConnection.getConnection();
//// PreparedStatement ps = conn.prepareStatement(sql)) {
////
////ps.setString(1, name);
////ps.setInt(2, max);
////ps.setInt(3, pass);
////ps.setInt(4, facultyId);
////ps.setInt(5, classId);
////ps.setInt(6, subjectId);
////
////ps.executeUpdate();
////
////} catch (SQLException e) {
////e.printStackTrace();
////}
////}
//	public void addExam(String name, int max, int pass,
//            int facultyId, int classId, int subjectId) {
//
//String sql =
//"INSERT INTO exam " +
//"(exam_name, max_marks, pass_marks, faculty_id, class_id, subject_id) " +
//"VALUES (?, ?, ?, ?, ?, ?)";
//
//try (Connection conn = DBConnection.getConnection();
// PreparedStatement ps = conn.prepareStatement(sql)) {
//
//ps.setString(1, name);
//ps.setInt(2, max);
//ps.setInt(3, pass);
//ps.setInt(4, facultyId);
//ps.setInt(5, classId);
//ps.setInt(6, subjectId);
//
//ps.executeUpdate();
//} catch (SQLException e) {
//e.printStackTrace();
//}
//}
//
//
//
//    // ✅ GET EXAMS BY FACULTY
//    public List<String[]> getExamsByFaculty(int facultyId) {
//        List<String[]> list = new ArrayList<>();
//        String sql = "SELECT exam_id, exam_name, max_marks, pass_marks FROM exam WHERE faculty_id=?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, facultyId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                list.add(new String[]{
//                        rs.getString("exam_id"),
//                        rs.getString("exam_name"),
//                        rs.getString("max_marks"),
//                        rs.getString("pass_marks")
//                });
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    // ✅ UPDATE EXAM
//    public boolean updateExam(int examId, String name, int max, int pass) {
//        String sql = "UPDATE exam SET exam_name=?, max_marks=?, pass_marks=? WHERE exam_id=?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, name);
//            ps.setInt(2, max);
//            ps.setInt(3, pass);
//            ps.setInt(4, examId);
//
//            return ps.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // ✅ DELETE EXAM
//    public boolean deleteExam(int examId) {
//        String sql = "DELETE FROM exam WHERE exam_id=?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, examId);
//            return ps.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // ✅ RESULT SUMMARY
//    public List<StudentResult> getResultsForClassExam(int classId, int examId) {
//        List<StudentResult> list = new ArrayList<>();
//
//        String sql =
//                "SELECT s.roll_no, s.name, AVG(m.marks) avg_marks, " +
//                "CASE WHEN MIN(m.marks) >= 35 THEN 1 ELSE 0 END passed " +
//                "FROM marks m " +
//                "JOIN students s ON m.student_id = s.student_id " +
//                "WHERE s.class_id=? AND m.exam_id=? " +
//                "GROUP BY s.student_id, s.roll_no, s.name";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, classId);
//            ps.setInt(2, examId);
//
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                StudentResult sr = new StudentResult();
//                sr.setRollNo(rs.getString("roll_no"));
//                sr.setStudentName(rs.getString("name"));
//                sr.setAverageMarks(rs.getDouble("avg_marks"));
//                sr.setPassed(rs.getInt("passed") == 1);
//                list.add(sr);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
// // ---------------- GET ALL EXAMS (FOR MARKS UI) ----------------
//    public List<String[]> getAllExams() {
//        List<String[]> list = new ArrayList<>();
//        String sql = "SELECT exam_id, exam_name FROM exam";
//
//        try (Connection con = DBConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                list.add(new String[]{
//                        String.valueOf(rs.getInt("exam_id")),
//                        rs.getString("exam_name")
//                });
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
////    public List<String[]> getClassesByFaculty(int facultyId) {
////        List<String[]> list = new ArrayList<>();
////        String sql = "SELECT class_id, class_name FROM class WHERE faculty_id=?";
////
////        try (Connection conn = DBConnection.getConnection();
////             PreparedStatement ps = conn.prepareStatement(sql)) {
////
////            ps.setInt(1, facultyId);
////            ResultSet rs = ps.executeQuery();
////
////            while (rs.next()) {
////                list.add(new String[]{
////                    String.valueOf(rs.getInt("class_id")),
////                    rs.getString("class_name")
////                });
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////        return list;
////    }
////    public List<String[]> getClassesByFaculty(int facultyId) {
////        List<String[]> list = new ArrayList<>();
////
////        String sql =
////            "SELECT c.class_id, c.class_name " +
////            "FROM class c " +
////            "JOIN faculty_class fc ON c.class_id = fc.class_id " +
////            "WHERE fc.faculty_id = ?";
////
////        try (Connection conn = DBConnection.getConnection();
////             PreparedStatement ps = conn.prepareStatement(sql)) {
////
////            ps.setInt(1, facultyId);
////            ResultSet rs = ps.executeQuery();
////
////            while (rs.next()) {
////                list.add(new String[]{
////                    rs.getString("class_id"),
////                    rs.getString("class_name")
////                });
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////        return list;
////    }
//    public List<String[]> getClassesByFaculty(int facultyId) {
//        List<String[]> list = new ArrayList<>();
//
//        String sql =
//            "SELECT class_id, class_name " +
//            "FROM class " +
//            "WHERE faculty_id = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, facultyId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                list.add(new String[]{
//                    rs.getString("class_id"),
//                    rs.getString("class_name")
//                });
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    
////    public List<String[]> getSubjectsByClassAndFaculty(int classId, int facultyId) {
////
////        List<String[]> list = new ArrayList<>();
////
////        String sql = "SELECT subject_id, subject_name " +
////                     "FROM subject " +
////                     "WHERE class_id = ? AND faculty_id = ?";
////
////        try (Connection conn = DBConnection.getConnection();
////             PreparedStatement ps = conn.prepareStatement(sql)) {
////
////            ps.setInt(1, classId);
////            ps.setInt(2, facultyId);
////
////            ResultSet rs = ps.executeQuery();
////            while (rs.next()) {
////                list.add(new String[] {
////                    rs.getString("subject_id"),
////                    rs.getString("subject_name")
////                });
////            }
////
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////
////        return list;
////    }
//
////    public List<String[]> getSubjectsByClassAndFaculty(int classId, int facultyId) {
////        List<String[]> list = new ArrayList<>();
////
////        String sql =
////            "SELECT subject_id, subject_name " +
////            "FROM subject " +
////            "WHERE class_id = ? AND faculty_id = ?";
////
////        try (Connection conn = DBConnection.getConnection();
////             PreparedStatement ps = conn.prepareStatement(sql)) {
////
////            ps.setInt(1, classId);
////            ps.setInt(2, facultyId);
////
////            ResultSet rs = ps.executeQuery();
////            while (rs.next()) {
////                list.add(new String[]{
////                    rs.getString("subject_id"),
////                    rs.getString("subject_name")
////                });
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////        return list;
////    }
// // ExamDAO.java
//    public List<String[]> getSubjectsByClassAndFaculty(int classId, int facultyId) {
//
//        List<String[]> list = new ArrayList<>();
//
//        String sql =
//            "SELECT subject_id, subject_name " +
//            "FROM subject " +
//            "WHERE class_id = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, classId);
//
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                list.add(new String[]{
//                    rs.getString("subject_id"),
//                    rs.getString("subject_name")
//                });
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//
//
//    public List<String[]> getExamsByClassAndFaculty(int classId, int facultyId) {
//
//        List<String[]> list = new ArrayList<>();
//
//        String sql =
//            "SELECT exam_id, exam_name, max_marks, pass_marks " +
//            "FROM exam " +
//            "WHERE class_id = ? AND faculty_id = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, classId);
//            ps.setInt(2, facultyId);
//
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                list.add(new String[]{
//                    rs.getString("exam_id"),
//                    rs.getString("exam_name"),
//                    rs.getString("max_marks"),
//                    rs.getString("pass_marks")
//                });
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//
//    public List<String[]> getExamsByClassFacultyAndSubject(
//            int classId, int facultyId, int subjectId) {
//
//        List<String[]> list = new ArrayList<>();
//
//        String sql =
//            "SELECT exam_id, exam_name, max_marks, pass_marks " +
//            "FROM exam " +
//            "WHERE class_id = ? AND faculty_id = ? AND subject_id = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, classId);
//            ps.setInt(2, facultyId);
//            ps.setInt(3, subjectId);
//
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                list.add(new String[]{
//                    rs.getString("exam_id"),
//                    rs.getString("exam_name"),
//                    rs.getString("max_marks"),
//                    rs.getString("pass_marks")
//                });
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//
////    --
//
//public List<Exam> getExamsBySubject(int subjectId) {
//    List<Exam> exams = new ArrayList<>();
//    String sql = "SELECT * FROM exam WHERE subject_id = ?";
//
//    // Create connection directly inside the method
//    try (Connection conn = DBConnection.getConnection();
//         PreparedStatement ps = conn.prepareStatement(sql)) {
//
//        ps.setInt(1, subjectId);
//        try (ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                exams.add(new Exam(
//                        rs.getInt("exam_id"),
//                        rs.getString("exam_name"),
//                        rs.getInt("max_marks"),
//                        rs.getInt("pass_marks"),
//                        rs.getInt("class_id"),
//                        rs.getInt("subject_id"),
//                        rs.getInt("faculty_id")
//                ));
//            }
//        }
//
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }
//
//    return exams;
//}
//
//
//public int getPassMarks(int examId) {
//    String sql = "SELECT pass_marks FROM exam WHERE exam_id = ?";
//    try (Connection conn = DBConnection.getConnection();
//         PreparedStatement ps = conn.prepareStatement(sql)) {
//
//        ps.setInt(1, examId);
//        try (ResultSet rs = ps.executeQuery()) {
//            if (rs.next()) {
//                return rs.getInt("pass_marks");
//            }
//        }
//
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }
//    return 0; // default if not found
//}
//
//
//public int getMaxMarks(int examId) {
//    String sql = "SELECT max_marks FROM exam WHERE exam_id = ?";
//    try (Connection conn = DBConnection.getConnection();
//         PreparedStatement ps = conn.prepareStatement(sql)) {
//
//        ps.setInt(1, examId);
//        try (ResultSet rs = ps.executeQuery()) {
//            if (rs.next()) {
//                return rs.getInt("max_marks");
//            }
//        }
//
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }
//    return 100; // default if not found
//}
////    --
//    
//
//}


package com.college.sms.dao;

import com.college.sms.model.Exam;
import com.college.sms.model.StudentResult;
import com.college.sms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    // ✅ ADD EXAM with full context
    public void addExam(String name, int max, int pass, int facultyId, int classId, int subjectId) {
        String sql = "INSERT INTO exam (exam_name, max_marks, pass_marks, faculty_id, class_id, subject_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, max);
            ps.setInt(3, pass);
            ps.setInt(4, facultyId);
            ps.setInt(5, classId);
            ps.setInt(6, subjectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ CRITICAL FIX: Added missing method for faculty-isolated exam filtering
    public List<Exam> getExamsByFacultyClassSubject(int facultyId, int classId, int subjectId) {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT exam_id, exam_name, max_marks, pass_marks, class_id, subject_id, faculty_id " +
                     "FROM exam " +
                     "WHERE faculty_id = ? AND class_id = ? AND subject_id = ? " +
                     "ORDER BY exam_name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, facultyId);
            ps.setInt(2, classId);
            ps.setInt(3, subjectId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                exams.add(new Exam(
                    rs.getInt("exam_id"),
                    rs.getString("exam_name"),
                    rs.getInt("max_marks"),
                    rs.getInt("pass_marks"),
                    rs.getInt("class_id"),
                    rs.getInt("subject_id"),
                    rs.getInt("faculty_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    // ✅ GET PASS MARKS
    public int getPassMarks(int examId) {
        String sql = "SELECT pass_marks FROM exam WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("pass_marks");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ✅ UPDATE EXAM
    public boolean updateExam(int examId, String name, int max, int pass) {
        String sql = "UPDATE exam SET exam_name=?, max_marks=?, pass_marks=? WHERE exam_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, max);
            ps.setInt(3, pass);
            ps.setInt(4, examId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ DELETE EXAM
    public boolean deleteExam(int examId) {
        String sql = "DELETE FROM exam WHERE exam_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ GET EXAMS BY FACULTY (for faculty dashboard)
    public List<String[]> getExamsByFaculty(int facultyId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT exam_id, exam_name, max_marks, pass_marks FROM exam WHERE faculty_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getInt("exam_id")),
                    rs.getString("exam_name"),
                    String.valueOf(rs.getInt("max_marks")),
                    String.valueOf(rs.getInt("pass_marks"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ GET ALL EXAMS (for admin UI)
    public List<String[]> getAllExams() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT exam_id, exam_name FROM exam ORDER BY exam_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getInt("exam_id")),
                    rs.getString("exam_name")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
 // ✅ GET EXAMS BY SUBJECT ID (required for backward compatibility)
    public List<Exam> getExamsBySubject(int subjectId) {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT exam_id, exam_name, max_marks, pass_marks, class_id, subject_id, faculty_id " +
                     "FROM exam WHERE subject_id = ? ORDER BY exam_name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, subjectId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                exams.add(new Exam(
                    rs.getInt("exam_id"),
                    rs.getString("exam_name"),
                    rs.getInt("max_marks"),
                    rs.getInt("pass_marks"),
                    rs.getInt("class_id"),
                    rs.getInt("subject_id"),
                    rs.getInt("faculty_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }
 // ✅ GET EXAM BY ID (required for ResultsUI)
    public Exam getExamById(int examId) {
        String sql = "SELECT exam_id, exam_name, max_marks, pass_marks, class_id, subject_id, faculty_id " +
                     "FROM exam WHERE exam_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Exam(
                    rs.getInt("exam_id"),
                    rs.getString("exam_name"),
                    rs.getInt("max_marks"),
                    rs.getInt("pass_marks"),
                    rs.getInt("class_id"),
                    rs.getInt("subject_id"),
                    rs.getInt("faculty_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Exam not found
    }
}