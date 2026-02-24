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
@RequestMapping("/api/escrow")
@RequiredArgsConstructor
public class EscrowController {

    private final EscrowService escrowService;

    // ✅ CREATE ESCROW (BUYER ONLY)
    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/create")
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

    // ✅ GET BUYER ESCROWS
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

    // ✅ GET SELLER ESCROWS
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

    // ✅ SELLER SHIPS PRODUCT
    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/ship/{id}")
    public ResponseEntity<ApiResponse<Escrow>> updateShipment(
            @PathVariable String id,
            Authentication authentication
    ) {

        String sellerEmail = authentication.getName();

        Escrow escrow =
                escrowService.updateShipment(id, sellerEmail);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Shipment updated", escrow)
        );
    }

    // ✅ BUYER CONFIRMS DELIVERY
    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/confirm/{id}")
    public ResponseEntity<ApiResponse<Escrow>> confirmDelivery(
            @PathVariable String id,
            Authentication authentication
    ) {

        String buyerEmail = authentication.getName();

        Escrow escrow =
                escrowService.confirmDelivery(id, buyerEmail);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Delivery confirmed", escrow)
        );
    }
}