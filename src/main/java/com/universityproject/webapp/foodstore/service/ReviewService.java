package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(Review review);
    List<Review> getReviewsBySellerUserId(int sellerId);
    Double getAverageRatingBySellerUserId(int sellerId);
}

