package com.elderlycare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "caregiver_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaregiverProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String qualifications;
    private Integer yearsOfExperience;
    private String skills;
    private String serviceLocations;
    private String availability; // e.g. "Weekdays 9-5, Weekends available"
    private String certificateInfo;
    private Double hourlyRate;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    private String adminRemarks;
    private Double averageRating = 0.0;
    private Integer totalReviews = 0;
}
