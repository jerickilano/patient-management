package com.patientmanagement.auth.controller;

// ============================================
// DEVPLAN PHASE 2.4: API ENDPOINTS
// ============================================
// This file implements Phase 2.4 - API Endpoints from DEVPLAN.md
//
// POST /auth/register: Validate input, hash password, save user, return success
// POST /auth/login: Verify credentials, generate JWT, return token
// Input validation and error handling
// ============================================

// ============================================
// SPRING FRAMEWORK IMPORTS
// ============================================
// @RestController: Combines @Controller and @ResponseBody
//   - @Controller: Marks this as a Spring MVC controller
//   - @ResponseBody: Return values are serialized to JSON (not a view name)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ============================================
// VALIDATION IMPORTS
// ============================================
// @Valid: Triggers validation of the request body
// Validation errors result in HTTP 400 (Bad Request)
import jakarta.validation.Valid;

// ============================================
// OUR IMPORTS
// ============================================
import com.patientmanagement.auth.dto.AuthResponse;
import com.patientmanagement.auth.dto.LoginRequest;
import com.patientmanagement.auth.dto.RegisterRequest;
import com.patientmanagement.auth.service.AuthService;

/**
 * AUTH CONTROLLER - HTTP Request Handler
 * 
 * WHAT IS A CONTROLLER?
 * Controllers handle HTTP requests and responses. They:
 * - Receive HTTP requests (GET, POST, etc.)
 * - Validate request data
 * - Call service methods to perform business logic
 * - Return HTTP responses (JSON, status codes, etc.)
 * 
 * REQUEST FLOW:
 * 1. Client sends HTTP request -> Controller receives it
 * 2. Controller validates request (@Valid)
 * 3. Controller calls Service method
 * 4. Service performs business logic
 * 5. Service returns result to Controller
 * 6. Controller creates HTTP response
 * 7. Response sent back to client
 * 
 * ANNOTATIONS EXPLAINED:
 * - @RestController: This is a REST API controller (returns JSON, not HTML)
 * - @RequestMapping: Base path for all endpoints in this controller
 * - @PostMapping: Handle POST requests
 * - @RequestBody: Deserialize JSON request body into Java object
 * - @Valid: Validate the request object
 * - @ResponseEntity: Allows us to set HTTP status code and headers
 */
@RestController  // Marks this as a REST controller (returns JSON, not HTML views)
@RequestMapping("/auth")  // All endpoints in this controller start with "/auth"
public class AuthController {
    
    /**
     * AUTH SERVICE - Business Logic Layer
     * 
     * @Autowired: Spring injects the AuthService instance here.
     * The controller delegates business logic to the service.
     * This keeps the controller thin (just HTTP handling) and service focused (business logic).
     */
    @Autowired
    private AuthService authService;
    
    /**
     * REGISTER ENDPOINT
     * 
     * HTTP Method: POST
     * Path: /auth/register
     * Full URL: http://localhost:8081/auth/register
     * 
     * REQUEST BODY EXAMPLE:
     * {
     *   "email": "user@example.com",
     *   "password": "password123"
     * }
     * 
     * RESPONSE EXAMPLE (Success - HTTP 200):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "message": "Registration successful"
     * }
     * 
     * RESPONSE EXAMPLE (Error - HTTP 400):
     * {
     *   "timestamp": "2025-01-01T12:00:00Z",
     *   "status": 400,
     *   "error": "Bad Request",
     *   "message": "Email must be a valid email address",
     *   "path": "/auth/register"
     * }
     * 
     * @PostMapping: Handle POST requests to "/register"
     *   - POST is used for creating resources
     *   - POST requests have a body (unlike GET)
     * 
     * @RequestBody: Spring automatically deserializes the JSON request body
     *   into a RegisterRequest object
     *   - Content-Type header must be "application/json"
     *   - Spring uses Jackson library to convert JSON to Java object
     * 
     * @Valid: Triggers validation of the RegisterRequest object
     *   - Checks @Email, @NotBlank, @Size annotations
     *   - If validation fails, Spring returns HTTP 400 with error details
     *   - Method won't be called if validation fails
     * 
     * @param request Registration data (email, password)
     * @return ResponseEntity containing JWT token and success message
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Call the service to handle registration
            // The service will:
            // 1. Check if email exists
            // 2. Hash the password
            // 3. Save the user
            // 4. Generate JWT token
            AuthResponse response = authService.register(request);
            
            // Return HTTP 200 (OK) with the response body
            // ResponseEntity allows us to set status code and headers
            return ResponseEntity.ok(response);
            // ResponseEntity.ok() is shorthand for:
            // return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            // Handle business logic errors (e.g., email already exists)
            // In a production app, you might want:
            // - Custom exception types
            // - Global exception handler (@ControllerAdvice)
            // - More specific error messages
            // - Logging
            
            // For now, return HTTP 400 (Bad Request) with error message
            AuthResponse errorResponse = new AuthResponse(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * LOGIN ENDPOINT
     * 
     * HTTP Method: POST
     * Path: /auth/login
     * Full URL: http://localhost:8081/auth/login
     * 
     * REQUEST BODY EXAMPLE:
     * {
     *   "email": "user@example.com",
     *   "password": "password123"
     * }
     * 
     * RESPONSE EXAMPLE (Success - HTTP 200):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "message": "Login successful"
     * }
     * 
     * RESPONSE EXAMPLE (Error - HTTP 401 Unauthorized):
     * {
     *   "token": null,
     *   "message": "Invalid email or password"
     * }
     * 
     * @PostMapping: Handle POST requests to "/login"
     * 
     * @RequestBody: Deserialize JSON to LoginRequest object
     * 
     * @Valid: Validate email format and required fields
     * 
     * @param request Login credentials (email, password)
     * @return ResponseEntity containing JWT token and success message
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Call the service to handle authentication
            // The service will:
            // 1. Find user by email
            // 2. Verify password
            // 3. Generate JWT token
            AuthResponse response = authService.login(request);
            
            // Return HTTP 200 (OK) with the token
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            // Handle authentication failures
            // Return HTTP 401 (Unauthorized) - standard status for auth failures
            AuthResponse errorResponse = new AuthResponse(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    /**
     * HEALTH CHECK ENDPOINT (Optional but useful)
     * 
     * This endpoint can be used to check if the service is running.
     * Useful for:
     * - Docker health checks
     * - Load balancer health checks
     * - Monitoring systems
     * 
     * HTTP Method: GET
     * Path: /auth/health
     * 
     * @GetMapping: Handle GET requests (no body, just a simple request)
     * 
     * @return Simple message indicating service is up
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        // Simple health check - just return a message
        // In production, you might check:
        // - Database connectivity
        // - External service availability
        // - System resources
        return ResponseEntity.ok("Auth Service is running");
    }
}
