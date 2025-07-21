package com.diginamic.groupe1.transport.validation;
import com.diginamic.groupe1.transport.dto.carpool.CarpoolOrganizeUpsertDTO;
import com.diginamic.groupe1.transport.entity.Carpool;
import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.exception.BusinessException;
import com.diginamic.groupe1.transport.repository.CarpoolRepository;
import com.diginamic.groupe1.transport.utils.CarpoolUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Transactional
@Component
public class CarpoolBusinessValidator {

    CarpoolRepository carpoolRepository;

    CarpoolUtils carpoolUtils;

    public void validateUpsertCarpool (UserInfo userInfo, CarpoolOrganizeUpsertDTO carpoolOrganizeUpsertDTO){

        if (carpoolOrganizeUpsertDTO.getEndCoordinates().getLabel().equals(carpoolOrganizeUpsertDTO.getStartCoordinates().getLabel())) {
            throw new BusinessException("Impossible de créer un covoiturage avec les mêmes coordonnées de départ et d'arrivée");
        }


        if (carpoolRepository.existsOverlapingCarpool(userInfo.getId(), carpoolOrganizeUpsertDTO.getEstimatedDepartureTime(), carpoolOrganizeUpsertDTO.getEstimatedArrivalTime())) {
            throw new BusinessException("Impossible de créer un covoiturage en même temps qu'un autre covoiturage est actif");
        }
    }

    public void validateDeleteOrganizeCarpool (UserInfo userInfo, Carpool existingCarpool){

        if(!carpoolRepository.existsByIdAndOrganizerId(existingCarpool.getId(), userInfo.getId())){
            throw new BusinessException("Impossible de supprimer un covoiturage non organisé par l'utilisateur");
        }
    }

    public void validateParticipateCarpool(UserInfo userInfo, Carpool existingCarpool) {

        existingCarpool.getParticipants().size();

        if(existingCarpool.getParticipants().contains(userInfo)) {
            throw new BusinessException("Impossible de participer deux fois au même covoiturage");
        }

        if(carpoolUtils.calculateAvailableSeats(existingCarpool) < 1) {
            throw new BusinessException("Pas assez de places dans le covoiturage");
        }

        if(existingCarpool.getOrganizer().getId().equals(userInfo.getId())){
            throw new BusinessException("Impossible de participer au covoiturage organisé");
        }
    }

}
