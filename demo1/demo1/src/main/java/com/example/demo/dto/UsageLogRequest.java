package com.example.demo.dto;

import java.time.LocalDate;

public class UsageLogRequest {
    private Long materialId;
    private String logType;   // USAGE, WASTAGE, RESTOCK, ADJUSTMENT
    private Double quantity;
    private String jobReference;
    private String wastageReason;
    private String notes;
    private String loggedBy;
    private LocalDate logDate;

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getLogType() { return logType; }
    public void setLogType(String logType) { this.logType = logType; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getJobReference() { return jobReference; }
    public void setJobReference(String jobReference) { this.jobReference = jobReference; }
    public String getWastageReason() { return wastageReason; }
    public void setWastageReason(String wastageReason) { this.wastageReason = wastageReason; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getLoggedBy() { return loggedBy; }
    public void setLoggedBy(String loggedBy) { this.loggedBy = loggedBy; }
    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
}