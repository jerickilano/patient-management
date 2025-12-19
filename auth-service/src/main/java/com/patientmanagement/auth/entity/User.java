package com.patientmanagement.auth.entity;

// ============================================
// DEVPLAN PHASE 2.2: USER ENTITY & REPOSITORY
// ============================================
// This file implements Phase 2.2 - User Entity & Repository from DEVPLAN.md
//
// User entity: id (UUID), email (unique), passwordHash, role, createdAt
// ============================================

// ============================================
// JPA (JAVA PERSISTENCE API) IMPORTS
// ============================================
// JPA provides annotations to map Java classes to database tables.
// This is called "Object-Relational Mapping" (ORM).
import jakarta.persistence.*;
// Validation annotations to ensure data integrity
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// ============================================
// JAVA STANDARD LIBRARY IMPORTS
// ============================================
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * USER ENTITY - Represents a User in the Database
 * 
 * WHAT IS AN ENTITY?
 * An entity is a Java class that represents a database table.
 * Each instance of this class represents one row in the "users" table.
 * 
 * HOW IT WORKS:
 * - JPA (via Hibernate) automatically creates the database table based on this class
 * - You can save, retrieve, update, and delete User objects using Spring Data JPA
 * - No need to write SQL queries - JPA handles the translation
 * 
 * BEST PRACTICES:
 * - Entities should be simple data holders (avoid business logic)
 * - Use immutable fields where possible (final fields)
 * - Always validate input data
 * - Use UUIDs for primary keys (better than auto-increment IDs for distributed systems)
 */
@Entity  // Tells JPA: "This class represents a database table"
@Table(name = "users")  // Explicitly name the table "users" (optional - JPA would use "User" by default)
public class User {
    
    /**
     * PRIMARY KEY - User ID
     * 
     * @Id: Marks this field as the primary key
     * @GeneratedValue: Automatically generates a value when the entity is saved
     *   - strategy = GenerationType.UUID: Use UUID (Universally Unique Identifier)
     *     UUIDs are 128-bit values that are virtually guaranteed to be unique
     *     Format: "550e8400-e29b-41d4-a716-446655440000"
     * 
     * WHY UUID INSTEAD OF AUTO-INCREMENT?
     * - No database round-trip needed to get the ID
     * - Works better in distributed systems (no ID conflicts across databases)
     * - More secure (can't guess other user IDs)
     * - Can generate IDs before saving to database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * EMAIL ADDRESS - Must be unique and valid email format
     * 
     * @Column: Customize the database column
     *   - unique = true: Database enforces uniqueness (prevents duplicate emails)
     *   - nullable = false: Email is required (NOT NULL constraint)
     *   - length = 255: Maximum length in database
     * 
     * @Email: Validation annotation - ensures the string is a valid email format
     *   This is checked when the entity is validated (e.g., in controllers)
     * 
     * @NotNull: Ensures email is not null (checked by validation framework)
     * 
     * WHY UNIQUE EMAIL?
     * Email is typically used as the username for login, so it must be unique.
     * The database will reject attempts to create duplicate emails.
     */
    @Column(unique = true, nullable = false, length = 255)
    @Email(message = "Email must be a valid email address")
    @NotNull(message = "Email is required")
    private String email;
    
    /**
     * PASSWORD HASH - Never store plain text passwords!
     * 
     * @Column: 
     *   - nullable = false: Password hash is required
     *   - length = 255: BCrypt hashes are 60 characters, but 255 gives room for future algorithms
     * 
     * @NotNull: Ensures password hash is not null
     * 
     * SECURITY NOTE:
     * This field stores the HASHED password, not the plain text password.
     * We use BCrypt (via Spring Security) to hash passwords before saving.
     * 
     * WHY HASH PASSWORDS?
     * - If database is compromised, attackers can't see actual passwords
     * - BCrypt is a one-way function - you can't reverse it to get the original password
     * - When user logs in, we hash their input and compare it to this stored hash
     * 
     * NEVER, EVER store plain text passwords in production!
     */
    @Column(nullable = false, length = 255)
    @NotNull
    private String passwordHash;
    
    /**
     * USER ROLE - Defines what the user can do
     * 
     * @Column:
     *   - nullable = false: Role is required
     *   - length = 50: Role names are short (e.g., "ADMIN", "STAFF", "USER")
     * 
     * @NotNull: Ensures role is not null
     * 
     * ROLE-BASED ACCESS CONTROL (RBAC):
     * Different users have different permissions:
     * - ADMIN: Full access to everything
     * - STAFF: Can manage patients, limited admin access
     * - USER: Basic access (if we add this role later)
     * 
     * The API Gateway or services can check the role from the JWT token
     * to decide what actions a user can perform.
     */
    @Column(nullable = false, length = 50)
    @NotNull
    private String role;
    
    /**
     * CREATED TIMESTAMP - When the user account was created
     * 
     * @Column:
     *   - nullable = false: Timestamp is required
     *   - updatable = false: Once set, this can't be changed (immutable)
     * 
     * @CreationTimestamp: Hibernate automatically sets this when the entity is first saved
     *   No need to manually set it - Hibernate does it for you!
     * 
     * WHY TRACK CREATED_AT?
     * - Audit trail: Know when accounts were created
     * - Analytics: Track user growth over time
     * - Debugging: Helpful when investigating issues
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * JPA REQUIRES A NO-ARG CONSTRUCTOR
     * 
     * Hibernate needs to create instances of this class when loading data from the database.
     * It uses reflection to set field values, so it needs a constructor with no arguments.
     * 
     * PROTECTED: We make it protected (not public) so it can't be accidentally used elsewhere.
     * We want developers to use the constructor with parameters or builder pattern instead.
     */
    protected User() {
        // Intentionally empty - JPA requirement
    }
    
    /**
     * CONSTRUCTOR - Create a new User
     * 
     * This constructor is used when creating a new user from registration data.
     * Note: id and createdAt are set automatically by JPA.
     * 
     * @param email User's email address (will be validated)
     * @param passwordHash Already-hashed password (hashing happens in the service layer)
     * @param role User's role (e.g., "ADMIN", "STAFF")
     */
    public User(String email, String passwordHash, String role) {
        // Validate inputs (defensive programming)
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or blank");
        }
        
        // Set the fields
        this.email = email.toLowerCase().trim();  // Normalize email (lowercase, no spaces)
        this.passwordHash = passwordHash;
        this.role = role.toUpperCase();  // Normalize role (uppercase)
        // Note: id and createdAt will be set by JPA when we save the entity
    }
    
    // ============================================
    // GETTERS AND SETTERS
    // ============================================
    // These methods allow other classes to read and modify the entity's fields.
    // In a real application, you might use Lombok's @Getter/@Setter to generate these automatically.
    
    /**
     * GET ID
     * @return The user's unique identifier (UUID)
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * GET EMAIL
     * @return The user's email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * SET EMAIL (rarely used after creation, but available if needed)
     * @param email New email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * GET PASSWORD HASH
     * @return The hashed password (never expose this in API responses!)
     */
    public String getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * SET PASSWORD HASH (used when updating password)
     * @param passwordHash New hashed password
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    /**
     * GET ROLE
     * @return The user's role
     */
    public String getRole() {
        return role;
    }
    
    /**
     * SET ROLE (used for role management)
     * @param role New role
     */
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     * GET CREATED AT
     * @return When the user account was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * SET CREATED AT (called by Hibernate when loading from database)
     * @param createdAt Creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * TO STRING - Useful for logging and debugging
     * 
     * NOTE: We intentionally exclude passwordHash from the string representation
     * to avoid accidentally logging sensitive data.
     * 
     * @return String representation of the user (without password)
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
