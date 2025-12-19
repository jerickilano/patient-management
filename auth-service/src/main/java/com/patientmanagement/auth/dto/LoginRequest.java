package com.patientmanagement.auth.dto;

// ============================================
// DEVPLAN PHASE 2.4: API ENDPOINTS
// ============================================
// This file implements Phase 2.4 - API Endpoints from DEVPLAN.md
// DTO for POST /auth/login endpoint
// ============================================

// ============================================
// VALIDATION IMPORTS
// ============================================
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * LOGIN REQUEST DTO
 * 
 * Similar to RegisterRequest, but for login operations.
 * Contains the credentials needed to authenticate a user.
 * 
 * SECURITY NOTE:
 * In a production system, you might want to add:
 * - Rate limiting (prevent brute force attacks)
 * - Account lockout after failed attempts
 * - CAPTCHA after multiple failures
 * - Two-factor authentication (2FA)
 */
public class LoginRequest {
    
    /**
     * EMAIL - Used as username for login
     */
    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email is required")
    private String email;
    
    /**
     * PASSWORD - User's password (will be verified against stored hash)
     */
    @NotBlank(message = "Password is required")
    private String password;
    
    /**
     * DEFAULT CONSTRUCTOR
     * Required for JSON deserialization.
     */
    public LoginRequest() {
        // Intentionally empty
    }
    
    /**
     * CONSTRUCTOR WITH PARAMETERS
     * 
     * @param email User's email
     * @param password User's password
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // ============================================
    // GETTERS AND SETTERS
    // ============================================
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        // Normalize email (lowercase, trim)
        this.email = email != null ? email.toLowerCase().trim() : null;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
