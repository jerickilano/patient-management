package com.patientmanagement.auth.dto;

/**
 * Response DTO for authentication endpoints.
 * Contains the JWT token and a success message.
 */
public class AuthResponse {
    
    private String token;
    private String message;
    
    public AuthResponse() {
    }
    
    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }
    
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
