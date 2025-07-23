package com.universityproject.webapp.foodstore.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.feedback.to}")
    private String feedbackRecipient;

    @Override
    public void sendFeedback(String sellerEmail, String message, String reporterEmail) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(feedbackRecipient);
        mailMessage.setSubject("📢 شكوى على البائع: " + sellerEmail);
        mailMessage.setText(
                "📢 شكوى واردة\n" +
                        "البائع: " + sellerEmail + "\n" +
                        "من المستخدم: " + reporterEmail + "\n\n" +
                        "نص الشكوى:\n" + message
        );

        mailSender.send(mailMessage);
    }
}