package com.example.demo.repository;

import com.example.demo.model.MaterialUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaterialUsageLogRepository extends JpaRepository<MaterialUsageLog, Long> {

    List<MaterialUsageLog> findByMaterialIdOrderByCreatedAtDesc(Long materialId);

    List<MaterialUsageLog> findByLogTypeOrderByCreatedAtDesc(MaterialUsageLog.LogType logType);

    List<MaterialUsageLog> findAllByOrderByCreatedAtDesc();

    @Query("SELECT SUM(l.quantity) FROM MaterialUsageLog l WHERE l.material.id = :materialId AND l.logType = 'WASTAGE'")
    Double getTotalWastageByMaterial(@Param("materialId") Long materialId);

    @Query("SELECT SUM(l.quantity) FROM MaterialUsageLog l WHERE l.material.id = :materialId AND l.logType = 'USAGE'")
    Double getTotalUsageByMaterial(@Param("materialId") Long materialId);

    @Query("SELECT SUM(l.quantity) FROM MaterialUsageLog l WHERE l.logType = 'WASTAGE' AND l.logDate BETWEEN :from AND :to")
    Double getTotalWastageInPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT SUM(l.quantity) FROM MaterialUsageLog l WHERE l.logType = 'USAGE' AND l.logDate BETWEEN :from AND :to")
    Double getTotalUsageInPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<MaterialUsageLog> findByLogDateBetweenOrderByCreatedAtDesc(LocalDate from, LocalDate to);
}