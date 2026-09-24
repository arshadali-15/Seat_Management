package com.spring.seat_management.seat_management.dto.response;

import com.spring.seat_management.seat_management.common.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserSignupRes(
        UUID userId,
        String name,
        String email,
        String slsId,
        Role role,
        LocalDateTime createdAt
) {
}