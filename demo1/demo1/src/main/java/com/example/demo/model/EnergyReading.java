package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "energy_readings")
public class EnergyReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to machine by name/id (denormalized for simplicity, matches Machine model pattern)
    @NotBlank(message = "Machine name is required")
    @Column(name = "machine_name", nullable = false)
    private String machineName;

    @Column(name = "machine_id")
    private String machineId;

    // Energy consumption in kWh for this reading period
    @NotNull(message = "Energy consumed is required")
    @Column(name = "energy_kwh", nullable = false)
    private Double energyKwh;

    // Power demand in kW at moment of reading
    @Column(name = "power_kw")
    private Double powerKw;

    // Voltage (V)
    @Column(name = "voltage")
    private Double voltage;

    // Current (A)
    @Column(name = "current_amps")
    private Double currentAmps;

    // Power factor (0-1)
    @Column(name = "power_factor")
    private Double powerFactor;

    // Machine running status at time of reading
    @Column(name = "machine_status")
    private String machineStatus;  // RUNNING, IDLE, STOPPED, MAINTENANCE

    // Runtime in minutes for this period
    @Column(name = "runtime_minutes")
    private Integer runtimeMinutes;

    // Cost per kWh at time of reading
    @Column(name = "cost_per_kwh")
    private Double costPerKwh;

    // Computed cost = energyKwh * costPerKwh
    @Column(name = "total_cost")
    private Double totalCost;

    // Reading timestamp
    @NotNull
    @Column(name = "reading_timestamp", nullable = false)
    private LocalDateTime readingTimestamp;

    // Who logged this reading
    @Column(name = "recorded_by")
    private String recordedBy;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (readingTimestamp == null) readingTimestamp = LocalDateTime.now();
        // Auto-calculate total cost
        if (energyKwh != null && costPerKwh != null) {
            totalCost = energyKwh * costPerKwh;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (energyKwh != null && costPerKwh != null) {
            totalCost = energyKwh * costPerKwh;
        }
    }

    // ── Getters & Setters ──────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }

    public String getMachineId() { return machineId; }
    public void setMachineId(String machineId) { this.machineId = machineId; }

    public Double getEnergyKwh() { return energyKwh; }
    public void setEnergyKwh(Double energyKwh) { this.energyKwh = energyKwh; }

    public Double getPowerKw() { return powerKw; }
    public void setPowerKw(Double powerKw) { this.powerKw = powerKw; }

    public Double getVoltage() { return voltage; }
    public void setVoltage(Double voltage) { this.voltage = voltage; }

    public Double getCurrentAmps() { return currentAmps; }
    public void setCurrentAmps(Double currentAmps) { this.currentAmps = currentAmps; }

    public Double getPowerFactor() { return powerFactor; }
    public void setPowerFactor(Double powerFactor) { this.powerFactor = powerFactor; }

    public String getMachineStatus() { return machineStatus; }
    public void setMachineStatus(String machineStatus) { this.machineStatus = machineStatus; }

    public Integer getRuntimeMinutes() { return runtimeMinutes; }
    public void setRuntimeMinutes(Integer runtimeMinutes) { this.runtimeMinutes = runtimeMinutes; }

    public Double getCostPerKwh() { return costPerKwh; }
    public void setCostPerKwh(Double costPerKwh) { this.costPerKwh = costPerKwh; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public LocalDateTime getReadingTimestamp() { return readingTimestamp; }
    public void setReadingTimestamp(LocalDateTime readingTimestamp) { this.readingTimestamp = readingTimestamp; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}