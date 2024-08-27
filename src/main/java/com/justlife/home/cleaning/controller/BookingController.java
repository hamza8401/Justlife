package com.justlife.home.cleaning.controller;

import com.justlife.home.cleaning.request.BookingRequest;
import com.justlife.home.cleaning.request.BookingUpdateRequest;
import com.justlife.home.cleaning.response.AvailabilityResponse;
import com.justlife.home.cleaning.response.BookingResponse;
import com.justlife.home.cleaning.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking", description = "APIs related to Booking operations")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Check availability of cleaning professionals", description = "Check the availability of cleaning professionals for a given date and time")
    @ApiResponse(responseCode = "200", description = "List of available professionals",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AvailabilityResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @GetMapping("/availability")
    @ResponseStatus(HttpStatus.OK)
    public List<AvailabilityResponse> checkAvailability(@RequestParam LocalDate date,
                                                        @RequestParam(required = false) LocalDateTime startTime,
                                                        @RequestParam(required = false, defaultValue = "0") Integer duration) {
        return bookingService.checkAvailability(date, startTime, duration);
    }

    @Operation(summary = "Create a new booking", description = "Create a new booking with specified details")
    @ApiResponse(responseCode = "201", description = "Booking created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookingResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request.getStartTime(), request.getDuration(), request.getProfessionalIds());
    }

    @Operation(summary = "Update an existing booking", description = "Update the details of an existing booking")
    @ApiResponse(responseCode = "200", description = "Booking updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookingResponse.class)))
    @ApiResponse(responseCode = "404", description = "Booking not found")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse updateBooking(@PathVariable Long id, @RequestBody BookingUpdateRequest request) {
        return bookingService.updateBooking(id, request.getNewStartTime(), request.getNewDuration());
    }
}

