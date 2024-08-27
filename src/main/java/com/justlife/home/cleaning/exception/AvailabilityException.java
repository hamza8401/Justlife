package com.justlife.home.cleaning.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AvailabilityException extends RuntimeException {

    public AvailabilityException(String message) {
        super(message);
    }
}

