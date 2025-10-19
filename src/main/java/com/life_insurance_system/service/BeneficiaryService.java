package com.life_insurance_system.service;

import com.life_insurance_system.model.Beneficiary;
import com.life_insurance_system.model.Policy;
import com.life_insurance_system.repository.BeneficiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;

    @Autowired
    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public List<Beneficiary> getBeneficiariesByPolicy(Policy policy) {
        return beneficiaryRepository.findAll().stream()
                .filter(b -> b.getPolicy().getPolicyId() == policy.getPolicyId())
                .collect(Collectors.toList());
    }

    public Beneficiary createBeneficiary(Beneficiary beneficiary) {
        return beneficiaryRepository.save(beneficiary);
    }

    public void deleteBeneficiary(int id) {
        beneficiaryRepository.deleteById(id);
    }
}