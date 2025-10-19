package com.life_insurance_system.repository;

import com.life_insurance_system.model.InternalMessage;
import com.life_insurance_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternalMessageRepository extends JpaRepository<InternalMessage, Integer> {
    List<InternalMessage> findByReceiver(User receiver);
    List<InternalMessage> findBySender(User sender);
}