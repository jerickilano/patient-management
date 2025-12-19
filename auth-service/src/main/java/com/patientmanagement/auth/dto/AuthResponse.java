package com.patientmanagement.auth.dto;

// ============================================
// DEVPLAN PHASE 2.4: API ENDPOINTS
// ============================================
// This file implements Phase 2.4 - API Endpoints from DEVPLAN.md
// Response DTO for authentication endpoints
// ============================================

/**
 * AUTH RESPONSE DTO
 * 
 * This is what we send back to the client after successful login or registration.
 * Contains the JWT token that the client will use for subsequent authenticated requests.
 * 
 * RESPONSE FORMAT:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "message": "Login successful"
 * }
 * 
 * HOW THE CLIENT USES IT:
 * 1. Client receives this response
 * 2. Client extracts the "token" field
 * 3. Client stores it (localStorage, sessionStorage, or memory)
 * 4. Client sends it in subsequent requests: Authorization: Bearer <token>
 * 5. API Gateway validates the token before forwarding requests
 */
public class AuthResponse {
    
    /**
     * JWT TOKEN
     * 
     * This is the JSON Web Token that contains:
     * - User ID
     * - Email
     * - Role
     * - Expiration time
     * 
     * The token is signed with a secret key, so it can't be tampered with.
     * The client includes this token in the Authorization header for protected endpoints.
     */
    private String token;
    
    /**
     * MESSAGE
     * 
     * Human-readable message indicating the result of the operation.
     * Examples:
     * - "Registration successful"
     * - "Login successful"
     * - "User authenticated"
     * 
     * This is helpful for debugging and user feedback.
     */
    private String message;
    
    /**
     * DEFAULT CONSTRUCTOR
     * Required for JSON serialization.
     */
    public AuthResponse() {
        // Intentionally empty
    }
    
    /**
     * CONSTRUCTOR WITH PARAMETERS
     * 
     * @param token The JWT token
     * @param message Success message
     */
    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }
    
    // ============================================
    // GETTERS AND SETTERS
    // ============================================
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
