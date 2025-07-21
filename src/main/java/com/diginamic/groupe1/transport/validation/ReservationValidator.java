package com.diginamic.groupe1.transport.validation;

import com.diginamic.groupe1.transport.entity.Reservation;
import com.diginamic.groupe1.transport.entity.Vehicle;
import com.diginamic.groupe1.transport.enums.VehicleStatus;
import com.diginamic.groupe1.transport.enums.ReservationStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationValidator {
    /**
     * Valide la cohérence métier d'une réservation
     * @param reservation la réservation à valider
     * @param reservationsExistantes les réservations déjà existantes pour le même véhicule
     * @param vehicle le véhicule concerné
     * @return message d'erreur ou null si tout est OK
     */
    public String validate(Reservation reservation, List<Reservation> reservationsExistantes, Vehicle vehicle) {
        // 1. Dates cohérentes
        if (reservation.getDepartureTime() == null || reservation.getArrivalTime() == null) {
            System.out.println(reservation);


            return "Les dates de début et de fin sont obligatoires.";
        }
        if (reservation.getDepartureTime().isAfter(reservation.getArrivalTime())) {
            return "La date de début doit être avant la date de fin.";
        }
        if (reservation.getDepartureTime().isBefore(LocalDateTime.now())) {
            return "Impossible de réserver dans le passé.";
        }
        // 2. Véhicule en service
        if (vehicle == null || vehicle.getVehicleStatus() != VehicleStatus.AVAILABLE) {
            return "Le véhicule n'est pas disponible (hors service ou en travaux).";
        }
        // 3. Pas de chevauchement de réservation
        for (Reservation existante : reservationsExistantes) {
            if (existante.getId() != null && reservation.getId() != null && existante.getId().equals(reservation.getId())) continue;
            if (existante.getStatus() == null || existante.getStatus() == ReservationStatus.ACTIVE &&
                existante.getDepartureTime() != null && existante.getArrivalTime() != null &&
                reservation.getDepartureTime().isBefore(existante.getArrivalTime()) &&
                reservation.getArrivalTime().isAfter(existante.getDepartureTime())) {
                return "Le véhicule est déjà réservé sur ce créneau.";
            }
        }
        // 4. Autres règles métier à ajouter ici
        return null; // OK
    }
} 