package com.example.demo.service;

import com.example.demo.dto.MaterialRequestDto;
import com.example.demo.model.Material;
import com.example.demo.model.MaterialRequestEntity;
import com.example.demo.model.MaterialRequestEntity.Status;
import com.example.demo.model.MaterialRequestEntity.Urgency;
import com.example.demo.repository.MaterialRepository;
import com.example.demo.repository.MaterialRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class MaterialRequestService {

    @Autowired
    private MaterialRequestRepository requestRepository;

    @Autowired
    private MaterialRepository materialRepository;

    // ── Staff: submit a new request ────────────────────────────────────────

    @Transactional
    public MaterialRequestEntity submitRequest(MaterialRequestDto dto, String username, String staffName) {
        if (dto.getMaterialName() == null || dto.getMaterialName().isBlank())
            throw new RuntimeException("Material name is required");
        if (dto.getRequestedQuantity() == null || dto.getRequestedQuantity() <= 0)
            throw new RuntimeException("Quantity must be greater than zero");

        MaterialRequestEntity req = new MaterialRequestEntity();

        // Optionally link to inventory record
        if (dto.getMaterialId() != null) {
            Material mat = materialRepository.findById(dto.getMaterialId()).orElse(null);
            if (mat != null) {
                req.setMaterial(mat);
                // Auto-fill unit from inventory if not supplied
                if ((dto.getUnit() == null || dto.getUnit().isBlank()) && mat.getUnit() != null)
                    req.setUnit(mat.getUnit().name());
            }
        }

        req.setMaterialName(dto.getMaterialName().trim());
        req.setSkuCode(dto.getSkuCode() != null ? dto.getSkuCode().trim() : null);
        req.setRequestedQuantity(dto.getRequestedQuantity());
        req.setUnit(dto.getUnit());
        req.setUrgency(parseUrgency(dto.getUrgency()));
        req.setReason(dto.getReason() != null ? dto.getReason().trim() : null);
        req.setRequestedBy(username);
        req.setStaffName(staffName);
        req.setStatus(Status.PENDING);

        return requestRepository.save(req);
    }

    // ── Staff: get own requests ────────────────────────────────────────────

    public List<MaterialRequestEntity> getMyRequests(String username) {
        return requestRepository.findByStaffOrderByCreatedAtDesc(username);
    }

    // ── Admin: get all requests ────────────────────────────────────────────

    public List<MaterialRequestEntity> getAllRequests() {
        return requestRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<MaterialRequestEntity> getRequestsByStatus(String status) {
        return requestRepository.findByStatusOrderByCreatedAtDesc(Status.valueOf(status.toUpperCase()));
    }

    // ── Admin: update status ───────────────────────────────────────────────

    @Transactional
    public MaterialRequestEntity updateStatus(Long id, String status, String adminNotes) {
        MaterialRequestEntity req = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found: " + id));
        req.setStatus(Status.valueOf(status.toUpperCase()));
        if (adminNotes != null) req.setAdminNotes(adminNotes.trim());
        return requestRepository.save(req);
    }

    // ── Admin: delete ──────────────────────────────────────────────────────

    public void deleteRequest(Long id) {
        if (!requestRepository.existsById(id))
            throw new RuntimeException("Request not found: " + id);
        requestRepository.deleteById(id);
    }

    // ── Stats for badge counts ─────────────────────────────────────────────

    public Map<String, Long> getStatusCounts() {
        return Map.of(
                "PENDING",  requestRepository.countByStatus(Status.PENDING),
                "REVIEWED", requestRepository.countByStatus(Status.REVIEWED),
                "ORDERED",  requestRepository.countByStatus(Status.ORDERED),
                "REJECTED", requestRepository.countByStatus(Status.REJECTED)
        );
    }

    // ── Helper ────────────────────────────────────────────────────────────

    private Urgency parseUrgency(String val) {
        if (val == null || val.isBlank()) return Urgency.MEDIUM;
        try { return Urgency.valueOf(val.toUpperCase()); }
        catch (IllegalArgumentException e) { return Urgency.MEDIUM; }
    }
}