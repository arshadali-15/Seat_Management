package com.spring.seat_management.seat_management.services;

import com.spring.seat_management.seat_management.common.config.security.UserContext;
import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import com.spring.seat_management.seat_management.common.exceptions.DuplicateResourceException;
import com.spring.seat_management.seat_management.common.exceptions.ResourceNotFoundException;
import com.spring.seat_management.seat_management.dto.request.BookingReq;
import com.spring.seat_management.seat_management.dto.response.BookingRes;
import com.spring.seat_management.seat_management.entities.Booking;
import com.spring.seat_management.seat_management.entities.Desk;
import com.spring.seat_management.seat_management.entities.User;
import com.spring.seat_management.seat_management.repo.BookingRepo;
import com.spring.seat_management.seat_management.repo.DeskRepo;
import com.spring.seat_management.seat_management.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final DeskRepo deskRepo;
    private final UserContext userContext;

    @Transactional
    public BookingRes bookDesk(BookingReq request) {

        User user = userRepo.findById(userContext.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                userContext.getUserId().toString()
                        )
                );

        Desk desk = deskRepo.findById(request.deskId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Desk",
                                request.deskId().toString()
                        )
                );

        // 1. Desk must be active
        if (!desk.getIsActive()) {
            throw new DuplicateResourceException(
                    "DESK_NOT_ACTIVE",
                    "Desk is not active for booking"
            );
        }

        // 2. Resolve requested date range
        LocalDate fromDate = request.fromDate();

        LocalDate toDate = request.toDate() != null
                ? request.toDate()
                : fromDate;

        // 3. Validate date range
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "From date cannot be after to date"
            );
        }

        // 4. Check whether USER already has an overlapping booking
        boolean userAlreadyBooked =
                bookingRepo.existsOverlappingUserBooking(
                        userContext.getUserId(),
                        fromDate,
                        toDate,
                        BookingStatus.BOOKED
                );

        if (userAlreadyBooked) {
            throw new DuplicateResourceException(
                    "USER_BOOKING_OVERLAP",
                    "You already have a booking that overlaps with the requested date range"
            );
        }

        // 5. Check whether DESK already has an overlapping booking
        boolean deskAlreadyBooked =
                bookingRepo.existsOverlappingDeskBooking(
                        request.deskId(),
                        fromDate,
                        toDate,
                        BookingStatus.BOOKED
                );

        if (deskAlreadyBooked) {
            throw new DuplicateResourceException(
                    "DESK_BOOKING_OVERLAP",
                    "Desk " + desk.getDeskNumber()
                            + " is already booked for part of the requested date range"
            );
        }

        // 6. Create ONE booking for the complete requested range
        Booking booking = Booking.builder()
                .user(user)
                .desk(desk)
                .bookingFromDate(fromDate)
                .bookingToDate(toDate)
                .status(BookingStatus.BOOKED)
                .build();

        // 7. Save booking
        Booking savedBooking = bookingRepo.save(booking);

        // 8. Return the actual saved booking
        return BookingRes.builder()
                .bookingId(savedBooking.getBookingId())
                .deskNumber(desk.getDeskNumber())
                .bookedBy(user.getName())
                .status(savedBooking.getStatus())
                .createdAt(savedBooking.getCreatedAt())
                .bookingFromDate(savedBooking.getBookingFromDate())
                .bookingToDate(savedBooking.getBookingToDate())
                .build();
    }

    @Transactional(readOnly = true)
    public List<BookingRes> getBookingsForDesk(UUID deskId) {

        List<Booking> bookings =
                bookingRepo.findByDeskIdAndStatus(
                        deskId,
                        BookingStatus.BOOKED
                );

        return bookings.stream()
                .map(booking -> BookingRes.builder()
                        .bookingId(booking.getBookingId())
                        .deskNumber(booking.getDesk().getDeskNumber())
                        .bookedBy(booking.getUser().getName())
                        .status(booking.getStatus())
                        .createdAt(booking.getCreatedAt())
                        .bookingFromDate(booking.getBookingFromDate())
                        .bookingToDate(booking.getBookingToDate())
                        .build())
                .toList();
    }

    @Transactional
    public void cancelBooking(UUID bookingId) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking",
                                bookingId.toString()
                        )
                );

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new DuplicateResourceException(
                    "BOOKING_ALREADY_CANCELLED",
                    "Booking is already cancelled"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepo.save(booking);
    }
}