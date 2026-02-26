package com.escrow.escrowbackend.service;

import com.escrow.escrowbackend.entity.Notification;
import com.escrow.escrowbackend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
}