package com.escrow.escrowbackend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.escrow.escrowbackend.entity.ChatMessage;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByEscrowId(String escrowId);
    List<ChatMessage> findBySenderId(Long senderId);
    List<ChatMessage> findByReceiverId(Long receiverId);
}