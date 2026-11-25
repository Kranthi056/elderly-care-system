package com.elderlycare.repository;

import com.elderlycare.entity.CareService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CareServiceRepository extends JpaRepository<CareService, Long> {
    List<CareService> findByActiveTrue();
}
