package com.elderlycare.repository;

import com.elderlycare.entity.Assignment;
import com.elderlycare.entity.AssignmentStatus;
import com.elderlycare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCaregiver(User caregiver);
    List<Assignment> findByElderlyUser(User elderlyUser);
    List<Assignment> findByStatus(AssignmentStatus status);
    long countByStatus(AssignmentStatus status);
    long countByCaregiver(User caregiver);
}
