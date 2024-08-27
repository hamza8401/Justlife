package com.justlife.home.cleaning.request;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class BookingRequest {

    private LocalDateTime startTime;
    private int duration;
    private List<Long> professionalIds;
}

