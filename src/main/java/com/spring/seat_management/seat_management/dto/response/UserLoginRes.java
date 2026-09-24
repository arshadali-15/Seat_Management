package com.spring.seat_management.seat_management.dto.response;

import lombok.Builder;
import lombok.Data;

public record UserLoginRes(
        String accessToken
) {
}
