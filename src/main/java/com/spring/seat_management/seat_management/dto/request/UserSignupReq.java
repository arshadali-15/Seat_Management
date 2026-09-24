package com.spring.seat_management.seat_management.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
        String password
) {
}
