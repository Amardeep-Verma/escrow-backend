package com.escrow.escrowbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.entity.EscrowStatus;

public interface EscrowRepository extends MongoRepository<Escrow, String> {

    List<Escrow> findByBuyerEmail(String buyerEmail);

    List<Escrow> findBySellerEmail(String sellerEmail);

    Optional<Escrow> findByContractAddress(String contractAddress);

    long countByEscrowStatus(EscrowStatus status);
}
