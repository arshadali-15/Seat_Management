package com.spring.seat_management.seat_management.common.exceptions;

import lombok.Getter;

@Getter
public class BookingConflictException extends RuntimeException {

    private final String errorCode;

    public BookingConflictException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}