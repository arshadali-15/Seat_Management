package com.spring.seat_management.seat_management.services;

import com.spring.seat_management.seat_management.common.cache.DeskAvailabilityCache;
import com.spring.seat_management.seat_management.common.config.security.UserContext;
import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import com.spring.seat_management.seat_management.common.enums.DeskStatus;
import com.spring.seat_management.seat_management.common.exceptions.BadRequestException;
import com.spring.seat_management.seat_management.common.exceptions.BookingConflictException;
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

        // 1. Get logged-in user
        User user = userRepo.findById(userContext.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                userContext.getUserId().toString()
                        )
                );

        // 2. Get desk
        Desk desk = deskRepo.findById(request.deskId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Desk",
                                request.deskId().toString()
                        )
                );

        // 3. Desk must be active
        if (!desk.getIsActive()) {
            throw new BadRequestException(
                    "DESK_NOT_ACTIVE",
                    "Desk is not active for booking"
            );
        }

        // 4. Desk must be AVAILABLE
        if (desk.getStatus() == DeskStatus.UNAVAILABLE) {
            throw new BadRequestException(
                    "DESK_UNAVAILABLE",
                    "Desk " + desk.getDeskNumber()
                            + " is currently unavailable"
            );
        }

        // 5. Resolve requested date range
        LocalDate fromDate = request.fromDate();

        LocalDate toDate = request.toDate() != null
                ? request.toDate()
                : fromDate;

        // 6. Validate date range
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "From date cannot be after to date"
            );
        }

        List<LocalDate> bookedDates = new ArrayList<>();
        List<BookingRes.SkippedDate> skippedDates = new ArrayList<>();
        List<BookingRes.BookingRange> bookingRanges = new ArrayList<>();

        LocalDate rangeStart = null;
        LocalDate previousBookedDate = null;

        LocalDate currentDate = fromDate;

        while (!currentDate.isAfter(toDate)) {

            // 7. Check whether desk is already booked on this date
            boolean deskAlreadyBooked =
                    bookingRepo.existsDeskBookingOnDate(
                            request.deskId(),
                            currentDate,
                            BookingStatus.BOOKED
                    );

            if (deskAlreadyBooked) {

                // Close current consecutive range
                if (rangeStart != null) {

                    Booking booking = createBooking(
                            user,
                            desk,
                            rangeStart,
                            previousBookedDate
                    );

                    Booking savedBooking = bookingRepo.save(booking);


                    // Evict each // Evict each date in this range
                    rangeStart.datesUntil(previousBookedDate.plusDays(1))
                            .forEach(deskAvailabilityCache::evict);


                    bookingRanges.add(
                            new BookingRes.BookingRange(
                                    savedBooking.getBookingId(),
                                    savedBooking.getBookingFromDate(),
                                    savedBooking.getBookingToDate()
                            )
                    );

                    rangeStart = null;
                    previousBookedDate = null;
                }

                skippedDates.add(
                        new BookingRes.SkippedDate(
                                currentDate,
                                "Desk already booked"
                        )
                );

                currentDate = currentDate.plusDays(1);
                continue;
            }

            // 8. Check whether USER already has a booking on this date
            boolean userAlreadyBooked =
                    bookingRepo.existsUserBookingOnDate(
                            userContext.getUserId(),
                            currentDate,
                            BookingStatus.BOOKED
                    );

            if (userAlreadyBooked) {

                // Close current consecutive range
                if (rangeStart != null) {

                    Booking booking = createBooking(
                            user,
                            desk,
                            rangeStart,
                            previousBookedDate
                    );

                    Booking savedBooking = bookingRepo.save(booking);

                    bookingRanges.add(
                            new BookingRes.BookingRange(
                                    savedBooking.getBookingId(),
                                    savedBooking.getBookingFromDate(),
                                    savedBooking.getBookingToDate()
                            )
                    );

                    rangeStart = null;
                    previousBookedDate = null;
                }

                skippedDates.add(
                        new BookingRes.SkippedDate(
                                currentDate,
                                "You already have a booking"
                        )
                );

                currentDate = currentDate.plusDays(1);
                continue;
            }

            // 9. Date is available → add it to current range
            bookedDates.add(currentDate);

            if (rangeStart == null) {
                rangeStart = currentDate;
            }

            previousBookedDate = currentDate;

            currentDate = currentDate.plusDays(1);
        }

        // 10. Save final consecutive range
        if (rangeStart != null) {

            Booking booking = createBooking(
                    user,
                    desk,
                    rangeStart,
                    previousBookedDate
            );

            Booking savedBooking = bookingRepo.save(booking);

            bookingRanges.add(
                    new BookingRes.BookingRange(
                            savedBooking.getBookingId(),
                            savedBooking.getBookingFromDate(),
                            savedBooking.getBookingToDate()
                    )
            );
        }

        // 11. If nothing was booked, return the skipped result
        if (bookedDates.isEmpty()) {

            String reasons = skippedDates.stream()
                    .map(BookingRes.SkippedDate::reason)
                    .distinct()
                    .reduce((first, second) -> first + ", " + second)
                    .orElse("No dates were available for booking");

            throw new BookingConflictException(
                    "BOOKING_CONFLICTS",
                    "No dates were booked. " + reasons
            );
        }

        bookedDates.forEach(deskAvailabilityCache::evict);

        // 12. Return booking result
        return BookingRes.builder()
                .deskNumber(desk.getDeskNumber())
                .status(BookingStatus.BOOKED)
                .createdAt(null)
                .bookings(bookingRanges)
                .bookedDates(bookedDates)
                .skippedDates(skippedDates)
                .build();
    }

    private Booking createBooking(
            User user,
            Desk desk,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        return Booking.builder()
                .user(user)
                .desk(desk)
                .bookingFromDate(fromDate)
                .bookingToDate(toDate)
                .status(BookingStatus.BOOKED)
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
                .map(booking ->
                        BookingRes.builder()
                                .deskNumber(
                                        booking.getDesk().getDeskNumber()
                                )
                                .status(booking.getStatus())
                                .createdAt(booking.getCreatedAt())
                                .bookings(
                                        List.of(
                                                new BookingRes.BookingRange(
                                                        booking.getBookingId(),
                                                        booking.getBookingFromDate(),
                                                        booking.getBookingToDate()
                                                )
                                        )
                                )
                                .bookedDates(
                                        getDatesBetween(
                                                booking.getBookingFromDate(),
                                                booking.getBookingToDate()
                                        )
                                )
                                .skippedDates(List.of())
                                .build()
                )
                .toList();
    }

    private List<LocalDate> getDatesBetween(
            LocalDate fromDate,
            LocalDate toDate
    ) {

//        List<LocalDate> dates = new ArrayList<>();
//
//        LocalDate current = fromDate;
//
//        while (!current.isAfter(toDate)) {
//            dates.add(current);
//            current = current.plusDays(1);
//        }
//
//        return dates;
        return fromDate.datesUntil(toDate.plusDays(1)).toList();
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

        booking.getBookingFromDate()
                .datesUntil(booking.getBookingToDate().plusDays(1))
                .forEach(deskAvailabilityCache::evict);
    }
}