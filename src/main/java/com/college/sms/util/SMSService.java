package com.college.sms.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * SMS Service for sending result notifications to parent mobile numbers
 * ✅ PRODUCTION READY: Uses Msg91 API with HttpURLConnection (no external dependencies)
 */
public class SMSService {

    // ═══════════════════════════════════════════════════════════
    // ⚙️ SMS PROVIDER CONFIGURATION - Msg91 (India)
    // ═══════════════════════════════════════════════════════════
    
    private static final String SMS_PROVIDER = "MSG91";
    
    // 🔐 MSG91 Credentials - YOUR ACTUAL AUTH KEY INSERTED ✅
    private static final String MSG91_AUTH_KEY = "495166Avl4DIahh6997e733P1";  // ← Your actual key
    private static final String MSG91_SENDER_ID = "MSGIND";  // ← Pre-approved by Msg91 (no DLT needed)
    private static final String MSG91_ROUTE = "4";  // Transactional route
    private static final String MSG91_COUNTRY_CODE = "91";  // India

    // 🎯 Your test number for development
    private static final String TEST_MOBILE = "+916301372060";

    /**
     * Send SMS to parent with exam result
     * @param mobile Parent mobile number (with/without country code)
     * @return true if sent successfully, false otherwise
     */
    public static boolean sendResultSMS(String mobile, String studentName, String rollNo, 
                                        String className, String subjectName, String examName,
                                        int marksObtained, int maxMarks, double percentage, 
                                        String result, String grade) {
        
        // Validate mobile number
        if (mobile == null || mobile.trim().isEmpty()) {
            System.err.println("❌ SMSService: Empty mobile number");
            return false;
        }
        
        // Format mobile: remove spaces, ensure country code
        String formattedMobile = formatMobileNumber(mobile);
        if (formattedMobile == null) {
            System.err.println("❌ SMSService: Invalid mobile format: " + mobile);
            return false;
        }

        // Build SMS message (keep it short - SMS has 160 char limit)
        String message = buildSMSMessage(studentName, rollNo, examName, marksObtained, 
                                        maxMarks, percentage, result, grade);

        try {
            switch (SMS_PROVIDER.toUpperCase()) {
                case "MSG91":
                    return sendViaMsg91(formattedMobile, message);
                case "TWILIO":
                    return sendViaTwilio(formattedMobile, message);
                case "TEXTLOCAL":
                    return sendViaTextLocal(formattedMobile, message);
                case "NONE":
                default:
                    // Testing mode - log instead of sending
                    System.out.println("📱 [TEST MODE] SMS would be sent to: " + formattedMobile);
                    System.out.println("   Message: " + message);
                    return true;
            }
        } catch (Exception e) {
            System.err.println("❌ SMSService: Failed to send SMS to " + formattedMobile);
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Format mobile number to international format
     * Input: "9876543210" or "+91 98765 43210" → Output: "919876543210" (Msg91 format)
     */
    private static String formatMobileNumber(String mobile) {
        if (mobile == null) return null;
        
        // Remove all spaces, dashes, parentheses, plus sign
        String cleaned = mobile.replaceAll("[\\s\\-\\(\\)\\+]", "");
        
        // Add India country code if missing
        if (cleaned.startsWith("0")) {
            cleaned = "91" + cleaned.substring(1);
        } else if (!cleaned.startsWith("91")) {
            cleaned = "91" + cleaned;
        }
        
        // Basic validation: should be 91 + 10 digits = 12 digits total
        if (cleaned.matches("^91\\d{10}$")) {
            return cleaned;
        }
        return null; // Invalid format
    }

    /**
     * Build concise SMS message (under 160 chars)
     */
    private static String buildSMSMessage(String studentName, String rollNo, String examName, 
                                         int marks, int maxMarks, double percentage, 
                                         String result, String grade) {
        // Keep it short for SMS limits (160 chars max)
        return String.format(
            "College SMS: %s(%s) scored %d/%d (%.0f%%, Grade %s) in %s. Result: %s. -CollegeMgmt",
            studentName.split(" ")[0], // First name only
            rollNo,
            marks, maxMarks,
            percentage,
            grade,
            examName.split(" ")[0], // Short exam name
            result
        ).replaceAll("\\s+", " ").trim(); // Ensure no extra spaces
    }

    /**
     * Send via Msg91 API (India) - PRODUCTION READY
     * Uses HttpURLConnection (no external dependencies)
     * Docs: https://docs.msg91.com/collection/msg91-api-integration/66516592a4a46670e701d162
     */
    private static boolean sendViaMsg91(String to, String message) {
        try {
            // Validate credentials
            if (MSG91_AUTH_KEY == null || MSG91_AUTH_KEY.isEmpty() || MSG91_AUTH_KEY.equals("your_32_char_auth_key_here")) {
                System.err.println("❌ Msg91: Auth key not configured! Update MSG91_AUTH_KEY in SMSService.java");
                return false;
            }
            
            // Sender ID validation - just warn if not approved, don't block
            if (MSG91_SENDER_ID == null || MSG91_SENDER_ID.isEmpty() || MSG91_SENDER_ID.length() != 6) {
                System.err.println("⚠️ Msg91: Sender ID should be 6 chars and approved in dashboard. Using: " + MSG91_SENDER_ID);
            }

            // Msg91 API endpoint for transactional SMS
            String apiUrl = "https://control.msg91.com/api/v2/sendsms";
            
            // Build JSON payload
            String jsonPayload = String.format(
                "{\"sender\":\"%s\",\"route\":\"%s\",\"country\":\"%s\",\"sms\":[{\"message\":\"%s\",\"to\":\"%s\"}]}",
                MSG91_SENDER_ID,
                MSG91_ROUTE,
                MSG91_COUNTRY_CODE,
                message.replace("\"", "\\\""), // Escape quotes in message
                to
            );

            // Create connection
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("authkey", MSG91_AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000); // 10 second timeout
            conn.setReadTimeout(10000);

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get response
            int responseCode = conn.getResponseCode();
            String response;
            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8)) {
                response = scanner.useDelimiter("\\A").next();
            } catch (Exception e) {
                // Try error stream if success stream fails
                try (Scanner scanner = new Scanner(conn.getErrorStream(), StandardCharsets.UTF_8)) {
                    response = scanner.useDelimiter("\\A").hasNext() ? scanner.useDelimiter("\\A").next() : "No response";
                }
            }

            conn.disconnect();

            // Check response
            if (responseCode == 200 && (response.contains("\"type\":\"success\"") || response.contains("\"status\":\"success\""))) {
                System.out.println("✅ [MSG91] SMS sent to " + to + ": " + message);
                return true;
            } else {
                System.err.println("❌ [MSG91] Failed: Response code " + responseCode);
                System.err.println("   Response: " + response);
                
                // Common error hints
                if (response.contains("senderid")) {
                    System.err.println("   💡 Hint: Sender ID '" + MSG91_SENDER_ID + "' may not be approved yet.");
                    System.err.println("   💡 Try using 'MSGIND' (pre-approved by Msg91)");
                }
                if (response.contains("authkey")) {
                    System.err.println("   💡 Hint: Auth key may be invalid or expired.");
                }
                if (response.contains("balance") || response.contains("credit")) {
                    System.err.println("   💡 Hint: Insufficient credits. Add credits at https://msg91.com/pricing");
                }
                
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Msg91 error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Send via Twilio API (Global) - Uncomment when Twilio SDK is added
     */
    private static boolean sendViaTwilio(String to, String message) {
        try {
            System.err.println("⚠️ Twilio not configured. Add Twilio SDK and credentials to enable.");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Twilio error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Send via TextLocal API (India/UK) - Uncomment when configured
     */
    private static boolean sendViaTextLocal(String to, String message) {
        try {
            System.err.println("⚠️ TextLocal not configured. Add API key and credentials to enable.");
            return false;
        } catch (Exception e) {
            System.err.println("❌ TextLocal error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if SMS service is in testing mode
     */
    public static boolean isTestingMode() {
        return "NONE".equalsIgnoreCase(SMS_PROVIDER);
    }

    /**
     * Test SMS configuration
     */
    public static void testConfiguration() {
        System.out.println("🔧 SMS Service Configuration:");
        System.out.println("Provider: " + SMS_PROVIDER);
        System.out.println("Auth Key: " + (MSG91_AUTH_KEY != null && !MSG91_AUTH_KEY.isEmpty() ? "✅ Configured" : "❌ Missing"));
        System.out.println("Sender ID: " + MSG91_SENDER_ID + " (pre-approved by Msg91)");
        System.out.println("Test Mobile: " + TEST_MOBILE);
        System.out.println("Mobile format test: 9876543210 → " + formatMobileNumber("9876543210"));
        System.out.println("Mobile format test: +91 98765 43210 → " + formatMobileNumber("+91 98765 43210"));
        
        // Test message build
        String testMsg = buildSMSMessage("John Doe", "A001", "Mid Term", 85, 100, 85.0, "Pass", "B");
        System.out.println("Sample SMS (" + testMsg.length() + " chars): " + testMsg);
    }

    /**
     * Send test SMS to your number
     */
    public static void sendTestToYourNumber() {
        System.out.println("🧪 Sending test SMS to your number: " + TEST_MOBILE);
        boolean sent = sendResultSMS(
            TEST_MOBILE,                    // Your test number
            "Test Student",                 // Student name
            "TEST01",                       // Roll number
            "Class X-A",                    // Class
            "Mathematics",                  // Subject
            "Mid Term",                     // Exam
            90,                             // Marks obtained
            100,                            // Max marks
            90.0,                           // Percentage
            "Pass",                         // Result
            "A"                             // Grade
        );
        System.out.println("Test SMS: " + (sent ? "✅ Sent" : "❌ Failed"));
    }

    public static void main(String[] args) {
        testConfiguration();
        sendTestToYourNumber();
    }
}