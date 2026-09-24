package com.spring.seat_management.seat_management.common.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Component
@Getter
@Setter
@NoArgsConstructor
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserContext {

    private UUID userId;
    private String keyId;
    private String email;
    private String role;
}
