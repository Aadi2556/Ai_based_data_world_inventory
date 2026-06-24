package com.example.demo.dto;

public class TechnicianProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    private String employeeId;
    private String specialization;
    private Integer experienceYears;
    private String certifications;
    private String availabilityStatus;
    private String profileImage;
    private String notes;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;

    // workload summary (populated by admin-facing service)
    private Integer totalAssignedSchedules;
    private Integer completedSchedules;
    private Integer openRepairs;
    private Integer resolvedRepairs;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String v) { this.employeeId = v; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String v) { this.specialization = v; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer v) { this.experienceYears = v; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String v) { this.certifications = v; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String v) { this.availabilityStatus = v; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String v) { this.profileImage = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean v) { this.isActive = v; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String v) { this.createdAt = v; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String v) { this.updatedAt = v; }
    public Integer getTotalAssignedSchedules() { return totalAssignedSchedules; }
    public void setTotalAssignedSchedules(Integer v) { this.totalAssignedSchedules = v; }
    public Integer getCompletedSchedules() { return completedSchedules; }
    public void setCompletedSchedules(Integer v) { this.completedSchedules = v; }
    public Integer getOpenRepairs() { return openRepairs; }
    public void setOpenRepairs(Integer v) { this.openRepairs = v; }
    public Integer getResolvedRepairs() { return resolvedRepairs; }
    public void setResolvedRepairs(Integer v) { this.resolvedRepairs = v; }
}