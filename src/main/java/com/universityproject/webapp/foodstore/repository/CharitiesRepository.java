package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.Charities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharitiesRepository extends JpaRepository<Charities, Integer> {
    // Custom query methods

    // Find a charity by its name
    Optional<Charities> findByCharityName(String charityName);

    // Check if a charity exists by its name
    boolean existsByCharityName(String charityName);
}
