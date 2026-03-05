//package com.college.sms.model;
//
//public class Student {
//
//    private int studentId;
//    private String rollNo;
//    private String name;
//    private int classId;
////    --
//    private String parentMobile;
//    private String parentEmail;
//    private Boolean consentToCommunicate; 
////    --
//
//    public Student(int studentId, String rollNo, String name, int classId) {
//        this.studentId = studentId;
//        this.rollNo = rollNo;
//        this.name = name;
//        this.classId = classId;
//    }
//
//    public int getStudentId() {
//        return studentId;
//    }
//
//    public String getRollNo() {
//        return rollNo;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public int getClassId() {
//        return classId;
//    }
//    
////    --
//    
//    public String getParentMobile() { 
//        return parentMobile != null ? parentMobile.trim() : ""; 
//    }
//    public void setParentMobile(String parentMobile) { 
//        this.parentMobile = parentMobile != null ? parentMobile.trim() : null; 
//    }
//
//    public String getParentEmail() { 
//        return parentEmail != null ? parentEmail.trim() : ""; 
//    }
//    public void setParentEmail(String parentEmail) { 
//        this.parentEmail = parentEmail != null ? parentEmail.trim() : null; 
//    }
//
//    public Boolean getConsentToCommunicate() { 
//        return consentToCommunicate != null ? consentToCommunicate : true; // Default TRUE
//    }
//    public void setConsentToCommunicate(Boolean consentToCommunicate) { 
//        this.consentToCommunicate = consentToCommunicate; 
//    }
//    
////    --
//}

package com.college.sms.model;

public class Student {

    private int studentId;
    private String rollNo;
    private String name;
    private int classId;
    
    // Parent contact details
    private String parentMobile;
    private String parentEmail;
    private Boolean consentToCommunicate;

    // Default constructor (required for reflection/ORM)
    public Student() {
        this.consentToCommunicate = true; // Default value
    }

    // Parameterized constructor for basic student data
    public Student(int studentId, String rollNo, String name, int classId) {
        this.studentId = studentId;
        this.rollNo = rollNo;
        this.name = name;
        this.classId = classId;
        this.consentToCommunicate = true;
    }

    // Getters
    public int getStudentId() {
        return studentId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getName() {
        return name;
    }

    public int getClassId() {
        return classId;
    }

    public String getParentMobile() {
        return (parentMobile != null && !parentMobile.trim().isEmpty()) ? parentMobile.trim() : "";
    }

    public String getParentEmail() {
        return (parentEmail != null && !parentEmail.trim().isEmpty()) ? parentEmail.trim() : "";
    }

    public Boolean getConsentToCommunicate() {
        return (consentToCommunicate != null) ? consentToCommunicate : true;
    }

    // Setters
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = (rollNo != null) ? rollNo.trim() : null;
    }

    public void setName(String name) {
        this.name = (name != null) ? name.trim() : null;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public void setParentMobile(String parentMobile) {
        this.parentMobile = (parentMobile != null && !parentMobile.trim().isEmpty()) ? parentMobile.trim() : null;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = (parentEmail != null && !parentEmail.trim().isEmpty()) ? parentEmail.trim() : null;
    }

    public void setConsentToCommunicate(Boolean consentToCommunicate) {
        this.consentToCommunicate = consentToCommunicate;
    }
}