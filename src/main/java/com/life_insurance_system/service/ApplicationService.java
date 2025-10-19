package com.life_insurance_system.service;

import com.life_insurance_system.model.Application;
import com.life_insurance_system.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public Application createApplication(Application application) {
        if (application.getApplicationId() == 0) {
            application.setSubmissionDate(Timestamp.from(Instant.now()));
            application.setCurrentStatus(Application.ApplicationStatus.Submitted);
        }
        return applicationRepository.save(application);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAllWithUser();
    }

    @Transactional(readOnly = true)
    public Application getApplicationById(int id) {
        return applicationRepository.findByIdWithUser(id).orElse(null);
    }

    public Application updateApplication(Application application) {
        return applicationRepository.save(application);
    }
}