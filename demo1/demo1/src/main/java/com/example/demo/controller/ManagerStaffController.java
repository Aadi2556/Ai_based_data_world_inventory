package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.TechnicianService;
import com.example.demo.service.StaffOperatorService;
import com.example.demo.service.ManagerService;
import com.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Manager-facing staff & technician management endpoints.
 *
 * Base: /api/manager/staff-mgmt/**
 *
 * Managers can:
 *   - View all technicians and staff operators (list + individual)
 *   - View all managers and admins (read-only)
 *   - Update details of technicians and staff operators
 *   - Deactivate (soft-delete) technicians and staff operators
 *   - Remove a technician from active tasks
 *   - Change role assignments between technician/staff/manager/admin
 *
 * Managers CANNOT:
 *   - Hard-delete users (admin-only)
 *   - Create new users (admin-only)
 *
 * Security: ROLE_MANAGER enforced in SecurityConfig via
 *   .requestMatchers("/api/manager/staff-mgmt/**").hasRole("MANAGER")
 */
@RestController
@RequestMapping("/api/manager/staff-mgmt")
@CrossOrigin(origins = "*")
public class ManagerStaffController {

    @Autowired private TechnicianService    technicianService;
    @Autowired private StaffOperatorService staffOperatorService;
    @Autowired private ManagerService       managerService;
    @Autowired private AdminService         adminService;

    // ═══════════════════════════════════════════════════════════════════════
    // TECHNICIAN endpoints
    // ═══════════════════════════════════════════════════════════════════════

    /** GET /api/manager/staff-mgmt/technicians — list all technicians */
    @GetMapping("/technicians")
    public ResponseEntity<?> getAllTechnicians() {
        return ResponseEntity.ok(technicianService.getAllTechnicians());
    }

    /** GET /api/manager/staff-mgmt/technicians/active — active technicians only */
    @GetMapping("/technicians/active")
    public ResponseEntity<?> getActiveTechnicians() {
        return ResponseEntity.ok(technicianService.getActiveTechnicians());
    }

    /** GET /api/manager/staff-mgmt/technicians/{id} — single technician with workload */
    @GetMapping("/technicians/{id}")
    public ResponseEntity<?> getTechnicianById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(technicianService.getTechnicianById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** PUT /api/manager/staff-mgmt/technicians/{id} — update technician details */
    @PutMapping("/technicians/{id}")
    public ResponseEntity<?> updateTechnician(
            @PathVariable Long id,
            @RequestBody TechnicianUpdateRequest req) {
        try {
            return ResponseEntity.ok(technicianService.adminUpdateTechnician(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/manager/staff-mgmt/technicians/{id}/deactivate — soft-deactivate */
    @PatchMapping("/technicians/{id}/deactivate")
    public ResponseEntity<?> deactivateTechnician(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(technicianService.deactivateTechnician(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/manager/staff-mgmt/technicians/{id}/reactivate — reactivate account */
    @PatchMapping("/technicians/{id}/reactivate")
    public ResponseEntity<?> reactivateTechnician(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(technicianService.reactivateTechnician(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/manager/staff-mgmt/technicians/{id}/remove-from-tasks — clear assignments */
    @PatchMapping("/technicians/{id}/remove-from-tasks")
    public ResponseEntity<?> removeFromTasks(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(technicianService.removeFromTask(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STAFF OPERATOR endpoints
    // ═══════════════════════════════════════════════════════════════════════

    /** GET /api/manager/staff-mgmt/staff — list all staff operators */
    @GetMapping("/staff")
    public ResponseEntity<?> getAllStaff() {
        return ResponseEntity.ok(staffOperatorService.getAllStaff());
    }

    /** GET /api/manager/staff-mgmt/staff/active — active staff only */
    @GetMapping("/staff/active")
    public ResponseEntity<?> getActiveStaff() {
        return ResponseEntity.ok(staffOperatorService.getActiveStaff());
    }

    /** GET /api/manager/staff-mgmt/staff/{id} — single staff operator */
    @GetMapping("/staff/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(staffOperatorService.getStaffById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** PUT /api/manager/staff-mgmt/staff/{id} — update staff operator details */
    @PutMapping("/staff/{id}")
    public ResponseEntity<?> updateStaff(
            @PathVariable Long id,
            @RequestBody StaffOperatorUpdateRequest req) {
        try {
            return ResponseEntity.ok(staffOperatorService.adminUpdateStaff(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/manager/staff-mgmt/staff/{id}/deactivate — soft-deactivate */
    @PatchMapping("/staff/{id}/deactivate")
    public ResponseEntity<?> deactivateStaff(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(staffOperatorService.deactivateStaff(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/manager/staff-mgmt/staff/{id}/reactivate — reactivate account */
    @PatchMapping("/staff/{id}/reactivate")
    public ResponseEntity<?> reactivateStaff(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(staffOperatorService.reactivateStaff(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MANAGER endpoints (read-only for managers viewing managers)
    // ═══════════════════════════════════════════════════════════════════════

    /** GET /api/manager/staff-mgmt/managers — list all managers */
    @GetMapping("/managers")
    public ResponseEntity<?> getAllManagers() {
        try {
            return ResponseEntity.ok(managerService.getAllManagers());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/manager/staff-mgmt/managers/{id} — single manager */
    @GetMapping("/managers/{id}")
    public ResponseEntity<?> getManagerById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(managerService.getManagerById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** PUT /api/manager/staff-mgmt/managers/{id} — update manager details */
    @PutMapping("/managers/{id}")
    public ResponseEntity<?> updateManager(
            @PathVariable Long id,
            @RequestBody ManagerUpdateRequest req) {
        try {
            return ResponseEntity.ok(managerService.adminUpdateManager(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/manager/staff-mgmt/managers/{id}/deactivate — deactivate manager */
    @PatchMapping("/managers/{id}/deactivate")
    public ResponseEntity<?> deactivateManager(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(managerService.deactivateManager(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/manager/staff-mgmt/managers/{id}/reactivate — reactivate account */
    @PatchMapping("/managers/{id}/reactivate")
    public ResponseEntity<?> reactivateManager(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(managerService.reactivateManager(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ADMIN endpoints (read-only for managers viewing admins)
    // ═══════════════════════════════════════════════════════════════════════

    /** GET /api/manager/staff-mgmt/admins — list all admins */
    @GetMapping("/admins")
    public ResponseEntity<?> getAllAdmins() {
        try {
            return ResponseEntity.ok(adminService.getAllAdmins());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/manager/staff-mgmt/admins/{id}/deactivate — deactivate admin */
    @PatchMapping("/admins/{id}/deactivate")
    public ResponseEntity<?> deactivateAdmin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminService.deactivateAdmin(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/manager/staff-mgmt/admins/{id}/reactivate — reactivate account */
    @PatchMapping("/admins/{id}/reactivate")
    public ResponseEntity<?> reactivateAdmin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminService.reactivateAdmin(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROLE CHANGE endpoint
    // ═══════════════════════════════════════════════════════════════════════

    @Autowired
    private com.example.demo.service.RoleChangeService roleChangeService;

    /** POST /api/manager/staff-mgmt/role-change — change role of a user */
    @PostMapping("/role-change")
    public ResponseEntity<?> changeRole(@RequestBody Map<String, Object> payload) {
        try {
            String fromRole = (String) payload.get("fromRole");
            String toRole = (String) payload.get("toRole");
            Long fromId = Long.parseLong(payload.get("fromId").toString());

            if (fromRole == null || fromRole.isBlank())
                return ResponseEntity.badRequest().body(Map.of("error", "fromRole is required"));
            if (toRole == null || toRole.isBlank())
                return ResponseEntity.badRequest().body(Map.of("error", "toRole is required"));
            if (fromId == null)
                return ResponseEntity.badRequest().body(Map.of("error", "fromId is required"));
            if (fromRole.equalsIgnoreCase(toRole))
                return ResponseEntity.badRequest().body(Map.of("error", "Source and target roles are the same"));

            String result = roleChangeService.changeRole(fromRole, fromId, toRole);
            return ResponseEntity.ok(Map.of("message", result));

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}