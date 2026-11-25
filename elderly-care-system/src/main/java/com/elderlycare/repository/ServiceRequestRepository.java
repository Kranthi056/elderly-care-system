package com.elderlycare.repository;

import com.elderlycare.entity.RequestStatus;
import com.elderlycare.entity.ServiceRequest;
import com.elderlycare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByElderlyUser(User user);
    List<ServiceRequest> findByStatus(RequestStatus status);
    long countByStatus(RequestStatus status);
}
