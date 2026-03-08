package com.escrow.escrowbackend.service;

import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.entity.EscrowStatus;
import com.escrow.escrowbackend.entity.ShipmentStatus;

public class EscrowStateMachine {

    // ======================
    // FUND ESCROW (NEW)
    // ======================
    public static void fund(Escrow escrow) {

        if (escrow.getEscrowStatus() != EscrowStatus.CREATED) {
            throw new RuntimeException("Escrow not in CREATED state");
        }

        escrow.setEscrowStatus(EscrowStatus.FUNDED);
    }

    // ======================
    // SELLER SHIPS PRODUCT
    // ======================
    public static void ship(Escrow escrow) {

        if (escrow.getShipmentStatus() != ShipmentStatus.PENDING) {
            throw new RuntimeException("Product already shipped");
        }

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

    // ======================
    // RELEASE PAYMENT
    // ======================
    public static void release(Escrow escrow) {

        if (escrow.getEscrowStatus() != EscrowStatus.DELIVERED) {
            throw new RuntimeException("Delivery not confirmed yet");
        }

        escrow.setEscrowStatus(EscrowStatus.RELEASED);
    }

    // ======================
    // CANCEL ESCROW
    // ======================
    public static void cancel(Escrow escrow) {

        if (escrow.getEscrowStatus() != EscrowStatus.CREATED) {
            throw new RuntimeException("Escrow cannot be cancelled in current state");
        }

        escrow.setEscrowStatus(EscrowStatus.CANCELLED);
    }
}
