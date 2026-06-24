package com.example.demo.repository;

import com.example.demo.model.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {

    List<StockAlert> findByIsResolvedFalseOrderByCreatedAtDesc();

    List<StockAlert> findByIsAcknowledgedFalseAndIsResolvedFalseOrderByCreatedAtDesc();

    List<StockAlert> findByMaterialIdOrderByCreatedAtDesc(Long materialId);

    List<StockAlert> findBySeverityAndIsResolvedFalse(StockAlert.AlertSeverity severity);

    long countByIsResolvedFalseAndIsAcknowledgedFalse();
}