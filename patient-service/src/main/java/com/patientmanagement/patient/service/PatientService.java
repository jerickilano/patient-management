package com.patientmanagement.patient.service;

// Patient Service - handles all business logic for patient operations

// ============================================
// SPRING FRAMEWORK IMPORTS
// ============================================
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
 * Patient Service
 * 
 * This service handles all the business logic for patient operations.
 * It's responsible for creating, retrieving, updating, and deleting patients,
 * as well as handling pagination, sorting, and filtering.
 */
@Service
public class PatientService {
    
    /**
     * PATIENT REPOSITORY - Data Access Layer
     */
    @Autowired
    private PatientRepository patientRepository;
    
    /**
     * Creates a new patient record in the database.
     * 
     * The @Transactional annotation ensures that if anything goes wrong,
     * the entire operation is rolled back. We also capture who created
     * the patient for auditing purposes.
     * 
     * @param request Patient data from the API request
     * @param createdByUserId User ID from the X-User-Id header (for auditing)
     * @return PatientResponse with the created patient data
     */
    @Transactional
    public PatientResponse createPatient(PatientRequest request, String createdByUserId) {
        // Create a new Patient entity from the request data
        Patient patient = new Patient(
            request.getFirstName(),
            request.getLastName(),
            request.getDateOfBirth(),
            request.getEmail(),
            request.getPhone(),
            createdByUserId
        );
        
        // Set the creation timestamp
        patient.setCreatedAt(LocalDateTime.now());
        
        // Save to database - JPA will automatically generate a UUID for the id
        Patient savedPatient = patientRepository.save(patient);
        
        // Convert to response DTO and return
        return toResponse(savedPatient);
    }
    
    /**
     * Retrieves a single patient by their unique ID.
     * 
     * @param id Patient's UUID
     * @return Optional containing the patient if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<PatientResponse> getPatientById(UUID id) {
        Optional<Patient> patient = patientRepository.findById(id);
        return patient.map(this::toResponse);
    }
    
    /**
     * Retrieves a page of patients with support for pagination, sorting, and filtering.
     * 
     * This method handles all the complexity of parsing sort parameters and applying
     * filters. If no filters are provided, it just returns all patients with pagination.
     * 
     * @param page Page number (0-indexed, so page 0 is the first page)
     * @param size Number of records per page
     * @param sort Sort specification like "createdAt,desc" or "lastName,asc"
     * @param firstName Optional filter for first name (case-insensitive partial match)
     * @param lastName Optional filter for last name (case-insensitive partial match)
     * @return Page of PatientResponse objects
     */
    @Transactional(readOnly = true)
    public Page<PatientResponse> getAllPatients(int page, int size, String sort, String firstName, String lastName) {
        // Parse the sort parameter - it should be in format "field,direction"
        // like "createdAt,desc" or "lastName,asc"
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = sort.split(",");
            if (sortParts.length == 2) {
                String field = sortParts[0].trim();
                Sort.Direction direction = sortParts[1].trim().equalsIgnoreCase("desc") 
                    ? Sort.Direction.DESC 
                    : Sort.Direction.ASC;
                sortObj = Sort.by(direction, field);
            } else if (sortParts.length == 1) {
                // If no direction specified, default to ascending
                sortObj = Sort.by(Sort.Direction.ASC, sortParts[0].trim());
            }
        } else {
            // Default to sorting by creation date, newest first
            sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        
        // Create the Pageable object with pagination and sorting info
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        // Get the page of patients, applying filters if provided
        Page<Patient> patients;
        if ((firstName != null && !firstName.isEmpty()) || (lastName != null && !lastName.isEmpty())) {
            // Use the custom filtered query
            patients = patientRepository.findByFilters(
                firstName != null && !firstName.isEmpty() ? firstName : null,
                lastName != null && !lastName.isEmpty() ? lastName : null,
                pageable
            );
        } else {
            // No filters, just get all patients with pagination
            patients = patientRepository.findAll(pageable);
        }
        
        // Convert each Patient entity to a PatientResponse DTO
        return patients.map(this::toResponse);
    }
    
    /**
     * Updates an existing patient record.
     * 
     * @param id Patient ID to update
     * @param request Updated patient data
     * @return Optional containing the updated patient if found, empty otherwise
     */
    @Transactional
    public java.util.Optional<PatientResponse> updatePatient(UUID id, PatientRequest request) {
        // Find patient by ID
        java.util.Optional<Patient> patientOpt = patientRepository.findById(id);
        
        if (patientOpt.isEmpty()) {
            return java.util.Optional.empty();
        }
        
        Patient patient = patientOpt.get();
        
        // Update fields
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        
        // Save updated patient
        Patient updatedPatient = patientRepository.save(patient);
        
        // Convert to response DTO
        return java.util.Optional.of(toResponse(updatedPatient));
    }
    
    /**
     * Deletes a patient record by ID.
     * 
     * @param id Patient ID to delete
     * @return true if the patient was deleted, false if not found
     */
    @Transactional
    public boolean deletePatient(UUID id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Helper method to convert a Patient entity to a PatientResponse DTO.
     * This keeps our internal database structure separate from what we expose in the API.
     * 
     * @param patient Patient entity from the database
     * @return PatientResponse DTO for the API
     */
    private PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
            patient.getId(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getDateOfBirth(),
            patient.getEmail(),
            patient.getPhone(),
            patient.getCreatedAt()
        );
    }
}
