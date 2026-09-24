package com.spring.seat_management.seat_management.common.cache;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyCacheEntry(

        String keyId,
        String keySecretHash,
        String previousKeySecretHash,
        LocalDateTime gracePeriodExpiresAt,
        UUID userId,
        boolean enabled

) {

    public boolean isInGracePeriod() {
        return gracePeriodExpiresAt != null && LocalDateTime.now().isBefore(gracePeriodExpiresAt);
    }
}
