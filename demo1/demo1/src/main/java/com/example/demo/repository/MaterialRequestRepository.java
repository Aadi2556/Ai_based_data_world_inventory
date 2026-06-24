package com.example.demo.repository;

import com.example.demo.model.MaterialRequestEntity;
import com.example.demo.model.MaterialRequestEntity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRequestRepository extends JpaRepository<MaterialRequestEntity, Long> {

    List<MaterialRequestEntity> findAllByOrderByCreatedAtDesc();

    List<MaterialRequestEntity> findByStatusOrderByCreatedAtDesc(Status status);

    List<MaterialRequestEntity> findByRequestedByOrderByCreatedAtDesc(String requestedBy);

    List<MaterialRequestEntity> findByStaffNameOrderByCreatedAtDesc(String staffName);

    @Query("SELECT r FROM MaterialRequestEntity r WHERE r.requestedBy = :username OR r.staffName = :username ORDER BY r.createdAt DESC")
    List<MaterialRequestEntity> findByStaffOrderByCreatedAtDesc(@Param("username") String username);

    long countByStatus(Status status);

    @Query("SELECT r FROM MaterialRequestEntity r WHERE r.urgency = 'CRITICAL' AND r.status = 'PENDING' ORDER BY r.createdAt DESC")
    List<MaterialRequestEntity> findCriticalPendingRequests();
}