package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.dto.*;
import com.diginamic.groupe1.transport.dto.carpool.*;
import com.diginamic.groupe1.transport.entity.Carpool;
import com.diginamic.groupe1.transport.entity.Coordinates;
import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.entity.Vehicle;
import com.diginamic.groupe1.transport.exception.BusinessException;
import com.diginamic.groupe1.transport.exception.GlobalExceptionHandler;
import com.diginamic.groupe1.transport.exception.ResourceNotFoundException;
import com.diginamic.groupe1.transport.repository.CarpoolRepository;
import com.diginamic.groupe1.transport.repository.UserInfoRepository;
import com.diginamic.groupe1.transport.repository.VehicleRepository;
import com.diginamic.groupe1.transport.security.CustomUserDetails;
import com.diginamic.groupe1.transport.utils.CarpoolUtils;
import com.diginamic.groupe1.transport.validation.CarpoolBusinessValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class CarpoolService {

    private ModelMapper modelMapper;

    private CarpoolRepository carpoolRepository;

    private VehicleRepository vehicleRepository;

    private UserInfoRepository userInfoRepository;

    private CoordinatesService coordinatesService;

    private MessageService messageService;

    private CarpoolBusinessValidator carpoolBusinessValidator;

    private final RouteCalculatorService routeCalculatorService;

    private CarpoolUtils carpoolUtils;

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**Retourne les covoiturages correspondant aux critères de recherche de l'utilisateur
     *
     * @param startX coordonnées gps de départ X
     * @param startY coordonnées gps de départ X
     * @param endX coordonnées gps de départ X
     * @param endY coordonnées gps de départ X
     * @param departureDate date de départ
     * @param startWeight future feature
     * @param endWeight future feature
     * @param pageable pagination
     * @return page de covoiturages correspondant aux critères de recherche
     */
    public Page<CarpoolSearchResponseListDTO> findAllCarpools(Double startX,
                                                              Double startY,
                                                              Double endX,
                                                              Double endY,
                                                              LocalDate departureDate,
                                                              Double startWeight,
                                                              Double endWeight,
                                                              Pageable pageable) {

        List<Object[]> matchingCarpools = carpoolRepository.findMatchingCarpools(
                startX,
                startY,
                endX,
                endY,
                (departureDate == null) ? LocalDate.now() : departureDate,
                LocalDateTime.now(),
                (startWeight == null) ? 1.0 : startWeight,
                (endWeight == null) ? 1.0 : endWeight,
                pageable.getPageSize(),
                (int) pageable.getOffset()
        );

        long total = carpoolRepository.countMatchingCarpools(startX,
                startY,
                endX,
                endY,
                (departureDate == null) ? LocalDate.now() : departureDate,
                LocalDateTime.now());


        List<CarpoolSearchResponseListDTO> carpoolSearchResponseDTO = matchingCarpools.stream().map(row -> {
            CarpoolSearchResponseListDTO dto = new CarpoolSearchResponseListDTO();

            dto.setId(((Number) row[0]).longValue());
            dto.setCreationTime(((Timestamp) row[1]).toLocalDateTime());
            dto.setEstimatedDepartureTime(((Timestamp) row[3]).toLocalDateTime());
            dto.setEstimatedArrivalTime(((Timestamp) row[4]).toLocalDateTime());
            dto.setEstimatedDuration((Integer) row[5]);
            dto.setEstimatedLength((Integer) row[6]);
            dto.setRemainingSeats(((Number) row[7]).intValue());
            dto.setIsCanceled((Boolean) row[8]);

            CoordinatesDTO startCoordinates = new CoordinatesDTO();
            startCoordinates.setLabel((String) row[9]);
            startCoordinates.setCity((String) row[10]);
            startCoordinates.setStreet((String) row[11]);
            startCoordinates.setHouseNumber((String) row[12]);
            startCoordinates.setX(((Number) row[13]).doubleValue());
            startCoordinates.setY(((Number) row[14]).doubleValue());
            dto.setStartCoordinates(startCoordinates);

            CoordinatesDTO endCoordinates = new CoordinatesDTO();
            endCoordinates.setLabel((String) row[15]);
            endCoordinates.setCity((String) row[16]);
            endCoordinates.setStreet((String) row[17]);
            endCoordinates.setHouseNumber((String) row[18]);
            endCoordinates.setX(((Number) row[19]).doubleValue());
            endCoordinates.setY(((Number) row[20]).doubleValue());
            dto.setEndCoordinates(endCoordinates);

            dto.setModel((String) row[23]);
            dto.setToStartDistance(((Number) row[24]).doubleValue());
            dto.setToEndDistance(((Number) row[25]).doubleValue());
            dto.setWeightedDistance(((Number) row[26]).doubleValue());

            return dto;
        }).toList();

        return new PageImpl<>(carpoolSearchResponseDTO, pageable, total);

    }

    /**
     * Retourne les détails du covoiturage dont l'id est renseigné
     *
     * @param carpoolId id du covoiturage dont les détails sont à récupérer
     * @return détqils du covoiturage
     */
    public CarpoolDetailsDTO findCarpoolDetails(Long carpoolId) {
        Carpool existingCarpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new ResourceNotFoundException("Détails du covoiturage non trouvé")
        );

        return modelMapper.map(existingCarpool, CarpoolDetailsDTO.class);
    }

    /**
     * Retourne tous les covoiturages organisés par l'utilisateur
     *
     * @param userInfo utilisateur de la session, dont l'id sera utilisé
     * @param pageable paramétres de la page retourné
     * @return page de covoiturages organisés par l'utilisateur
     */
    public Page<CarpoolDTO> findAllOrganizedCarpools(UserInfo userInfo, Pageable pageable) {

        Page<Carpool> organizedCarpools = carpoolRepository.findByOrganizerId(userInfo.getId(), pageable);

        return organizedCarpools.map(carpool -> {
                    CarpoolDTO organizedCarpoolDTO = modelMapper.map(carpool, CarpoolDTO.class);
                    organizedCarpoolDTO.setOccupiedSeats(carpoolUtils.calculateRemainingSeats(carpool));
                    return organizedCarpoolDTO;
                }
        );
    }

    /**
     * Retourne tous les covoiturages auxquels va participer/a participé l'utilisateur
     *
     * @param userDetails utilisateur de la session, dont l'id sera utilisé
     * @param pageable    paramétres de la page retourné
     * @return page de covoiturages auxquels va participer/a participé l'utilisateur
     */
    public Page<CarpoolDTO> findAllParticipatedCarpools(CustomUserDetails userDetails, Pageable pageable) {

        Page<Carpool> particpatedCarpools = carpoolRepository.findByParticipantsId(userDetails.getUserInfo().getId(), pageable);

        return particpatedCarpools.map(carpool -> {
                    CarpoolDTO participatedCarpoolDTO = modelMapper.map(carpool, CarpoolDTO.class);
                    participatedCarpoolDTO.setRemainingSeats(carpoolUtils.calculateAvailableSeats(carpool));
                    return participatedCarpoolDTO;
                }
        );
    }

    /**Crée et retourne un covoiturage
     * @param userInfo détails de l'utilisateur
     * @param carpoolOrganizeUpsertDTO informations du covoiturage à créer
     * @return covoiturage organisé nouvellement créé
     */
    public CarpoolDTO createOrganizeCarpool(UserInfo userInfo, CarpoolOrganizeUpsertDTO carpoolOrganizeUpsertDTO) {


        Vehicle userVehicle = vehicleRepository.findByIdAndUserInfoId(
                carpoolOrganizeUpsertDTO.getVehicleId(),
                userInfo.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Can't create a carpool without a car"));

        Coordinates startCoordinates = coordinatesService.findOrCreateByLabel(carpoolOrganizeUpsertDTO.getStartCoordinates());
        Coordinates endCoordinates = coordinatesService.findOrCreateByLabel(carpoolOrganizeUpsertDTO.getEndCoordinates());

        Carpool newCarpool = modelMapper.map(carpoolOrganizeUpsertDTO, Carpool.class);

        RouteCalculatorService.RouteInfo routeInfo = routeCalculatorService.calculateRoute(startCoordinates, endCoordinates);

        newCarpool.setEstimatedLength(routeInfo.distanceMeters);
        newCarpool.setEstimatedDuration(routeInfo.durationSeconds);

        newCarpool.setStartCoordinates(startCoordinates);
        newCarpool.setEndCoordinates(endCoordinates);

        newCarpool.setDepartureDate(carpoolOrganizeUpsertDTO.getEstimatedDepartureTime().toLocalDate());
        newCarpool.setEstimatedArrivalTime(newCarpool.getEstimatedDepartureTime().plusSeconds(routeInfo.durationSeconds));

        newCarpool.setCreationTime(LocalDateTime.now());

        newCarpool.setVehicle(userVehicle);
        newCarpool.setOrganizer(userInfo);

        carpoolBusinessValidator.validateUpsertCarpool(userInfo, carpoolOrganizeUpsertDTO);

        return modelMapper.map(carpoolRepository.save(newCarpool), CarpoolDTO.class);
    }

    /**Participe et retourne covoiturage
     * @param userInfo détails de l'utilisateur
     * @param carpoolId id du covoiturage auquel participer
     * @return covoiturage nouvellement participé
     */
        public CarpoolDTO createParticipateCarpool(UserInfo userInfo, Long carpoolId) {

        Carpool existingCarpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new ResourceNotFoundException("Le covoiturage à réserver n'existe pas")
        );

        UserInfo managedUser = userInfoRepository.findById(userInfo.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Utilisateur non trouvé")
        );

        carpoolBusinessValidator.validateParticipateCarpool(userInfo, existingCarpool);

        managedUser.getParticipatedCarpools().add(existingCarpool);
        userInfoRepository.save(managedUser);

        existingCarpool.getParticipants().add(managedUser);

        return modelMapper.map(existingCarpool, CarpoolDTO.class);
    }

    /**Supprime covoiturage organisé
     *
     * @param userInfo détails de l'utilisateur
     * @param carpoolId id du covoiturage à supprimer
     */
    public void deleteOrganizeCarpool(UserInfo userInfo, Long carpoolId) {
        Carpool existingCarpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new ResourceNotFoundException("Le covoiturage à supprimer n'existe pas")
        );

        carpoolBusinessValidator.validateDeleteOrganizeCarpool(userInfo, existingCarpool);


        Set<UserInfo> participants = existingCarpool.getParticipants();

        for(UserInfo participant : participants) {
            participant.getParticipatedCarpools().remove(existingCarpool);
            userInfoRepository.save(participant);
        }

        carpoolRepository.deleteById(existingCarpool.getId());
    }

    /**Supprime participation à covoiturage
     *
     * @param userInfo détails de l'utilisateur
     * @param carpoolId id du covoiturage où la participation est à annuler
     */
    public void deleteParticipateCarpool(UserInfo userInfo, Long carpoolId) {

        Carpool existingCarpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new ResourceNotFoundException("Le covoiturage à annuler n'existe pas")
        );

        UserInfo managedUser = userInfoRepository.findById(userInfo.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );

        managedUser.getParticipatedCarpools().size();
        managedUser.getParticipatedCarpools().remove(existingCarpool);


        userInfoRepository.save(managedUser);


    }

}
