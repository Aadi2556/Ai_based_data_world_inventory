package com.example.demo.service;

import com.example.demo.dto.EnergyAnalyticsResponse;
import com.example.demo.dto.EnergyReadingRequest;
import com.example.demo.dto.EnergyReadingResponse;
import com.example.demo.model.EnergyReading;
import com.example.demo.repository.EnergyReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnergyService {

    @Autowired
    private EnergyReadingRepository repository;

    // ── CREATE ─────────────────────────────────────────────────────────
    public EnergyReadingResponse create(EnergyReadingRequest req, String recordedBy) {
        EnergyReading reading = new EnergyReading();
        mapToEntity(req, reading);
        reading.setRecordedBy(recordedBy);
        if (reading.getReadingTimestamp() == null) {
            reading.setReadingTimestamp(LocalDateTime.now());
        }
        return toResponse(repository.save(reading));
    }

    // ── READ ALL ───────────────────────────────────────────────────────
    public List<EnergyReadingResponse> getAll(String search, String status,
                                              String from, String to) {
        List<EnergyReading> list;

        if (search != null && !search.isBlank()) {
            list = repository.findByMachineNameContainingIgnoreCaseOrderByReadingTimestampDesc(search);
        } else if (status != null && !status.isBlank()) {
            list = repository.findByMachineStatusOrderByReadingTimestampDesc(status.toUpperCase());
        } else if (from != null && to != null) {
            LocalDateTime fromDt = LocalDateTime.parse(from);
            LocalDateTime toDt   = LocalDateTime.parse(to);
            list = repository.findByReadingTimestampBetweenOrderByReadingTimestampDesc(fromDt, toDt);
        } else {
            list = repository.findAllByOrderByReadingTimestampDesc();
        }

        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── READ ONE ───────────────────────────────────────────────────────
    public EnergyReadingResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ── UPDATE ─────────────────────────────────────────────────────────
    public EnergyReadingResponse update(Long id, EnergyReadingRequest req) {
        EnergyReading reading = findOrThrow(id);
        mapToEntity(req, reading);
        return toResponse(repository.save(reading));
    }

    // ── DELETE ─────────────────────────────────────────────────────────
    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    // ── ANALYTICS ─────────────────────────────────────────────────────
    public EnergyAnalyticsResponse getAnalytics(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from;
        boolean useHourly;

        switch (period.toUpperCase()) {
            case "TODAY":
                from = now.toLocalDate().atStartOfDay();
                useHourly = true;
                break;
            case "WEEK":
                from = now.minusDays(6).toLocalDate().atStartOfDay();
                useHourly = false;
                break;
            case "MONTH":
                from = now.minusDays(29).toLocalDate().atStartOfDay();
                useHourly = false;
                break;
            default:  // "TODAY"
                from = now.toLocalDate().atStartOfDay();
                useHourly = true;
        }

        EnergyAnalyticsResponse resp = new EnergyAnalyticsResponse();
        resp.setPeriod(period.toUpperCase());

        // ── KPI aggregates ──
        resp.setTotalEnergyKwh(round2(repository.sumEnergyBetween(from, now)));
        resp.setTotalCost(round2(repository.sumCostBetween(from, now)));
        resp.setTotalRuntimeMinutes(repository.sumRuntimeBetween(from, now));
        resp.setAvgPowerFactor(round4(repository.avgPowerFactor(from, now)));
        resp.setTotalMachines(repository.countDistinctMachines());

        // Machine status counts from latest readings
        long running = 0, idle = 0, stopped = 0;
        List<Object[]> statusRows = repository.latestMachineStatusCounts();
        for (Object[] row : statusRows) {
            String s = row[0] != null ? row[0].toString() : "";
            long cnt = ((Number) row[1]).longValue();
            if ("RUNNING".equalsIgnoreCase(s))     running  += cnt;
            else if ("IDLE".equalsIgnoreCase(s))   idle     += cnt;
            else                                   stopped  += cnt;
        }
        resp.setRunningMachines(running);
        resp.setIdleMachines(idle);
        resp.setStoppedMachines(stopped);

        // ── Trend chart ──
        List<Object[]> trendRows = useHourly
                ? repository.hourlyTrend(from, now)
                : repository.dailyTrend(from, now);

        List<Map<String, Object>> trend = new ArrayList<>();
        for (Object[] row : trendRows) {
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("label", row[0] != null ? row[0].toString() : "");
            point.put("kwh",   row[1] != null ? round2(((Number) row[1]).doubleValue()) : 0.0);
            point.put("cost",  row[2] != null ? round2(((Number) row[2]).doubleValue()) : 0.0);
            trend.add(point);
        }
        resp.setTrendData(trend);

        // ── Per-machine breakdown ──
        List<Object[]> machineRows = repository.energyPerMachine(from, now);
        List<Map<String, Object>> machineBreakdown = new ArrayList<>();
        for (Object[] row : machineRows) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("machine",  row[0] != null ? row[0].toString() : "Unknown");
            m.put("kwh",      row[1] != null ? round2(((Number) row[1]).doubleValue()) : 0.0);
            m.put("runtime",  row[2] != null ? ((Number) row[2]).longValue() : 0L);
            m.put("cost",     row[3] != null ? round2(((Number) row[3]).doubleValue()) : 0.0);
            machineBreakdown.add(m);
        }
        resp.setMachineBreakdown(machineBreakdown);

        // ── Status distribution (donut) ──
        List<Map<String, Object>> statusDist = new ArrayList<>();
        Map<String, Long> statusMap = new LinkedHashMap<>();
        statusMap.put("RUNNING",     running);
        statusMap.put("IDLE",        idle);
        statusMap.put("STOPPED",     stopped);
        for (Map.Entry<String, Long> e : statusMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("status", e.getKey());
            item.put("count",  e.getValue());
            statusDist.add(item);
        }
        resp.setStatusDistribution(statusDist);

        // ── Latest readings per machine ──
        List<EnergyReadingResponse> latest = repository.latestReadingPerMachine()
                .stream().map(this::toResponse).collect(Collectors.toList());
        resp.setLatestReadings(latest);

        return resp;
    }

    // ── Helpers ────────────────────────────────────────────────────────
    private EnergyReading findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Energy reading not found with id: " + id));
    }

    private void mapToEntity(EnergyReadingRequest req, EnergyReading e) {
        e.setMachineName(req.getMachineName());
        e.setMachineId(req.getMachineId());
        e.setEnergyKwh(req.getEnergyKwh());
        e.setPowerKw(req.getPowerKw());
        e.setVoltage(req.getVoltage());
        e.setCurrentAmps(req.getCurrentAmps());
        e.setPowerFactor(req.getPowerFactor());
        e.setMachineStatus(req.getMachineStatus());
        e.setRuntimeMinutes(req.getRuntimeMinutes());
        e.setCostPerKwh(req.getCostPerKwh());
        e.setNotes(req.getNotes());
        if (req.getReadingTimestamp() != null) {
            e.setReadingTimestamp(req.getReadingTimestamp());
        }
        // Compute cost
        if (req.getEnergyKwh() != null && req.getCostPerKwh() != null) {
            e.setTotalCost(round2(req.getEnergyKwh() * req.getCostPerKwh()));
        }
    }

    private EnergyReadingResponse toResponse(EnergyReading e) {
        EnergyReadingResponse r = new EnergyReadingResponse();
        r.setId(e.getId());
        r.setMachineName(e.getMachineName());
        r.setMachineId(e.getMachineId());
        r.setEnergyKwh(e.getEnergyKwh());
        r.setPowerKw(e.getPowerKw());
        r.setVoltage(e.getVoltage());
        r.setCurrentAmps(e.getCurrentAmps());
        r.setPowerFactor(e.getPowerFactor());
        r.setMachineStatus(e.getMachineStatus());
        r.setRuntimeMinutes(e.getRuntimeMinutes());
        r.setCostPerKwh(e.getCostPerKwh());
        r.setTotalCost(e.getTotalCost());
        r.setReadingTimestamp(e.getReadingTimestamp());
        r.setRecordedBy(e.getRecordedBy());
        r.setNotes(e.getNotes());
        r.setCreatedAt(e.getCreatedAt());
        return r;
    }

    private double round2(double v)  { return Math.round(v * 100.0) / 100.0; }
    private double round4(double v)  { return Math.round(v * 10000.0) / 10000.0; }
}