package com.patientmanagement.patient;

// ============================================
// DEVPLAN PHASE 4.1: PATIENT SERVICE SETUP
// ============================================
// This file implements Phase 4.1 - Service Setup from DEVPLAN.md
// Creates patient-service/ module with Spring Boot
// ============================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PATIENT SERVICE APPLICATION
 * 
 * Main entry point for the Patient Service.
 * Handles patient CRUD operations and publishes events to Kafka.
 */
@SpringBootApplication
public class PatientServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }
}
