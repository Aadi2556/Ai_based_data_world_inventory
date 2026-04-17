package com.example.demo.dto;

public class MaintenanceScheduleResponse {
    private Long id;
    private Long machineId;
    private String machineName;
    private String machineModel;
    private String title;
    private String description;
    private String maintenanceType;
    private String scheduledDate;
    private String completedDate;
    private String status;
    private String assignedTechnician;
    private Integer estimatedDurationHours;
    private String notes;
    private String createdByUsername;
    private String createdAt;
    private String updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMachineId() { return machineId; }
    public void setMachineId(Long machineId) { this.machineId = machineId; }
    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }
    public String getMachineModel() { return machineModel; }
    public void setMachineModel(String machineModel) { this.machineModel = machineModel; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }
    public String getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }
    public String getCompletedDate() { return completedDate; }
    public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAssignedTechnician() { return assignedTechnician; }
    public void setAssignedTechnician(String assignedTechnician) { this.assignedTechnician = assignedTechnician; }
    public Integer getEstimatedDurationHours() { return estimatedDurationHours; }
    public void setEstimatedDurationHours(Integer estimatedDurationHours) { this.estimatedDurationHours = estimatedDurationHours; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}