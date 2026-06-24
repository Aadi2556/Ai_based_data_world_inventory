package com.example.demo.service;


import com.example.demo.dto.SparePartRequest;
import com.example.demo.dto.SparePartResponse;
import com.example.demo.dto.SparePartStatsResponse;
import com.example.demo.model.SparePart;
import com.example.demo.repository.SparePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SparePartService {

    @Autowired
    private SparePartRepository repository;

    // ── CREATE ─────────────────────────────────────────────────────────
    public SparePartResponse create(SparePartRequest request, String createdBy) {
        // Validate unique part number
        if (request.getPartNumber() != null && !request.getPartNumber().isBlank()) {
            if (repository.existsByPartNumber(request.getPartNumber())) {
                throw new RuntimeException("Part number '" + request.getPartNumber() + "' already exists.");
            }
        }
        SparePart part = new SparePart();
        mapToEntity(request, part);
        part.setCreatedBy(createdBy);
        if (part.getIsActive() == null) part.setIsActive(true);
        return toResponse(repository.save(part));
    }

    // ── READ ALL ───────────────────────────────────────────────────────
    public List<SparePartResponse> getAll(String search, String category, String stockFilter) {
        List<SparePart> list;

        if (search != null && !search.isBlank() && category != null && !category.isBlank()) {
            list = repository.searchByCategory(search, category);
        } else if (search != null && !search.isBlank()) {
            list = repository.search(search);
        } else if (category != null && !category.isBlank()) {
            list = repository.findByCategoryOrderByPartNameAsc(category);
        } else if ("LOW".equalsIgnoreCase(stockFilter)) {
            list = repository.findLowStock();
        } else if ("OUT".equalsIgnoreCase(stockFilter)) {
            list = repository.findOutOfStock();
        } else {
            list = repository.findAllByOrderByPartNameAsc();
        }

        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── READ ONE ───────────────────────────────────────────────────────
    public SparePartResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ── UPDATE ─────────────────────────────────────────────────────────
    public SparePartResponse update(Long id, SparePartRequest request) {
        SparePart part = findOrThrow(id);
        // Validate unique part number (exclude self)
        if (request.getPartNumber() != null && !request.getPartNumber().isBlank()) {
            if (repository.existsByPartNumberAndIdNot(request.getPartNumber(), id)) {
                throw new RuntimeException("Part number '" + request.getPartNumber() + "' already belongs to another part.");
            }
        }
        mapToEntity(request, part);
        return toResponse(repository.save(part));
    }

    // ── DELETE ─────────────────────────────────────────────────────────
    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    // ── ADJUST STOCK (quick +/- from table) ───────────────────────────
    public SparePartResponse adjustStock(Long id, int delta) {
        SparePart part = findOrThrow(id);
        int newQty = part.getQuantityInStock() + delta;
        if (newQty < 0) throw new RuntimeException("Stock cannot go below 0. Current: " + part.getQuantityInStock());
        part.setQuantityInStock(newQty);
        return toResponse(repository.save(part));
    }

    // ── STATS / NOTIFICATIONS ──────────────────────────────────────────
    public SparePartStatsResponse getStats() {
        SparePartStatsResponse stats = new SparePartStatsResponse();
        stats.setTotalParts(repository.count());
        stats.setActiveParts(repository.countByIsActive(true));
        stats.setLowStockCount(repository.countLowStock());
        stats.setOutOfStockCount(repository.countOutOfStock());
        stats.setTotalInventoryValue(repository.totalInventoryValue());
        stats.setCategories(repository.findDistinctCategories());
        stats.setLowStockParts(repository.findLowStock().stream().map(this::toResponse).collect(Collectors.toList()));
        stats.setOutOfStockParts(repository.findOutOfStock().stream().map(this::toResponse).collect(Collectors.toList()));
        return stats;
    }

    // ── Helpers ────────────────────────────────────────────────────────
    private SparePart findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spare part not found with id: " + id));
    }

    private void mapToEntity(SparePartRequest req, SparePart e) {
        e.setPartName(req.getPartName());
        e.setPartNumber(req.getPartNumber());
        e.setBarcode(req.getBarcode());
        e.setCategory(req.getCategory());
        e.setCompatibleMachines(req.getCompatibleMachines());
        e.setDescription(req.getDescription());
        e.setBrand(req.getBrand());
        e.setUnit(req.getUnit());
        e.setQuantityInStock(req.getQuantityInStock());
        e.setLowStockThreshold(req.getLowStockThreshold());
        e.setReorderQuantity(req.getReorderQuantity());
        e.setUnitPrice(req.getUnitPrice());
        e.setSupplierName(req.getSupplierName());
        e.setSupplierContact(req.getSupplierContact());
        e.setStorageLocation(req.getStorageLocation());
        e.setLastRestockedDate(req.getLastRestockedDate());
        e.setExpiryDate(req.getExpiryDate());
        if (req.getIsActive() != null) e.setIsActive(req.getIsActive());
        e.recalculateTotalValue();
    }

    public SparePartResponse toResponse(SparePart e) {
        SparePartResponse r = new SparePartResponse();
        r.setId(e.getId());
        r.setPartName(e.getPartName());
        r.setPartNumber(e.getPartNumber());
        r.setBarcode(e.getBarcode());
        r.setCategory(e.getCategory());
        r.setCompatibleMachines(e.getCompatibleMachines());
        r.setDescription(e.getDescription());
        r.setBrand(e.getBrand());
        r.setUnit(e.getUnit());
        r.setQuantityInStock(e.getQuantityInStock());
        r.setLowStockThreshold(e.getLowStockThreshold());
        r.setReorderQuantity(e.getReorderQuantity());
        r.setUnitPrice(e.getUnitPrice());
        r.setTotalValue(e.getTotalValue());
        r.setSupplierName(e.getSupplierName());
        r.setSupplierContact(e.getSupplierContact());
        r.setStorageLocation(e.getStorageLocation());
        r.setLastRestockedDate(e.getLastRestockedDate());
        r.setExpiryDate(e.getExpiryDate());
        r.setIsActive(e.getIsActive());
        r.setCreatedBy(e.getCreatedBy());
        r.setCreatedAt(e.getCreatedAt());
        r.setUpdatedAt(e.getUpdatedAt());
        // Derived stock status
        boolean out = e.isOutOfStock();
        boolean low = e.isLowStock();
        r.setOutOfStock(out);
        r.setLowStock(low);
        r.setStockStatus(out ? "OUT" : low ? "LOW" : "OK");
        return r;
    }
}