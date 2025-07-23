package com.universityproject.webapp.foodstore.controller;


import com.universityproject.webapp.foodstore.service.FeedbackService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<String> submitFeedback(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FeedbackRequest request) {

        String reporterEmail = userDetails.getUsername();
        feedbackService.sendFeedback(request.getSellerEmail(), request.getMessage(), reporterEmail);
        return ResponseEntity.ok("Your complaint has been submitted successfully.");

    }

    public static class FeedbackRequest {
        private String sellerEmail;
        private String message;

        public String getSellerEmail() {
            return sellerEmail;
        }

        public void setSellerEmail(String sellerEmail) {
            this.sellerEmail = sellerEmail;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}