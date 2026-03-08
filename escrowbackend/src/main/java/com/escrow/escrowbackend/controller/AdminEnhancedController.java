package com.escrow.escrowbackend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.escrow.escrowbackend.common.ApiResponse;
import com.escrow.escrowbackend.dto.UserDTO;
import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.service.AdminLiveService;
import com.escrow.escrowbackend.service.EscrowService;
import com.escrow.escrowbackend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEnhancedController {

    private final UserService userService;
    private final EscrowService escrowService;
    private final AdminLiveService adminLiveService;

    // ================= GET ALL USERS =================
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved", users));
    }

    // ================= GET USER BY ID =================
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved", user));
    }

    // ================= BAN USER =================
    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<ApiResponse<UserDTO>> banUser(
            @PathVariable String userId,
            @RequestBody Map<String, String> request
    ) {
        String reason = request.get("reason");
        UserDTO user = userService.banUser(userId, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "User banned", user));
    }

    // ================= UNBAN USER =================
    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<ApiResponse<UserDTO>> unbanUser(@PathVariable String userId) {
        UserDTO user = userService.unbanUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User unbanned", user));
    }

    // ================= SEARCH USERS =================
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<List<UserDTO>>> searchUsers(@RequestParam String q) {
        List<UserDTO> users = userService.searchUsers(q);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users found", users));
    }

    // ================= BULK UPDATE USERS =================
    @PostMapping("/users/bulk")
    public ResponseEntity<ApiResponse<String>> bulkUpdateUsers(
            @RequestBody Map<String, Object> request
    ) {
        List<String> userIds = (List<String>) request.get("userIds");
        String action = (String) request.get("action");
        userService.bulkUpdateUsers(userIds, action);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bulk update completed", "Success"));
    }

    // ================= GET ALL ESCROWS =================
    @GetMapping("/escrows")
    public ResponseEntity<ApiResponse<List<Escrow>>> getAllEscrows() {
        List<Escrow> escrows = escrowService.getAllEscrows();
        return ResponseEntity.ok(new ApiResponse<>(true, "Escrows retrieved", escrows));
    }

    // ================= FREEZE ESCROW =================
    @PutMapping("/escrows/{escrowId}/freeze")
    public ResponseEntity<ApiResponse<Escrow>> freezeEscrow(
            @PathVariable String escrowId,
            @RequestBody Map<String, String> request
    ) {
        String reason = request.get("reason");
        Escrow escrow = escrowService.freezeEscrow(escrowId, reason);
        adminLiveService.sendEscrowUpdate(escrow);
        return ResponseEntity.ok(new ApiResponse<>(true, "Escrow frozen", escrow));
    }

    // ================= UNFREEZE ESCROW =================
    @PutMapping("/escrows/{escrowId}/unfreeze")
    public ResponseEntity<ApiResponse<Escrow>> unfreezeEscrow(@PathVariable String escrowId) {
        Escrow escrow = escrowService.unfreezeEscrow(escrowId);
        adminLiveService.sendEscrowUpdate(escrow);
        return ResponseEntity.ok(new ApiResponse<>(true, "Escrow unfrozen", escrow));
    }

    // ================= SEARCH ESCROWS =================
    @GetMapping("/escrows/search")
    public ResponseEntity<ApiResponse<List<Escrow>>> searchEscrows(@RequestParam String q) {
        List<Escrow> escrows = escrowService.searchEscrows(q);
        return ResponseEntity.ok(new ApiResponse<>(true, "Escrows found", escrows));
    }

    // ================= BULK UPDATE ESCROWS =================
    @PostMapping("/escrows/bulk")
    public ResponseEntity<ApiResponse<String>> bulkUpdateEscrows(
            @RequestBody Map<String, Object> request
    ) {
        List<String> escrowIds = (List<String>) request.get("escrowIds");
        String action = (String) request.get("action");
        escrowService.bulkUpdateEscrows(escrowIds, action);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bulk update completed", "Success"));
    }

    // ================= ADMIN STATS =================
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminStats() {
        Map<String, Object> stats = escrowService.getEscrowAnalytics();
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin stats retrieved", stats));
    }

}