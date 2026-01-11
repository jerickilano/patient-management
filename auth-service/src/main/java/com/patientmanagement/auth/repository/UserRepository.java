package com.patientmanagement.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import com.patientmanagement.auth.entity.User;

/**
 * Repository interface for User entities.
 * Spring Data JPA automatically provides standard CRUD methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Finds a user by their email address.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Checks if a user with the given email exists.
     * More efficient than findByEmail().isPresent() since it only checks existence.
     */
    boolean existsByEmail(String email);
}
