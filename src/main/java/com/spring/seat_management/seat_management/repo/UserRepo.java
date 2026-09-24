package com.spring.seat_management.seat_management.repo;


import com.spring.seat_management.seat_management.dto.response.UserLoginRes;
import com.spring.seat_management.seat_management.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findBySlsId(String slsId);

    boolean existsByEmail(String email);

    boolean existsBySlsId(String slsId);
}
