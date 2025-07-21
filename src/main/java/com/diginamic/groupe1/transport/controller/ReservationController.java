package com.diginamic.groupe1.transport.controller;

import com.diginamic.groupe1.transport.dto.ReservationDTO;
import com.diginamic.groupe1.transport.entity.Reservation;
import com.diginamic.groupe1.transport.exception.ResourceNotFoundException;
import com.diginamic.groupe1.transport.mapper.ReservationMapper;
import com.diginamic.groupe1.transport.service.ReservationService;
import com.diginamic.groupe1.transport.service.UserInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserInfoService userInfoService;
    private final ReservationMapper reservationMapper;

    public ReservationController(
            ReservationService reservationService,
            UserInfoService userInfoService,
            ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.userInfoService = userInfoService;
        this.reservationMapper = reservationMapper;
    }

    @GetMapping
    public List<ReservationDTO> getAllReservations() {
        return reservationService.getAllReservations()
                .stream()
                .map(reservationMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ReservationDTO getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(reservationMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDTO createReservation(@Valid @RequestBody ReservationDTO dto,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userInfoService.findByEmail(userDetails.getUsername());
        Reservation reservation = reservationMapper.toEntity(dto);
        return reservationMapper.toDTO(reservationService.createReservation(reservation, user));
    }

    @PutMapping("/{id}")
    public ReservationDTO updateReservation(@PathVariable Long id,
                                            @Valid @RequestBody ReservationDTO dto,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userInfoService.findByEmail(userDetails.getUsername());
        Reservation reservation = reservationMapper.toEntity(dto);
        return reservationMapper.toDTO(reservationService.updateReservation(id, reservation, user));
    }

    @DeleteMapping("/cancel/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        var user = userInfoService.findByEmail(userDetails.getUsername());
        reservationService.cancelReservation(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }
}
