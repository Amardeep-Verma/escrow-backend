package com.escrow.escrowbackend.service;

import com.escrow.escrowbackend.enums.EscrowStatus;

public class EscrowStateValidator {

    private EscrowStateValidator() {
        // prevent object creation (utility class)
    }

    /**
     * Validates escrow status transition
     *
     * @param current current status in DB
     * @param next    requested new status
     */
    public static void validate(EscrowStatus current, EscrowStatus next) {

        if (current == null || next == null) {
            throw new RuntimeException("Escrow status cannot be null");
        }

        switch (current) {

            case CREATED:
                if (next != EscrowStatus.FUNDED &&
                        next != EscrowStatus.CANCELLED) {
                    throw new RuntimeException(
                            "CREATED status can only move to FUNDED or CANCELLED"
                    );
                }
                break;

            case FUNDED:
                if (next != EscrowStatus.DELIVERED &&
                        next != EscrowStatus.DISPUTED) {
                    throw new RuntimeException(
                            "FUNDED status can only move to DELIVERED or DISPUTED"
                    );
                }
                break;

            case DELIVERED:
                if (next != EscrowStatus.RELEASED &&
                        next != EscrowStatus.DISPUTED) {
                    throw new RuntimeException(
                            "DELIVERED status can only move to RELEASED or DISPUTED"
                    );
                }
                break;

            case RELEASED:
                if (next != EscrowStatus.COMPLETED) {
                    throw new RuntimeException(
                            "RELEASED status can only move to COMPLETED"
                    );
                }
                break;

            case COMPLETED:
            case CANCELLED:
                throw new RuntimeException(
                        "Final state reached. Status cannot be changed."
                );

            case DISPUTED:
                if (next != EscrowStatus.RELEASED &&
                        next != EscrowStatus.CANCELLED) {
                    throw new RuntimeException(
                            "DISPUTED can move only to RELEASED or CANCELLED"
                    );
                }
                break;

            default:
                throw new RuntimeException("Invalid escrow state transition");
        }
    }
}