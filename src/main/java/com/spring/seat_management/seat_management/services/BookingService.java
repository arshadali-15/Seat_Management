package com.spring.seat_management.seat_management.services;

import com.spring.seat_management.seat_management.common.cache.DeskAvailabilityCache;
import com.spring.seat_management.seat_management.common.config.security.UserContext;
import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import com.spring.seat_management.seat_management.common.enums.DeskStatus;
import com.spring.seat_management.seat_management.common.enums.Role;
import com.spring.seat_management.seat_management.common.exceptions.BadRequestException;
import com.spring.seat_management.seat_management.common.exceptions.BookingConflictException;
import com.spring.seat_management.seat_management.common.exceptions.ResourceNotFoundException;
import com.spring.seat_management.seat_management.dto.request.BookingReq;
import com.spring.seat_management.seat_management.dto.response.BookingRes;
import com.spring.seat_management.seat_management.dto.response.MyBookingRes;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final DeskRepo deskRepo;
    private final UserContext userContext;
    private final DeskAvailabilityCache deskAvailabilityCache;
    private final BookingDateService bookingDateService;

    @Transactional
    public BookingRes bookDesk(BookingReq request) {

        UUID userId = userContext.getUserId();

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "USER_NOT_FOUND",
                        userId.toString()
                ));

        Desk desk = deskRepo.findById(request.deskId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DESK_NOT_FOUND",
                        request.deskId().toString()
                ));

        /*
         * Validate desk.
         */
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

        /*
         * Resolve date range.
         *
         * If toDate is null, only fromDate is booked.
         */
        LocalDate fromDate = request.fromDate();

        LocalDate toDate = request.toDate() != null
                ? request.toDate()
                : fromDate;

        /*
         * Validate dates.
         */
        if (fromDate.isBefore(LocalDate.now())) {
            throw new BadRequestException(
                    "INVALID_BOOKING_DATE",
                    "Booking date cannot be in the past"
            );
        }

        if (toDate.isBefore(fromDate)) {
            throw new BadRequestException(
                    "INVALID_DATE_RANGE",
                    "To date cannot be before from date"
            );
        }

        List<BookingRes.BookingDate> bookedDates =
                new ArrayList<>();

        List<BookingRes.SkippedDate> skippedDates =
                new ArrayList<>();

        /*
         * Process every date independently.
         */
        LocalDate currentDate = fromDate;

        while (!currentDate.isAfter(toDate)) {

            BookingDateResult result =
                    bookingDateService.bookForDate(
                            userId,
                            request.deskId(),
                            currentDate
                    );

            if (result.booked()) {

                bookedDates.add(
                        new BookingRes.BookingDate(
                                result.bookingId(),
                                currentDate
                        )
                );

            } else {

                skippedDates.add(
                        new BookingRes.SkippedDate(
                                currentDate,
                                result.reason()
                        )
                );
            }

            currentDate = currentDate.plusDays(1);
        }

        /*
         * Nothing was booked.
         */
        if (bookedDates.isEmpty()) {

            String reasons = skippedDates.stream()
                    .map(BookingRes.SkippedDate::reason)
                    .distinct()
                    .reduce(
                            (first, second) ->
                                    first + ", " + second
                    )
                    .orElse(
                            "No dates were available for booking"
                    );

            throw new BookingConflictException(
                    "BOOKING_CONFLICTS",
                    "No dates were booked. " + reasons
            );
        }

        /*
         * At least one date was successfully booked.
         */
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

        UUID userId = userContext.getUserId();

        Booking booking = bookingRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BOOKING_NOT_FOUND",
                        bookingId.toString()
                ));

        if (!booking.getUser().getUserId().equals(userId) && !Objects.equals(userContext.getRole(), Role.ADMIN.name())) {
            throw new BadRequestException("UNAUTHORIZED",
                    "You are not authorized to cancel this booking"
            );
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException(
                    "BOOKING_ALREADY_CANCELLED",
                    "Booking is already cancelled"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);

        bookingRepo.save(booking);

        deskAvailabilityCache.evict(
                booking.getDesk().getSection(),
                booking.getBookingDate()
        );
    }

    @Transactional(readOnly = true)
    public List<MyBookingRes> getMyBookings() {

        UUID userId = userContext.getUserId();

        List<Booking> bookings =
                bookingRepo.findByUser_UserIdAndStatusOrderByBookingDateAsc(userId, BookingStatus.BOOKED);

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