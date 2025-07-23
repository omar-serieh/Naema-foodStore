package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.Notification;

import java.util.List;

public interface NotificationService {

    // إرسال إشعار جديد لمستخدم
    Notification createNotification(Notification notification);

    // جلب كل الإشعارات لمستخدم
    List<Notification> getNotificationsByUserId(int userId);

    // جلب الإشعارات غير المقروءة
    List<Notification> getUnreadNotificationsByUserId(int userId);

    // تعليم إشعار كمقروء
    Notification markAsRead(int notificationId);
    void markAllAsReadByUserId(int userId);

}

