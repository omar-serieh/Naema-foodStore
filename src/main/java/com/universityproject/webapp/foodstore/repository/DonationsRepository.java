package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.Donations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonationsRepository extends JpaRepository<Donations, Integer> {

}
