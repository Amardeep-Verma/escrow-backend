package com.escrow.escrowbackend.entity;

public enum EscrowStatus {

    CREATED,
    ACCEPTED,
    FUNDED,
    DELIVERED,
    RELEASED,
    DISPUTED,
    CANCELLED   // ⭐ ADD THIS
}