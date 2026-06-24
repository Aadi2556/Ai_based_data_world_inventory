package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.time.LocalDateTime;

public class EnergyReadingRequest {

    @NotBlank(message = "Machine name is required")
    private String machineName;

    private String machineId;

    @NotNull(message = "Energy (kWh) is required")
    @DecimalMin(value = "0.0", message = "Energy must be non-negative")
    private Double energyKwh;

    private Double powerKw;
    private Double voltage;
    private Double currentAmps;
    private Double powerFactor;
    private String machineStatus;  // RUNNING, IDLE, STOPPED, MAINTENANCE
    private Integer runtimeMinutes;
    private Double costPerKwh;
    private LocalDateTime readingTimestamp;
    private String notes;

    // Getters & Setters
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

    public LocalDateTime getReadingTimestamp() { return readingTimestamp; }
    public void setReadingTimestamp(LocalDateTime readingTimestamp) { this.readingTimestamp = readingTimestamp; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}