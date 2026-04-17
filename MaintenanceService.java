package com.example.demo.service;

import com.example.demo.dto.MachineRequest;
import com.example.demo.dto.MaintenanceScheduleRequest;
import com.example.demo.dto.MaintenanceScheduleResponse;
import com.example.demo.model.Admin;
import com.example.demo.model.Manager; // Added this import
import com.example.demo.model.Attendance;
import com.example.demo.model.Machine;
import com.example.demo.model.MaintenanceSchedule;
import com.example.demo.model.MaintenanceSchedule.MaintenanceType;
import com.example.demo.model.MaintenanceSchedule.Status;
import com.example.demo.model.Technician;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.MachineRepository;
import com.example.demo.repository.MaintenanceScheduleRepository;
import com.example.demo.repository.TechnicianRepository;
import com.example.demo.repository.ManagerRepository; // Added
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceService {

    @Autowired private MaintenanceScheduleRepository scheduleRepo;
    @Autowired private MachineRepository             machineRepo;
    @Autowired private AdminRepository               adminRepo;
    @Autowired private AttendanceRepository          attendanceRepo;
    @Autowired private TechnicianRepository          techRepo;
    @Autowired private ManagerRepository             managerRepo; // Added

    // ── Machine CRUD ──────────────────────────────────────────────────────────

    public Machine createMachine(MachineRequest req) {
        if (req.getSerialNumber() != null && !req.getSerialNumber().isEmpty()
                && machineRepo.existsBySerialNumber(req.getSerialNumber())) {
            throw new RuntimeException("Serial number already exists");
        }
        Machine m = new Machine();
        m.setName(req.getName());
        m.setModel(req.getModel());
        m.setSerialNumber(req.getSerialNumber());
        m.setLocation(req.getLocation());
        m.setDescription(req.getDescription());
        return machineRepo.save(m);
    }

    public List<Machine> getAllActiveMachines() {
        return machineRepo.findByIsActiveTrue();
    }

    public List<Machine> getAllMachines() {
        return machineRepo.findAll();
    }

    // ── Schedule CRUD ─────────────────────────────────────────────────────────

    public MaintenanceScheduleResponse createSchedule(MaintenanceScheduleRequest req, String username) {
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

        final String finalUsername = realUsername;

        // Find by Admin or Manager
        Admin admin = adminRepo.findByUsername(finalUsername).orElse(null);
        Manager manager = null;
        if (admin == null) {
            manager = managerRepo.findByUsername(finalUsername)
                    .orElseThrow(() -> new RuntimeException("User not found (neither Admin nor Manager): " + finalUsername));
        }

        if (req.getAssignedTechnician() != null && !req.getAssignedTechnician().isBlank()) {
            checkTechnicianPresentToday(req.getAssignedTechnician());
        }

        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setMachine(machine);
        schedule.setTitle(req.getTitle());
        schedule.setDescription(req.getDescription());
        schedule.setMaintenanceType(MaintenanceType.valueOf(req.getMaintenanceType()));
        schedule.setScheduledDate(LocalDateTime.parse(req.getScheduledDate()));
        schedule.setAssignedTechnician(req.getAssignedTechnician());
        schedule.setEstimatedDurationHours(req.getEstimatedDurationHours());
        schedule.setNotes(req.getNotes());
        schedule.setStatus(Status.SCHEDULED);

        if (admin != null) {
            schedule.setCreatedBy(admin);
        } else {
            schedule.setCreatedByManager(manager);
        }

        return toResponse(scheduleRepo.save(schedule));
    }

    public List<MaintenanceScheduleResponse> getAllSchedules() {
        return scheduleRepo.findAllByOrderByScheduledDateDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public MaintenanceScheduleResponse getScheduleById(Long id) {
        return toResponse(scheduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found")));
    }

    public MaintenanceScheduleResponse updateSchedule(Long id, MaintenanceScheduleRequest req) {
        MaintenanceSchedule schedule = scheduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (schedule.getStatus() == Status.CANCELLED) {
            throw new RuntimeException("Cannot update a cancelled schedule");
        }

        if (req.getAssignedTechnician() != null
                && !req.getAssignedTechnician().isBlank()
                && !req.getAssignedTechnician().equals(schedule.getAssignedTechnician())) {
            checkTechnicianPresentToday(req.getAssignedTechnician());
        }

        if (req.getMachineId() != null) {
            Machine machine = machineRepo.findById(req.getMachineId())
                    .orElseThrow(() -> new RuntimeException("Machine not found"));
            schedule.setMachine(machine);
        }
        if (req.getTitle() != null)       schedule.setTitle(req.getTitle());
        if (req.getDescription() != null) schedule.setDescription(req.getDescription());
        if (req.getMaintenanceType() != null)
            schedule.setMaintenanceType(MaintenanceType.valueOf(req.getMaintenanceType()));
        if (req.getScheduledDate() != null)
            schedule.setScheduledDate(LocalDateTime.parse(req.getScheduledDate()));
        if (req.getAssignedTechnician() != null)
            schedule.setAssignedTechnician(req.getAssignedTechnician());
        if (req.getEstimatedDurationHours() != null)
            schedule.setEstimatedDurationHours(req.getEstimatedDurationHours());
        if (req.getNotes() != null) schedule.setNotes(req.getNotes());
        if (req.getStatus() != null) {
            Status newStatus = Status.valueOf(req.getStatus());
            schedule.setStatus(newStatus);
            if (newStatus == Status.COMPLETED) {
                schedule.setCompletedDate(LocalDateTime.now());
            }
        }

        return toResponse(scheduleRepo.save(schedule));
    }
    public MaintenanceScheduleResponse updateTaskStatusByTechnician(
            Long scheduleId, String newStatus, String notes, String username) {

        MaintenanceSchedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        // Resolve technician
        Technician technician = techRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        String fullName = technician.getFirstName() + " " + technician.getLastName();
        String assignedTo = schedule.getAssignedTechnician();

        // Check ownership — compare both username AND full name
        boolean isAssigned = (assignedTo != null) && (
                assignedTo.equalsIgnoreCase(username) ||
                        assignedTo.equalsIgnoreCase(fullName)
        );

        if (!isAssigned) {
            throw new RuntimeException("Access denied: this task is not assigned to you.");
        }

        // Apply status update
        Status status = Status.valueOf(newStatus);
        schedule.setStatus(status);
        if (status == Status.COMPLETED) {
            schedule.setCompletedDate(LocalDateTime.now());
        }
        if (notes != null && !notes.isBlank()) {
            schedule.setNotes(notes);
        }

        return toResponse(scheduleRepo.save(schedule));
    }
    public void cancelSchedule(Long id) {
        MaintenanceSchedule schedule = scheduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        if (schedule.getStatus() == Status.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed schedule");
        }
        schedule.setStatus(Status.CANCELLED);
        scheduleRepo.save(schedule);
    }

    public void deleteSchedule(Long id) {
        if (!scheduleRepo.existsById(id)) throw new RuntimeException("Schedule not found");
        scheduleRepo.deleteById(id);
    }

    // ── NEW: Technician-scoped task fetch ─────────────────────────────────────

    /**
     * Returns ONLY the maintenance schedules assigned to the given technician.
     * The assignedTechnician column may store either a username (e.g. "john_doe")
     * or a full name (e.g. "John Doe"). We try both so nothing is missed.
     *
     * This method is called exclusively from the /api/technician/my-tasks endpoint,
     * which is secured to the logged-in technician, so no cross-technician leakage
     * is possible.
     */
    public List<MaintenanceScheduleResponse> getSchedulesForTechnician(String username) {

        // 1. Try matching directly by username (e.g. column stores "john_doe")
        List<MaintenanceSchedule> byUsername =
                scheduleRepo.findByAssignedTechnicianIgnoreCaseOrderByScheduledDateDesc(username);

        if (!byUsername.isEmpty()) {
            return byUsername.stream().map(this::toResponse).collect(Collectors.toList());
        }

        // 2. Fall back: resolve full name from the Technician table and match by full name
        //    (e.g. column stores "John Doe" but JWT carries "john_doe")
        return techRepo.findByUsername(username)
                .map(t -> {
                    String fullName = t.getFirstName() + " " + t.getLastName();
                    return scheduleRepo
                            .findByAssignedTechnicianIgnoreCaseOrderByScheduledDateDesc(fullName)
                            .stream()
                            .map(this::toResponse)
                            .collect(Collectors.toList());
                })
                .orElse(List.of());
    }

    // ── Attendance check helper ───────────────────────────────────────────────

    /**
     * Verifies that the technician named in assignedTechnician has clocked in
     * today with a status of PRESENT or LATE.
     */
    private void checkTechnicianPresentToday(String assignedTechnician) {
        LocalDate today = LocalDate.now();

        List<Attendance> byNameOrUsername =
                attendanceRepo.findPresentTechnicianByNameOrUsername(assignedTechnician, today);

        if (!byNameOrUsername.isEmpty()) {
            return;
        }

        List<Technician> candidates = techRepo.findByIsActiveTrueOrderByFirstNameAsc();

        for (Technician t : candidates) {
            String fullName = t.getFirstName() + " " + t.getLastName();
            boolean nameMatch = fullName.equalsIgnoreCase(assignedTechnician)
                    || t.getUsername().equalsIgnoreCase(assignedTechnician);

            if (nameMatch) {
                boolean present = attendanceRepo
                        .findByUserRoleAndUserIdAndAttendanceDate(
                                Attendance.UserRole.TECHNICIAN, t.getId(), today)
                        .filter(a -> a.getStatus() == Attendance.Status.PRESENT
                                || a.getStatus() == Attendance.Status.LATE)
                        .isPresent();

                if (present) return;

                throw new IllegalArgumentException(
                        "Cannot assign task: " + fullName
                                + " has not marked attendance as Present or Late today ("
                                + today + "). The technician must clock in before being assigned.");
            }
        }

        throw new IllegalArgumentException(
                "Cannot assign task: no active technician found matching '"
                        + assignedTechnician + "'. Please select a valid technician.");
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private MaintenanceScheduleResponse toResponse(MaintenanceSchedule s) {
        MaintenanceScheduleResponse r = new MaintenanceScheduleResponse();
        r.setId(s.getId());
        r.setMachineId(s.getMachine().getId());
        r.setMachineName(s.getMachine().getName());
        r.setMachineModel(s.getMachine().getModel());
        r.setTitle(s.getTitle());
        r.setDescription(s.getDescription());
        r.setMaintenanceType(s.getMaintenanceType().name());
        r.setScheduledDate(s.getScheduledDate().toString());
        r.setCompletedDate(s.getCompletedDate() != null ? s.getCompletedDate().toString() : null);
        r.setStatus(s.getStatus().name());
        r.setAssignedTechnician(s.getAssignedTechnician());
        r.setEstimatedDurationHours(s.getEstimatedDurationHours());
        r.setNotes(s.getNotes());

        if (s.getCreatedBy() != null) {
            r.setCreatedByUsername(s.getCreatedBy().getUsername());
        } else if (s.getCreatedByManager() != null) {
            r.setCreatedByUsername(s.getCreatedByManager().getUsername());
        }

        return r;
    }
}