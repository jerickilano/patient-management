package com.patientmanagement.patient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import com.patientmanagement.patient.dto.PatientRequest;
import com.patientmanagement.patient.dto.PatientResponse;
import com.patientmanagement.patient.service.PatientService;
import com.patientmanagement.patient.util.SecurityUtil;

/**
 * REST controller for patient operations.
 * All endpoints require authentication, which is handled by the API Gateway.
 */
@RestController
@RequestMapping("/patients")
public class PatientController {
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private SecurityUtil securityUtil;
    
    /**
     * Creates a new patient record.
     * Both USER and ADMIN roles can create patients.
     */
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest request) {
        String userId = securityUtil.getUserId();
        PatientResponse response = patientService.createPatient(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Retrieves a patient by their ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable UUID id) {
        Optional<PatientResponse> patient = patientService.getPatientById(id);
        return patient.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * Lists patients with pagination, sorting, and optional filtering.
     * Supports query parameters: page, size, sort (e.g., "createdAt,desc"), firstName, lastName
     */
    @GetMapping
    public ResponseEntity<Page<PatientResponse>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        
        Page<PatientResponse> patients = patientService.getAllPatients(
            page, size, sort, firstName, lastName
        );
        return ResponseEntity.ok(patients);
    }
    
    /**
     * Updates an existing patient. Only ADMIN role can update patients.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientRequest request) {
        
        if (!securityUtil.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Optional<PatientResponse> updatedPatient = patientService.updatePatient(id, request);
        return updatedPatient.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * Deletes a patient by ID. Only ADMIN role can delete patients.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        if (!securityUtil.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        boolean deleted = patientService.deletePatient(id);
        return deleted ? ResponseEntity.noContent().build() 
                      : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Patient Service is running");
    }
}
