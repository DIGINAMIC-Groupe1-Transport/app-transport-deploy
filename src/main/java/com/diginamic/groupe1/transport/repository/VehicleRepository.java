package com.diginamic.groupe1.transport.repository;

import com.diginamic.groupe1.transport.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByIdAndUserInfoId(Long id, Long userInfoId);

    Page<Vehicle> findByUserInfoId(Long userInfoId, Pageable page);

    boolean existsByIdAndUserInfoId(Long id, Long userInfoId);

    boolean existsByRegistration(String registration);

    Page<Vehicle> findByIsCompanyTrue(Pageable page);

    Page<Vehicle> findByUserInfoIdAndIsCompanyFalse(Long userInfoId, Pageable page);
}
