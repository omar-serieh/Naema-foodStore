package com.universityproject.webapp.foodstore.controller;

import com.universityproject.webapp.foodstore.entity.Notification;
import com.universityproject.webapp.foodstore.entity.Products;
import com.universityproject.webapp.foodstore.entity.Review;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import com.universityproject.webapp.foodstore.service.NotificationService;
import com.universityproject.webapp.foodstore.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReviewDTO dto) {
        System.out.println("📨 sellerId: " + dto.getSellerId());
        System.out.println("📦 productId: " + dto.getProductId());
        System.out.println("⭐ rating: " + dto.getRating());
        System.out.println("📝 comment: " + dto.getComment());

        Users buyer = usersRepository.findByEmail(userDetails.getUsername());

        Review review = new Review();
        review.setBuyer(buyer);

        Users seller = new Users();
        seller.setUserId(dto.getSellerId());
        review.setSeller(seller);

        Products product = new Products();
        product.setProductId(dto.getProductId());
        review.setProduct(product);

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review created = reviewService.addReview(review);
        Users sellerUser = usersRepository.findById(dto.getSellerId()).orElse(null);
        if (sellerUser != null) {
            String msg = String.format("%s left a review (⭐ %d): \"%s\"", buyer.getUserName(), dto.getRating(), dto.getComment());


            notificationService.createNotification(
                    new Notification(
                            sellerUser,
                            "⭐ New Review on Your Profile",
                            msg,
                            Notification.NotificationType.REVIEW
                    )
            );

        }

        return ResponseEntity.ok(created);
    }

    @GetMapping("/seller/{sellerId}/details")
    public ResponseEntity<SellerReviewDetails> getSellerDetails(@PathVariable int sellerId) {
        Double avg = reviewService.getAverageRatingBySellerUserId(sellerId);
        List<Review> reviews = reviewService.getReviewsBySellerUserId(sellerId);

        List<ReviewCommentDTO> reviewDtos = reviews.stream()
                .map(r -> new ReviewCommentDTO(
                        r.getBuyer().getUserName(),
                        r.getComment(),
                        r.getRating()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new SellerReviewDetails(avg != null ? avg : 0.0, reviewDtos));
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<SellerReviewDetails> getMyReviews(@AuthenticationPrincipal UserDetails user) {
        Users seller = usersRepository.findByEmail(user.getUsername());
        return getSellerDetails(seller.getUserId());
    }

    // ---------- DTOs موجودة فقط داخل الكنترولر ----------
    public static class ReviewDTO {
        private int sellerId;
        private int productId;
        private int rating;
        private String comment;
        // Getters and Setters

        public int getSellerId() {
            return sellerId;
        }

        public void setSellerId(int sellerId) {
            this.sellerId = sellerId;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class ReviewCommentDTO {
        private String buyerName;
        private String comment;
        private int rating;

        public ReviewCommentDTO(String buyerName, String comment, int rating) {
            this.buyerName = buyerName;
            this.comment = comment;
            this.rating = rating;
        }

        // Getters

        public String getBuyerName() {
            return buyerName;
        }

        public String getComment() {
            return comment;
        }

        public int getRating() {
            return rating;
        }
    }

    public static class SellerReviewDetails {
        private double averageRating;
        private List<ReviewCommentDTO> reviews;

        public SellerReviewDetails(double averageRating, List<ReviewCommentDTO> reviews) {
            this.averageRating = averageRating;
            this.reviews = reviews;
        }

        // Getters

        public double getAverageRating() {
            return averageRating;
        }

        public List<ReviewCommentDTO> getReviews() {
            return reviews;
        }
    }
}



