package com.patientmanagement.auth.dto;

// ============================================
// DEVPLAN PHASE 2.4: API ENDPOINTS
// ============================================
// This file implements Phase 2.4 - API Endpoints from DEVPLAN.md
// DTO for POST /auth/register endpoint
// ============================================

// ============================================
// VALIDATION IMPORTS
// ============================================
// These annotations validate the data when the request is received.
// Spring automatically validates @RequestBody objects if you add @Valid annotation.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REGISTER REQUEST DTO (Data Transfer Object)
 * 
 * WHAT IS A DTO?
 * A DTO is a simple object that carries data between layers of an application.
 * In this case, it carries data from the HTTP request to our service layer.
 * 
 * WHY USE DTOs INSTEAD OF ENTITIES?
 * 1. Security: Don't expose internal entity structure to clients
 * 2. Validation: Validate input before processing
 * 3. Flexibility: Request structure can differ from database structure
 * 4. Versioning: Can change DTOs without changing entities
 * 
 * EXAMPLE:
 * Client sends JSON: {"email": "user@example.com", "password": "secret123"}
 * Spring automatically deserializes it into this RegisterRequest object.
 * 
 * VALIDATION:
 * The @Valid annotation in the controller triggers validation.
 * If validation fails, Spring returns HTTP 400 (Bad Request) with error details.
 */
public class RegisterRequest {
    
    /**
     * EMAIL FIELD
     * 
     * @Email: Ensures the string matches email format (e.g., user@example.com)
     *   - Checks for @ symbol, domain, etc.
     *   - Spring's validation framework enforces this
     * 
     * @NotBlank: Ensures the field is not null, not empty, and not just whitespace
     *   - More strict than @NotNull (which only checks for null)
     *   - Automatically trims whitespace
     * 
     * WHY VALIDATE HERE?
     * - Fail fast: Catch invalid data before it reaches the service layer
     * - Better error messages: Spring provides detailed validation errors
     * - Security: Prevents malformed data from causing issues
     */
    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email is required")
    private String email;
    
    /**
     * PASSWORD FIELD
     * 
     * @NotBlank: Password cannot be empty
     * 
     * @Size: Enforces minimum and maximum length
     *   - min = 6: Minimum 6 characters (security best practice)
     *   - max = 100: Maximum 100 characters (reasonable limit)
     *   - message: Custom error message if validation fails
     * 
     * PASSWORD SECURITY NOTES:
     * - We validate length here, but actual password strength (uppercase, numbers, etc.)
     *   could be added with a custom validator
     * - The password will be hashed before storing (never stored in plain text!)
     * - In production, consider requiring stronger passwords (8+ chars, mixed case, numbers, symbols)
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    /**
     * DEFAULT CONSTRUCTOR
     * 
     * Required for JSON deserialization.
     * When Spring receives JSON like {"email": "...", "password": "..."},
     * it uses this constructor (or setters) to create the object.
     */
    public RegisterRequest() {
        // Intentionally empty
    }
    
    /**
     * CONSTRUCTOR WITH PARAMETERS
     * 
     * Convenience constructor for creating RegisterRequest objects in code.
     * 
     * @param email User's email address
     * @param password User's password (will be hashed before storage)
     */
    public RegisterRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // ============================================
    // GETTERS AND SETTERS
    // ============================================
    // Required for Spring to populate the object from JSON.
    // Spring uses reflection to call these methods.
    
    /**
     * GET EMAIL
     * @return The email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * SET EMAIL
     * Called by Spring when deserializing JSON.
     * 
     * @param email The email address
     */
    public void setEmail(String email) {
        // Normalize email: lowercase and trim whitespace
        // This ensures "User@Example.com" and "user@example.com" are treated the same
        this.email = email != null ? email.toLowerCase().trim() : null;
    }
    
    /**
     * GET PASSWORD
     * @return The password (plain text - will be hashed in service layer)
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * SET PASSWORD
     * Called by Spring when deserializing JSON.
     * 
     * @param password The password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
