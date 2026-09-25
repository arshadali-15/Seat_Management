package com.spring.seat_management.seat_management.common.cache;

import com.spring.seat_management.seat_management.dto.response.DeskAvailabilityRes;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeskAvailabilityCacheEntry {
    private List<DeskAvailabilityRes> desks;
}