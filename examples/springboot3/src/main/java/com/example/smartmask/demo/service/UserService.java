package com.example.smartmask.demo.service;

import com.example.smartmask.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class that demonstrates how SmartMask automatically masks sensitive data in logs.
 * <p>
 * This service uses standard SLF4J logging, but the sensitive data in the logged objects
 * is automatically masked according to the {@code @Sensitive} annotations in the {@link User} class.
 * </p>
 * <p>
 * The masking is performed by the {@code MaskingMessageConverter} configured in logback.xml,
 * which intercepts log messages and masks sensitive data before it appears in logs.
 * </p>
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * Creates a new user and logs the user object before and after processing.
     * <p>
     * This method demonstrates how sensitive data is automatically masked in logs.
     * When the user object is logged, fields annotated with {@code @Sensitive} are
     * masked according to their masking rules.
     * </p>
     * 
     * @param user The user to create
     * @return The created user with an ID assigned
     */
    public User createUser(User user) {
        // Log the user object - sensitive fields will be automatically masked in logs
        // The MaskingMessageConverter will intercept this log message and mask sensitive data
        log.info("Creating new user: {}", user);

        // Simulate saving to database
        user.setId(1L);

        // Log again after processing - sensitive fields will still be masked
        log.info("User created successfully: {}", user);

        return user;
    }

    /**
     * Retrieves a user by ID (simulated).
     * <p>
     * This method creates a sample user with various sensitive data fields and logs it.
     * The sensitive fields are automatically masked in the logs according to their
     * masking rules defined by the {@code @Sensitive} annotations.
     * </p>
     * 
     * @param id The ID of the user to retrieve
     * @return A sample user with the given ID
     */
    public User getUserById(Long id) {
        // Create a sample user with sensitive data
        User user = new User(
            id,
            "johndoe",
            "secret123",                // Will be masked as "******"
            "john.doe@example.com",     // Will be masked as "j******e@example.com"
            "4111111111111111",         // Will be masked as "************1111"
            "12345678901",              // Will be masked as "*****78901"
            "DE89370400440532013000",   // Will be masked as "DE89********3000"
            "SENSITIVE_DATA_123",       // Will show first 3 chars: "SEN***************"
            "SENSITIVE_DATA_456",       // Will show last 4 chars: "****************456"
            "CUSTOM_MASK_CHAR_DATA",    // Will use # as mask char: "######################"
            "This data is only visible to admins" // Only visible to users with ROLE_ADMIN
        );

        // Log the user - sensitive fields will be automatically masked by MaskingMessageConverter
        log.info("Retrieved user: {}", user);

        return user;
    }
}
