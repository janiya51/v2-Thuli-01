package com.life_insurance_system.service;

import com.life_insurance_system.model.Policy;
import com.life_insurance_system.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    @Autowired
    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Policy createPolicy(Policy policy) {
        return policyRepository.save(policy);
    }

    public List<Policy> getPoliciesByUser(com.life_insurance_system.model.User user) {
        return policyRepository.findByApplicationUser(user);
    }

    public Policy getPolicyById(int id) {
        return policyRepository.findById(id).orElse(null);
    }

    public List<Policy> getActivePoliciesByUser(com.life_insurance_system.model.User user) {
        return policyRepository.findByApplicationUser(user).stream()
                .filter(p -> p.getPolicyStatus() == Policy.PolicyStatus.Active)
                .collect(java.util.stream.Collectors.toList());
    }
}