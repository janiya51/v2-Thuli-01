// Corrected ApplicationRepository.java

package com.life_insurance_system.repository;

import com.life_insurance_system.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // <-- Make sure this import is added
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    @Query("SELECT a FROM Application a LEFT JOIN FETCH a.user WHERE a.applicationId = :id")
    Optional<Application> findByIdWithUser(@Param("id") int id);

    @Query("SELECT a FROM Application a LEFT JOIN FETCH a.user")
    List<Application> findAllWithUser();
}