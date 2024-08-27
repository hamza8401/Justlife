package com.justlife.home.cleaning.response;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponse {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;;
    private List<Long> professionalIds;
    private Long vehicleId;
}

