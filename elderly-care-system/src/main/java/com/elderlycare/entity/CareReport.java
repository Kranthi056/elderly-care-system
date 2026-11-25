package com.elderlycare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "care_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "caregiver_id", nullable = false)
    private User caregiver;

    @ManyToOne
    @JoinColumn(name = "elderly_user_id", nullable = false)
    private User elderlyUser;

    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(length = 2000)
    private String completedActivities;

    @Column(length = 1000)
    private String pendingActivities;

    @Column(length = 1000)
    private String healthObservations;

    @Column(length = 1000)
    private String remarks;

    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}
