package com.patientmanagement.auth;

// ============================================
// DEVPLAN PHASE 2.1: AUTH SERVICE SETUP
// ============================================
// This file implements Phase 2.1 - Service Setup from DEVPLAN.md
// Creates auth-service/ module with Spring Boot starter
// ============================================

// ============================================
// SPRING BOOT IMPORTS
// ============================================
// @SpringBootApplication is a convenience annotation that combines:
// - @Configuration: Marks this class as a source of bean definitions
// - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
// - @ComponentScan: Scans for Spring components (controllers, services, etc.) in this package and subpackages
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AUTH SERVICE APPLICATION - Main Entry Point
 * 
 * This is the main class that starts the Spring Boot application.
 * When you run this class (or the JAR), Spring Boot will:
 * 1. Start an embedded web server (Tomcat by default)
 * 2. Scan for Spring components (controllers, services, repositories)
 * 3. Auto-configure beans based on dependencies on the classpath
 * 4. Connect to the database
 * 5. Start listening for HTTP requests
 * 
 * HOW IT WORKS:
 * - The main() method calls SpringApplication.run()
 * - Spring Boot reads application.yml for configuration
 * - It discovers all @Component, @Service, @Repository, @Controller classes
 * - It wires them together (Dependency Injection)
 * - The application starts and waits for requests
 * 
 * WHY @SpringBootApplication?
 * This single annotation replaces what used to require multiple XML configuration files
 * or many Java configuration classes. Spring Boot's "convention over configuration"
 * philosophy means sensible defaults are applied automatically.
 */
@SpringBootApplication
public class AuthServiceApplication {
    
    /**
     * MAIN METHOD - Application Entry Point
     * 
     * This is the standard Java main method. When you run the application:
     * - From IDE: Right-click -> Run
     * - From command line: java -jar auth-service.jar
     * - From Maven: mvn spring-boot:run
     * - From Docker: The Dockerfile will run this
     * 
     * @param args Command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // SpringApplication.run() does the heavy lifting:
        // 1. Creates the Spring ApplicationContext (container for all beans)
        // 2. Registers all @Configuration classes
        // 3. Scans for components
        // 4. Starts the embedded server
        // 5. Runs any CommandLineRunner or ApplicationRunner beans
        
        // AuthServiceApplication.class tells Spring where to start component scanning
        // It will scan this package (com.patientmanagement.auth) and all subpackages
        SpringApplication.run(AuthServiceApplication.class, args);
        
        // After this line, the application is running and ready to accept HTTP requests!
        // You'll see logs like "Tomcat started on port(s): 8081"
    }
}
