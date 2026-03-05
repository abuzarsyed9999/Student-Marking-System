////package com.college.sms.dao;
////
////import com.college.sms.model.Subject;
////import com.college.sms.util.DBConnection;
////
////import java.sql.*;
////import java.util.ArrayList;
////import java.util.List;
////
////public class SubjectDAO {
////
////    private Connection conn;
////
////    // Constructor only creates connection; no queries
////    public SubjectDAO() throws SQLException {
////        conn = DriverManager.getConnection(
////                "jdbc:mysql://localhost:3306/student_marks_system", "root", "#Abuzar99"); // put your password here
////    }
////
////    // Get subjects by class
////    public List<Subject> getSubjectsByClass(int classId) {
////        List<Subject> list = new ArrayList<>();
////        String sql = "SELECT subject_id, subject_name, class_id FROM subject WHERE class_id = ?";
////
////        try (PreparedStatement ps = conn.prepareStatement(sql)) {
////            ps.setInt(1, classId);
////            ResultSet rs = ps.executeQuery();
////            while (rs.next()) {
////                list.add(new Subject(
////                        rs.getInt("subject_id"),
////                        rs.getString("subject_name"),
////                        rs.getInt("class_id")
////                ));
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////        return list;
////    }
////
////    // Add a subject
////    public boolean addSubject(String name, int classId) {
////        String sql = "INSERT INTO subject(subject_name, class_id) VALUES(?, ?)";
////        try (PreparedStatement ps = conn.prepareStatement(sql)) {
////            ps.setString(1, name);
////            ps.setInt(2, classId);
////            return ps.executeUpdate() > 0;
////        } catch (SQLException e) {
////            e.printStackTrace();
////            return false;
////        }
////    }
////
////    // Update subject
////    public boolean updateSubject(int id, String name, int classId) {
////        String sql = "UPDATE subject SET subject_name=?, class_id=? WHERE subject_id=?";
////        try (PreparedStatement ps = conn.prepareStatement(sql)) {
////            ps.setString(1, name);
////            ps.setInt(2, classId);
////            ps.setInt(3, id);
////            return ps.executeUpdate() > 0;
////        } catch (SQLException e) {
////            e.printStackTrace();
////            return false;
////        }
////    }
////
////    // Delete subject
////    public boolean deleteSubject(int id) {
////        String sql = "DELETE FROM subject WHERE subject_id=?";
////        try (PreparedStatement ps = conn.prepareStatement(sql)) {
////            ps.setInt(1, id);
////            return ps.executeUpdate() > 0;
////        } catch (SQLException e) {
////            e.printStackTrace();
////            return false;
////        }
////    }
//// // Get all subjects without filtering by class
////    public List<Subject> getAllSubjects() {
////        List<Subject> list = new ArrayList<>();
////        String sql = "SELECT subject_id, subject_name, class_id FROM subject";
////
////        try (PreparedStatement ps = conn.prepareStatement(sql)) {
////            ResultSet rs = ps.executeQuery();
////            while (rs.next()) {
////                list.add(new Subject(
////                        rs.getInt("subject_id"),
////                        rs.getString("subject_name"),
////                        rs.getInt("class_id")
////                ));
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////        return list;
////    }
////    
////    public List<String[]> getClassesByFaculty(int facultyId) {
////        List<String[]> list = new ArrayList<>();
////        String sql = "SELECT class_id, class_name FROM class WHERE faculty_id=?";
////        try (Connection conn = DBConnection.getConnection();
////             PreparedStatement ps = conn.prepareStatement(sql)) {
////            ps.setInt(1, facultyId);
////            ResultSet rs = ps.executeQuery();
////            while (rs.next()) {
////                list.add(new String[]{rs.getString("class_id"), rs.getString("class_name")});
////            }
////        } catch (SQLException e) { e.printStackTrace(); }
////        return list;
////    }
////
////     
////
////
////}
//
//package com.college.sms.dao;
//
//import com.college.sms.model.Subject;
//import com.college.sms.util.DBConnection;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class SubjectDAO {
//
//    // ✅ SUBJECTS BY FACULTY
//    public List<Subject> getSubjectsByFaculty(int facultyId) {
//        List<Subject> list = new ArrayList<>();
//        String sql = "SELECT subject_id, subject_name FROM subject WHERE faculty_id=?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, facultyId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//            	list.add(new Subject(
//            		    rs.getInt("subject_id"),
//            		    rs.getString("subject_name"),
//            		    rs.getInt("class_id"),
//            		    0 // or some default facultyId if unknown
//            		));
//
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
////    // ✅ ADD SUBJECT
////    public boolean addSubject(String name, int facultyId) {
////        String sql = "INSERT INTO subject(subject_name, faculty_id) VALUES (?,?)";
////
////        try (Connection conn = DBConnection.getConnection();
////             PreparedStatement ps = conn.prepareStatement(sql)) {
////
////            ps.setString(1, name);
////            ps.setInt(2, facultyId);
////            return ps.executeUpdate() > 0;
////
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////        return false;
////    }
//    
//    public boolean addSubject(String name, int classId, int facultyId) {
//        String sql = "INSERT INTO subject (subject_name, class_id, faculty_id) VALUES (?, ?, ?)";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, name);
//            ps.setInt(2, classId);
//            ps.setInt(3, facultyId);
//
//            return ps.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace(); // IMPORTANT
//        }
//        return false;
//    }
//
//
//    // ✅ UPDATE SUBJECT
//    public boolean updateSubject(int id, String name) {
//        String sql = "UPDATE subject SET subject_name=? WHERE subject_id=?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, name);
//            ps.setInt(2, id);
//            return ps.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // ✅ DELETE SUBJECT
//    public boolean deleteSubject(int id) {
//        String sql = "DELETE FROM subject WHERE subject_id=?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, id);
//            return ps.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // ✅ CLASSES BY FACULTY
//    public List<String[]> getClassesByFaculty(int facultyId) {
//        List<String[]> list = new ArrayList<>();
//        String sql = "SELECT class_id, class_name FROM class WHERE faculty_id=?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, facultyId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                list.add(new String[]{
//                        rs.getString("class_id"),
//                        rs.getString("class_name")
//                });
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//    public List<Subject> getSubjectsByClass(int classId) {
//        List<Subject> list = new ArrayList<>();
//        String sql = "SELECT subject_id, subject_name, class_id FROM subject WHERE class_id=?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, classId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                list.add(new Subject(
//                        rs.getInt("subject_id"),
//                        rs.getString("subject_name"),
//                        rs.getInt("class_id")
//                ));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//   
//    
////    --
//    
//    public List<Subject> getSubjectsByClassAndFaculty(int classId, int facultyId) {
//        List<Subject> subjects = new ArrayList<>();
//        String sql = "SELECT * FROM subject WHERE class_id=? AND faculty_id=?";
//
//        // Connection is created inside the try-with-resources
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, classId);
//            ps.setInt(2, facultyId);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    // Create Subject object with all fields from your table
//                    Subject s = new Subject();
//                    s.setSubjectId(rs.getInt("subject_id"));
//                    s.setSubjectName(rs.getString("subject_name"));
//                    s.setClassId(rs.getInt("class_id"));
//                    s.setFacultyId(rs.getInt("faculty_id"));
//
//                    subjects.add(s);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return subjects;
//    }
////    --
//
//
//
//}
//
//package com.college.sms.dao;
//
//import com.college.sms.model.Subject;
//import com.college.sms.util.DBConnection;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class SubjectDAO {
//
//    // ✅ CORRECTED: Filters by BOTH class_id AND faculty_id
//    public List<Subject> getSubjectsByClassAndFaculty(int classId, int facultyId) {
//        List<Subject> subjects = new ArrayList<>();
//        String sql = "SELECT subject_id, subject_name, class_id, faculty_id " +
//                     "FROM subject " +
//                     "WHERE class_id = ? AND faculty_id = ?"; // BOTH filters applied
//        
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            
//            ps.setInt(1, classId);
//            ps.setInt(2, facultyId);
//            ResultSet rs = ps.executeQuery();
//            
//            while (rs.next()) {
//                Subject s = new Subject();
//                s.setSubjectId(rs.getInt("subject_id"));
//                s.setSubjectName(rs.getString("subject_name"));
//                s.setClassId(rs.getInt("class_id"));
//                s.setFacultyId(rs.getInt("faculty_id"));
//                subjects.add(s);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return subjects;
//    }
//
//    public boolean addSubject(String name, int classId, int facultyId) {
//        String sql = "INSERT INTO subject (subject_name, class_id, faculty_id) VALUES (?, ?, ?)";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, name);
//            ps.setInt(2, classId);
//            ps.setInt(3, facultyId);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public boolean updateSubject(int id, String name) {
//        String sql = "UPDATE subject SET subject_name=? WHERE subject_id=?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, name);
//            ps.setInt(2, id);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public boolean deleteSubject(int id) {
//        String sql = "DELETE FROM subject WHERE subject_id=?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, id);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public List<Subject> getSubjectsByClass(int classId) {
//        List<Subject> list = new ArrayList<>();
//        String sql = "SELECT subject_id, subject_name, class_id, faculty_id FROM subject WHERE class_id=?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, classId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                Subject s = new Subject();
//                s.setSubjectId(rs.getInt("subject_id"));
//                s.setSubjectName(rs.getString("subject_name"));
//                s.setClassId(rs.getInt("class_id"));
//                s.setFacultyId(rs.getInt("faculty_id"));
//                list.add(s);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//}
//
//package com.college.sms.dao;
package com.college.sms.dao;

import com.college.sms.model.Subject;
import com.college.sms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public List<Subject> getSubjectsByClassAndFaculty(int classId, int facultyId) {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT subject_id, subject_name, class_id, faculty_id FROM subject WHERE class_id = ? AND faculty_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, classId);
            ps.setInt(2, facultyId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Subject s = new Subject();
                s.setSubjectId(rs.getInt("subject_id"));
                s.setSubjectName(rs.getString("subject_name"));
                s.setClassId(rs.getInt("class_id"));
                s.setFacultyId(rs.getInt("faculty_id"));
                subjects.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    public boolean addSubject(String name, int classId, int facultyId) {
        String sql = "INSERT INTO subject (subject_name, class_id, faculty_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, classId);
            ps.setInt(3, facultyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSubject(int id, String name) {
        String sql = "UPDATE subject SET subject_name=? WHERE subject_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String deleteSubject(int subjectId) {
        if (hasExams(subjectId)) {
            return "Cannot delete subject: Exams exist for this subject.\nPlease delete all exams in 'Exam Management' first.";
        }
        
        String sql = "DELETE FROM subject WHERE subject_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            int rows = ps.executeUpdate();
            return rows > 0 ? "SUCCESS" : "Subject not found";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }
    
    private boolean hasExams(int subjectId) {
        String sql = "SELECT COUNT(*) FROM exam WHERE subject_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Subject> getSubjectsByClass(int classId) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT subject_id, subject_name, class_id, faculty_id FROM subject WHERE class_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Subject s = new Subject();
                s.setSubjectId(rs.getInt("subject_id"));
                s.setSubjectName(rs.getString("subject_name"));
                s.setClassId(rs.getInt("class_id"));
                s.setFacultyId(rs.getInt("faculty_id"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}