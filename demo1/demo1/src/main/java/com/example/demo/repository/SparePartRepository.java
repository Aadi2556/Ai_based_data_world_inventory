package com.example.demo.repository;

import com.example.demo.model.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long> {

    // ── Basic finders ─────────────────────────────────────────────────
    List<SparePart> findAllByOrderByPartNameAsc();

    Optional<SparePart> findByPartNumber(String partNumber);

    boolean existsByPartNumber(String partNumber);

    boolean existsByPartNumberAndIdNot(String partNumber, Long id);

    // ── Search: by name OR part number OR brand OR category ───────────
    @Query("SELECT s FROM SparePart s WHERE " +
            "LOWER(s.partName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(s.partNumber) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(s.brand) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(s.category) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(s.compatibleMachines) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "ORDER BY s.partName ASC")
    List<SparePart> search(@Param("q") String query);

    // ── Filter by category ────────────────────────────────────────────
    List<SparePart> findByCategoryOrderByPartNameAsc(String category);

    // ── Filter by active status ───────────────────────────────────────
    List<SparePart> findByIsActiveOrderByPartNameAsc(Boolean isActive);

    // ── Low stock: quantity <= threshold ─────────────────────────────
    @Query("SELECT s FROM SparePart s WHERE s.quantityInStock <= s.lowStockThreshold ORDER BY s.quantityInStock ASC")
    List<SparePart> findLowStock();

    // ── Out of stock ──────────────────────────────────────────────────
    @Query("SELECT s FROM SparePart s WHERE s.quantityInStock = 0 ORDER BY s.partName ASC")
    List<SparePart> findOutOfStock();

    // ── Count helpers for dashboard stats ─────────────────────────────
    long countByIsActive(Boolean isActive);

    @Query("SELECT COUNT(s) FROM SparePart s WHERE s.quantityInStock <= s.lowStockThreshold")
    long countLowStock();

    @Query("SELECT COUNT(s) FROM SparePart s WHERE s.quantityInStock = 0")
    long countOutOfStock();

    // ── Total inventory value ─────────────────────────────────────────
    @Query("SELECT COALESCE(SUM(s.totalValue), 0) FROM SparePart s WHERE s.isActive = true")
    java.math.BigDecimal totalInventoryValue();

    // ── Distinct categories ───────────────────────────────────────────
    @Query("SELECT DISTINCT s.category FROM SparePart s WHERE s.category IS NOT NULL ORDER BY s.category ASC")
    List<String> findDistinctCategories();

    // ── Combined search + category filter ────────────────────────────
    @Query("SELECT s FROM SparePart s WHERE " +
            "s.category = :category AND (" +
            "LOWER(s.partName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(s.partNumber) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(s.brand) LIKE LOWER(CONCAT('%', :q, '%'))) " +
            "ORDER BY s.partName ASC")
    List<SparePart> searchByCategory(@Param("q") String query, @Param("category") String category);
}