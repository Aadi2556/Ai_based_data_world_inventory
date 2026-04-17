package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RepairLogRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Machine ID is required")
    private Long machineId;

    private Long scheduleId;             // optional link to a schedule

    private String problemDescription;
    private String repairAction;

    private String severity;             // LOW, MEDIUM, HIGH, CRITICAL
    private String status;               // OPEN, IN_PROGRESS, RESOLVED, CLOSED

    private String reportedDate;         // ISO datetime string
    private String resolvedDate;

    private String technician;
    private String partsReplaced;
    private String repairCost;           // string for safe parsing
    private Integer downtimeHours;
    private String notes;
    private String documents;            // comma-separated filenames

    // Getters & Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getMachineId() { return machineId; }
    public void setMachineId(Long machineId) { this.machineId = machineId; }

    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }

    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

    public String getRepairAction() { return repairAction; }
    public void setRepairAction(String repairAction) { this.repairAction = repairAction; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReportedDate() { return reportedDate; }
    public void setReportedDate(String reportedDate) { this.reportedDate = reportedDate; }

    public String getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(String resolvedDate) { this.resolvedDate = resolvedDate; }

    public String getTechnician() { return technician; }
    public void setTechnician(String technician) { this.technician = technician; }

    public String getPartsReplaced() { return partsReplaced; }
    public void setPartsReplaced(String partsReplaced) { this.partsReplaced = partsReplaced; }

    public String getRepairCost() { return repairCost; }
    public void setRepairCost(String repairCost) { this.repairCost = repairCost; }

    public Integer getDowntimeHours() { return downtimeHours; }
    public void setDowntimeHours(Integer downtimeHours) { this.downtimeHours = downtimeHours; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDocuments() { return documents; }
    public void setDocuments(String documents) { this.documents = documents; }
}