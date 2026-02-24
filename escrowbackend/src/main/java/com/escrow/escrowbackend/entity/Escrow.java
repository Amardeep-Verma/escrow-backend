package com.escrow.escrowbackend.entity;

import lombok.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private String productName;

    // PENDING, SHIPPED, DELIVERED
    private String shipmentStatus;

    // CREATED, FUNDED, RELEASED
    private String escrowStatus;
}