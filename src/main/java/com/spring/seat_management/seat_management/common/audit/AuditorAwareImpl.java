package com.spring.seat_management.seat_management.common.audit;

import com.spring.seat_management.seat_management.common.config.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAwareImpl")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final UserContext userContext;

    @Override
    public Optional<String> getCurrentAuditor() {

        try {
            String keyId = userContext.getKeyId();
            if (keyId != null && !keyId.isBlank()) return Optional.of(keyId);

            if (userContext.getUserId() != null) {
                return Optional.of("user_id: " + userContext.getUserId());
            }
        } catch (Exception ignored) {
        }
        return Optional.of("SYSTEM");
    }
}
