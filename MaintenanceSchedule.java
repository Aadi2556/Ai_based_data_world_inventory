package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_schedules")
public class MaintenanceSchedule {

    public enum Status {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    public enum MaintenanceType {
        PREVENTIVE, CORRECTIVE, INSPECTION, EMERGENCY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Machine is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @NotBlank(message = "Title is required")
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Maintenance type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false)
    private MaintenanceType maintenanceType;

    @NotNull(message = "Scheduled date is required")
    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.SCHEDULED;

    @Column(name = "assigned_technician")
    private String assignedTechnician;

    @Column(name = "estimated_duration_hours")
    private Integer estimatedDurationHours;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    private Admin createdBy;

    // Added to support Manager role creating schedules
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_manager")
    private Manager createdByManager;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public MaintenanceSchedule() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Machine getMachine() { return machine; }
    public void setMachine(Machine machine) { this.machine = machine; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public MaintenanceType getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(MaintenanceType maintenanceType) { this.maintenanceType = maintenanceType; }

    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAssignedTechnician() { return assignedTechnician; }
    public void setAssignedTechnician(String assignedTechnician) { this.assignedTechnician = assignedTechnician; }

    public Integer getEstimatedDurationHours() { return estimatedDurationHours; }
    public void setEstimatedDurationHours(Integer estimatedDurationHours) { this.estimatedDurationHours = estimatedDurationHours; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Admin getCreatedBy() { return createdBy; }
    public void setCreatedBy(Admin createdBy) { this.createdBy = createdBy; }

    public Manager getCreatedByManager() { return createdByManager; }
    public void setCreatedByManager(Manager createdByManager) { this.createdByManager = createdByManager; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}