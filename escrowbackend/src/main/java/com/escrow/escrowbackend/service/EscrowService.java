package com.escrow.escrowbackend.service;

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
}