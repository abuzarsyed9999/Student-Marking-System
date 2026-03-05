//package com.college.sms.dao;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//import com.college.sms.util.DBConnection;
//
//public class FacultyDAO {
//
//    public int loginFaculty(String email, String password) {
//
//        int facultyId = -1;
//
//        String sql = "SELECT faculty_id FROM faculty WHERE email = ? AND password = ?";
//
//        try (
//            Connection con = DBConnection.getConnection();
//            PreparedStatement ps = con.prepareStatement(sql);
//        ) {
//            ps.setString(1, email);
//            ps.setString(2, password);
//
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                facultyId = rs.getInt("faculty_id");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return facultyId;
//    }
//}


//package com.college.sms.dao;
package com.college.sms.dao;

import com.college.sms.model.Faculty;
import com.college.sms.util.DBConnection;
import com.college.sms.util.PasswordUtil;

import java.sql.*;

public class FacultyDAO {

    // ✅ HASH-AWARE AUTHENTICATION (works with BOTH plaintext AND hashed passwords)
    public Faculty authenticateFaculty(String email, String password) {
        String sql = "SELECT faculty_id, name, email, password FROM faculty WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email.trim().toLowerCase());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int facultyId = rs.getInt("faculty_id");
                String name = rs.getString("name");
                String storedPassword = rs.getString("password");
                
                // ✅ SMART VERIFICATION: Try BOTH plaintext AND hashed comparison
                boolean isAuthenticated = false;
                
                // Option 1: Check if stored password is plaintext match
                if (storedPassword.equals(password.trim())) {
                    isAuthenticated = true;
                    System.out.println("DEBUG: Authenticated using PLAINTEXT password");
                } 
                // Option 2: Check if stored password is hashed match
                else if (PasswordUtil.verifyPassword(password.trim(), storedPassword)) {
                    isAuthenticated = true;
                    System.out.println("DEBUG: Authenticated using HASHED password");
                }
                
                if (isAuthenticated) {
                    return new Faculty(facultyId, name, email);
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ✅ OTHER METHODS (unchanged)
    public Faculty getFacultyById(int facultyId) {
        String sql = "SELECT faculty_id, name, email FROM faculty WHERE faculty_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Faculty(
                    rs.getInt("faculty_id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ ADD NEW FACULTY WITH HASHED PASSWORD (for future registrations)
    public boolean addFaculty(String name, String email, String password) {
        String sql = "INSERT INTO faculty (name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, email.trim().toLowerCase());
            ps.setString(3, PasswordUtil.hashPassword(password.trim())); // Always hash new passwords
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}