package com.example.demo.service;

import com.example.demo.dto.RepairLogRequest;
import com.example.demo.dto.RepairLogResponse;
import com.example.demo.model.*;
import com.example.demo.model.RepairLog.RepairStatus;
import com.example.demo.model.RepairLog.Severity;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.MachineRepository;
import com.example.demo.repository.MaintenanceScheduleRepository;
import com.example.demo.repository.RepairLogRepository;
import com.example.demo.repository.ManagerRepository; // Added
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RepairLogService {

    @Autowired private RepairLogRepository repairRepo;
    @Autowired private MachineRepository machineRepo;
    @Autowired private MaintenanceScheduleRepository scheduleRepo;
    @Autowired private AdminRepository adminRepo;
    @Autowired private ManagerRepository managerRepo; // Added

    // ── CREATE ────────────────────────────────────────────────────────────────

    public RepairLogResponse createRepairLog(RepairLogRequest req, String username) {
        Machine machine = machineRepo.findById(req.getMachineId())
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        // Extract real username if it contains a prefix like "MANAGER:"
        String realUsername = username;
        if (username.startsWith("MANAGER:")) {
            realUsername = username.substring(8);
        } else if (username.startsWith("ADMIN:")) {
            realUsername = username.substring(6);
        } else if (username.startsWith("TECH:")) {
            realUsername = username.substring(5);
        } else if (username.startsWith("STAFF:")) {
            realUsername = username.substring(6);
        }

        // Find by Admin or Manager
        Admin creatorResult = adminRepo.findByUsername(realUsername).orElse(null);
        final Admin creator = creatorResult;

        final Manager manager;
        if (creator == null) {
            final String searchName = realUsername;
            manager = managerRepo.findByUsername(searchName)
                    .orElseThrow(() -> new RuntimeException("User not found (not an Admin or Manager): " + searchName));
        } else {
            manager = null;
        }

        RepairLog log = new RepairLog();
        log.setMachine(machine);
        log.setTitle(req.getTitle());
        log.setProblemDescription(req.getProblemDescription());
        log.setRepairAction(req.getRepairAction());
        log.setSeverity(req.getSeverity() != null ? Severity.valueOf(req.getSeverity()) : Severity.MEDIUM);
        log.setStatus(req.getStatus() != null ? RepairStatus.valueOf(req.getStatus()) : RepairStatus.OPEN);
        log.setReportedDate(req.getReportedDate() != null ? LocalDateTime.parse(req.getReportedDate()) : LocalDateTime.now());
        if (req.getResolvedDate() != null && !req.getResolvedDate().isEmpty()) {
            log.setResolvedDate(LocalDateTime.parse(req.getResolvedDate()));
        }
        log.setTechnician(req.getTechnician());
        log.setPartsReplaced(req.getPartsReplaced());
        if (req.getRepairCost() != null && !req.getRepairCost().isEmpty()) {
            try { log.setRepairCost(new BigDecimal(req.getRepairCost())); } catch (Exception ignored) {}
        }
        log.setDowntimeHours(req.getDowntimeHours());
        log.setNotes(req.getNotes());
        log.setDocuments(req.getDocuments());

        if (creator != null) {
            log.setLoggedBy(creator);
        } else {
            log.setLoggedByManager(manager);
        }

        if (req.getScheduleId() != null) {
            scheduleRepo.findById(req.getScheduleId()).ifPresent(log::setSchedule);
        }

        return toResponse(repairRepo.save(log));
    }

    // ── READ ALL ──────────────────────────────────────────────────────────────

    public List<RepairLogResponse> getAllRepairLogs() {
        return repairRepo.findAllByOrderByReportedDateDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<RepairLogResponse> getRepairLogsByMachine(Long machineId) {
        return repairRepo.findByMachineIdOrderByReportedDateDesc(machineId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── READ ONE ──────────────────────────────────────────────────────────────

    public RepairLogResponse getRepairLogById(Long id) {
        RepairLog log = repairRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Repair log not found"));
        return toResponse(log);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public RepairLogResponse updateRepairLog(Long id, RepairLogRequest req) {
        RepairLog log = repairRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Repair log not found"));

        if (req.getMachineId() != null) {
            Machine machine = machineRepo.findById(req.getMachineId())
                    .orElseThrow(() -> new RuntimeException("Machine not found"));
            log.setMachine(machine);
        }
        if (req.getScheduleId() != null) {
            scheduleRepo.findById(req.getScheduleId()).ifPresent(log::setSchedule);
        }
        if (req.getTitle() != null) log.setTitle(req.getTitle());
        if (req.getProblemDescription() != null) log.setProblemDescription(req.getProblemDescription());
        if (req.getRepairAction() != null) log.setRepairAction(req.getRepairAction());
        if (req.getSeverity() != null) log.setSeverity(Severity.valueOf(req.getSeverity()));
        if (req.getStatus() != null) {
            RepairStatus newStatus = RepairStatus.valueOf(req.getStatus());
            log.setStatus(newStatus);
            if ((newStatus == RepairStatus.RESOLVED || newStatus == RepairStatus.CLOSED)
                    && log.getResolvedDate() == null) {
                log.setResolvedDate(LocalDateTime.now());
            }
        }
        if (req.getReportedDate() != null) log.setReportedDate(LocalDateTime.parse(req.getReportedDate()));
        if (req.getResolvedDate() != null && !req.getResolvedDate().isEmpty()) {
            log.setResolvedDate(LocalDateTime.parse(req.getResolvedDate()));
        }
        if (req.getTechnician() != null) log.setTechnician(req.getTechnician());
        if (req.getPartsReplaced() != null) log.setPartsReplaced(req.getPartsReplaced());
        if (req.getRepairCost() != null && !req.getRepairCost().isEmpty()) {
            try { log.setRepairCost(new BigDecimal(req.getRepairCost())); } catch (Exception ignored) {}
        }
        if (req.getDowntimeHours() != null) log.setDowntimeHours(req.getDowntimeHours());
        if (req.getNotes() != null) log.setNotes(req.getNotes());
        if (req.getDocuments() != null) log.setDocuments(req.getDocuments());

        return toResponse(repairRepo.save(log));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public void deleteRepairLog(Long id) {
        if (!repairRepo.existsById(id)) throw new RuntimeException("Repair log not found");
        repairRepo.deleteById(id);
    }

    // ── STATS ─────────────────────────────────────────────────────────────────

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", repairRepo.count());
        stats.put("open", repairRepo.countByStatus(RepairStatus.OPEN));
        stats.put("inProgress", repairRepo.countByStatus(RepairStatus.IN_PROGRESS));
        stats.put("resolved", repairRepo.countByStatus(RepairStatus.RESOLVED));
        stats.put("closed", repairRepo.countByStatus(RepairStatus.CLOSED));
        stats.put("critical", repairRepo.countBySeverity(Severity.CRITICAL));
        stats.put("high", repairRepo.countBySeverity(Severity.HIGH));
        return stats;
    }

    // ── MAPPER ────────────────────────────────────────────────────────────────

    private RepairLogResponse toResponse(RepairLog log) {
        RepairLogResponse r = new RepairLogResponse();
        r.setId(log.getId());
        r.setMachineId(log.getMachine().getId());
        r.setMachineName(log.getMachine().getName());
        r.setMachineModel(log.getMachine().getModel());

        if (log.getSchedule() != null) {
            r.setScheduleId(log.getSchedule().getId());
            r.setScheduleTitle(log.getSchedule().getTitle());
        }

        r.setTitle(log.getTitle());
        r.setProblemDescription(log.getProblemDescription());
        r.setRepairAction(log.getRepairAction());
        r.setSeverity(log.getSeverity().name());
        r.setStatus(log.getStatus().name());
        r.setReportedDate(log.getReportedDate() != null ? log.getReportedDate().toString() : null);
        r.setResolvedDate(log.getResolvedDate() != null ? log.getResolvedDate().toString() : null);
        r.setTechnician(log.getTechnician());
        r.setPartsReplaced(log.getPartsReplaced());
        r.setRepairCost(log.getRepairCost() != null ? log.getRepairCost().toString() : null);
        r.setDowntimeHours(log.getDowntimeHours());
        r.setNotes(log.getNotes());

        // Parse comma-separated documents into list
        if (log.getDocuments() != null && !log.getDocuments().isBlank()) {
            r.setDocuments(Arrays.stream(log.getDocuments().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList()));
        } else {
            r.setDocuments(List.of());
        }

        r.setLoggedByUsername(log.getLoggedBy() != null ? log.getLoggedBy().getUsername() : null);
        r.setCreatedAt(log.getCreatedAt() != null ? log.getCreatedAt().toString() : null);
        r.setUpdatedAt(log.getUpdatedAt() != null ? log.getUpdatedAt().toString() : null);
        return r;
    }
}