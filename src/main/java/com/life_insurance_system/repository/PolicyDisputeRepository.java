package com.life_insurance_system.repository;

import com.life_insurance_system.model.PolicyDispute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyDisputeRepository extends JpaRepository<PolicyDispute, Integer> {
}