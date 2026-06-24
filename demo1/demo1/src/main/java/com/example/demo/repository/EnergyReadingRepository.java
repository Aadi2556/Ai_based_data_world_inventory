package com.example.demo.repository;

import com.example.demo.model.EnergyReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnergyReadingRepository extends JpaRepository<EnergyReading, Long> {

    // All readings ordered newest first
    List<EnergyReading> findAllByOrderByReadingTimestampDesc();

    // By machine name (search)
    List<EnergyReading> findByMachineNameContainingIgnoreCaseOrderByReadingTimestampDesc(String name);

    // By machine status
    List<EnergyReading> findByMachineStatusOrderByReadingTimestampDesc(String status);

    // Date range
    List<EnergyReading> findByReadingTimestampBetweenOrderByReadingTimestampDesc(
            LocalDateTime from, LocalDateTime to);

    // Machine + date range
    List<EnergyReading> findByMachineNameAndReadingTimestampBetweenOrderByReadingTimestampDesc(
            String machineName, LocalDateTime from, LocalDateTime to);

    // ── Analytics queries ──────────────────────────────────────────────

    // Total kWh in a time window
    @Query("SELECT COALESCE(SUM(e.energyKwh), 0) FROM EnergyReading e WHERE e.readingTimestamp BETWEEN :from AND :to")
    Double sumEnergyBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Total cost in a time window
    @Query("SELECT COALESCE(SUM(e.totalCost), 0) FROM EnergyReading e WHERE e.readingTimestamp BETWEEN :from AND :to")
    Double sumCostBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Total runtime (minutes) in a time window
    @Query("SELECT COALESCE(SUM(e.runtimeMinutes), 0) FROM EnergyReading e WHERE e.readingTimestamp BETWEEN :from AND :to")
    Long sumRuntimeBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Per-machine energy totals (for bar chart)
    @Query("SELECT e.machineName, SUM(e.energyKwh), SUM(e.runtimeMinutes), SUM(e.totalCost) " +
            "FROM EnergyReading e " +
            "WHERE e.readingTimestamp BETWEEN :from AND :to " +
            "GROUP BY e.machineName ORDER BY SUM(e.energyKwh) DESC")
    List<Object[]> energyPerMachine(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Hourly energy trend (for line chart) — grouped by date + hour
    @Query(value = "SELECT DATE_FORMAT(reading_timestamp, '%Y-%m-%d %H:00') as period, " +
            "SUM(energy_kwh) as total_kwh, SUM(total_cost) as total_cost " +
            "FROM energy_readings " +
            "WHERE reading_timestamp BETWEEN :from AND :to " +
            "GROUP BY period ORDER BY period ASC",
            nativeQuery = true)
    List<Object[]> hourlyTrend(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Daily energy trend (for line chart over longer periods)
    @Query(value = "SELECT DATE(reading_timestamp) as day, " +
            "SUM(energy_kwh) as total_kwh, SUM(total_cost) as total_cost " +
            "FROM energy_readings " +
            "WHERE reading_timestamp BETWEEN :from AND :to " +
            "GROUP BY day ORDER BY day ASC",
            nativeQuery = true)
    List<Object[]> dailyTrend(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Machine status counts (for pie / donut)
    @Query("SELECT e.machineStatus, COUNT(DISTINCT e.machineName) FROM EnergyReading e " +
            "WHERE e.readingTimestamp = (SELECT MAX(e2.readingTimestamp) FROM EnergyReading e2 WHERE e2.machineName = e.machineName) " +
            "GROUP BY e.machineStatus")
    List<Object[]> latestMachineStatusCounts();

    // Most recent reading per machine (for live dashboard tiles)
    @Query(value = "SELECT * FROM energy_readings e1 " +
            "WHERE reading_timestamp = (SELECT MAX(e2.reading_timestamp) FROM energy_readings e2 WHERE e2.machine_name = e1.machine_name) " +
            "ORDER BY machine_name ASC",
            nativeQuery = true)
    List<EnergyReading> latestReadingPerMachine();

    // Count distinct machines
    @Query("SELECT COUNT(DISTINCT e.machineName) FROM EnergyReading e")
    long countDistinctMachines();

    // Average power factor
    @Query("SELECT COALESCE(AVG(e.powerFactor), 0) FROM EnergyReading e WHERE e.readingTimestamp BETWEEN :from AND :to")
    Double avgPowerFactor(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}