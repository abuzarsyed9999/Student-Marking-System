package com.college.sms.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USER = "abuzar7993@gmail.com";
    private static final String SMTP_PASS = "brtlajsaxdoodpcp"; // Generate new app password after testing

    private static final String FROM_NAME = "CollegeSMS";
    private static final String FROM_EMAIL = SMTP_USER;

    public static boolean sendResultEmail(String toEmail,
                                          String studentName,
                                          String rollNo,
                                          String className,
                                          String subjectName,
                                          String examName,
                                          int marksObtained,
                                          int maxMarks,
                                          double percentage,
                                          String result,
                                          String grade) {

        if (toEmail == null || toEmail.trim().isEmpty()) {
            System.err.println("Recipient email is empty.");
            return false;
        }

        Properties props = new Properties();

        // ✅ Core SMTP settings
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // ✅ Force modern TLS
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // ⭐ FIX FOR PKIX ERROR
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        // Optional but helps in strict networks
        props.put("mail.smtp.ssl.checkserveridentity", "true");

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER, SMTP_PASS);
                }
            });

            session.setDebug(true); // Keep true while testing

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Exam Result: " + examName + " - " + studentName);

            message.setText(buildEmailBody(studentName, rollNo, className,
                    subjectName, examName, marksObtained,
                    maxMarks, percentage, result, grade));

            Transport.send(message);

            System.out.println("✅ Email sent successfully to: " + toEmail);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Email sending failed:");
            e.printStackTrace();
            return false;
        }
    }

    private static String buildEmailBody(String studentName,
                                         String rollNo,
                                         String className,
                                         String subjectName,
                                         String examName,
                                         int marksObtained,
                                         int maxMarks,
                                         double percentage,
                                         String result,
                                         String grade) {

        return String.format(
                "Dear Parent,\n\n" +
                "This is to inform you about the exam result of your ward:\n\n" +
                "Student Name : %s\n" +
                "Roll No      : %s\n" +
                "Class        : %s\n" +
                "Subject      : %s\n" +
                "Exam         : %s\n\n" +
                "Marks        : %d / %d\n" +
                "Percentage   : %.2f%%\n" +
                "Grade        : %s\n" +
                "Result       : %s\n\n" +
                "Regards,\nCollege Management MITS ",
                studentName, rollNo, className, subjectName,
                examName, marksObtained, maxMarks,
                percentage, grade, result
        );
    }
}
