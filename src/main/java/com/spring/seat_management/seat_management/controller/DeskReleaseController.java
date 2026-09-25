package com.spring.seat_management.seat_management.controller;

import com.spring.seat_management.seat_management.common.config.security.UserContext;
import com.spring.seat_management.seat_management.dto.request.DeskReleaseReq;
import com.spring.seat_management.seat_management.entities.User;
import com.spring.seat_management.seat_management.repo.UserRepo;
import com.spring.seat_management.seat_management.services.DeskReleaseService;
import com.spring.seat_management.seat_management.services.DeskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/desk-releases")
public class DeskReleaseController {

    private final DeskReleaseService deskReleaseService;
    private final UserContext userContext;
    private final UserRepo userRepo;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> releaseDesk(
            @RequestBody @Valid DeskReleaseReq request
    ) {
        deskReleaseService.releaseDesk(
                request.deskId(),
                request.releaseDate(),
                request.reason()
        );
        return ResponseEntity.ok().build();
    }
}