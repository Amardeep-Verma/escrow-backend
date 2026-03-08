package com.escrow.escrowbackend.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "evidence")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evidence {
    
    @Id
    private String id;
    
    private String fileName;
    private Long uploaderId;
    private String disputeId;
    private LocalDateTime uploadTime;
    private String fileUrl;
}