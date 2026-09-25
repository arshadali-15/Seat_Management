package com.spring.seat_management.seat_management.common.cache;

import com.spring.seat_management.seat_management.dto.response.DeskAvailabilityRes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DeskAvailabilityCache {
    Optional<List<DeskAvailabilityRes>> get(LocalDate date);

    void put(LocalDate date, List<DeskAvailabilityRes> desks);

    void evict(LocalDate date);

    void evictAll();
}
