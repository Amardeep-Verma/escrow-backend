package com.escrow.escrowbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.escrow.escrowbackend.entity.Dispute;
import com.escrow.escrowbackend.service.DisputeService;

@RestController
@RequestMapping("/api/disputes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DisputeController {

    private final DisputeService disputeService;

    // ================= BUYER RAISE =================
    @PostMapping("/raise")
    public Dispute raiseDispute(
            @RequestParam String escrowId,
            @RequestParam String reason,
            @RequestParam(required = false) String evidenceUrl
    ) {
        return disputeService.raiseDispute(escrowId, reason, evidenceUrl);
    }

    // ================= ADMIN GET ALL =================
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Dispute> getAllDisputes() {
        return disputeService.getAllDisputes();
    }

    // ================= ADMIN RESOLVE =================
    @PostMapping("/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public Dispute resolveDispute(
            @RequestParam String disputeId,
            @RequestParam String decision
    ) {
        return disputeService.resolveDispute(disputeId, decision);
    }
}