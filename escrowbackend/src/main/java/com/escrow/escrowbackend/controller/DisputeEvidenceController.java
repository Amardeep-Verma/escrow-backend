package com.escrow.escrowbackend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.escrow.escrowbackend.entity.Evidence;
import com.escrow.escrowbackend.service.DisputeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/disputes")
@RequiredArgsConstructor
public class DisputeEvidenceController {

    private final DisputeService disputeService;

    @PostMapping("/{disputeId}/evidence")
    public ResponseEntity<Map<String, Object>> uploadEvidence(
            @PathVariable String disputeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploaderId") Long uploaderId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("error", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }
            
            // For now, we'll use a placeholder URL
            // In a real implementation, you would upload to cloud storage (AWS S3, etc.)
            String fileUrl = "uploads/" + file.getOriginalFilename();
            
            // Upload evidence
            Evidence evidence = disputeService.uploadEvidence(
                disputeId, 
                uploaderId, 
                file.getOriginalFilename(), 
                fileUrl
            );
            
            response.put("evidenceId", evidence.getId());
            response.put("fileName", evidence.getFileName());
            response.put("uploaderId", evidence.getUploaderId());
            response.put("disputeId", evidence.getDisputeId());
            response.put("uploadTime", evidence.getUploadTime());
            response.put("status", "Evidence uploaded successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{disputeId}/evidence")
    public ResponseEntity<List<Evidence>> getEvidenceForDispute(@PathVariable String disputeId) {
        List<Evidence> evidence = disputeService.getEvidenceForDispute(disputeId);
        return ResponseEntity.ok(evidence);
    }

    @GetMapping("/evidence/user/{uploaderId}")
    public ResponseEntity<List<Evidence>> getEvidenceByUser(@PathVariable Long uploaderId) {
        List<Evidence> evidence = disputeService.getEvidenceByUser(uploaderId);
        return ResponseEntity.ok(evidence);
    }
}