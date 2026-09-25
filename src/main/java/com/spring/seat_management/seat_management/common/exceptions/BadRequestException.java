package com.spring.seat_management.seat_management.common.exceptions;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    final String errorCode;

    public BadRequestException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
