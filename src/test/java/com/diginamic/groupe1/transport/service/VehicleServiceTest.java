package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.dto.VehicleDTO;
import com.diginamic.groupe1.transport.entity.Role;
import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.entity.Vehicle;
import com.diginamic.groupe1.transport.exception.BusinessException;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

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
    @Test
    void updateServiceVehicle_Success() {
        Long vehicleId = 1L;
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setBrand("Toyota");
        vehicleDTO.setModel("Corolla");

        UserInfo adminUser = new UserInfo();
        Role adminRole = new Role(1L, "ADMIN");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);

        Vehicle existingVehicle = new Vehicle();
        existingVehicle.setId(vehicleId);
        existingVehicle.setBrand("Old Brand");

        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setId(vehicleId);
        updatedVehicle.setBrand("Toyota");
        updatedVehicle.setModel("Corolla");
        updatedVehicle.setIsCompany(true);

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        doNothing().when(modelMapper).map(vehicleDTO, existingVehicle);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(updatedVehicle);

        Vehicle result = vehicleService.updateServiceVehicle(vehicleId, vehicleDTO, adminUser);

        assertNotNull(result);
        assertEquals(vehicleId, result.getId());
        assertEquals(true, result.getIsCompany());
        verify(vehicleBusinessValidator).validateUpdateVehicle(vehicleId, vehicleDTO);
        verify(vehicleRepository).save(existingVehicle);
    }

    @Test
    void updateServiceVehicle_NotAdmin_ThrowsException() {
        Long vehicleId = 1L;
        VehicleDTO vehicleDTO = new VehicleDTO();

        UserInfo regularUser = new UserInfo();
        Role userRole = new Role(2L, "USER");
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        regularUser.setRoles(userRoles);

        assertThrows(BusinessException.class,
                () -> vehicleService.updateServiceVehicle(vehicleId, vehicleDTO, regularUser));

        verify(vehicleBusinessValidator, never()).validateUpdateVehicle(any(), any());
        verify(vehicleRepository, never()).findById(any());
    }

    @Test
    void updateServiceVehicle_VehicleNotFound_ThrowsException() {
        Long vehicleId = 1L;
        VehicleDTO vehicleDTO = new VehicleDTO();

        UserInfo adminUser = new UserInfo();
        Role adminRole = new Role(1L, "ADMIN");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> vehicleService.updateServiceVehicle(vehicleId, vehicleDTO, adminUser));

        verify(vehicleBusinessValidator).validateUpdateVehicle(vehicleId, vehicleDTO);
        verify(vehicleRepository).findById(vehicleId);
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void deleteServiceVehicle_Success() {
        Long vehicleId = 1L;
        UserInfo adminUser = new UserInfo();
        Role adminRole = new Role(1L, "ADMIN");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);

        doNothing().when(vehicleBusinessValidator).validateDeleteVehicle(eq(vehicleId), eq(adminUser), any(LocalDateTime.class));
        doNothing().when(vehicleRepository).deleteById(vehicleId);

        vehicleService.deleteServiceVehicle(vehicleId, adminUser);

        verify(vehicleBusinessValidator).validateDeleteVehicle(eq(vehicleId), eq(adminUser), any(LocalDateTime.class));
        verify(vehicleRepository).deleteById(vehicleId);
    }

    @Test
    void getVehicleById_Success() {
        Long vehicleId = 1L;
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setBrand("Toyota");
        vehicle.setModel("Corolla");

        VehicleDTO expectedDTO = new VehicleDTO();
        expectedDTO.setId(vehicleId);
        expectedDTO.setBrand("Toyota");
        expectedDTO.setModel("Corolla");

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(modelMapper.map(vehicle, VehicleDTO.class)).thenReturn(expectedDTO);

        VehicleDTO result = vehicleService.getVehicleById(vehicleId);

        assertNotNull(result);
        assertEquals(vehicleId, result.getId());
        assertEquals("Toyota", result.getBrand());
        assertEquals("Corolla", result.getModel());
        verify(vehicleRepository).findById(vehicleId);
        verify(modelMapper).map(vehicle, VehicleDTO.class);
    }

    @Test
    void getVehicleById_VehicleNotFound_ThrowsException() {
        Long vehicleId = 1L;

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> vehicleService.getVehicleById(vehicleId));

        verify(vehicleRepository).findById(vehicleId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void validateAdminRights_AdminRole_Success() {
        UserInfo adminUser = new UserInfo();
        Role adminRole = new Role(1L, "ADMIN");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);

        assertDoesNotThrow(() -> {
            try {
                Method method = VehicleService.class.getDeclaredMethod("validateAdminRights", UserInfo.class);
                method.setAccessible(true);
                method.invoke(vehicleService, adminUser);
            } catch (Exception e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void validateAdminRights_NonAdminRole_ThrowsException() {
        UserInfo regularUser = new UserInfo();
        Role userRole = new Role(2L, "USER");
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        regularUser.setRoles(userRoles);

        assertThrows(BusinessException.class, () -> {
            try {
                Method method = VehicleService.class.getDeclaredMethod("validateAdminRights", UserInfo.class);
                method.setAccessible(true);
                method.invoke(vehicleService, regularUser);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
