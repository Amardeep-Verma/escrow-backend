package com.escrow.escrowbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String id;
    private String name;
    private String email;
    private String role;
    private String token;   // for login only
}