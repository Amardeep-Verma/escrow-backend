package com.escrow.escrowbackend.controller;

import com.escrow.escrowbackend.dto.UserDTO;
import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.entity.EscrowStatus;
import com.escrow.escrowbackend.repository.EscrowRepository;
import com.escrow.escrowbackend.repository.UserRepository;
import com.escrow.escrowbackend.service.AdminLiveService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')") // ✅ Entire controller secured
public class AdminController {

    private final UserRepository userRepository;
    private final EscrowRepository escrowRepository;
    private final AdminLiveService adminLiveService; // 🔥 for live updates

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

    // ================= RESOLVE ESCROW =================
    @PutMapping("/resolve/{id}")
    public ResponseEntity<Escrow> resolveEscrow(@PathVariable String id) {

        Escrow escrow = escrowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        escrow.setEscrowStatus(EscrowStatus.RELEASED);

        Escrow savedEscrow = escrowRepository.save(escrow);

        // 🔥 Send Live Update to Admin Dashboard
        adminLiveService.sendEscrowUpdate(savedEscrow);

        return ResponseEntity.ok(savedEscrow);
    }

    // ================= CANCEL ESCROW =================
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Escrow> cancelEscrow(@PathVariable String id) {

        Escrow escrow = escrowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Escrow not found"));

        escrow.setEscrowStatus(EscrowStatus.CANCELLED);

        Escrow savedEscrow = escrowRepository.save(escrow);

        // 🔥 Send Live Update
        adminLiveService.sendEscrowUpdate(savedEscrow);

        return ResponseEntity.ok(savedEscrow);
    }

    // ================= DELETE USER =================
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {

        userRepository.deleteById(id);

        return ResponseEntity.ok("User deleted successfully");
    }
}