package com.spring.seat_management.seat_management.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record DeskReleaseRes(
        UUID deskId,
        Integer deskNumber,
        LocalDate releaseDate
) {
}
