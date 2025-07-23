package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.Subscriptions;
import com.universityproject.webapp.foodstore.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscriptions, Integer> {
    Optional<Subscriptions> findByUserId(Users user);
}
