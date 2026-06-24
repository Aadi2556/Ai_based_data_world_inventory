package com.example.demo.repository;

import com.example.demo.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByIsActiveTrue();

    List<Material> findByCategory(Material.Category category);

    List<Material> findByNameContainingIgnoreCase(String name);

    // Materials at or below minimum stock level
    @Query("SELECT m FROM Material m WHERE m.currentStock <= m.minimumStockLevel AND m.isActive = true")
    List<Material> findLowStockMaterials();

    // Materials at or below critical stock level
    @Query("SELECT m FROM Material m WHERE m.currentStock <= m.criticalStockLevel AND m.isActive = true")
    List<Material> findCriticalStockMaterials();

    // Materials over 90% of max stock
    @Query("SELECT m FROM Material m WHERE m.currentStock >= (m.maximumStockLevel * 0.9) AND m.isActive = true")
    List<Material> findOverstockedMaterials();

    @Query("SELECT SUM(m.currentStock * m.unitCost) FROM Material m WHERE m.isActive = true")
    Double getTotalInventoryValue();
}