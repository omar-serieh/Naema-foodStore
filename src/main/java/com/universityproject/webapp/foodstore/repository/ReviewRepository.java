package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.seller.userId = :sellerId")
    List<Review> findBySellerUserId(@Param("sellerId") int sellerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.seller.userId = :sellerId")
    Double findAverageRatingBySellerUserId(@Param("sellerId") int sellerId);
}
