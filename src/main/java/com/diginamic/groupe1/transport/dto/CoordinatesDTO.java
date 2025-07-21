package com.diginamic.groupe1.transport.dto;

import com.diginamic.groupe1.transport.validation.ValidationGroups;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CoordinatesDTO {

    private Long id;

    @NotNull(message = "label error")
    private String label;

    @NotNull(message = "street error")
    private String street;

    @NotNull(message = "house number error")
    private String houseNumber;

    @NotNull(message = "city error")
    private String city;

    @NotNull(message = "x coo error")
    private Double x;

    @NotNull(message = "y coo error")
    private Double y;

}
