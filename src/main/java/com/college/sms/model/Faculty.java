package com.college.sms.model;

public class Faculty {
    private int facultyId;
    private String name;
    private String email;
    private String password; // Optional: only needed for certain operations

    // Constructor for authentication (minimal fields)
    public Faculty(int facultyId, String name, String email) {
        this.facultyId = facultyId;
        this.name = name;
        this.email = email;
    }

    // Full constructor
    public Faculty(int facultyId, String name, String email, String password) {
        this.facultyId = facultyId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters
    public int getFacultyId() {
        return facultyId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters (optional but useful)
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "facultyId=" + facultyId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}