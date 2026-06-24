package com.example.demo.repository;

import com.example.demo.model.Manager;
import com.example.demo.model.Manager.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByUsername(String username);
    Optional<Manager> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmployeeId(String employeeId);
    List<Manager> findByIsActiveTrueOrderByFirstNameAsc();
    List<Manager> findByManagedDepartmentAndIsActiveTrue(Department department);
    List<Manager> findAllByOrderByCreatedAtDesc();
}