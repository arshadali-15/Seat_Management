package com.spring.seat_management.seat_management.services;

import com.spring.seat_management.seat_management.common.config.security.JwtUtil;
import com.spring.seat_management.seat_management.common.enums.Role;
import com.spring.seat_management.seat_management.common.exceptions.DuplicateResourceException;
import com.spring.seat_management.seat_management.common.exceptions.ResourceNotFoundException;
import com.spring.seat_management.seat_management.dto.request.UserLoginReq;
import com.spring.seat_management.seat_management.dto.request.UserSignupReq;
import com.spring.seat_management.seat_management.dto.response.UserLoginRes;
import com.spring.seat_management.seat_management.dto.response.UserSignupRes;
import com.spring.seat_management.seat_management.entities.User;
import com.spring.seat_management.seat_management.mapper.UserMapper;
import com.spring.seat_management.seat_management.repo.UserRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserLoginRes login(UserLoginReq request) {

        log.info(passwordEncoder.encode(request.password()));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.email()));

        String token = jwtUtil.generateAccessToken(
                user.getEmail(), user.getUserId(), user.getRole().name());

        return new UserLoginRes(token);
    }

    @Transactional
    public UserSignupRes addUser(UserSignupReq request) {
        if (userRepo.existsByEmail(request.email())) {
            throw new DuplicateResourceException("EMAIL_EXISTS", "Email already exists with email: " + request.email());
        }

        User appUser = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.EMPLOYEE)
                .build();

        userRepo.save(appUser);

        return userMapper.toSignUpResponse(appUser);
    }
}
