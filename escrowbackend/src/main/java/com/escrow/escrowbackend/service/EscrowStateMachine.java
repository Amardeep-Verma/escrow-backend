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

        if (escrow.getEscrowStatus() != EscrowStatus.CREATED) {
            throw new RuntimeException("Escrow must be CREATED");
        }

        escrow.setShipmentStatus(ShipmentStatus.SHIPPED);
        escrow.setEscrowStatus(EscrowStatus.FUNDED);
    }

    // ======================
    // BUYER CONFIRMS DELIVERY
    // ======================
    public static void confirmDelivery(Escrow escrow) {

        if (escrow.getShipmentStatus() != ShipmentStatus.SHIPPED) {
            throw new RuntimeException("Product not shipped yet");
        }

        if (escrow.getEscrowStatus() == EscrowStatus.RELEASED) {
            throw new RuntimeException("Already released");
        }

        escrow.setShipmentStatus(ShipmentStatus.DELIVERED);
        escrow.setEscrowStatus(EscrowStatus.RELEASED);
    }
}