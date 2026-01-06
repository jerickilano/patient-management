package com.patientmanagement.notification;

// ============================================
// DEVPLAN PHASE 5.1: NOTIFICATION SERVICE SETUP
// ============================================
// This file implements Phase 5.1 - Service Setup from DEVPLAN.md
// Creates notification-service/ module with Spring Boot
// ============================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NOTIFICATION SERVICE APPLICATION
 * 
 * This service consumes Kafka events and processes notifications.
 * It doesn't expose any HTTP endpoints - it's purely event-driven.
 */
@SpringBootApplication
public class NotificationServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
