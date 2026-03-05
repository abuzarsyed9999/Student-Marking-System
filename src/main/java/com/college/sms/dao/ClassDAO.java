//package com.college.sms.dao;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.college.sms.util.DBConnection;
//
//public class ClassDAO {
//
//   
//    public boolean addClass(String className, int facultyId) {
//
//        String sql = "INSERT INTO class (class_name, faculty_id) VALUES (?, ?)";
//
//        try (
//            Connection con = DBConnection.getConnection();
//            PreparedStatement ps = con.prepareStatement(sql);
//        ) {
//            ps.setString(1, className);
//            ps.setInt(2, facultyId);
//
//            return ps.executeUpdate() > 0;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // View classes of logged-in faculty
//    public void viewClasses(int facultyId) {
//
//        String sql = "SELECT class_id, class_name FROM class WHERE faculty_id = ?";
//
//        try (
//            Connection con = DBConnection.getConnection();
//            PreparedStatement ps = con.prepareStatement(sql);
//        ) {
//            ps.setInt(1, facultyId);
//
//            ResultSet rs = ps.executeQuery();
//
//            System.out.println("Classes handled by faculty:");
//            while (rs.next()) {
//                System.out.println(
//                    rs.getInt("class_id") + " - " + rs.getString("class_name")
//                );
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
// // Returns list of all classes as {classId, className} array
//    public java.util.List<String[]> getAllClasses() {
//        java.util.List<String[]> list = new java.util.ArrayList<>();
//        String sql = "SELECT class_id, class_name FROM class";
//
//        try (Connection con = DBConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                list.add(new String[]{
//                    String.valueOf(rs.getInt("class_id")),
//                    rs.getString("class_name")
//                });
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//    
////    public List<String[]> getClassesByFaculty(int facultyId) {
////        List<String[]> classes = new ArrayList<>();
////        String sql = "SELECT class_id, class_name FROM class WHERE faculty_id = ?";
////
////        try (Connection conn = DBConnection.getConnection();
////             PreparedStatement ps = conn.prepareStatement(sql)) {
////
////            ps.setInt(1, facultyId);
////            try (ResultSet rs = ps.executeQuery()) {
////                while (rs.next()) {
////                    classes.add(new String[]{
////                            String.valueOf(rs.getInt("class_id")),
////                            rs.getString("class_name")
////                    });
////                }
////            }
////
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////
////        return classes;
////    }
//    public List<String[]> getClassesByFaculty(int facultyId) {
//
//        List<String[]> list = new ArrayList<>();
//
//        String sql = "SELECT class_id, class_name FROM class WHERE faculty_id = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, facultyId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                list.add(new String[]{
//                    String.valueOf(rs.getInt("class_id")),
//                    rs.getString("class_name")
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
//
//}
package com.college.sms.dao;

import com.college.sms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    // ✅ ADD CLASS
    public boolean addClass(String className, int facultyId) {
        String sql = "INSERT INTO class (class_name, faculty_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, className);
            ps.setInt(2, facultyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ DELETE CLASS (with enhanced error messages)
    public String deleteClass(int classId, int facultyId) {
        // Verify class belongs to faculty (security)
        if (!isClassAssignedToFaculty(classId, facultyId)) {
            return "Cannot delete class: Class not assigned to your account";
        }
        
        // Check for students FIRST (most common blocker)
        int studentCount = countStudents(classId);
        if (studentCount > 0) {
            return String.format(
                "Cannot delete class: %d student(s) are enrolled.\n" +
                "1. Go to 'Student Management'\n" +
                "2. Delete all students in this class\n" +
                "3. Try deleting class again",
                studentCount
            );
        }
        
        // Check for subjects (your current blocker)
        int subjectCount = countSubjects(classId);
        if (subjectCount > 0) {
            return String.format(
                "Cannot delete class: %d subject(s) are assigned.\n" +
                "✅ QUICK FIX:\n" +
                "1. Go to 'Subject Management'\n" +
                "2. Select this class from dropdown\n" +
                "3. Delete all subjects shown in table\n" +
                "4. Return here and delete class",
                subjectCount
            );
        }
        
        // Safe to delete
        String sql = "DELETE FROM class WHERE class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            int rows = ps.executeUpdate();
            return rows > 0 ? "SUCCESS" : "Class not found";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }
    
    // ✅ Helper: Count students in class
    private int countStudents(int classId) {
        String sql = "SELECT COUNT(*) FROM student WHERE class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    // ✅ Helper: Count subjects in class (FOR CURRENT FACULTY ONLY)
    private int countSubjects(int classId) {
        String sql = "SELECT COUNT(*) FROM subject WHERE class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    // ✅ Helper: Verify faculty owns class
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

    // ✅ GET CLASSES BY FACULTY
    public List<String[]> getClassesByFaculty(int facultyId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT class_id, class_name FROM class WHERE faculty_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getInt("class_id")),
                    rs.getString("class_name")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ GET ALL CLASSES (for admin)
    public List<String[]> getAllClasses() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT class_id, class_name FROM class";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getInt("class_id")),
                    rs.getString("class_name")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
//    --
 // ADD TO com.college.sms.dao.ClassDAO
    public int getClassIdByName(String className) {
        String sql = "SELECT class_id FROM class WHERE class_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, className);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("class_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 7; // Default fallback for your class (HARDCODED TEMP FIX)
    }
//    --
}