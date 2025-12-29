package com.example.smartmask.demo.controller;

import com.example.smartmask.demo.model.User;
import com.example.smartmask.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that demonstrates how SmartMask automatically masks sensitive data
 * in JSON responses based on {@code @Sensitive} annotations and user roles.
 * <p>
 * This controller provides endpoints that show how SmartMask works with:
 * </p>
 * <ul>
 *   <li>Public endpoints where all sensitive data is masked</li>
 *   <li>Admin-only endpoints where certain sensitive data is unmasked based on user roles</li>
 *   <li>POST endpoints that accept and return objects with sensitive data</li>
 * </ul>
 * <p>
 * The masking in JSON responses is handled by the {@code SensitiveDataSerializer} and
 * {@code SensitiveAnnotationIntrospector} classes from the SmartMask library.
 * </p>
 */
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Public endpoint that returns a user with all sensitive fields masked.
     * <p>
     * This endpoint is accessible without authentication. All fields annotated with
     * {@code @Sensitive} in the {@link User} class will be masked in the JSON response
     * according to their masking rules.
     * </p>
     * <p>
     * Example usage:
     * </p>
     * <pre>
     * curl http://localhost:8080/api/public/user
     * </pre>
     *
     * @return A user object with sensitive fields masked
     */
    @GetMapping("/public/user")
    public User getPublicUser() {
        return userService.getUserById(1L);
    }

    /**
     * Admin-only endpoint that returns a user with role-based masking.
     * <p>
     * This endpoint demonstrates role-based access control for sensitive data.
     * When accessed with the ROLE_ADMIN role, fields annotated with
     * {@code @Sensitive(rolesAllowed = {"ROLE_ADMIN"})} will be unmasked in the response.
     * </p>
     * <p>
     * Example usage (with basic auth):
     * </p>
     * <pre>
     * curl -u admin:admin http://localhost:8080/api/admin/user
     * </pre>
     *
     * @return A user object with role-based masking applied
     */
    @GetMapping("/admin/user")
    public User getAdminUser() {
        // Same user data, but when accessed with admin role,
        // fields with rolesAllowed={"ROLE_ADMIN"} will be unmasked
        return userService.getUserById(1L);
    }

    /**
     * Endpoint for creating a new user, demonstrating masking in both request and response.
     * <p>
     * This endpoint accepts a User object in the request body and returns the created user.
     * The sensitive fields in the response will be masked according to their masking rules.
     * </p>
     * <p>
     * Note that SmartMask only masks data in the response, not in the request. The sensitive
     * data in the request is processed normally, but masked when logged or returned.
     * </p>
     * <p>
     * Example usage:
     * </p>
     * <pre>
     * curl -X POST -H "Content-Type: application/json" -d '{
     *   "username": "newuser",
     *   "password": "password123",
     *   "email": "new.user@example.com",
     *   "creditCardNumber": "4111111111111111",
     *   ...
     * }' http://localhost:8080/api/public/user
     * </pre>
     *
     * @param user The user to create
     * @return The created user with sensitive fields masked
     */
    @PostMapping("/public/user")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
