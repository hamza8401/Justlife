package com.justlife.home.cleaning.response;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AvailabilityResponse {

    private Long professionalId;
    private String name;
    List<TimeSlots> availabilities;
}

