package com.spring.seat_management.seat_management.controller;

import com.spring.seat_management.seat_management.dto.request.BookingReq;
import com.spring.seat_management.seat_management.dto.response.BookingRes;
import com.spring.seat_management.seat_management.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingRes> bookDesk(@RequestBody @Valid BookingReq request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.bookDesk(request));
    }

    @GetMapping("/desk/{deskId}")
    public ResponseEntity<List<BookingRes>> getDeskBookings(
            @PathVariable UUID deskId) {

        return ResponseEntity.ok(
                bookingService.getBookingsForDesk(deskId)
        );
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
