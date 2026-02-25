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
@RequestMapping("/api/escrows")   // ⭐ plural REST naming
@RequiredArgsConstructor
public class EscrowController {

    private final EscrowService escrowService;

    // =====================================================
    // ✅ CREATE ESCROW (BUYER ONLY)
    // =====================================================
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

    // =====================================================
    // ✅ BUYER → VIEW HIS ESCROWS
    // =====================================================
    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer")
    public ResponseEntity<ApiResponse<List<Escrow>>> getBuyerEscrows(
            Authentication authentication
    ) {

        String buyerEmail = authentication.getName();

        List<Escrow> escrows =
                escrowService.getEscrowsByBuyer(buyerEmail);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Buyer escrows fetched", escrows)
        );
    }

    // =====================================================
    // ✅ SELLER → VIEW HIS ESCROWS
    // =====================================================
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller")
    public ResponseEntity<ApiResponse<List<Escrow>>> getSellerEscrows(
            Authentication authentication
    ) {

        String sellerEmail = authentication.getName();

        List<Escrow> escrows =
                escrowService.getEscrowsBySeller(sellerEmail);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Seller escrows fetched", escrows)
        );
    }

    // =====================================================
    // ✅ SELLER SHIPS PRODUCT
    // =====================================================
    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<Escrow>> shipProduct(
            @PathVariable String id,
            Authentication authentication
    ) {

        String sellerEmail = authentication.getName();

        Escrow escrow =
                escrowService.updateShipment(id, sellerEmail);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product shipped successfully", escrow)
        );
    }

    // =====================================================
    // ✅ BUYER CONFIRMS DELIVERY
    // =====================================================
    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<Escrow>> confirmDelivery(
            @PathVariable String id,
            Authentication authentication
    ) {

        String buyerEmail = authentication.getName();

        Escrow escrow =
                escrowService.confirmDelivery(id, buyerEmail);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Delivery confirmed & payment released", escrow)
        );
    }
}