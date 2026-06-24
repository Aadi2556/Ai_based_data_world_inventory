package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "spare_parts")
public class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Core identity ──────────────────────────────────────────────────
    @NotBlank(message = "Part name is required")
    @Column(name = "part_name", nullable = false)
    private String partName;

    @Column(name = "part_number", unique = true)
    private String partNumber;      // e.g. SP-00042

    @Column(name = "barcode")
    private String barcode;

    // ── Classification ─────────────────────────────────────────────────
    @NotBlank(message = "Category is required")
    @Column(name = "category", nullable = false)
    private String category;        // e.g. MECHANICAL, ELECTRICAL, CONSUMABLE, HYDRAULIC

    @Column(name = "compatible_machines", columnDefinition = "TEXT")
    private String compatibleMachines;  // comma-separated machine names

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "brand")
    private String brand;

    @Column(name = "unit")
    private String unit;            // PIECE, BOX, LITRE, METRE, KG

    // ── Stock ─────────────────────────────────────────────────────────
    @NotNull(message = "Quantity in stock is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(name = "quantity_in_stock", nullable = false)
    private Integer quantityInStock;

    @NotNull(message = "Low stock threshold is required")
    @Min(value = 1, message = "Threshold must be at least 1")
    @Column(name = "low_stock_threshold", nullable = false)
    private Integer lowStockThreshold;   // alert fires when quantityInStock <= this

    @Column(name = "reorder_quantity")
    private Integer reorderQuantity;     // suggested order quantity

    // ── Pricing ───────────────────────────────────────────────────────
    @Column(name = "unit_price", precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_value", precision = 14, scale = 2)
    private BigDecimal totalValue;   // auto-calculated = unitPrice * quantityInStock

    // ── Supplier / location ────────────────────────────────────────────
    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "supplier_contact")
    private String supplierContact;

    @Column(name = "storage_location")
    private String storageLocation;   // e.g. "Shelf B3", "Warehouse Room 2"

    // ── Dates ─────────────────────────────────────────────────────────
    @Column(name = "last_restocked_date")
    private LocalDate lastRestockedDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    // ── Status ────────────────────────────────────────────────────────
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // ── Audit ─────────────────────────────────────────────────────────
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        recalculateTotalValue();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        recalculateTotalValue();
    }

    // Derived: auto-compute total value
    public void recalculateTotalValue() {
        if (unitPrice != null && quantityInStock != null) {
            totalValue = unitPrice.multiply(BigDecimal.valueOf(quantityInStock));
        }
    }

    // Derived: is this part low on stock?
    @Transient
    public boolean isLowStock() {
        return quantityInStock != null
                && lowStockThreshold != null
                && quantityInStock <= lowStockThreshold;
    }

    // Derived: is this part out of stock?
    @Transient
    public boolean isOutOfStock() {
        return quantityInStock != null && quantityInStock == 0;
    }

    // ── Getters & Setters ──────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCompatibleMachines() { return compatibleMachines; }
    public void setCompatibleMachines(String compatibleMachines) { this.compatibleMachines = compatibleMachines; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Integer getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(Integer quantityInStock) { this.quantityInStock = quantityInStock; }

    public Integer getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(Integer lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }

    public Integer getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(Integer reorderQuantity) { this.reorderQuantity = reorderQuantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getSupplierContact() { return supplierContact; }
    public void setSupplierContact(String supplierContact) { this.supplierContact = supplierContact; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public LocalDate getLastRestockedDate() { return lastRestockedDate; }
    public void setLastRestockedDate(LocalDate lastRestockedDate) { this.lastRestockedDate = lastRestockedDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}