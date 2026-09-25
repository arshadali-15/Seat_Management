package com.spring.seat_management.seat_management.repo;

import com.spring.seat_management.seat_management.common.enums.Section;
import com.spring.seat_management.seat_management.dto.response.DeskAvailabilityProjection;
import com.spring.seat_management.seat_management.dto.response.DeskAvailabilityRes;
import com.spring.seat_management.seat_management.entities.Desk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DeskRepo extends JpaRepository<Desk, UUID> {

    @Query(value = """
            SELECT 
                d.desk_id AS deskId,
                d.desk_number AS deskNumber,
                d.is_active AS isActive,
                CASE
                    WHEN d.status = 'UNAVAILABLE'
                        THEN 'UNAVAILABLE'
                    WHEN d.is_active = false
                        THEN 'INACTIVE'
                    WHEN b.booking_id IS NOT NULL
                        THEN 'BOOKED'
                    ELSE 'AVAILABLE'
                END AS status,
            
                u.name AS bookedBy,
                b.booking_from_date AS bookingFromDate,
                b.booking_to_date AS bookingToDate
            
            FROM desk d
            
            LEFT JOIN bookings b
                   ON b.desk_id = d.desk_id
                  AND b.booking_from_date <= :date
                  AND b.booking_to_date >= :date
                  AND b.status = 'BOOKED'
            
            LEFT JOIN users u
                   ON u.user_id = b.user_id
            
            ORDER BY d.desk_number ASC
            """, nativeQuery = true)
    List<DeskAvailabilityProjection> findDesksWithAvailability(
            @Param("date") LocalDate date
    );

    @Query(value = """
            SELECT
                d.desk_id AS deskId,
                d.desk_number AS deskNumber,
                d.is_active AS isActive,
            
                CASE
                    WHEN d.status = 'UNAVAILABLE'
                        THEN 'UNAVAILABLE'
            
                    WHEN d.is_active = false
                        THEN 'INACTIVE'
            
                    WHEN dr.release_id IS NOT NULL
                        THEN 'AVAILABLE'
            
                    WHEN b.booking_id IS NOT NULL
                        THEN 'BOOKED'
            
                    ELSE 'AVAILABLE'
                END AS status,
            
                CASE
                    WHEN dr.release_id IS NOT NULL
                        THEN NULL
                    ELSE u.name
                END AS bookedBy,
            
                CASE
                    WHEN dr.release_id IS NOT NULL
                        THEN NULL
                    ELSE b.booking_from_date
                END AS bookingFromDate,
            
                CASE
                    WHEN dr.release_id IS NOT NULL
                        THEN NULL
                    ELSE b.booking_to_date
                END AS bookingToDate
            
            FROM desk d
            
            LEFT JOIN desk_releases dr
                   ON dr.desk_id = d.desk_id
                  AND dr.release_date = :date
            
            LEFT JOIN bookings b
                   ON b.desk_id = d.desk_id
                  AND b.booking_from_date <= :date
                  AND b.booking_to_date >= :date
                  AND b.status = 'BOOKED'
            
            LEFT JOIN users u
                   ON u.user_id = b.user_id
            
            WHERE d.section = :section
            
            ORDER BY d.desk_number ASC
            """, nativeQuery = true)
    List<DeskAvailabilityProjection> findDesksWithAvailabilityBySection(
            @Param("section") String section,
            @Param("date") LocalDate date
    );

}
