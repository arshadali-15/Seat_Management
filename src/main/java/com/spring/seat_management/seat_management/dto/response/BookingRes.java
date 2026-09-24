package com.spring.seat_management.seat_management.dto.response;

import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record BookingRes(
        UUID bookingId,
        Integer deskNumber,
        String bookedBy,
        BookingStatus status,
        LocalDateTime createdAt,
        LocalDate bookingFromDate,
        LocalDate bookingToDate
) {
}