package com.elderlycare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "elderly_user_id")
    private User elderlyUser;

    @ManyToOne
    @JoinColumn(name = "caregiver_id")
    private User caregiver;

    private Integer rating; // 1-5
    @Column(length = 1000)
    private String comments;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
