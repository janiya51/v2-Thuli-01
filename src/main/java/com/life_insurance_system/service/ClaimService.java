package com.life_insurance_system.service;

import com.life_insurance_system.model.Claim;
import com.life_insurance_system.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    @Autowired
    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Claim createClaim(Claim claim) {
        claim.setFilingDate(new java.sql.Timestamp(System.currentTimeMillis()));
        claim.setClaimStatus(Claim.ClaimStatus.Filed);
        return claimRepository.save(claim);
    }

    public Claim getClaimById(int id) {
        return claimRepository.findById(id).orElse(null);
    }

    public Claim updateClaim(Claim claim) {
        return claimRepository.save(claim);
    }
}