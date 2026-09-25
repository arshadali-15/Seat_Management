package com.spring.seat_management.seat_management.common.cache;

//import com.fasterxml.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectMapper;
import com.spring.seat_management.seat_management.dto.response.DeskAvailabilityRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisDeskAvailabilityCache implements DeskAvailabilityCache {

    private static final String PREFIX = "desk:availability:";

    private static final Duration TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;


    @Override
    public Optional<List<DeskAvailabilityRes>> get(LocalDate date) {
        try {
            String json = stringRedisTemplate.opsForValue()
                    .get(PREFIX + date);

            if (json == null) {
                log.info("Cache MISS for date: {}", date);
                return Optional.empty();
            }

            log.info("Cache HIT for date: {}", date);
            DeskAvailabilityCacheEntry entry = objectMapper
                    .readValue(json, DeskAvailabilityCacheEntry.class);
            return Optional.of(entry.getDesks());

        } catch (Exception e) {
            log.warn("Error reading desk availability cache for date: {}", date);
            return Optional.empty();
        }
    }

    @Override
    public void put(LocalDate date, List<DeskAvailabilityRes> desks) {
        try {
            DeskAvailabilityCacheEntry entry = DeskAvailabilityCacheEntry
                    .builder()
                    .desks(desks)
                    .build();

            stringRedisTemplate.opsForValue().set(
                    PREFIX + date,
                    objectMapper.writeValueAsString(entry),
                    TTL
            );
            log.info("Cache SET for date: {}", date);

        } catch (Exception e) {
            log.warn("Error writing desk availability cache for date: {}", date);
        }
    }

    @Override
    public void evict(LocalDate date) {
        stringRedisTemplate.delete(PREFIX + date);
        log.info("Cache EVICTED for date: {}", date);
    }

    // RedisDeskAvailabilityCache.java
    @Override
    public void evictAll() {
        // delete all keys matching the prefix
        Set<String> keys = stringRedisTemplate.keys(PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
            log.info("Cache EVICTED ALL desk availability entries");
        }
    }
}
