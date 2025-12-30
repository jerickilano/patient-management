package com.patientmanagement.gateway.filter;

// ============================================
// DEVPLAN PHASE 3.3: JWT VALIDATION FILTER
// ============================================
// This file implements Phase 3.3 - JWT Validation Filter from DEVPLAN.md
//
// Custom filter to validate JWT tokens on protected routes
// Extract token from Authorization: Bearer <token> header
// Validate signature and expiration
// Forward request with user context if valid, reject if invalid
// ============================================

// ============================================
// SPRING CLOUD GATEWAY IMPORTS
// ============================================
// Spring Cloud Gateway uses reactive programming (WebFlux) instead of traditional servlets.
// This means we work with Mono and Flux (reactive types) instead of regular objects.
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

// ============================================
// REACTIVE STREAMS IMPORTS
// ============================================
// Mono represents a single value that will be available in the future (asynchronous).
// Think of it like a Promise in JavaScript or Future in Java.
import reactor.core.publisher.Mono;

// ============================================
// JAVA STANDARD LIBRARY IMPORTS
// ============================================
import java.util.List;

// ============================================
// OUR IMPORTS
// ============================================
import com.patientmanagement.gateway.util.JwtUtil;

/**
 * JWT AUTHENTICATION FILTER
 * 
 * WHAT IS A GATEWAY FILTER?
 * Filters in Spring Cloud Gateway intercept requests and responses.
 * They can:
 * - Modify requests before forwarding
 * - Validate authentication
 * - Add headers
 * - Log requests
 * - Reject invalid requests
 * 
 * GLOBAL FILTER:
 * This filter runs for ALL requests that pass through the gateway.
 * We check if the route requires authentication, and if so, validate the JWT token.
 * 
 * HOW IT WORKS:
 * 1. Request comes to gateway
 * 2. Filter intercepts the request
 * 3. Checks if route requires authentication (not /auth/**)
 * 4. If yes, extracts JWT token from Authorization header
 * 5. Validates the token
 * 6. If valid, forwards request to target service
 * 7. If invalid, returns HTTP 401 Unauthorized
 */
@Component  // Spring will create an instance of this filter
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    /**
     * JWT UTILITY - For validating tokens
     * 
     * We'll create this utility class similar to the one in Auth Service.
     */
    private final JwtUtil jwtUtil;
    
    /**
     * CONSTRUCTOR - Dependency Injection
     * 
     * Spring will inject the JwtUtil bean when creating this filter.
     * 
     * @param jwtUtil JWT utility for token validation
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * FILTER METHOD - Main Logic
     * 
     * This method is called for every request that passes through the gateway.
     * 
     * @param exchange ServerWebExchange - contains request and response
     * @param chain GatewayFilterChain - allows us to continue to next filter or reject
     * @return Mono<Void> - reactive type representing completion
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Get the request from the exchange
        ServerHttpRequest request = exchange.getRequest();
        
        // Get the request path (e.g., "/auth/login", "/patients", "/patients/123")
        String path = request.getURI().getPath();
        
        // STEP 1: Check if this is a public endpoint (doesn't require authentication)
        // Public endpoints: /auth/** (registration, login, health check)
        if (path.startsWith("/auth/")) {
            // Public endpoint - no authentication required
            // Continue to the next filter (or route to target service)
            return chain.filter(exchange);
        }
        
        // STEP 2: Protected endpoint - require authentication
        // Extract JWT token from Authorization header
        // Format: "Authorization: Bearer <token>"
        List<String> authHeaders = request.getHeaders().get("Authorization");
        
        // Check if Authorization header exists
        if (authHeaders == null || authHeaders.isEmpty()) {
            // No Authorization header - reject the request
            return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }
        
        // Get the first Authorization header value
        String authHeader = authHeaders.get(0);
        
        // Check if it starts with "Bearer " (JWT token format)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Invalid format - reject the request
            return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
        }
        
        // Extract the token (remove "Bearer " prefix)
        String token = authHeader.substring(7);  // "Bearer ".length() = 7
        
        // STEP 3: Validate the token
        try {
            // Use JwtUtil to validate the token
            // This checks:
            // - Token signature is valid (not tampered with)
            // - Token is not expired
            if (!jwtUtil.validateToken(token)) {
                // Token is invalid or expired
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }
            
            // STEP 4: Token is valid - forward the request to target service
            // Optionally, we could add user information to headers for downstream services
            // For now, we just forward the request as-is
            
            return chain.filter(exchange);
            
        } catch (Exception e) {
            // Any error during validation (malformed token, etc.)
            return onError(exchange, "Token validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * ON ERROR - Handle Authentication Failures
     * 
     * When authentication fails, we need to return an HTTP 401 Unauthorized response.
     * 
     * @param exchange ServerWebExchange
     * @param message Error message
     * @param status HTTP status code
     * @return Mono<Void> representing the error response
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        // Get the response object
        ServerHttpResponse response = exchange.getResponse();
        
        // Set the HTTP status code
        response.setStatusCode(status);
        
        // Optionally, we could set a response body with error details
        // For now, we just set the status code
        
        // Return a completed Mono (reactive way of saying "we're done")
        return response.setComplete();
    }
    
    /**
     * GET ORDER - Filter Execution Order
     * 
     * Filters are executed in order (lower numbers first).
     * We want this filter to run early (before routing) so we can reject
     * invalid requests before they're forwarded.
     * 
     * @return Order value (lower = earlier execution)
     */
    @Override
    public int getOrder() {
        // Return a low number so this filter runs early
        // -1 is commonly used for authentication filters
        return -1;
    }
}
