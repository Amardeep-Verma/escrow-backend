package com.escrow.escrowbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {

    private String id;
    private String name;
    private String email;
    private String role;
}