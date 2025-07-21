package com.diginamic.groupe1.transport.dto.carpool;

import com.diginamic.groupe1.transport.dto.CoordinatesDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CarpoolSearchRequestDTO {

    private LocalDateTime estimatedDepartureTime;

    private CoordinatesDTO startCoordinates;

    private CoordinatesDTO endCoordinates;

    private Double startWeight;

    private Double endWeight;

}
