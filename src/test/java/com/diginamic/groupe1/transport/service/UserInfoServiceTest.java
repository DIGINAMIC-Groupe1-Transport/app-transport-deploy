package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.dto.UserInfoDTO;
import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.exception.ResourceNotFoundException;
import com.diginamic.groupe1.transport.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserInfoServiceTest {

    private UserInfoRepository userInfoRepository;
    private ModelMapper modelMapper;
    private UserInfoService userInfoService;

    @BeforeEach
    void setUp() {
        userInfoRepository = mock(UserInfoRepository.class);
        modelMapper = new ModelMapper();
        userInfoService = new UserInfoService(modelMapper, userInfoRepository);
    }

    @Test
    void shouldFindUserByCorpEmail() {
        // Given
        String email = "john.doe@company.com";
        UserInfo userInfo = new UserInfo();
        userInfo.setCorpEmail(email);
        userInfo.setFirstName("John");
        userInfo.setLastName("Doe");

        when(userInfoRepository.findByCorpEmail(email)).thenReturn(Optional.of(userInfo));

        // When
        UserInfoDTO result = userInfoService.findUserInfoByCorpEmail(email);

        // Then
        assertThat(result.getCorpEmail()).isEqualTo(email);
        assertThat(result.getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldThrowWhenUserNotFoundByCorpEmail() {
        // Given
        String email = "notfound@company.com";
        when(userInfoRepository.findByCorpEmail(email)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> userInfoService.findUserInfoByCorpEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No such user found");
    }

    @Test
    void shouldReturnAllUsersWithFirstAndLastName() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        UserInfo user1 = new UserInfo();
        user1.setFirstName("Alice");
        user1.setLastName("Smith");

        when(userInfoRepository.findByFirstNameAndLastName("Alice", "Smith", pageable))
                .thenReturn(new PageImpl<>(List.of(user1)));

        // When
        Page<UserInfoDTO> result = userInfoService.findAllUsersInfo("Alice", "Smith", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void shouldReturnAllUsersWhenNoFilters() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        UserInfo user = new UserInfo();
        user.setFirstName("Bob");

        when(userInfoRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(user)));

        // When
        Page<UserInfoDTO> result = userInfoService.findAllUsersInfo(null, null, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Bob");
    }

    @Test
    void shouldThrowIfNoUserMatchesCriteria() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(userInfoRepository.findAll(pageable)).thenReturn(Page.empty());

        // Then
        assertThatThrownBy(() -> userInfoService.findAllUsersInfo(null, null, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Aucun utilisateur trouvé avec ces critères.");
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        String email = "jane@corp.com";
        UserInfo userInfo = new UserInfo();
        userInfo.setCorpEmail(email);

        when(userInfoRepository.findByCorpEmail(email)).thenReturn(Optional.of(userInfo));

        // When
        UserInfo result = userInfoService.findByEmail(email);

        // Then
        assertThat(result.getCorpEmail()).isEqualTo(email);
    }

    @Test
    void shouldThrowWhenEmailNotFound() {
        // Given
        String email = "missing@corp.com";
        when(userInfoRepository.findByCorpEmail(email)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> userInfoService.findByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Utilisateur non trouvé pour l'email");
    }
}
