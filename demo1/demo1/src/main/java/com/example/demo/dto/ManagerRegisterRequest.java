package com.example.demo.dto;

import jakarta.validation.constraints.*;

public class ManagerRegisterRequest {

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
    private String managedDepartment;   // enum name: OPERATIONS, WAREHOUSE, etc.
    private String jobTitle;
    private Integer experienceYears;
    private String certifications;
    private String notes;

    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { lastName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { password = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }
    public String getUsername() { return username; }
    public void setUsername(String v) { username = v; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String v) { employeeId = v; }
    public String getManagedDepartment() { return managedDepartment; }
    public void setManagedDepartment(String v) { managedDepartment = v; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String v) { jobTitle = v; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer v) { experienceYears = v; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String v) { certifications = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { notes = v; }
}