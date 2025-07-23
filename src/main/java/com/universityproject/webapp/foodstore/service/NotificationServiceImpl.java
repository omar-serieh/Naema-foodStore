package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.controller.NotificationController;
import com.universityproject.webapp.foodstore.entity.Notification;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.repository.NotificationRepository;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // ✅ WebSocket

    @Override
    public Notification createNotification(Notification notification) {
        Users user = usersRepository.findById(notification.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        notification.setUser(user);
        notification.setIsRead(false);
        notification.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        Notification saved = notificationRepository.save(notification);

        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/notifications",
                new NotificationController.NotificationResponseDTO(
                        saved.getNotificationId(),
                        saved.getTitle(),
                        saved.getMessage(),
                        saved.getIsRead(),
                        saved.getType().name(),
                        saved.getCreatedAt(),
                        saved.getUser().getUserName()
                )
        );


        return saved;
    }

    @Override
    public List<Notification> getNotificationsByUserId(int userId) {
        return notificationRepository.findAllByUserId(userId);
    }

    @Override
    public List<Notification> getUnreadNotificationsByUserId(int userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }

    @Override
    public Notification markAsRead(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }
    @Transactional
    @Override
    public void markAllAsReadByUserId(int userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

}


