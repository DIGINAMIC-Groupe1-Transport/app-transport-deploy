package com.diginamic.groupe1.transport.dto;

import com.diginamic.groupe1.transport.enums.VehicleCategory;
import com.diginamic.groupe1.transport.enums.VehicleMotor;
import com.diginamic.groupe1.transport.enums.VehicleStatus;
import com.diginamic.groupe1.transport.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VehicleDTO {

    private Long id;

    @NotNull(message = "registration error")
    private String registration;

    private String brand;

    @NotNull(message = "model error")
    private String model;

    @NotNull(message = "seats error")
    private Integer seats;

    private Boolean isCompany;

    private Integer co2PerKm;

    private VehicleStatus vehicleStatus;

    private VehicleCategory vehicleCategory;

    private VehicleMotor vehicleMotor;
}
