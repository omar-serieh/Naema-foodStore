package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillingRepository extends JpaRepository<Billing, Integer> {

    // Custom query to find billings for a specific userId
    @Query("SELECT b FROM Billing b WHERE b.cartId.user.userId = :userId")
    List<Billing> findByUserId(@Param("userId") int userId);
    @Query("SELECT b FROM Billing b WHERE b.cartId.cartId = :cartId")
    List<Billing> findByCartId_CartId(@Param("cartId") int cartId);
    List<Billing> findAllByUser_UserId(int userId);



}