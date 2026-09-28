package com.spring.seat_management.seat_management.services;

import com.spring.seat_management.seat_management.common.config.security.UserContext;
import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import com.spring.seat_management.seat_management.common.enums.DeskStatus;
import com.spring.seat_management.seat_management.common.exceptions.BadRequestException;
import com.spring.seat_management.seat_management.common.exceptions.ResourceNotFoundException;
import com.spring.seat_management.seat_management.dto.response.DeskReleaseRes;
import com.spring.seat_management.seat_management.entities.Desk;
import com.spring.seat_management.seat_management.entities.DeskRelease;
import com.spring.seat_management.seat_management.entities.User;
import com.spring.seat_management.seat_management.repo.BookingRepo;
import com.spring.seat_management.seat_management.repo.DeskReleaseRepo;
import com.spring.seat_management.seat_management.repo.DeskRepo;
import com.spring.seat_management.seat_management.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeskReleaseService {

    private final DeskReleaseRepo deskReleaseRepo;
    private final DeskRepo deskRepo;
    private final UserRepo userRepo;
    private final BookingRepo bookingRepo;
    private final UserContext userContext;

    @Transactional
    public DeskReleaseRes releaseDesk(
            UUID deskId,
            LocalDate releaseDate,
            String reason
    ) {
        Desk desk = deskRepo.findById(deskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("DESK_NOT_FOUND", "Desk :" + deskId));

        UUID userId = userContext.getUserId();

        boolean isAdmin =
                userContext.getRole() != null
                        && userContext.getRole().equals("ADMIN");

        // 3. Verify that this user owns a booking
        bookingRepo.findUserBookingOnDate(
                deskId,
                userId,
                releaseDate,
                BookingStatus.BOOKED
        ).orElseThrow(() ->
                new BadRequestException(
                        "BOOKING_NOT_FOUND",
                        "You do not have an active booking for Desk :"
                                + deskId
                                + " on "
                                + releaseDate
                )
        );

        DeskRelease existingRelease =
                deskReleaseRepo.findByDesk_DeskIdAndReleaseDate(
                        deskId,
                        releaseDate
                );

        // Existing release record
        if (isAdmin) {

            reason = "RELEASED BY ADMIN";

            if (existingRelease != null) {

                existingRelease.setStatus(DeskStatus.AVAILABLE);
                existingRelease.setBookedBy(null);
                existingRelease.setBookedAt(null);
                existingRelease.setReason(reason);
                existingRelease.setReleasedBy(userContext.getName());
                existingRelease.setReleasedAt(LocalDateTime.now());

                deskReleaseRepo.save(existingRelease);

                return DeskReleaseRes.builder()
                        .deskId(deskId)
                        .deskNumber(desk.getDeskNumber())
                        .releaseDate(releaseDate)
                        .build();
            }

            DeskRelease deskRelease = DeskRelease.builder()
                    .desk(desk)
                    .releaseDate(releaseDate)
                    .reason(reason)
                    .releasedBy(userContext.getName())
                    .status(DeskStatus.AVAILABLE)
                    .releasedAt(LocalDateTime.now())
                    .build();

            deskReleaseRepo.save(deskRelease);

            return DeskReleaseRes.builder()
                    .deskId(deskId)
                    .deskNumber(desk.getDeskNumber())
                    .releaseDate(releaseDate)
                    .build();
        }

        /*
         * EMPLOYEE
         * --------
         * Employee can release only their own booking date.
         */
        bookingRepo.findUserBookingOnDate(
                deskId,
                userId,
                releaseDate,
                BookingStatus.BOOKED
        ).orElseThrow(() ->
                new BadRequestException(
                        "BOOKING_NOT_FOUND",
                        "You do not have an active booking for Desk :"
                                + deskId
                                + " on "
                                + releaseDate
                )
        );

        if (existingRelease != null) {

            if (existingRelease.getStatus() == DeskStatus.AVAILABLE) {
                throw new BadRequestException(
                        "DESK_ALREADY_RELEASED",
                        "Desk :" + deskId
                                + " is already released on "
                                + releaseDate
                );
            }

            if (existingRelease.getStatus() == DeskStatus.BOOKED) {
                throw new BadRequestException(
                        "DESK_ALREADY_BOOKED",
                        "Desk :" + deskId
                                + " is already booked on "
                                + releaseDate
                );
            }
        }

        DeskRelease deskRelease = DeskRelease.builder()
                .desk(desk)
                .releaseDate(releaseDate)
                .reason(reason)
                .releasedBy(userContext.getName())
                .status(DeskStatus.AVAILABLE)
                .releasedAt(LocalDateTime.now())
                .build();

        deskReleaseRepo.save(deskRelease);

        return DeskReleaseRes.builder()
                .deskId(deskId)
                .deskNumber(desk.getDeskNumber())
                .releaseDate(releaseDate)
                .build();
    }
}