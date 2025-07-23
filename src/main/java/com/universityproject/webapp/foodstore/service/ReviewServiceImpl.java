package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Review;
import com.universityproject.webapp.foodstore.repository.ProductRepository;
import com.universityproject.webapp.foodstore.repository.ReviewRepository;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProductRepository productsRepository;

    @Override
    public Review addReview(Review review) {
        review.setBuyer(usersRepository.findById(review.getBuyer().getUserId()).orElseThrow());
        review.setSeller(usersRepository.findById(review.getSeller().getUserId()).orElseThrow());
        review.setProduct(productsRepository.findById(review.getProduct().getProductId()).orElseThrow());
        review.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsBySellerUserId(int sellerId) {
        return reviewRepository.findBySellerUserId(sellerId);
    }

    @Override
    public Double getAverageRatingBySellerUserId(int sellerId) {
        return reviewRepository.findAverageRatingBySellerUserId(sellerId);
    }
}




