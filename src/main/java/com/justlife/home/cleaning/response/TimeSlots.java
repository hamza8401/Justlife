package com.justlife.home.cleaning.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class TimeSlots {
    private LocalTime startTime;
    private LocalTime endTime;
}
