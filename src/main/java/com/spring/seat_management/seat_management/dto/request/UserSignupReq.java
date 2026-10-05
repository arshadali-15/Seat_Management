package com.spring.seat_management.seat_management.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jdk.jfr.Name;
import org.springframework.validation.annotation.Validated;

public record UserSignupReq(
        @NotNull(message = "Email should be provided")
        @Email
        String email,

        @NotNull(message = "Name should be provided")
        @Size(max = 50, message = "Name should not exceed 50 characters")
        String name,

        @NotNull(message = "SLSID should be provided")
        String SLSID,

        @NotNull(message = "Password should be provided")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password
) {
}
