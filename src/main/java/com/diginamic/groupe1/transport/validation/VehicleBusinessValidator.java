package com.diginamic.groupe1.transport.validation;

import com.diginamic.groupe1.transport.dto.VehicleDTO;
import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.exception.BusinessException;
import com.diginamic.groupe1.transport.repository.CarpoolRepository;
import com.diginamic.groupe1.transport.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;

@AllArgsConstructor
@Transactional
@Component
public class VehicleBusinessValidator {

    VehicleRepository vehicleRepository;

    CarpoolRepository carpoolRepository;

    public void validateCreateVehicle(VehicleDTO vehicleDTO) {
        if(vehicleRepository.existsByRegistration(vehicleDTO.getRegistration())){
            throw new BusinessException("Plaque d'imatriculation déjà utilisée");
        }
    }
    
    

    public void validateDeleteVehicle(Long vehicleId, UserInfo userInfo, LocalDateTime instantTime) {
        // Vérifier si l'utilisateur est admin
        boolean isAdmin = userInfo.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));
        // Log plus sûr pour le débogage
        userInfo.getRoles().stream().findFirst().ifPresent(role ->
                System.out.println("Premier rôle de l'utilisateur: " + role.getName())
        );


        // Si l'utilisateur n'est pas admin, on vérifie s'il est propriétaire
        if (!isAdmin && !vehicleRepository.existsByIdAndUserInfoId(vehicleId, userInfo.getId())) {
            throw new BusinessException("Le véhicule à supprimer ne vous appartient pas, ou n'existe pas");
        }

        // Dans tous les cas, vérifier si le véhicule est utilisé dans un covoiturage actif
        if (carpoolRepository.existsActiveCarpoolsWithParticipantsUsingVehicle(vehicleId, instantTime)) {
            throw new BusinessException("Véhicule déjà utilisé dans un covoit. Impossible de supprimer maintenant");
        }
    }

    public void validateCreatePersonalVehicle(VehicleDTO vehicleDTO, UserInfo userInfo) {

        System.out.println("ici info user: " + userInfo.getId().toString());
        if (userInfo == null || userInfo.getId() == null) {
            throw new BusinessException("Un véhicule personnel doit obligatoirement avoir un propriétaire.");
        }
        if(vehicleRepository.existsByRegistration(vehicleDTO.getRegistration())){
            throw new BusinessException("Plaque d'immatriculation déjà utilisée");
        }
    }

    public void validateUpdateVehicle( Long id ,VehicleDTO vehicleDTO) {
        if (!vehicleRepository.existsById(id)) {
            throw new BusinessException("Le véhicule à mettre à jour n'existe pas");
        }

    }
}
