package com.spring.seat_management.seat_management.services;

import com.spring.seat_management.seat_management.common.config.security.JwtUtil;
import com.spring.seat_management.seat_management.common.config.security.UserContext;
import com.spring.seat_management.seat_management.common.enums.BookingStatus;
import com.spring.seat_management.seat_management.common.enums.Role;
import com.spring.seat_management.seat_management.common.exceptions.BadRequestException;
import com.spring.seat_management.seat_management.common.exceptions.DuplicateResourceException;
import com.spring.seat_management.seat_management.common.exceptions.ResourceNotFoundException;
import com.spring.seat_management.seat_management.dto.request.ResetPasswordReq;
import com.spring.seat_management.seat_management.dto.request.UserLoginReq;
import com.spring.seat_management.seat_management.dto.request.UserSignupReq;
import com.spring.seat_management.seat_management.dto.response.AdminUserRes;
import com.spring.seat_management.seat_management.dto.response.UserLoginRes;
import com.spring.seat_management.seat_management.dto.response.UserProfileRes;
import com.spring.seat_management.seat_management.dto.response.UserSignupRes;
import com.spring.seat_management.seat_management.entities.User;
import com.spring.seat_management.seat_management.mapper.UserMapper;
import com.spring.seat_management.seat_management.repo.BookingRepo;
import com.spring.seat_management.seat_management.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final BookingRepo bookingRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserContext userContext;

    public UserLoginRes login(UserLoginReq request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new BadRequestException(
                    "INVALID_CREDENTIALS",
                    "Invalid email or password"
            );
        }

        User user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException(
                        "INVALID_CREDENTIALS",
                        "Invalid email or password"
                ));

        String token = jwtUtil.generateAccessToken(
                user.getEmail(), user.getUserId(), user.getRole().name(), user.getName());

        return new UserLoginRes(token, new UserProfileRes(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        ));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserSignupRes addUser(UserSignupReq request) {
        if (userRepo.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "EMAIL_EXISTS",
                    "Email already exists with email: " + request.email()
            );
        }

        if (userRepo.existsBySlsId(request.SLSID())) {
            throw new DuplicateResourceException(
                    "SLSID_EXISTS",
                    "SLS ID already exists: " + request.SLSID()
            );
        }

        User appUser = User.builder()
                .name(request.name())
                .email(request.email())
                .slsId(request.SLSID())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.EMPLOYEE)
                .build();

        userRepo.save(appUser);

        return userMapper.toSignUpResponse(appUser);
    }

    @Transactional
    public void resetPassword(ResetPasswordReq request) {

        User user = userRepo.findById(userContext.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "USER_NOT_FOUND",
                                userContext.getUserId().toString()
                        ));

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPasswordHash())) {

            throw new BadRequestException(
                    "INVALID_CURRENT_PASSWORD",
                    "Current password is incorrect"
            );
        }

        if (passwordEncoder.matches(
                request.newPassword(),
                user.getPasswordHash())) {

            throw new BadRequestException(
                    "SAME_PASSWORD",
                    "New password must be different from current password"
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(request.newPassword())
        );

        userRepo.save(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminUserRes> getAllUsersForAdmin() {

        List<User> users = userRepo.findAll();

        return users.stream()
                .map(user -> {

                    List<AdminUserRes.AdminBookingRes> bookings =
                            bookingRepo
                                    .findByUser_UserIdAndStatusOrderByBookingDateAsc(
                                            user.getUserId(),
                                            BookingStatus.BOOKED
                                    )
                                    .stream()
                                    .map(booking ->
                                            new AdminUserRes.AdminBookingRes(
                                                    booking.getBookingId(),
                                                    booking.getDesk().getDeskNumber(),
                                                    booking.getBookingDate(),
                                                    booking.getStatus()
                                            )
                                    )
                                    .toList();

                    return new AdminUserRes(
                            user.getUserId(),
                            user.getName(),
                            user.getEmail(),
                            user.getSlsId(),
                            user.getRole().name(),
                            bookings
                    );
                })
                .toList();
    }
}
