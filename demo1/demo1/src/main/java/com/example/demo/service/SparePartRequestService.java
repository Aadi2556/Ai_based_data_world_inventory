// com/example/demo/service/SparePartRequestService.java
package com.example.demo.service;

import com.example.demo.dto.SparePartRequestDto;
import com.example.demo.model.SparePartRequestEntity;
import com.example.demo.repository.SparePartRepository;
import com.example.demo.repository.SparePartRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SparePartRequestService {

    @Autowired
    private SparePartRequestRepository requestRepository;

    @Autowired
    private SparePartRepository sparePartRepository;

    /** Technician submits a request */
    public SparePartRequestEntity submitRequest(SparePartRequestDto dto,
                                                String username,
                                                String technicianName) {
        SparePartRequestEntity req = new SparePartRequestEntity();
        req.setRequestedQuantity(dto.getRequestedQuantity());
        req.setReason(dto.getReason());
        req.setUrgency(dto.getUrgency() != null ? dto.getUrgency() : "MEDIUM");
        req.setRequestedBy(username);
        req.setTechnicianName(technicianName);
        req.setStatus(SparePartRequestEntity.Status.PENDING);

        // Link to real part if ID provided
        if (dto.getSparePartId() != null) {
            sparePartRepository.findById(dto.getSparePartId()).ifPresent(part -> {
                req.setSparePart(part);
                req.setSparePartName(part.getPartName());
                req.setPartNumber(part.getPartNumber());
            });
        }
        // Fall back to manually entered name
        if (req.getSparePartName() == null) {
            req.setSparePartName(dto.getSparePartName());
            req.setPartNumber(dto.getPartNumber());
        }

        return requestRepository.save(req);
    }

    /** Technician views their own requests */
    public List<SparePartRequestEntity> getMyRequests(String username) {
        return requestRepository.findByRequestedByOrderByCreatedAtDesc(username);
    }

    /** Admin views all requests */
    public List<SparePartRequestEntity> getAllRequests() {
        return requestRepository.findAllByOrderByCreatedAtDesc();
    }

    /** Admin views only PENDING requests */
    public List<SparePartRequestEntity> getPendingRequests() {
        return requestRepository.findByStatusOrderByCreatedAtDesc(
                SparePartRequestEntity.Status.PENDING);
    }

    /** Count of pending requests — for notification badge */
    public long countPending() {
        return requestRepository.countByStatus(SparePartRequestEntity.Status.PENDING);
    }

    /** Admin updates status of a request */
    public SparePartRequestEntity updateStatus(Long id, String status, String adminNotes) {
        SparePartRequestEntity req = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found: " + id));
        req.setStatus(SparePartRequestEntity.Status.valueOf(status.toUpperCase()));
        if (adminNotes != null) req.setAdminNotes(adminNotes);
        return requestRepository.save(req);
    }

    public void deleteRequest(Long id) {
        requestRepository.deleteById(id);
    }
}