package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.MaintenanceService;
import com.example.demo.service.TechnicianService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/technician")
@CrossOrigin(origins = "*")
public class TechnicianController {

    @Autowired
    private TechnicianService technicianService;

    @Autowired
    private MaintenanceService maintenanceService;

    // ── Auth ──────────────────────────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody TechnicianRegisterRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(technicianService.register(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            return ResponseEntity.ok(technicianService.login(req));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ── Profile ───────────────────────────────────────────────────────────────

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        try {
            return ResponseEntity.ok(
                    technicianService.getProfile(extractUsername(auth)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody TechnicianUpdateRequest req,
                                           Authentication auth) {
        try {
            TechnicianProfileResponse updated =
                    technicianService.updateProfile(extractUsername(auth), req);
            return ResponseEntity.ok(
                    Map.of("message", "Profile updated", "technician", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── My Assigned Tasks ─────────────────────────────────────────────────────

    /**
     * Returns only the maintenance schedules assigned to the logged-in technician.
     * GET /api/technician/my-tasks
     */
    @GetMapping("/my-tasks")
    public ResponseEntity<?> getMyTasks(Authentication auth) {
        try {
            return ResponseEntity.ok(
                    maintenanceService.getSchedulesForTechnician(extractUsername(auth)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Allows a technician to update the status of a task assigned to them.
     * PATCH /api/technician/my-tasks/{id}/status
     *
     * Request body:
     * {
     *   "status": "IN_PROGRESS" | "COMPLETED",
     *   "notes": "optional progress note"
     * }
     */
    @PatchMapping("/my-tasks/{id}/status")
    public ResponseEntity<?> updateMyTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        try {
            String username = extractUsername(auth);
            String newStatus = body.get("status");
            String notes    = body.get("notes");

            if (newStatus == null || newStatus.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "status is required"));
            }

            return ResponseEntity.ok(
                    maintenanceService.updateTaskStatusByTechnician(
                            id, newStatus, notes, username));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // NOTE: DELETE /profile is intentionally absent.
    // Technicians cannot delete their own accounts.
    // Use DELETE /api/admin/technicians/{id} (admin/manager only).

    // ── Helper ────────────────────────────────────────────────────────────────

    /** JWT subject is "TECH:<username>" — strip prefix */
    private String extractUsername(Authentication auth) {
        String name = auth.getName();
        return name.startsWith("TECH:") ? name.substring(5) : name;
    }
}