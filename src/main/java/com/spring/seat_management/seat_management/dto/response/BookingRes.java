package com.spring.seat_management.seat_management.dto.response;

import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record BookingRes(
        Integer deskNumber,
        BookingStatus status,
        LocalDateTime createdAt,
        String bookedBy,
        List<BookingDate> bookings,
        List<SkippedDate> skippedDates
) {

    public record BookingDate(
            UUID bookingId,
            LocalDate date
    ) {
    }

    public record SkippedDate(
            LocalDate date,
            String reason
    ) {
    }
}