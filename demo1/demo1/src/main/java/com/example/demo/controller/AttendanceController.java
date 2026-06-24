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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Attendance endpoints.
 *
 * Technician routes  →  /api/attendance/technician/**
 * Staff routes       →  /api/attendance/staff/**
 * Admin routes       →  /api/attendance/admin/**
 *
 * Security is enforced in SecurityConfig:
 *   - /api/attendance/technician/** → TECHNICIAN (+ ADMIN)
 *   - /api/attendance/staff/**      → STAFF      (+ ADMIN)
 *   - /api/attendance/admin/**      → ADMIN only
 */
@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // ══════════════════════════════════════════════════════════════════════
    // TECHNICIAN self-service
    // ══════════════════════════════════════════════════════════════════════

    /** POST /api/attendance/technician/clock-in */
    @PostMapping("/technician/clock-in")
    public ResponseEntity<?> techClockIn(
            @RequestBody(required = false) AttendanceClockInRequest req,
            Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "TECH:");
            String notes = req != null ? req.getNotes() : null;
            AttendanceResponse r = attendanceService.clockIn(UserRole.TECHNICIAN, username, notes);
            return ResponseEntity.ok(r);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/attendance/technician/clock-out */
    @PostMapping("/technician/clock-out")
    public ResponseEntity<?> techClockOut(
            @RequestBody(required = false) AttendanceClockOutRequest req,
            Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "TECH:");
            String notes = req != null ? req.getNotes() : null;
            AttendanceResponse r = attendanceService.clockOut(UserRole.TECHNICIAN, username, notes);
            return ResponseEntity.ok(r);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/attendance/technician/today */
    @GetMapping("/technician/today")
    public ResponseEntity<?> techToday(Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "TECH:");
            AttendanceResponse r = attendanceService.getTodayRecord(UserRole.TECHNICIAN, username);
            return ResponseEntity.ok(r != null ? r : Map.of("message", "Not clocked in yet"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/attendance/technician/history */
    @GetMapping("/technician/history")
    public ResponseEntity<?> techHistory(Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "TECH:");
            List<AttendanceResponse> list =
                    attendanceService.getMyAttendance(UserRole.TECHNICIAN, username);
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/attendance/technician/history/range?from=...&to=... */
    @GetMapping("/technician/history/range")
    public ResponseEntity<?> techHistoryRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "TECH:");
            List<AttendanceResponse> list =
                    attendanceService.getMyAttendanceInRange(UserRole.TECHNICIAN, username, from, to);
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/attendance/technician/mark-leave */
    @PostMapping("/technician/mark-leave")
    public ResponseEntity<?> techMarkLeave(
            @RequestBody AttendanceMarkRequest req,
            Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "TECH:");
            AttendanceResponse r = attendanceService.markStatusByUsername(
                    UserRole.TECHNICIAN, username, req);
            return ResponseEntity.ok(r);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // STAFF self-service
    // ══════════════════════════════════════════════════════════════════════

    /** POST /api/attendance/staff/clock-in */
    @PostMapping("/staff/clock-in")
    public ResponseEntity<?> staffClockIn(
            @RequestBody(required = false) AttendanceClockInRequest req,
            Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "STAFF:");
            String notes = req != null ? req.getNotes() : null;
            AttendanceResponse r = attendanceService.clockIn(UserRole.STAFF, username, notes);
            return ResponseEntity.ok(r);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/attendance/staff/clock-out */
    @PostMapping("/staff/clock-out")
    public ResponseEntity<?> staffClockOut(
            @RequestBody(required = false) AttendanceClockOutRequest req,
            Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "STAFF:");
            String notes = req != null ? req.getNotes() : null;
            AttendanceResponse r = attendanceService.clockOut(UserRole.STAFF, username, notes);
            return ResponseEntity.ok(r);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/attendance/staff/today */
    @GetMapping("/staff/today")
    public ResponseEntity<?> staffToday(Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "STAFF:");
            AttendanceResponse r = attendanceService.getTodayRecord(UserRole.STAFF, username);
            return ResponseEntity.ok(r != null ? r : Map.of("message", "Not clocked in yet"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/attendance/staff/history */
    @GetMapping("/staff/history")
    public ResponseEntity<?> staffHistory(Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "STAFF:");
            List<AttendanceResponse> list =
                    attendanceService.getMyAttendance(UserRole.STAFF, username);
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/attendance/staff/history/range?from=...&to=... */
    @GetMapping("/staff/history/range")
    public ResponseEntity<?> staffHistoryRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "STAFF:");
            List<AttendanceResponse> list =
                    attendanceService.getMyAttendanceInRange(UserRole.STAFF, username, from, to);
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/attendance/staff/mark-leave */
    @PostMapping("/staff/mark-leave")
    public ResponseEntity<?> staffMarkLeave(
            @RequestBody AttendanceMarkRequest req,
            Authentication auth) {
        try {
            String username = extractUsername(auth.getName(), "STAFF:");
            AttendanceResponse r = attendanceService.markStatusByUsername(
                    UserRole.STAFF, username, req);
            return ResponseEntity.ok(r);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // ADMIN supervision endpoints (manage other users)
    // ══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/attendance/admin/daily?date=2025-04-04
     * All attendance records for a given date (all roles).
     */
    @GetMapping("/admin/daily")
    public ResponseEntity<?> adminDailyReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            LocalDate target = date != null ? date : LocalDate.now();
            return ResponseEntity.ok(attendanceService.getDailyReport(target));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/attendance/admin/range?from=2025-04-01&to=2025-04-30
     * All records in a date range (all roles).
     */
    @GetMapping("/admin/range")
    public ResponseEntity<?> adminRangeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        try {
            return ResponseEntity.ok(attendanceService.getRangeReport(from, to));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/attendance/admin/user/{role}/{userId}?from=...&to=...
     * Records for a specific user. role = TECHNICIAN | STAFF | ADMIN | MANAGER
     */
    @GetMapping("/admin/user/{role}/{userId}")
    public ResponseEntity<?> adminUserReport(
            @PathVariable String role,
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        try {
            UserRole ur = UserRole.valueOf(role.toUpperCase());
            List<AttendanceResponse> list = attendanceService.getUserReport(ur, userId, from, to);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role: " + role));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/attendance/admin/summary/{role}/{userId}?from=...&to=...
     * Stats summary card for one user.
     */
    @GetMapping("/admin/summary/{role}/{userId}")
    public ResponseEntity<?> adminUserSummary(
            @PathVariable String role,
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        try {
            UserRole ur = UserRole.valueOf(role.toUpperCase());
            AttendanceSummary summary = attendanceService.getSummary(ur, userId, from, to);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role: " + role));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/attendance/admin/mark/{role}/{userId}
     * Admin manually sets attendance status for any user on any date.
     */
    @PostMapping("/admin/mark/{role}/{userId}")
    public ResponseEntity<?> adminMarkAttendance(
            @PathVariable String role,
            @PathVariable Long userId,
            @RequestBody AttendanceMarkRequest req) {
        try {
            UserRole ur = UserRole.valueOf(role.toUpperCase());
            AttendanceResponse r = attendanceService.markStatus(ur, userId, req);
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/attendance/admin/{id}
     * Admin removes an erroneous attendance record.
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> adminDeleteRecord(@PathVariable Long id) {
        try {
            attendanceService.deleteRecord(id);
            return ResponseEntity.ok(Map.of("message", "Attendance record deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // ADMIN self-service attendance (admin clocks in/out for themselves)
    // ══════════════════════════════════════════════════════════════════════

    /**
     * POST /api/attendance/admin/clock-in
     * Admin clocks themselves in for today.
     */
    @PostMapping("/admin/clock-in")
    public ResponseEntity<?> adminClockIn(
            @RequestBody(required = false) AttendanceClockInRequest req,
            Authentication auth) {
        try {
            // Admin JWT subject is plain username (no prefix)
            String username = auth.getName();
            String notes = req != null ? req.getNotes() : null;
            AttendanceResponse r = attendanceService.clockIn(UserRole.ADMIN, username, notes);
            return ResponseEntity.ok(r);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/attendance/admin/clock-out
     * Admin clocks themselves out for today.
     */
    @PostMapping("/admin/clock-out")
    public ResponseEntity<?> adminClockOut(
            @RequestBody(required = false) AttendanceClockOutRequest req,
            Authentication auth) {
        try {
            String username = auth.getName();
            String notes = req != null ? req.getNotes() : null;
            AttendanceResponse r = attendanceService.clockOut(UserRole.ADMIN, username, notes);
            return ResponseEntity.ok(r);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/attendance/admin/my-today
     * Returns the admin's own attendance record for today.
     */
    @GetMapping("/admin/my-today")
    public ResponseEntity<?> adminMyToday(Authentication auth) {
        try {
            String username = auth.getName();
            AttendanceResponse r = attendanceService.getTodayRecord(UserRole.ADMIN, username);
            return ResponseEntity.ok(r != null ? r : Map.of("message", "Not clocked in yet"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/attendance/admin/my-history/range?from=...&to=...
     * Returns the admin's own attendance history over a date range.
     */
    @GetMapping("/admin/my-history/range")
    public ResponseEntity<?> adminMyHistoryRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication auth) {
        try {
            String username = auth.getName();
            List<AttendanceResponse> list =
                    attendanceService.getMyAttendanceInRange(UserRole.ADMIN, username, from, to);
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/attendance/admin/my-mark-leave
     * Admin marks their own leave / absent status.
     */
    @PostMapping("/admin/my-mark-leave")
    public ResponseEntity<?> adminMyMarkLeave(
            @RequestBody AttendanceMarkRequest req,
            Authentication auth) {
        try {
            String username = auth.getName();
            AttendanceResponse r =
                    attendanceService.markStatusByUsername(UserRole.ADMIN, username, req);
            return ResponseEntity.ok(r);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Strip the role prefix that is embedded in the JWT subject. */
    private String extractUsername(String principal, String prefix) {
        return principal.startsWith(prefix) ? principal.substring(prefix.length()) : principal;
    }
}