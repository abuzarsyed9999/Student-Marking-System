//
//package com.college.sms.dao;
//
//import com.college.sms.model.Student;
//import com.college.sms.util.DBConnection;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class StudentDAO {
//
//    // ✅ GET STUDENTS BY FACULTY (Critical fix - enforces faculty isolation)
//    public List<String[]> getStudentsByFaculty(int facultyId) {
//        List<String[]> students = new ArrayList<>();
//        String sql = "SELECT s.student_id, s.roll_no, s.name, c.class_name, c.class_id " +
//                     "FROM student s " +
//                     "JOIN class c ON s.class_id = c.class_id " +
//                     "WHERE c.faculty_id = ? " +
//                     "ORDER BY c.class_name, s.name";
//        
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            
//            ps.setInt(1, facultyId);
//            ResultSet rs = ps.executeQuery();
//            
//            while (rs.next()) {
//                students.add(new String[]{
//                    String.valueOf(rs.getInt("student_id")),
//                    rs.getString("roll_no"),
//                    rs.getString("name"),
//                    rs.getString("class_name"),
//                    String.valueOf(rs.getInt("class_id"))
//                });
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return students;
//    }
//
//    // ✅ ADD STUDENT (with faculty validation)
//    public boolean addStudent(String rollNo, String name, int classId, int facultyId) {
//        if (!isClassAssignedToFaculty(classId, facultyId)) {
//            return false;
//        }
//        
//        String sql = "INSERT INTO student (roll_no, name, class_id) VALUES (?, ?, ?)";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, rollNo);
//            ps.setString(2, name);
//            ps.setInt(3, classId);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    // ✅ UPDATE STUDENT (with faculty validation)
//    public boolean updateStudent(int studentId, String rollNo, String name, int newClassId, int facultyId) {
//        if (!isStudentInFacultyClass(studentId, facultyId) || 
//            !isClassAssignedToFaculty(newClassId, facultyId)) {
//            return false;
//        }
//        
//        String sql = "UPDATE student SET roll_no = ?, name = ?, class_id = ? WHERE student_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, rollNo);
//            ps.setString(2, name);
//            ps.setInt(3, newClassId);
//            ps.setInt(4, studentId);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    // ✅ DELETE STUDENT (with dependency checks)
//    public String deleteStudent(int studentId, int facultyId) {
//        if (!isStudentInFacultyClass(studentId, facultyId)) {
//            return "Cannot delete: Student not in your assigned classes";
//        }
//        
//        if (hasMarks(studentId)) {
//            return "Cannot delete: Student has marks records.\nDelete marks first in Marks Management.";
//        }
//        
//        String sql = "DELETE FROM student WHERE student_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, studentId);
//            int rows = ps.executeUpdate();
//            return rows > 0 ? "SUCCESS" : "Student not found";
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return "Database error: " + e.getMessage();
//        }
//    }
//
//    // ✅ GET STUDENTS BY CLASS (for ResultsUI/Marks UI)
////    public List<String[]> getStudentsByClass(int classId) {
////        List<String[]> students = new ArrayList<>();
////        String sql = "SELECT student_id, roll_no, name FROM student WHERE class_id = ? ORDER BY name";
////        try (Connection conn = DBConnection.getConnection();
////             PreparedStatement ps = conn.prepareStatement(sql)) {
////            ps.setInt(1, classId);
////            ResultSet rs = ps.executeQuery();
////            while (rs.next()) {
////                students.add(new String[]{
////                    String.valueOf(rs.getInt("student_id")),
////                    rs.getString("roll_no"),
////                    rs.getString("name")
////                });
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////        return students;
////    }
// // REPLACE IN com.college.sms.dao.StudentDAO
//    public List<String[]> getStudentsByClass(int classId) {
//        List<String[]> students = new ArrayList<>();
////        String sql = "SELECT student_id, roll_no, name FROM student WHERE class_id = ?";
//        String sql = "SELECT student_id, roll_no, name, class_id, parent_mobile, parent_email, consent_to_communicate " +
//                "FROM student WHERE class_id = ?";
//        System.out.println("🔍 DEBUG StudentDAO.getStudentsByClass(): Querying class_id=" + classId);
//        
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            
//            ps.setInt(1, classId);
//            ResultSet rs = ps.executeQuery();
//            
//            int count = 0;
//            while (rs.next()) {
//                students.add(new String[]{
//                    String.valueOf(rs.getInt("student_id")),
//                    rs.getString("roll_no"),
//                    rs.getString("name")
//                });
//                count++;
//            }
//            
//            System.out.println("✅ DEBUG: Found " + count + " students for class_id=" + classId);
//            if (count == 0) {
//                // EXTRA DEBUG: Show ALL classes to help diagnose
//                System.out.println("⚠️ DEBUG: Checking ALL classes in DB:");
//                try (Statement stmt = conn.createStatement();
//                     ResultSet allClasses = stmt.executeQuery("SELECT class_id, class_name FROM class")) {
//                    while (allClasses.next()) {
//                        System.out.println("   class_id=" + allClasses.getInt("class_id") + 
//                                         " | " + allClasses.getString("class_name"));
//                    }
//                }
//            }
//            
//        } catch (SQLException e) {
//            System.err.println("❌ SQL Error in getStudentsByClass: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return students;
//    }
//    // ✅ GET MARKS BY EXAM (for ResultsUI - CORRECT implementation)
////    public int getMarksByExam(int studentId, int examId) {
////        String sql = "SELECT marks_obtained FROM marks WHERE student_id = ? AND exam_id = ?";
////        try (Connection conn = DBConnection.getConnection();
////             PreparedStatement ps = conn.prepareStatement(sql)) {
////            ps.setInt(1, studentId);
////            ps.setInt(2, examId);
////            ResultSet rs = ps.executeQuery();
////            return rs.next() ? rs.getInt("marks_obtained") : 0;
////        } catch (SQLException e) {
////            e.printStackTrace();
////            return 0;
////        }
////    }
//
//    // ===== HELPER METHODS =====
//    private boolean isClassAssignedToFaculty(int classId, int facultyId) {
//        String sql = "SELECT COUNT(*) FROM class WHERE class_id = ? AND faculty_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, classId);
//            ps.setInt(2, facultyId);
//            ResultSet rs = ps.executeQuery();
//            return rs.next() && rs.getInt(1) > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    private boolean isStudentInFacultyClass(int studentId, int facultyId) {
//        String sql = "SELECT COUNT(*) " +
//                     "FROM student s " +
//                     "JOIN class c ON s.class_id = c.class_id " +
//                     "WHERE s.student_id = ? AND c.faculty_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, studentId);
//            ps.setInt(2, facultyId);
//            ResultSet rs = ps.executeQuery();
//            return rs.next() && rs.getInt(1) > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    private boolean hasMarks(int studentId) {
//        String sql = "SELECT COUNT(*) FROM marks WHERE student_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, studentId);
//            ResultSet rs = ps.executeQuery();
//            return rs.next() && rs.getInt(1) > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
// // MUST EXIST IN com.college.sms.dao.StudentDAO
//    public int getMarksByExam(int studentId, int examId) {
//        String sql = "SELECT marks_obtained FROM marks WHERE student_id = ? AND exam_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            
//            ps.setInt(1, studentId);
//            ps.setInt(2, examId);
//            ResultSet rs = ps.executeQuery();
//            
//            if (rs.next()) {
//                return rs.getInt("marks_obtained");
//            }
//        } catch (SQLException e) {
//            System.err.println("❌ SQL Error fetching marks: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return 0; // Return 0 if no record found
//    }
////    --
// // ===== ADD THIS METHOD (FETCHES FULL STUDENT WITH PARENT DETAILS) =====
//    public Student getStudentById(int studentId) throws SQLException {
//        String sql = "SELECT student_id, roll_no, name, class_id, parent_mobile, parent_email, consent_to_communicate " +
//                     "FROM student WHERE student_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, studentId);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                Student student = new Student();
//                student.setStudentId(rs.getInt("student_id"));
//                student.setRollNo(rs.getString("roll_no"));
//                student.setName(rs.getString("name"));
//                student.setClassId(rs.getInt("class_id"));
//                
//                // NEW: Parent contact details
//                student.setParentMobile(rs.getString("parent_mobile"));
//                student.setParentEmail(rs.getString("parent_email"));
//                if (rs.getObject("consent_to_communicate") != null) {
//                    student.setConsentToCommunicate(rs.getBoolean("consent_to_communicate"));
//                } else {
//                    student.setConsentToCommunicate(true); // Default if NULL
//                }
//                return student;
//            }
//            return null;
//        }
//    }
//
//    // ===== ADD THIS METHOD (UPDATES PARENT CONTACT DETAILS ONLY) =====
//    public boolean updateParentContact(int studentId, String mobile, String email, boolean consent) throws SQLException {
//        String sql = "UPDATE student SET parent_mobile = ?, parent_email = ?, consent_to_communicate = ? WHERE student_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, mobile != null && !mobile.trim().isEmpty() ? mobile.trim() : null);
//            ps.setString(2, email != null && !email.trim().isEmpty() ? email.trim() : null);
//            ps.setBoolean(3, consent);
//            ps.setInt(4, studentId);
//            return ps.executeUpdate() > 0;
//        }
//    }
//
//    // ===== FIX EXISTING getStudentsByClass() METHOD =====
//    // In your existing getStudentsByClass() method, UPDATE the SQL query to include new columns:
//    // BEFORE: "SELECT student_id, roll_no, name, class_id FROM student WHERE class_id = ?"
//    // AFTER:  "SELECT student_id, roll_no, name, class_id, parent_mobile, parent_email, consent_to_communicate FROM student WHERE class_id = ?"
//    //
//    // Then ADD these lines when mapping ResultSet to String[]:
//    // rowData[3] = rs.getString("parent_mobile"); // Add after class_id
//    // rowData[4] = rs.getString("parent_email");
//    // rowData[5] = rs.getBoolean("consent_to_communicate");
////    --
//}

package com.college.sms.dao;

import com.college.sms.model.Student;
import com.college.sms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // ✅ GET STUDENTS BY FACULTY (Critical fix - enforces faculty isolation)
    public List<String[]> getStudentsByFaculty(int facultyId) {
        List<String[]> students = new ArrayList<>();
        String sql = "SELECT s.student_id, s.roll_no, s.name, c.class_name, c.class_id " +
                     "FROM student s " +
                     "JOIN class c ON s.class_id = c.class_id " +
                     "WHERE c.faculty_id = ? " +
                     "ORDER BY c.class_name, s.name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                students.add(new String[]{
                    String.valueOf(rs.getInt("student_id")),
                    rs.getString("roll_no"),
                    rs.getString("name"),
                    rs.getString("class_name"),
                    String.valueOf(rs.getInt("class_id"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // ✅ ADD STUDENT (with faculty validation)
    public boolean addStudent(String rollNo, String name, int classId, int facultyId) {
        if (!isClassAssignedToFaculty(classId, facultyId)) {
            return false;
        }
        
        String sql = "INSERT INTO student (roll_no, name, class_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            ps.setString(2, name);
            ps.setInt(3, classId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ UPDATE STUDENT (with faculty validation)
    public boolean updateStudent(int studentId, String rollNo, String name, int newClassId, int facultyId) {
        if (!isStudentInFacultyClass(studentId, facultyId) || 
            !isClassAssignedToFaculty(newClassId, facultyId)) {
            return false;
        }
        
        String sql = "UPDATE student SET roll_no = ?, name = ?, class_id = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            ps.setString(2, name);
            ps.setInt(3, newClassId);
            ps.setInt(4, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ DELETE STUDENT (with dependency checks)
    public String deleteStudent(int studentId, int facultyId) {
        if (!isStudentInFacultyClass(studentId, facultyId)) {
            return "Cannot delete: Student not in your assigned classes";
        }
        
        if (hasMarks(studentId)) {
            return "Cannot delete: Student has marks records.\nDelete marks first in Marks Management.";
        }
        
        String sql = "DELETE FROM student WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            int rows = ps.executeUpdate();
            return rows > 0 ? "SUCCESS" : "Student not found";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }

    // ✅ GET STUDENTS BY CLASS (for ResultsUI/Marks UI)
    public List<String[]> getStudentsByClass(int classId) {
        List<String[]> students = new ArrayList<>();
        String sql = "SELECT student_id, roll_no, name FROM student WHERE class_id = ? ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                students.add(new String[]{
                    String.valueOf(rs.getInt("student_id")),
                    rs.getString("roll_no"),
                    rs.getString("name")
                });
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL Error in getStudentsByClass: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    // ✅ GET MARKS BY EXAM (for ResultsUI)
    public int getMarksByExam(int studentId, int examId) {
        String sql = "SELECT marks_obtained FROM marks WHERE student_id = ? AND exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("marks_obtained");
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL Error fetching marks: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // ✅ FETCH FULL STUDENT WITH PARENT DETAILS
    public Student getStudentById(int studentId) throws SQLException {
        String sql = "SELECT student_id, roll_no, name, class_id, parent_mobile, parent_email, consent_to_communicate " +
                     "FROM student WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setRollNo(rs.getString("roll_no"));
                student.setName(rs.getString("name"));
                student.setClassId(rs.getInt("class_id"));
                
                student.setParentMobile(rs.getString("parent_mobile"));
                student.setParentEmail(rs.getString("parent_email"));
                
                Object consentObj = rs.getObject("consent_to_communicate");
                if (consentObj != null) {
                    student.setConsentToCommunicate(rs.getBoolean("consent_to_communicate"));
                } else {
                    student.setConsentToCommunicate(true);
                }
                return student;
            }
            return null;
        }
    }

    // ✅ UPDATE PARENT CONTACT DETAILS ONLY
    public boolean updateParentContact(int studentId, String mobile, String email, boolean consent) throws SQLException {
        String sql = "UPDATE student SET parent_mobile = ?, parent_email = ?, consent_to_communicate = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, (mobile != null && !mobile.trim().isEmpty()) ? mobile.trim() : null);
            ps.setString(2, (email != null && !email.trim().isEmpty()) ? email.trim() : null);
            ps.setBoolean(3, consent);
            ps.setInt(4, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== HELPER METHODS =====
    
    private boolean isClassAssignedToFaculty(int classId, int facultyId) {
        String sql = "SELECT COUNT(*) FROM class WHERE class_id = ? AND faculty_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.setInt(2, facultyId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isStudentInFacultyClass(int studentId, int facultyId) {
        String sql = "SELECT COUNT(*) " +
                     "FROM student s " +
                     "JOIN class c ON s.class_id = c.class_id " +
                     "WHERE s.student_id = ? AND c.faculty_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, facultyId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean hasMarks(int studentId) {
        String sql = "SELECT COUNT(*) FROM marks WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}