package com.spring.seat_management.seat_management.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public interface DeskAvailabilityProjection {
    UUID getDeskId();

    Integer getDeskNumber();

    Boolean getIsActive();

    String getType();

    String getStatus();

    String getBookedBy();

    LocalDate getBookingFromDate();

    LocalDate getBookingToDate();
}