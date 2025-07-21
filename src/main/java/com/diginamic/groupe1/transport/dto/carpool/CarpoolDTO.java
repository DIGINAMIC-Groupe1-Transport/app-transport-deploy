package com.diginamic.groupe1.transport.dto.carpool;

import com.diginamic.groupe1.transport.dto.CoordinatesDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarpoolDTO {


    private Long id;

    private LocalDateTime creationTime;

    private LocalDateTime estimatedArrivalTime;

    @NotNull(message = "departure time error")
    private LocalDateTime estimatedDepartureTime;

    private Long estimatedDuration;

    private Integer remainingSeats;

    private Integer occupiedSeats;

    private Long estimatedLength;

    @NotNull(message = "coordinates start null error")
    @Valid
    private CoordinatesDTO startCoordinates;

    @NotNull(message = "coordinates end null error")
    @Valid
    private CoordinatesDTO endCoordinates;

    private boolean isCanceled;


}
