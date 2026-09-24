package com.spring.seat_management.seat_management.controller;

import com.spring.seat_management.seat_management.dto.request.UserLoginReq;
import com.spring.seat_management.seat_management.dto.request.UserSignupReq;
import com.spring.seat_management.seat_management.dto.response.UserLoginRes;
import com.spring.seat_management.seat_management.dto.response.UserSignupRes;
import com.spring.seat_management.seat_management.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/v1/")
public class UserController {

    private final UserService userService;

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
}
