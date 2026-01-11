package com.patientmanagement.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import com.patientmanagement.patient.entity.Patient;

/**
 * Repository interface for Patient entities.
 * Spring Data JPA automatically provides standard CRUD methods.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    
    /**
     * Finds patients with optional filtering by first name and/or last name.
     * Supports case-insensitive partial matching.
     */
    @Query("SELECT p FROM Patient p WHERE " +
           "(:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))")
    Page<Patient> findByFilters(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            Pageable pageable
    );
}
