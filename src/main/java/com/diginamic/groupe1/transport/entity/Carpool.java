package com.diginamic.groupe1.transport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entité représentant un covoiturage dans le système
 */
@Entity
@Table(name = "carpools")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Carpool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="creation_time")
    private LocalDateTime creationTime;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "estimated_departure_time")
    private LocalDateTime estimatedDepartureTime;

    @Column(name = "estimated_arrival_time")
    private LocalDateTime estimatedArrivalTime;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration;

    @Column(name = "estimated_length")
    private Integer estimatedLength;

    @Column(name = "initial_available_seats")
    private Integer initialAvailableSeats;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "fk_coordinates_start_id")
    private Coordinates startCoordinates;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "fk_coordinates_end_id")
    private Coordinates endCoordinates;

    @ManyToOne
    @JoinColumn(name = "fk_user_organizer_id")
    private UserInfo organizer;

    @ManyToMany(mappedBy = "participatedCarpools")
    private Set<UserInfo> participants = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="fk_vehicle_id")
    private Vehicle vehicle;

    @Column(name= "is_canceled")
    private boolean isCanceled;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Carpool carpool) {
            return carpool.getCreationTime().equals(getCreationTime()) && carpool.getDepartureDate().equals(getDepartureDate());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(creationTime, departureDate);
    }

}
