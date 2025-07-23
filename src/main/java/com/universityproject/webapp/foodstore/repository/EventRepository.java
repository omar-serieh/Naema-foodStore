package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Events, Integer> {
}


