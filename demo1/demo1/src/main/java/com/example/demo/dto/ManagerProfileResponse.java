package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ManagerProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    private String employeeId;
    private String managedDepartment;
    private String jobTitle;
    private Integer experienceYears;
    private String certifications;
    private String profileImage;
    private String notes;
    @JsonProperty("active")
    private boolean isActive;
    private String createdAt;
    private String updatedAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { lastName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
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
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String v) { profileImage = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { notes = v; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean v) { isActive = v; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String v) { createdAt = v; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String v) { updatedAt = v; }
}