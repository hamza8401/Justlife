package com.justlife.home.cleaning.repository;

import com.justlife.home.cleaning.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
