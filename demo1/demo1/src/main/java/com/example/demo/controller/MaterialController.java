package com.example.demo.controller;

import com.example.demo.dto.MaterialRequest;
import com.example.demo.dto.UsageLogRequest;
import com.example.demo.model.Material;
import com.example.demo.model.MaterialUsageLog;
import com.example.demo.model.StockAlert;
import com.example.demo.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/materials")          // ← removed the /api prefix here
@PreAuthorize("hasRole('ADMIN')")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    // ===================== PAGE =====================

    @GetMapping
    public String materialsPage(Model model) {
        model.addAttribute("materials", materialService.getAllMaterials());
        model.addAttribute("alerts", materialService.getActiveAlerts());
        model.addAttribute("stats", materialService.getDashboardStats());
        return "materials";
    }

    // ===================== MATERIAL CRUD API =====================

    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<Material>> getAllMaterials() {
        return ResponseEntity.ok(materialService.getAllMaterials());
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Material> getMaterial(@PathVariable Long id) {
        return materialService.getMaterialById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<?> addMaterial(@RequestBody MaterialRequest request) {
        try {
            return ResponseEntity.ok(materialService.addMaterial(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/api/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateMaterial(@PathVariable Long id, @RequestBody MaterialRequest request) {
        try {
            return ResponseEntity.ok(materialService.updateMaterial(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteMaterial(@PathVariable Long id) {
        try {
            materialService.deleteMaterial(id);
            return ResponseEntity.ok(Map.of("message", "Material deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===================== USAGE & WASTAGE LOG API =====================

    @GetMapping("/api/logs")
    @ResponseBody
    public ResponseEntity<List<MaterialUsageLog>> getAllLogs() {
        return ResponseEntity.ok(materialService.getAllUsageLogs());
    }

    @GetMapping("/api/logs/material/{materialId}")
    @ResponseBody
    public ResponseEntity<List<MaterialUsageLog>> getLogsByMaterial(@PathVariable Long materialId) {
        return ResponseEntity.ok(materialService.getLogsByMaterial(materialId));
    }

    @GetMapping("/api/logs/type/{type}")
    @ResponseBody
    public ResponseEntity<List<MaterialUsageLog>> getLogsByType(@PathVariable String type) {
        return ResponseEntity.ok(materialService.getLogsByType(type));
    }

    @PostMapping("/api/logs/add")
    @ResponseBody
    public ResponseEntity<?> addLog(@RequestBody UsageLogRequest request) {
        try {
            return ResponseEntity.ok(materialService.logUsage(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===================== ALERT API =====================

    @GetMapping("/api/alerts")
    @ResponseBody
    public ResponseEntity<List<StockAlert>> getAlerts() {
        return ResponseEntity.ok(materialService.getActiveAlerts());
    }

    @GetMapping("/api/alerts/unacknowledged")
    @ResponseBody
    public ResponseEntity<List<StockAlert>> getUnacknowledgedAlerts() {
        return ResponseEntity.ok(materialService.getUnacknowledgedAlerts());
    }

    @PutMapping("/api/alerts/{id}/acknowledge")
    @ResponseBody
    public ResponseEntity<?> acknowledgeAlert(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(materialService.acknowledgeAlert(id,
                    body.getOrDefault("adminName", "Admin")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/api/alerts/{id}/resolve")
    @ResponseBody
    public ResponseEntity<?> resolveAlert(@PathVariable Long id) {
        try {
            materialService.resolveAlert(id);
            return ResponseEntity.ok(Map.of("message", "Alert resolved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/alerts/scan")
    @ResponseBody
    public ResponseEntity<?> runAlertScan() {
        materialService.runFullAlertScan();
        return ResponseEntity.ok(Map.of("message", "Alert scan completed"));
    }

    // ===================== STATS API =====================

    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(materialService.getDashboardStats());
    }
}