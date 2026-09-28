package com.spring.seat_management.seat_management.repo;

import com.spring.seat_management.seat_management.entities.DeskRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DeskReleaseRepo extends JpaRepository<DeskRelease, UUID> {

    boolean existsByDesk_DeskIdAndReleaseDate(
            UUID deskId,
            LocalDate releaseDate
    );

    DeskRelease findByDesk_DeskIdAndReleaseDate(UUID deskId, LocalDate currentDate);

    List<DeskRelease> findByDesk_DeskIdAndReleaseDateBetween(
            UUID deskId,
            LocalDate fromDate,
            LocalDate toDate
    );

}
