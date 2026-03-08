package com.escrow.escrowbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.escrow.escrowbackend.entity.Dispute;
import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.entity.EscrowStatus;
import com.escrow.escrowbackend.repository.DisputeRepository;
import com.escrow.escrowbackend.repository.EscrowRepository;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final EscrowRepository escrowRepository;

    // ================= RAISE =================
    public Dispute raiseDispute(String escrowId, String reason, String evidenceUrl) {

        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        escrow.setEscrowStatus(EscrowStatus.DISPUTED);
        escrowRepository.save(escrow);

        Dispute dispute = Dispute.builder()
                .reason(reason)
                .evidenceUrl(evidenceUrl)
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .escrow(escrow)
                .build();

        return disputeRepository.save(dispute);
    }

    // ================= GET ALL =================
    public List<Dispute> getAllDisputes() {
        return disputeRepository.findAll();
    }

    // ================= RESOLVE =================
    public Dispute resolveDispute(String disputeId, String decision) {

        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Dispute not found"));

        dispute.setStatus("RESOLVED");
        dispute.setResolution(decision);

        Escrow escrow = dispute.getEscrow();

        if ("REFUND_BUYER".equalsIgnoreCase(decision)) {
            escrow.setEscrowStatus(EscrowStatus.REFUNDED);
        } else {
            escrow.setEscrowStatus(EscrowStatus.RELEASED);
        }

        escrowRepository.save(escrow);

        return disputeRepository.save(dispute);
    }
}