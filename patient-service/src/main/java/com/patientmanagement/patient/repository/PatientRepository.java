package com.patientmanagement.patient.repository;

// ============================================
// DEVPLAN PHASE 4.2: PATIENT ENTITY & REPOSITORY
// ============================================
// This file implements Phase 4.2 - Patient Entity & Repository from DEVPLAN.md
//
// PatientRepository with standard CRUD methods
// ============================================

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import com.patientmanagement.patient.entity.Patient;

/**
 * PATIENT REPOSITORY
 * 
 * Database access layer for Patient entities.
 * Spring Data JPA provides standard CRUD methods automatically.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    
    /**
     * FIND ALL WITH PAGINATION
     * 
     * Spring Data JPA automatically provides pagination support.
     * The Pageable parameter allows you to specify:
     * - Page number (0-indexed)
     * - Page size (number of records per page)
     * - Sorting (optional)
     * 
     * Example usage:
     *   Pageable pageable = PageRequest.of(0, 10); // First page, 10 records
     *   Page<Patient> patients = patientRepository.findAll(pageable);
     * 
     * @param pageable Pagination and sorting information
     * @return Page of patients
     */
    // Note: findAll(Pageable) is already provided by JpaRepository
    // We don't need to declare it, but it's documented here for learning
    
    /**
     * FIND BY EMAIL (Optional - if you want to search by email)
     */
    // Uncomment if needed:
    // Optional<Patient> findByEmail(String email);
}
