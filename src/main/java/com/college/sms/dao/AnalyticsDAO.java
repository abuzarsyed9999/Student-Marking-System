package com.college.sms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.college.sms.util.DBConnection;

public class AnalyticsDAO {

    public void classAnalytics(int classId, int subjectId, int examId) {

        String sql =
            "SELECT COUNT(*) AS total_students, " +
            "SUM(CASE WHEN m.marks_obtained >= e.pass_marks THEN 1 ELSE 0 END) AS passed, " +
            "SUM(CASE WHEN m.marks_obtained < e.pass_marks THEN 1 ELSE 0 END) AS failed, " +
            "AVG(m.marks_obtained) AS average_marks " +
            "FROM marks m " +
            "JOIN student s ON m.student_id = s.student_id " +
            "JOIN exam e ON m.exam_id = e.exam_id " +
            "WHERE s.class_id = ? AND m.subject_id = ? AND m.exam_id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setInt(1, classId);
            ps.setInt(2, subjectId);
            ps.setInt(3, examId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Class Analytics:");
                System.out.println("Total Students: " + rs.getInt("total_students"));
                System.out.println("Passed: " + rs.getInt("passed"));
                System.out.println("Failed: " + rs.getInt("failed"));
                System.out.println("Average Marks: " + rs.getDouble("average_marks"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
