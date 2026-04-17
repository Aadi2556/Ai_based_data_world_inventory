package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "repair_logs")
public class RepairLog {

    public enum RepairStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Machine is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    // Optional link to a maintenance schedule
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id")
    private MaintenanceSchedule schedule;

    @NotBlank(message = "Title is required")
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "problem_description", columnDefinition = "TEXT")
    private String problemDescription;

    @Column(name = "repair_action", columnDefinition = "TEXT")
    private String repairAction;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity = Severity.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RepairStatus status = RepairStatus.OPEN;

    @Column(name = "reported_date", nullable = false)
    private LocalDateTime reportedDate;

    @Column(name = "resolved_date")
    private LocalDateTime resolvedDate;

    @Column(name = "technician")
    private String technician;

    @Column(name = "parts_replaced", columnDefinition = "TEXT")
    private String partsReplaced;

    @Column(name = "repair_cost", precision = 10, scale = 2)
    private BigDecimal repairCost;

    @Column(name = "downtime_hours")
    private Integer downtimeHours;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Stored as comma-separated filenames/paths
    @Column(name = "documents", columnDefinition = "TEXT")
    private String documents;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "logged_by")
    private Admin loggedBy;

    // Added to support Manager role logging repairs
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "logged_by_manager")
    private Manager loggedByManager;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (reportedDate == null) reportedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public RepairLog() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Machine getMachine() { return machine; }
    public void setMachine(Machine machine) { this.machine = machine; }

    public MaintenanceSchedule getSchedule() { return schedule; }
    public void setSchedule(MaintenanceSchedule schedule) { this.schedule = schedule; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

    public String getRepairAction() { return repairAction; }
    public void setRepairAction(String repairAction) { this.repairAction = repairAction; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public RepairStatus getStatus() { return status; }
    public void setStatus(RepairStatus status) { this.status = status; }

    public LocalDateTime getReportedDate() { return reportedDate; }
    public void setReportedDate(LocalDateTime reportedDate) { this.reportedDate = reportedDate; }

    public LocalDateTime getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(LocalDateTime resolvedDate) { this.resolvedDate = resolvedDate; }

    public String getTechnician() { return technician; }
    public void setTechnician(String technician) { this.technician = technician; }

    public String getPartsReplaced() { return partsReplaced; }
    public void setPartsReplaced(String partsReplaced) { this.partsReplaced = partsReplaced; }

    public BigDecimal getRepairCost() { return repairCost; }
    public void setRepairCost(BigDecimal repairCost) { this.repairCost = repairCost; }

    public Integer getDowntimeHours() { return downtimeHours; }
    public void setDowntimeHours(Integer downtimeHours) { this.downtimeHours = downtimeHours; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDocuments() { return documents; }
    public void setDocuments(String documents) { this.documents = documents; }

    public Admin getLoggedBy() { return loggedBy; }
    public void setLoggedBy(Admin loggedBy) { this.loggedBy = loggedBy; }

    public Manager getLoggedByManager() { return loggedByManager; }
    public void setLoggedByManager(Manager loggedByManager) { this.loggedByManager = loggedByManager; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}