package com.spring.seat_management.seat_management.common.cache;

//import com.fasterxml.jackson.databind.ObjectMapper;

import com.spring.seat_management.seat_management.common.enums.Section;
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

    private String key(Section section, LocalDate date) {
        return PREFIX + section + ":" + date;
    }

    @Override
    public Optional<List<DeskAvailabilityRes>> get(Section section, LocalDate date) {

        try {
            String json = stringRedisTemplate
                    .opsForValue()
                    .get(key(section, date));
            if (json == null) {
                log.info(
                        "Cache MISS for section: {}, date: {}",
                        section,
                        date
                );
                return Optional.empty();
            }
            log.info(
                    "Cache HIT for section: {}, date: {}",
                    section,
                    date
            );
            DeskAvailabilityCacheEntry entry =
                    objectMapper.readValue(
                            json,
                            DeskAvailabilityCacheEntry.class
                    );
            return Optional.of(entry.getDesks());

        } catch (Exception e) {
            log.warn(
                    "Error reading cache for section: {}, date: {}",
                    section,
                    date,
                    e
            );
            return Optional.empty();
        }
    }

    @Override
    public void put(Section section, LocalDate date, List<DeskAvailabilityRes> desks) {

        try {
            DeskAvailabilityCacheEntry entry =
                    DeskAvailabilityCacheEntry.builder()
                            .desks(desks)
                            .build();

            stringRedisTemplate.opsForValue().set(
                    key(section, date),
                    objectMapper.writeValueAsString(entry),
                    TTL
            );

            log.info(
                    "Cache SET for section: {}, date: {}",
                    section,
                    date
            );

        } catch (Exception e) {
            log.warn(
                    "Error writing cache for section: {}, date: {}",
                    section,
                    date,
                    e
            );
        }
    }

    @Override
    public void evict(Section section, LocalDate date) {
        try {
            String redisKey = key(section, date);

            stringRedisTemplate.delete(redisKey);

            log.info(
                    "Cache EVICTED for section: {}, date: {}",
                    section,
                    date
            );

        } catch (Exception e) {
            log.warn(
                    "Error evicting cache for section: {}, date: {}",
                    section,
                    date,
                    e
            );
        }
    }

    // RedisDeskAvailabilityCache.java
    @Override
    public void evictAll() {
        try {
            Set<String> keys =
                    stringRedisTemplate.keys(PREFIX + "*");

            if (keys != null && !keys.isEmpty()) {
                stringRedisTemplate.delete(keys);

                log.info(
                        "Cache EVICTED ALL desk availability entries: {} keys",
                        keys.size()
                );
            } else {
                log.info("No desk availability cache entries found");
            }

        } catch (Exception e) {
            log.warn(
                    "Error evicting all desk availability cache entries",
                    e
            );
        }
    }
}
