package com.elderlycare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "elderly_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElderlyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;

    @Column(length = 2000)
    private String medicalHistory;

    @Column(length = 1000)
    private String healthConditions;

    @Column(length = 500)
    private String allergies;

    @Column(length = 1000)
    private String currentMedications;

    @Column(length = 1000)
    private String carePreferences;

    private String preferredLanguage;
}
