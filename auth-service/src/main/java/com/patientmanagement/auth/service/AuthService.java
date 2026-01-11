package com.patientmanagement.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.patientmanagement.auth.dto.AuthResponse;
import com.patientmanagement.auth.dto.LoginRequest;
import com.patientmanagement.auth.dto.RegisterRequest;
import com.patientmanagement.auth.entity.User;
import com.patientmanagement.auth.repository.UserRepository;
import com.patientmanagement.auth.util.JwtUtil;

/**
 * Service that handles authentication business logic.
 * Responsible for user registration, login, and password hashing.
 */
@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Registers a new user account.
     * Hashes the password, saves the user, and generates a JWT token.
     * 
     * @param request Registration data (email, password)
     * @return AuthResponse containing JWT token
     * @throws RuntimeException if email is already registered
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Hash the password before storing it
        String passwordHash = passwordEncoder.encode(request.getPassword());
        
        // Create user with default USER role
        User user = new User(
            request.getEmail(),
            passwordHash,
            "USER"
        );
        
        User savedUser = userRepository.save(user);
        
        // Generate JWT token with user info
        String token = jwtUtil.generateToken(
            savedUser.getEmail(),
            savedUser.getId().toString(),
            savedUser.getRole()
        );
        
        return new AuthResponse(token, "Registration successful");
    }
    
    /**
     * Authenticates a user and generates a JWT token.
     * 
     * @param request Login credentials (email, password)
     * @return AuthResponse containing JWT token
     * @throws RuntimeException if email not found or password incorrect
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isEmpty()) {
            // Use generic message to prevent email enumeration
            throw new RuntimeException("Invalid email or password");
        }
        
        User user = userOptional.get();
        
        // Verify password against stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(
            user.getEmail(),
            user.getId().toString(),
            user.getRole()
        );
        
        return new AuthResponse(token, "Login successful");
    }
}
