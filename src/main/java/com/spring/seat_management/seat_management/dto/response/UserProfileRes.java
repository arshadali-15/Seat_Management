package com.spring.seat_management.seat_management.dto.response;

import java.util.UUID;

public record UserProfileRes(
        UUID userId,
        String name,
        String email,
        String role
) {
}
