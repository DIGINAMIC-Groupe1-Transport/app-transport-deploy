package com.diginamic.groupe1.transport.mapper;

import com.diginamic.groupe1.transport.dto.ReservationDTO;
import com.diginamic.groupe1.transport.entity.Reservation;
import org.mapstruct.*;

/**
 * Mapper pour convertir entre les objets Reservation et ReservationDTO.
 * Utilise MapStruct pour automatiser les mappings.
 */
@Named("ReservationMapper")
@Mapper(componentModel = "spring")
public interface ReservationMapper {

    /**
     * Convertit une entité Reservation en DTO
     *
     * @param reservation l'entité à convertir
     * @return le DTO correspondant
     */
    @Mapping(target = "userId", source = "userInfo.id")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "dateDebut", source = "departureTime",dateFormat = "yyyy-MM-dd'T'HH:mm:SS")
    @Mapping(target = "dateFin", source = "arrivalTime",dateFormat = "yyyy-MM-dd'T'HH:mm:SS")
    @Mapping(target = "status", source = "status")
    ReservationDTO toDTO(Reservation reservation);

    /**
     * Convertit un DTO en entité Reservation
     *
     * @param dto le DTO à convertir
     * @return l'entité correspondante
     */
    @Mapping(target = "userInfo", ignore = true)
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "departureTime", source = "dateDebut", dateFormat = "yyyy-MM-dd'T'HH:mm:SS")
    @Mapping(target = "arrivalTime", source = "dateFin",dateFormat = "yyyy-MM-dd'T'HH:mm:SS")
    @Mapping(target = "status", source = "status")
    Reservation toEntity(ReservationDTO dto);
}