package com.justlife.home.cleaning.repository;

import com.justlife.home.cleaning.model.CleaningProfessional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface CleaningProfessionalRepository extends JpaRepository<CleaningProfessional, Long> {

    @Query("SELECT cp FROM CleaningProfessional cp " +
            "JOIN cp.availabilities a " +
            "WHERE a.date = :date " +
            "AND a.status = 'AVAILABLE'")
    List<CleaningProfessional> findAvailableProfessionalsOnDate(@Param("date") LocalDate date);
    @Query("SELECT cp FROM CleaningProfessional cp JOIN cp.availabilities a WHERE a.date = :date" +
            " AND a.status = 'AVAILABLE'" +
            " AND a.startTime <= :startTime" +
            " AND a.endTime >= :endTime" )
    List<CleaningProfessional> findAvailableProfessionals(LocalDate date, LocalTime startTime, LocalTime endTime);

}
