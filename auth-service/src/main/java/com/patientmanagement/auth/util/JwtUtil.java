package com.patientmanagement.auth.util;

// ============================================
// DEVPLAN PHASE 2.3: SECURITY IMPLEMENTATION
// ============================================
// This file implements Phase 2.3 - Security Implementation from DEVPLAN.md
//
// JWT utility class for token generation/validation (secret from env var)
// ============================================

// ============================================
// JWT LIBRARY IMPORTS
// ============================================
// These are from the jjwt (Java JWT) library we added as a dependency.
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// ============================================
// SPRING FRAMEWORK IMPORTS
// ============================================
// @Component: Marks this class as a Spring-managed bean
// @Value: Injects values from application.yml or environment variables
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// ============================================
// JAVA STANDARD LIBRARY IMPORTS
// ============================================
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT UTILITY CLASS - JWT Token Creation and Validation
 * 
 * WHAT IS JWT?
 * JSON Web Token (JWT) is a compact, URL-safe token format for securely
 * transmitting information between parties. It's commonly used for authentication.
 * 
 * JWT STRUCTURE:
 * A JWT has three parts, separated by dots (.):

 * 1. HEADER: Contains token type and signing algorithm
 *    Example: {"alg": "HS256", "typ": "JWT"}
 * 
 * 2. PAYLOAD: Contains claims (data about the user)
 *    Example: {"sub": "user-id", "email": "user@example.com", "role": "ADMIN", "exp": 1234567890}
 * 
 * 3. SIGNATURE: Ensures token hasn't been tampered with
 *    Created by: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
 * 
 * FINAL TOKEN LOOKS LIKE:
 * eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
 * 
 * HOW IT WORKS:
 * 1. User logs in -> Auth Service creates JWT with user info
 * 2. Client stores JWT (localStorage, cookie, etc.)
 * 3. Client sends JWT in Authorization header: "Bearer <token>"
 * 4. API Gateway validates JWT signature and expiration
 * 5. If valid, request is forwarded to the appropriate service
 * 
 * BENEFITS:
 * - Stateless: No need to store sessions on the server
 * - Scalable: Works across multiple servers
 * - Secure: Signed with secret key, can't be tampered with
 * - Self-contained: User info is in the token itself
 */
@Component  // Spring will create a single instance of this class and inject it where needed
public class JwtUtil {
    
    /**
     * JWT SECRET KEY
     * 
     * @Value: Injects the value from application.yml or environment variable
     *   - "${jwt.secret}" looks for "jwt.secret" in application.yml
     *   - Or "JWT_SECRET" environment variable
     * 
     * SECURITY:
     * This secret is used to SIGN tokens (so we can verify they're authentic)
     * and to VERIFY tokens (to ensure they weren't tampered with).
     * 
     * IMPORTANT:
     * - Must be kept secret! Never commit to Git.
     * - Should be at least 32 characters long
     * - Use different secrets for different environments (dev/prod)
     * - Rotate periodically in production
     */
    @Value("${jwt.secret}")
    private String secret;
    
    /**
     * JWT EXPIRATION TIME (in milliseconds)
     * 
     * How long tokens remain valid before they expire.
     * After expiration, users must log in again.
     * 
     * Common values:
     * - 3600000 = 1 hour
     * - 86400000 = 24 hours
     * - 900000 = 15 minutes
     */
    @Value("${jwt.expiration:3600000}")  // Default to 1 hour if not specified
    private Long expiration;
    
    /**
     * GET SECRET KEY
     * 
     * Converts the secret string into a SecretKey object that JWT library can use.
     * 
     * HMAC-SHA256: The algorithm used to sign tokens.
     * - HMAC = Hash-based Message Authentication Code
     * - SHA256 = Secure Hash Algorithm 256-bit
     * 
     * @return SecretKey for signing/verifying tokens
     */
    private SecretKey getSigningKey() {
        // Convert secret string to bytes (UTF-8 encoding)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        
        // Create a SecretKey using HMAC-SHA256 algorithm
        // This key will be used to sign and verify tokens
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * EXTRACT USERNAME (EMAIL) FROM TOKEN
     * 
     * The "subject" (sub) claim in JWT typically contains the username/identifier.
     * In our case, we store the email as the subject.
     * 
     * @param token The JWT token string
     * @return The email address (username) from the token
     */
    public String extractUsername(String token) {
        // Extract the "sub" (subject) claim, which contains the email
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * EXTRACT EXPIRATION DATE FROM TOKEN
     * 
     * @param token The JWT token string
     * @return The expiration date
     */
    public Date extractExpiration(String token) {
        // Extract the "exp" (expiration) claim
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * EXTRACT A SPECIFIC CLAIM FROM TOKEN
     * 
     * This is a generic method that can extract any claim from the token.
     * 
     * @param token The JWT token string
     * @param claimsResolver A function that extracts a specific claim
     * @param <T> The type of the claim value
     * @return The claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // First, extract all claims from the token
        final Claims claims = extractAllClaims(token);
        
        // Then, apply the resolver function to get the specific claim
        return claimsResolver.apply(claims);
    }
    
    /**
     * EXTRACT ALL CLAIMS FROM TOKEN
     * 
     * This parses the token and extracts all the data (claims) from it.
     * It also VERIFIES the token signature to ensure it's authentic.
     * 
     * @param token The JWT token string
     * @return Claims object containing all token data
     * @throws io.jsonwebtoken.JwtException if token is invalid, expired, or tampered with
     */
    private Claims extractAllClaims(String token) {
        // Jwts.parserBuilder() creates a parser
        // .verifyWith(getSigningKey()) sets the secret key for verification
        //   - This ensures the token was signed with our secret (not tampered with)
        // .build() creates the parser
        // .parseSignedClaims(token) parses and verifies the token
        //   - Throws exception if signature is invalid or token is malformed
        // .getPayload() extracts the claims (payload) from the token
        
        return Jwts.parser()
                .verifyWith(getSigningKey())  // Verify signature with our secret
                .build()
                .parseSignedClaims(token)     // Parse and verify the token
                .getPayload();                 // Get the claims (data)
    }
    
    /**
     * CHECK IF TOKEN IS EXPIRED
     * 
     * @param token The JWT token string
     * @return true if token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        // Extract expiration date and compare with current date
        // If expiration is before now, token is expired
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * GENERATE TOKEN FOR USER
     * 
     * This is the main method for creating JWT tokens.
     * Called when a user successfully logs in.
     * 
     * @param email User's email address (stored as "subject" claim)
     * @param userId User's unique ID (stored as custom claim)
     * @param role User's role (stored as custom claim)
     * @return The JWT token string
     */
    public String generateToken(String email, String userId, String role) {
        // Create a map to hold custom claims (additional data we want in the token)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);  // Store user ID for easy access
        claims.put("role", role);      // Store role for authorization checks
        
        // Generate token with these claims
        return createToken(claims, email);
    }
    
    /**
     * CREATE TOKEN WITH CLAIMS
     * 
     * This is the low-level method that actually builds the JWT token.
     * 
     * @param claims Custom claims (additional data)
     * @param subject The subject (typically username/email)
     * @return The JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        // Calculate expiration time: current time + expiration duration
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        // Build the JWT token using the jjwt library
        return Jwts.builder()
                // Set custom claims (userId, role, etc.)
                .claims(claims)
                // Set subject (email/username)
                .subject(subject)
                // Set issued at time (when token was created)
                .issuedAt(now)
                // Set expiration time
                .expiration(expiryDate)
                // Sign the token with our secret key
                // This creates the signature that proves the token is authentic
                .signWith(getSigningKey())
                // Compact the token into a string (the final JWT format)
                .compact();
    }
    
    /**
     * VALIDATE TOKEN
     * 
     * This method checks if a token is valid:
     * 1. Token signature is valid (not tampered with)
     * 2. Token is not expired
     * 3. Token subject matches the expected username
     * 
     * This is called by the API Gateway to verify tokens before allowing requests.
     * 
     * @param token The JWT token string
     * @param username The expected username (to verify token belongs to this user)
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token, String username) {
        try {
            // Extract username from token
            final String tokenUsername = extractUsername(token);
            
            // Check:
            // 1. Username in token matches expected username
            // 2. Token is not expired (isTokenExpired checks this)
            // Note: extractAllClaims() already verified the signature
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            // If anything goes wrong (invalid token, expired, tampered, etc.),
            // the token is invalid
            return false;
        }
    }
    
    /**
     * VALIDATE TOKEN (without username check)
     * 
     * Sometimes we just want to check if a token is valid (not expired, signature OK)
     * without checking the username. Useful in API Gateway where we don't know
     * the username ahead of time.
     * 
     * @param token The JWT token string
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token) {
        try {
            // Just check if token is not expired
            // extractAllClaims() already verified the signature
            return !isTokenExpired(token);
        } catch (Exception e) {
            // If anything goes wrong, token is invalid
            return false;
        }
    }
}
