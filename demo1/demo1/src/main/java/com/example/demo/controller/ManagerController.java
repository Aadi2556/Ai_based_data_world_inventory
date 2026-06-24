package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin(origins = "*")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody ManagerRegisterRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(managerService.register(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            return ResponseEntity.ok(managerService.login(req));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        try {
            return ResponseEntity.ok(managerService.getProfile(extractUsername(auth)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody ManagerUpdateRequest req, Authentication auth) {
        try {
            ManagerProfileResponse updated = managerService.updateProfile(extractUsername(auth), req);
            return ResponseEntity.ok(Map.of("message", "Profile updated", "manager", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteProfile(Authentication auth) {
        try {
            managerService.deleteProfile(extractUsername(auth));
            return ResponseEntity.ok(Map.of("message", "Account deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String extractUsername(Authentication auth) {
        String name = auth.getName();
        return name.startsWith("MANAGER:") ? name.substring(8) : name;
    }
}