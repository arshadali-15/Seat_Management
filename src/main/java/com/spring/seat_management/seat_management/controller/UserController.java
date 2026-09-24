package com.spring.seat_management.seat_management.controller;

import com.spring.seat_management.seat_management.common.exceptions.ResourceNotFoundException;
import com.spring.seat_management.seat_management.dto.request.ResetPasswordReq;
import com.spring.seat_management.seat_management.dto.request.UserLoginReq;
import com.spring.seat_management.seat_management.dto.request.UserSignupReq;
import com.spring.seat_management.seat_management.dto.response.UserLoginRes;
import com.spring.seat_management.seat_management.dto.response.UserSignupRes;
import com.spring.seat_management.seat_management.entities.User;
import com.spring.seat_management.seat_management.repo.UserRepo;
import com.spring.seat_management.seat_management.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    @PostMapping("/auth/login")
    public ResponseEntity<UserLoginRes> login(@RequestBody @Valid UserLoginReq request) {
        UserLoginRes response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addUser")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<UserSignupRes> addUser(@RequestBody @Valid UserSignupReq request) {
        UserSignupRes response = userService.addUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/resetPassword")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void resetAdminPassword(
            @RequestBody ResetPasswordReq request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow();

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        userRepo.save(user);
    }


}
