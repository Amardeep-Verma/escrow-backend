package com.escrow.escrowbackend.controller;

import com.escrow.escrowbackend.entity.Notification;
import com.escrow.escrowbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable String userId) {
        return service.getUserNotifications(userId);
    }

    @GetMapping("/unread/{userId}")
    public long getUnread(@PathVariable String userId) {
        return service.getUnreadCount(userId);
    }

    @PostMapping("/read/{userId}")
    public void markRead(@PathVariable String userId) {
        service.markAllRead(userId);
    }
}