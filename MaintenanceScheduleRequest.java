package com.example.demo.dto;

// ─── Request DTOs ────────────────────────────────────────────────────────────

// MaintenanceScheduleRequest.java
// Use as: CreateMaintenanceRequest and UpdateMaintenanceRequest

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MaintenanceScheduleRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Machine ID is required")
    private Long machineId;

    @NotNull(message = "Maintenance type is required")
    private String maintenanceType; // PREVENTIVE, CORRECTIVE, INSPECTION, EMERGENCY

    @NotBlank(message = "Scheduled date is required")
    private String scheduledDate; // ISO string: "2025-07-15T10:00:00"

    private String assignedTechnician;
    private Integer estimatedDurationHours;
    private String notes;
    private String status; // used for updates only

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getMachineId() { return machineId; }
    public void setMachineId(Long machineId) { this.machineId = machineId; }
    public String getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }
    public String getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }
    public String getAssignedTechnician() { return assignedTechnician; }
    public void setAssignedTechnician(String assignedTechnician) { this.assignedTechnician = assignedTechnician; }
    public Integer getEstimatedDurationHours() { return estimatedDurationHours; }
    public void setEstimatedDurationHours(Integer estimatedDurationHours) { this.estimatedDurationHours = estimatedDurationHours; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}