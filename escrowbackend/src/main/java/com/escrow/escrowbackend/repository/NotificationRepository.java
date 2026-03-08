package com.escrow.escrowbackend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.escrow.escrowbackend.entity.Notification;

public interface NotificationRepository
        extends MongoRepository<Notification, String> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    long countByUserIdAndReadFalse(String userId);

    long countByUserId(String userId);

    long countByUserIdAndReadTrue(String userId);

    void deleteByUserId(String userId);
}
