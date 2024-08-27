package com.justlife.home.cleaning.functional;

import com.justlife.home.cleaning.exception.AvailabilityException;
import com.justlife.home.cleaning.exception.BookingNotFoundException;
import com.justlife.home.cleaning.exception.DifferentVehicleException;
import com.justlife.home.cleaning.model.*;
import com.justlife.home.cleaning.model.enums.AvailabilityStatus;
import com.justlife.home.cleaning.repository.AvailabilityRepository;
import com.justlife.home.cleaning.repository.BookingRepository;
import com.justlife.home.cleaning.repository.CleaningProfessionalRepository;
import com.justlife.home.cleaning.repository.VehicleRepository;
import com.justlife.home.cleaning.response.AvailabilityResponse;
import com.justlife.home.cleaning.response.BookingResponse;
import com.justlife.home.cleaning.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingServiceImplFunctionalTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private CleaningProfessionalRepository cleaningProfessionalRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private CleaningProfessional professional1;
    private CleaningProfessional professional2;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {

        bookingRepository.deleteAll();
        availabilityRepository.deleteAll();
        cleaningProfessionalRepository.deleteAll();
        vehicleRepository.deleteAll();

        // Set up a vehicle and professionals
        vehicle = new Vehicle();
        vehicle.setVehicleNumber("Vehicle 1");
        vehicleRepository.save(vehicle);

        professional1 = new CleaningProfessional();
        professional1.setName("John Doe");
        professional1.setVehicle(vehicle);

        professional2 = new CleaningProfessional();
        professional2.setName("Jane Smith");
        professional2.setVehicle(vehicle);

        // Set up availabilities
        Availability availability1 = new Availability();
        availability1.setCleaningProfessional(professional1);
        availability1.setDate(LocalDate.now());
        availability1.setStartTime(LocalTime.of(8, 0));
        availability1.setEndTime(LocalTime.of(13, 0));
        availability1.setStatus(AvailabilityStatus.AVAILABLE);
        professional1.setAvailabilities(Collections.singletonList(availability1));
        professional1 = cleaningProfessionalRepository.save(professional1);

        Availability availability2 = new Availability();
        availability2.setCleaningProfessional(professional2);
        availability2.setDate(LocalDate.now());
        availability2.setStartTime(LocalTime.of(9, 0));
        availability2.setEndTime(LocalTime.of(14, 0));
        availability2.setStatus(AvailabilityStatus.AVAILABLE);
        professional2.setAvailabilities(Collections.singletonList(availability2));
        professional2 = cleaningProfessionalRepository.save(professional2);

    }

    @Test
    void testCheckAvailability() {
        List<AvailabilityResponse> availabilityResponses = bookingService.checkAvailability(LocalDate.now(), null, 0);
        assertNotNull(availabilityResponses);
        assertEquals(2, availabilityResponses.size());
    }

    @Test
    void testCreateBooking_Success() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));
        BookingResponse response = bookingService.createBooking(startTime, 2, List.of(professional1.getId(), professional2.getId()));

        assertNotNull(response);
        assertEquals(2, response.getProfessionalIds().size());
        assertEquals(vehicle.getId(), response.getVehicleId());

        Booking booking = bookingRepository.findById(response.getId()).orElse(null);
        assertNotNull(booking);
        assertEquals(2, booking.getCleaningProfessionals().size());
        assertEquals(startTime, booking.getStartDateTime());
    }

    @Test
    void testCreateBooking_DifferentVehicleException() {
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleNumber("Vehicle 2");
        vehicleRepository.save(vehicle2);

        CleaningProfessional professional3 = new CleaningProfessional();
        professional3.setName("Another Professional");
        professional3.setVehicle(vehicle2);
        cleaningProfessionalRepository.save(professional3);

        // Expect DifferentVehicleException
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));
        assertThrows(DifferentVehicleException.class, () -> {
            bookingService.createBooking(startTime, 2, List.of(professional1.getId(), professional3.getId()));
        });
    }

    @Test
    void testUpdateBooking_Success() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
        BookingResponse initialResponse = bookingService.createBooking(startTime, 2, List.of(professional1.getId(), professional2.getId()));

        // Update the booking with a new time slot
        LocalDateTime newStartTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));
        BookingResponse updatedResponse = bookingService.updateBooking(initialResponse.getId(), newStartTime, 2);

        assertNotNull(updatedResponse);
        assertEquals(2, updatedResponse.getProfessionalIds().size());
        assertEquals(newStartTime, updatedResponse.getStartTime());

        // Verify that the booking was updated
        Booking updatedBooking = bookingRepository.findById(updatedResponse.getId()).orElse(null);
        assertNotNull(updatedBooking);
        assertEquals(newStartTime, updatedBooking.getStartDateTime());
        assertEquals(newStartTime.plusHours(2), updatedBooking.getEndDateTime());
    }

    @Test
    void testUpdateBooking_AvailabilityException() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
        BookingResponse initialResponse = bookingService.createBooking(startTime, 2, List.of(professional1.getId(), professional2.getId()));

        // Set availability to unavailable for the new time slot
        Availability availability = availabilityRepository.findByCleaningProfessionalIdAndDate(professional1.getId(), LocalDate.now()).get(0);
        availability.setStatus(AvailabilityStatus.UNAVAILABLE);
        availabilityRepository.save(availability);

        // Expect AvailabilityException
        LocalDateTime newStartTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));
        assertThrows(AvailabilityException.class, () -> {
            bookingService.updateBooking(initialResponse.getId(), newStartTime, 2);
        });
    }

    @Test
    void testUpdateBooking_BookingNotFoundException() {
        // Expect BookingNotFoundException for non-existent booking ID
        LocalDateTime newStartTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0));
        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.updateBooking(999L, newStartTime, 2); // Assuming 999L doesn't exist
        });
    }
}
