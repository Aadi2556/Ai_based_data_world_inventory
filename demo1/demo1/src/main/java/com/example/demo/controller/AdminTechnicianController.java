package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.TechnicianService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin-only technician management endpoints.
 * Routes: /api/admin/technicians/*
 * All routes are protected by JWT (any authenticated admin via SecurityConfig).
 */
@RestController
@RequestMapping("/api/admin/technicians")
@CrossOrigin(origins = "*")
public class AdminTechnicianController {

    @Autowired
    private TechnicianService technicianService;

    // LIST ALL
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(technicianService.getAllTechnicians());
    }

    // LIST ACTIVE ONLY
    @GetMapping("/active")
    public ResponseEntity<?> getActive() {
        return ResponseEntity.ok(technicianService.getActiveTechnicians());
    }

    // GET ONE (with workload)
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(technicianService.getTechnicianById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // CREATE (admin adds a technician)
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TechnicianRegisterRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(technicianService.adminCreateTechnician(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // UPDATE details / availability / assignment
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody TechnicianUpdateRequest req) {
        try {
            return ResponseEntity.ok(technicianService.adminUpdateTechnician(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // REMOVE FROM ALL ACTIVE TASKS (clears assignment, sets AVAILABLE)
    @PatchMapping("/{id}/remove-from-tasks")
    public ResponseEntity<?> removeFromTasks(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(technicianService.removeFromTask(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DEACTIVATE (soft delete)
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(technicianService.deactivateTechnician(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // REACTIVATE
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivate(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(technicianService.reactivateTechnician(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // HARD DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            technicianService.deleteTechnician(id);
            return ResponseEntity.ok(Map.of("message", "Technician deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}