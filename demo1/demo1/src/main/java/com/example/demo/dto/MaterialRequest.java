package com.example.demo.dto;

// ===================== MATERIAL REQUEST =====================
public class MaterialRequest {
    private String name;
    private String description;
    private String category;
    private String unit;
    private Double currentStock;
    private Double minimumStockLevel;
    private Double criticalStockLevel;
    private Double maximumStockLevel;
    private Double unitCost;
    private String supplierName;
    private String skuCode;
    private String brand;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
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
}