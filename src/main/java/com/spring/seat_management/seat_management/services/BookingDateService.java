package com.spring.seat_management.seat_management.services;

import com.spring.seat_management.seat_management.common.cache.DeskAvailabilityCache;
import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import com.spring.seat_management.seat_management.common.exceptions.ResourceNotFoundException;
import com.spring.seat_management.seat_management.entities.Booking;
import com.spring.seat_management.seat_management.entities.BookingDateResult;
import com.spring.seat_management.seat_management.entities.Desk;
import com.spring.seat_management.seat_management.entities.User;
import com.spring.seat_management.seat_management.repo.BookingRepo;
import com.spring.seat_management.seat_management.repo.DeskRepo;
import com.spring.seat_management.seat_management.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingDateService {

    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final DeskRepo deskRepo;
    private final DeskAvailabilityCache deskAvailabilityCache;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BookingDateResult bookForDate(
            UUID userId,
            UUID deskId,
            LocalDate bookingDate
    ) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "USER_NOT_FOUND",
                        userId.toString()
                ));

        Desk desk = deskRepo.findById(deskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DESK_NOT_FOUND",
                        deskId.toString()
                ));

        /*
         * Check whether the desk is already booked.
         */
        boolean deskBooked =
                bookingRepo.existsByDesk_DeskIdAndBookingDateAndStatus(
                        deskId,
                        bookingDate,
                        BookingStatus.BOOKED
                );

        if (deskBooked) {
            return BookingDateResult.skipped(
                    "Desk already booked"
            );
        }

        /*
         * Check whether the user already has a booking
         * on this date.
         */
        boolean userAlreadyBooked =
                bookingRepo.existsByUser_UserIdAndBookingDateAndStatus(
                        userId,
                        bookingDate,
                        BookingStatus.BOOKED
                );

        if (userAlreadyBooked) {
            return BookingDateResult.skipped(
                    "You already have a booking on this date"
            );
        }

        Booking booking = Booking.builder()
                .user(user)
                .desk(desk)
                .bookingDate(bookingDate)
                .status(BookingStatus.BOOKED)
                .build();

        try {

            Booking savedBooking =
                    bookingRepo.saveAndFlush(booking);

            /*
             * Availability changed for this date.
             */
            deskAvailabilityCache.evict(bookingDate);

            return BookingDateResult.booked(
                    savedBooking.getBookingId()
            );

        } catch (DataIntegrityViolationException e) {

            /*
             * Another user may have booked the same
             * desk/date between our availability check
             * and INSERT.
             *
             * Because this method runs inside REQUIRES_NEW,
             * only this date transaction is rolled back.
             */
            return BookingDateResult.skipped(
                    "Desk was just booked by another user"
            );
        }
    }
}