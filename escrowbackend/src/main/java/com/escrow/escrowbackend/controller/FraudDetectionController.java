package com.escrow.escrowbackend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escrow.escrowbackend.entity.Dispute;
import com.escrow.escrowbackend.entity.User;
import com.escrow.escrowbackend.repository.DisputeRepository;
import com.escrow.escrowbackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
public class FraudDetectionController {

    private final DisputeRepository disputeRepository;
    private final UserRepository userRepository;

    @GetMapping("/score/{userId}")
    public ResponseEntity<Map<String, Object>> getFraudScore(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        
        // Get user
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.put("error", "User not found");
            return ResponseEntity.notFound().build();
        }

        // Calculate fraud score based on disputes
        List<Dispute> userDisputes = disputeRepository.findByBuyerId(Long.valueOf(userId)) != null ? 
            disputeRepository.findByBuyerId(Long.valueOf(userId)) : 
            disputeRepository.findBySellerId(Long.valueOf(userId));
        
        int disputeCount = userDisputes != null ? userDisputes.size() : 0;
        
        // Simple fraud scoring logic
        int fraudScore = Math.min(disputeCount * 10, 100);
        
        // Add suspicious activity detection
        if (disputeCount > 5) {
            fraudScore += 20;
        }
        
        if (user.isBanned()) {
            fraudScore = 100;
        }

        response.put("userId", userId);
        response.put("fraudScore", Math.min(fraudScore, 100));
        response.put("disputeCount", disputeCount);
        response.put("status", user.isBanned() ? "BANNED" : "ACTIVE");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getFlaggedUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> flaggedUsers = users.stream()
            .filter(user -> {
                // Flag users with high dispute count or banned status
                List<Dispute> userDisputes = disputeRepository.findByBuyerId(user.getId() != null ? Long.valueOf(user.getId()) : 0L) != null ? 
                    disputeRepository.findByBuyerId(user.getId() != null ? Long.valueOf(user.getId()) : 0L) : 
                    disputeRepository.findBySellerId(user.getId() != null ? Long.valueOf(user.getId()) : 0L);
                int disputeCount = userDisputes != null ? userDisputes.size() : 0;
                return disputeCount > 3 || user.isBanned();
            })
            .map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", user.getId());
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                userMap.put("role", user.getRole());
                userMap.put("banned", user.isBanned());
                userMap.put("banReason", user.getBanReason());
                
                // Calculate fraud score for flagged user
                List<Dispute> userDisputes = disputeRepository.findByBuyerId(user.getId() != null ? Long.valueOf(user.getId()) : 0L) != null ? 
                    disputeRepository.findByBuyerId(user.getId() != null ? Long.valueOf(user.getId()) : 0L) : 
                    disputeRepository.findBySellerId(user.getId() != null ? Long.valueOf(user.getId()) : 0L);
                int disputeCount = userDisputes != null ? userDisputes.size() : 0;
                int fraudScore = Math.min(disputeCount * 10, 100);
                if (user.isBanned()) fraudScore = 100;
                
                userMap.put("fraudScore", fraudScore);
                userMap.put("disputeCount", disputeCount);
                return userMap;
            })
            .toList();

        return ResponseEntity.ok(flaggedUsers);
    }
}