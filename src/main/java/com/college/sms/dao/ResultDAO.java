package com.college.sms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.college.sms.util.DBConnection;

public class ResultDAO {

     
    public void viewPassFailBySubjectAndExam(int subjectId, int examId) {

        String sql =
            "SELECT s.roll_no, s.name, m.marks_obtained, e.pass_marks " +
            "FROM marks m " +
            "JOIN student s ON m.student_id = s.student_id " +
            "JOIN exam e ON m.exam_id = e.exam_id " +
            "WHERE m.subject_id = ? AND m.exam_id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setInt(1, subjectId);
            ps.setInt(2, examId);

            ResultSet rs = ps.executeQuery();

            System.out.println("Results:");
            while (rs.next()) {

                int marks = rs.getInt("marks_obtained");
                int passMarks = rs.getInt("pass_marks");

                String status = (marks >= passMarks) ? "PASS" : "FAIL";

                System.out.println(
                    rs.getString("roll_no") + " | " +
                    rs.getString("name") + " | " +
                    marks + " | " +
                    status
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
