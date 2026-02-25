package com.escrow.escrowbackend.controller;

import com.escrow.escrowbackend.dto.UserDTO;
import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.entity.EscrowStatus;
import com.escrow.escrowbackend.entity.User;
import com.escrow.escrowbackend.repository.EscrowRepository;
import com.escrow.escrowbackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // ✅ Entire controller secured
public class AdminController {

    private final UserRepository userRepository;
    private final EscrowRepository escrowRepository;

    // ===================================
    // ✅ Get all users
    // ===================================
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().name()
                ))
                .toList();
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

    // ===================================
    // ✅ Delete user (Admin power)
    // ===================================
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);
        return "User deleted successfully";
    }
}