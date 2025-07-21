package com.diginamic.groupe1.transport.repository;

import com.diginamic.groupe1.transport.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Ajoute ici des méthodes de recherche personnalisées si besoin
    List<Reservation> findByVehicleId(Long vehicleId);
} 