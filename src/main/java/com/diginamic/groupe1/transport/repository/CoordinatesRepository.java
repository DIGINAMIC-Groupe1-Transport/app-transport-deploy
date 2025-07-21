package com.diginamic.groupe1.transport.repository;

import com.diginamic.groupe1.transport.entity.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {

    public Optional<Coordinates> findByLabel (
            String label
    );
}
