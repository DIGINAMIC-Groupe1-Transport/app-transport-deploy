package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.entity.Reservation;
import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.entity.Vehicle;
import com.diginamic.groupe1.transport.enums.ReservationStatus;
import com.diginamic.groupe1.transport.exception.ReservationException;
import com.diginamic.groupe1.transport.repository.ReservationRepository;
import com.diginamic.groupe1.transport.repository.VehicleRepository;
import com.diginamic.groupe1.transport.service.ReservationService;
import com.diginamic.groupe1.transport.validation.ReservationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    
    @Mock
    private ReservationValidator reservationValidator;
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @InjectMocks
    private ReservationService reservationService;
    
    private UserInfo userInfo;
    private Reservation reservation;
    private Vehicle vehicle;
    
    @BeforeEach
    void setUp() {
        userInfo = new UserInfo();
        userInfo.setId(1L);
        
        vehicle = new Vehicle();
        vehicle.setId(1L);
        
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUserInfo(userInfo);
        reservation.setVehicle(vehicle);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setDepartureTime(LocalDateTime.now().plusHours(1));
        reservation.setArrivalTime(LocalDateTime.now().plusHours(2));
    }
    
    @Test
    void getAllReservations_Success() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));
        
        List<Reservation> result = reservationService.getAllReservations();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reservationRepository).findAll();
    }
    
    @Test
    void getReservationById_Success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        
        Optional<Reservation> result = reservationService.getReservationById(1L);
        
        assertTrue(result.isPresent());
        assertEquals(reservation, result.get());
    }
    
    @Test
    void createReservation_Success() {
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(reservationRepository.findByVehicleId(vehicle.getId())).thenReturn(List.of());
        when(reservationValidator.validate(any(), any(), any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(reservation);
        
        Reservation result = reservationService.createReservation(reservation, userInfo);
        
        assertNotNull(result);
        assertEquals(ReservationStatus.ACTIVE, result.getStatus());
        verify(reservationRepository).save(any());
    }
    
    @Test
    void createReservation_ValidationError() {
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(reservationRepository.findByVehicleId(vehicle.getId())).thenReturn(List.of());
        when(reservationValidator.validate(any(), any(), any())).thenReturn("Erreur de validation");
        
        assertThrows(ReservationException.class, () ->
            reservationService.createReservation(reservation, userInfo)
        );
    }
    
    @Test
    void updateReservation_Success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(reservationValidator.validate(any(), any(), any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(reservation);
        
        Reservation updated = new Reservation();
        updated.setVehicle(vehicle);
        updated.setDepartureTime(LocalDateTime.now().plusHours(3));
        updated.setArrivalTime(LocalDateTime.now().plusHours(4));
        
        Reservation result = reservationService.updateReservation(1L, updated, userInfo);
        
        assertNotNull(result);
        verify(reservationRepository).save(any());
    }
    
    @Test
    void updateReservation_NotFound() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () ->
            reservationService.updateReservation(1L, reservation, userInfo)
        );
    }
    
    @Test
    void cancelReservation_Success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        
        reservationService.cancelReservation(1L, userInfo);
        
        assertEquals(ReservationStatus.CANCELED, reservation.getStatus());
        verify(reservationRepository).save(reservation);
    }
}