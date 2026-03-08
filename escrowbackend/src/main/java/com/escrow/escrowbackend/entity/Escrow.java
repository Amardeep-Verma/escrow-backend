package com.escrow.escrowbackend.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "escrows")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Escrow {

    @Id
    private String id;

    private String buyerEmail;
    private String sellerEmail;

    private LocalDateTime createdAt;
    private Double amount;
    private String contractAddress;
    private String productName;
    private String productDescription;
    // ✅ ENUMS instead of Strings
    private ShipmentStatus shipmentStatus;
    private EscrowStatus escrowStatus;
    private String frozenReason;
}
