package com.spring.seat_management.seat_management.entities;

import com.spring.seat_management.seat_management.common.enums.DeskStatus;
import com.spring.seat_management.seat_management.common.enums.DeskType;
import com.spring.seat_management.seat_management.common.enums.Section;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Desk {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deskId;

    @Column(nullable = false, unique = true)
    private Integer deskNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Section section;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeskStatus status = DeskStatus.AVAILABLE;
}
