package com.patientmanagement.patient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.Instant;

/**
 * Global exception handler that provides consistent error responses across all endpoints.
 * 
 * Error response format:
 * {
 *   "timestamp": "2026-01-05T18:32:11Z",
 *   "path": "/patients",
 *   "errorCode": "VALIDATION_ERROR",
 *   "message": "lastName must not be blank"
 * }
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(FieldError::getDefaultMessage)
            .orElse("Validation failed");
        
        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            request.getDescription(false).replace("uri=", ""),
            "VALIDATION_ERROR",
            errorMessage
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            request.getDescription(false).replace("uri=", ""),
            "INVALID_ARGUMENT",
            ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            request.getDescription(false).replace("uri=", ""),
            "INTERNAL_ERROR",
            "An unexpected error occurred"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    public static class ErrorResponse {
        private Instant timestamp;
        private String path;
        private String errorCode;
        private String message;
        
        public ErrorResponse(Instant timestamp, String path, String errorCode, String message) {
            this.timestamp = timestamp;
            this.path = path;
            this.errorCode = errorCode;
            this.message = message;
        }
        
        public Instant getTimestamp() {
            return timestamp;
        }
        
        public String getPath() {
            return path;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
