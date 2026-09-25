package com.spring.seat_management.seat_management.repo;

import com.spring.seat_management.seat_management.entities.DeskRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface DeskReleaseRepo extends JpaRepository<DeskRelease, UUID> {

    boolean existsByDesk_DeskIdAndReleaseDate(
            UUID deskId,
            LocalDate releaseDate
    );
}
