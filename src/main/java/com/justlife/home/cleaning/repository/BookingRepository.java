package com.justlife.home.cleaning.repository;

import com.justlife.home.cleaning.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
