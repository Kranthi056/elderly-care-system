package com.elderlycare.repository;

import com.elderlycare.entity.CaregiverProfile;
import com.elderlycare.entity.User;
import com.elderlycare.entity.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CaregiverProfileRepository extends JpaRepository<CaregiverProfile, Long> {
    Optional<CaregiverProfile> findByUser(User user);
    Optional<CaregiverProfile> findByUserId(Long userId);
    List<CaregiverProfile> findByVerificationStatus(VerificationStatus status);
    long countByVerificationStatus(VerificationStatus status);
}
