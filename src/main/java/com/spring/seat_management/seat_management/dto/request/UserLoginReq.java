package com.spring.seat_management.seat_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLoginReq(
        @NotNull
        @Email
        String email,
        @NotBlank
        String password
) {
}
