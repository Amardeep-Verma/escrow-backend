package com.escrow.escrowbackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.escrow.escrowbackend.entity.Dispute;
import com.escrow.escrowbackend.service.DisputeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/disputes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDisputeController {

    private final DisputeService disputeService;

    @GetMapping
    public ResponseEntity<List<Dispute>> getAllDisputes() {
        return ResponseEntity.ok(disputeService.getAllDisputes());
    }

    @PutMapping("/{disputeId}/resolve")
    public ResponseEntity<Dispute> resolveDispute(
            @PathVariable String disputeId,
            @RequestParam Dispute.DisputeStatus resolution,
            @RequestParam(required = false) String resolutionNotes
    ) {
        return ResponseEntity.ok(
                disputeService.resolveDispute(disputeId, resolution, resolutionNotes)
        );
    }


    @GetMapping("/{disputeId}")
    public ResponseEntity<Dispute> getDisputeById(@PathVariable String disputeId) {
        return disputeService.getDisputeById(disputeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}