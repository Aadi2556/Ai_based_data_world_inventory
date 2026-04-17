package com.example.demo.repository;

import com.example.demo.model.MaintenanceSchedule;
import com.example.demo.model.MaintenanceSchedule.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Long> {

    List<MaintenanceSchedule> findAllByOrderByScheduledDateDesc();

    List<MaintenanceSchedule> findByStatus(Status status);

    List<MaintenanceSchedule> findByMachineId(Long machineId);

    // ── NEW: fetch only tasks assigned to a specific technician (case-insensitive) ──
    List<MaintenanceSchedule> findByAssignedTechnicianIgnoreCaseOrderByScheduledDateDesc(
            String assignedTechnician);
}