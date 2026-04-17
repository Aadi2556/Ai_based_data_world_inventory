package com.example.demo.dto;

import java.util.List;

public class RepairLogResponse {
    private Long id;
    private Long machineId;
    private String machineName;
    private String machineModel;
    private Long scheduleId;
    private String scheduleTitle;
    private String title;
    private String problemDescription;
    private String repairAction;
    private String severity;
    private String status;
    private String reportedDate;
    private String resolvedDate;
    private String technician;
    private String partsReplaced;
    private String repairCost;
    private Integer downtimeHours;
    private String notes;
    private List<String> documents;
    private String loggedByUsername;
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
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public String getScheduleTitle() { return scheduleTitle; }
    public void setScheduleTitle(String scheduleTitle) { this.scheduleTitle = scheduleTitle; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
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
    public List<String> getDocuments() { return documents; }
    public void setDocuments(List<String> documents) { this.documents = documents; }
    public String getLoggedByUsername() { return loggedByUsername; }
    public void setLoggedByUsername(String loggedByUsername) { this.loggedByUsername = loggedByUsername; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}