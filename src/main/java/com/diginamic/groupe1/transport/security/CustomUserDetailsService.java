package com.diginamic.groupe1.transport.security;

import com.diginamic.groupe1.transport.entity.UserInfo;
import com.diginamic.groupe1.transport.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserInfoRepository userInfoRepo;

    @Override
    public UserDetails loadUserByUsername(String corpEmail) throws UsernameNotFoundException {
        UserInfo user = userInfoRepo.findByCorpEmail(corpEmail).orElseThrow(() -> new UsernameNotFoundException("Username "+corpEmail+" was not found"));
        return new CustomUserDetails(user);
    }
}
