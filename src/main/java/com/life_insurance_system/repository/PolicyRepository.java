package com.life_insurance_system.repository;

import com.life_insurance_system.model.Policy;
import com.life_insurance_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {
    List<Policy> findByApplicationUser(User user);
}