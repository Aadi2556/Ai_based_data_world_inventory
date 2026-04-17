package com.example.demo.repository;

import com.example.demo.model.RepairLog;
import com.example.demo.model.RepairLog.RepairStatus;
import com.example.demo.model.RepairLog.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairLogRepository extends JpaRepository<RepairLog, Long> {
    List<RepairLog> findAllByOrderByReportedDateDesc();
    List<RepairLog> findByMachineIdOrderByReportedDateDesc(Long machineId);
    List<RepairLog> findByStatusOrderByReportedDateDesc(RepairStatus status);
    List<RepairLog> findBySeverityOrderByReportedDateDesc(Severity severity);
    List<RepairLog> findByScheduleId(Long scheduleId);

    @Query("SELECT r FROM RepairLog r WHERE r.machine.id = :machineId ORDER BY r.reportedDate DESC")
    List<RepairLog> findHistoryByMachine(@Param("machineId") Long machineId);

    long countByStatus(RepairStatus status);
    long countBySeverity(Severity severity);
}