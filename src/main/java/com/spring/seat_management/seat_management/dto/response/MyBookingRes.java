package com.spring.seat_management.seat_management.dto.response;

import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record MyBookingRes(
        UUID bookingId,
        Integer deskNumber,
        BookingStatus status,
        String bookedBy,
        LocalDate fromDate,
        LocalDate toDate) {
}
