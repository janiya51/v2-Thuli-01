package com.life_insurance_system.service;

import com.life_insurance_system.model.PolicyDispute;
import com.life_insurance_system.repository.PolicyDisputeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyDisputeService {

    private final PolicyDisputeRepository policyDisputeRepository;

    @Autowired
    public PolicyDisputeService(PolicyDisputeRepository policyDisputeRepository) {
        this.policyDisputeRepository = policyDisputeRepository;
    }

    public List<PolicyDispute> getAllPolicyDisputes() {
        return policyDisputeRepository.findAll();
    }

    public PolicyDispute createPolicyDispute(PolicyDispute policyDispute) {
        return policyDisputeRepository.save(policyDispute);
    }

    public PolicyDispute getPolicyDisputeById(int id) {
        return policyDisputeRepository.findById(id).orElse(null);
    }

    public void deletePolicyDispute(int id) {
        policyDisputeRepository.deleteById(id);
    }

    public PolicyDispute updatePolicyDispute(PolicyDispute policyDispute) {
        return policyDisputeRepository.save(policyDispute);
    }
}