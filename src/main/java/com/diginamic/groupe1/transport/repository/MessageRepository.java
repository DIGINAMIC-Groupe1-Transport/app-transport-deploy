package com.diginamic.groupe1.transport.repository;

import com.diginamic.groupe1.transport.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

}
