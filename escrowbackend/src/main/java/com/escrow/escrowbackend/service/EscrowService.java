package com.escrow.escrowbackend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.entity.EscrowStatus;
import com.escrow.escrowbackend.entity.ShipmentStatus;
import com.escrow.escrowbackend.entity.User;
import com.escrow.escrowbackend.repository.EscrowRepository;
import com.escrow.escrowbackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EscrowService {

    private final EscrowRepository escrowRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    // =========================
    // CREATE ESCROW
    // =========================
    public Escrow createEscrow(String buyerEmail,
                               String sellerEmail,
                               Double amount,
                               String productName,
                               String contractAddress) {

        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        Escrow escrow = Escrow.builder()
                .buyerEmail(buyerEmail)
                .sellerEmail(sellerEmail)
                .amount(amount)
                .productName(productName)
                .contractAddress(contractAddress)
                .createdAt(LocalDateTime.now())
                .shipmentStatus(ShipmentStatus.PENDING)
                .escrowStatus(EscrowStatus.CREATED)
                .build();

        Escrow saved = escrowRepository.save(escrow);

        // SEND EMAIL
        emailService.sendEscrowCreatedEmail(
                sellerEmail,
                productName,
                amount
        );

        // OPTIONAL: notify buyer too
        emailService.sendEscrowCreatedEmail(
                buyerEmail,
                productName,
                amount
        );

        webSocketService.sendEscrowUpdate(saved);

        return saved;
    }

    // =========================
    // FUND ESCROW (NEW)
    // =========================
    public Escrow fundEscrow(String escrowId) {

        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        EscrowStateMachine.fund(escrow);

        Escrow updated = escrowRepository.save(escrow);

        webSocketService.sendEscrowUpdate(updated);

        return updated;
    }

    // =========================
    // GET ESCROWS
    // =========================
    public List<Escrow> getEscrowsByBuyer(String email) {
        return escrowRepository.findByBuyerEmail(email);
    }

    public List<Escrow> getEscrowsBySeller(String email) {
        return escrowRepository.findBySellerEmail(email);
    }

    // =========================
    // SHIP PRODUCT
    // =========================
    public Escrow updateShipment(String escrowId, String sellerEmail) {

        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        EscrowStateMachine.ship(escrow);

        Escrow updated = escrowRepository.save(escrow);

        webSocketService.sendEscrowUpdate(updated);

        return updated;
    }

    // =========================
    // CONFIRM DELIVERY
    // =========================
    public Escrow confirmDelivery(String escrowId, String buyerEmail) {

        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        EscrowStateMachine.confirmDelivery(escrow);

        Escrow updated = escrowRepository.save(escrow);

        webSocketService.sendEscrowUpdate(updated);

        return updated;
    }

    // =========================
    // RELEASE PAYMENT
    // =========================
    public Escrow releasePayment(String escrowId, String buyerEmail) {

        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        EscrowStateMachine.release(escrow);

        Escrow updated = escrowRepository.save(escrow);

        webSocketService.sendEscrowUpdate(updated);

        return updated;
    }

    // =========================
    // GET ESCROW BY ID
    // =========================
    public Escrow getEscrowById(String escrowId) {
        return escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));
    }

    // =========================
    // GET ESCROW HISTORY
    // =========================
    public List<Map<String, Object>> getEscrowHistory(String escrowId) {
        // This would typically query an audit log or history table
        // For now, returning basic info
        Escrow escrow = getEscrowById(escrowId);
        List<Map<String, Object>> history = new ArrayList<>();
        Map<String, Object> entry = new HashMap<>();
        entry.put("status", escrow.getEscrowStatus());
        entry.put("shipmentStatus", escrow.getShipmentStatus());
        entry.put("updatedAt", escrow.getCreatedAt());
        history.add(entry);
        return history;
    }

    // =========================
    // CANCEL ESCROW
    // =========================
    public Escrow cancelEscrow(String escrowId, String userEmail) {
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        // Only buyer can cancel
        if (!escrow.getBuyerEmail().equals(userEmail)) {
            throw new RuntimeException("Only buyer can cancel escrow");
        }

        EscrowStateMachine.cancel(escrow);

        Escrow updated = escrowRepository.save(escrow);

        webSocketService.sendEscrowUpdate(updated);

        return updated;
    }

    // =========================
    // GET ESCROW ANALYTICS
    // =========================
    public Map<String, Object> getEscrowAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalEscrows", escrowRepository.count());
        analytics.put("activeEscrows", escrowRepository.countByEscrowStatus(EscrowStatus.FUNDED));
        analytics.put("completedEscrows", escrowRepository.countByEscrowStatus(EscrowStatus.RELEASED));
        analytics.put("cancelledEscrows", escrowRepository.countByEscrowStatus(EscrowStatus.CANCELLED));
        return analytics;
    }

    // =========================
    // GET ESCROW BY CONTRACT ADDRESS
    // =========================
    public Escrow getEscrowByContractAddress(String contractAddress) {
        return escrowRepository.findByContractAddress(contractAddress)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));
    }

    // =========================
    // UPDATE ESCROW METADATA
    // =========================
    public Escrow updateEscrowMetadata(String escrowId, Map<String, Object> metadata) {
        Escrow escrow = getEscrowById(escrowId);
        // In a real implementation, you'd have a metadata field in Escrow entity
        // For now, just return the escrow
        return escrow;
    }

    // =========================
    // GET ESCROW DISPUTES
    // =========================
    public List<Object> getEscrowDisputes(String escrowId) {
        // This would typically query a Dispute repository
        // For now, returning empty list
        return new ArrayList<>();
    }

    // =========================
    // FREEZE ESCROW
    // =========================
    public Escrow freezeEscrow(String escrowId, String reason) {
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        escrow.setEscrowStatus(EscrowStatus.FROZEN);
        escrow.setFrozenReason(reason);

        Escrow updated = escrowRepository.save(escrow);

        webSocketService.sendEscrowUpdate(updated);

        return updated;
    }

    // =========================
    // UNFREEZE ESCROW
    // =========================
    public Escrow unfreezeEscrow(String escrowId) {
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        escrow.setEscrowStatus(EscrowStatus.FUNDED);
        escrow.setFrozenReason(null);

        Escrow updated = escrowRepository.save(escrow);

        webSocketService.sendEscrowUpdate(updated);

        return updated;
    }

    // =========================
    // GET ALL ESCROWS
    // =========================
    public List<Escrow> getAllEscrows() {
        return escrowRepository.findAll();
    }

    // =========================
    // SEARCH ESCROWS
    // =========================
    public List<Escrow> searchEscrows(String query) {
        // This would typically query by product name, buyer email, or seller email
        // For now, returning all escrows
        return escrowRepository.findAll();
    }

    // =========================
    // BULK UPDATE ESCROWS
    // =========================
    public void bulkUpdateEscrows(List<String> escrowIds, String action) {
        List<Escrow> escrows = escrowRepository.findAllById(escrowIds);
        for (Escrow escrow : escrows) {
            if ("freeze".equals(action)) {
                escrow.setEscrowStatus(EscrowStatus.FROZEN);
            } else if ("unfreeze".equals(action)) {
                escrow.setEscrowStatus(EscrowStatus.FUNDED);
            }
        }
        escrowRepository.saveAll(escrows);
    }

    // =========================
    // EXPORT DATA
    // =========================
    public String exportData(String format) {
        // This would typically export data to the specified format
        // For now, returning a mock URL
        return "https://example.com/export/escrows." + format;
    }

    // =========================
    // GET AUDIT LOGS
    // =========================
    public List<Map<String, Object>> getAuditLogs() {
        // This would typically query an audit log table
        // For now, returning mock data
        List<Map<String, Object>> logs = new ArrayList<>();
        Map<String, Object> log = new HashMap<>();
        log.put("action", "escrow_created");
        log.put("timestamp", "2023-01-01T00:00:00Z");
        log.put("user", "admin");
        logs.add(log);
        return logs;
    }

    // =========================
    // GET SYSTEM HEALTH
    // =========================
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("database", "UP");
        health.put("cache", "UP");
        health.put("diskSpace", "UP");
        return health;
    }

    // =========================
    // CLEAR CACHE
    // =========================
    public void clearCache() {
        // This would typically clear application cache
        // For now, just a placeholder
    }

    // =========================
    // GET NOTIFICATIONS
    // =========================
    public List<Map<String, Object>> getNotifications() {
        // This would typically query a notifications table
        // For now, returning mock data
        List<Map<String, Object>> notifications = new ArrayList<>();
        Map<String, Object> notification = new HashMap<>();
        notification.put("id", "1");
        notification.put("message", "New escrow created");
        notification.put("read", false);
        notifications.add(notification);
        return notifications;
    }

    // =========================
    // MARK NOTIFICATION READ
    // =========================
    public void markNotificationRead(String notificationId) {
        // This would typically update the notification status
        // For now, just a placeholder
    }

    // =========================
    // BROADCAST NOTIFICATION
    // =========================
    public void broadcastNotification(String message) {
        // This would typically send a notification to all users
        // For now, just a placeholder
    }
}
