package com.example.demo.controller;

import com.example.demo.dto.RepairLogRequest;
import com.example.demo.service.RepairLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/repairs")
@CrossOrigin(origins = "*")
public class RepairLogController {

    @Autowired
    private RepairLogService repairLogService;

    // CREATE
    @PostMapping
    public ResponseEntity<?> createRepairLog(@RequestBody RepairLogRequest req,
                                             Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(repairLogService.createRepairLog(req, authentication.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<?> getAllRepairLogs() {
        return ResponseEntity.ok(repairLogService.getAllRepairLogs());
    }

    // READ ALL BY MACHINE
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<?> getByMachine(@PathVariable Long machineId) {
        return ResponseEntity.ok(repairLogService.getRepairLogsByMachine(machineId));
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<?> getRepairLog(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(repairLogService.getRepairLogById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRepairLog(@PathVariable Long id,
                                             @RequestBody RepairLogRequest req) {
        try {
            return ResponseEntity.ok(repairLogService.updateRepairLog(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRepairLog(@PathVariable Long id) {
        try {
            repairLogService.deleteRepairLog(id);
            return ResponseEntity.ok(Map.of("message", "Repair log deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // STATS
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(repairLogService.getStats());
    }
}