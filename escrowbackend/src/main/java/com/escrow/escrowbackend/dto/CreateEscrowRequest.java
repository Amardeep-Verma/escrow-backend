package com.escrow.escrowbackend.dto;

import lombok.Data;

@Data
public class CreateEscrowRequest {

    private String sellerEmail;
    private Double amount;
    private String productName;
}