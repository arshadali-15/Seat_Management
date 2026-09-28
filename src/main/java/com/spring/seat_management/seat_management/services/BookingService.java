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
import com.spring.seat_management.seat_management.dto.response.MyBookingRes;
import com.spring.seat_management.seat_management.entities.Booking;
import com.spring.seat_management.seat_management.entities.Desk;
import com.spring.seat_management.seat_management.entities.DeskRelease;
import com.spring.seat_management.seat_management.entities.User;
import com.spring.seat_management.seat_management.mapper.BookingMapper;
import com.spring.seat_management.seat_management.repo.BookingRepo;
import com.spring.seat_management.seat_management.repo.DeskReleaseRepo;
import com.spring.seat_management.seat_management.repo.DeskRepo;
import com.spring.seat_management.seat_management.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final DeskRepo deskRepo;
    private final DeskReleaseRepo deskReleaseRepo;
    private final UserContext userContext;
    private final DeskAvailabilityCache deskAvailabilityCache;
    private final BookingMapper bookingMapper;


    @Transactional
    public BookingRes bookDesk(BookingReq request) {

        // 1. Get logged-in user
        User user = userRepo.findById(userContext.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "USER_NOT_FOUND",
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

        // 4. Desk must not be unavailable
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

        List<BookingRes.SkippedDate> skippedDates =
                new ArrayList<>();

        List<BookingRes.BookingRange> bookingRanges =
                new ArrayList<>();

        /*
         * Used to build consecutive NORMAL booking ranges.
         *
         * Example:
         *
         * 23 AVAILABLE
         * 24 AVAILABLE
         * 25 RELEASED
         * 26 AVAILABLE
         *
         * Results in:
         *
         * Booking 23-24
         * DeskRelease 25 -> BOOKED
         * Booking 26-26
         */
        LocalDate rangeStart = null;
        LocalDate previousBookedDate = null;

        LocalDate currentDate = fromDate;

        while (!currentDate.isAfter(toDate)) {

            /*
             * =====================================================
             * 7. CHECK DESK RELEASE FIRST
             * =====================================================
             *
             * A DeskRelease has precedence over a normal Booking.
             *
             * Possible states:
             *
             * AVAILABLE -> this user can book the released date.
             * BOOKED    -> someone already booked the released date.
             */
            DeskRelease release =
                    deskReleaseRepo.findByDesk_DeskIdAndReleaseDate(
                            desk.getDeskId(),
                            currentDate
                    );

            if (release != null) {

                /*
                 * -------------------------------------------------
                 * Released date is already booked
                 * -------------------------------------------------
                 */
                if (release.getStatus() == DeskStatus.BOOKED) {

                    /*
                     * Close any normal booking range before
                     * this released/blocked date.
                     */
                    if (rangeStart != null) {

                        Booking booking = createBooking(
                                user,
                                desk,
                                rangeStart,
                                previousBookedDate
                        );

                        Booking savedBooking =
                                bookingRepo.save(booking);

                        bookingRanges.add(
                                new BookingRes.BookingRange(
                                        savedBooking.getBookingId(),
                                        savedBooking.getBookingFromDate(),
                                        savedBooking.getBookingToDate()
                                )
                        );

                        /*
                         * Evict cache for this normal booking range.
                         */
                        rangeStart
                                .datesUntil(previousBookedDate.plusDays(1))
                                .forEach(deskAvailabilityCache::evict);

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

                /*
                 * -------------------------------------------------
                 * Released date is AVAILABLE
                 * -------------------------------------------------
                 *
                 * Book it through DeskRelease.
                 *
                 * DO NOT create a normal Booking row.
                 */
                if (release.getStatus() == DeskStatus.AVAILABLE) {

                    /*
                     * Close any normal booking range before
                     * this released date.
                     */
                    if (rangeStart != null) {

                        Booking booking = createBooking(
                                user,
                                desk,
                                rangeStart,
                                previousBookedDate
                        );

                        Booking savedBooking =
                                bookingRepo.save(booking);

                        bookingRanges.add(
                                new BookingRes.BookingRange(
                                        savedBooking.getBookingId(),
                                        savedBooking.getBookingFromDate(),
                                        savedBooking.getBookingToDate()
                                )
                        );

                        /*
                         * Evict cache for this normal booking range.
                         */
                        rangeStart
                                .datesUntil(previousBookedDate.plusDays(1))
                                .forEach(deskAvailabilityCache::evict);

                        rangeStart = null;
                        previousBookedDate = null;
                    }

                    /*
                     * Convert released date:
                     *
                     * AVAILABLE -> BOOKED
                     */
                    release.setStatus(DeskStatus.BOOKED);
                    release.setBookedBy(user.getName());
                    release.setBookedAt(
                            java.time.LocalDateTime.now()
                    );

                    /*
                     * If you have added bookedByUserId to DeskRelease,
                     * also set:
                     *
                     * release.setBookedByUserId(user.getUserId());
                     */

                    deskReleaseRepo.save(release);

                    bookedDates.add(currentDate);

                    deskAvailabilityCache.evict(currentDate);

                    currentDate = currentDate.plusDays(1);
                    continue;
                }
            }

            /*
             * =====================================================
             * 8. CHECK NORMAL BOOKING
             * =====================================================
             *
             * No DeskRelease exists, so now check the normal
             * Booking table.
             */
            Optional<Booking> bookingOnDate =
                    bookingRepo.findBookingOnDate(
                            desk.getDeskId(),
                            currentDate,
                            BookingStatus.BOOKED
                    );

            if (bookingOnDate.isPresent()) {

                /*
                 * Close current consecutive normal booking range.
                 */
                if (rangeStart != null) {

                    Booking booking = createBooking(
                            user,
                            desk,
                            rangeStart,
                            previousBookedDate
                    );

                    Booking savedBooking =
                            bookingRepo.save(booking);

                    bookingRanges.add(
                            new BookingRes.BookingRange(
                                    savedBooking.getBookingId(),
                                    savedBooking.getBookingFromDate(),
                                    savedBooking.getBookingToDate()
                            )
                    );

                    /*
                     * Evict cache for this range.
                     */
                    rangeStart
                            .datesUntil(previousBookedDate.plusDays(1))
                            .forEach(deskAvailabilityCache::evict);

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

            /*
             * =====================================================
             * 9. CHECK WHETHER USER ALREADY HAS A BOOKING
             * =====================================================
             */
            boolean userAlreadyBooked =
                    bookingRepo.existsUserBookingOnDate(
                            userContext.getUserId(),
                            currentDate,
                            BookingStatus.BOOKED
                    );

            if (userAlreadyBooked) {

                /*
                 * Close current consecutive normal booking range.
                 */
                if (rangeStart != null) {

                    Booking booking = createBooking(
                            user,
                            desk,
                            rangeStart,
                            previousBookedDate
                    );

                    Booking savedBooking =
                            bookingRepo.save(booking);

                    bookingRanges.add(
                            new BookingRes.BookingRange(
                                    savedBooking.getBookingId(),
                                    savedBooking.getBookingFromDate(),
                                    savedBooking.getBookingToDate()
                            )
                    );

                    /*
                     * Evict cache for this range.
                     */
                    rangeStart
                            .datesUntil(previousBookedDate.plusDays(1))
                            .forEach(deskAvailabilityCache::evict);

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

            /*
             * =====================================================
             * 10. DATE IS COMPLETELY AVAILABLE
             * =====================================================
             *
             * Add it to the current consecutive NORMAL booking
             * range.
             */
            bookedDates.add(currentDate);

            if (rangeStart == null) {
                rangeStart = currentDate;
            }

            previousBookedDate = currentDate;

            currentDate = currentDate.plusDays(1);
        }

        /*
         * =========================================================
         * 11. SAVE FINAL NORMAL BOOKING RANGE
         * =========================================================
         */
        if (rangeStart != null) {

            Booking booking = createBooking(
                    user,
                    desk,
                    rangeStart,
                    previousBookedDate
            );

            Booking savedBooking =
                    bookingRepo.save(booking);

            bookingRanges.add(
                    new BookingRes.BookingRange(
                            savedBooking.getBookingId(),
                            savedBooking.getBookingFromDate(),
                            savedBooking.getBookingToDate()
                    )
            );

            /*
             * Evict cache for final range.
             */
            rangeStart
                    .datesUntil(previousBookedDate.plusDays(1))
                    .forEach(deskAvailabilityCache::evict);
        }

        /*
         * =========================================================
         * 12. NOTHING WAS BOOKED
         * =========================================================
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
         * =========================================================
         * 13. RETURN BOOKING RESULT
         * =========================================================
         */
        return BookingRes.builder()
                .deskNumber(desk.getDeskNumber())
                .status(BookingStatus.BOOKED)
                .createdAt(null)
                .bookings(bookingRanges)
                .bookedBy(userContext.getName())
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
                                .bookedBy(booking.getUser().getName())
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
        return fromDate.datesUntil(toDate.plusDays(1)).toList();
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

        boolean isAdmin =
                currentUser.getRole() != null
                        && currentUser.getRole().name().equals("ADMIN");

        Booking existingBooking =
                bookingRepo.findById(bookingId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "BOOKING_NOT_FOUND",
                                        "No booking found with ID: "
                                                + bookingId
                                )
                        );

        // Already cancelled
        if (existingBooking.getStatus() != BookingStatus.BOOKED) {
            throw new BadRequestException(
                    "BOOKING_NOT_ACTIVE",
                    "Booking is already cancelled or inactive"
            );
        }

        // Check ownership
        boolean isOwner =
                existingBooking.getUser()
                        .getUserId()
                        .equals(currentUser.getUserId());

        if (!isAdmin && !isOwner) {
            throw new BadRequestException(
                    "BOOKING_CANCEL_NOT_ALLOWED",
                    "You are not allowed to cancel this booking"
            );
        }

        UUID deskId =
                existingBooking.getDesk().getDeskId();

        LocalDate fromDate =
                existingBooking.getBookingFromDate();

        LocalDate toDate =
                existingBooking.getBookingToDate();

        /*
         * 1. Cancel the original booking.
         */
        existingBooking.setStatus(BookingStatus.CANCELLED);

        bookingRepo.save(existingBooking);

        /*
         * 2. Find all release records belonging to
         *    this desk and booking date range.
         */
        List<DeskRelease> releases =
                deskReleaseRepo.findByDesk_DeskIdAndReleaseDateBetween(
                        deskId,
                        fromDate,
                        toDate
                );

        /*
         * 3. Remove only AVAILABLE releases.
         *
         * AVAILABLE:
         *     Original booking released the date,
         *     but nobody booked it.
         *
         * BOOKED:
         *     Another user booked the released date.
         *     We MUST keep this record.
         */
        releases.stream()
                .filter(release ->
                        release.getStatus() == DeskStatus.AVAILABLE
                )
                .forEach(deskReleaseRepo::delete);

        /*
         * 4. Evict availability cache for EVERY date
         *    covered by the original booking.
         */
        LocalDate currentDate = fromDate;

        while (!currentDate.isAfter(toDate)) {

            deskAvailabilityCache.evict(currentDate);

            currentDate = currentDate.plusDays(1);
        }
    }

    public List<MyBookingRes> getMyBookings() {

        UUID userId = userContext.getUserId();

        List<Booking> bookings =
                bookingRepo.findByUser_UserId(userId);

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
                                .fromDate(
                                        booking.getBookingFromDate()
                                )
                                .toDate(
                                        booking.getBookingToDate()
                                )
                                .build()
                )
                .toList();
    }
}