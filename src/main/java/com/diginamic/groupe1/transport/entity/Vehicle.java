package com.diginamic.groupe1.transport.entity;

import com.diginamic.groupe1.transport.enums.VehicleMotor;
import com.diginamic.groupe1.transport.enums.VehicleCategory;
import com.diginamic.groupe1.transport.enums.VehicleStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant un véhicule dans le système
 */
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String registration;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer seats;

    @Column(name = "is_company")
    private Boolean isCompany;

    @Column(name= "co2_per_km")
    private Integer co2PerKm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VehicleStatus vehicleStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private VehicleCategory vehicleCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "motor", nullable = false)
    private VehicleMotor vehicleMotor;

    @ManyToOne
    @JoinColumn(name = "fk_user_owner_id")
    @JsonBackReference
    private UserInfo userInfo;

    @OneToMany(mappedBy = "vehicle")
    @JsonBackReference
    private Set<Carpool> carpools = new HashSet<>();

}