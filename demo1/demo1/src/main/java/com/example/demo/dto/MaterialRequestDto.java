package com.example.demo.dto;

/**
 * DTO used for both submitting a material request (staff)
 * and updating its status (admin).
 */
public class MaterialRequestDto {

    // ── Submit fields (staff) ──────────────────────────────────
    private Long materialId;        // optional — link to inventory
    private String materialName;    // required
    private String skuCode;
    private Double requestedQuantity;
    private String unit;
    private String urgency;         // LOW | MEDIUM | HIGH | CRITICAL
    private String reason;

    // ── Status-update fields (admin) ──────────────────────────
    private String status;          // PENDING | REVIEWED | ORDERED | REJECTED
    private String adminNotes;

    // ── Getters & Setters ──────────────────────────────────────

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }

    public Double getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(Double requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}