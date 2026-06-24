package com.example.demo.dto;

import jakarta.validation.constraints.*;

public class ManagerUpdateRequest {

    @Size(min = 2, max = 50) private String firstName;
    @Size(min = 2, max = 50) private String lastName;
    @Email private String email;
    @Pattern(regexp = "^[0-9]{10,15}$") private String phone;
    private String managedDepartment;
    private String jobTitle;
    private Integer experienceYears;
    private String certifications;
    private String notes;
    private String currentPassword;
    @Size(min = 6, max = 100) private String newPassword;

    // Getters and setters (omitted for brevity, same pattern as StaffOperatorUpdateRequest)
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { lastName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }
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
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String v) { currentPassword = v; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String v) { newPassword = v; }
}