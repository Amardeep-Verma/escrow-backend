package com.escrow.escrowbackend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escrow.escrowbackend.common.ApiResponse;
import com.escrow.escrowbackend.entity.Dispute;
import com.escrow.escrowbackend.service.DisputeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/disputes")
@RequiredArgsConstructor
public class DisputeEnhancedController {

    private final DisputeService disputeService;

    // ================= GET DISPUTE DETAILS =================
    @GetMapping("/{disputeId}")
    public ResponseEntity<ApiResponse<Dispute>> getDisputeDetails(
            @PathVariable String disputeId
    ) {
        Optional<Dispute> disputeOpt = disputeService.getDisputeById(disputeId);
        if (disputeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Dispute dispute = disputeOpt.get();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Dispute details retrieved", dispute)
        );
    }

    // ================= GET DISPUTES BY ESCROW =================
    @GetMapping("/escrow/{escrowId}")
    public ResponseEntity<ApiResponse<List<Dispute>>> getDisputesByEscrow(
            @PathVariable String escrowId
    ) {
        List<Dispute> disputes = disputeService.getDisputesForEscrow(escrowId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Disputes by escrow retrieved", disputes)
        );
    }

    // ================= GET DISPUTES BY STATUS =================
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Dispute>>> getDisputesByStatus(
            @PathVariable Dispute.DisputeStatus status
    ) {
        List<Dispute> disputes = disputeService.getDisputesByStatus(status);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Disputes by status retrieved", disputes)
        );
    }

    // ================= MARK DISPUTE AS UNDER REVIEW =================
    @PutMapping("/{disputeId}/under-review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Dispute>> markAsUnderReview(
            @PathVariable String disputeId
    ) {
        Dispute dispute = disputeService.markAsUnderReview(disputeId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Dispute marked as under review", dispute)
        );
    }

    // ================= GET ALL DISPUTES (ADMIN) =================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Dispute>>> getAllDisputes() {
        List<Dispute> disputes = disputeService.getAllDisputes();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "All disputes retrieved", disputes)
        );
    }
}