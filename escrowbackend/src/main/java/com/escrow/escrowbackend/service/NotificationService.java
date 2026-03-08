package com.escrow.escrowbackend.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.escrow.escrowbackend.entity.Notification;
import com.escrow.escrowbackend.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;

    public void createNotification(String userId, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        repo.save(notification);
    }

    public List<Notification> getUserNotifications(String userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(String userId) {
        return repo.countByUserIdAndReadFalse(userId);
    }

    public void markAllRead(String userId) {
        List<Notification> list = repo.findByUserIdOrderByCreatedAtDesc(userId);
        list.forEach(n -> n.setRead(true));
        repo.saveAll(list);
    }

    // ================= MARK AS READ =================
    public Notification markAsRead(String notificationId) {
        Notification notification = repo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        return repo.save(notification);
    }

    // ================= DELETE NOTIFICATION =================
    public void deleteNotification(String notificationId) {
        repo.deleteById(notificationId);
    }

    // ================= GET NOTIFICATIONS BY TYPE =================
    public List<Notification> getNotificationsByType(String userId, String type) {
        // This would typically query by notification type
        // For now, returning all notifications for the user
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ================= GET NOTIFICATIONS BY ESCROW =================
    public List<Notification> getNotificationsByEscrow(String userId, String escrowId) {
        // This would typically query by escrow ID
        // For now, returning all notifications for the user
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ================= SUBSCRIBE TO TOPICS =================
    public void subscribeToTopics(String userId, List<String> topics) {
        // This would typically update user preferences
        // For now, just a placeholder
    }

    // ================= UNSUBSCRIBE FROM TOPICS =================
    public void unsubscribeFromTopics(String userId, List<String> topics) {
        // This would typically update user preferences
        // For now, just a placeholder
    }

    // ================= GET NOTIFICATION SETTINGS =================
    public Map<String, Object> getNotificationSettings(String userId) {
        Map<String, Object> settings = new HashMap<>();
        settings.put("emailEnabled", true);
        settings.put("pushEnabled", true);
        settings.put("topics", List.of("escrow", "dispute", "payment"));
        return settings;
    }

    // ================= UPDATE NOTIFICATION SETTINGS =================
    public Map<String, Object> updateNotificationSettings(String userId, Map<String, Object> settings) {
        // This would typically save user preferences
        // For now, just return the provided settings
        return settings;
    }

    // ================= SEND TEST NOTIFICATION =================
    public void sendTestNotification(String userId, String message) {
        createNotification(userId, "TEST: " + message);
    }

    // ================= GET ANALYTICS =================
    public Map<String, Object> getAnalytics(String userId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalNotifications", repo.countByUserId(userId));
        analytics.put("unreadNotifications", repo.countByUserIdAndReadFalse(userId));
        analytics.put("readNotifications", repo.countByUserIdAndReadTrue(userId));
        return analytics;
    }

    // ================= CLEAR ALL NOTIFICATIONS =================
    public void clearAllNotifications(String userId) {
        repo.deleteByUserId(userId);
    }
}
