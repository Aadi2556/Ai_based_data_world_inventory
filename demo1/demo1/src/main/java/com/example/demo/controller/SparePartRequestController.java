// com/example/demo/controller/SparePartRequestController.java
package com.example.demo.controller;

import com.example.demo.dto.SparePartRequestDto;
import com.example.demo.model.SparePartRequestEntity;
import com.example.demo.service.SparePartRequestService;
import com.example.demo.service.TechnicianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class SparePartRequestController {

    @Autowired
    private SparePartRequestService requestService;

    @Autowired
    private TechnicianService technicianService;

    // ── Technician endpoints ──────────────────────────────────────────

    @PostMapping("/api/technician/spare-requests")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<?> submitRequest(@RequestBody SparePartRequestDto dto,
                                           Authentication auth) {
        try {
            String username = extractTechUsername(auth);
            String fullName = resolveTechName(username);
            SparePartRequestEntity saved = requestService.submitRequest(dto, username, fullName);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/technician/spare-requests")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<List<SparePartRequestEntity>> getMyRequests(Authentication auth) {
        String username = extractTechUsername(auth);
        return ResponseEntity.ok(requestService.getMyRequests(username));
    }

    // ── Admin endpoints ───────────────────────────────────────────────

    @GetMapping("/api/admin/spare-requests")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<SparePartRequestEntity>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping("/api/admin/spare-requests/pending-count")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Map<String, Long>> getPendingCount() {
        return ResponseEntity.ok(Map.of("count", requestService.countPending()));
    }

    @PutMapping("/api/admin/spare-requests/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody Map<String, String> body) {
        try {
            SparePartRequestEntity updated = requestService.updateStatus(
                    id,
                    body.get("status"),
                    body.get("adminNotes")
            );
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/api/admin/spare-requests/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {
        try {
            requestService.deleteRequest(id);
            return ResponseEntity.ok(Map.of("message", "Request deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private String extractTechUsername(Authentication auth) {
        String name = auth.getName();
        return name.startsWith("TECH:") ? name.substring(5) : name;
    }

    private String resolveTechName(String username) {
        try {
            var profile = technicianService.getProfile(username);
            return profile.getFirstName() + " " + profile.getLastName();
        } catch (Exception e) {
            return username;
        }
    }
}