package com.escrow.escrowbackend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escrow.escrowbackend.entity.ChatMessage;
import com.escrow.escrowbackend.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody ChatMessage message) {
        Map<String, Object> response = new HashMap<>();
        
        // Validate required fields
        if (message.getSenderId() == null || message.getReceiverId() == null || 
            message.getEscrowId() == null || message.getMessage() == null || 
            message.getMessage().trim().isEmpty()) {
            response.put("error", "Missing required fields: senderId, receiverId, escrowId, message");
            return ResponseEntity.badRequest().body(response);
        }

        // Set timestamp
        message.setTimestamp(java.time.LocalDateTime.now());
        
        // Save message
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        response.put("messageId", savedMessage.getId());
        response.put("status", "Message sent successfully");
        response.put("timestamp", savedMessage.getTimestamp());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{escrowId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String escrowId) {
        List<ChatMessage> messages = chatMessageRepository.findByEscrowId(escrowId);
        return ResponseEntity.ok(messages);
    }
}