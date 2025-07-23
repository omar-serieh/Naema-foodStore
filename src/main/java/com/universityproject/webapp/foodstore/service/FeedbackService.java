package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Feedback;
import jakarta.mail.MessagingException;
import java.util.Date;

public interface FeedbackService {
    void sendFeedback(String sellerEmail, String message, String reporterEmail);

}