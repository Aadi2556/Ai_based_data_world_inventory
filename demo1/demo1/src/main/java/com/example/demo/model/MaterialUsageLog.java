package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "material_usage_logs")
public class MaterialUsageLog {

    public enum LogType {
        USAGE,       // Normal consumption during jobs
        WASTAGE,     // Wasted/spoiled material
        RESTOCK,     // Stock added/received
        ADJUSTMENT   // Manual stock correction
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogType logType;

    @Column(nullable = false)
    private Double quantity;

    @Column(name = "stock_before")
    private Double stockBefore;

    @Column(name = "stock_after")
    private Double stockAfter;

    @Column(name = "job_reference")
    private String jobReference;

    @Column(name = "wastage_reason")
    private String wastageReason;

    private String notes;

    @Column(name = "logged_by")
    private String loggedBy;

    @Column(name = "log_date")
    private LocalDate logDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (logDate == null) logDate = LocalDate.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }
    public LogType getLogType() { return logType; }
    public void setLogType(LogType logType) { this.logType = logType; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public Double getStockBefore() { return stockBefore; }
    public void setStockBefore(Double stockBefore) { this.stockBefore = stockBefore; }
    public Double getStockAfter() { return stockAfter; }
    public void setStockAfter(Double stockAfter) { this.stockAfter = stockAfter; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}