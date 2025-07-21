package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.dto.VehicleDTO;
import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.entity.Vehicle;
import com.diginamic.groupe1.transport.repository.VehicleRepository;
import com.diginamic.groupe1.transport.service.UserInfoService;
import com.diginamic.groupe1.transport.service.VehicleService;
import com.diginamic.groupe1.transport.validation.VehicleBusinessValidator;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private UserInfoService userInfoService;
    
    @Mock
    private ModelMapper modelMapper;
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private VehicleBusinessValidator vehicleBusinessValidator;
    
    @InjectMocks
    private VehicleService vehicleService;
    
    private UserInfo userInfo;
    private Vehicle vehicle;
    private VehicleDTO vehicleDTO;
    
    @BeforeEach
    void setUp() {
        userInfo = new UserInfo();
        userInfo.setId(1L);
        
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setUserInfo(userInfo);
        
        vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(1L);
    }
    
    @Test
    void getPersonalVehicles_Success() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        Pageable pageable = PageRequest.of(0, 10);
        
        when(vehicleRepository.findByUserInfoId(userInfo.getId(), pageable)).thenReturn(vehiclePage);
        when(modelMapper.map(vehicle, VehicleDTO.class)).thenReturn(vehicleDTO);
        
        Page<VehicleDTO> result = vehicleService.getPersonalVehicles(userInfo, pageable);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
    
    @Test
    void createPersonalVehicle_Success() {
        when(modelMapper.map(vehicleDTO, Vehicle.class)).thenReturn(vehicle);
        when(vehicleRepository.save(any())).thenReturn(vehicle);
        
        VehicleDTO result = vehicleService.createPersonalVehicle(userInfo, vehicleDTO);
        
        assertNotNull(result);
        verify(vehicleBusinessValidator).validateCreateVehicle(vehicleDTO);
        verify(vehicleRepository).save(any());
    }
    
    @Test
    void createServiceVehicle_Success() {
        when(modelMapper.map(vehicleDTO, Vehicle.class)).thenReturn(vehicle);
        when(vehicleRepository.save(any())).thenReturn(vehicle);
        
        VehicleDTO result = vehicleService.createServiceVehicle(vehicleDTO);
        
        assertNotNull(result);
        assertTrue(vehicle.getIsCompany());
        verify(vehicleBusinessValidator).validateCreateVehicle(vehicleDTO);
    }

    @Test
    void deletePersonalVehicle_Success() {
        // Arrangement
        Long vehicleId = 1L;

        // Utiliser doNothing().when() pour les méthodes void
        doNothing().when(vehicleBusinessValidator)
                .validateDeleteVehicle(eq(vehicleId), eq(userInfo), any(LocalDateTime.class));

        // Action
        vehicleService.deletePersonalVehicle(vehicleId, userInfo);

        // Assert
        verify(vehicleRepository).deleteById(vehicleId);
        verify(vehicleBusinessValidator).validateDeleteVehicle(eq(vehicleId), eq(userInfo), any(LocalDateTime.class));
    }
    @Test
    void getAllServiceVehicles_Success() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        Pageable pageable = PageRequest.of(0, 10);
        
        when(vehicleRepository.findByIsCompanyTrue(pageable)).thenReturn(vehiclePage);
        when(modelMapper.map(vehicle, VehicleDTO.class)).thenReturn(vehicleDTO);
        
        Page<VehicleDTO> result = vehicleService.getAllServiceVehicles(pageable);
        
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
    
    @Test
    void getServiceVehicleById_Success() {
        vehicle.setIsCompany(true);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(modelMapper.map(vehicle, VehicleDTO.class)).thenReturn(vehicleDTO);
        
        VehicleDTO result = vehicleService.getServiceVehicleById(1L);
        
        assertNotNull(result);
        assertEquals(vehicleDTO, result);
    }
    
    @Test
    void getServiceVehicleById_NotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () ->
            vehicleService.getServiceVehicleById(1L)
        );
    }
}
