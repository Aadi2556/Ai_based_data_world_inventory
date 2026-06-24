package com.example.demo.controller;

import com.example.demo.dto.AttendanceClockInRequest;
import com.example.demo.dto.AttendanceClockOutRequest;
import com.example.demo.dto.AttendanceMarkRequest;
import com.example.demo.dto.AttendanceResponse;
import com.example.demo.dto.AttendanceSummary;
import com.example.demo.model.Attendance.UserRole;
import com.example.demo.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Manager-facing attendance endpoints.
 *
 * Base: /api/attendance/manager/**
 *
 * Supervision (other users):
 *   - READ  daily / range / per-user reports
 *   - MARK  attendance for technicians and staff
 *   - DELETE any technician / staff record
 *
 * Self-service (manager's own record):
 *   - POST  /my-clock-in
 *   - POST  /my-clock-out
 *   - GET   /my-today
 *   - GET   /my-history/range
 *   - POST  /my-mark-leave
 *
 * Security: ROLE_MANAGER enforced in SecurityConfig via
 *   .requestMatchers("/api/attendance/manager/**").hasRole("MANAGER")
 */
@RestController
@RequestMapping("/api/attendance/manager")
@CrossOrigin(origins = "*")
public class ManagerAttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // ── Helper: strip "MANAGER:" prefix from JWT subject ──────────────────────
    private String extractUsername(Authentication auth) {
        String name = auth.getName();
        return name.startsWith("MANAGER:") ? name.substring(8) : name;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SUPERVISION — daily / range / per-user reports
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * GET /api/attendance/manager/daily?date=yyyy-MM-dd
     * All staff + technician records for a given date.
     */
    @GetMapping("/daily")
    public ResponseEntity<?> getDailyReport(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) date = LocalDate.now();
            List<AttendanceResponse> records = attendanceService.getDailyReport(date);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/attendance/manager/range?from=yyyy-MM-dd&to=yyyy-MM-dd
     * All records between two dates.
     */
    @GetMapping("/range")
    public ResponseEntity<?> getRangeReport(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        try {
            List<AttendanceResponse> records = attendanceService.getRangeReport(from, to);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/attendance/manager/user/{role}/{userId}?from=...&to=...
     * Records for one specific user. role: TECHNICIAN | STAFF
     */
    @GetMapping("/user/{role}/{userId}")
    public ResponseEntity<?> getUserReport(
            @PathVariable String role,
            @PathVariable Long userId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            List<AttendanceResponse> records =
                    attendanceService.getUserReport(userRole, userId, from, to);
            return ResponseEntity.ok(records);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role: " + role));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/attendance/manager/summary/{role}/{userId}?from=...&to=...
     * Stats summary card for one user.
     */
    @GetMapping("/summary/{role}/{userId}")
    public ResponseEntity<?> getSummary(
            @PathVariable String role,
            @PathVariable Long userId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            AttendanceSummary summary =
                    attendanceService.getSummary(userRole, userId, from, to);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role: " + role));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SUPERVISION — mark & delete
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * POST /api/attendance/manager/mark/{role}/{userId}
     * Manager marks attendance for a technician or staff member.
     * Body: { "status": "PRESENT|LATE|ABSENT|HALF_DAY|ON_LEAVE",
     *         "date": "yyyy-MM-dd", "notes": "..." }
     */
    @PostMapping("/mark/{role}/{userId}")
    public ResponseEntity<?> markAttendance(
            @PathVariable String role,
            @PathVariable Long userId,
            @RequestBody AttendanceMarkRequest req) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            AttendanceResponse response = attendanceService.markStatus(userRole, userId, req);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/attendance/manager/{id}
     * Manager deletes any technician / staff attendance record.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long id) {
        try {
            attendanceService.deleteRecord(id);
            return ResponseEntity.ok(Map.of("message", "Attendance record deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SELF-SERVICE — manager clocks in / out for themselves
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * POST /api/attendance/manager/my-clock-in
     * Manager clocks themselves in for today.
     */
    @PostMapping("/my-clock-in")
    public ResponseEntity<?> myClockIn(
            @RequestBody(required = false) AttendanceClockInRequest req,
            Authentication auth) {
        try {
            String username = extractUsername(auth);
            String notes = req != null ? req.getNotes() : null;
            AttendanceResponse r = attendanceService.clockIn(UserRole.MANAGER, username, notes);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/attendance/manager/my-clock-out
     * Manager clocks themselves out for today.
     */
    @PostMapping("/my-clock-out")
    public ResponseEntity<?> myClockOut(
            @RequestBody(required = false) AttendanceClockOutRequest req,
            Authentication auth) {
        try {
            String username = extractUsername(auth);
            String notes = req != null ? req.getNotes() : null;
            AttendanceResponse r = attendanceService.clockOut(UserRole.MANAGER, username, notes);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/attendance/manager/my-today
     * Returns the manager's own attendance record for today.
     */
    @GetMapping("/my-today")
    public ResponseEntity<?> myToday(Authentication auth) {
        try {
            String username = extractUsername(auth);
            AttendanceResponse r =
                    attendanceService.getTodayRecord(UserRole.MANAGER, username);
            return ResponseEntity.ok(r != null ? r : Map.of("message", "Not clocked in yet"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/attendance/manager/my-history/range?from=...&to=...
     * Returns the manager's own attendance history over a date range.
     */
    @GetMapping("/my-history/range")
    public ResponseEntity<?> myHistoryRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication auth) {
        try {
            String username = extractUsername(auth);
            List<AttendanceResponse> list =
                    attendanceService.getMyAttendanceInRange(UserRole.MANAGER, username, from, to);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/attendance/manager/my-mark-leave
     * Manager marks their own leave / absent status.
     */
    @PostMapping("/my-mark-leave")
    public ResponseEntity<?> myMarkLeave(
            @RequestBody AttendanceMarkRequest req,
            Authentication auth) {
        try {
            String username = extractUsername(auth);
            AttendanceResponse r =
                    attendanceService.markStatusByUsername(UserRole.MANAGER, username, req);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}