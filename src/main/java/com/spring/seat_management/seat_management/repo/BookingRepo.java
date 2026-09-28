package com.spring.seat_management.seat_management.repo;

import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import com.spring.seat_management.seat_management.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepo extends JpaRepository<Booking, UUID> {

    Optional<Booking> findByDesk_DeskIdAndBookingDateAndStatus(
            UUID deskId,
            LocalDate bookingDate,
            BookingStatus status
    );

    boolean existsByDesk_DeskIdAndBookingDateAndStatus(
            UUID deskId,
            LocalDate bookingDate,
            BookingStatus status
    );

    boolean existsByUser_UserIdAndBookingDateAndStatus(
            UUID userId,
            LocalDate bookingDate,
            BookingStatus status
    );

    List<Booking> findByDesk_DeskIdAndStatusOrderByBookingDateAsc(
            UUID deskId,
            BookingStatus status
    );

    List<Booking> findByUser_UserIdOrderByBookingDateAsc(
            UUID userId
    );

    Optional<Booking> findByBookingId(UUID bookingId);

    List<Booking> findByUser_UserIdAndStatusOrderByBookingDateAsc(
            UUID userId,
            BookingStatus status
    );
}