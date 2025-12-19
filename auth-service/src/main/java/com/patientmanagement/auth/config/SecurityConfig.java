package com.patientmanagement.auth.config;

// ============================================
// DEVPLAN PHASE 2.3: SECURITY IMPLEMENTATION
// ============================================
// This file implements Phase 2.3 - Security Implementation from DEVPLAN.md
//
// Password encoder (BCrypt) for hashing
// Spring Security configuration for /auth/** endpoints (public)
// ============================================

// ============================================
// SPRING SECURITY IMPORTS
// ============================================
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SECURITY CONFIGURATION
 * 
 * WHAT IS THIS CLASS?
 * This class configures Spring Security for the Auth Service.
 * Spring Security provides authentication and authorization features.
 * 
 * WHY CONFIGURE SECURITY?
 * By default, Spring Security secures ALL endpoints, requiring authentication.
 * For the Auth Service, we want the /auth/** endpoints to be PUBLIC
 * (no authentication required) so users can register and log in.
 * 
 * SECURITY FILTER CHAIN:
 * Spring Security uses a chain of filters to process requests:
 * 1. Request comes in
 * 2. Goes through security filters
 * 3. Filters check authentication/authorization
 * 4. Request is allowed or rejected
 * 5. If allowed, request reaches the controller
 */
@Configuration  // Tells Spring: "This class contains configuration beans"
@EnableWebSecurity  // Enables Spring Security's web security support
public class SecurityConfig {
    
    /**
     * PASSWORD ENCODER BEAN
     * 
     * WHAT IS A BEAN?
     * A bean is an object managed by Spring's IoC (Inversion of Control) container.
     * Spring creates it, manages its lifecycle, and injects it where needed.
     * 
     * @Bean: Tells Spring: "Create an instance of this and make it available for injection"
     * 
     * WHY BCRYPT?
     * BCrypt is a password hashing algorithm that:
     * - Is intentionally slow (resistant to brute force attacks)
     * - Automatically includes a salt (prevents rainbow table attacks)
     * - Is widely used and battle-tested
     * - Is the default in Spring Security
     * 
     * @return PasswordEncoder instance (BCrypt implementation)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder with strength 10
        // Strength 10 means 2^10 = 1024 rounds of hashing
        // Higher strength = more secure but slower
        // Strength 10 is a good balance
        return new BCryptPasswordEncoder(10);
    }
    
    /**
     * SECURITY FILTER CHAIN
     * 
     * This method configures how Spring Security handles HTTP requests.
     * 
     * @param http HttpSecurity object - used to configure security
     * @return SecurityFilterChain - the configured security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ============================================
            // DISABLE CSRF PROTECTION
            // ============================================
            // CSRF (Cross-Site Request Forgery) protection is important for web apps
            // with forms, but not needed for stateless REST APIs using JWT.
            // 
            // WHY DISABLE?
            // - REST APIs are stateless (no sessions)
            // - JWT tokens are sent in headers (not cookies)
            // - CSRF attacks target cookie-based authentication
            .csrf(csrf -> csrf.disable())
            
            // ============================================
            // SESSION MANAGEMENT
            // ============================================
            // Configure session creation policy
            .sessionManagement(session -> session
                // STATELESS: Don't create HTTP sessions
                // Since we're using JWT tokens, we don't need server-side sessions.
                // Each request is authenticated independently using the JWT token.
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // ============================================
            // AUTHORIZE REQUESTS
            // ============================================
            // Configure which endpoints require authentication
            .authorizeHttpRequests(auth -> auth
                // Allow all requests to /auth/** without authentication
                // This is necessary because:
                // - /auth/register: Users need to register (no account yet)
                // - /auth/login: Users need to log in (no token yet)
                // - /auth/health: Health check endpoint (should be public)
                .requestMatchers("/auth/**").permitAll()
                
                // All other requests require authentication
                // (Though in this service, we only have /auth/** endpoints)
                .anyRequest().authenticated()
            );
        
        // Build and return the configured SecurityFilterChain
        return http.build();
    }
}
