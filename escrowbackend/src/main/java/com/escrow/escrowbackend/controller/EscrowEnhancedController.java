package com.escrow.escrowbackend.controller;

import com.escrow.escrowbackend.common.ApiResponse;
import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.service.EscrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/escrows")
@RequiredArgsConstructor
public class EscrowEnhancedController {

    private final EscrowService escrowService;

    // ================= GET ESCROW DETAILS =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Escrow>> getEscrowDetails(
            @PathVariable String id
    ) {
        Escrow escrow = escrowService.getEscrowById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Escrow details retrieved", escrow)
        );
    }

    // ================= GET ESCROW HISTORY =================
    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getEscrowHistory(
            @PathVariable String id
    ) {
        List<Map<String, Object>> history = escrowService.getEscrowHistory(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Escrow history retrieved", history)
        );
    }

    // ================= CANCEL ESCROW =================
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<Escrow>> cancelEscrow(
            @PathVariable String id,
            Authentication authentication
    ) {
        Escrow escrow = escrowService.cancelEscrow(id, authentication.getName());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Escrow cancelled successfully", escrow)
        );
    }

    // ================= GET ESCROW ANALYTICS =================
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEscrowAnalytics() {
        Map<String, Object> analytics = escrowService.getEscrowAnalytics();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Escrow analytics retrieved", analytics)
        );
    }

    // ================= GET ESCROW BY CONTRACT =================
    @GetMapping("/contract/{contractAddress}")
    public ResponseEntity<ApiResponse<Escrow>> getEscrowByContract(
            @PathVariable String contractAddress
    ) {
        Escrow escrow = escrowService.getEscrowByContractAddress(contractAddress);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Escrow found", escrow)
        );
    }

    // ================= UPDATE ESCROW METADATA =================
    @PutMapping("/{id}/metadata")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Escrow>> updateEscrowMetadata(
            @PathVariable String id,
            @RequestBody Map<String, Object> metadata
    ) {
        Escrow escrow = escrowService.updateEscrowMetadata(id, metadata);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Escrow metadata updated", escrow)
        );
    }

    // ================= GET ESCROW DISPUTES =================
    @GetMapping("/{id}/disputes")
    public ResponseEntity<ApiResponse<List<Object>>> getEscrowDisputes(
            @PathVariable String id
    ) {
        List<Object> disputes = escrowService.getEscrowDisputes(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Escrow disputes retrieved", disputes)
        );
    }

    // ================= FREEZE ESCROW =================
    @PutMapping("/{id}/freeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Escrow>> freezeEscrow(
            @PathVariable String id,
            @RequestParam String reason
    ) {
        Escrow escrow = escrowService.freezeEscrow(id, reason);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Escrow frozen", escrow)
        );
    }

    // ================= UNFREEZE ESCROW =================
    @PutMapping("/{id}/unfreeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Escrow>> unfreezeEscrow(
            @PathVariable String id
    ) {
        Escrow escrow = escrowService.unfreezeEscrow(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Escrow unfrozen", escrow)
        );
    }
}