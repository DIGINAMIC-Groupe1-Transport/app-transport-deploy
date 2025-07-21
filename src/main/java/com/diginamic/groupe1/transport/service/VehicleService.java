package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.dto.VehicleDTO;
import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.entity.Vehicle;
import com.diginamic.groupe1.transport.exception.BusinessException;
import com.diginamic.groupe1.transport.repository.VehicleRepository;
import com.diginamic.groupe1.transport.validation.VehicleBusinessValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
@AllArgsConstructor
public class VehicleService {

    private final UserInfoService userInfoService;

    private ModelMapper modelMapper;

    private VehicleRepository vehicleRepository;

    private VehicleBusinessValidator vehicleBusinessValidator;

    public Page<VehicleDTO> getPersonalVehicles(UserInfo userInfo, Pageable page) {
        Page<Vehicle> personalVehicles = vehicleRepository.findByUserInfoId(userInfo.getId(), page);

        return personalVehicles.map(vehicle ->
                modelMapper.map(vehicle, VehicleDTO.class)
                );
    }

    public VehicleDTO createPersonalVehicle(UserInfo userInfo, VehicleDTO vehicleDTO) {

        vehicleBusinessValidator.validateCreateVehicle(vehicleDTO);

        Vehicle newPersonalVehicle = modelMapper.map(vehicleDTO, Vehicle.class);
        newPersonalVehicle.setUserInfo(userInfo);
        newPersonalVehicle.setIsCompany(false);

        vehicleRepository.save(newPersonalVehicle);

        vehicleDTO.setId(newPersonalVehicle.getId());

        return vehicleDTO;
    }


    public void deletePersonalVehicle(Long vehicleId, UserInfo userInfo) {

        LocalDateTime instantDate = LocalDateTime.now();
        vehicleBusinessValidator.validateDeleteVehicle(vehicleId, userInfo, instantDate);
        vehicleRepository.deleteById(vehicleId);
    }

    public VehicleDTO getVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + vehicleId));
        return modelMapper.map(vehicle, VehicleDTO.class);
    }

    // CRUD véhicules de service
    public VehicleDTO createServiceVehicle(VehicleDTO vehicleDTO) {
        vehicleBusinessValidator.validateCreateVehicle(vehicleDTO);
        Vehicle newServiceVehicle = modelMapper.map(vehicleDTO, Vehicle.class);
        newServiceVehicle.setIsCompany(true);
        vehicleRepository.save(newServiceVehicle);
        vehicleDTO.setId(newServiceVehicle.getId());
        return vehicleDTO;
    }

    public Vehicle updateServiceVehicle(Long id,VehicleDTO vehicleDTO, UserInfo userInfo) {
        validateAdminRights(userInfo);
        vehicleBusinessValidator.validateUpdateVehicle(id,vehicleDTO);
        return updateVehicle(id,vehicleDTO);
    }

    private void validateAdminRights(UserInfo userInfo) {
        boolean isAdmin = userInfo.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        if (!isAdmin) {
            throw new BusinessException("Seul un administrateur peut modifier un véhicule de service");
        }
    }

    private Vehicle updateVehicle(Long id, VehicleDTO vehicleDTO) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Le véhicule n'existe pas"));

        // Mettre à jour les propriétés du véhicule existant
        modelMapper.map(vehicleDTO, existingVehicle);
        existingVehicle.setId(id); // Conserver l'ID original
        existingVehicle.setIsCompany(true);


        return vehicleRepository.save(existingVehicle);
    }


    public void deleteServiceVehicle(Long vehicleId, UserInfo userInfo  ) {
        LocalDateTime instantDate = LocalDateTime.now();
        vehicleBusinessValidator.validateDeleteVehicle(vehicleId, userInfo, instantDate);
        vehicleRepository.deleteById(vehicleId);
    }

    public Page<VehicleDTO> getAllServiceVehicles(Pageable page) {
        Page<Vehicle> serviceVehicles = vehicleRepository.findByIsCompanyTrue(page);
        return serviceVehicles.map(vehicle -> modelMapper.map(vehicle, VehicleDTO.class));
    }

    public VehicleDTO getServiceVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .filter(v -> v.getIsCompany() != null && v.getIsCompany())
                .orElseThrow(() -> new IllegalArgumentException("Service vehicle not found with id: " + vehicleId));
        return modelMapper.map(vehicle, VehicleDTO.class);
    }

}