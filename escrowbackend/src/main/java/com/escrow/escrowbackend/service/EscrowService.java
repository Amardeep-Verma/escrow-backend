package com.escrow.escrowbackend.service;

import com.escrow.escrowbackend.entity.EscrowStatus;
import com.escrow.escrowbackend.entity.ShipmentStatus;
import com.escrow.escrowbackend.entity.*;
import com.escrow.escrowbackend.repository.EscrowRepository;
import com.escrow.escrowbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EscrowService {

    private final EscrowRepository escrowRepository;
    private final UserRepository userRepository;

    // =========================
    // CREATE ESCROW
    // =========================
    public Escrow createEscrow(String buyerEmail,
                               String sellerEmail,
                               Double amount,
                               String productName) {

        Escrow escrow = Escrow.builder()
                .buyerEmail(buyerEmail)
                .sellerEmail(sellerEmail)
                .amount(amount)
                .productName(productName)
                .createdAt(LocalDateTime.now())
                .shipmentStatus(ShipmentStatus.PENDING)
                .escrowStatus(EscrowStatus.CREATED)
                .build();

        return escrowRepository.save(escrow);
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

        if (!escrow.getSellerEmail().equals(sellerEmail)) {
            throw new RuntimeException("Unauthorized seller");
        }

        // ⭐ STATE MACHINE
        EscrowStateMachine.ship(escrow);

        return escrowRepository.save(escrow);
    }

    // =========================
    // CONFIRM DELIVERY
    // =========================
    public Escrow confirmDelivery(String escrowId, String buyerEmail) {

        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        if (!escrow.getBuyerEmail().equals(buyerEmail)) {
            throw new RuntimeException("Unauthorized buyer");
        }

        // ⭐ STATE MACHINE
        EscrowStateMachine.confirmDelivery(escrow);

        return escrowRepository.save(escrow);
    }
}