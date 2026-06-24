package com.example.demo.dto;

import jakarta.validation.constraints.*;

public class TechnicianUpdateRequest {

    @Size(min = 2, max = 50)
    private String firstName;

    @Size(min = 2, max = 50)
    private String lastName;

    @Email
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$")
    private String phone;

    private String specialization;
    private Integer experienceYears;
    private String certifications;
    private String availabilityStatus;   // AVAILABLE | BUSY | ON_LEAVE | INACTIVE
    private String notes;

    // password change
    private String currentPassword;

    @Size(min = 6, max = 100)
    private String newPassword;

    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String v) { this.specialization = v; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer v) { this.experienceYears = v; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String v) { this.certifications = v; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String v) { this.availabilityStatus = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String v) { this.currentPassword = v; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String v) { this.newPassword = v; }
}