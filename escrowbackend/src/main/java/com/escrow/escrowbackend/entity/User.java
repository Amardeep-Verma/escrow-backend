package com.escrow.escrowbackend.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    // ✅ Role ENUM (BUYER, SELLER, ADMIN)
    private Role role;

    private boolean banned;
    private String banReason;

    // =====================================
    // Spring Security Authorization
    // =====================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // Spring expects ROLE_ prefix internally
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    // username = email
    @Override
    public String getUsername() {
        return email;
    }

    // =====================================
    // Account Status (all true for now)
    // =====================================

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !banned;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
