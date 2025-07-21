package com.diginamic.groupe1.transport.dto.carpool;

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
public class CarpoolOrganizeUpsertDTO extends CarpoolDTO {

     @NotNull(groups = {ValidationGroups.OnCreate.class}, message = "available seats error")
     Integer initialAvailableSeats;

     @NotNull(groups = {ValidationGroups.OnCreate.class}, message = "vehicule organize error")
     Long vehicleId;

}
