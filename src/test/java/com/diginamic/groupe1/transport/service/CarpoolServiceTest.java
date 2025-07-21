package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.dto.*;
import com.diginamic.groupe1.transport.dto.carpool.*;
import com.diginamic.groupe1.transport.entity.*;
import com.diginamic.groupe1.transport.exception.ResourceNotFoundException;
import com.diginamic.groupe1.transport.repository.*;
import com.diginamic.groupe1.transport.security.CustomUserDetails;
import com.diginamic.groupe1.transport.utils.CarpoolUtils;
import com.diginamic.groupe1.transport.validation.CarpoolBusinessValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarpoolServiceTest {

    @Mock
    private CarpoolRepository carpoolRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private CoordinatesService coordinatesService;

    @Mock
    private RouteCalculatorService routeCalculatorService;

    @Mock
    private CarpoolBusinessValidator carpoolBusinessValidator;

    @Mock
    private CarpoolUtils carpoolUtils;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CarpoolService carpoolService;

    private UserInfo userInfo;
    private Carpool carpool;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {

        userInfo = new UserInfo();
        userInfo.setId(1L);
        userInfo.setParticipatedCarpools(new java.util.HashSet<>());

        vehicle = new Vehicle();
        vehicle.setId(1L);

        carpool = new Carpool();
        carpool.setId(1L);
        carpool.setOrganizer(userInfo);
        carpool.setVehicle(vehicle);
        carpool.setEstimatedDepartureTime(LocalDateTime.now().plusHours(1));
        carpool.setParticipants(new java.util.HashSet<>());
    }

    @Test
    void findCarpoolDetails_Success() {

        Long carpoolId = 1L;
        CarpoolDetailsDTO expectedDTO = new CarpoolDetailsDTO();

        when(carpoolRepository.findById(carpoolId)).thenReturn(Optional.of(carpool));
        when(modelMapper.map(carpool, CarpoolDetailsDTO.class)).thenReturn(expectedDTO);

        CarpoolDetailsDTO result = carpoolService.findCarpoolDetails(carpoolId);

        assertNotNull(result);
        assertEquals(expectedDTO, result);
        verify(carpoolRepository).findById(carpoolId);
    }

    @Test
    void findCarpoolDetails_NotFound() {

        Long carpoolId = 1L;
        when(carpoolRepository.findById(carpoolId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> carpoolService.findCarpoolDetails(carpoolId));
    }

    @Test
    void findAllOrganizedCarpools_Success() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Carpool> carpoolPage = new PageImpl<>(List.of(carpool));
        CarpoolDTO carpoolDTO = new CarpoolDTO();

        when(carpoolRepository.findByOrganizerId(userInfo.getId(), pageable)).thenReturn(carpoolPage);
        when(modelMapper.map(carpool, CarpoolDTO.class)).thenReturn(carpoolDTO);
        when(carpoolUtils.calculateRemainingSeats(carpool)).thenReturn(3);

        Page<CarpoolDTO> result = carpoolService.findAllOrganizedCarpools(userInfo, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(carpoolRepository).findByOrganizerId(userInfo.getId(), pageable);
    }

    @Test
    void createOrganizeCarpool_Success() {

        CarpoolOrganizeUpsertDTO upsertDTO = new CarpoolOrganizeUpsertDTO();
        upsertDTO.setVehicleId(1L);
        upsertDTO.setEstimatedDepartureTime(LocalDateTime.now().plusHours(1));

        Coordinates startCoords = new Coordinates();
        Coordinates endCoords = new Coordinates();
        RouteCalculatorService.RouteInfo routeInfo = new RouteCalculatorService.RouteInfo(1000, 600);
        CarpoolDTO expectedDTO = new CarpoolDTO();

        when(vehicleRepository.findByIdAndUserInfoId(1L, userInfo.getId())).thenReturn(Optional.of(vehicle));
        when(coordinatesService.findOrCreateByLabel(any())).thenReturn(startCoords).thenReturn(endCoords);
        when(routeCalculatorService.calculateRoute(startCoords, endCoords)).thenReturn(routeInfo);
        when(modelMapper.map(upsertDTO, Carpool.class)).thenReturn(carpool);
        when(carpoolRepository.save(any(Carpool.class))).thenReturn(carpool);
        when(modelMapper.map(carpool, CarpoolDTO.class)).thenReturn(expectedDTO);

        CarpoolDTO result = carpoolService.createOrganizeCarpool(userInfo, upsertDTO);

        assertNotNull(result);
        verify(carpoolBusinessValidator).validateUpsertCarpool(userInfo, upsertDTO);
        verify(carpoolRepository).save(any(Carpool.class));
    }

    @Test
    void createOrganizeCarpool_VehicleNotFound() {

        CarpoolOrganizeUpsertDTO upsertDTO = new CarpoolOrganizeUpsertDTO();
        upsertDTO.setVehicleId(1L);

        when(vehicleRepository.findByIdAndUserInfoId(1L, userInfo.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> carpoolService.createOrganizeCarpool(userInfo, upsertDTO));
    }

    @Test
    void createParticipateCarpool_Success() {

        Long carpoolId = 1L;
        userInfo.setParticipatedCarpools(new java.util.HashSet<>());
        carpool.setParticipants(new java.util.HashSet<>());
        CarpoolDTO expectedDTO = new CarpoolDTO();

        when(carpoolRepository.findById(carpoolId)).thenReturn(Optional.of(carpool));
        when(userInfoRepository.findById(userInfo.getId())).thenReturn(Optional.of(userInfo));
        when(modelMapper.map(carpool, CarpoolDTO.class)).thenReturn(expectedDTO);

        CarpoolDTO result = carpoolService.createParticipateCarpool(userInfo, carpoolId);

        assertNotNull(result);
        assertTrue(userInfo.getParticipatedCarpools().contains(carpool));
        assertTrue(carpool.getParticipants().contains(userInfo));
        verify(carpoolBusinessValidator).validateParticipateCarpool(userInfo, carpool);
        verify(userInfoRepository).save(userInfo);
    }

    @Test
    void createParticipateCarpool_CarpoolNotFound() {

        Long carpoolId = 1L;
        when(carpoolRepository.findById(carpoolId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> carpoolService.createParticipateCarpool(userInfo, carpoolId));
    }

    @Test
    void createParticipateCarpool_UserNotFound() {

        Long carpoolId = 1L;
        when(carpoolRepository.findById(carpoolId)).thenReturn(Optional.of(carpool));
        when(userInfoRepository.findById(userInfo.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> carpoolService.createParticipateCarpool(userInfo, carpoolId));
    }

    @Test
    void deleteOrganizeCarpool_Success() {

        Long carpoolId = 1L;

        when(carpoolRepository.findById(carpoolId)).thenReturn(Optional.of(carpool));

        carpoolService.deleteOrganizeCarpool(userInfo, carpoolId);

        verify(carpoolBusinessValidator).validateDeleteOrganizeCarpool(userInfo, carpool);
        verify(carpoolRepository).deleteById(carpoolId);
    }

    @Test
    void deleteOrganizeCarpool_NotFound() {
        // Given
        Long carpoolId = 1L;
        when(carpoolRepository.findById(carpoolId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> carpoolService.deleteOrganizeCarpool(userInfo, carpoolId));
    }

//    @Test
//    void findAllCarpools_Success() {
//        // Given
//        Double startX = 2.0, startY = 48.0, endX = 3.0, endY = 49.0;
//        Pageable pageable = PageRequest.of(0, 10);
//
//        Object[] row = new Object[27];
//        row[0] = 1L; // id
//        row[1] = java.sql.Timestamp.valueOf(LocalDateTime.now()); // creationTime
//        row[3] = java.sql.Timestamp.valueOf(LocalDateTime.now().plusHours(1)); // departureTime
//        row[4] = java.sql.Timestamp.valueOf(LocalDateTime.now().plusHours(2)); // arrivalTime
//        row[5] = 3600; // duration
//        row[6] = 50000; // length
//        row[7] = 3; // remainingSeats
//        row[8] = false; // isCanceled
//        // Start coordinates
//        row[9] = "Start Label"; row[10] = "Paris"; row[11] = "Rue A"; row[12] = "1";
//        row[13] = 2.0; row[14] = 48.0;
//        // End coordinates
//        row[15] = "End Label"; row[16] = "Lyon"; row[17] = "Rue B"; row[18] = "2";
//        row[19] = 3.0; row[20] = 49.0;
//        row[23] = "Toyota"; // model
//        row[24] = 100.0; // toStartDistance
//        row[25] = 200.0; // toEndDistance
//        row[26] = 150.0; // weightedDistance
//
//        when(carpoolRepository.findMatchingCarpools(any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
//                .thenReturn((List<Object[]>) List.of(row));
//        when(carpoolRepository.countMatchingCarpools(any(), any(), any(), any(), any(), any()))
//                .thenReturn(1L);
//
//        // When
//        Page<CarpoolSearchResponseListDTO> result = carpoolService.findAllCarpools(
//                startX, startY, endX, endY, null, null, null, pageable);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getContent().size());
//        CarpoolSearchResponseListDTO dto = result.getContent().get(0);
//        assertEquals(1L, dto.getId());
//        assertEquals("Toyota", dto.getModel());
//    }

    @Test
    void findAllParticipatedCarpools_Success() {

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserInfo()).thenReturn(userInfo);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Carpool> carpoolPage = new PageImpl<>(List.of(carpool));
        CarpoolDTO carpoolDTO = new CarpoolDTO();

        when(carpoolRepository.findByParticipantsId(userInfo.getId(), pageable)).thenReturn(carpoolPage);
        when(modelMapper.map(carpool, CarpoolDTO.class)).thenReturn(carpoolDTO);
        when(carpoolUtils.calculateAvailableSeats(carpool)).thenReturn(2);

        Page<CarpoolDTO> result = carpoolService.findAllParticipatedCarpools(userDetails, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(carpoolRepository).findByParticipantsId(userInfo.getId(), pageable);
    }

    @Test
    void deleteParticipateCarpool_Success() {

        Long carpoolId = 1L;
        userInfo.setParticipatedCarpools(new java.util.HashSet<>(java.util.Set.of(carpool)));

        when(carpoolRepository.findById(carpoolId)).thenReturn(Optional.of(carpool));
        when(userInfoRepository.findById(userInfo.getId())).thenReturn(Optional.of(userInfo));

        carpoolService.deleteParticipateCarpool(userInfo, carpoolId);

        verify(userInfoRepository).save(userInfo);
        assertFalse(userInfo.getParticipatedCarpools().contains(carpool));
    }

    @Test
    void deleteParticipateCarpool_CarpoolNotFound() {

        Long carpoolId = 1L;
        when(carpoolRepository.findById(carpoolId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> carpoolService.deleteParticipateCarpool(userInfo, carpoolId));
    }

    @Test
    void deleteParticipateCarpool_UserNotFound() {

        Long carpoolId = 1L;
        when(carpoolRepository.findById(carpoolId)).thenReturn(Optional.of(carpool));
        when(userInfoRepository.findById(userInfo.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> carpoolService.deleteParticipateCarpool(userInfo, carpoolId));
    }
}