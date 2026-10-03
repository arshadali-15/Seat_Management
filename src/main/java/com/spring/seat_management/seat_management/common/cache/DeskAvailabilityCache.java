package com.spring.seat_management.seat_management.common.cache;

import com.spring.seat_management.seat_management.common.enums.Section;
import com.spring.seat_management.seat_management.dto.response.DeskAvailabilityRes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DeskAvailabilityCache {
    Optional<List<DeskAvailabilityRes>> get(
            Section section,
            LocalDate date
    );

    void put(
            Section section,
            LocalDate date,
            List<DeskAvailabilityRes> desks
    );

    void evict(
            Section section,
            LocalDate date
    );

    void evictAll();
}
