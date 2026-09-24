package com.spring.seat_management.seat_management.repo;


import com.spring.seat_management.seat_management.entities.User;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(@Valid String email);

    boolean existsByEmail(@Valid String email);

    boolean existsBySlsId(@Valid String slsId);
}
