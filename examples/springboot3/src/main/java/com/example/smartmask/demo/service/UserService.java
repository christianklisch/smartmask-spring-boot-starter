package com.example.smartmask.demo.service;

import com.example.smartmask.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public User createUser(User user) {
        // Log the user object - sensitive fields will be automatically masked in logs
        log.info("Creating new user: {}", user);
        
        // Simulate saving to database
        user.setId(1L);
        
        // Log again after processing
        log.info("User created successfully: {}", user);
        
        return user;
    }
    
    public User getUserById(Long id) {
        // Create a sample user with sensitive data
        User user = new User(
            id,
            "johndoe",
            "secret123",
            "john.doe@example.com",
            "4111111111111111",
            "12345678901",
            "DE89370400440532013000",
            "SENSITIVE_DATA_123",
            "SENSITIVE_DATA_456",
            "CUSTOM_MASK_CHAR_DATA",
            "This data is only visible to admins"
        );
        
        // Log the user - sensitive fields will be automatically masked
        log.info("Retrieved user: {}", user);
        
        return user;
    }
}