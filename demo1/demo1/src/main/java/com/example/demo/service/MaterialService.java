package com.example.demo.service;

import com.example.demo.dto.MaterialRequest;
import com.example.demo.dto.UsageLogRequest;
import com.example.demo.model.Material;
import com.example.demo.model.MaterialUsageLog;
import com.example.demo.model.StockAlert;
import com.example.demo.repository.MaterialRepository;
import com.example.demo.repository.MaterialUsageLogRepository;
import com.example.demo.repository.StockAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialUsageLogRepository usageLogRepository;

    @Autowired
    private StockAlertRepository alertRepository;

    // ===================== MATERIAL CRUD =====================

    public List<Material> getAllMaterials() {
        return materialRepository.findByIsActiveTrue();
    }

    public Optional<Material> getMaterialById(Long id) {
        return materialRepository.findById(id);
    }

    @Transactional
    public Material addMaterial(MaterialRequest request) {
        Material material = mapToMaterial(new Material(), request);
        Material saved = materialRepository.save(material);
        checkAndGenerateAlerts(saved);
        return saved;
    }

    @Transactional
    public Material updateMaterial(Long id, MaterialRequest request) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found: " + id));
        mapToMaterial(material, request);
        Material saved = materialRepository.save(material);
        checkAndGenerateAlerts(saved);
        return saved;
    }

    @Transactional
    public void deleteMaterial(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found: " + id));
        material.setIsActive(false); // Soft delete
        materialRepository.save(material);
    }

    private Material mapToMaterial(Material m, MaterialRequest r) {
        m.setName(r.getName());
        m.setDescription(r.getDescription());
        if (r.getCategory() != null && !r.getCategory().isEmpty())
            m.setCategory(Material.Category.valueOf(r.getCategory()));
        if (r.getUnit() != null && !r.getUnit().isEmpty())
            m.setUnit(Material.Unit.valueOf(r.getUnit()));
        if (r.getCurrentStock() != null) m.setCurrentStock(r.getCurrentStock());
        if (r.getMinimumStockLevel() != null) m.setMinimumStockLevel(r.getMinimumStockLevel());
        if (r.getCriticalStockLevel() != null) m.setCriticalStockLevel(r.getCriticalStockLevel());
        if (r.getMaximumStockLevel() != null) m.setMaximumStockLevel(r.getMaximumStockLevel());
        if (r.getUnitCost() != null) m.setUnitCost(r.getUnitCost());
        m.setSupplierName(r.getSupplierName());
        m.setSkuCode(r.getSkuCode());
        m.setBrand(r.getBrand());
        return m;
    }

    // ===================== USAGE / WASTAGE LOGGING =====================

    @Transactional
    public MaterialUsageLog logUsage(UsageLogRequest request) {
        Material material = materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new RuntimeException("Material not found"));

        MaterialUsageLog log = new MaterialUsageLog();
        log.setMaterial(material);
        log.setLogType(MaterialUsageLog.LogType.valueOf(request.getLogType()));
        log.setQuantity(request.getQuantity());
        log.setStockBefore(material.getCurrentStock());
        log.setJobReference(request.getJobReference());
        log.setWastageReason(request.getWastageReason());
        log.setNotes(request.getNotes());
        log.setLoggedBy(request.getLoggedBy());
        log.setLogDate(request.getLogDate() != null ? request.getLogDate() : LocalDate.now());

        // Adjust stock
        switch (log.getLogType()) {
            case USAGE, WASTAGE -> material.setCurrentStock(
                    Math.max(0, material.getCurrentStock() - request.getQuantity()));
            case RESTOCK, ADJUSTMENT -> material.setCurrentStock(
                    material.getCurrentStock() + request.getQuantity());
        }
        log.setStockAfter(material.getCurrentStock());

        materialRepository.save(material);
        MaterialUsageLog saved = usageLogRepository.save(log);

        // Intelligent alert check after every stock change
        checkAndGenerateAlerts(material);

        return saved;
    }

    public List<MaterialUsageLog> getAllUsageLogs() {
        return usageLogRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<MaterialUsageLog> getLogsByMaterial(Long materialId) {
        return usageLogRepository.findByMaterialIdOrderByCreatedAtDesc(materialId);
    }

    public List<MaterialUsageLog> getLogsByType(String type) {
        return usageLogRepository.findByLogTypeOrderByCreatedAtDesc(
                MaterialUsageLog.LogType.valueOf(type));
    }

    // ===================== ALERT SYSTEM =====================

    /**
     * Intelligent alert engine — evaluates each material against thresholds
     * and generates or resolves alerts automatically.
     */
    @Transactional
    public void checkAndGenerateAlerts(Material material) {
        double stock = material.getCurrentStock();

        // Resolve old alerts for this material first
        List<StockAlert> existing = alertRepository.findByMaterialIdOrderByCreatedAtDesc(material.getId());
        existing.stream()
                .filter(a -> !a.getIsResolved())
                .forEach(a -> {
                    a.setIsResolved(true);
                    alertRepository.save(a);
                });

        // CRITICAL threshold
        if (stock <= material.getCriticalStockLevel()) {
            createAlert(material, StockAlert.AlertType.CRITICAL_STOCK, StockAlert.AlertSeverity.CRITICAL,
                    String.format("🚨 CRITICAL: %s is nearly out! Current stock: %.1f %s (Critical level: %.1f)",
                            material.getName(), stock, material.getUnit(), material.getCriticalStockLevel()), stock);
        }
        // LOW threshold (only if not critical)
        else if (stock <= material.getMinimumStockLevel()) {
            createAlert(material, StockAlert.AlertType.LOW_STOCK, StockAlert.AlertSeverity.WARNING,
                    String.format("⚠️ LOW STOCK: %s is running low. Current: %.1f %s (Min: %.1f)",
                            material.getName(), stock, material.getUnit(), material.getMinimumStockLevel()), stock);
        }
        // OVERSTOCK
        else if (stock >= material.getMaximumStockLevel() * 0.9) {
            createAlert(material, StockAlert.AlertType.OVERSTOCKED, StockAlert.AlertSeverity.INFO,
                    String.format("📦 OVERSTOCKED: %s has excess stock. Current: %.1f %s (Max: %.1f)",
                            material.getName(), stock, material.getUnit(), material.getMaximumStockLevel()), stock);
        }
    }

    private void createAlert(Material material, StockAlert.AlertType type,
                             StockAlert.AlertSeverity severity, String message, double stock) {
        StockAlert alert = new StockAlert();
        alert.setMaterial(material);
        alert.setAlertType(type);
        alert.setSeverity(severity);
        alert.setMessage(message);
        alert.setStockLevelAtAlert(stock);
        alertRepository.save(alert);
    }

    public List<StockAlert> getActiveAlerts() {
        return alertRepository.findByIsResolvedFalseOrderByCreatedAtDesc();
    }

    public List<StockAlert> getUnacknowledgedAlerts() {
        return alertRepository.findByIsAcknowledgedFalseAndIsResolvedFalseOrderByCreatedAtDesc();
    }

    @Transactional
    public StockAlert acknowledgeAlert(Long alertId, String adminName) {
        StockAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setIsAcknowledged(true);
        alert.setAcknowledgedBy(adminName);
        alert.setAcknowledgedAt(java.time.LocalDateTime.now());
        return alertRepository.save(alert);
    }

    @Transactional
    public void resolveAlert(Long alertId) {
        StockAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setIsResolved(true);
        alertRepository.save(alert);
    }

    // ===================== DASHBOARD STATS =====================

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Material> all = materialRepository.findByIsActiveTrue();
        stats.put("totalMaterials", all.size());
        stats.put("lowStockCount", materialRepository.findLowStockMaterials().size());
        stats.put("criticalStockCount", materialRepository.findCriticalStockMaterials().size());
        stats.put("overstockedCount", materialRepository.findOverstockedMaterials().size());
        stats.put("totalInventoryValue", materialRepository.getTotalInventoryValue());
        stats.put("activeAlertsCount", alertRepository.countByIsResolvedFalseAndIsAcknowledgedFalse());

        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate today = LocalDate.now();
        Double monthlyUsage = usageLogRepository.getTotalUsageInPeriod(monthStart, today);
        Double monthlyWastage = usageLogRepository.getTotalWastageInPeriod(monthStart, today);
        stats.put("monthlyUsage", monthlyUsage != null ? monthlyUsage : 0.0);
        stats.put("monthlyWastage", monthlyWastage != null ? monthlyWastage : 0.0);
        return stats;
    }

    // Run alert check for ALL active materials (scheduled or on-demand)
    @Transactional
    public void runFullAlertScan() {
        materialRepository.findByIsActiveTrue().forEach(this::checkAndGenerateAlerts);
    }
}