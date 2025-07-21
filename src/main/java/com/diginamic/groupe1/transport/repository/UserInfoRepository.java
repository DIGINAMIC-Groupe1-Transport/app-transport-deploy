package com.diginamic.groupe1.transport.repository;

import com.diginamic.groupe1.transport.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    public Optional<UserInfo> findByCorpEmail(String corpEmail);

    public Optional<UserInfo> findById(Long id);

    public Page<UserInfo> findAll(Pageable pageable);

    public Page<UserInfo> findByFirstName(String firstName, Pageable pageable);

    public Page<UserInfo> findByLastName(String lastName, Pageable pageable);

    public Page<UserInfo> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);
}
