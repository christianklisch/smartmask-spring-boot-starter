package io.github.christianklisch.smartmask.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides access to the current security context for role-based access checks.
 * This class should be registered as a Spring Bean.
 */
@Component
public class SecurityContextProvider {

    /**
     * Checks if the current authenticated user has any of the specified roles.
     *
     * @param rolesAllowed Array of role names to check against
     * @return true if the user has at least one of the specified roles, false otherwise
     */
    public boolean hasAnyRole(String[] rolesAllowed) {
        if (rolesAllowed == null || rolesAllowed.length == 0) {
            return false; // No roles defined, so no special permission
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false; // No authenticated user
        }

        Set<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return Arrays.stream(rolesAllowed).anyMatch(userRoles::contains);
    }
}