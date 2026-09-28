package com.spring.seat_management.seat_management.services;

import com.spring.seat_management.seat_management.common.cache.DeskAvailabilityCache;
import com.spring.seat_management.seat_management.common.enums.DeskStatus;
import com.spring.seat_management.seat_management.common.enums.Section;
import com.spring.seat_management.seat_management.common.exceptions.ResourceNotFoundException;
import com.spring.seat_management.seat_management.dto.response.DeskAvailabilityRes;
import com.spring.seat_management.seat_management.entities.Desk;
import com.spring.seat_management.seat_management.repo.DeskRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeskService {

    private final DeskRepo deskRepo;
    private final DeskAvailabilityCache deskAvailabilityCache;

    @Transactional(readOnly = true)
    public List<DeskAvailabilityRes> getAllDesks(LocalDate date) {

        Optional<List<DeskAvailabilityRes>> cachedDesks =
                deskAvailabilityCache.get(date);

        if (cachedDesks.isPresent()) {
            return cachedDesks.get();
        }

        List<DeskAvailabilityRes> desks = deskRepo
                .findDesksWithAvailability(date)
                .stream()
                .map(p -> new DeskAvailabilityRes(
                        p.getDeskId(),
                        p.getDeskNumber(),
                        p.getIsActive(),
                        p.getStatus(),
                        p.getBookedBy(),
                        p.getBookingDate()
                ))
                .toList();

        deskAvailabilityCache.put(date, desks);

        return desks;
    }

    @Transactional(readOnly = true)
    public List<DeskAvailabilityRes> getDesksBySection(
            Section section,
            LocalDate date
    ) {

        return deskRepo
                .findDesksWithAvailabilityBySection(
                        section.name(),
                        date
                )
                .stream()
                .map(projection -> new DeskAvailabilityRes(
                        projection.getDeskId(),
                        projection.getDeskNumber(),
                        projection.getIsActive(),
                        projection.getStatus(),
                        projection.getBookedBy(),
                        projection.getBookingDate()
                ))
                .toList();
    }

    @Transactional
    public void updateStatus(UUID deskId, DeskStatus status) {

        Desk desk = deskRepo.findById(deskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "DESK_NOT_FOUND",
                                "Desk not found"
                        )
                );

        desk.setStatus(status);
        deskRepo.save(desk);

        deskAvailabilityCache.evictAll();
    }

    @Transactional
    public void setActiveStatus(UUID deskId, boolean isActive) {

        Desk desk = deskRepo.findById(deskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "DESK_NOT_FOUND",
                                "Desk not found"
                        )
                );

        desk.setIsActive(isActive);
        deskRepo.save(desk);

        deskAvailabilityCache.evictAll();
    }
}