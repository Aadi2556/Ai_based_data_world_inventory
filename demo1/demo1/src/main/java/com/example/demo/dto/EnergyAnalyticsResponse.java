package com.example.demo.dto;

import java.util.List;
import java.util.Map;

public class EnergyAnalyticsResponse {

    // ── KPI Tiles ──────────────────────────────────────────────────────
    private Double totalEnergyKwh;
    private Double totalCost;
    private Long   totalRuntimeMinutes;
    private Double avgPowerFactor;
    private Long   totalMachines;
    private Long   runningMachines;
    private Long   idleMachines;
    private Long   stoppedMachines;

    // Period for which stats are calculated
    private String period;   // e.g. "TODAY", "WEEK", "MONTH"

    // ── Chart data ─────────────────────────────────────────────────────

    // Line chart: energy over time  [{label, kwh, cost}]
    private List<Map<String, Object>> trendData;

    // Bar chart: per-machine breakdown  [{machine, kwh, runtime, cost}]
    private List<Map<String, Object>> machineBreakdown;

    // Donut chart: machine status distribution  [{status, count}]
    private List<Map<String, Object>> statusDistribution;

    // ── Latest readings per machine (live tiles) ───────────────────────
    private List<EnergyReadingResponse> latestReadings;

    // Getters & Setters
    public Double getTotalEnergyKwh() { return totalEnergyKwh; }
    public void setTotalEnergyKwh(Double totalEnergyKwh) { this.totalEnergyKwh = totalEnergyKwh; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public Long getTotalRuntimeMinutes() { return totalRuntimeMinutes; }
    public void setTotalRuntimeMinutes(Long totalRuntimeMinutes) { this.totalRuntimeMinutes = totalRuntimeMinutes; }

    public Double getAvgPowerFactor() { return avgPowerFactor; }
    public void setAvgPowerFactor(Double avgPowerFactor) { this.avgPowerFactor = avgPowerFactor; }

    public Long getTotalMachines() { return totalMachines; }
    public void setTotalMachines(Long totalMachines) { this.totalMachines = totalMachines; }

    public Long getRunningMachines() { return runningMachines; }
    public void setRunningMachines(Long runningMachines) { this.runningMachines = runningMachines; }

    public Long getIdleMachines() { return idleMachines; }
    public void setIdleMachines(Long idleMachines) { this.idleMachines = idleMachines; }

    public Long getStoppedMachines() { return stoppedMachines; }
    public void setStoppedMachines(Long stoppedMachines) { this.stoppedMachines = stoppedMachines; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public List<Map<String, Object>> getTrendData() { return trendData; }
    public void setTrendData(List<Map<String, Object>> trendData) { this.trendData = trendData; }

    public List<Map<String, Object>> getMachineBreakdown() { return machineBreakdown; }
    public void setMachineBreakdown(List<Map<String, Object>> machineBreakdown) { this.machineBreakdown = machineBreakdown; }

    public List<Map<String, Object>> getStatusDistribution() { return statusDistribution; }
    public void setStatusDistribution(List<Map<String, Object>> statusDistribution) { this.statusDistribution = statusDistribution; }

    public List<EnergyReadingResponse> getLatestReadings() { return latestReadings; }
    public void setLatestReadings(List<EnergyReadingResponse> latestReadings) { this.latestReadings = latestReadings; }
}