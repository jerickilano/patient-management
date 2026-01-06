package com.patientmanagement.notification.listener;

// ============================================
// DEVPLAN PHASE 5.2 & 5.3: KAFKA CONSUMER & NOTIFICATION LOGIC
// ============================================
// This file implements:
// - Phase 5.2 - Kafka Consumer from DEVPLAN.md
//   Subscribe to patient-created topic
//   Create PatientCreatedListener with @KafkaListener
//   Deserialize JSON event payload
//
// - Phase 5.3 - Notification Logic from DEVPLAN.md
//   Log welcome notification: "Sending welcome notification to patient {id}"
//   Simple implementation (can be extended later for email/SMS)
// ============================================

// ============================================
// SPRING KAFKA IMPORTS
// ============================================
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

// ============================================
// JACKSON IMPORTS (JSON deserialization)
// ============================================
import com.fasterxml.jackson.databind.ObjectMapper;

// ============================================
// JAVA STANDARD LIBRARY IMPORTS
// ============================================
import java.util.Map;

/**
 * PATIENT CREATED LISTENER - Kafka Consumer
 * 
 * WHAT IS A KAFKA LISTENER?
 * A listener is a method that gets called automatically when a message
 * arrives on a Kafka topic. Spring Kafka handles all the complexity:
 * - Connecting to Kafka
 * - Subscribing to topics
 * - Polling for messages
 * - Deserializing messages
 * - Error handling
 * 
 * HOW IT WORKS:
 * 1. Patient Service publishes event to "patient-created" topic
 * 2. Kafka stores the message
 * 3. Notification Service (this listener) polls Kafka for new messages
 * 4. When a message arrives, this method is called
 * 5. Method processes the event (sends notification)
 * 6. Kafka marks the message as consumed
 * 
 * EVENT-DRIVEN ARCHITECTURE:
 * This demonstrates asynchronous, event-driven communication:
 * - Patient Service doesn't wait for Notification Service
 * - Notification Service processes events independently
 * - If Notification Service is down, events are queued in Kafka
 * - Multiple services can consume the same events
 */
@Component  // Spring will create an instance and register it as a Kafka listener
public class PatientCreatedListener {
    
    /**
     * OBJECT MAPPER - For deserializing JSON
     * 
     * Spring Boot auto-configures this bean.
     */
    private final ObjectMapper objectMapper;
    
    /**
     * CONSTRUCTOR - Dependency Injection
     * 
     * @param objectMapper Jackson mapper for JSON deserialization
     */
    public PatientCreatedListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * HANDLE PATIENT CREATED EVENT
     * 
     * This method is called automatically when a message arrives on the
     * "patient-created" Kafka topic.
     * 
     * @KafkaListener: Marks this method as a Kafka message listener
     *   - topics: List of topics to subscribe to
     *   - groupId: Consumer group ID (allows multiple instances to share work)
     * 
     * @Payload: The message body (JSON string in our case)
     * 
     * @Header: Extract headers from the Kafka message
     *   - KafkaHeaders.RECEIVED_KEY: The message key (patient ID)
     *   - KafkaHeaders.RECEIVED_TOPIC: The topic name
     *   - KafkaHeaders.RECEIVED_PARTITION: The partition number
     *   - KafkaHeaders.OFFSET: The message offset
     * 
     * @param message JSON string containing the event data
     * @param key Message key (patient ID)
     * @param topic Topic name
     * @param partition Partition number
     * @param offset Message offset
     */
    @KafkaListener(
        topics = "patient-created",  // Subscribe to this topic
        groupId = "notification-service-group"  // Consumer group ID
    )
    public void handlePatientCreated(
            @Payload String message,  // The JSON message body
            @Header(KafkaHeaders.RECEIVED_KEY) String key,  // Patient ID (message key)
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        try {
            // STEP 1: Deserialize JSON message to Map
            // The message is a JSON string like:
            // {"patientId":"123","firstName":"Alice","lastName":"Nguyen","createdAt":"2025-01-01T12:00:00Z"}
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            
            // STEP 2: Extract event data
            String patientId = (String) event.get("patientId");
            String firstName = (String) event.get("firstName");
            String lastName = (String) event.get("lastName");
            String createdAt = (String) event.get("createdAt");
            
            // STEP 3: Process the event (send notification)
            // In a real application, this might:
            // - Send an email to the patient
            // - Send an SMS
            // - Create a notification record in database
            // - Trigger other workflows
            // 
            // For this learning project, we just log a message
            sendWelcomeNotification(patientId, firstName, lastName);
            
            // STEP 4: Log message metadata (for debugging/monitoring)
            System.out.println(String.format(
                "Processed message - Topic: %s, Partition: %d, Offset: %d, Key: %s",
                topic, partition, offset, key
            ));
            
        } catch (Exception e) {
            // ERROR HANDLING
            // In production, you might want to:
            // - Log to a monitoring system
            // - Send to a dead letter queue
            // - Retry the message
            // - Alert operations team
            System.err.println("Error processing patient created event: " + e.getMessage());
            e.printStackTrace();
            
            // Note: If we throw an exception, Kafka will retry the message
            // (up to a certain number of retries, then it goes to dead letter queue)
        }
    }
    
    /**
     * SEND WELCOME NOTIFICATION
     * 
     * This simulates sending a welcome notification to a new patient.
     * In a real application, this would:
     * - Send an email via SMTP or email service (SendGrid, AWS SES, etc.)
     * - Send an SMS via SMS gateway (Twilio, AWS SNS, etc.)
     * - Create a notification record in a database
     * - Trigger other business processes
     * 
     * @param patientId Patient's unique ID
     * @param firstName Patient's first name
     * @param lastName Patient's last name
     */
    private void sendWelcomeNotification(String patientId, String firstName, String lastName) {
        // Simulate notification sending
        // In production, this would make actual API calls to email/SMS services
        
        String notificationMessage = String.format(
            "Sending welcome notification to patient %s (%s %s)",
            patientId, firstName, lastName
        );
        
        System.out.println("=".repeat(60));
        System.out.println("NOTIFICATION SERVICE - PATIENT CREATED EVENT");
        System.out.println("=".repeat(60));
        System.out.println(notificationMessage);
        System.out.println("Action: Sending welcome email and SMS");
        System.out.println("Status: Notification queued for delivery");
        System.out.println("=".repeat(60));
        
        // In a real application, you might do something like:
        // emailService.sendWelcomeEmail(patientId, firstName, lastName, email);
        // smsService.sendWelcomeSMS(phoneNumber, firstName);
        // notificationRepository.save(new Notification(patientId, "WELCOME", "SENT"));
    }
}
