package com.spring.seat_management.seat_management.controller;

import com.spring.seat_management.seat_management.common.config.security.UserContext;
import com.spring.seat_management.seat_management.dto.request.DeskReleaseReq;
import com.spring.seat_management.seat_management.dto.response.DeskReleaseRes;
import com.spring.seat_management.seat_management.entities.User;
import com.spring.seat_management.seat_management.repo.UserRepo;
import com.spring.seat_management.seat_management.services.DeskReleaseService;
import com.spring.seat_management.seat_management.services.DeskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/desk-releases")
public class DeskReleaseController {

    private final DeskReleaseService deskReleaseService;

    @PutMapping("/release/{deskId}")
    public ResponseEntity<DeskReleaseRes> releaseDesk(@PathVariable UUID deskId,
                                                      @RequestParam LocalDate date) {
        return ResponseEntity.ok(deskReleaseService.releaseDesk(deskId, date, "DESK RELEASED"));
    }
}