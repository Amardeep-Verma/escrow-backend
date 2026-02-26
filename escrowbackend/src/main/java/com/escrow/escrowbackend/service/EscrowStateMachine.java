package com.escrow.escrowbackend.service;

import com.escrow.escrowbackend.entity.*;

public class EscrowStateMachine {

    // ======================
    // SELLER SHIPS PRODUCT
    // ======================
    public static void ship(Escrow escrow) {

        if (escrow.getShipmentStatus() != ShipmentStatus.PENDING) {
            throw new RuntimeException("Product already shipped");
        }

        // ✅ Seller can ship only after payment funded
        if (escrow.getEscrowStatus() != EscrowStatus.FUNDED) {
            throw new RuntimeException("Payment not funded yet");
        }

        escrow.setShipmentStatus(ShipmentStatus.SHIPPED);
    }

    // ======================
    // BUYER CONFIRMS DELIVERY
    // ======================
    public static void confirmDelivery(Escrow escrow) {

        if (escrow.getShipmentStatus() != ShipmentStatus.SHIPPED) {
            throw new RuntimeException("Product not shipped yet");
        }

        if (escrow.getEscrowStatus() != EscrowStatus.FUNDED) {
            throw new RuntimeException("Invalid escrow state");
        }

        escrow.setShipmentStatus(ShipmentStatus.DELIVERED);
        escrow.setEscrowStatus(EscrowStatus.DELIVERED);
    }

    public static void release(Escrow escrow) {

        if (escrow.getEscrowStatus() != EscrowStatus.DELIVERED) {
            throw new RuntimeException("Delivery not confirmed yet");
        }

        escrow.setEscrowStatus(EscrowStatus.RELEASED);
    }


}