package com.patientmanagement.patient.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utility class to extract user identity from HTTP headers.
 * The API Gateway forwards X-User-Id and X-User-Role headers after validating JWT tokens.
 */
@Component
public class SecurityUtil {
    
    /**
     * Gets the user ID from the X-User-Id header.
     */
    public String getUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getHeader("X-User-Id");
        }
        return null;
    }
    
    /**
     * Gets the user role from the X-User-Role header.
     */
    public String getUserRole() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getHeader("X-User-Role");
        }
        return null;
    }
    
    /**
     * Checks if the current user has the ADMIN role.
     */
    public boolean isAdmin() {
        String role = getUserRole();
        return "ADMIN".equalsIgnoreCase(role);
    }
    
    /**
     * Checks if the current user has a specific role.
     */
    public boolean hasRole(String role) {
        String userRole = getUserRole();
        return role != null && role.equalsIgnoreCase(userRole);
    }
    
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }
}
