package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.dto.CoordinatesDTO;
import com.diginamic.groupe1.transport.entity.Coordinates;
import com.diginamic.groupe1.transport.repository.CoordinatesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoordinatesServiceTest {

    private CoordinatesRepository coordinatesRepository;
    private ModelMapper modelMapper;
    private CoordinatesService coordinatesService;

    @BeforeEach
    void setUp() {
        coordinatesRepository = mock(CoordinatesRepository.class);
        modelMapper = new ModelMapper();
        coordinatesService = new CoordinatesService(coordinatesRepository, modelMapper);
    }

    @Test
    void findOrCreateByLabel_shouldReturnExistingCoordinates_whenFoundInRepository() {
        // Given
        CoordinatesDTO dto = new CoordinatesDTO();
        dto.setLabel("Lyon");

        Coordinates expectedCoordinates = new Coordinates();
        expectedCoordinates.setLabel("Lyon");

        when(coordinatesRepository.findByLabel("Lyon")).thenReturn(Optional.of(expectedCoordinates));

        // When
        Coordinates result = coordinatesService.findOrCreateByLabel(dto);

        // Then
        assertEquals(expectedCoordinates, result);
        verify(coordinatesRepository, times(1)).findByLabel("Lyon");
    }

    @Test
    void findOrCreateByLabel_shouldReturnNewMappedCoordinates_whenNotFoundInRepository() {
        // Given
        CoordinatesDTO dto = new CoordinatesDTO();
        dto.setLabel("Nice");
        dto.setX(4.5);
        dto.setY(44.0);

        when(coordinatesRepository.findByLabel("Nice")).thenReturn(Optional.empty());

        // When
        Coordinates result = coordinatesService.findOrCreateByLabel(dto);

        // Then
        assertNotNull(result);
        assertEquals("Nice", result.getLabel());
        assertEquals(4.5, result.getX());
        assertEquals(44.0, result.getY());
        verify(coordinatesRepository, times(1)).findByLabel("Nice");
    }
}
