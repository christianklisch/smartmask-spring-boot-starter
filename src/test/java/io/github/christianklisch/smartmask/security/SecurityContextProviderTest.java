package io.github.christianklisch.smartmask.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SecurityContextProvider class.
 * These tests verify that the provider correctly checks user roles for access control.
 */
public class SecurityContextProviderTest {

    private SecurityContextProvider securityContextProvider;

    @BeforeEach
    void setUp() {
        securityContextProvider = new SecurityContextProvider();
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // Clear security context after each test
        SecurityContextHolder.clearContext();
    }

    /**
     * Test that hasAnyRole returns false when no roles are specified.
     */
    @Test
    void testHasAnyRoleWithEmptyRoles() {
        // Arrange
        String[] roles = new String[0];

        // Act
        boolean result = securityContextProvider.hasAnyRole(roles);

        // Assert
        assertFalse(result, "Should return false when no roles are specified");
    }

    /**
     * Test that hasAnyRole returns false when no authentication is present.
     */
    @Test
    void testHasAnyRoleWithNoAuthentication() {
        // Arrange
        String[] roles = new String[] { "ROLE_ADMIN" };

        // Act
        boolean result = securityContextProvider.hasAnyRole(roles);

        // Assert
        assertFalse(result, "Should return false when no authentication is present");
    }

    /**
     * Test that hasAnyRole returns false when authentication is not authenticated.
     */
    @Test
    void testHasAnyRoleWithNotAuthenticatedUser() {
        // Arrange
        String[] roles = new String[] { "ROLE_ADMIN" };
        Authentication authentication = new TestingAuthenticationToken("user", "password", Collections.emptyList());
        authentication.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        boolean result = securityContextProvider.hasAnyRole(roles);

        // Assert
        assertFalse(result, "Should return false when authentication is not authenticated");
    }

    /**
     * Test that hasAnyRole returns false when user doesn't have any of the required roles.
     */
    @Test
    void testHasAnyRoleWithUserNotHavingRole() {
        // Arrange
        String[] roles = new String[] { "ROLE_ADMIN" };
        Authentication authentication = new TestingAuthenticationToken(
                "user", 
                "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        boolean result = securityContextProvider.hasAnyRole(roles);

        // Assert
        assertFalse(result, "Should return false when user doesn't have any of the required roles");
    }

    /**
     * Test that hasAnyRole returns true when user has one of the required roles.
     */
    @Test
    void testHasAnyRoleWithUserHavingOneRole() {
        // Arrange
        String[] roles = new String[] { "ROLE_ADMIN", "ROLE_MANAGER" };
        Authentication authentication = new TestingAuthenticationToken(
                "user", 
                "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        boolean result = securityContextProvider.hasAnyRole(roles);

        // Assert
        assertTrue(result, "Should return true when user has one of the required roles");
    }

    /**
     * Test that hasAnyRole returns true when user has multiple required roles.
     */
    @Test
    void testHasAnyRoleWithUserHavingMultipleRoles() {
        // Arrange
        String[] roles = new String[] { "ROLE_ADMIN", "ROLE_MANAGER" };
        Authentication authentication = new TestingAuthenticationToken(
                "user", 
                "password", 
                Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_MANAGER"),
                    new SimpleGrantedAuthority("ROLE_USER")
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        boolean result = securityContextProvider.hasAnyRole(roles);

        // Assert
        assertTrue(result, "Should return true when user has multiple required roles");
    }

    /**
     * Test that hasAnyRole handles null roles array.
     */
    @Test
    void testHasAnyRoleWithNullRoles() {
        // Arrange
        String[] roles = null;
        Authentication authentication = new TestingAuthenticationToken(
                "user", 
                "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        boolean result = securityContextProvider.hasAnyRole(roles);

        // Assert
        assertFalse(result, "Should return false when roles array is null");
    }
}