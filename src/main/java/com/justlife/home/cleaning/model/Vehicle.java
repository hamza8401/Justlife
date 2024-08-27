package com.justlife.home.cleaning.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "vehicle")
    private List<CleaningProfessional> professionals;

    private String vehicleNumber;
}
