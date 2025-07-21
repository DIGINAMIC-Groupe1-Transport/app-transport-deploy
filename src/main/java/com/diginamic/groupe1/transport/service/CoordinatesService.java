package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.dto.CoordinatesDTO;
import com.diginamic.groupe1.transport.entity.Coordinates;
import com.diginamic.groupe1.transport.repository.CoordinatesRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class CoordinatesService {

    CoordinatesRepository coordinatesRepository;

    ModelMapper modelMapper;

    public Coordinates findOrCreateByLabel (CoordinatesDTO coordinatesDTO){

        return coordinatesRepository.findByLabel(
                coordinatesDTO.getLabel()
        ).orElseGet(() -> modelMapper.map(coordinatesDTO, Coordinates.class));
    }
}
