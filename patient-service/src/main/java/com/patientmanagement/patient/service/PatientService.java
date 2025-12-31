package com.patientmanagement.patient.service;

// ============================================
// DEVPLAN PHASE 4.4: REST API ENDPOINTS
// ============================================
// This file implements Phase 4.4 - REST API Endpoints from DEVPLAN.md
//
// Business logic for:
// - POST /patients: Create patient, save to DB, publish PATIENT_CREATED event
// - GET /patients/{id}: Retrieve patient by ID
// - GET /patients: List patients with pagination (page, size)
// ============================================

// ============================================
// SPRING FRAMEWORK IMPORTS
// ============================================
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ============================================
// JAVA STANDARD LIBRARY IMPORTS
// ============================================
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

// ============================================
// OUR IMPORTS
// ============================================
import com.patientmanagement.patient.dto.PatientRequest;
import com.patientmanagement.patient.dto.PatientResponse;
import com.patientmanagement.patient.entity.Patient;
import com.patientmanagement.patient.repository.PatientRepository;

/**
 * PATIENT SERVICE - Business Logic Layer
 * 
 * Handles all business logic for patient operations:
 * - Creating patients
 * - Retrieving patients
 * - Listing patients with pagination
 * - Publishing events to Kafka
 */
@Service
public class PatientService {
    
    /**
     * PATIENT REPOSITORY - Data Access Layer
     */
    @Autowired
    private PatientRepository patientRepository;
    
    /**
     * EVENT PUBLISHER - For publishing Kafka events
     */
    @Autowired
    private PatientEventPublisher eventPublisher;
    
    /**
     * CREATE PATIENT
     * 
     * Creates a new patient and publishes a "patient created" event to Kafka.
     * 
     * @Transactional: Ensures the entire operation is atomic.
     * If event publishing fails, we might want to rollback (depending on requirements).
     * For now, we'll let the transaction commit even if event publishing fails
     * (event publishing is best-effort).
     * 
     * @param request Patient data from the API request
     * @return PatientResponse with created patient data
     */
    @Transactional
    public PatientResponse createPatient(PatientRequest request) {
        // STEP 1: Create Patient entity from request DTO
        Patient patient = new Patient(
            request.getFirstName(),
            request.getLastName(),
            request.getDateOfBirth(),
            request.getEmail()
        );
        
        // STEP 2: Set creation timestamp
        // (In a real app, you might use @CreationTimestamp annotation)
        patient.setCreatedAt(LocalDateTime.now());
        
        // STEP 3: Save patient to database
        // JPA will:
        // - Generate UUID for id
        // - Insert row into patients table
        Patient savedPatient = patientRepository.save(patient);
        
        // STEP 4: Publish event to Kafka
        // This is asynchronous - doesn't block the response
        // Other services (like Notification Service) will consume this event
        eventPublisher.publishPatientCreated(
            savedPatient.getId().toString(),
            savedPatient.getFirstName(),
            savedPatient.getLastName(),
            savedPatient.getCreatedAt().toString()
        );
        
        // STEP 5: Convert entity to response DTO and return
        return toResponse(savedPatient);
    }
    
    /**
     * GET PATIENT BY ID
     * 
     * Retrieves a single patient by their unique ID.
     * 
     * @param id Patient's UUID
     * @return Optional<PatientResponse> - empty if patient not found
     */
    @Transactional(readOnly = true)
    public Optional<PatientResponse> getPatientById(UUID id) {
        // Find patient by ID
        Optional<Patient> patient = patientRepository.findById(id);
        
        // Convert to response DTO if found
        return patient.map(this::toResponse);
    }
    
    /**
     * LIST ALL PATIENTS (WITH PAGINATION)
     * 
     * Retrieves a page of patients with pagination support.
     * 
     * PAGINATION BENEFITS:
     * - Performance: Don't load all patients at once
     * - User experience: Show data in manageable chunks
     * - Scalability: Works with millions of records
     * 
     * @param pageable Pagination information (page number, size, sorting)
     * @return Page of PatientResponse objects
     */
    @Transactional(readOnly = true)
    public Page<PatientResponse> getAllPatients(Pageable pageable) {
        // Get page of patients from repository
        Page<Patient> patients = patientRepository.findAll(pageable);
        
        // Convert each Patient entity to PatientResponse DTO
        // map() transforms each element in the page
        return patients.map(this::toResponse);
    }
    
    /**
     * CONVERT ENTITY TO RESPONSE DTO
     * 
     * Helper method to convert Patient entity to PatientResponse DTO.
     * This separates internal entity structure from API response structure.
     * 
     * @param patient Patient entity
     * @return PatientResponse DTO
     */
    private PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
            patient.getId(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getDateOfBirth(),
            patient.getEmail(),
            patient.getCreatedAt()
        );
    }
}
