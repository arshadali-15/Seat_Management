package com.spring.seat_management.seat_management.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record DeskAvailabilityRes(
        UUID deskId,
        Integer deskNumber,
        Boolean isActive,
        String status,
        String bookedBy,
        LocalDate bookingDate
) {
}