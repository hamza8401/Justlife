package com.justlife.home.cleaning.service.impl;

import com.justlife.home.cleaning.exception.AvailabilityException;
import com.justlife.home.cleaning.exception.BookingNotFoundException;
import com.justlife.home.cleaning.exception.DifferentVehicleException;
import com.justlife.home.cleaning.model.Availability;
import com.justlife.home.cleaning.model.Booking;
import com.justlife.home.cleaning.model.CleaningProfessional;
import com.justlife.home.cleaning.model.enums.AvailabilityStatus;
import com.justlife.home.cleaning.repository.AvailabilityRepository;
import com.justlife.home.cleaning.repository.BookingRepository;
import com.justlife.home.cleaning.repository.CleaningProfessionalRepository;
import com.justlife.home.cleaning.response.AvailabilityResponse;
import com.justlife.home.cleaning.response.BookingResponse;
import com.justlife.home.cleaning.response.TimeSlots;
import com.justlife.home.cleaning.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final CleaningProfessionalRepository cleaningProfessionalRepository;
    private final AvailabilityRepository availabilityRepository;
    private final BookingRepository bookingRepository;
    @Override
    public List<AvailabilityResponse> checkAvailability(LocalDate date, LocalDateTime startTime, int duration) {
        log.info("fetching cleaning professionals availabilities...");
        List<CleaningProfessional> availableProfessionals;
        if(startTime == null){
            availableProfessionals = cleaningProfessionalRepository.findAvailableProfessionalsOnDate(date);
        }else {
            LocalTime endTime = startTime.toLocalTime().plusHours(duration);
            availableProfessionals = cleaningProfessionalRepository.findAvailableProfessionals(date, startTime.toLocalTime(), endTime);
        }
        return availableProfessionals.stream().map(this::mapProfessionalToAvailabilityResponse).toList();
    }

    @Override
    public BookingResponse createBooking(LocalDateTime startTime, int duration, List<Long> professionalIds) {
        log.info("Going to create new booking...");
        // Validate the professionals belong to the same vehicle
        Long vehicleId = cleaningProfessionalRepository.findById(professionalIds.get(0)).get().getVehicle().getId();
        validateProfessionalsInSameVehicle(professionalIds, vehicleId);

        // Check if all professionals are available
        List<CleaningProfessional> professionals = cleaningProfessionalRepository.findAllById(professionalIds);
        if (!areProfessionalsAvailable(professionals, startTime.toLocalDate(), startTime.toLocalTime(), startTime.plusHours(duration).toLocalTime())) {
            log.error("One or more professionals are not available during the requested time.");
            throw new AvailabilityException("One or more professionals are not available during the requested time.");
        }

        // Create a new booking
        Booking booking = new Booking();
        booking.setStartDateTime(startTime);
        booking.setEndDateTime(startTime.plusHours(duration));
        booking.setCleaningProfessionals(professionals);

        // Save the booking
        booking = bookingRepository.save(booking);

        // Update availability for each professional
        updateAvailabilityAfterBooking(professionals, startTime.toLocalDate(), startTime.toLocalTime(), duration);

        return mapToBookingResponse(booking, vehicleId);
    }

    @Override
    public BookingResponse updateBooking(Long bookingId, LocalDateTime newStartTime, int newDuration) {
        log.info("Going to update booking...");
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isEmpty()) {
            log.error("No Booking Found for id: {}", bookingId);
            throw new BookingNotFoundException(bookingId);
        }


        Booking booking = optionalBooking.get();
        // Check if all professionals are available
        if (!areProfessionalsAvailable(booking.getCleaningProfessionals(), newStartTime.toLocalDate(), newStartTime.toLocalTime(), newStartTime.plusHours(newDuration).toLocalTime())) {
            log.error("One or more professionals are not available during the requested time.");
            throw new AvailabilityException("One or more professionals are not available during the requested time.");
        }
        Long vehicleId = cleaningProfessionalRepository.findById(booking.getCleaningProfessionals().get(0).getId()).get().getVehicle().getId();

        // Update availability before making changes
        revertAvailability(booking);

        // Update booking details
        booking.setStartDateTime(newStartTime);
        booking.setEndDateTime(newStartTime.plusHours(newDuration));

        // Update availability for the new time slot
        updateAvailabilityAfterBooking(booking.getCleaningProfessionals(), newStartTime.toLocalDate(), newStartTime.toLocalTime(), newDuration);

        bookingRepository.save(booking);
        return mapToBookingResponse(booking, vehicleId);
    }

    private void validateProfessionalsInSameVehicle(List<Long> professionalIds, Long vehicleId) {
        for (Long professionalId : professionalIds) {
            if (!cleaningProfessionalRepository.findById(professionalId).get().getVehicle().getId().equals(vehicleId)) {
                log.error("Professionals belong to different vehicles.");
                throw new DifferentVehicleException("Professionals must belong to the same vehicle");
            }
        }
    }

    private void updateAvailabilityAfterBooking(List<CleaningProfessional> professionals, LocalDate date, LocalTime startTime, int duration) {
        log.info("Updating availabilities after booking...");
        LocalTime endTime = startTime.plusHours(duration);
        for (CleaningProfessional professional : professionals) {
            List<Availability> availabilities = availabilityRepository.findByCleaningProfessionalIdAndDate(professional.getId(), date);

            for (Availability availability : availabilities) {
                if (availability.getStartTime().equals(startTime) && availability.getEndTime().equals(endTime)) {
                    availability.setStatus(AvailabilityStatus.BOOKED);
                    availabilityRepository.save(availability);
                    break;
                }
            }
        }
    }
    private void revertAvailability(Booking booking) {
        log.info("Reverting availabilities...");
        LocalDateTime startDateTime = booking.getStartDateTime();
        LocalTime endTime = booking.getEndDateTime().toLocalTime();

        for (CleaningProfessional professional : booking.getCleaningProfessionals()) {
            List<Availability> availabilities = availabilityRepository.findByCleaningProfessionalIdAndDate(professional.getId(), startDateTime.toLocalDate());

            for (Availability availability : availabilities) {
                if (availability.getStartTime().equals(startDateTime.toLocalTime()) && availability.getEndTime().equals(endTime)) {
                    availability.setStatus(AvailabilityStatus.AVAILABLE);
                    availabilityRepository.save(availability);
                    break;
                }
            }
        }
    }

    private AvailabilityResponse mapProfessionalToAvailabilityResponse(CleaningProfessional professional) {
        List<TimeSlots> timeSlots = professional.getAvailabilities().stream()
                .filter(availability -> availability.getStatus() == AvailabilityStatus.AVAILABLE)
                .map(this::mapAvailabilityToTimeSlots)
                .collect(Collectors.toList());

        return AvailabilityResponse.builder()
                .professionalId(professional.getId())
                .name(professional.getName())
                .availabilities(timeSlots)
                .build();
    }
    private TimeSlots mapAvailabilityToTimeSlots(Availability availability) {
        return TimeSlots.builder()
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .build();
    }
    public BookingResponse mapToBookingResponse(Booking booking, Long vehicleId) {
        return BookingResponse.builder()
                .id(booking.getId())
                .startTime(booking.getStartDateTime())
                .endTime(booking.getEndDateTime())
                .professionalIds(booking.getCleaningProfessionals().stream()
                        .map(CleaningProfessional::getId)
                        .collect(Collectors.toList()))
                .vehicleId(vehicleId)
                .build();
    }
    private boolean areProfessionalsAvailable(List<CleaningProfessional> professionals, LocalDate date, LocalTime startTime, LocalTime endTime) {
        for (CleaningProfessional professional : professionals) {
            List<Availability> availabilities = availabilityRepository.findByCleaningProfessionalIdAndDate(professional.getId(), date);
            boolean available = availabilities.stream()
                    .anyMatch(a -> a.getStatus() == AvailabilityStatus.AVAILABLE &&
                            (a.getStartTime().isBefore(startTime) || a.getStartTime().equals(startTime)) &&
                            (a.getEndTime().isAfter(endTime) || a.getStartTime().equals(startTime)));
            if (!available) {
                return false;
            }
        }
        return true;
    }
}
