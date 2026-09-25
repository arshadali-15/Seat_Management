package com.spring.seat_management.seat_management.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "desk_releases",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_desk_release_date",
                        columnNames = {"desk_id", "release_date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeskRelease extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID releaseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "desk_id", nullable = false)
    private Desk desk;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(length = 255)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "released_by", nullable = false)

    private String releasedBy;

    @CreationTimestamp
    private LocalDateTime releasedAt;
}