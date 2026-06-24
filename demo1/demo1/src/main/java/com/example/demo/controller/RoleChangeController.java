package com.example.demo.controller;

import com.example.demo.dto.RoleChangeRequest;
import com.example.demo.service.RoleChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin-only endpoint: change a user's role across tables.
 *
 * POST /api/admin/role-change
 * Body: { "fromRole": "technician", "fromId": 3, "toRole": "manager" }
 *
 * IMPORTANT – no @Transactional here.
 * The transaction is managed inside RoleChangeService. Putting @Transactional
 * on the controller and then catching the exception in the same method causes
 * Spring to throw UnexpectedRollbackException because the transaction is
 * already marked rollback-only by the time the catch block runs.
 */
@RestController
@RequestMapping("/api/admin/role-change")
@CrossOrigin(origins = "*")
public class RoleChangeController {

    @Autowired
    private RoleChangeService roleChangeService;

    @PostMapping
    public ResponseEntity<?> changeRole(@RequestBody RoleChangeRequest req) {

        // ── Basic validation (no DB access, so no transaction needed) ─────────
        if (req.getFromRole() == null || req.getFromRole().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "fromRole is required"));
        if (req.getToRole() == null || req.getToRole().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "toRole is required"));
        if (req.getFromId() == null)
            return ResponseEntity.badRequest().body(Map.of("error", "fromId is required"));
        if (req.getFromRole().equalsIgnoreCase(req.getToRole()))
            return ResponseEntity.badRequest().body(Map.of("error", "Source and target roles are the same"));

        // ── Delegate to service (transaction lives there) ─────────────────────
        try {
            String result = roleChangeService.changeRole(
                    req.getFromRole(), req.getFromId(), req.getToRole());

            return ResponseEntity.ok(Map.of(
                    "message",  result,
                    "fromRole", req.getFromRole(),
                    "toRole",   req.getToRole(),
                    "fromId",   req.getFromId()
            ));

        } catch (IllegalArgumentException e) {
            // Conflict checks or unknown role values → 400
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        } catch (RuntimeException e) {
            // Record-not-found or unexpected DB error → 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}