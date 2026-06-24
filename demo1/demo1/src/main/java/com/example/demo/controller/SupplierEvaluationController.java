package com.example.demo.controller;

import com.example.demo.dto.SupplierEvaluationRequest;
import com.example.demo.dto.SupplierPerformanceSummary;
import com.example.demo.model.SupplierEvaluation;
import com.example.demo.service.SupplierEvaluationService;
import com.example.demo.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/suppliers/evaluations")
@PreAuthorize("hasRole('ADMIN')")
public class SupplierEvaluationController {

    @Autowired
    private SupplierEvaluationService evalService;

    @Autowired
    private SupplierService supplierService;

    // ── PAGE ──
    @GetMapping
    public String evaluationsPage(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("leaderboard", evalService.getLeaderboard());
        model.addAttribute("stats", evalService.getDashboardStats());
        return "supplier-evaluations";
    }

    // ── EVALUATIONS CRUD ──

    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<SupplierEvaluation>> getAllEvaluations() {
        return ResponseEntity.ok(evalService.getAllEvaluations());
    }

    @GetMapping("/api/supplier/{supplierId}")
    @ResponseBody
    public ResponseEntity<List<SupplierEvaluation>> getBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(evalService.getEvaluationsBySupplier(supplierId));
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<SupplierEvaluation> getById(@PathVariable Long id) {
        return evalService.getEvaluationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody SupplierEvaluationRequest request) {
        try {
            return ResponseEntity.ok(evalService.createEvaluation(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/api/update/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SupplierEvaluationRequest request) {
        try {
            return ResponseEntity.ok(evalService.updateEvaluation(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            evalService.deleteEvaluation(id);
            return ResponseEntity.ok(Map.of("message", "Evaluation deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── LEADERBOARD & SUMMARIES ──

    @GetMapping("/api/leaderboard")
    @ResponseBody
    public ResponseEntity<List<SupplierPerformanceSummary>> getLeaderboard() {
        return ResponseEntity.ok(evalService.getLeaderboard());
    }

    @GetMapping("/api/summary/{supplierId}")
    @ResponseBody
    public ResponseEntity<?> getSupplierSummary(@PathVariable Long supplierId) {
        SupplierPerformanceSummary summary = evalService.getSupplierSummary(supplierId);
        if (summary == null) return ResponseEntity.ok(Map.of("message", "No evaluations yet"));
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/api/trend/{supplierId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getTrend(@PathVariable Long supplierId) {
        return ResponseEntity.ok(evalService.getSupplierTrendData(supplierId));
    }

    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(evalService.getDashboardStats());
    }
}