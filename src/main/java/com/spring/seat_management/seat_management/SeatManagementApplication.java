package com.spring.seat_management.seat_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SeatManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatManagementApplication.class, args);
    }

}
