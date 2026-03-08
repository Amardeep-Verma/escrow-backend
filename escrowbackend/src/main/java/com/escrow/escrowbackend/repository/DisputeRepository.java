package com.escrow.escrowbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.escrow.escrowbackend.entity.Dispute;

@Repository
public interface DisputeRepository extends MongoRepository<Dispute, String> {
    
    // Find disputes by escrow ID
    List<Dispute> findByEscrowId(Long escrowId);
    
    // Find disputes by buyer ID
    List<Dispute> findByBuyerId(Long buyerId);
    
    // Find disputes by seller ID
    List<Dispute> findBySellerId(Long sellerId);
    
    // Find disputes by status
    List<Dispute> findByStatus(Dispute.DisputeStatus status);
    
    // Find open disputes
    List<Dispute> findByStatusOrderByCreatedAtDesc(Dispute.DisputeStatus status);
    
    List<Dispute> findByEscrowIdOrderByCreatedAtDesc(Long escrowId);
    
    List<Dispute> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    
    List<Dispute> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    
    // Check if there's an open dispute for an escrow
    Optional<Dispute> findFirstByEscrowIdAndStatus(Long escrowId, Dispute.DisputeStatus status);
    
    // Custom query to get all disputes for admin panel
    @Query(sort = "{ 'createdAt' : -1 }")
    List<Dispute> findAllByOrderByCreatedAtDesc();
    
    // Find disputes by reason
    List<Dispute> findByReason(Dispute.DisputeReason reason);
}