package com.patientmanagement.patient.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Patient entity representing a patient record in the database.
 */
@Entity
@Table(name = "patients")
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 100)
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @Column(nullable = false)
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @Column(length = 255)
    @Email(message = "Email must be a valid email address")
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    /**
     * Tracks which user created this patient record.
     * Populated from the X-User-Id header forwarded by the API Gateway.
     */
    @Column(nullable = false, updatable = false)
    private String createdByUserId;
    
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
    public Patient(String firstName, String lastName, LocalDate dateOfBirth, String email, String phone, String createdByUserId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email != null ? email.toLowerCase().trim() : null;
        this.phone = phone;
        this.createdByUserId = createdByUserId;
    }
    
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
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getCreatedByUserId() {
        return createdByUserId;
    }
    
    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
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
                ", phone='" + phone + '\'' +
                ", createdByUserId='" + createdByUserId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
