package com.patientmanagement.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;
import com.patientmanagement.gateway.util.JwtUtil;

/**
 * Global filter that validates JWT tokens for protected routes.
 * 
 * This filter runs for every request that comes through the gateway. For public
 * routes like /auth/**, we let the request through. For protected routes, we
 * extract the JWT token from the Authorization header, validate it, and if valid,
 * forward the request with user identity headers to the downstream service.
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    private final JwtUtil jwtUtil;
    
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
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // Let public endpoints through without authentication
        if (path.startsWith("/auth/")) {
            return chain.filter(exchange);
        }
        
        // For protected routes, we need a valid JWT token
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }
        
        String authHeader = authHeaders.get(0);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
        }
        
        // Extract the token (skip "Bearer " prefix)
        String token = authHeader.substring(7);
        
        try {
            // Validate the token signature and expiration
            if (!jwtUtil.validateToken(token)) {
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }
            
            // Extract user identity from the token so we can forward it to downstream services
            String userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);
            
            // Add identity headers so downstream services know who made the request
            // This way they don't need to validate the token again
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId != null ? userId : "")
                    .header("X-User-Role", role != null ? role : "")
                    .build();
            
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();
            
            return chain.filter(modifiedExchange);
            
        } catch (Exception e) {
            return onError(exchange, "Token validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
    
    @Override
    public int getOrder() {
        // Run this filter early so we can reject invalid requests before routing
        return -1;
    }
}
