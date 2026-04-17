package com.example.demo.controller;

import com.example.demo.dto.MachineRequest;
import com.example.demo.dto.MaintenanceScheduleRequest;
import com.example.demo.model.Machine;
import com.example.demo.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "*")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    // ── Machines ──────────────────────────────────────────────────────────────

    @PostMapping("/machines")
    public ResponseEntity<?> createMachine(@RequestBody MachineRequest req) {
        try {
            Machine m = maintenanceService.createMachine(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(m);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/machines")
    public ResponseEntity<?> getMachines() {
        return ResponseEntity.ok(maintenanceService.getAllActiveMachines());
    }

    // ── Schedules ─────────────────────────────────────────────────────────────

    // CREATE
    @PostMapping("/schedules")
    public ResponseEntity<?> createSchedule(@RequestBody MaintenanceScheduleRequest req,
                                            Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(maintenanceService.createSchedule(req, authentication.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // READ ALL
    @GetMapping("/schedules")
    public ResponseEntity<?> getAllSchedules() {
        return ResponseEntity.ok(maintenanceService.getAllSchedules());
    }

    // READ ONE
    @GetMapping("/schedules/{id}")
    public ResponseEntity<?> getSchedule(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(maintenanceService.getScheduleById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // UPDATE
    @PutMapping("/schedules/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id,
                                            @RequestBody MaintenanceScheduleRequest req) {
        try {
            return ResponseEntity.ok(maintenanceService.updateSchedule(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // CANCEL
    @PatchMapping("/schedules/{id}/cancel")
    public ResponseEntity<?> cancelSchedule(@PathVariable Long id) {
        try {
            maintenanceService.cancelSchedule(id);
            return ResponseEntity.ok(Map.of("message", "Schedule cancelled successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE
    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        try {
            maintenanceService.deleteSchedule(id);
            return ResponseEntity.ok(Map.of("message", "Schedule deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}