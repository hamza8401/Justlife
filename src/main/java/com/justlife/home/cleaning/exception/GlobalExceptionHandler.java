package com.justlife.home.cleaning.exception;

import com.justlife.home.cleaning.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleBookingNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Booking not found. Please check the booking ID and try again."));
    }

    @ExceptionHandler(AvailabilityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleAvailabilityConflict() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("There was a conflict with the availability. Please try again later."));
    }

    @ExceptionHandler(DifferentVehicleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDifferentVehicles() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Professionals must belong to the same vehicle"));
    }
}
