package com.escrow.escrowbackend.controller;

import com.escrow.escrowbackend.common.ApiResponse;
import com.escrow.escrowbackend.dto.CreateEscrowRequest;
import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.service.EscrowService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/escrows")
@RequiredArgsConstructor
public class EscrowController {

    private final EscrowService escrowService;

    // ================= CREATE ESCROW (BUYER)
    @PreAuthorize("hasRole('BUYER')")
    @PostMapping
    public ResponseEntity<ApiResponse<Escrow>> createEscrow(
            Authentication authentication,
            @RequestBody CreateEscrowRequest request
    ) {

        String buyerEmail = authentication.getName();

        Escrow escrow = escrowService.createEscrow(
                buyerEmail,
                request.getSellerEmail(),
                request.getAmount(),
                request.getProductName()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Escrow created successfully", escrow)
        );
    }

    // ================= BUYER ESCROWS
    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer")
    public ResponseEntity<ApiResponse<List<Escrow>>> getBuyerEscrows(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Buyer escrows fetched",
                        escrowService.getEscrowsByBuyer(authentication.getName())
                )
        );
    }

    // ================= SELLER ESCROWS
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller")
    public ResponseEntity<ApiResponse<List<Escrow>>> getSellerEscrows(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Seller escrows fetched",
                        escrowService.getEscrowsBySeller(authentication.getName())
                )
        );
    }

    // ================= SELLER SHIPS
    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<Escrow>> shipProduct(
            @PathVariable String id,
            Authentication authentication
    ) {

        Escrow escrow =
                escrowService.updateShipment(id, authentication.getName());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product shipped successfully", escrow)
        );
    }

    // ================= BUYER CONFIRM DELIVERY
    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<Escrow>> confirmDelivery(
            @PathVariable String id,
            Authentication authentication
    ) {

        Escrow escrow =
                escrowService.confirmDelivery(id, authentication.getName());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Delivery confirmed", escrow)
        );
    }

    // ================= BUYER RELEASE PAYMENT
    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/{id}/release")
    public ResponseEntity<ApiResponse<Escrow>> releasePayment(
            @PathVariable String id,
            Authentication authentication
    ) {

        Escrow escrow =
                escrowService.releasePayment(id, authentication.getName());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment released successfully", escrow)
        );
    }
}