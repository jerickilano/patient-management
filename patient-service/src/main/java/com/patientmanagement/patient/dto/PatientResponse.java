package com.patientmanagement.patient.dto;

// ============================================
// DEVPLAN PHASE 4.4: REST API ENDPOINTS
// ============================================
// This file implements Phase 4.4 - REST API Endpoints from DEVPLAN.md
// Response DTO for patient endpoints
// ============================================

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PATIENT RESPONSE DTO
 * 
 * Data transfer object for patient responses.
 * Used in GET endpoints to return patient data to clients.
 * 
 * WHY SEPARATE FROM ENTITY?
 * - Don't expose internal entity structure
 * - Can add computed fields (e.g., age)
 * - Can exclude sensitive fields
 * - Versioning flexibility
 */
public class PatientResponse {
    
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private LocalDateTime createdAt;
    
    // ============================================
    // CONSTRUCTORS
    // ============================================
    
    public PatientResponse() {
        // Intentionally empty
    }
    
    public PatientResponse(UUID id, String firstName, String lastName, 
                          LocalDate dateOfBirth, String email, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.createdAt = createdAt;
    }
    
    // ============================================
    // GETTERS AND SETTERS
    // ============================================
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
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
        this.email = email;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
