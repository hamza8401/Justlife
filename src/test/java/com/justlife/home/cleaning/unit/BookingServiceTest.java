package com.justlife.home.cleaning.unit;

import com.justlife.home.cleaning.exception.AvailabilityException;
import com.justlife.home.cleaning.exception.BookingNotFoundException;
import com.justlife.home.cleaning.exception.DifferentVehicleException;
import com.justlife.home.cleaning.model.Availability;
import com.justlife.home.cleaning.model.Booking;
import com.justlife.home.cleaning.model.CleaningProfessional;
import com.justlife.home.cleaning.model.Vehicle;
import com.justlife.home.cleaning.model.enums.AvailabilityStatus;
import com.justlife.home.cleaning.repository.AvailabilityRepository;
import com.justlife.home.cleaning.repository.BookingRepository;
import com.justlife.home.cleaning.repository.CleaningProfessionalRepository;
import com.justlife.home.cleaning.response.BookingResponse;
import com.justlife.home.cleaning.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private CleaningProfessionalRepository cleaningProfessionalRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private LocalDateTime startTime;
    private CleaningProfessional professional1;
    private CleaningProfessional professional2;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.of(2024, 8, 27, 10, 0);

        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setVehicleNumber("Van");

        professional1 = new CleaningProfessional();
        professional1.setId(1L);
        professional1.setName("John");
        professional1.setVehicle(vehicle);

        professional2 = new CleaningProfessional();
        professional2.setId(2L);
        professional2.setName("Jane");
        professional2.setVehicle(vehicle);
    }

    @Test
    void testCreateBooking_Success() {
        when(cleaningProfessionalRepository.findById(1L)).thenReturn(Optional.of(professional1));
        when(cleaningProfessionalRepository.findById(2L)).thenReturn(Optional.of(professional2));

        when(cleaningProfessionalRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(Arrays.asList(professional1, professional2));

        when(availabilityRepository.findByCleaningProfessionalIdAndDate(1L, startTime.toLocalDate()))
                .thenReturn(getAvailableSlots());
        when(availabilityRepository.findByCleaningProfessionalIdAndDate(2L, startTime.toLocalDate()))
                .thenReturn(getAvailableSlots());

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDateTime(startTime);
        booking.setEndDateTime(startTime.plusHours(2));
        booking.setCleaningProfessionals(List.of(professional1, professional2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.createBooking(startTime, 2, List.of(1L, 2L));

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(2, response.getProfessionalIds().size());
        assertEquals(1L, response.getVehicleId());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_DifferentVehicleException() {
        Vehicle differentVehicle = new Vehicle();
        differentVehicle.setId(2L);
        differentVehicle.setVehicleNumber("Truck");

        professional2.setVehicle(differentVehicle);

        when(cleaningProfessionalRepository.findById(1L)).thenReturn(Optional.of(professional1));
        when(cleaningProfessionalRepository.findById(2L)).thenReturn(Optional.of(professional2));
        when(cleaningProfessionalRepository.findById(1L)).thenReturn(Optional.of(professional1));

        assertThrows(DifferentVehicleException.class, () ->
                bookingService.createBooking(startTime, 2, List.of(1L, 2L)));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testUpdateBooking_BookingNotFoundException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () ->
                bookingService.updateBooking(1L, startTime, 2));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testUpdateBooking_AvailabilityException() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDateTime(startTime);
        booking.setEndDateTime(startTime.plusHours(2));
        booking.setCleaningProfessionals(List.of(professional1, professional2));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(AvailabilityException.class, () ->
                bookingService.updateBooking(1L, startTime, 2));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    private List<Availability> getAvailableSlots() {
        Availability availability = new Availability();
        availability.setStartTime(LocalTime.of(10, 0));
        availability.setEndTime(LocalTime.of(12, 0));
        availability.setStatus(AvailabilityStatus.AVAILABLE);
        return List.of(availability);
    }

    private List<Availability> getUnavailableSlots() {
        Availability availability = new Availability();
        availability.setStartTime(LocalTime.of(10, 0));
        availability.setEndTime(LocalTime.of(12, 0));
        availability.setStatus(AvailabilityStatus.BOOKED);
        return List.of(availability);
    }
}
