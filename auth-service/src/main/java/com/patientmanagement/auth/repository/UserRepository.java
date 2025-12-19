package com.patientmanagement.auth.repository;

// ============================================
// DEVPLAN PHASE 2.2: USER ENTITY & REPOSITORY
// ============================================
// This file implements Phase 2.2 - User Entity & Repository from DEVPLAN.md
//
// UserRepository with findByEmail method
// ============================================

// ============================================
// SPRING DATA JPA IMPORTS
// ============================================
// Spring Data JPA provides repository interfaces that automatically implement
// common database operations (save, findById, findAll, delete, etc.)
// You just define the interface, and Spring creates the implementation at runtime!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ============================================
// JAVA STANDARD LIBRARY IMPORTS
// ============================================
import java.util.Optional;
import java.util.UUID;

// ============================================
// OUR ENTITY
// ============================================
import com.patientmanagement.auth.entity.User;

/**
 * USER REPOSITORY - Database Access Layer
 * 
 * WHAT IS A REPOSITORY?
 * A repository is a design pattern that abstracts database access.
 * Instead of writing SQL queries, you define methods in this interface,
 * and Spring Data JPA automatically implements them!
 * 
 * HOW IT WORKS:
 * 1. You extend JpaRepository<User, UUID>
 *    - User: The entity type this repository manages
 *    - UUID: The type of the primary key
 * 2. Spring Data JPA creates a proxy implementation at runtime
 * 3. You can call methods like save(), findById(), findAll() without writing any code
 * 4. You can define custom query methods by following naming conventions
 * 
 * BENEFITS:
 * - No boilerplate code (no need to write save, update, delete methods)
 * - Type-safe (compiler catches errors)
 * - Automatic transaction management
 * - Built-in pagination and sorting support
 * 
 * EXAMPLE USAGE:
 *   User user = userRepository.findByEmail("test@example.com");
 *   userRepository.save(newUser);
 *   userRepository.deleteById(userId);
 */
@Repository  // Tells Spring: "This is a repository component - manage it as a Spring bean"
public interface UserRepository extends JpaRepository<User, UUID> {
    // JpaRepository provides these methods automatically:
    // - save(User entity) - Save or update a user
    // - findById(UUID id) - Find user by ID (returns Optional<User>)
    // - findAll() - Get all users
    // - deleteById(UUID id) - Delete a user
    // - count() - Count total users
    // - existsById(UUID id) - Check if user exists
    // And many more!
    
    /**
     * FIND BY EMAIL - Custom Query Method
     * 
     * HOW IT WORKS:
     * Spring Data JPA uses "method name parsing" to generate SQL queries automatically.
     * 
     * Method name: "findByEmail"
     * - "find" = SELECT query
     * - "By" = WHERE clause
     * - "Email" = column name (matches the "email" field in User entity)
     * 
     * Spring automatically generates SQL like:
     *   SELECT * FROM users WHERE email = ?
     * 
     * NAMING CONVENTIONS:
     * - findBy{FieldName} - Find by a single field
     * - findBy{Field1}And{Field2} - Find by multiple fields (WHERE field1 = ? AND field2 = ?)
     * - findBy{Field}OrderBy{OtherField}Desc - Find with sorting
     * - countBy{Field} - Count matching records
     * 
     * @param email The email address to search for
     * @return Optional<User> - Wrapped in Optional because user might not exist
     * 
     * WHY OPTIONAL?
     * Optional<T> is a Java 8+ feature that represents a value that might not exist.
     * Instead of returning null (which can cause NullPointerException), we return Optional.
     * 
     * Usage:
     *   Optional<User> user = userRepository.findByEmail("test@example.com");
     *   if (user.isPresent()) {
     *       User u = user.get();
     *   }
     *   
     *   // Or using modern Java:
     *   user.ifPresent(u -> System.out.println(u.getEmail()));
     */
    Optional<User> findByEmail(String email);
    
    /**
     * EXISTS BY EMAIL - Check if email is already registered
     * 
     * This is useful during registration to check if an email is already taken.
     * More efficient than findByEmail().isPresent() because it only checks existence,
     * not loading the full user object.
     * 
     * Generated SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     * 
     * @param email The email address to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
