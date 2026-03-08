package com.escrow.escrowbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.escrow.escrowbackend.entity.Dispute;

public interface DisputeRepository extends MongoRepository<Dispute, String> {
}