package com.escrow.escrowbackend.controller;

import com.escrow.escrowbackend.dto.UserDTO;
import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.entity.EscrowStatus;
import com.escrow.escrowbackend.repository.EscrowRepository;
import com.escrow.escrowbackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')") // ✅ FIXED
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final EscrowRepository escrowRepository;

    // ================= USERS =================
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {

        List<UserDTO> users = userRepository.findAll()
                .stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().name()
                ))
                .toList();

        return ResponseEntity.ok(users);
    }

    // ================= ESCROWS =================
    @GetMapping("/escrows")
    public ResponseEntity<List<Escrow>> getAllEscrows() {
        return ResponseEntity.ok(escrowRepository.findAll());
    }

    // ================= RESOLVE =================
    @PutMapping("/resolve/{id}")
    public ResponseEntity<Escrow> resolveEscrow(@PathVariable String id) {

        Escrow escrow = escrowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        escrow.setEscrowStatus(EscrowStatus.RELEASED);

        return ResponseEntity.ok(escrowRepository.save(escrow));
    }

    // ================= CANCEL =================
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Escrow> cancelEscrow(@PathVariable String id) {

        Escrow escrow = escrowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        escrow.setEscrowStatus(EscrowStatus.CANCELLED);

        return ResponseEntity.ok(escrowRepository.save(escrow));
    }

    // ================= DELETE USER =================
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {

        userRepository.deleteById(id);

        return ResponseEntity.ok("User deleted successfully");
    }
}