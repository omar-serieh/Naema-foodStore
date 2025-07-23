package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.PointsSystem;
import com.universityproject.webapp.foodstore.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsSystemRepository extends JpaRepository<PointsSystem, Integer> {
    @Query("SELECT p FROM PointsSystem p WHERE p.userId = :user")
    PointsSystem findByUserId(@Param("user") Users user);
}