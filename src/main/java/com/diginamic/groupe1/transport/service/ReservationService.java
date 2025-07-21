package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.entity.Reservation;
import com.diginamic.groupe1.transport.repository.ReservationRepository;
import com.diginamic.groupe1.transport.validation.ReservationValidator;
import com.diginamic.groupe1.transport.entity.Vehicle;
import com.diginamic.groupe1.transport.repository.VehicleRepository;
import com.diginamic.groupe1.transport.enums.ReservationStatus;
import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.exception.ReservationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationValidator reservationValidator;
    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation createReservation(Reservation reservation, UserInfo user) {
        getVehicleById(reservation);
        reservation.setUserInfo(user);
        reservation.setStatus(ReservationStatus.ACTIVE);
        return reservationRepository.save(reservation);
    }

    private void getVehicleById(Reservation reservation) {
        Vehicle vehicle = vehicleRepository.findById(reservation.getVehicle().getId()).orElse(null);
        List<Reservation> existing = reservationRepository.findByVehicleId(reservation.getVehicle().getId());
        String validationError = reservationValidator.validate(reservation, existing, vehicle);
        if (validationError != null) {
            throw new ReservationException(validationError);
        }
    }

    public Reservation updateReservation(Long id, Reservation updated, UserInfo user) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée"));
        // Vérifier que seul le propriétaire peut modifier
        if (!reservation.getUserInfo().getId().equals(user.getId())) {
            throw new ReservationException("Vous ne pouvez modifier que vos propres réservations.");
        }
        // Ne pas autoriser la modification si la réservation est annulée ou terminée
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new ReservationException("Impossible de modifier une réservation annulée ou terminée.");
        }
        reservation.setDepartureTime(updated.getDepartureTime());
        reservation.setArrivalTime(updated.getArrivalTime());
        reservation.setVehicle(updated.getVehicle());
        // Validation métier sur la modification
        getVehicleById(reservation);
        return reservationRepository.save(reservation);
    }

    public void cancelReservation(Long id, UserInfo user) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée"));
        if (!reservation.getUserInfo().getId().equals(user.getId())) {
            throw new ReservationException("Vous ne pouvez annuler que vos propres réservations.");
        }
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new ReservationException("Impossible d'annuler une réservation déjà annulée ou terminée.");
        }
        reservation.setStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
} 