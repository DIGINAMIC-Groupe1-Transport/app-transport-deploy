package com.diginamic.groupe1.transport.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant des coordonnées géographiques
 */
@Entity
@Table(name = "coordinates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Coordinates {

    //noms des valeurs renvoyées par l'api proton
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "address")
    private String label;

    @Column(name = "street_name")
    private String street;

    @Column(name = "house_number")
    private String houseNumber;

    @Column
    private String city;

    @Column(name = "long_coordinates")
    private Double  x;

    @Column(name = "lat_coordinates")
    private Double  y;

    @OneToMany(mappedBy = "startCoordinates")
    @JsonBackReference
    private Set<Carpool> startingCarpools = new HashSet<>();

    @OneToMany(mappedBy = "endCoordinates")
    @JsonBackReference
    private Set<Carpool> endingCarpools = new HashSet<>();
}
