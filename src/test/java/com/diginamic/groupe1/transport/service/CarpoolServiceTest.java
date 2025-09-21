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
import org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Test
    void findAllCarpools_Success() {
        Double startX = 2.0, startY = 48.0, endX = 3.0, endY = 49.0;
        LocalDate departureDate = LocalDate.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);

        Object[] row = new Object[27];
        row[0] = 1L;
        row[1] = Timestamp.valueOf(LocalDateTime.now());
        row[2] = null;
        row[3] = Timestamp.valueOf(LocalDateTime.now().plusHours(1));
        row[4] = Timestamp.valueOf(LocalDateTime.now().plusHours(2));
        row[5] = 3600;
        row[6] = 50000;
        row[7] = 3;
        row[8] = false;
        row[9] = "Start Label";
        row[10] = "Paris";
        row[11] = "Rue de Rivoli";
        row[12] = "1";
        row[13] = 2.0;
        row[14] = 48.0;
        row[15] = "End Label";
        row[16] = "Lyon";
        row[17] = "Rue de la République";
        row[18] = "10";
        row[19] = 3.0;
        row[20] = 49.0;
        row[21] = null;
        row[22] = null;
        row[23] = "Toyota Corolla";
        row[24] = 100.0;
        row[25] = 200.0;
        row[26] = 150.0;

        List<Object[]> mockData = new ArrayList<>();
        mockData.add(row);

        when(carpoolRepository.findMatchingCarpools(
                eq(startX), eq(startY), eq(endX), eq(endY),
                eq(departureDate), any(LocalDateTime.class),
                eq(1.0), eq(1.0), eq(10), eq(0)))
                .thenReturn(mockData);

        when(carpoolRepository.countMatchingCarpools(
                eq(startX), eq(startY), eq(endX), eq(endY),
                eq(departureDate), any(LocalDateTime.class)))
                .thenReturn(1L);

        Page<CarpoolSearchResponseListDTO> result = carpoolService.findAllCarpools(
                startX, startY, endX, endY, departureDate, 1.0, 1.0, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalElements());

        CarpoolSearchResponseListDTO dto = result.getContent().get(0);
        assertEquals(1L, dto.getId());
        assertEquals(3600, dto.getEstimatedDuration());
        assertEquals(50000, dto.getEstimatedLength());
        assertEquals(3, dto.getRemainingSeats());
        assertEquals(false, dto.getIsCanceled());
        assertEquals("Toyota Corolla", dto.getModel());
        assertEquals(100.0, dto.getToStartDistance());
        assertEquals(200.0, dto.getToEndDistance());
        assertEquals(150.0, dto.getWeightedDistance());

        assertNotNull(dto.getStartCoordinates());
        assertEquals("Start Label", dto.getStartCoordinates().getLabel());
        assertEquals("Paris", dto.getStartCoordinates().getCity());
        assertEquals("Rue de Rivoli", dto.getStartCoordinates().getStreet());
        assertEquals("1", dto.getStartCoordinates().getHouseNumber());
        assertEquals(2.0, dto.getStartCoordinates().getX());
        assertEquals(48.0, dto.getStartCoordinates().getY());

        assertNotNull(dto.getEndCoordinates());
        assertEquals("End Label", dto.getEndCoordinates().getLabel());
        assertEquals("Lyon", dto.getEndCoordinates().getCity());
        assertEquals("Rue de la République", dto.getEndCoordinates().getStreet());
        assertEquals("10", dto.getEndCoordinates().getHouseNumber());
        assertEquals(3.0, dto.getEndCoordinates().getX());
        assertEquals(49.0, dto.getEndCoordinates().getY());

        verify(carpoolRepository).findMatchingCarpools(
                eq(startX), eq(startY), eq(endX), eq(endY),
                eq(departureDate), any(LocalDateTime.class),
                eq(1.0), eq(1.0), eq(10), eq(0));
        verify(carpoolRepository).countMatchingCarpools(
                eq(startX), eq(startY), eq(endX), eq(endY),
                eq(departureDate), any(LocalDateTime.class));
    }

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