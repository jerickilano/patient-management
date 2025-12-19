package com.patientmanagement.auth.service;

// ============================================
// DEVPLAN PHASE 2.4: API ENDPOINTS
// ============================================
// This file implements Phase 2.4 - API Endpoints from DEVPLAN.md
//
// Business logic for:
// - POST /auth/register: Validate input, hash password, save user, return success
// - POST /auth/login: Verify credentials, generate JWT, return token
// ============================================

// ============================================
// SPRING FRAMEWORK IMPORTS
// ============================================
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ============================================
// JAVA STANDARD LIBRARY IMPORTS
// ============================================
import java.util.Optional;

// ============================================
// OUR IMPORTS
// ============================================
import com.patientmanagement.auth.dto.AuthResponse;
import com.patientmanagement.auth.dto.LoginRequest;
import com.patientmanagement.auth.dto.RegisterRequest;
import com.patientmanagement.auth.entity.User;
import com.patientmanagement.auth.repository.UserRepository;
import com.patientmanagement.auth.util.JwtUtil;

/**
 * AUTH SERVICE - Business Logic Layer
 * 
 * WHAT IS A SERVICE?
 * Services contain the business logic of your application. They:
 * - Coordinate between repositories (data access) and controllers (HTTP layer)
 * - Implement business rules and validation
 * - Handle transactions
 * - Don't know about HTTP (that's the controller's job)
 * 
 * LAYERED ARCHITECTURE:
 * Controller (HTTP) -> Service (Business Logic) -> Repository (Data Access) -> Database
 * 
 * WHY SEPARATE LAYERS?
 * - Single Responsibility: Each layer has one job
 * - Testability: Can test business logic without HTTP or database
 * - Reusability: Service can be used by different controllers (REST, GraphQL, etc.)
 * - Maintainability: Changes in one layer don't affect others
 */
@Service  // Tells Spring: "This is a service component - manage it as a Spring bean"
public class AuthService {
    
    /**
     * USER REPOSITORY - Data Access Layer
     * 
     * @Autowired: Spring automatically injects a UserRepository instance here.
     * This is called "Dependency Injection" (DI).
     * 
     * HOW IT WORKS:
     * 1. Spring scans for @Repository classes
     * 2. Creates instances of them (singletons by default)
     * 3. When creating AuthService, Spring sees @Autowired UserRepository
     * 4. Spring finds the UserRepository instance and injects it here
     * 
     * BENEFITS:
     * - No need to manually create objects (new UserRepository())
     * - Easy to swap implementations (e.g., for testing)
     * - Spring manages the lifecycle
     */
    @Autowired
    private UserRepository userRepository;
    
    /**
     * PASSWORD ENCODER - For Hashing Passwords
     * 
     * Spring Security provides BCryptPasswordEncoder, which uses the BCrypt algorithm.
     * 
     * WHAT IS BCRYPT?
     * - A password hashing function designed to be slow (resistant to brute force)
     * - Automatically includes a "salt" (random data) to prevent rainbow table attacks
     * - One-way function: Can't reverse it to get the original password
     * 
     * HOW IT WORKS:
     * - Hash password: encoder.encode("password123") -> "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
     * - Verify password: encoder.matches("password123", hash) -> true/false
     * 
     * SECURITY:
     * BCrypt automatically generates a new salt for each password, so even if two users
     * have the same password, their hashes will be different.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * JWT UTILITY - For Creating Tokens
     * 
     * We'll use this to generate JWT tokens after successful login/registration.
     */
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * REGISTER NEW USER
     * 
     * This method handles user registration:
     * 1. Check if email is already taken
     * 2. Hash the password
     * 3. Create and save the user
     * 4. Generate a JWT token
     * 5. Return the token
     * 
     * @Transactional: Ensures the entire method runs in a database transaction.
     * If anything fails, the database changes are rolled back.
     * 
     * @param request Registration data (email, password)
     * @return AuthResponse containing JWT token
     * @throws RuntimeException if email is already taken
     */
    @Transactional  // All database operations in this method are atomic
    public AuthResponse register(RegisterRequest request) {
        // STEP 1: Check if email is already registered
        // This prevents duplicate accounts with the same email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
            // In production, you might want a custom exception type
            // and better error handling
        }
        
        // STEP 2: Hash the password
        // NEVER store plain text passwords! Always hash them.
        // BCrypt automatically generates a salt and hashes the password
        String passwordHash = passwordEncoder.encode(request.getPassword());
        
        // STEP 3: Create a new User entity
        // Default role is "STAFF" - in production, you might have different registration flows
        // for different user types (admin registration vs. regular user registration)
        User user = new User(
            request.getEmail(),
            passwordHash,
            "STAFF"  // Default role - could be made configurable
        );
        
        // STEP 4: Save the user to the database
        // userRepository.save() will:
        // - Generate a UUID for the user (because of @GeneratedValue)
        // - Set createdAt timestamp (because of @CreationTimestamp)
        // - Insert the row into the "users" table
        User savedUser = userRepository.save(user);
        
        // STEP 5: Generate JWT token
        // The token will contain:
        // - Email (as subject)
        // - User ID (as custom claim)
        // - Role (as custom claim)
        // - Expiration time
        String token = jwtUtil.generateToken(
            savedUser.getEmail(),
            savedUser.getId().toString(),
            savedUser.getRole()
        );
        
        // STEP 6: Return the response with the token
        // The client will use this token for subsequent authenticated requests
        return new AuthResponse(token, "Registration successful");
    }
    
    /**
     * LOGIN USER
     * 
     * This method handles user authentication:
     * 1. Find user by email
     * 2. Check if user exists
     * 3. Verify password (compare hash)
     * 4. Generate JWT token
     * 5. Return the token
     * 
     * @param request Login credentials (email, password)
     * @return AuthResponse containing JWT token
     * @throws RuntimeException if email not found or password incorrect
     */
    @Transactional(readOnly = true)  // Read-only transaction (optimization)
    public AuthResponse login(LoginRequest request) {
        // STEP 1: Find user by email
        // Optional<User> means the user might not exist
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        // STEP 2: Check if user exists
        if (userOptional.isEmpty()) {
            // Don't reveal whether email exists or password is wrong
            // This prevents attackers from discovering valid email addresses
            throw new RuntimeException("Invalid email or password");
        }
        
        // STEP 3: Get the user from Optional
        User user = userOptional.get();
        
        // STEP 4: Verify password
        // passwordEncoder.matches() compares the plain text password with the stored hash
        // It handles the BCrypt comparison internally (including salt extraction)
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            // Same error message as above - don't reveal which part was wrong
            throw new RuntimeException("Invalid email or password");
        }
        
        // STEP 5: Generate JWT token
        // Same as in register() - create a token with user information
        String token = jwtUtil.generateToken(
            user.getEmail(),
            user.getId().toString(),
            user.getRole()
        );
        
        // STEP 6: Return the response with the token
        return new AuthResponse(token, "Login successful");
    }
}
