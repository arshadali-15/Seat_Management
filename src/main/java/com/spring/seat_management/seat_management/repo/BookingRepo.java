package com.spring.seat_management.seat_management.repo;

import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import com.spring.seat_management.seat_management.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepo extends JpaRepository<Booking, UUID> {

    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            WHERE b.desk.deskId = :deskId
              AND b.status = :status
              AND b.bookingFromDate <= :requestedToDate
              AND b.bookingToDate >= :requestedFromDate
            """)
    boolean existsOverlappingDeskBooking(
            @Param("deskId") UUID deskId,
            @Param("requestedFromDate") LocalDate requestedFromDate,
            @Param("requestedToDate") LocalDate requestedToDate,
            @Param("status") BookingStatus status
    );

    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            WHERE b.user.userId = :userId
              AND b.status = :status
              AND b.bookingFromDate <= :requestedToDate
              AND b.bookingToDate >= :requestedFromDate
            """)
    boolean existsOverlappingUserBooking(
            @Param("userId") UUID userId,
            @Param("requestedFromDate") LocalDate requestedFromDate,
            @Param("requestedToDate") LocalDate requestedToDate,
            @Param("status") BookingStatus status
    );

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.desk.deskId = :deskId
              AND b.status = :status
            ORDER BY b.bookingFromDate ASC
            """)
    List<Booking> findByDeskIdAndStatus(
            @Param("deskId") UUID deskId,
            @Param("status") BookingStatus status
    );
}