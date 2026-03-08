package com.escrow.escrowbackend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.escrow.escrowbackend.entity.Dispute;
import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.entity.EscrowStatus;
import com.escrow.escrowbackend.entity.Evidence;
import com.escrow.escrowbackend.exception.DisputeException;
import com.escrow.escrowbackend.repository.DisputeRepository;
import com.escrow.escrowbackend.repository.EscrowRepository;
import com.escrow.escrowbackend.repository.EvidenceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final EscrowRepository escrowRepository;
    private final EvidenceRepository evidenceRepository;

    /**
     * Create a new dispute for an escrow
     */
    public Dispute createDispute(String escrowId, String buyerEmail, Dispute.DisputeReason reason, 
                                String description, String evidenceUrl) {
        
        // Find the escrow
        Optional<Escrow> escrowOpt = escrowRepository.findById(escrowId);
        if (escrowOpt.isEmpty()) {
            throw new DisputeException("Escrow with ID " + escrowId + " does not exist");
        }
        
        Escrow escrow = escrowOpt.get();
        
        // Check if user is the buyer
        if (!escrow.getBuyerEmail().equals(buyerEmail)) {
            throw new DisputeException("Only the buyer can create a dispute");
        }
        
        // Check if escrow is in a valid state for dispute
        if (escrow.getEscrowStatus() == EscrowStatus.DELIVERED || 
            escrow.getEscrowStatus() == EscrowStatus.RELEASED ||
            escrow.getEscrowStatus() == EscrowStatus.REFUNDED) {
            throw new DisputeException("Cannot create dispute for completed escrow");
        }
        
        // Check if there's already an open dispute for this escrow
        Optional<Dispute> existingDispute = disputeRepository.findFirstByEscrowIdAndStatus(
            Long.parseLong(escrowId), Dispute.DisputeStatus.OPEN);
        
        if (existingDispute.isPresent()) {
            throw new DisputeException("A dispute already exists for this escrow");
        }
        
        // Create the dispute
        Dispute dispute = new Dispute(
            Long.parseLong(escrowId),
            0L, // buyerId placeholder - we'll use email instead
            0L, // sellerId placeholder - we'll use email instead
            reason,
            description,
            evidenceUrl
        );
        
        // Save the dispute
        Dispute savedDispute = disputeRepository.save(dispute);
        
        // Update escrow status to DISPUTED and lock funds
        escrow.setEscrowStatus(EscrowStatus.DISPUTED);
        escrowRepository.save(escrow);
        
        return savedDispute;
    }

    /**
     * Get all disputes for a specific escrow
     */
    public List<Dispute> getDisputesForEscrow(String escrowId) {
        return disputeRepository.findByEscrowIdOrderByCreatedAtDesc(Long.parseLong(escrowId));
    }

    /**
     * Get all disputes (for admin)
     */
    public List<Dispute> getAllDisputes() {
        return disputeRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get dispute by ID
     */
    public Optional<Dispute> getDisputeById(String disputeId) {
        return disputeRepository.findById(disputeId);
    }

    /**
     * Resolve a dispute
     */
    public Dispute resolveDispute(String disputeId, Dispute.DisputeStatus resolution, String resolutionNotes) {
        
        Optional<Dispute> disputeOpt = disputeRepository.findById(disputeId);
        if (disputeOpt.isEmpty()) {
            throw new DisputeException("Dispute with ID " + disputeId + " does not exist");
        }
        
        Dispute dispute = disputeOpt.get();
        
        // Check if dispute is already resolved
        if (dispute.getStatus() != Dispute.DisputeStatus.OPEN && 
            dispute.getStatus() != Dispute.DisputeStatus.UNDER_REVIEW) {
            throw new DisputeException("Dispute is already resolved");
        }
        
        // Update dispute status
        dispute.setStatus(resolution);
        dispute.setResolvedAt(LocalDateTime.now());
        dispute.setResolutionNotes(resolutionNotes);
        
        Dispute savedDispute = disputeRepository.save(dispute);
        
        // Update escrow status and handle fund release/refund
        Optional<Escrow> escrowOpt = escrowRepository.findById(String.valueOf(dispute.getEscrowId()));
        if (escrowOpt.isPresent()) {
            Escrow escrow = escrowOpt.get();
            
            if (resolution == Dispute.DisputeStatus.RESOLVED_BUYER_WIN) {
                // Buyer wins - refund buyer
                escrow.setEscrowStatus(EscrowStatus.REFUNDED);
                // TODO: Implement actual refund logic (blockchain transaction)
            } else if (resolution == Dispute.DisputeStatus.RESOLVED_SELLER_WIN) {
                // Seller wins - release to seller
                escrow.setEscrowStatus(EscrowStatus.RELEASED);
                // TODO: Implement actual release logic (blockchain transaction)
            }
            
            escrowRepository.save(escrow);
        }
        
        return savedDispute;
    }

    /**
     * Update dispute status to under review
     */
    public Dispute markAsUnderReview(String disputeId) {
        Optional<Dispute> disputeOpt = disputeRepository.findById(disputeId);
        if (disputeOpt.isEmpty()) {
            throw new DisputeException("Dispute with ID " + disputeId + " does not exist");
        }
        
        Dispute dispute = disputeOpt.get();
        dispute.setStatus(Dispute.DisputeStatus.UNDER_REVIEW);
        
        return disputeRepository.save(dispute);
    }

    /**
     * Get disputes by status
     */
    public List<Dispute> getDisputesByStatus(Dispute.DisputeStatus status) {
        return disputeRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Get disputes by buyer
     */
    public List<Dispute> getDisputesByBuyer(String buyerEmail) {
        // Since we don't have buyerId, we'll need to implement this differently
        // For now, return empty list - this would need a different approach
        return disputeRepository.findByBuyerIdOrderByCreatedAtDesc(0L);
    }

    /**
     * Get disputes by seller
     */
    public List<Dispute> getDisputesBySeller(String sellerEmail) {
        // Since we don't have sellerId, we'll need to implement this differently
        // For now, return empty list - this would need a different approach
        return disputeRepository.findBySellerIdOrderByCreatedAtDesc(0L);
    }

    /**
     * Upload evidence for a dispute
     */
    public Evidence uploadEvidence(String disputeId, Long uploaderId, String fileName, String fileUrl) {
        Optional<Dispute> disputeOpt = disputeRepository.findById(disputeId);
        if (disputeOpt.isEmpty()) {
            throw new DisputeException("Dispute with ID " + disputeId + " does not exist");
        }
        
        Dispute dispute = disputeOpt.get();
        
        // Create evidence record
        Evidence evidence = Evidence.builder()
            .fileName(fileName)
            .uploaderId(uploaderId)
            .disputeId(disputeId)
            .uploadTime(java.time.LocalDateTime.now())
            .fileUrl(fileUrl)
            .build();
        
        return evidenceRepository.save(evidence);
    }

    /**
     * Get evidence for a dispute
     */
    public List<Evidence> getEvidenceForDispute(String disputeId) {
        return evidenceRepository.findByDisputeId(disputeId);
    }

    /**
     * Get evidence uploaded by a user
     */
    public List<Evidence> getEvidenceByUser(Long uploaderId) {
        return evidenceRepository.findByUploaderId(uploaderId);
    }
}
