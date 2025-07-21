package com.diginamic.groupe1.transport.entity;

import com.diginamic.groupe1.transport.enums.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité représentant une réservation dans le système
 */
@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "departure_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:SS")
    private LocalDateTime departureTime;

    @Column(name = "arrival_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:SS")
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)

    private ReservationStatus status;

    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "fk_vehicle_id")
    private Vehicle vehicle;
}
