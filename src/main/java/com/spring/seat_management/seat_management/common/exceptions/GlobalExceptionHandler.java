package com.spring.seat_management.seat_management.common.exceptions;

//import io.jsonwebtoken.JwtException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        String errorCode = exception.getResourceName().toUpperCase() + "_NOT_FOUND";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).
                body(ErrorResponse.of(errorCode, exception.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(DuplicateResourceException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).
                body(ErrorResponse.of(exception.getErrorCode(), exception.getMessage()));
    }


    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ErrorResponse> handleBookingConflictException(BookingConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).
                body(ErrorResponse.of(exception.getErrorCode(), exception.getMessage()));
    }

//    // ✅ Catches expired JWT
//    @ExceptionHandler(ExpiredJwtException.class)
//    public ResponseEntity<ErrorResponse> handleExpiredJwt(
//            ExpiredJwtException ex) {
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(ErrorResponse.of("SESSION_EXPIRED", "Your session has expired. Please login again."));
//    }
//
//    // ✅ Catches invalid/malformed JWT
//    @ExceptionHandler(JwtException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidJwt(
//            JwtException ex) {
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(ErrorResponse.of(
//                        "INVALID_TOKEN",
//                        "Invalid token. Please login again."
//                ));
//    }
}
