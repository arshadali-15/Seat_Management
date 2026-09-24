package com.spring.seat_management.seat_management.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record BookingReq(
        @NotNull(message = "Desk ID is required")
        UUID deskId,

        @NotNull(message = "Booking date is required")
        LocalDate fromDate,

        LocalDate toDate
) {
}
