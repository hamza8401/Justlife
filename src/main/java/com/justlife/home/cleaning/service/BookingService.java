package com.justlife.home.cleaning.service;

import com.justlife.home.cleaning.response.AvailabilityResponse;
import com.justlife.home.cleaning.response.BookingResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    List<AvailabilityResponse> checkAvailability(LocalDate date, LocalDateTime startTime, int duration);
    BookingResponse createBooking(LocalDateTime startTime, int duration, List<Long> professionalIds);
    BookingResponse updateBooking(Long bookingId, LocalDateTime newStartTime, int newDuration);
}
