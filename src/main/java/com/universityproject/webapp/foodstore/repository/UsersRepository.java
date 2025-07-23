package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.Products;
import com.universityproject.webapp.foodstore.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Users findByEmail(String email);
    boolean existsByEmail(String email);
}


