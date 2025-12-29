package io.github.christianklisch.smartmask.security;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides access to the current security context for role-based access checks.
 * <p>
 * This class integrates with Spring Security to determine if the current user
 * has the necessary roles to see unmasked sensitive data. It is used by the
 * {@link io.github.christianklisch.smartmask.jackson.SensitiveDataSerializer}
 * to decide whether to mask a field based on the user's roles.
 * </p>
 * <p>
 * The class is designed to be registered as a Spring Bean and is automatically
 * configured by {@link io.github.christianklisch.smartmask.autoconfig.SensitiveDataMaskingAutoConfiguration}.
 * </p>
 * <p>
 * Applications can override this bean by defining their own SecurityContextProvider
 * bean, which will be used instead of this default implementation. This allows
 * for custom role-checking logic if needed.
 * </p>
 * 
 * @see io.github.christianklisch.smartmask.annotations.Sensitive#rolesAllowed()
 * @see io.github.christianklisch.smartmask.jackson.SensitiveDataSerializer
 * @see io.github.christianklisch.smartmask.autoconfig.SensitiveDataMaskingAutoConfiguration
 */
@Component
public class SecurityContextProvider {

    /**
     * Checks if the current authenticated user has any of the specified roles.
     * <p>
     * This method is called by the {@link io.github.christianklisch.smartmask.jackson.SensitiveDataSerializer}
     * to determine if the current user has permission to see the unmasked value of a sensitive field.
     * </p>
     * <p>
     * The method uses Spring Security's {@link SecurityContextHolder} to access the current
     * authentication context and check the user's granted authorities against the specified roles.
     * </p>
     * <p>
     * If the roles array is null or empty, the method returns false, indicating that no special
     * permission is granted. Similarly, if there is no authenticated user, the method returns false.
     * </p>
     * <p>
     * Example usage:
     * </p>
     * <pre>
     * SecurityContextProvider provider = new SecurityContextProvider();
     * boolean hasAccess = provider.hasAnyRole(new String[]{"ROLE_ADMIN", "ROLE_SUPPORT"});
     * </pre>
     *
     * @param rolesAllowed Array of role names to check against, can be null or empty
     * @return true if the user has at least one of the specified roles, false otherwise
     */
    public boolean hasAnyRole(@Nullable String[] rolesAllowed) {
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
