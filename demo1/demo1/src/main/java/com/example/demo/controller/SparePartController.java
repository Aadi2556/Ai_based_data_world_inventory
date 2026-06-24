// com/example/demo/controller/SparePartController.java
package com.example.demo.controller;

import com.example.demo.dto.SparePartRequest;
import com.example.demo.dto.SparePartResponse;
import com.example.demo.dto.SparePartStatsResponse;
import com.example.demo.service.SparePartService;
import com.example.demo.repository.SparePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/spareparts")
@CrossOrigin(origins = "*")
public class SparePartController {

    @Autowired
    private SparePartService sparePartService;

    @Autowired
    private SparePartRepository sparePartRepository;

    // CREATE new spare part
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> create(@RequestBody SparePartRequest request, Authentication auth) {
        try {
            String createdBy = auth.getName();
            return ResponseEntity.ok(sparePartService.create(request, createdBy));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET all parts (technician can view)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','MANAGER','TECHNICIAN')")
    public ResponseEntity<List<SparePartResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String stock) {
        return ResponseEntity.ok(sparePartService.getAll(search, category, stock));
    }

    // GET single part
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','MANAGER','TECHNICIAN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(sparePartService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // UPDATE spare part
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SparePartRequest request) {
        try {
            return ResponseEntity.ok(sparePartService.update(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE spare part
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            sparePartService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Spare part deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET stats (for admin notification panel)
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','MANAGER','TECHNICIAN')")
    public ResponseEntity<SparePartStatsResponse> getStats() {
        return ResponseEntity.ok(sparePartService.getStats());
    }

    // PATCH adjust stock — technician adjusts, triggers low-stock alert if needed
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIAN','MANAGER')")
    public ResponseEntity<?> adjustStock(@PathVariable Long id,
                                         @RequestParam int delta) {
        try {
            SparePartResponse updated = sparePartService.adjustStock(id, delta);
            boolean isLow = "LOW".equals(updated.getStockStatus())
                    || "OUT".equals(updated.getStockStatus());

            return ResponseEntity.ok(Map.of(
                    "part",      updated,
                    "lowStock",  isLow,
                    "message",   isLow
                            ? "⚠️ Stock is " + updated.getStockStatus() + " for " + updated.getPartName()
                            : "Stock updated successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET low-stock parts — admin supplier dashboard notification endpoint
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<SparePartResponse>> getLowStock() {
        return ResponseEntity.ok(
                sparePartRepository.findLowStock().stream()
                        .map(sparePartService::toResponse)
                        .toList()
        );
    }
}