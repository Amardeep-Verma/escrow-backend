package com.escrow.escrowbackend.exception;

public class DisputeException extends RuntimeException {
    public DisputeException(String message) {
        super(message);
    }
    
    public DisputeException(String message, Throwable cause) {
        super(message, cause);
    }
}