package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public class SparePartStatsResponse {

    private long totalParts;
    private long activeParts;
    private long lowStockCount;
    private long outOfStockCount;
    private BigDecimal totalInventoryValue;
    private List<String> categories;

    // Low-stock parts for notification panel
    private List<SparePartResponse> lowStockParts;
    private List<SparePartResponse> outOfStockParts;

    // Getters & Setters
    public long getTotalParts() { return totalParts; }
    public void setTotalParts(long totalParts) { this.totalParts = totalParts; }

    public long getActiveParts() { return activeParts; }
    public void setActiveParts(long activeParts) { this.activeParts = activeParts; }

    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }

    public long getOutOfStockCount() { return outOfStockCount; }
    public void setOutOfStockCount(long outOfStockCount) { this.outOfStockCount = outOfStockCount; }

    public BigDecimal getTotalInventoryValue() { return totalInventoryValue; }
    public void setTotalInventoryValue(BigDecimal totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public List<SparePartResponse> getLowStockParts() { return lowStockParts; }
    public void setLowStockParts(List<SparePartResponse> lowStockParts) { this.lowStockParts = lowStockParts; }

    public List<SparePartResponse> getOutOfStockParts() { return outOfStockParts; }
    public void setOutOfStockParts(List<SparePartResponse> outOfStockParts) { this.outOfStockParts = outOfStockParts; }
}