package com.spring.seat_management.seat_management.entities;

import java.util.UUID;

public record BookingDateResult(
        boolean booked,
        UUID bookingId,
        String reason
) {

    public static BookingDateResult booked(UUID bookingId) {
        return new BookingDateResult(
                true,
                bookingId,
                null
        );
    }

    public static BookingDateResult skipped(String reason) {
        return new BookingDateResult(
                false,
                null,
                reason
        );
    }
}