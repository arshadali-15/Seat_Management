package com.spring.seat_management.seat_management.services;

import com.spring.seat_management.seat_management.common.config.security.UserContext;
import com.spring.seat_management.seat_management.entities.Desk;
import com.spring.seat_management.seat_management.entities.DeskRelease;
import com.spring.seat_management.seat_management.entities.User;
import com.spring.seat_management.seat_management.repo.DeskReleaseRepo;
import com.spring.seat_management.seat_management.repo.DeskRepo;
import com.spring.seat_management.seat_management.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeskReleaseService {

    private final DeskReleaseRepo deskReleaseRepo;
    private final DeskRepo deskRepo;
    private final UserRepo userRepo;
    private final UserContext userContext;

    @Transactional
    public void releaseDesk(
            UUID deskId,
            LocalDate releaseDate,
            String reason
    ) {
        Desk desk = deskRepo.findById(deskId)
                .orElseThrow(() ->
                        new RuntimeException("Desk not found")
                );

        User user = userRepo.findByEmail(userContext.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyReleased =
                deskReleaseRepo.existsByDesk_DeskIdAndReleaseDate(
                        deskId,
                        releaseDate
                );

        if (alreadyReleased) {
            throw new RuntimeException(
                    "Desk is already released for " + releaseDate
            );
        }

        DeskRelease deskRelease = DeskRelease.builder()
                .desk(desk)
                .releaseDate(releaseDate)
                .reason(reason)
                .releasedBy(user.getName())
                .releasedAt(LocalDateTime.now())
                .build();

        deskReleaseRepo.save(deskRelease);
    }
}