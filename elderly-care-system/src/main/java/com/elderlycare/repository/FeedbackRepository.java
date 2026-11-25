package com.elderlycare.repository;

import com.elderlycare.entity.Feedback;
import com.elderlycare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByCaregiver(User caregiver);
}
