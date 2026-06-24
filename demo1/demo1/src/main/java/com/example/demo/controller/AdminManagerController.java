package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/managers")
@CrossOrigin(origins = "*")
public class AdminManagerController {

    @Autowired
    private ManagerService managerService;

    @GetMapping
    public ResponseEntity<?> getAllManagers() {
        return ResponseEntity.ok(managerService.getAllManagers());
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveManagers() {
        return ResponseEntity.ok(managerService.getActiveManagers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getManagerById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(managerService.getManagerById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createManager(@Valid @RequestBody ManagerRegisterRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(managerService.adminCreateManager(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateManager(@PathVariable Long id, @RequestBody ManagerUpdateRequest req) {
        try {
            return ResponseEntity.ok(managerService.adminUpdateManager(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateManager(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(managerService.deactivateManager(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivateManager(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(managerService.reactivateManager(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteManager(@PathVariable Long id) {
        try {
            managerService.deleteManager(id);
            return ResponseEntity.ok(Map.of("message", "Manager deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}