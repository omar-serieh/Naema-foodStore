package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.Cart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId")
    Optional<Cart> findByUser_UserId(@Param("userId") int userId);
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE cart  AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrementcart();


}