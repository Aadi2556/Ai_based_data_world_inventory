package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.StaffOperatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Self-service endpoints for Staff Operators.
 * Routes: /api/staff/*
 *
 * DELETE /profile has been removed — staff cannot delete their own accounts.
 * Only admins and managers can delete staff accounts
 * via /api/admin/staff/{id}.
 */
@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffOperatorController {

    @Autowired
    private StaffOperatorService staffService;

    // ── Auth ──────────────────────────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody StaffOperatorRegisterRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(staffService.register(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            return ResponseEntity.ok(staffService.login(req));
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
                    staffService.getProfile(extractUsername(auth)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody StaffOperatorUpdateRequest req,
            Authentication auth) {
        try {
            StaffOperatorProfileResponse updated =
                    staffService.updateProfile(extractUsername(auth), req);
            return ResponseEntity.ok(
                    Map.of("message", "Profile updated", "staff", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // NOTE: DELETE /profile is intentionally absent.
    // Staff cannot delete their own accounts.
    // Use DELETE /api/admin/staff/{id} (admin/manager only).

    // ── Helper ────────────────────────────────────────────────────────────────

    private String extractUsername(Authentication auth) {
        String name = auth.getName();
        return name.startsWith("STAFF:") ? name.substring(6) : name;
    }
}