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

        List<BookingRange> bookings,

        List<LocalDate> bookedDates,

        List<SkippedDate> skippedDates
) {

    public record BookingRange(
            UUID bookingId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
    }

    public record SkippedDate(
            LocalDate date,
            String reason
    ) {
    }
}