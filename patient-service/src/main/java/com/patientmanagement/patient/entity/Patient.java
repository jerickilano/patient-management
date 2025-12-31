package com.patientmanagement.patient.entity;

// ============================================
// DEVPLAN PHASE 4.2: PATIENT ENTITY & REPOSITORY
// ============================================
// This file implements Phase 4.2 - Patient Entity & Repository from DEVPLAN.md
//
// Patient entity: id (UUID), firstName, lastName, dateOfBirth, email (optional), createdAt
// ============================================

// ============================================
// JPA IMPORTS
// ============================================
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

// ============================================
// JAVA STANDARD LIBRARY IMPORTS
// ============================================
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PATIENT ENTITY
 * 
 * Represents a patient in the database.
 * Similar structure to User entity in Auth Service.
 */
@Entity
@Table(name = "patients")
public class Patient {
    
    /**
     * PRIMARY KEY - Patient ID (UUID)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * FIRST NAME
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "First name is required")
    private String firstName;
    
    /**
     * LAST NAME
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    /**
     * DATE OF BIRTH
     * 
     * @Past: Ensures the date is in the past (can't be future date)
     */
    @Column(nullable = false)
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    /**
     * EMAIL (Optional)
     * 
     * Not all patients may have email addresses.
     */
    @Column(length = 255)
    @Email(message = "Email must be a valid email address")
    private String email;
    
    /**
     * CREATED TIMESTAMP
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * DEFAULT CONSTRUCTOR (JPA requirement)
     */
    protected Patient() {
        // Intentionally empty
    }
    
    /**
     * CONSTRUCTOR
     */
    public Patient(String firstName, String lastName, LocalDate dateOfBirth, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email != null ? email.toLowerCase().trim() : null;
    }
    
    // ============================================
    // GETTERS AND SETTERS
    // ============================================
    
    public UUID getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase().trim() : null;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
