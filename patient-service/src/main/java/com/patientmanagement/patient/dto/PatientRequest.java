package com.patientmanagement.patient.dto;

// ============================================
// DEVPLAN PHASE 4.4: REST API ENDPOINTS
// ============================================
// This file implements Phase 4.4 - REST API Endpoints from DEVPLAN.md
// DTO for patient creation/update requests
// ============================================

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

/**
 * PATIENT REQUEST DTO
 * 
 * Data transfer object for creating/updating patients.
 * Used in POST /patients and PUT /patients/{id} endpoints.
 */
public class PatientRequest {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @Email(message = "Email must be a valid email address")
    private String email;  // Optional
    
    // ============================================
    // CONSTRUCTORS
    // ============================================
    
    public PatientRequest() {
        // Intentionally empty
    }
    
    public PatientRequest(String firstName, String lastName, LocalDate dateOfBirth, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }
    
    // ============================================
    // GETTERS AND SETTERS
    // ============================================
    
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
}
