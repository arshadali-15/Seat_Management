package com.spring.seat_management.seat_management.dto.response;

import com.spring.seat_management.seat_management.common.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AdminUserRes(
        UUID userId,
        String name,
        String email,
        String slsId,
        String role,
        List<AdminBookingRes> bookings
) {

    public record AdminBookingRes(
            UUID bookingId,
            Integer deskNumber,
            LocalDate bookingDate,
            BookingStatus status
    ) {
    }
}