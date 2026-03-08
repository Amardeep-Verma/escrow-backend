package com.escrow.escrowbackend.controller;

import com.escrow.escrowbackend.common.ApiResponse;
import com.escrow.escrowbackend.entity.Notification;
import com.escrow.escrowbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationEnhancedController {

    private final NotificationService notificationService;

    // ================= MARK AS READ =================
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(
            @PathVariable String notificationId
    ) {
        Notification notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notification marked as read", notification)
        );
    }

    // ================= DELETE NOTIFICATION =================
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(
            @PathVariable String notificationId
    ) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notification deleted", "Success")
        );
    }

    // ================= GET NOTIFICATIONS BY TYPE =================
    @GetMapping("/{userId}/type/{type}")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotificationsByType(
            @PathVariable String userId,
            @PathVariable String type
    ) {
        List<Notification> notifications = notificationService.getNotificationsByType(userId, type);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notifications by type retrieved", notifications)
        );
    }

    // ================= GET NOTIFICATIONS BY ESCROW =================
    @GetMapping("/{userId}/escrow/{escrowId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotificationsByEscrow(
            @PathVariable String userId,
            @PathVariable String escrowId
    ) {
        List<Notification> notifications = notificationService.getNotificationsByEscrow(userId, escrowId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notifications by escrow retrieved", notifications)
        );
    }

    // ================= SUBSCRIBE TO NOTIFICATIONS =================
    @PostMapping("/{userId}/subscribe")
    @PreAuthorize("hasRole('BUYER') or hasRole('SELLER')")
    public ResponseEntity<ApiResponse<String>> subscribeToNotifications(
            @PathVariable String userId,
            @RequestBody Map<String, List<String>> request
    ) {
        List<String> topics = request.get("topics");
        notificationService.subscribeToTopics(userId, topics);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Subscribed to notifications", "Success")
        );
    }

    // ================= UNSUBSCRIBE FROM NOTIFICATIONS =================
    @PostMapping("/{userId}/unsubscribe")
    @PreAuthorize("hasRole('BUYER') or hasRole('SELLER')")
    public ResponseEntity<ApiResponse<String>> unsubscribeFromNotifications(
            @PathVariable String userId,
            @RequestBody Map<String, List<String>> request
    ) {
        List<String> topics = request.get("topics");
        notificationService.unsubscribeFromTopics(userId, topics);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Unsubscribed from notifications", "Success")
        );
    }

    // ================= GET NOTIFICATION SETTINGS =================
    @GetMapping("/{userId}/settings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotificationSettings(
            @PathVariable String userId
    ) {
        Map<String, Object> settings = notificationService.getNotificationSettings(userId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notification settings retrieved", settings)
        );
    }

    // ================= UPDATE NOTIFICATION SETTINGS =================
    @PutMapping("/{userId}/settings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateNotificationSettings(
            @PathVariable String userId,
            @RequestBody Map<String, Object> settings
    ) {
        Map<String, Object> updatedSettings = notificationService.updateNotificationSettings(userId, settings);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notification settings updated", updatedSettings)
        );
    }

    // ================= SEND TEST NOTIFICATION =================
    @PostMapping("/{userId}/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> sendTestNotification(
            @PathVariable String userId,
            @RequestBody Map<String, String> request
    ) {
        String message = request.get("message");
        notificationService.sendTestNotification(userId, message);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Test notification sent", "Success")
        );
    }

    // ================= GET NOTIFICATION ANALYTICS =================
    @GetMapping("/{userId}/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotificationAnalytics(
            @PathVariable String userId
    ) {
        Map<String, Object> analytics = notificationService.getAnalytics(userId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notification analytics retrieved", analytics)
        );
    }

    // ================= CLEAR ALL NOTIFICATIONS =================
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<ApiResponse<String>> clearAllNotifications(
            @PathVariable String userId
    ) {
        notificationService.clearAllNotifications(userId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "All notifications cleared", "Success")
        );
    }
}