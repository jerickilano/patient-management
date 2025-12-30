package com.patientmanagement.gateway.util;

// ============================================
// DEVPLAN PHASE 3.3: JWT VALIDATION FILTER
// ============================================
// This file implements Phase 3.3 - JWT Validation Filter from DEVPLAN.md
// JWT utility for token validation in the gateway
// ============================================

// ============================================
// JWT LIBRARY IMPORTS
// ============================================
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// ============================================
// SPRING FRAMEWORK IMPORTS
// ============================================
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// ============================================
// JAVA STANDARD LIBRARY IMPORTS
// ============================================
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT UTILITY - Token Validation
 * 
 * This is similar to the JwtUtil in Auth Service, but simpler.
 * We only need to VALIDATE tokens here (not create them).
 * 
 * IMPORTANT: The JWT_SECRET must be the SAME as in Auth Service!
 * Otherwise, tokens created by Auth Service won't be validated here.
 */
@Component
public class JwtUtil {
    
    /**
     * JWT SECRET - Must match Auth Service secret!
     */
    @Value("${jwt.secret}")
    private String secret;
    
    /**
     * GET SIGNING KEY
     * Converts secret string to SecretKey for validation.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * EXTRACT ALL CLAIMS
     * Parses and validates the token signature.
     * 
     * @param token JWT token string
     * @return Claims object with token data
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * EXTRACT EXPIRATION
     * Gets the expiration date from the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * EXTRACT CLAIM
     * Generic method to extract any claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * CHECK IF TOKEN IS EXPIRED
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * VALIDATE TOKEN
     * 
     * Main method for validating JWT tokens.
     * Checks:
     * 1. Token signature is valid (not tampered with)
     * 2. Token is not expired
     * 
     * @param token JWT token string
     * @return true if valid, false otherwise
     */
    public Boolean validateToken(String token) {
        try {
            // extractAllClaims() verifies the signature
            // isTokenExpired() checks expiration
            return !isTokenExpired(token);
        } catch (Exception e) {
            // Any error means token is invalid
            return false;
        }
    }
}
