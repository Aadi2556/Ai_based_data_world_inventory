package com.example.demo.controller;

import com.example.demo.dto.MaterialRequestDto;
import com.example.demo.model.MaterialRequestEntity;
import com.example.demo.service.MaterialRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Material Request endpoints.
 *
 * Staff  → /api/staff/material-requests  (ROLE_STAFF)
 * Admin  → /api/admin/material-requests  (ROLE_ADMIN)
 */
@RestController
@CrossOrigin(origins = "*")
public class MaterialRequestController {

    @Autowired
    private MaterialRequestService materialRequestService;

    // ══════════════════════════════════════════════════════
    //  STAFF endpoints
    // ══════════════════════════════════════════════════════

    /**
     * Submit a new material request.
     * POST /api/staff/material-requests
     */
    @PostMapping("/api/staff/material-requests")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> submitRequest(
            @RequestBody MaterialRequestDto dto,
            Authentication auth) {
        try {
            String username  = extractUsername(auth);
            String staffName = extractDisplayName(auth);
            MaterialRequestEntity saved = materialRequestService.submitRequest(dto, username, staffName);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get own requests.
     * GET /api/staff/material-requests
     */
    @GetMapping("/api/staff/material-requests")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<MaterialRequestEntity>> getMyRequests(Authentication auth) {
        return ResponseEntity.ok(
                materialRequestService.getMyRequests(extractUsername(auth)));
    }

    // ══════════════════════════════════════════════════════
    //  ADMIN endpoints
    // ══════════════════════════════════════════════════════

    /**
     * Get all material requests (with optional status filter).
     * GET /api/admin/material-requests?status=PENDING
     */
    @GetMapping("/api/admin/material-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MaterialRequestEntity>> getAllRequests(
            @RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(materialRequestService.getRequestsByStatus(status));
        }
        return ResponseEntity.ok(materialRequestService.getAllRequests());
    }

    /**
     * Get pending count / status breakdown for badge display.
     * GET /api/admin/material-requests/counts
     */
    @GetMapping("/api/admin/material-requests/counts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getCounts() {
        return ResponseEntity.ok(materialRequestService.getStatusCounts());
    }

    /**
     * Update status (REVIEWED / ORDERED / REJECTED).
     * PUT /api/admin/material-requests/{id}/status
     * Body: { "status": "ORDERED", "adminNotes": "..." }
     */
    @PutMapping("/api/admin/material-requests/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            MaterialRequestEntity updated = materialRequestService.updateStatus(
                    id,
                    body.getOrDefault("status", "REVIEWED"),
                    body.get("adminNotes"));
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete a request.
     * DELETE /api/admin/material-requests/{id}
     */
    @DeleteMapping("/api/admin/material-requests/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {
        try {
            materialRequestService.deleteRequest(id);
            return ResponseEntity.ok(Map.of("message", "Request deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Staff JWT subject is "STAFF:<username>" — strip prefix if present.
     */
    private String extractUsername(Authentication auth) {
        String name = auth.getName();
        if (name.startsWith("STAFF:")) return name.substring(6);
        if (name.startsWith("TECH:"))  return name.substring(5);
        return name;
    }

    private String extractDisplayName(Authentication auth) {
        // Fallback: just return the username as display name.
        // If your StaffOperator entity is available via auth.getPrincipal()
        // you can enrich this.
        return extractUsername(auth);
    }
}