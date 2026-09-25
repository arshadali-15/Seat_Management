package com.spring.seat_management.seat_management.controller;

import com.spring.seat_management.seat_management.common.enums.DeskStatus;
import com.spring.seat_management.seat_management.common.enums.Section;
import com.spring.seat_management.seat_management.dto.response.BookingRes;
import com.spring.seat_management.seat_management.dto.response.DeskAvailabilityProjection;
import com.spring.seat_management.seat_management.dto.response.DeskAvailabilityRes;
import com.spring.seat_management.seat_management.entities.Desk;
import com.spring.seat_management.seat_management.services.BookingService;
import com.spring.seat_management.seat_management.services.DeskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/desks")
public class DeskController {
    private final DeskService deskService;

    @GetMapping
    public ResponseEntity<List<DeskAvailabilityRes>> getAllDesks(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        LocalDate bookingDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(deskService.getAllDesks(bookingDate));
    }

    @GetMapping("/{section}")
    public ResponseEntity<List<DeskAvailabilityRes>> getDesksBySection(
            @PathVariable Section section,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        LocalDate bookingDate = date != null ? date : LocalDate.now();

        return ResponseEntity.ok(
                deskService.getDesksBySection(section, bookingDate)
        );
    }

    @PatchMapping("/{deskId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateDeskStatus(
            @PathVariable UUID deskId,
            @RequestParam DeskStatus status) {
        deskService.updateStatus(deskId, status);
        return ResponseEntity.noContent().build();
    }

    // ✅ Admin — toggle maintenance (permanent deactivation)
    @PatchMapping("/{deskId}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setActiveStatus(
            @PathVariable UUID deskId,
            @RequestParam boolean isActive) {
        deskService.setActiveStatus(deskId, isActive);
        return ResponseEntity.noContent().build();
    }


}
