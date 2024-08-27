package com.justlife.home.cleaning.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.justlife.home.cleaning.model.enums.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cleaning_professional_id")
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CleaningProfessional cleaningProfessional;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus status;

    private LocalTime startTime;
    private LocalTime endTime;
}
