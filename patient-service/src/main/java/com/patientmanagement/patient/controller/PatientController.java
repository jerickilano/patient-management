package com.patientmanagement.patient.controller;

// ============================================
// DEVPLAN PHASE 4.4: REST API ENDPOINTS
// ============================================
// This file implements Phase 4.4 - REST API Endpoints from DEVPLAN.md
//
// POST /patients: Create patient, save to DB, publish PATIENT_CREATED event
// GET /patients/{id}: Retrieve patient by ID
// GET /patients: List patients with pagination (page, size)
// All endpoints require authentication (validated by gateway)
// ============================================

// ============================================
// SPRING FRAMEWORK IMPORTS
// ============================================
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ============================================
// VALIDATION IMPORTS
// ============================================
import jakarta.validation.Valid;

// ============================================
// JAVA STANDARD LIBRARY IMPORTS
// ============================================
import java.util.Optional;
import java.util.UUID;

// ============================================
// OUR IMPORTS
// ============================================
import com.patientmanagement.patient.dto.PatientRequest;
import com.patientmanagement.patient.dto.PatientResponse;
import com.patientmanagement.patient.service.PatientService;

/**
 * PATIENT CONTROLLER - HTTP Request Handler
 * 
 * Handles HTTP requests for patient operations.
 * All endpoints require authentication (validated by API Gateway).
 */
@RestController
@RequestMapping("/patients")  // Base path: /patients
public class PatientController {
    
    /**
     * PATIENT SERVICE - Business Logic Layer
     */
    @Autowired
    private PatientService patientService;
    
    /**
     * CREATE PATIENT ENDPOINT
     * 
     * HTTP Method: POST
     * Path: /patients
     * Full URL: http://localhost:8080/patients (via API Gateway)
     * 
     * REQUEST BODY EXAMPLE:
     * {
     *   "firstName": "Alice",
     *   "lastName": "Nguyen",
     *   "dateOfBirth": "1995-06-01",
     *   "email": "alice@example.com"
     * }
     * 
     * RESPONSE EXAMPLE (Success - HTTP 201):
     * {
     *   "id": "550e8400-e29b-41d4-a716-446655440000",
     *   "firstName": "Alice",
     *   "lastName": "Nguyen",
     *   "dateOfBirth": "1995-06-01",
     *   "email": "alice@example.com",
     *   "createdAt": "2025-01-01T12:00:00"
     * }
     * 
     * @PostMapping: Handle POST requests
     * @RequestBody: Deserialize JSON to PatientRequest
     * @Valid: Validate request data
     * @param request Patient data
     * @return ResponseEntity with created patient
     */
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest request) {
        // Call service to create patient
        // Service will:
        // 1. Save patient to database
        // 2. Publish event to Kafka
        PatientResponse response = patientService.createPatient(request);
        
        // Return HTTP 201 (Created) with the response body
        // HTTP 201 is the standard status for resource creation
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * GET PATIENT BY ID ENDPOINT
     * 
     * HTTP Method: GET
     * Path: /patients/{id}
     * Full URL: http://localhost:8080/patients/550e8400-e29b-41d4-a716-446655440000
     * 
     * @GetMapping: Handle GET requests
     * @PathVariable: Extract {id} from URL path
     * @param id Patient's UUID
     * @return ResponseEntity with patient data or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable UUID id) {
        // Find patient by ID
        Optional<PatientResponse> patient = patientService.getPatientById(id);
        
        // Check if patient exists
        if (patient.isPresent()) {
            // Patient found - return HTTP 200 (OK) with patient data
            return ResponseEntity.ok(patient.get());
        } else {
            // Patient not found - return HTTP 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * LIST PATIENTS ENDPOINT (WITH PAGINATION)
     * 
     * HTTP Method: GET
     * Path: /patients
     * Full URL: http://localhost:8080/patients?page=0&size=10
     * 
     * QUERY PARAMETERS:
     * - page: Page number (0-indexed, default: 0)
     * - size: Number of records per page (default: 20)
     * 
     * EXAMPLE REQUEST:
     * GET /patients?page=0&size=10
     * 
     * RESPONSE EXAMPLE:
     * {
     *   "content": [
     *     { "id": "...", "firstName": "Alice", ... },
     *     { "id": "...", "firstName": "Bob", ... }
     *   ],
     *   "page": { "number": 0, "size": 10, "totalElements": 25, "totalPages": 3 }
     * }
     * 
     * @GetMapping: Handle GET requests
     * @RequestParam: Extract query parameters from URL
     *   - required = false: Parameter is optional
     *   - defaultValue: Default value if not provided
     * @param page Page number (0-indexed)
     * @param size Page size (number of records)
     * @return ResponseEntity with page of patients
     */
    @GetMapping
    public ResponseEntity<Page<PatientResponse>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Create Pageable object from query parameters
        // PageRequest is an implementation of Pageable
        Pageable pageable = PageRequest.of(page, size);
        
        // Get page of patients from service
        Page<PatientResponse> patients = patientService.getAllPatients(pageable);
        
        // Return HTTP 200 (OK) with page of patients
        return ResponseEntity.ok(patients);
    }
    
    /**
     * HEALTH CHECK ENDPOINT
     * 
     * @GetMapping: Handle GET requests
     * @return Simple health check message
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Patient Service is running");
    }
}
