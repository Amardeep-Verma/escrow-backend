package com.escrow.escrowbackend.controller;

import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.entity.EscrowStatus;
import com.escrow.escrowbackend.repository.EscrowRepository;
import com.escrow.escrowbackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final EscrowRepository escrowRepository;

    // ===================================
    // ✅ Get all users
    // ===================================
    @GetMapping("/users")
    public Object getAllUsers() {
        return userRepository.findAll();
    }

    // ===================================
    // ✅ Get all escrows
    // ===================================
    @GetMapping("/escrows")
    public List<Escrow> getAllEscrows() {
        return escrowRepository.findAll();
    }

    // ===================================
    // ✅ Resolve dispute (Release payment)
    // ===================================
    @PutMapping("/resolve/{id}")
    public Escrow resolveEscrow(@PathVariable String id) {

        Escrow escrow = escrowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        // ✅ FIXED LINE
        escrow.setEscrowStatus(EscrowStatus.RELEASED);

        return escrowRepository.save(escrow);
    }

    // ===================================
    // ✅ Cancel escrow
    // ===================================
    @PutMapping("/cancel/{id}")
    public Escrow cancelEscrow(@PathVariable String id) {

        Escrow escrow = escrowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        escrow.setEscrowStatus(EscrowStatus.CANCELLED);

        return escrowRepository.save(escrow);
    }
}