package com.spring.seat_management.seat_management.services;

import com.spring.seat_management.seat_management.common.enums.DeskType;
import com.spring.seat_management.seat_management.common.enums.Section;
import com.spring.seat_management.seat_management.dto.response.DeskAvailabilityProjection;
import com.spring.seat_management.seat_management.dto.response.DeskAvailabilityRes;
import com.spring.seat_management.seat_management.entities.Booking;
import com.spring.seat_management.seat_management.entities.Desk;
import com.spring.seat_management.seat_management.repo.BookingRepo;
import com.spring.seat_management.seat_management.repo.DeskRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeskService {

    private final DeskRepo deskRepo;

    public List<DeskAvailabilityRes> getAllDesks(LocalDate date) {
        return deskRepo
                .findDesksWithAvailability(date)
                .stream()
                .map(p -> new DeskAvailabilityRes(
                        p.getDeskId(),
                        p.getDeskNumber(),
                        p.getIsActive(),
                        p.getType() != null
                                ? DeskType.valueOf(p.getType())
                                : null,
                        p.getStatus(),
                        p.getBookedBy(),
                        p.getBookingFromDate(),
                        p.getBookingToDate()
                ))
                .toList();
    }

    public List<Desk> getAllDesksBySection(Section section) {
        return deskRepo.findBySection(section);
    }

}
