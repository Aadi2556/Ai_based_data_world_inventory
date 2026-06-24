// com/example/demo/repository/SparePartRequestRepository.java
package com.example.demo.repository;

import com.example.demo.model.SparePartRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SparePartRequestRepository extends JpaRepository<SparePartRequestEntity, Long> {

    List<SparePartRequestEntity> findByRequestedByOrderByCreatedAtDesc(String requestedBy);

    List<SparePartRequestEntity> findAllByOrderByCreatedAtDesc();

    List<SparePartRequestEntity> findByStatusOrderByCreatedAtDesc(SparePartRequestEntity.Status status);

    long countByStatus(SparePartRequestEntity.Status status);
}