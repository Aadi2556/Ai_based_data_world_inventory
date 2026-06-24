package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.StaffOperatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin-only management endpoints for Staff Operators.
 * Routes: /api/admin/staff/*
 *
 * All routes here require ROLE_ADMIN (enforced in SecurityConfig).
 */
@RestController
@RequestMapping("/api/admin/staff")
@CrossOrigin(origins = "*")
public class AdminStaffController {

    @Autowired
    private StaffOperatorService staffService;

    /** GET /api/admin/staff — list all staff (newest first) */
    @GetMapping
    public ResponseEntity<?> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    /** GET /api/admin/staff/active — list only active staff */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveStaff() {
        return ResponseEntity.ok(staffService.getActiveStaff());
    }

    /** GET /api/admin/staff/{id} — get one staff member's profile */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(staffService.getStaffById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /** POST /api/admin/staff — admin creates a new staff operator */
    @PostMapping
    public ResponseEntity<?> createStaff(@Valid @RequestBody StaffOperatorRegisterRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(staffService.adminCreateStaff(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PUT /api/admin/staff/{id} — admin updates any staff operator */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable Long id,
                                         @RequestBody StaffOperatorUpdateRequest req) {
        try {
            return ResponseEntity.ok(staffService.adminUpdateStaff(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/admin/staff/{id}/deactivate — soft-disable account */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateStaff(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(staffService.deactivateStaff(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/admin/staff/{id}/reactivate — re-enable account */
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivateStaff(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(staffService.reactivateStaff(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/admin/staff/{id} — hard-delete a staff operator */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Long id) {
        try {
            staffService.deleteStaff(id);
            return ResponseEntity.ok(Map.of("message", "Staff operator deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}