package com.diginamic.groupe1.transport.service;

import com.diginamic.groupe1.transport.entity.UserInfo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class MessageService {

    @Transactional
    public void sendDeletedCarpoolMessageTo(Set<UserInfo> users){

    }
}
