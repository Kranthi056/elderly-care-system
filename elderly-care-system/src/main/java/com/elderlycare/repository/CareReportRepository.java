package com.elderlycare.repository;

import com.elderlycare.entity.CareReport;
import com.elderlycare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CareReportRepository extends JpaRepository<CareReport, Long> {
    List<CareReport> findByElderlyUser(User elderlyUser);
    List<CareReport> findByCaregiver(User caregiver);
}
