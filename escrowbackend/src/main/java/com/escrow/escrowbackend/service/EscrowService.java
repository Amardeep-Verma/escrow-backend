package com.escrow.escrowbackend.service;

import com.escrow.escrowbackend.entity.Role;
import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.entity.User;
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

    // ✅ CREATE ESCROW (BUYER ONLY)
    public Escrow createEscrow(String buyerEmail,
                               String sellerEmail,
                               Double amount,
                               String productName) {

        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        if (buyer.getRole() != Role.ROLE_BUYER) {
            throw new RuntimeException("Only BUYER can create escrow");
        }

        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        if (seller.getRole() != Role.ROLE_SELLER) {
            throw new RuntimeException("Seller must have SELLER role");
        }

        Escrow escrow = Escrow.builder()
                .buyerEmail(buyerEmail)
                .sellerEmail(sellerEmail)
                .amount(amount)
                .productName(productName)
                .shipmentStatus("PENDING")
                .escrowStatus("CREATED")
                .createdAt(LocalDateTime.now())
                .build();

        return escrowRepository.save(escrow);
    }

    // ✅ GET ESCROWS FOR BUYER
    public List<Escrow> getEscrowsByBuyer(String email) {
        return escrowRepository.findByBuyerEmail(email);
    }

    // ✅ GET ESCROWS FOR SELLER
    public List<Escrow> getEscrowsBySeller(String email) {
        return escrowRepository.findBySellerEmail(email);
    }

    // ✅ SELLER SHIPS PRODUCT
    public Escrow updateShipment(String escrowId, String sellerEmail) {

        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        if (!escrow.getSellerEmail().equals(sellerEmail)) {
            throw new RuntimeException("Only assigned seller can update shipment");
        }

        // workflow validation
        if (!escrow.getEscrowStatus().equals("CREATED")) {
            throw new RuntimeException("Escrow must be CREATED before shipping");
        }

        if (escrow.getShipmentStatus().equals("SHIPPED")) {
            throw new RuntimeException("Product already shipped");
        }

        // ✅ update shipment
        escrow.setShipmentStatus("SHIPPED");

        // ✅ IMPORTANT FIX — MOVE ESCROW STATE
        escrow.setEscrowStatus("FUNDED");

        return escrowRepository.save(escrow);
    }

    // ✅ BUYER CONFIRMS DELIVERY
    public Escrow confirmDelivery(String escrowId, String buyerEmail) {

        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        if (!escrow.getBuyerEmail().equals(buyerEmail)) {
            throw new RuntimeException("Only buyer can confirm delivery");
        }

        if (!escrow.getShipmentStatus().equals("SHIPPED")) {
            throw new RuntimeException("Product must be shipped first");
        }

        if (escrow.getEscrowStatus().equals("RELEASED")) {
            throw new RuntimeException("Escrow already completed");
        }

        // ✅ update delivery
        escrow.setShipmentStatus("DELIVERED");

        // ✅ release payment
        escrow.setEscrowStatus("RELEASED");

        return escrowRepository.save(escrow);
    }
}