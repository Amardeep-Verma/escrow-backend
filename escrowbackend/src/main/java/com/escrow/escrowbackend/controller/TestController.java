package com.escrow.escrowbackend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/api/test")
    public String protectedRoute() {
        return "Protected route working!";
    }

    @GetMapping("/test-error")
    public String testError() {
        throw new RuntimeException("Exception handler is working!");
    }
}