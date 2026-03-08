package com.escrow.escrowbackend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.escrow.escrowbackend.entity.Evidence;

public interface EvidenceRepository extends MongoRepository<Evidence, String> {
    List<Evidence> findByDisputeId(String disputeId);
    List<Evidence> findByUploaderId(Long uploaderId);
}