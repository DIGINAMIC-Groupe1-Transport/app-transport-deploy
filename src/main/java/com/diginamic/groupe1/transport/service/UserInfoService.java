package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.dto.UserInfoDTO;
import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.exception.ResourceNotFoundException;
import com.diginamic.groupe1.transport.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserInfoService {

    private ModelMapper modelMapper;

    private UserInfoRepository userInfoRepo;

    public UserInfoDTO findUserInfoByCorpEmail(String corpEmail) {

        UserInfo userInfo = userInfoRepo.findByCorpEmail(corpEmail).orElseThrow(() -> new ResourceNotFoundException("No such user found"));

        return modelMapper.map(userInfo, UserInfoDTO.class);
    }

    public Page<UserInfoDTO> findAllUsersInfo(String firstName, String lastName, Pageable pageable) {

        Page<UserInfo> usersInfo;

        if ((firstName != null && !firstName.isBlank())) {
            if ((lastName != null && !lastName.isBlank())) {
                usersInfo = userInfoRepo.findByFirstNameAndLastName(firstName, lastName, pageable);
            } else {
                usersInfo = userInfoRepo.findByFirstName(firstName, pageable);
            }
        } else if ((lastName != null && !lastName.isBlank())) {
            usersInfo = userInfoRepo.findByLastName(lastName, pageable);
        } else {
            usersInfo = userInfoRepo.findAll(pageable);
        }

        if (usersInfo.isEmpty()) {
            throw new ResourceNotFoundException("Aucun utilisateur trouvé avec ces critères.");
        }

        return usersInfo.map(user -> modelMapper.map(user, UserInfoDTO.class));
    }

    /**
     * Trouve un utilisateur par email (alias pour findByCorpEmail)
     */
    public UserInfo findByEmail(String email) {
        return userInfoRepo.findByCorpEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé pour l'email: " + email));
    }
}
