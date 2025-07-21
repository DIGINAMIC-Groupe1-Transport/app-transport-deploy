package com.diginamic.groupe1.transport.controller;


import com.diginamic.groupe1.transport.dto.VehicleDTO;
import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.security.CustomUserDetails;
import com.diginamic.groupe1.transport.service.VehicleService;
import com.diginamic.groupe1.transport.utils.ResponseApi;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/vehicles")
@AllArgsConstructor
public class VehicleController {

    private VehicleService vehicleService;

    @GetMapping("/personal")
    public ResponseEntity<ResponseApi<Page<VehicleDTO>>> getPersonalVehicles
            (@AuthenticationPrincipal CustomUserDetails userDetails, Pageable page) {
        //crée et récupére le DTO du véhicule
        Page<VehicleDTO> vehiclesDTO = vehicleService.getPersonalVehicles(userDetails.getUserInfo(), page);
        //retourne ok avec le DTO du véhicule
        return ResponseEntity.ok(ResponseApi.success(vehiclesDTO, "Véhicules personnel récupérés"));
    }

    @PostMapping("/personal/create")
    public ResponseEntity<ResponseApi<VehicleDTO>> createPersonalVehicle
            (@AuthenticationPrincipal CustomUserDetails userDetails,
             @Valid @RequestBody VehicleDTO vehicleDTO
            ) {
        //crée et récupére le DTO du véhicule
        VehicleDTO createdVehicle = vehicleService.createPersonalVehicle(userDetails.getUserInfo(), vehicleDTO);
        //retourne ok avec le DTO du véhicule
        return ResponseEntity.ok(ResponseApi.success(createdVehicle, "Véhicule personnel créé avec succés"));
    }

    @DeleteMapping("/personal/delete/{personalVehicleId}")
    public ResponseEntity<ResponseApi<Void>> deletePersonalVehicle
            (@AuthenticationPrincipal CustomUserDetails userDetails,
             @PathVariable Long personalVehicleId) {

        vehicleService.deletePersonalVehicle(personalVehicleId, userDetails.getUserInfo());

        return ResponseEntity.ok(ResponseApi.success(null, "Véhicule supprimé avec succès"));
    }

    // --- CRUD véhicules de service ---
    @PostMapping("/service")
    public ResponseEntity<ResponseApi<VehicleDTO>> createServiceVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        VehicleDTO createdVehicle = vehicleService.createServiceVehicle(vehicleDTO);
        return ResponseEntity.ok(ResponseApi.success(createdVehicle, "Véhicule de service créé avec succès"));
    }

    @DeleteMapping("/service/delete/{serviceVehicleId}")
    public ResponseEntity<ResponseApi<Void>> deleteServiceVehicle(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable Long serviceVehicleId) {
        vehicleService.deleteServiceVehicle(serviceVehicleId, userDetails.getUserInfo());
        return ResponseEntity.ok(ResponseApi.success(null, "Véhicule de service supprimé avec succès"));
    }
    @PutMapping("/service/update/{id}")
    public ResponseEntity<ResponseApi<VehicleDTO>> updateServiceVehicle(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable Long id, @Valid @RequestBody VehicleDTO vehicleDTO) {
        vehicleService.updateServiceVehicle(id,vehicleDTO,userDetails.getUserInfo());
        return ResponseEntity.ok(ResponseApi.success(null,"vehicule de service mise à jour avec sucée"));


    }

    @GetMapping("/service/all")
    public ResponseEntity<ResponseApi<Page<VehicleDTO>>> getAllServiceVehicles(Pageable page) {
        Page<VehicleDTO> vehiclesDTO = vehicleService.getAllServiceVehicles(page);
        return ResponseEntity.ok(ResponseApi.success(vehiclesDTO, "Véhicules de service récupérés"));
    }

    @GetMapping("/service/{serviceVehicleId}")
    public ResponseEntity<ResponseApi<VehicleDTO>> getServiceVehicleById(@PathVariable Long serviceVehicleId) {
        VehicleDTO vehicleDTO = vehicleService.getServiceVehicleById(serviceVehicleId);
        return ResponseEntity.ok(ResponseApi.success(vehicleDTO, "Véhicule de service récupéré"));
    }

}
