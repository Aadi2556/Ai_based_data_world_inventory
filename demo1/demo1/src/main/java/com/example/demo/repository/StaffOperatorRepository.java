package com.example.demo.repository;

import com.example.demo.model.StaffOperator;
import com.example.demo.model.StaffOperator.AvailabilityStatus;
import com.example.demo.model.StaffOperator.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffOperatorRepository extends JpaRepository<StaffOperator, Long> {
    Optional<StaffOperator> findByUsername(String username);
    Optional<StaffOperator> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmployeeId(String employeeId);
    List<StaffOperator> findByIsActiveTrueOrderByFirstNameAsc();
    List<StaffOperator> findByAvailabilityStatusAndIsActiveTrue(AvailabilityStatus status);
    List<StaffOperator> findByDepartmentAndIsActiveTrue(Department department);
    List<StaffOperator> findAllByOrderByCreatedAtDesc();
}