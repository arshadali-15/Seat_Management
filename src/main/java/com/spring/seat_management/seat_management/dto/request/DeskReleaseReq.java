package com.spring.seat_management.seat_management.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.UUID;

public record DeskReleaseReq(

        @NotNull(message = "Desk ID is required")
        UUID deskId,
        @CreationTimestamp
        LocalDate releaseDate,

        String reason
) {
}
