package com.escrow.escrowbackend.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "disputes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispute {

    @Id
    private String id;

    private String reason;

    private String evidenceUrl;

    private String status;      // OPEN / RESOLVED

    private String resolution;  // REFUND_BUYER / RELEASE_SELLER

    private LocalDateTime createdAt;

    private Escrow escrow;
}