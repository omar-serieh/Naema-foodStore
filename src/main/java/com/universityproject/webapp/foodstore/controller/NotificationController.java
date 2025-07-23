package com.universityproject.webapp.foodstore.controller;

import com.universityproject.webapp.foodstore.entity.Notification;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.service.NotificationService;
import com.universityproject.webapp.foodstore.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UsersService usersService;

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NotificationRequestDTO dto) {

        Users user = usersService.getUserByEmail(userDetails.getUsername());

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setType(Notification.NotificationType.valueOf(dto.getType().toUpperCase()));

        Notification saved = notificationService.createNotification(notification);

        return ResponseEntity.ok(toResponseDTO(saved));
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        Users user = usersService.getUserByEmail(userDetails.getUsername());
        List<Notification> list = notificationService.getNotificationsByUserId(user.getUserId());

        return ResponseEntity.ok(list.stream().map(this::toResponseDTO).toList());
    }

    @GetMapping("/me/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getMyUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        Users user = usersService.getUserByEmail(userDetails.getUsername());
        List<Notification> list = notificationService.getUnreadNotificationsByUserId(user.getUserId());

        return ResponseEntity.ok(list.stream().map(this::toResponseDTO).toList());
    }

    @PutMapping("/read/{notificationId}")
    public ResponseEntity<Notification> markAsRead(@PathVariable int notificationId) {
        return ResponseEntity.ok(notificationService.markAsRead(notificationId));
    }

    private NotificationResponseDTO toResponseDTO(Notification n) {
        return new NotificationResponseDTO(
                n.getNotificationId(),
                n.getTitle(),
                n.getMessage(),
                n.getIsRead(),
                n.getType().name(),
                n.getCreatedAt(),
                n.getUser().getUserName()
        );
    }
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = usersService.getUserByEmail(userDetails.getUsername());
        notificationService.markAllAsReadByUserId(user.getUserId());
        return ResponseEntity.ok().build();
    }


    public static class NotificationRequestDTO {
        private String title;
        private String message;
        private String type;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    // 📨 DTO داخلي - الرد
    public static class NotificationResponseDTO {
        private int id;
        private String title;
        private String message;
        private boolean isRead;
        private String type;
        private Timestamp createdAt;
        private String userName;

        public NotificationResponseDTO(int id, String title, String message, boolean isRead,
                                       String type, Timestamp createdAt, String userName) {
            this.id = id;
            this.title = title;
            this.message = message;
            this.isRead = isRead;
            this.type = type;
            this.createdAt = createdAt;
            this.userName = userName;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public boolean isRead() { return isRead; }
        public String getType() { return type; }
        public Timestamp getCreatedAt() { return createdAt; }
        public String getUserName() { return userName; }
    }
}
