package com.example.demo.dto;

import jakarta.validation.constraints.*;

public class StaffOperatorRegisterRequest {

    @NotBlank @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6, max = 100)
    private String password;

    @NotBlank @Pattern(regexp = "^[0-9]{10,15}$")
    private String phone;

    @NotBlank @Size(min = 4, max = 30)
    private String username;

    private String employeeId;
    private String department;      // OPERATIONS | WAREHOUSE | LOGISTICS | PRODUCTION | GENERAL
    private String jobTitle;
    private String shift;           // Morning | Evening | Night
    private Integer experienceYears;
    private String certifications;
    private String notes;

    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String v) { this.employeeId = v; }
    public String getDepartment() { return department; }
    public void setDepartment(String v) { this.department = v; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String v) { this.jobTitle = v; }
    public String getShift() { return shift; }
    public void setShift(String v) { this.shift = v; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer v) { this.experienceYears = v; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String v) { this.certifications = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
}