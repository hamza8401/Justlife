package com.justlife.home.cleaning.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @ManyToMany
    @JoinTable(
            name = "booking_professional",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "professional_id")
    )
    private List<CleaningProfessional> cleaningProfessionals;
}
