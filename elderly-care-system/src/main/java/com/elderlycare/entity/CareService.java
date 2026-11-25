package com.elderlycare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "care_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private String iconClass; // FontAwesome or similar
    private boolean active = true;
    private Double basePrice;
}
