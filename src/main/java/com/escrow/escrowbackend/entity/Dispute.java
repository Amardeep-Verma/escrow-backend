package com.escrow.escrowbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "disputes")
public class Dispute {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "escrow_id", nullable = false)
    private Long escrowId;
    
    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;
    
    @Column(name = "seller_id", nullable = false)
    private Long sellerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeReason reason;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "evidence_url")
    private String evidenceUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeStatus status = DisputeStatus.OPEN;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolution_notes")
    private String resolutionNotes;
    
    // Constructors
    public Dispute() {}
    
    public Dispute(Long escrowId, Long buyerId, Long sellerId, DisputeReason reason, 
                   String description, String evidenceUrl) {
        this.escrowId = escrowId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.reason = reason;
        this.description = description;
        this.evidenceUrl = evidenceUrl;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getEscrowId() {
        return escrowId;
    }
    
    public void setEscrowId(Long escrowId) {
        this.escrowId = escrowId;
    }
    
    public Long getBuyerId() {
        return buyerId;
    }
    
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    
    public DisputeReason getReason() {
        return reason;
    }
    
    public void setReason(DisputeReason reason) {
        this.reason = reason;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getEvidenceUrl() {
        return evidenceUrl;
    }
    
    public void setEvidenceUrl(String evidenceUrl) {
        this.evidenceUrl = evidenceUrl;
    }
    
    public DisputeStatus getStatus() {
        return status;
    }
    
    public void setStatus(DisputeStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public String getResolutionNotes() {
        return resolutionNotes;
    }
    
    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
    
    // Enums
    public enum DisputeReason {
        FRAUD,
        ITEM_NOT_AS_DESCRIBED,
        ITEM_NOT_RECEIVED,
        SELLER_NOT_RESPONDING,
        OTHER
    }
    
    public enum DisputeStatus {
        OPEN,
        UNDER_REVIEW,
        RESOLVED_BUYER_WIN,
        RESOLVED_SELLER_WIN
    }
}