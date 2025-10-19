package com.life_insurance_system.service;

import com.life_insurance_system.model.SystemAnnouncement;
import com.life_insurance_system.repository.SystemAnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemAnnouncementService {

    private final SystemAnnouncementRepository systemAnnouncementRepository;

    @Autowired
    public SystemAnnouncementService(SystemAnnouncementRepository systemAnnouncementRepository) {
        this.systemAnnouncementRepository = systemAnnouncementRepository;
    }

    public List<SystemAnnouncement> getAllAnnouncements() {
        return systemAnnouncementRepository.findAll();
    }

    public SystemAnnouncement createAnnouncement(SystemAnnouncement announcement) {
        return systemAnnouncementRepository.save(announcement);
    }

    public SystemAnnouncement getAnnouncementById(int id) {
        return systemAnnouncementRepository.findById(id).orElse(null);
    }

    public void deleteAnnouncement(int id) {
        systemAnnouncementRepository.deleteById(id);
    }

    public SystemAnnouncement updateAnnouncement(SystemAnnouncement announcement) {
        return systemAnnouncementRepository.save(announcement);
    }
}