package com.patientmanagement.patient.service;

// ============================================
// DEVPLAN PHASE 4.3 & 4.6: KAFKA PRODUCER SETUP & EVENT PUBLISHING
// ============================================
// This file implements:
// - Phase 4.3 - Kafka Producer Setup from DEVPLAN.md
//   Add Spring Kafka dependency
//   Configure Kafka producer (broker URL from env var)
//   Create PatientEventPublisher service
//
// - Phase 4.6 - Event Publishing from DEVPLAN.md
//   Publish to patient-created Kafka topic with JSON payload
// ============================================

// ============================================
// SPRING KAFKA IMPORTS
// ============================================
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

// ============================================
// JAVA STANDARD LIBRARY IMPORTS
// ============================================
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// ============================================
// JACKSON IMPORTS (JSON serialization)
// ============================================
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PATIENT EVENT PUBLISHER
 * 
 * WHAT IS THIS?
 * This service publishes events to Kafka when patients are created.
 * 
 * EVENT-DRIVEN ARCHITECTURE:
 * Instead of Patient Service directly calling Notification Service,
 * we publish an event to Kafka. Notification Service (or any other service)
 * can subscribe to these events and react accordingly.
 * 
 * BENEFITS:
 * - Decoupling: Services don't need to know about each other
 * - Scalability: Multiple services can consume the same event
 * - Resilience: If Notification Service is down, events are queued
 * - Flexibility: Easy to add new event consumers
 * 
 * HOW IT WORKS:
 * 1. Patient is created in Patient Service
 * 2. PatientEventPublisher.publishPatientCreated() is called
 * 3. Event is serialized to JSON
 * 4. Event is sent to Kafka topic "patient-created"
 * 5. Notification Service (and other services) consume the event
 */
@Service
public class PatientEventPublisher {
    
    /**
     * KAFKA TEMPLATE
     * 
     * Spring Kafka provides KafkaTemplate for sending messages to Kafka.
     * It handles connection management, serialization, and error handling.
     * 
     * Generic types:
     * - String: Key type (we'll use patient ID as key)
     * - String: Value type (JSON string)
     */
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    /**
     * OBJECT MAPPER
     * 
     * Jackson's ObjectMapper converts Java objects to JSON and vice versa.
     * Spring Boot auto-configures this bean.
     */
    private final ObjectMapper objectMapper;
    
    /**
     * KAFKA TOPIC NAME
     * 
     * The topic where patient events are published.
     * Topics are like "channels" in Kafka - producers send to topics,
     * consumers subscribe to topics.
     */
    private static final String PATIENT_CREATED_TOPIC = "patient-created";
    
    /**
     * CONSTRUCTOR - Dependency Injection
     * 
     * @param kafkaTemplate Kafka template for sending messages
     * @param objectMapper Jackson mapper for JSON serialization
     */
    public PatientEventPublisher(KafkaTemplate<String, String> kafkaTemplate, 
                                 ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    /**
     * PUBLISH PATIENT CREATED EVENT
     * 
     * This method is called after a patient is successfully created.
     * It publishes an event to Kafka that other services can consume.
     * 
     * @param patientId Patient's unique ID
     * @param firstName Patient's first name
     * @param lastName Patient's last name
     * @param createdAt When the patient was created
     */
    public void publishPatientCreated(String patientId, String firstName, 
                                     String lastName, String createdAt) {
        try {
            // STEP 1: Create event payload (data to send)
            // We use a Map to structure the event data
            Map<String, Object> event = new HashMap<>();
            event.put("patientId", patientId);
            event.put("firstName", firstName);
            event.put("lastName", lastName);
            event.put("createdAt", createdAt);
            
            // STEP 2: Serialize event to JSON string
            // ObjectMapper converts the Map to a JSON string
            // Example output: {"patientId":"123","firstName":"Alice","lastName":"Nguyen","createdAt":"2025-01-01T12:00:00Z"}
            String eventJson = objectMapper.writeValueAsString(event);
            
            // STEP 3: Send event to Kafka
            // kafkaTemplate.send() is asynchronous (non-blocking)
            // - Topic: Where to send the message
            // - Key: patientId (used for partitioning - messages with same key go to same partition)
            // - Value: JSON string (the event data)
            // 
            // Returns CompletableFuture - we can handle success/failure asynchronously
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(PATIENT_CREATED_TOPIC, patientId, eventJson);
            
            // STEP 4: Handle result (optional - for logging/monitoring)
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    // Success - message was sent to Kafka
                    System.out.println("Patient created event published: " + eventJson);
                } else {
                    // Failure - log the error
                    // In production, you might want to:
                    // - Retry the send
                    // - Store in a dead letter queue
                    // - Alert monitoring system
                    System.err.println("Failed to publish patient created event: " + exception.getMessage());
                }
            });
            
        } catch (JsonProcessingException e) {
            // Error serializing to JSON (shouldn't happen with simple Map)
            // In production, log this error and potentially retry
            System.err.println("Error serializing patient created event: " + e.getMessage());
        }
    }
}
