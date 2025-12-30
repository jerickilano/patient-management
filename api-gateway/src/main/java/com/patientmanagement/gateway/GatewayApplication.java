package com.patientmanagement.gateway;

// ============================================
// DEVPLAN PHASE 3.1: API GATEWAY SERVICE SETUP
// ============================================
// This file implements Phase 3.1 - Service Setup from DEVPLAN.md
// Creates api-gateway/ module with Spring Cloud Gateway
// ============================================

// ============================================
// SPRING BOOT IMPORTS
// ============================================
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API GATEWAY APPLICATION - Main Entry Point
 * 
 * This is the entry point for the API Gateway service.
 * It's similar to the Auth Service application class, but uses Spring Cloud Gateway
 * instead of Spring MVC.
 * 
 * HOW IT WORKS:
 * 1. Starts Spring Cloud Gateway
 * 2. Loads route configuration from application.yml
 * 3. Registers JWT validation filter
 * 4. Starts listening for requests on port 8080
 * 5. Routes requests to appropriate microservices
 */
@SpringBootApplication
public class GatewayApplication {
    
    /**
     * MAIN METHOD
     * 
     * Starts the API Gateway application.
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
