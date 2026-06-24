// com/example/demo/dto/SparePartRequest.java
package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SparePartRequest {

    private String partName;
    private String partNumber;
    private String barcode;
    private String category;
    private String compatibleMachines;
    private String description;
    private String brand;
    private String unit;
    private Integer quantityInStock;
    private Integer lowStockThreshold;
    private Integer reorderQuantity;
    private BigDecimal unitPrice;
    private String supplierName;
    private String supplierContact;
    private String storageLocation;
    private LocalDate lastRestockedDate;
    private LocalDate expiryDate;
    private Boolean isActive;

    // ── Getters & Setters ──────────────────────────────────────────────
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
}