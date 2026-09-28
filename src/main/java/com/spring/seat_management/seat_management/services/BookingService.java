package com.spring.seat_management.seat_management.services;

import com.spring.seat_management.seat_management.common.cache.DeskAvailabilityCache;
import com.spring.seat_management.seat_management.common.config.security.UserContext;
import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import com.spring.seat_management.seat_management.common.enums.DeskStatus;
import com.spring.seat_management.seat_management.common.exceptions.BadRequestException;
import com.spring.seat_management.seat_management.common.exceptions.BookingConflictException;
import com.spring.seat_management.seat_management.common.exceptions.ResourceNotFoundException;
import com.spring.seat_management.seat_management.dto.request.BookingReq;
import com.spring.seat_management.seat_management.dto.response.BookingRes;
import com.spring.seat_management.seat_management.dto.response.MyBookingRes;
import com.spring.seat_management.seat_management.entities.Booking;
import com.spring.seat_management.seat_management.entities.Desk;
import com.spring.seat_management.seat_management.entities.User;
import com.spring.seat_management.seat_management.repo.BookingRepo;
import com.spring.seat_management.seat_management.repo.DeskRepo;
import com.spring.seat_management.seat_management.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final DeskRepo deskRepo;
    private final UserContext userContext;
    private final DeskAvailabilityCache deskAvailabilityCache;

    @Transactional
    public BookingRes bookDesk(BookingReq request) {

        User user = userRepo.findById(userContext.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "USER_NOT_FOUND",
                                userContext.getUserId().toString()
                        )
                );

        Desk desk = deskRepo.findById(request.deskId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "DESK_NOT_FOUND",
                                request.deskId().toString()
                        )
                );

        if (!desk.getIsActive()) {
            throw new BadRequestException(
                    "DESK_NOT_ACTIVE",
                    "Desk is not active for booking"
            );
        }

        if (desk.getStatus() == DeskStatus.UNAVAILABLE) {
            throw new BadRequestException(
                    "DESK_UNAVAILABLE",
                    "Desk " + desk.getDeskNumber()
                            + " is currently unavailable"
            );
        }

        LocalDate fromDate = request.fromDate();
        LocalDate toDate = request.toDate() != null
                ? request.toDate()
                : fromDate;

        // Past date
        if (fromDate.isBefore(LocalDate.now())) {
            throw new BadRequestException(
                    "INVALID_BOOKING_DATE",
                    "Booking date cannot be in the past"
            );
        }

        // Invalid range
        if (toDate.isBefore(fromDate)) {
            throw new BadRequestException(
                    "INVALID_DATE_RANGE",
                    "To date cannot be before from date"
            );
        }

        List<BookingRes.BookingDate> bookedDates = new ArrayList<>();
        List<BookingRes.SkippedDate> skippedDates = new ArrayList<>();

        LocalDate currentDate = fromDate;

        while (!currentDate.isAfter(toDate)) {

            // Desk already booked
            boolean deskBooked =
                    bookingRepo.existsByDesk_DeskIdAndBookingDateAndStatus(
                            desk.getDeskId(),
                            currentDate,
                            BookingStatus.BOOKED
                    );

            if (deskBooked) {
                skippedDates.add(
                        new BookingRes.SkippedDate(
                                currentDate,
                                "Desk already booked"
                        )
                );

                currentDate = currentDate.plusDays(1);
                continue;
            }

            // User already has another booking on this date
            boolean userAlreadyBooked =
                    bookingRepo.existsByUser_UserIdAndBookingDateAndStatus(
                            user.getUserId(),
                            currentDate,
                            BookingStatus.BOOKED
                    );

            if (userAlreadyBooked) {
                skippedDates.add(
                        new BookingRes.SkippedDate(
                                currentDate,
                                "You already have a booking on this date"
                        )
                );

                currentDate = currentDate.plusDays(1);
                continue;
            }

            Booking booking = Booking.builder()
                    .user(user)
                    .desk(desk)
                    .bookingDate(currentDate)
                    .status(BookingStatus.BOOKED)
                    .build();

            try {
                Booking savedBooking = bookingRepo.saveAndFlush(booking);

                bookedDates.add(
                        new BookingRes.BookingDate(
                                savedBooking.getBookingId(),
                                savedBooking.getBookingDate()
                        )
                );

            } catch (DataIntegrityViolationException e) {

                // Another user booked the desk between our availability
                // check and database insert.
                skippedDates.add(
                        new BookingRes.SkippedDate(
                                currentDate,
                                "Desk was just booked by another user"
                        )
                );
            }

            deskAvailabilityCache.evict(currentDate);

            currentDate = currentDate.plusDays(1);
        }

        if (bookedDates.isEmpty()) {

            String reasons = skippedDates.stream()
                    .map(BookingRes.SkippedDate::reason)
                    .distinct()
                    .reduce(
                            (first, second) ->
                                    first + ", " + second
                    )
                    .orElse("No dates were available for booking");

            throw new BookingConflictException(
                    "BOOKING_CONFLICTS",
                    "No dates were booked. " + reasons
            );
        }

        return BookingRes.builder()
                .deskNumber(desk.getDeskNumber())
                .status(BookingStatus.BOOKED)
                .createdAt(LocalDateTime.now())
                .bookedBy(user.getName())
                .bookings(bookedDates)
                .skippedDates(skippedDates)
                .build();
    }

    @Transactional(readOnly = true)
    public List<BookingRes> getBookingsForDesk(UUID deskId) {

        List<Booking> bookings =
                bookingRepo.findByDesk_DeskIdAndStatusOrderByBookingDateAsc(
                        deskId,
                        BookingStatus.BOOKED
                );

        return bookings.stream()
                .map(booking ->
                        BookingRes.builder()
                                .deskNumber(
                                        booking.getDesk().getDeskNumber()
                                )
                                .status(booking.getStatus())
                                .createdAt(booking.getCreatedAt())
                                .bookedBy(
                                        booking.getUser().getName()
                                )
                                .bookings(
                                        List.of(
                                                new BookingRes.BookingDate(
                                                        booking.getBookingId(),
                                                        booking.getBookingDate()
                                                )
                                        )
                                )
                                .skippedDates(List.of())
                                .build()
                )
                .toList();
    }

    @Transactional
    public void cancelBooking(UUID bookingId) {

        User currentUser = userRepo.findById(userContext.getUserId())
                .orElseThrow(() ->
                        new BadRequestException(
                                "UNAUTHORIZED",
                                userContext.getName()
                                        + " is not authorized to cancel this booking."
                        )
                );

        Booking booking = bookingRepo.findByBookingId(bookingId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "BOOKING_NOT_FOUND",
                                "No booking found with ID: " + bookingId
                        )
                );

        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new BadRequestException(
                    "BOOKING_NOT_ACTIVE",
                    "Booking is already cancelled or inactive"
            );
        }

        boolean isAdmin =
                currentUser.getRole() != null
                        && currentUser.getRole().name().equals("ADMIN");

        boolean isOwner =
                booking.getUser()
                        .getUserId()
                        .equals(currentUser.getUserId());

        if (!isAdmin && !isOwner) {
            throw new BadRequestException(
                    "BOOKING_CANCEL_NOT_ALLOWED",
                    "You are not allowed to cancel this booking"
            );
        }

        LocalDate bookingDate = booking.getBookingDate();

        /*
         * Remove the row completely.
         *
         * This allows another user to book the same
         * desk/date because of the unique constraint.
         */
        bookingRepo.delete(booking);

        deskAvailabilityCache.evict(bookingDate);
    }

    @Transactional(readOnly = true)
    public List<MyBookingRes> getMyBookings() {

        UUID userId = userContext.getUserId();

        List<Booking> bookings =
                bookingRepo.findByUser_UserIdOrderByBookingDateAsc(userId);

        return bookings.stream()
                .map(booking ->
                        MyBookingRes.builder()
                                .bookingId(booking.getBookingId())
                                .deskNumber(
                                        booking.getDesk().getDeskNumber()
                                )
                                .status(booking.getStatus())
                                .bookedBy(
                                        booking.getUser().getName()
                                )
                                .bookingDate(
                                        booking.getBookingDate()
                                )
                                .build()
                )
                .toList();
    }
}