package com.example.demo.service;

import com.example.demo.dto.AttendanceMarkRequest;
import com.example.demo.dto.AttendanceResponse;
import com.example.demo.dto.AttendanceSummary;
import com.example.demo.model.Admin;
import com.example.demo.model.Attendance;
import com.example.demo.model.Attendance.Status;
import com.example.demo.model.Attendance.UserRole;
import com.example.demo.model.Manager;
import com.example.demo.model.Technician;
import com.example.demo.model.StaffOperator;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ManagerRepository;
import com.example.demo.repository.TechnicianRepository;
import com.example.demo.repository.StaffOperatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    // Standard work-day start — arrivals after this are marked LATE
    private static final int LATE_THRESHOLD_HOUR   = 9;
    private static final int LATE_THRESHOLD_MINUTE = 15;

    @Autowired private AttendanceRepository    attendanceRepo;
    @Autowired private TechnicianRepository    techRepo;
    @Autowired private StaffOperatorRepository staffRepo;
    @Autowired private AdminRepository         adminRepo;
    @Autowired private ManagerRepository       managerRepo;

    // ═══════════════════════════════════════════════════════════════════════
    // CLOCK-IN  (any role)
    // ═══════════════════════════════════════════════════════════════════════

    @Transactional
    public AttendanceResponse clockIn(UserRole role, String username, String notes) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        UserInfo info = resolveUser(role, username);

        // Idempotent – if already clocked in today, return existing record
        Optional<Attendance> existing =
                attendanceRepo.findByUserRoleAndUserIdAndAttendanceDate(role, info.id, today);
        if (existing.isPresent()) {
            Attendance a = existing.get();
            if (a.getClockIn() != null)
                throw new RuntimeException("Already clocked in today at " + a.getClockIn());
            // Record existed but no clock-in (e.g. marked absent) — allow override
            a.setClockIn(now);
            a.setStatus(deriveStatus(now));
            if (notes != null && !notes.isBlank()) a.setNotes(notes);
            return toResponse(attendanceRepo.save(a));
        }

        Attendance a = new Attendance();
        a.setUserRole(role);
        a.setUserId(info.id);
        a.setUsername(info.username);
        a.setFullName(info.fullName);
        a.setAttendanceDate(today);
        a.setClockIn(now);
        a.setStatus(deriveStatus(now));
        a.setNotes(notes);
        return toResponse(attendanceRepo.save(a));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CLOCK-OUT  (any role)
    // ═══════════════════════════════════════════════════════════════════════

    @Transactional
    public AttendanceResponse clockOut(UserRole role, String username, String notes) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        UserInfo info = resolveUser(role, username);

        Attendance a = attendanceRepo
                .findByUserRoleAndUserIdAndAttendanceDate(role, info.id, today)
                .orElseThrow(() -> new RuntimeException(
                        "No clock-in found for today. Please clock in first."));

        if (a.getClockIn() == null)
            throw new RuntimeException("You must clock in before clocking out.");
        if (a.getClockOut() != null)
            throw new RuntimeException("Already clocked out today at " + a.getClockOut());

        a.setClockOut(now);
        if (notes != null && !notes.isBlank()) {
            String existingNotes = a.getNotes() == null ? "" : a.getNotes() + " | ";
            a.setNotes(existingNotes + notes);
        }

        // Compute work hours
        double hours = ChronoUnit.MINUTES.between(a.getClockIn(), now) / 60.0;
        a.setWorkHours(Math.round(hours * 100.0) / 100.0);

        // Upgrade PRESENT → HALF_DAY if < 5 hours worked
        if (hours < 5.0 && a.getStatus() == Status.PRESENT) {
            a.setStatus(Status.HALF_DAY);
        }

        return toResponse(attendanceRepo.save(a));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MARK STATUS  (self: leave; admin/manager: any status on any date)
    // ═══════════════════════════════════════════════════════════════════════

    @Transactional
    public AttendanceResponse markStatus(UserRole role, Long userId,
                                         AttendanceMarkRequest req) {
        LocalDate date = (req.getDate() != null && !req.getDate().isBlank())
                ? LocalDate.parse(req.getDate())
                : LocalDate.now();

        Status status;
        try {
            status = Status.valueOf(req.getStatus().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid status: " + req.getStatus());
        }

        // Resolve name info based on role
        String username, fullName;
        switch (role) {
            case TECHNICIAN -> {
                Technician t = techRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Technician not found"));
                username = t.getUsername();
                fullName = t.getFirstName() + " " + t.getLastName();
            }
            case STAFF -> {
                StaffOperator s = staffRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Staff operator not found"));
                username = s.getUsername();
                fullName = s.getFirstName() + " " + s.getLastName();
            }
            case ADMIN -> {
                Admin ad = adminRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Admin not found"));
                username = ad.getUsername();
                fullName = ad.getFirstName() + " " + ad.getLastName();
            }
            case MANAGER -> {
                Manager m = managerRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Manager not found"));
                username = m.getUsername();
                fullName = m.getFirstName() + " " + m.getLastName();
            }
            default -> throw new RuntimeException("Unknown role: " + role);
        }

        Attendance a = attendanceRepo
                .findByUserRoleAndUserIdAndAttendanceDate(role, userId, date)
                .orElseGet(() -> {
                    Attendance n = new Attendance();
                    n.setUserRole(role);
                    n.setUserId(userId);
                    n.setUsername(username);
                    n.setFullName(fullName);
                    n.setAttendanceDate(date);
                    return n;
                });

        a.setStatus(status);
        if (req.getNotes() != null && !req.getNotes().isBlank()) a.setNotes(req.getNotes());
        return toResponse(attendanceRepo.save(a));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // QUERIES – user self-view
    // ═══════════════════════════════════════════════════════════════════════

    public List<AttendanceResponse> getMyAttendance(UserRole role, String username) {
        UserInfo info = resolveUser(role, username);
        return attendanceRepo
                .findByUserRoleAndUserIdOrderByAttendanceDateDesc(role, info.id)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<AttendanceResponse> getMyAttendanceInRange(
            UserRole role, String username, LocalDate from, LocalDate to) {
        UserInfo info = resolveUser(role, username);
        return attendanceRepo
                .findByUserRoleAndUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        role, info.id, from, to)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AttendanceResponse getTodayRecord(UserRole role, String username) {
        UserInfo info = resolveUser(role, username);
        return attendanceRepo
                .findByUserRoleAndUserIdAndAttendanceDate(role, info.id, LocalDate.now())
                .map(this::toResponse)
                .orElse(null);   // null → frontend shows "Not clocked in yet"
    }

    // ═══════════════════════════════════════════════════════════════════════
    // QUERIES – admin / manager reports
    // ═══════════════════════════════════════════════════════════════════════

    /** All records for today (all roles). */
    public List<AttendanceResponse> getDailyReport(LocalDate date) {
        return attendanceRepo
                .findByAttendanceDateOrderByUserRoleAscFullNameAsc(date)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** All records for a specific user over a date range. */
    public List<AttendanceResponse> getUserReport(UserRole role, Long userId,
                                                  LocalDate from, LocalDate to) {
        return attendanceRepo
                .findByUserRoleAndUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        role, userId, from, to)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** All records in a date range (all roles). */
    public List<AttendanceResponse> getRangeReport(LocalDate from, LocalDate to) {
        return attendanceRepo
                .findByAttendanceDateBetweenOrderByAttendanceDateDescUserRoleAscFullNameAsc(from, to)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** Attendance summary (stats card) for one user over a date range. */
    public AttendanceSummary getSummary(UserRole role, Long userId,
                                        LocalDate from, LocalDate to) {
        // Resolve name based on role
        String username, fullName;
        switch (role) {
            case TECHNICIAN -> {
                Technician t = techRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Technician not found"));
                username = t.getUsername();
                fullName = t.getFirstName() + " " + t.getLastName();
            }
            case STAFF -> {
                StaffOperator s = staffRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Staff not found"));
                username = s.getUsername();
                fullName = s.getFirstName() + " " + s.getLastName();
            }
            case ADMIN -> {
                Admin ad = adminRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Admin not found"));
                username = ad.getUsername();
                fullName = ad.getFirstName() + " " + ad.getLastName();
            }
            case MANAGER -> {
                Manager m = managerRepo.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Manager not found"));
                username = m.getUsername();
                fullName = m.getFirstName() + " " + m.getLastName();
            }
            default -> throw new RuntimeException("Unknown role: " + role);
        }

        List<Attendance> records = attendanceRepo
                .findByUserRoleAndUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        role, userId, from, to);

        long total   = records.size();
        long present = records.stream().filter(r -> r.getStatus() == Status.PRESENT).count();
        long late    = records.stream().filter(r -> r.getStatus() == Status.LATE).count();
        long absent  = records.stream().filter(r -> r.getStatus() == Status.ABSENT).count();
        long half    = records.stream().filter(r -> r.getStatus() == Status.HALF_DAY).count();
        long leave   = records.stream().filter(r -> r.getStatus() == Status.ON_LEAVE).count();

        double rate = total == 0 ? 0 :
                Math.round(((present + late + half * 0.5) / total) * 10000.0) / 100.0;

        AttendanceSummary s = new AttendanceSummary();
        s.setUserId(userId);
        s.setUsername(username);
        s.setFullName(fullName);
        s.setUserRole(role.name());
        s.setTotalDays(total);
        s.setPresentDays(present);
        s.setLateDays(late);
        s.setAbsentDays(absent);
        s.setHalfDays(half);
        s.setLeaveDays(leave);
        s.setAttendanceRate(rate);
        return s;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MARK BY USERNAME  (self leave / status)
    // ═══════════════════════════════════════════════════════════════════════

    @Transactional
    public AttendanceResponse markStatusByUsername(UserRole role, String username,
                                                   AttendanceMarkRequest req) {
        UserInfo info = resolveUser(role, username);
        return markStatus(role, info.id, req);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DELETE  (admin / manager)
    // ═══════════════════════════════════════════════════════════════════════

    @Transactional
    public void deleteRecord(Long id) {
        if (!attendanceRepo.existsById(id))
            throw new RuntimeException("Attendance record not found: " + id);
        attendanceRepo.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    /** Decide PRESENT vs LATE based on clock-in time. */
    private Status deriveStatus(LocalDateTime clockIn) {
        if (clockIn.getHour() > LATE_THRESHOLD_HOUR ||
                (clockIn.getHour() == LATE_THRESHOLD_HOUR &&
                        clockIn.getMinute() > LATE_THRESHOLD_MINUTE)) {
            return Status.LATE;
        }
        return Status.PRESENT;
    }

    /**
     * Resolve any role's user by username → id + names.
     * Supports TECHNICIAN, STAFF, ADMIN, and MANAGER.
     */
    private UserInfo resolveUser(UserRole role, String username) {
        switch (role) {
            case TECHNICIAN -> {
                Technician t = techRepo.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Technician not found: " + username));
                return new UserInfo(t.getId(), t.getUsername(),
                        t.getFirstName() + " " + t.getLastName());
            }
            case STAFF -> {
                StaffOperator s = staffRepo.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Staff not found: " + username));
                return new UserInfo(s.getId(), s.getUsername(),
                        s.getFirstName() + " " + s.getLastName());
            }
            case ADMIN -> {
                Admin a = adminRepo.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Admin not found: " + username));
                return new UserInfo(a.getId(), a.getUsername(),
                        a.getFirstName() + " " + a.getLastName());
            }
            case MANAGER -> {
                Manager m = managerRepo.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Manager not found: " + username));
                return new UserInfo(m.getId(), m.getUsername(),
                        m.getFirstName() + " " + m.getLastName());
            }
            default -> throw new RuntimeException("Unknown role: " + role);
        }
    }

    private AttendanceResponse toResponse(Attendance a) {
        AttendanceResponse r = new AttendanceResponse();
        r.setId(a.getId());
        r.setUserRole(a.getUserRole().name());
        r.setUserId(a.getUserId());
        r.setUsername(a.getUsername());
        r.setFullName(a.getFullName());
        r.setAttendanceDate(a.getAttendanceDate().toString());
        r.setClockIn(a.getClockIn()  != null ? a.getClockIn().toString()  : null);
        r.setClockOut(a.getClockOut() != null ? a.getClockOut().toString() : null);
        r.setStatus(a.getStatus().name());
        r.setNotes(a.getNotes());
        r.setWorkHours(a.getWorkHours());
        r.setCreatedAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null);
        r.setUpdatedAt(a.getUpdatedAt() != null ? a.getUpdatedAt().toString() : null);
        return r;
    }

    /** Tiny inner record to carry resolved user identity. */
    private static class UserInfo {
        final Long   id;
        final String username;
        final String fullName;
        UserInfo(Long id, String username, String fullName) {
            this.id = id; this.username = username; this.fullName = fullName;
        }
    }
}