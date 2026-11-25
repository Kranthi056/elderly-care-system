package com.elderlycare.repository;

import com.elderlycare.entity.ElderlyProfile;
import com.elderlycare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ElderlyProfileRepository extends JpaRepository<ElderlyProfile, Long> {
    Optional<ElderlyProfile> findByUser(User user);
    Optional<ElderlyProfile> findByUserId(Long userId);
}
