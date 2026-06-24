package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "materials")
public class Material {

    public enum Category {
        PRINTING_PAPER, INK_TONER, VINYL, CARDSTOCK, LAMINATE, BINDING_MATERIAL, CLEANING_SUPPLY, OTHER
    }

    public enum Unit {
        REAMS, SHEETS, LITERS, MILLILITERS, KILOGRAMS, GRAMS, ROLLS, UNITS, BOXES
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Unit unit;

    @Column(name = "current_stock")
    private Double currentStock = 0.0;

    @Column(name = "minimum_stock_level")
    private Double minimumStockLevel = 10.0;

    @Column(name = "critical_stock_level")
    private Double criticalStockLevel = 5.0;

    @Column(name = "maximum_stock_level")
    private Double maximumStockLevel = 100.0;

    @Column(name = "unit_cost")
    private Double unitCost = 0.0;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "sku_code")
    private String skuCode;

    private String brand;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Derived alert status
    @Transient
    public String getAlertStatus() {
        if (currentStock <= criticalStockLevel) return "CRITICAL";
        if (currentStock <= minimumStockLevel) return "LOW";
        if (currentStock >= maximumStockLevel * 0.9) return "OVERSTOCKED";
        return "NORMAL";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Unit getUnit() { return unit; }
    public void setUnit(Unit unit) { this.unit = unit; }
    public Double getCurrentStock() { return currentStock; }
    public void setCurrentStock(Double currentStock) { this.currentStock = currentStock; }
    public Double getMinimumStockLevel() { return minimumStockLevel; }
    public void setMinimumStockLevel(Double minimumStockLevel) { this.minimumStockLevel = minimumStockLevel; }
    public Double getCriticalStockLevel() { return criticalStockLevel; }
    public void setCriticalStockLevel(Double criticalStockLevel) { this.criticalStockLevel = criticalStockLevel; }
    public Double getMaximumStockLevel() { return maximumStockLevel; }
    public void setMaximumStockLevel(Double maximumStockLevel) { this.maximumStockLevel = maximumStockLevel; }
    public Double getUnitCost() { return unitCost; }
    public void setUnitCost(Double unitCost) { this.unitCost = unitCost; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}