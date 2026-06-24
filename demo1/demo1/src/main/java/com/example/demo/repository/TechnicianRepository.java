package com.example.demo.repository;

import com.example.demo.model.Technician;
import com.example.demo.model.Technician.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    Optional<Technician> findByUsername(String username);
    Optional<Technician> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmployeeId(String employeeId);
    List<Technician> findByIsActiveTrueOrderByFirstNameAsc();
    List<Technician> findByAvailabilityStatusAndIsActiveTrue(AvailabilityStatus status);
    List<Technician> findAllByOrderByCreatedAtDesc();
}