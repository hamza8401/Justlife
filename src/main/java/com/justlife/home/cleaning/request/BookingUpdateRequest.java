package com.justlife.home.cleaning.request;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingUpdateRequest {

    private LocalDateTime newStartTime;
    private int newDuration;
}

