package com.example.demo.controller;

import com.example.demo.dto.EnergyAnalyticsResponse;
import com.example.demo.dto.EnergyReadingRequest;
import com.example.demo.dto.EnergyReadingResponse;
import com.example.demo.service.EnergyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/energy")
@CrossOrigin(origins = "*")
public class EnergyController {

    @Autowired
    private EnergyService energyService;

    // ── CREATE: Log a new energy reading ──────────────────────────────
    @PostMapping("/readings")
    public ResponseEntity<?> create(
            @Valid @RequestBody EnergyReadingRequest request,
            Authentication authentication) {
        try {
            EnergyReadingResponse resp = energyService.create(request, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── READ ALL: List readings with optional filters ──────────────────
    //   ?search=  ?status=  ?from=2024-01-01T00:00:00  ?to=2024-12-31T23:59:59
    @GetMapping("/readings")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        try {
            List<EnergyReadingResponse> list = energyService.getAll(search, status, from, to);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── READ ONE ───────────────────────────────────────────────────────
    @GetMapping("/readings/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(energyService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ── UPDATE ─────────────────────────────────────────────────────────
    @PutMapping("/readings/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody EnergyReadingRequest request) {
        try {
            EnergyReadingResponse updated = energyService.update(id, request);
            return ResponseEntity.ok(Map.of("message", "Reading updated successfully", "reading", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE ─────────────────────────────────────────────────────────
    @DeleteMapping("/readings/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            energyService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Reading deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── ANALYTICS: Dashboard data + charts ───────────────────────────
    //   ?period=TODAY | WEEK | MONTH  (default: TODAY)
    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics(
            @RequestParam(defaultValue = "TODAY") String period) {
        try {
            EnergyAnalyticsResponse analytics = energyService.getAnalytics(period);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}