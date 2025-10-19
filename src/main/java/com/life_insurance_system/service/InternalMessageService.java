package com.life_insurance_system.service;

import com.life_insurance_system.model.InternalMessage;
import com.life_insurance_system.model.User;
import com.life_insurance_system.repository.InternalMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class InternalMessageService {

    private final InternalMessageRepository internalMessageRepository;

    @Autowired
    public InternalMessageService(InternalMessageRepository internalMessageRepository) {
        this.internalMessageRepository = internalMessageRepository;
    }

    public InternalMessage sendMessage(InternalMessage message) {
        message.setSentTimestamp(Timestamp.from(Instant.now()));
        message.setRead(false);
        return internalMessageRepository.save(message);
    }

    public List<InternalMessage> getInbox(User user) {
        return internalMessageRepository.findByReceiver(user);
    }

    public List<InternalMessage> getSentMessages(User user) {
        return internalMessageRepository.findBySender(user);
    }

    public List<InternalMessage> getAllMessages() {
        return internalMessageRepository.findAll();
    }

    public InternalMessage getMessageById(int id) {
        return internalMessageRepository.findById(id).orElse(null);
    }
}