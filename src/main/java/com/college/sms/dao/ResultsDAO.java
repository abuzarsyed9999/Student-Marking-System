package com.college.sms.dao;

import com.college.sms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultsDAO {

    private Connection conn;

    public ResultsDAO() {
        try {
            conn = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get marks for all students in a class and subject
    public List<String[]> getMarksByClassAndSubject(int classId, int subjectId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT s.roll_no, s.name, m.marks_obtained " +
                     "FROM marks m " +
                     "JOIN student s ON m.student_id=s.student_id " +
                     "WHERE s.class_id=? AND m.subject_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.setInt(2, subjectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getString("marks_obtained")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Get overall marks for all exams for a student (for pie chart)
    public List<String[]> getStudentMarksSummary(int studentId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT sub.subject_name, SUM(marks_obtained) as total " +
                     "FROM marks m " +
                     "JOIN subject sub ON m.subject_id=sub.subject_id " +
                     "WHERE m.student_id=? " +
                     "GROUP BY sub.subject_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{rs.getString("subject_name"), rs.getString("total")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
