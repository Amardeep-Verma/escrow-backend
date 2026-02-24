package com.escrow.escrowbackend.repository;

import com.escrow.escrowbackend.entity.Escrow;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EscrowRepository extends MongoRepository<Escrow, String> {

    List<Escrow> findByBuyerEmail(String buyerEmail);

    List<Escrow> findBySellerEmail(String sellerEmail);
}