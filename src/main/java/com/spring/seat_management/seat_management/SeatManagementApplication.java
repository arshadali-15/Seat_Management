package com.spring.seat_management.seat_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class SeatManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatManagementApplication.class, args);
    }

}
