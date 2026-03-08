package com.escrow.escrowbackend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escrow.escrowbackend.service.DisputeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DisputeService disputeService;

    @GetMapping("/fraud/alerts")
    public ResponseEntity<Map<String, Object>> getFraudAlerts() {
        Map<String, Object> response = new HashMap<>();
        response.put("alerts", List.of(
            Map.of("id", 1, "type", "suspicious_activity", "description", "Multiple failed login attempts", "severity", "HIGH", "timestamp", "2024-01-01T10:00:00Z"),
            Map.of("id", 2, "type", "unusual_transaction", "description", "Large transaction from new device", "severity", "MEDIUM", "timestamp", "2024-01-01T11:00:00Z")
        ));
        response.put("totalAlerts", 2);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fraud/metrics")
    public ResponseEntity<Map<String, Object>> getFraudMetrics() {
        Map<String, Object> response = new HashMap<>();
        response.put("metrics", Map.of(
            "totalSuspiciousActivities", 15,
            "blockedTransactions", 8,
            "falsePositives", 2,
            "fraudRate", 0.05,
            "detectionAccuracy", 0.92
        ));
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> getReports() {
        Map<String, Object> response = new HashMap<>();
        response.put("reports", List.of(
            Map.of("id", 1, "title", "Weekly Activity Report", "type", "ACTIVITY", "date", "2024-01-01", "downloadUrl", "/api/admin/reports/1/download"),
            Map.of("id", 2, "title", "Financial Summary", "type", "FINANCIAL", "date", "2024-01-01", "downloadUrl", "/api/admin/reports/2/download"),
            Map.of("id", 3, "title", "User Analytics", "type", "ANALYTICS", "date", "2024-01-01", "downloadUrl", "/api/admin/reports/3/download")
        ));
        response.put("totalReports", 3);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}