package com.diginamic.groupe1.transport.dto.carpool;

import com.diginamic.groupe1.transport.dto.UserInfoDTO;
import com.diginamic.groupe1.transport.dto.VehicleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarpoolDetailsDTO extends CarpoolDTO {

    private Set<UserInfoDTO> participants;

    private UserInfoDTO organizer;

    VehicleDTO vehicle;

}
