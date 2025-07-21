package com.diginamic.groupe1.transport.dto.carpool;

import com.diginamic.groupe1.transport.dto.CoordinatesDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarpoolSearchResponseListDTO {

    private Long id;

    private LocalDateTime creationTime;

    private LocalDateTime estimatedDepartureTime;

    private LocalDateTime estimatedArrivalTime;

    private Integer estimatedLength;

    private Integer estimatedDuration;

    private Integer remainingSeats;

    private Boolean isCanceled;

    private CoordinatesDTO startCoordinates;

    private CoordinatesDTO endCoordinates;

    private String model;

    private Double toStartDistance;

    private Double toEndDistance;

    private Double weightedDistance;
}
