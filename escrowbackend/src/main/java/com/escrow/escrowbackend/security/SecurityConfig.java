package com.escrow.escrowbackend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // ✅ JWT filter
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // ✅ CORS config
    private final CorsConfigurationSource corsConfigurationSource;

    // ✅ NEW — UserDetailsService (IMPORTANT)
    private final CustomUserDetailsService userDetailsService;


    // =====================================================
    // AUTHENTICATION PROVIDER (ROLE FIX HAPPENS HERE)
    // =====================================================
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    // =====================================================
    // SECURITY FILTER CHAIN
    // =====================================================
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ✅ Disable CSRF for REST API
                .csrf(csrf -> csrf.disable())

                // ✅ Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // ✅ Authorization Rules
                .authorizeHttpRequests(auth -> auth

                        // ---------- PUBLIC ----------
                        .requestMatchers(
                                "/api/auth/**",
                                "/error",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // ---------- ADMIN ----------
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // ---------- ESCROW USERS ----------
                        .requestMatchers("/api/escrow/**")
                        .hasAnyRole("BUYER", "SELLER", "ADMIN")

                        // ---------- OTHERS ----------
                        .anyRequest().authenticated()
                )

                // ✅ Stateless JWT session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ Register authentication provider
                .authenticationProvider(authenticationProvider())

                // ✅ Add JWT filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


    // =====================================================
    // PASSWORD ENCODER
    // =====================================================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}