package com.justlife.home.cleaning.repository;

import com.justlife.home.cleaning.model.Availability;
import com.justlife.home.cleaning.model.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByCleaningProfessionalIdAndDate(Long cleaningProfessionalId, LocalDate date);

    List<Availability> findByDateAndStatus(LocalDate date, AvailabilityStatus status);
}
